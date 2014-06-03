/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saurau.data;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.saurau.config.AppGlobals;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tom
 */
public class DefaultConnectionProvider implements ConnectionProvider{

    private static final Logger logger = LoggerFactory.getLogger(DefaultConnectionProvider.class);

    private String driver;
    private String serverURL;
    private String username;
    private String password;
    private String testSQL;

    BoneCP connectionPool = null;

    @Override
    public Connection getConnection() throws SQLException {
        if (connectionPool == null) {
            logger.debug("connectionPool is null. need start connection provider!!");
            return null;
        }
        return connectionPool.getConnection();
    }

    public DefaultConnectionProvider() {
        loadProperties();
        //create pool
//        start();
    }

    public void start() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            logger.error("Can not load driver: {}", driver);
            return;
        }
        BoneCPConfig config = new BoneCPConfig();
        config.setJdbcUrl(serverURL); // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
        config.setUsername(username);
        config.setPassword(password);
//        config.setMaxConnectionsPerPartition(4);
        config.setMaxConnectionAgeInSeconds(10);
        try {
            connectionPool = new BoneCP(config); // setup the connection pool
        } catch (SQLException ex) {
            logger.error("pooling with BoneCP ERROR: {}", ex);
            return;
        }
        //test connection
        try (Connection connection = connectionPool.getConnection()) {
            if (connection != null) {
                logger.info("Connection successful!");
                Statement stmt = connection.createStatement();
                try (ResultSet rs = stmt.executeQuery(testSQL)) {
                    while (rs.next()) {
                        logger.debug("execute testSQL successfully, result is {}", rs.getString(1)); // should print out "1"'
                    }
                } catch (SQLException ex) {
                    logger.error("execute testSQL: {} ERROR: {}", testSQL, ex.getStackTrace());
                }
            }
        } catch (Exception e) {
            logger.error("Test connection from pool ERROR: {}", e);
            return;
        }
    }

    private void loadProperties() {
        driver = AppGlobals.getProperty("database.defaultProvider.driver");
        serverURL = AppGlobals.getProperty("database.defaultProvider.serverURL");
        username = AppGlobals.getProperty("database.defaultProvider.username");
        password = AppGlobals.getProperty("database.defaultProvider.password");
//        String minCons = JiveGlobals.getXMLProperty("database.defaultProvider.minConnections");
//        String maxCons = JiveGlobals.getXMLProperty("database.defaultProvider.maxConnections");
//        String conTimeout = JiveGlobals.getXMLProperty("database.defaultProvider.connectionTimeout");
        testSQL = AppGlobals.getProperty("database.defaultProvider.testSQL");
//        testSQL = BoomGlobals.getXmlProperty("database.defaultProvider.testSQL", DbConnectionManager.getTestSQL(driver));
//        testBeforeUse = JiveGlobals.getXMLProperty("database.defaultProvider.testBeforeUse", false);
//        testAfterUse = JiveGlobals.getXMLProperty("database.defaultProvider.testAfterUse", false);

        // See if we should use Unicode under MySQL
//        mysqlUseUnicode = Boolean.valueOf(JiveGlobals.getXMLProperty("database.mysql.useUnicode"));
//        try {
//            if (minCons != null) {
//                minConnections = Integer.parseInt(minCons);
//            }
//            if (maxCons != null) {
//                maxConnections = Integer.parseInt(maxCons);
//            }
//            if (conTimeout != null) {
//                connectionTimeout = Double.parseDouble(conTimeout);
//            }
//        } catch (Exception e) {
//            Log.error("Error: could not parse default pool properties. "
//                    + "Make sure the values exist and are correct.", e);
//        }
    }
    
    public void destroy() {
        connectionPool.shutdown();
        connectionPool = null;
    }
    
    public void restart() {
        destroy();
        start();
    }
    

    @Override
    public String getInfo() {
        StringBuilder buf = new StringBuilder("driver: ");
        buf.append(driver).append("\\\\nURL: ").append(serverURL).append("\\\\nName: ").append(username);
        return buf.toString();
    }
}
