package database.config;

import exceptions.DbException;
import logger.AppLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DB {
    private static Connection conn = null;

    public static Connection getConnection () {
        try {
            if (conn == null || conn.isClosed() || !conn.isValid(2)) {
                Properties props = loadProperties();
                String url = props.getProperty("dburl");
                conn = DriverManager.getConnection(url, props);
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return conn;
    }

    private static Properties loadProperties () {
        try (FileInputStream fs = new FileInputStream("db.properties")) {
            Properties props = new Properties();
            props.load(fs);
            return props;
        } catch (IOException e) {

            throw new DbException(e.getMessage());
        }
    }

    public static void closeConnection () {
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
