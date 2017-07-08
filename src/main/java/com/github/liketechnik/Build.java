package com.github.liketechnik;

import com.github.liketechnik.projects.Project;
import com.github.liketechnik.utils.ProjectClassLoader;

import java.io.File;

/**
 * @author liketechnik
 * @version 1.0
 * @date 04 of Juli 2017
 */
public class Build {
    public final static void main(String[] args) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        String buildClassName = new File(System.getProperty("user.dir")).getName();
        if (!new File(System.getProperty("user.dir") + buildClassName + ".class").exists()) {
            AssembleProject.main(new String[]{});
        }
        Class<? extends Project> projectClass = (Class<? extends Project>) new ProjectClassLoader().loadClass(buildClassName);
        try {
            Project project = projectClass.newInstance();


            project.executeFunctions(project.getBeforeBuild());

            project.build();

            project.executeFunctions(project.getAfterBuild());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
