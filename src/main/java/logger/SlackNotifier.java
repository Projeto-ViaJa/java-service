package logger;

import database.config.DB;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import logger.AppLogger;

public class SlackNotifier {

    private static final String SQL_GET_PARAMS_COM_TIPOS =
            """
                    SELECT pn.id, pn.tituloNotificacao, pn.descricao, pn.url, pn.canal,
                           pn.isAtivo, pn.fkUsuario, pn.fkEmpresa,
                           tl.id AS tipoId, tl.tipo, tl.onOff
                    FROM paramsNotificacao pn
                    LEFT JOIN setandoTipos st ON st.fkParamsNotificacoes = pn.id
                    LEFT JOIN tipoLog tl ON tl.id = st.fkTipoLog
                    WHERE pn.isAtivo = 1
                    """;

    private String webhookUrl = null;
    private String canal = "";
    private final List<TipoLog> tipos = new ArrayList<>();
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public SlackNotifier() {
        refreshParams();
    }

    private void refreshParams() {
        webhookUrl = null;
        canal = "";
        tipos.clear();

        Connection conn = DB.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_GET_PARAMS_COM_TIPOS + " LIMIT 10")) {

            // agrupa as linhas do resultset no primeiro paramsNotificacao encontrado
            Integer currentId = null;
            boolean found = false;
            while (rs.next()) {
                int id = rs.getInt("id");
                if (!found) {
                    // primeiro grupo encontrado -> preenche webhook e canal
                    webhookUrl = rs.getString("url");
                    canal = rs.getString("canal");
                    found = true;
                }

                Object tipoIdObj = rs.getObject("tipoId");
                if (tipoIdObj != null) {
                    String tipo = rs.getString("tipo");
                    boolean onOff = rs.getInt("onOff") == 1;
                    tipos.add(new TipoLog(tipo, onOff));
                }

                currentId = id;
            }

            if (found) {
                if (webhookUrl == null || webhookUrl.isBlank()) {
                    AppLogger.warning("SLACK", "Webhook ausente", "params ativo sem URL");
                    webhookUrl = null;
                    canal = "";
                } else {
                    AppLogger.info("SLACK", "Webhook carregado", "Canal: " + canal + " | paramsId: " + currentId);
                }
            } else {
                AppLogger.warning("SLACK", "Sem params ativos", "Não foi encontrado paramsNotificacao ativo com webhook");
            }

        } catch (Exception e) {
            webhookUrl = null;
            canal = "";
            AppLogger.error("SLACK", "Erro ao carregar params de Slack", e);
        }
    }

    private boolean deveNotificar(String nivel) {
        return tipos.stream()
                .anyMatch(t -> t.tipo.equalsIgnoreCase(nivel) && t.onOff);
    }

    public void notify(String nivel, String componente, String mensagem) {
        try {
            if (webhookUrl == null) refreshParams();
            if (webhookUrl == null) {
                AppLogger.warning("SLACK", "Notificação não enviada", "Nenhum webhook configurado para envio");
                return;
            }

            if (!deveNotificar(nivel)) {
                AppLogger.info("SLACK", "Nível desativado", "Não enviando " + nivel + " por parametrização");
                return;
            }

            String texto = String.format("[%s] | [%s] | %s | %s",
                    nivel,
                    componente,
                    mensagem,
                    LocalDateTime.now().format(DTF));

            enviarMensagem(webhookUrl, canal, texto);

        } catch (Exception e) {
            AppLogger.error("SLACK", "Erro ao enviar notificação", e);
        }
    }

    // Envia payload JSON para o webhook (usado internamente)
    private void enviarMensagem(String webhookUrl, String canal, String mensagem) {
        try {
            String textoEscapado = mensagem
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");

            String json = "{\"text\": \"" + textoEscapado + "\"}";

            URL url = new URL(webhookUrl);
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("POST");
            conexao.setRequestProperty("Content-Type", "application/json");
            conexao.setDoOutput(true);

            try (OutputStream output = conexao.getOutputStream()) {
                output.write(json.getBytes("UTF-8"));
                output.flush();
            }

            int codigoResposta = conexao.getResponseCode();
            if (codigoResposta != 200) {
                AppLogger.warning("SLACK", "Envio falhou", "HTTP " + codigoResposta + " | canal: " + canal + " | msg: " + mensagem);
            }

        } catch (Exception e) {
            AppLogger.error("SLACK", "Exceção ao enviar mensagem para Slack", e);
        }
    }
}
