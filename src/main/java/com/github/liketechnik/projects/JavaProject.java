package com.github.liketechnik.projects;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * @author liketechnik
 * @version 1.0
 * @date 05 of Juli 2017
 */
public abstract class JavaProject extends Project {
    public File getSrcDir() {
        return new File("src/main/java");
    }

    public File[] getSrcFiles() {
        return srcFiles;
    }
    private File[] srcFiles;

    public File getBuildDir() {
        return new File("build/main/");
    }

    public String getJarArchiveName() {
        String[] test = System.getProperty("user.dir").split("/");
        return test[test.length - 1];
    }

    public boolean buildJarArchive() {
        return false;
    }

    public abstract String getMainClassName();
    
    File getCompileOutputDir() {
        return new File(getBuildDir().getAbsolutePath() + "/classes/");
    }

    public JavaProject() {
        super();
        System.out.println("after super");

        if (!getBuildDir().isDirectory()) {
            System.out.println(getBuildDir().getAbsolutePath());
            if (!getBuildDir().mkdir()) {
                System.exit(1);
            }
        }
        if (!getCompileOutputDir().isDirectory()) {
            if (!getCompileOutputDir().mkdir()) {
                System.exit(1);
            }
        }
        System.out.println("Running");

        srcFiles = filterSrcFiles();
        System.out.println("filtered source files");
    }

    private File[] filterSrcFiles() {
        List<File> srcFiles = new LinkedList<File>(){};
        for (File file : getProjectFiles()) {
            if (file.getAbsolutePath().replace(System.getProperty("user.dir"), "").contains(getSrcDir().toString())) {
                srcFiles.add(file);
            }
        }
        return  srcFiles.toArray(new File[]{});
    }

    @Override
    public boolean build() {
        List<String> buildOptions = new LinkedList<String>(){};
        buildOptions.add("-d");
        buildOptions.add(getCompileOutputDir().getAbsolutePath());
        buildOptions.add("-sourcepath");
        buildOptions.add(getSrcDir().getAbsolutePath());

        for (File file : getSrcFiles()) {
            buildOptions.add(file.getAbsolutePath());
            System.out.println(file.getAbsolutePath());
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        compiler.run(null, null, null, buildOptions.toArray(new String[]{}));

        if (buildJarArchive()) {
            assembleJarArchive();
        }

        return true;
    }

    void assembleJarArchive() {
        HashMap<Integer, File[]> builtFiles = collectBuiltFiles(getCompileOutputDir().getAbsoluteFile());
        Map<Integer, Map<File, String>> compiledFiles = new HashMap<Integer, Map<File, String>>(){};
        for (Integer i : builtFiles.keySet()) {
            Map<File, String> fileStringMap = new HashMap<File, String>(){};
            for (File file : builtFiles.get(i)) {
                String builtFilePath = file.getAbsolutePath().replace(getSrcDir().getAbsolutePath(), getCompileOutputDir().getAbsolutePath());
                boolean isDir;
                if (file.isDirectory()) {
                    isDir = true;
                } else {
                    isDir = false;
                }

                File builtFile = new File(builtFilePath);
                if (!builtFile.exists()) {
                    System.err.println("File was to compile but no class file available!");
                } else {
                    String builtFileName = builtFile.getAbsolutePath().replace(getCompileOutputDir().getAbsolutePath(), "").replace("\\̄", "/̄");
                    if (isDir) {
                        if (!builtFileName.endsWith("/")) {
                            builtFileName += "/";
                        }
                    } else {
                        if (builtFileName.endsWith("/")) {
                            builtFileName = builtFileName.substring(0, builtFileName.lastIndexOf("/"));
                        }
                    }
                    if (builtFileName.startsWith("/")) {
                        builtFileName = builtFileName.substring(1);
                    }
                    fileStringMap.putIfAbsent(builtFile, builtFileName);
                }
            }
            compiledFiles.putIfAbsent(i, fileStringMap);
        }

        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, getMainClassName());
        System.out.println(getCompileOutputDir().getPath());
        try {
            JarOutputStream jarFile = new JarOutputStream(new FileOutputStream(System.getProperty("user.dir") + "/" + getBuildDir().getPath() + "/"  + getJarArchiveName() + ".jar"), manifest);
            createJarArchive(compiledFiles, jarFile);
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void createJarArchive(Map<Integer, Map<File, String>> toInclude, JarOutputStream target) {
        BufferedInputStream in = null;
        for (int i : toInclude.keySet()) {
            Map<File, String> fileStringMap = toInclude.get(i);
            for (File fileToInclude : fileStringMap.keySet()) {
                try {
                    System.out.println("File name: " + fileStringMap.get(fileToInclude));
                    JarEntry entry = new JarEntry(fileStringMap.get(fileToInclude));
                    entry.setTime(fileToInclude.lastModified());
                    target.putNextEntry(entry);
                    if (!fileToInclude.isDirectory()) {
                        in = new BufferedInputStream(new FileInputStream(fileToInclude));

                        byte[] buffer = new byte[1024];
                        while (true) {
                            int count = in.read(buffer);
                            if (count == -1) {
                                break;
                            }
                            target.write(buffer, 0, count);
                        }
                    }
                    target.closeEntry();
                } catch (IOException e) {
                    System.err.println("Error reading from file " + fileToInclude.getAbsolutePath());
                    e.printStackTrace();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private HashMap<Integer, File[]> collectBuiltFiles(File buildDir) {
        List<File> projectFiles = new ArrayList<>();
        Boolean foundAllDirs = false;
        int dirLevel = 1;
        HashMap<Integer, File[]> dirMappings = new LinkedHashMap<Integer, File[]>(){};
        dirMappings.put(dirLevel, buildDir.listFiles());
        while  (!foundAllDirs) {
            boolean foundDirectory = false;
            List<File> directories = new LinkedList<>();
            for (File file : dirMappings.get(dirLevel)) {
                if (file.isDirectory()) {
                    for (File directory : file.listFiles()) {
                        if (directory.isDirectory()) {
                            directories.add(directory);
                            foundDirectory = true;
                        }
                        if (directory.isFile()) {
                            projectFiles.add(directory.getAbsoluteFile());
                        }
                    }
                }
                if (file.isFile()) {
                    projectFiles.add(file.getAbsoluteFile());
                }
            }
            dirMappings.put(dirLevel + 1, directories.toArray(new File[]{}));
            foundAllDirs = !foundDirectory;
            dirLevel += 1;
        }
        dirMappings.put(dirLevel, projectFiles.toArray(new File[]{}));
        return dirMappings;
    }
}
