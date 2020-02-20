package beauty.scheduler.dao.core;

import beauty.scheduler.util.ExceptionKind;
import beauty.scheduler.util.ExtendedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class TransactionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManager.class);

    private static ThreadLocal<ConnectionWrapper> connectionThreadLocal = new ThreadLocal<>();

    static ConnectionWrapper getConnection() throws SQLException {
        ConnectionWrapper connectionWrapper = connectionThreadLocal.get();

        if (connectionWrapper != null) {
            return connectionWrapper;
        }

        return new ConnectionWrapper(ConnectionPoolHolder.getConnection());
    }

    public static void startTransaction() throws SQLException {
        ConnectionWrapper connectionWrapper = connectionThreadLocal.get();

        if (connectionWrapper != null) {
            throw new SQLException("Transaction is already started");
        }

        connectionWrapper = new ConnectionWrapper(ConnectionPoolHolder.getConnection());
        connectionWrapper.setAutoCommit(false);
        connectionThreadLocal.set(connectionWrapper);
    }

    public static void commit() throws ExtendedException {
        ConnectionWrapper connectionWrapper = connectionThreadLocal.get();

        if (connectionWrapper == null) {
            LOGGER.error("No transaction started to be committed");
            throw new ExtendedException(ExceptionKind.REPOSITORY_ISSUE);
        }

        boolean successfulCommit;

        try {
            connectionWrapper.commit();
            connectionWrapper.setAutoCommit(true);
            connectionWrapper.close();
            successfulCommit = true;
        } catch (SQLException e) {
            LOGGER.error("error during commit " + e.getMessage());
            successfulCommit = false;
        } finally {
            connectionThreadLocal.set(null);
        }

        if (!successfulCommit) {
            throw new ExtendedException(ExceptionKind.REPOSITORY_ISSUE);
        }
    }

    public static void rollback() {
        ConnectionWrapper connectionWrapper = connectionThreadLocal.get();

        if (connectionWrapper == null) {
            LOGGER.error("No transaction started to be rolled back");
            return;
        }

        try {
            connectionWrapper.rollback();
            connectionWrapper.setAutoCommit(true);
            connectionWrapper.close();
        } catch (SQLException e) {
            LOGGER.error("error during rollback " + e.getMessage());
        } finally {
            connectionThreadLocal.set(null);
        }
    }
}