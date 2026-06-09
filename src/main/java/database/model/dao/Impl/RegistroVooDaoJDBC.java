package database.model.dao.Impl;

import database.model.dao.AbstractDao;
import database.model.dao.RegistroVooDao;
import entity.RegistroVoo;
import exceptions.DbException;
import logger.AppLogger;
import logger.SlackNotifier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RegistroVooDaoJDBC extends AbstractDao implements RegistroVooDao {

    private final int BATCH_SIZE = 1000;

    private SlackNotifier notifier = null;

    private static final DateTimeFormatter HH_MM_SS =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    public RegistroVooDaoJDBC(Connection conn) {
        super(conn);
    }

    private void notificar(String nivel, String componente, String mensagem) {
        if (notifier == null) notifier = new SlackNotifier();
        notifier.notify(nivel, componente, mensagem);
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

        // Truncamos antes de criar o PreparedStatement de inserção
        truncateRegistroVoo();

        try {
            super.conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }

        try (PreparedStatement ps = super.conn.prepareStatement(sql)) {
            int batchCount = 0;

            for (RegistroVoo registro : registros) {
                ps.setInt(1,     registro.getAno());
                ps.setInt(2,     registro.getMes());
                ps.setString(3,  registro.getAeroportoOrigemUf());
                ps.setString(4,  registro.getAeroportoOrigemRegiao());
                ps.setString(5,  registro.getAeroportoOrigemLocalidade());
                ps.setString(6,  registro.getAeroportoDestinoUf());
                ps.setString(7,  registro.getAeroportoDestinoRegiao());
                ps.setString(8,  registro.getAeroportoDestinoLocalidade());
                ps.setString(9,  registro.getNatureza());
                ps.setString(10, registro.getGrupoVoo());
                ps.setInt(11,    registro.getPassageirosPagos());
                ps.setInt(12,    registro.getPassageirosGratis());
                ps.setLong(13,   registro.getAsk());
                ps.setLong(14,   registro.getRpk());
                ps.setLong(15,   registro.getAtk());
                ps.setLong(16,   registro.getRtk());
                ps.setInt(17,    registro.getDecolagens());
                ps.setInt(18,    registro.getAssentos());
                ps.addBatch();
                batchCount++;

                if (batchCount % BATCH_SIZE == 0) {
                    ps.executeBatch();
                    AppLogger.info("DATABASE", "Lote parcial executado",
                            batchCount + " registros enviados ao banco até o momento");
                    notificar("INFO",  "Database", "Lote parcial executado.");
                }
            }

            ps.executeBatch();
            super.conn.commit();
            super.conn.setAutoCommit(true);

            AppLogger.info("DATABASE", "Inserção finalizada com sucesso",
                    "Total de " + registros.size() + " registros inseridos na tabela registro_voo");
            notificar("INFO", "Database", "Inserção em lotes finalizada e commit realizado.");
        } catch (SQLException e) {

            AppLogger.error("DATABASE", "Falha durante inserção em lote, iniciando rollback", e);
            notificar("ERROR", "DATABASE", "Falha na inserção em lote | " + e.getMessage());

            super.rollback();
            AppLogger.warning("DATABASE", "Rollback realizado com sucesso.",
                    "Transação revertida após falha: " + e.getMessage());
            notificar("WARNING", "Database", "Rollback realizado com sucesso.");
            throw new DbException(e.getMessage());

        }
        AppLogger.info("DATABASE", "Inserção em lote concluída",
                "Total: " + registros.size() + " registros");
        notificar("INFO", "DATABASE", "Inserção em lote concluída | Total: " + registros.size() + " registros");
    }

    @Override
    public void truncateRegistroVoo() {
        String sql = "TRUNCATE TABLE registro_voo;";

        try (PreparedStatement ps = super.conn.prepareStatement(sql)) {
            ps.execute();
            AppLogger.info("DATABASE", "Tabela registro_voo truncada",
                    "TRUNCATE executado antes da inserção em batch");
        } catch (Exception e) {
            AppLogger.error("DATABASE", "Falha ao truncar tabela registro_voo", "Erro: " + e);
            notificar("ERROR", "DATABASE", "Falha no TRUNCATE de registro_voo | " + e.getMessage());
            throw new RuntimeException(e);
        }
        notificar("INFO", "DATABASE", "Tabela registro_voo truncada antes da inserção em batch");
    }
}
