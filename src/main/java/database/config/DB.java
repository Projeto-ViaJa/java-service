package database.config;

import exceptions.DbException;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DB {
    private static Connection conn = null;

    public static Connection getConnection() {
        if (conn == null) {
            Properties props = loadProperties();

            // 1. LER VARIÁVEIS DE AMBIENTE DO DOCKER (Se existirem, sobrescrevem o properties)
            String dbHost = System.getenv("DB_HOST");
            String dbPort = System.getenv("DB_PORT");
            String dbName = System.getenv("DB_DATABASE");

            String url;
            // Se o Docker passou o DB_HOST, montamos a URL dinamicamente
            if (dbHost != null && !dbHost.isEmpty()) {
                String port = (dbPort != null) ? dbPort : "3306";
                String name = (dbName != null) ? dbName : "viaja_dev";
                url = "jdbc:mysql://" + dbHost + ":" + port + "/" + name + "?allowPublicKeyRetrieval=true&useSSL=false";

                // Atualiza usuário e senha se vieram do Docker
                if (System.getenv("DB_USER") != null) props.setProperty("user", System.getenv("DB_USER"));
                if (System.getenv("DB_PASSWORD") != null) props.setProperty("password", System.getenv("DB_PASSWORD"));
            } else {
                // Se não está no Docker, usa a URL estática do arquivo db.properties
                url = props.getProperty("dburl");
            }
            
            // 2. SISTEMA DE RETENTATIVAS (Evita o erro de inicialização rápida do Docker)
            int maxRetries = 5;
            for (int i = 1; i <= maxRetries; i++) {
                try {
                conn = DriverManager.getConnection(url, props);
                    System.out.println("Conexão com o banco estabelecida com sucesso!");
                    break; // Conectou? Sai do loop.
                } catch (SQLException e) {
                    System.out.println("Falha ao conectar (Tentativa " + i + " de " + maxRetries + "). Aguardando banco...");
                    if (i == maxRetries) {
                        throw new DbException("Não foi possível conectar ao banco após várias tentativas: " + e.getMessage());
                    }
                    try {
                        Thread.sleep(3000); // Espera 3 segundos antes de tentar de novo
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        return conn;
    }
    private static Properties loadProperties() {
        Properties props = new Properties();
        // Tenta carregar o arquivo, mas não quebra o app se não achar (útil para nuvem onde o arquivo pode não existir)
        try (FileInputStream fs = new FileInputStream("db.properties")) {
            props.load(fs);
        } catch (IOException e) {
            System.out.println("Aviso: Arquivo db.properties não encontrado. Usando variáveis de ambiente.");
        }
        return props;
    }
    public static void closeConnection() {
        if (conn != null) try {
            conn.close();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }
    public static void closeStatment(Statement st) {
        if (st != null) try {
            st.close();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }
}
