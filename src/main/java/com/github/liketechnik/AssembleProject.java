package com.github.liketechnik;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;

/**
 * @author liketechnik
 * @version 1.0
 * @date 08 of Juli 2017
 */
public class AssembleProject {
    public static void main(String[] args) {
        String path = Test.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            String decodedPath = URLDecoder.decode(path, "UTF-8"); // get location of class files of the program itself

            List<String> buildOptions = new LinkedList<String>(){};
            buildOptions.add("-sourcepath");
            buildOptions.add(decodedPath);
            buildOptions.add(new File(System.getProperty("user.dir")).getName() + ".java");


            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

            compiler.run(null, null, null, buildOptions.toArray(new String[]{}));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
