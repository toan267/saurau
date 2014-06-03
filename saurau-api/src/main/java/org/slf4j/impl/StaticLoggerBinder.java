///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package org.slf4j.impl;
//
//import ch.qos.logback.classic.LoggerContext;
//import org.slf4j.ILoggerFactory;
//import org.slf4j.spi.LoggerFactoryBinder;
//
///**
// *
// * @author toan
// */
//public class StaticLoggerBinder implements LoggerFactoryBinder {
//
//    private final LoggerContext defaultLoggerContext = new LoggerContext();
//
//    @Override
//    public ILoggerFactory getLoggerFactory() {
//        return defaultLoggerContext;
//    }
//
//    @Override
//    public String getLoggerFactoryClassStr() {
//        return defaultLoggerContext.getClass().getName();
//    }
//    
//    public static StaticLoggerBinder getSingleton() {
//        return new StaticLoggerBinder();
//    }
//
//}
