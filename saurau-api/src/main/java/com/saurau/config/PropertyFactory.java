/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saurau.config;

import com.saurau.reflection.Reflections;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The factory will automatic find all subtype of CfgProperties and load them
 *
 * @author toan
 */
public class PropertyFactory {

    private static final Logger logger = LoggerFactory.getLogger(PropertyFactory.class);
    private static final List<CfgProperties> properties = new ArrayList<>();

    static {
        Set<Class> subClazz = Reflections.getSubTypeOf(CfgProperties.class, "com.saurau");
        CfgProperties pro;
        for (Class clazz : subClazz) {
            try {
                pro = (CfgProperties) clazz.newInstance();
                properties.add(pro);
            } catch (Exception e) {
                //ignore exception
            }
        }
        Collections.sort(properties);
        for (CfgProperties cfg : properties) {
            cfg.loadProperties();
        }
        Collections.sort(properties);
    }

    public static String getProperty(String name) {
        String result;
        for (CfgProperties cfg : properties) {
            if (cfg.isLoaded()) {
                logger.debug("Get property {} from {}", name, cfg.getName());
                result = cfg.getProperty(name);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public static List<CfgProperties> getProperties() {
        return properties;
    }
}
