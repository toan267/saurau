/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saurau.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.stream.XMLStreamException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * looking properties in xml file, see CfgProperties for detail
 *
 * @author tom
 */
public class XMLProperties extends CfgProperties {

    private static final Logger logger = LoggerFactory.getLogger(XMLProperties.class);
    private Map<String, String> propertyCache;
    Document document;
    private int priority = MASTER;

    public static String APP_CONFIG_FILENAME = "etc" + File.separator + "app.xml";

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    private File fileXml;

    public XMLProperties() {
        super(XMLProperties.class);
    }

    @Override
    public void loadProperties() {
        try {
            if (fileXml == null) {
                fileXml = new File(System.getProperty("app.home") + File.separator + APP_CONFIG_FILENAME);
            }
            logger.debug("Create XMLProperties from file: {}", fileXml.getName());
            this.propertyCache = new ConcurrentHashMap<>();
            SAXReader xmlReader = new SAXReader();
            xmlReader.setEncoding("UTF-8");
            Reader in = new FileReader(fileXml);
            document = xmlReader.read(in);
            loaded = true;
        } catch (Exception e) {
            //ignore exception
        }
    }

    public XMLProperties(File fileXml) throws FileNotFoundException, DocumentException, XMLStreamException {
        super(XMLProperties.class);
        this.fileXml = fileXml;
    }

    public XMLProperties(String fileXml) throws FileNotFoundException, DocumentException, XMLStreamException {
        this(new File(fileXml));
    }

    @Override
    public String getProperty(String name) {
        logger.debug("look for cached: {}", name);
        String value = propertyCache.get(name);
        if (value != null) {
            return value;
        }
        logger.debug("look for file xml");
        Element element = document.getRootElement();
        StringTokenizer tokenizer = new StringTokenizer(name, ".");
        String aPropName;
        while (tokenizer.hasMoreTokens()) {
            aPropName = tokenizer.nextToken();
            element = element.element(aPropName);
            if (element == null) {
                return null;
            }
        }
        value = element.getTextTrim();
        if ("".equals(value)) {
            return null;
        } else {
            // Add to cache so that getting property next time is fast.
            logger.debug("add {} : {} to cached", name, value);
            propertyCache.put(name, value);
            return value;
        }
    }

    @Override
    public String getProperty(String name, String defaultValue) {
        logger.debug("look for cached: {}", name);
        String value = propertyCache.get(name);
        if (value != null) {
            return value;
        }
        logger.debug("look for file xml");
        Element element = document.getRootElement();
        StringTokenizer tokenizer = new StringTokenizer(name, ".");
        String aPropName;
        while (tokenizer.hasMoreTokens()) {
            aPropName = tokenizer.nextToken();
            element = element.element(aPropName);
            if (element == null) {
                break;
            }
        }
        value = element == null ? "" : element.getTextTrim();
        if ("".equals(value)) {
            logger.debug("Can not find {}, set default value {}", name, defaultValue);
            return defaultValue;
        } else {
            // Add to cache so that getting property next time is fast.
            logger.debug("add {} : {} to cached", name, value);
            propertyCache.put(name, value);
            return value;
        }
    }
}
