/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saurau.data;

import com.saurau.config.AppGlobals;
import java.sql.Connection;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tom
 */
public class DBConnectionManager {

    private static final Logger logger = LoggerFactory.getLogger(DBConnectionManager.class);
    private static final Object lock = new Object();
    private static ConnectionProvider connectionProvider;

    private synchronized static void setConnectionProvider(ConnectionProvider dcProvider) {
        if (connectionProvider != null) {
            connectionProvider.destroy();
        }
        connectionProvider = dcProvider;
        connectionProvider.start();
    }

    public static Connection getConnection() throws SQLException {
        synchronized (lock) {
            if (connectionProvider == null) {
                String className = AppGlobals.getProperty("connectionProvider.className");
                if (className == null || className.isEmpty()) {
                    setConnectionProvider(new DefaultConnectionProvider());
                } else {
                    try {
                        Class provider = Class.forName(className);
                        setConnectionProvider((ConnectionProvider) provider.newInstance());
                    } catch (Exception ex) {
                        logger.warn("Failed to create the "
                                + "connection provider specified by connection"
                                + "Provider.className. Using the default pool.", ex);
                        setConnectionProvider(new DefaultConnectionProvider());
                    }
                }
            }
        }
        // TODO: May want to make these settings configurable
        Integer retryCnt = 0;
        Integer retryMax = 10;
        Integer retryWait = 250; // milliseconds
        Connection con = null;
        SQLException lastException = null;
        do {
            try {
                con = connectionProvider.getConnection();
                if (con != null) {
                    // Got one, lets hand it off.
                    // Usually profiling is not enabled. So we return a normal 
                    // connection unless profiling is enabled. If yes, wrap the
                    // connection with a profiled connection.
//                    if (!profilingEnabled) {
//                        return con;
//                    } else {
//                        return new ProfiledConnection(con);
//                    }
                    return con;
                }
            } catch (SQLException e) {
                // TODO distinguish recoverable from non-recoverable exceptions.
                lastException = e;
                logger.warn("Unable to get a connection from the database pool "
                        + "(attempt " + retryCnt + " out of " + retryMax + ").", e);
            }
            try {
                Thread.sleep(retryWait);
            } catch (Exception e) {
                // Ignored
            }
            retryCnt++;
        } while (retryCnt <= retryMax);
        throw new SQLException("ConnectionManager.getConnection() "
                + "failed to obtain a connection after " + retryCnt + " retries. "
                + "The exception from the last attempt is as follows: " + lastException);
    }

    public static String getConnectionInfo() {
        return connectionProvider.getInfo();
    }
}
