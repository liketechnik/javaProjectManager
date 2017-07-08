package com.github.liketechnik.projects;

import java.io.File;
import java.util.*;
import java.util.function.Function;

/**
 * @author liketechnik
 * @version 1.0
 * @date 04 of Juli 2017
 */
public abstract class Project {

    public Project() {
        projectFiles = collectProjectFiles(getProjectDir());
    }

    public File getProjectDir() {
        return new File(System.getProperty("user.dir"));
    }

    public File[] getProjectFiles() {
        return projectFiles;
    }
    private File[] projectFiles;

    public Function[] getBeforeBuild() {
        return new Function[]{};
    }
    public Function[] getAfterBuild() {
        return new Function[]{};
    }

    public abstract boolean build();

    public void executeFunctions(Function[] functions) {
        for (Function function : functions) {
            function.apply(new Object());
        }
    }

    private File[] collectProjectFiles(File projectDir) {
        List<File> projectFiles = new ArrayList<>();
        Boolean foundAllDirs = false;
        int dirLevel = 0;
        HashMap<Integer, File[]> dirMappings = new LinkedHashMap<Integer, File[]>(){};
        dirMappings.put(dirLevel, projectDir.listFiles());
        while  (!foundAllDirs) {
            boolean foundDirectory = false;
            List<File> directories = new LinkedList<>();
            for (File file : dirMappings.get(dirLevel)) {
                if (file.isDirectory()) {
                    for (File directory : file.listFiles()) {
                        directories.add(directory);
                    }
                    foundDirectory = true;
                }
                if (file.isFile()) {
                    projectFiles.add(file.getAbsoluteFile());
                }
            }
            dirMappings.put(dirLevel + 1, directories.toArray(new File[]{}));
            foundAllDirs = !foundDirectory;
            dirLevel += 1;
        }
        return projectFiles.toArray(new File[]{});
    }
}
