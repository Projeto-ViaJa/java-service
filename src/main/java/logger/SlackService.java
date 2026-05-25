package logger;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SlackService {
    private final String webhookUrl;
    public SlackService(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }
    public void enviarMensagem(String canal, String mensagem, String nivel) {
        try {

            String json = """
                    {
                        "channel": "%s",
                        "text": "%s"
                    }
                    """.formatted(canal, mensagem);
            URL url = new URL(webhookUrl);
            HttpURLConnection conexao =
                    (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("POST");
            conexao.setRequestProperty(
                    "Content-Type",
                    "application/json"
            );
            conexao.setDoOutput(true);
            OutputStream output =
                    conexao.getOutputStream();
            output.write(json.getBytes());
            output.flush();
            output.close();
            int codigoResposta =
                    conexao.getResponseCode();
            System.out.println("Status: " + codigoResposta);
        } catch (Exception e) {
            System.out.println("erro ao enviar mensagem para Slack");
            e.printStackTrace();
        }
    }
}