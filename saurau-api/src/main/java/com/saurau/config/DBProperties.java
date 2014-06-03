/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saurau.config;

import com.saurau.data.DBConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * looking properties in database, see CfgProperties for detail
 * @author tom
 */
public class DBProperties extends CfgProperties {

    private static final Logger logger = LoggerFactory.getLogger(DBProperties.class);

    protected Map<String, String> propertyCache;
    private static final String LOAD_PROPERTIES = "SELECT name, propValue FROM ofProperty";
    private static final String GET_PROPERTY = "SELECT propValue FROM ofProperty where name = ?";

    private int priority = SLAVE;

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public DBProperties() {
        super(DBProperties.class);
    }

    @Override
    public void loadProperties() {
        Connection con = null;
        propertyCache = new ConcurrentHashMap<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOAD_PROPERTIES);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                propertyCache.put(rs.getString(1), rs.getString(2));
            }
            priority = MASTER - 1;
            loaded = true;
        } catch (Exception e) {
            logger.error("execute {} from connection {} ERROR", LOAD_PROPERTIES, con);
        } finally {
            try {
                closeDB(rs, pstmt, con);
            } catch (Exception e) {
                //ignore exception
            }
        }
    }

    public void loadProperties(Connection con) {
        propertyCache = new ConcurrentHashMap<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con.prepareStatement(LOAD_PROPERTIES);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                propertyCache.put(rs.getString(1), rs.getString(2));
            }
        } catch (Exception e) {
            logger.error("execute {} from connection {} ERROR", LOAD_PROPERTIES, con);
        } finally {
            try {
                closeDB(rs, pstmt, con);
            } catch (Exception e) {
                //ignore exception
            }
        }
    }

    public DBProperties(Connection con) {
        this();
        loadProperties(con);
    }

    @Override
    public String getProperty(String name) {
        logger.debug("call getProperty for name: {}", name);
        String value = propertyCache.get(name);
        if (value != null) {
            logger.debug("look for cached: {{}: {}}", name, value);
            return value;
        }
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBConnectionManager.getConnection();
            logger.debug("look for database, con: {}", con);
            pstmt = con.prepareStatement(GET_PROPERTY);
            pstmt.setString(1, name);
            pstmt.setMaxRows(1);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                value = rs.getString(1);
                logger.debug("add {} : {} to cached", name, value);
                propertyCache.put(name, value);
            }
            return value;
        } catch (Exception e) {
            logger.error("execute {} from connection {} ERROR", GET_PROPERTY, con);
        } finally {
            try {
                closeDB(rs, pstmt, con);
            } catch (Exception e) {
                //ignore exception
            }
        }
        return null;
    }

    @Override
    public String getProperty(String name, String defaultValue) {
        logger.debug("look for cached: {}", name);
        String value = propertyCache.get(name);
        if (value != null) {
            return value;
        }
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBConnectionManager.getConnection();
            logger.debug("look for database, con: {}", con);
            pstmt = con.prepareStatement(GET_PROPERTY);
            pstmt.setString(1, name);
            pstmt.setMaxRows(1);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                value = rs.getString(1);
                logger.debug("add {} : {} to cached", name, value);
                propertyCache.put(name, value);
            }
            return value;
        } catch (SQLException e) {
            logger.error("execute {} from connection {} ERROR", GET_PROPERTY, con);
            logger.info("return default value {} for name: {}", defaultValue, name);
        } finally {
            try {
                closeDB(rs, pstmt, con);
            } catch (SQLException e) {
                //ignore exception
            }
        }
        logger.info("return default value {} for name: {}", defaultValue, name);
        return defaultValue;
    }

    private void closeDB(ResultSet rs, PreparedStatement pstmt, Connection con) throws SQLException {
        rs.close();
        pstmt.close();
        con.close();
    }
}
