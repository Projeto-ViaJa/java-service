package database.model.dao.Impl;

import database.config.DB;
import database.model.dao.RegistroVooDao;
import entity.RegistroVoo;
import exceptions.DbException;
import logger.AppLogger;
import logger.SlackService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class RegistroVooDaoJDBC implements RegistroVooDao {
    String webhook = "";
    SlackService slack = new SlackService(webhook);
    private Connection conn;
    private final Integer BATCH_SIZE = 1000;

    public RegistroVooDaoJDBC (Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(List<RegistroVoo> registros) {

        String sql = """
        INSERT INTO registro_voo (
            ano, mes,
            origem_uf, origem_regiao, origem_localidade,
            destino_uf, destino_regiao, destino_localidade,
            natureza, grupo_voo,
            passageiros_pagos, passageiros_gratis,
            ask, rpk, atk, rtk, decolagens, assentos
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            truncateRegistroVoo();

            Integer batchCount = 0;

            for (RegistroVoo registro : registros) {

                ps.setInt(1, registro.getAno());
                ps.setInt(2, registro.getMes());
                ps.setString(3, registro.getAeroportoOrigemUf());
                ps.setString(4, registro.getAeroportoOrigemRegiao());
                ps.setString(5, registro.getAeroportoOrigemLocalidade());
                ps.setString(6, registro.getAeroportoDestinoUf());
                ps.setString(7, registro.getAeroportoDestinoRegiao());
                ps.setString(8, registro.getAeroportoDestinoLocalidade());
                ps.setString(9, registro.getNatureza());
                ps.setString(10, registro.getGrupoVoo());
                ps.setInt(11, registro.getPassageirosPagos());
                ps.setInt(12, registro.getPassageirosGratis());
                ps.setLong(13, registro.getAsk());
                ps.setLong(14, registro.getRpk());
                ps.setLong(15, registro.getAtk());
                ps.setLong(16, registro.getRtk());
                ps.setInt(17, registro.getDecolagens());
                ps.setInt(18, registro.getAssentos());

                ps.addBatch();
                batchCount++;

                if (batchCount % BATCH_SIZE == 0){

                    ps.executeBatch();
                    AppLogger.info("DATABASE", "Lote parcial executado.",
                            batchCount + " registros enviados ao banco até o momento");
                    slack.enviarMensagem("Database", "DATABASE - Lote parcial executado.", "INFO");

                }
            }

            ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);

            AppLogger.info("DATABASE","Inserção em lotes finalizada e commit realizado.",
                    "Total de: " + registros.size() + " registros inseridos com sucesso na tabela registro_voo");
            slack.enviarMensagem("Database", "DATABASE - Inserção em lotes finalizada e commit realizado.", "INFO");


        } catch (SQLException e) {
            AppLogger.error("DATABASE", "Falha durante inserção em lote, tentativa de rollback.", e);
            slack.enviarMensagem("Database", "DATABASE - Falha durante inserção em lote, tentativa de rollback.", "ERROR");
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                AppLogger.warning("DATABASE","Rollbak realizado com sucesso.",
                        "Transação revertida após falha: " + e.getMessage());
                slack.enviarMensagem("Database", "DATABASE - Rollbak realizado com sucesso.", "WARNING");

            } catch (SQLException ex) {
                AppLogger.error("DATABASE","Falha ao executar rollback","Erro: " + ex.getMessage());
                slack.enviarMensagem("Database", "DATABASE - Falha ao executar rollback.", "ERROR");

                throw new DbException(ex.getMessage());
            }

            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void truncateRegistroVoo() {
        String sql = """
        TRUNCATE TABLE registro_voo;
        """;

        try ( PreparedStatement ps = conn.prepareStatement(sql) ){
            ps.execute();
            AppLogger.info("DATABASE", "Tabela registro_voo truncada", "TRUNCATE executado antes da inserção em batch");
            slack.enviarMensagem("Database", "DATABASE - TRUNCATE executado antes da inserção em batch", "ERROR");


        } catch (Exception e) {
            AppLogger.error("DATABASE","Falha ao truncar a tabela registro_voo.","Erro: " + e);
            slack.enviarMensagem("Database", "DATABASE - Falha ao truncar a tabela registro_voo", "ERROR");

            throw new RuntimeException(e);
        }
    }
}
