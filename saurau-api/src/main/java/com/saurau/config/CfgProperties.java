/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saurau.config;

/**
 * Class is template for furture config, 
 * which is able automatic load in PropertyFactory
 * each subtype need set priotiry to order the first load and this order can 
 * change when finishing load, let do it in function loadProperties, which is
 * first call after created
 * when we looking for properties, we will find in less priotiry first
 * @author toan
 */
public abstract class CfgProperties implements Comparable<CfgProperties> {

    protected boolean loaded = false;
    protected String name;

    @Override
    public int compareTo(CfgProperties o) {
        if (this.getPriority() > o.getPriority()) {
            return 1;
        }
        if (this.getPriority() < o.getPriority()) {
            return -1;
        }
        return 0;
    }

    public CfgProperties(Class clazz) {
        this.name = clazz.getSimpleName();
    }

    public boolean isLoaded() {
        return loaded;
    }

    public String getName() {
        return name;
    }

    public abstract String getProperty(String name);

    public abstract String getProperty(String name, String defaultValue);

    public abstract void loadProperties();

    public abstract int getPriority();

    public abstract void setPriority(int priority);

    int MASTER = 10;
    int SLAVE = 20;

}
