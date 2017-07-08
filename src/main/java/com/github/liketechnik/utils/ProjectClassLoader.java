package com.github.liketechnik.utils;

import com.github.liketechnik.projects.Project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author liketechnik
 * @version 1.0
 * @date 04 of Juli 2017
 */
public class ProjectClassLoader extends ClassLoader {
    @Override
    public Class<? extends Project> findClass(String name) {
        byte[] b = loadClassData(name);
        Class clazz = defineClass(name, b, 0, b.length);
        try {
            Class<? extends Project> projectClass = clazz;
            return projectClass;
        } catch (ClassCastException e) {
            System.out.println("Error at cast");
            throw e;
        }
    }

    private byte[] loadClassData(String name) {
        RandomAccessFile classFile;
        try {
            classFile = new RandomAccessFile(System.getProperty("user.dir") + "/" + name + ".class", "r");
             byte[] b = new byte[(int) classFile.length()];
            classFile.readFully(b);
            return b;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
