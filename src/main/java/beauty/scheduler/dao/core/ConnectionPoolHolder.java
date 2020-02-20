package beauty.scheduler.dao.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

class ConnectionPoolHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPoolHolder.class);
    private static volatile DataSource dataSource;

    private static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (ConnectionPoolHolder.class) {
                if (dataSource == null) {
                    try {
                        Context initContext = new InitialContext();
                        dataSource = (DataSource) initContext.lookup("java:/comp/env/jdbc/beauty_scheduler");
                    } catch (NamingException e) {
                        LOGGER.error("Could not find DataSource JNDI", e);
                    }
                }
            }
        }
        return dataSource;
    }

    static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }
}