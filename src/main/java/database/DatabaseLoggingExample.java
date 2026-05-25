package database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EXEMPLO DE USO - Integração Slack com SLF4J + Log4j2
 *
 * Demonstra como usar a integração de Slack com logs de banco de dados.
 * Nenhuma modificação especial é necessária - apenas use SLF4J normalmente!
 */
public class DatabaseLoggingExample {

    // Os logs desta classe serão automaticamente enviados para Slack
    // porque o nome da classe começa com "database"
    private static final Logger log = LoggerFactory.getLogger(DatabaseLoggingExample.class);

    /**
     * Exemplo 1: Log de Informação
     * ✅ Será enviado para Slack em AZUL
     */
    public static void exemploInfo() {
        log.info("Conexão estabelecida com o banco de dados MySQL");
        // Vai para:
        // - Console (SYSTEM_OUT)
        // - Slack #banco-de-dados (AZUL)
    }

    /**
     * Exemplo 2: Log de Aviso (Warning)
     * ✅ Será enviado para Slack em LARANJA
     */
    public static void exemploWarning() {
        log.warn("Tempo de resposta do banco acima do normal: 5.234 segundos");
        // Vai para:
        // - Console
        // - Slack #banco-de-dados (LARANJA)
    }

    /**
     * Exemplo 3: Log de Erro
     * ✅ Será enviado para Slack em VERMELHO
     */
    public static void exemploError() {
        try {
            throw new java.sql.SQLException("Connection refused: host unreachable");
        } catch (java.sql.SQLException e) {
            log.error("Falha ao conectar no banco de dados", e);
            // Vai para:
            // - Console
            // - Slack #banco-de-dados (VERMELHO com stack trace)
        }
    }

    /**
     * Exemplo 4: Fluxo típico de carregamento
     */
    public static void exemploFluxoCarregamento() {
        try {
            // 1. Conectar
            log.info("Iniciando carregamento de dados...");

            // 2. Processar
            log.info("100 registros lidos da planilha");
            log.info("50 registros inseridos no banco");

            // 3. Finalizar
            log.info("Carregamento concluído com sucesso");

        } catch (Exception e) {
            log.error("Erro durante o carregamento", e);
        }
    }

    /**
     * Exemplo dentro de um DAO real
     */
    public static class ExemploDAO {
        private static final Logger log = LoggerFactory.getLogger(ExemploDAO.class);

        public void salvarRegistro(String id, String descricao) {
            try {
                log.info("Salvando registro: {} - {}", id, descricao);

                // Simular query
                if (Math.random() > 0.8) {
                    throw new java.sql.SQLException("Constraint violation");
                }

                log.info("Registro salvo com sucesso");

            } catch (java.sql.SQLException e) {
                log.error("Erro ao salvar registro: {}", id, e);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Exemplos de Integração Slack com SLF4J ===\n");

        System.out.println("1. Log de Informação (AZUL no Slack):");
        exemploInfo();

        System.out.println("\n2. Log de Aviso (LARANJA no Slack):");
        exemploWarning();

        System.out.println("\n3. Log de Erro (VERMELHO no Slack):");
        exemploError();

        System.out.println("\n4. Fluxo de Carregamento:");
        exemploFluxoCarregamento();

        System.out.println("\n5. Exemplo DAO:");
        ExemploDAO dao = new ExemploDAO();
        dao.salvarRegistro("REG001", "Voo São Paulo");
        dao.salvarRegistro("REG002", "Voo Rio de Janeiro");

        System.out.println("\n=== Verificar #banco-de-dados no Slack! ===");
    }
}
