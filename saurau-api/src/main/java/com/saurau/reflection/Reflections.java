/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saurau.reflection;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * @author toan
 */
public class Reflections {

    public static Set<Class> getSubTypeOf(Class<?> parent, String packageName) {
        Iterable<Class> allClazz = null;
        try {
            allClazz = getClasses(packageName);
        } catch (Exception e) {
            //ignore exception
            e.printStackTrace();
        }
        Set<Class> result = new HashSet<>();
        if (allClazz != null) {
            for (Class clazz : allClazz) {
                try {
                    if (parent.isAssignableFrom(clazz)) {
                        result.add(clazz);
                    }
                } catch (ClassCastException e) {
                    //ignore exception
                }
            }
        }
        return result;
    }

    /**
     * Scans all classes accessible from the context class loader which belong
     * to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Iterable<Class> getClasses(String packageName) throws ClassNotFoundException, IOException, URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<Class> classes = new ArrayList<Class>();
        URL resource;
        while (resources.hasMoreElements()) {
            resource = resources.nextElement();
            classes.addAll(findClasses(resource, packageName));
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * subdirs.
     *
     * @param directory The base directory
     * @param packageName The package name for classes found inside the base
     * directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    public static List<Class> findClasses(URL resource, String packageName) throws ClassNotFoundException, URISyntaxException, MalformedURLException, IOException {
        List<Class> classes = new ArrayList<>();
        File[] files;
        switch (resource.getProtocol()) {
            case "file":
                files = new File(resource.toURI()).listFiles();
                for (File file : files) {
                    if (file.isDirectory()) {
                        classes.addAll(findClasses(file.toURI().toURL(), packageName + "." + file.getName()));
                    } else if (file.getName().endsWith(".class")) {
                        classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                    }
                }
                break;
            case "jar":
                String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!")); //strip out only the JAR file
                JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
                Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
                String name,
                 javaName;
                int classExtIdx;
                while (entries.hasMoreElements()) {
                    name = entries.nextElement().getName();
                    classExtIdx = name.indexOf(".class");
                    if (name.startsWith(packageName) && classExtIdx > 0) { //filter according to the path
                        javaName = name.substring(0, classExtIdx).replace("/", ".");
                        classes.add(Class.forName(javaName));
                    }
                }
                break;
        }
        return classes;
    }
}
