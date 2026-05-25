package database.model.dao;

import database.config.DB;
import database.model.SlackParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para consultar configurações de Slack na tabela SlackParams
 * Utilizado pelo SlackAppender para determinar se deve enviar notificações
 */
public class SlackParamsDao {

    private static final Logger log = LoggerFactory.getLogger(SlackParamsDao.class);
    private static final String SQL_GET_ALL = "SELECT id, url_slack, canal, warning, infos, error, fkUsuario FROM SlackParams";

    /**
     * Busca todos os parâmetros de Slack configurados
     */
    public List<SlackParams> obterTodosParams() {
        List<SlackParams> params = new ArrayList<>();

        try {
            Connection conn = DB.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL_GET_ALL);

            while (rs.next()) {
                SlackParams param = mapResultSetToEntity(rs);
                params.add(param);
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            log.error("Erro ao consultar SlackParams", e);
        }

        return params;
    }

    /**
     * Busca parâmetros de Slack por ID do usuário
     */
    public SlackParams obterParamsPorUsuario(Integer idUsuario) {
        if (idUsuario == null) {
            return null;
        }

        try {
            Connection conn = DB.getConnection();
            String sql = SQL_GET_ALL + " WHERE fkUsuario = " + idUsuario;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            SlackParams param = null;
            if (rs.next()) {
                param = mapResultSetToEntity(rs);
            }

            rs.close();
            stmt.close();

            return param;

        } catch (Exception e) {
            log.error("Erro ao consultar SlackParams por usuário: " + idUsuario, e);
            return null;
        }
    }

    /**
     * Busca o primeiro parâmetro de Slack configurado
     * (útil se houver apenas uma configuração global)
     */
    public SlackParams obterPrimeiroParam() {
        try {
            Connection conn = DB.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL_GET_ALL + " LIMIT 1");

            SlackParams param = null;
            if (rs.next()) {
                param = mapResultSetToEntity(rs);
            }

            rs.close();
            stmt.close();

            return param;

        } catch (Exception e) {
            log.error("Erro ao consultar primeiro SlackParams", e);
            return null;
        }
    }

    /**
     * Mapeia um resultado SQL para a entidade SlackParams
     */
    private SlackParams mapResultSetToEntity(ResultSet rs) throws java.sql.SQLException {
        SlackParams param = new SlackParams();

        param.setId(rs.getInt("id"));
        param.setUrlSlack(rs.getString("url_slack"));
        param.setCanal(rs.getString("canal"));
        param.setWarning(rs.getBoolean("warning"));
        param.setInfos(rs.getBoolean("infos"));
        param.setError(rs.getBoolean("error"));

        Object fkUsuario = rs.getObject("fkUsuario");
        if (fkUsuario != null) {
            param.setFkUsuario(rs.getInt("fkUsuario"));
        }

        return param;
    }
}
