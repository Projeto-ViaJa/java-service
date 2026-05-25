package database.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * Entidade que representa os parâmetros de envio para Slack
 * Consultada antes de cada envio de notificação
 */
public class SlackParams {

    private int id;
    private String urlSlack;
    private String canal;
    private boolean warning;
    private boolean infos;
    private boolean error;
    private Integer fkUsuario;

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUrlSlack() { return urlSlack; }
    public void setUrlSlack(String urlSlack) { this.urlSlack = urlSlack; }

    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }

    public boolean isWarning() { return warning; }
    public void setWarning(boolean warning) { this.warning = warning; }

    public boolean isInfos() { return infos; }
    public void setInfos(boolean infos) { this.infos = infos; }

    public boolean isError() { return error; }
    public void setError(boolean error) { this.error = error; }

    public Integer getFkUsuario() { return fkUsuario; }
    public void setFkUsuario(Integer fkUsuario) { this.fkUsuario = fkUsuario; }

    @Override
    public String toString() {
        return "SlackParams{" +
                "id=" + id +
                ", urlSlack='" + urlSlack + '\'' +
                ", canal='" + canal + '\'' +
                ", warning=" + warning +
                ", infos=" + infos +
                ", error=" + error +
                ", fkUsuario=" + fkUsuario +
                '}';
    }
}
