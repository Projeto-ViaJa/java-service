package logger;

import database.config.DB;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AppLogger {

    //static = qualquer classe no projeto consegur utilizar este atributo sem instanciar(new) a classe
    // final = valor nunca muda

    private static final org.slf4j.Logger log= LoggerFactory.getLogger(AppLogger.class);
    private static final String sql = "INSERT INTO log (tipo, modulo, mensagem, descricao) VALUES (?,?,?,?)";

    public static void info(String modulo, String mensagem, String descricao){

        //mostra no terminal e depois salva
        log.info("[{}] {} — {}", modulo, mensagem, descricao);
        salvar("INFO", modulo, mensagem, descricao);

    }

    public static void warning (String modulo, String mensagem, String descricao){
        log.warn("[{}] {} — {}", modulo, mensagem, descricao);
        salvar("WARNING", modulo, mensagem, descricao);
    }

    public static void error (String modulo, String mensagem, String descricao){
        log.error("[{}] {} — {}", modulo, mensagem, descricao);
        salvar("ERROR", modulo, mensagem, descricao);
    }

    // Sobrecarga: aceita uma Exception e extrai a mensagem dela para a descrição
    public static void error(String modulo, String mensagem, Exception e) {
        String descricao = e.getClass().getSimpleName() + ": " + e.getMessage();
        log.error("[{}] {} — {}", modulo, mensagem, descricao, e);
        salvar("ERROR", modulo, mensagem, descricao);
    }

    public static void salvar(String tipo, String modulo, String mensagem, String descricao){

        try{

            Connection conn = DB.getConnection();

            //preparacao da consulta sql
            try(PreparedStatement ps = conn.prepareStatement(sql)){

                //editando consulta
                ps.setString(1, tipo);
                ps.setString(2, modulo);
                ps.setString(3, mensagem);
                ps.setString(4, descricao);
                ps.executeUpdate();

            }

        } catch (SQLException ex) {

            log.error("AppLogger: Falha ao salvar log no banco", ex);
        }
    }
}
