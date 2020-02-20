package beauty.scheduler.dao.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConnectionWrapper implements AutoCloseable {
    private Connection connection;

    ConnectionWrapper(Connection connection) {
        this.connection = connection;
    }

    void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }

    private boolean isTransaction() throws SQLException {
        return !connection.getAutoCommit();
    }

    void commit() throws SQLException {
        connection.commit();
    }

    void rollback() throws SQLException {
        connection.rollback();
    }

    PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    @Override
    public void close() throws SQLException {
        if (!isTransaction()) {
            connection.close();
        }
    }
}