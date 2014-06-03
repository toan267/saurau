/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saurau.config;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tom
 */
public class AppGlobals {

    private static final Logger logger = LoggerFactory.getLogger(AppGlobals.class);
    private static final String home;

    static {
        String boomHome = System.getProperty("app.home");
        boomHome = boomHome == null ? "" : boomHome.trim();
        home = boomHome.isEmpty()
                ? AppGlobals.class.getClassLoader().getResource("").getPath()
                : boomHome.trim();
        logger.debug("Execute from dir {}", home);
        System.setProperty("app.home", home);
    }

    public static String getProperty(String name) {
        return PropertyFactory.getProperty(name);
    }

    public static String getProperty(String name, String defaultValue) {
        String value = getProperty(name);
        return value == null ? defaultValue : value;
    }

    public static boolean getBooleanProperty(String name, boolean defaultValue) {
        String value = getProperty(name);
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            //ignore exception
        }
        return defaultValue;
    }

    public static String getHomeDir() {
        return home;
    }

    public static String getFileLogPath() {
        return home + File.separator + getProperty("logback.file");
    }

    public static int getIntProperty(String name, int defaultValue) {
        String value = getProperty(name);
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            //ignore exception
        }
        return defaultValue;
    }

    /**
     * Convenience routine to migrate an XML property into the database storage
     * method. Will check for the XML property being null before migrating.
     *
     * @param name the name of the property to migrate.
     */
    public static void migrateProperty(String name) {
    }
}
