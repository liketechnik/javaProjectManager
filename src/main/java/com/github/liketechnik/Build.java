package com.github.liketechnik;

import com.github.liketechnik.projects.Project;
import com.github.liketechnik.utils.ProjectClassLoader;

import java.io.File;

/**
 * Runs a project build.
 *
 * @author liketechnik
 * @version 1.0
 * @date 04 of Juli 2017
 */
public class Build {
    /**
     * Runs a project build. To do this it
     * - first checks if the class file for the project exists
     * - then constructs a new instance of the project class
     * - and finally invokes the project methods for building it
     * @param args Command line arguments.
     */
    public final static void main(String[] args) {
        File buildClass = new File(new File (System.getProperty("user.dir")).getName());
        if (!new File(buildClass.getAbsolutePath() +  ".class").exists()) {
            AssembleProject.main(new String[]{});
        }
        Class<? extends Project> projectClass = null;
        try {
            projectClass = (Class<? extends Project>) new ProjectClassLoader().loadClass(buildClass.getName());

            Project project = projectClass.newInstance();


            project.executeFunctions(project.getBeforeBuild());

            project.build();

            project.executeFunctions(project.getAfterBuild());
        } catch (ClassNotFoundException e) {
            System.err.println("Project class not found on class path.");
        } catch (IllegalAccessException | InstantiationException e) {
            System.err.println("Error while instantiating a new project class instance. This should not happen when your " +
                    "project file is correct structured.");
        }
    }
}
