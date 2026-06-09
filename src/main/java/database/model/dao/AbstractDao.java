package database.model.dao;

import exceptions.DbException;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractDao {
    protected Connection conn;

    public AbstractDao (Connection conn) {
        this.conn = conn;
    }

    protected void rollback() {
        try {
            conn.rollback();
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }
}
