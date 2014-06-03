/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.saurau.server;

import com.saurau.config.AppGlobals;
import com.saurau.config.PropertyFactory;
import java.io.IOException;

/**
 *
 * @author toan
 */
public class TTT {
    public static void main(String[] args) throws Exception {
//        PropertyFactory p = new PropertyFactory();
//        Iterable<Class> c = p.getClasses("com");
//        for (Class class1 : c) {
//            System.out.println("c: " + class1.getName());
//            
//        }
        System.out.println("dd: " + AppGlobals.getProperty("xmpp.domain"));
        System.out.println("dd: " + AppGlobals.getProperty("database.defaultProvider.testSQL"));
    }
}
