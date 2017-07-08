package com.github.liketechnik;

import com.github.liketechnik.projects.Project;

import java.util.Properties;

/**
 * @author liketechnik
 * @version 1.0
 * @date 04 of Juli 2017
 */
abstract public  class Test extends Project {

    Class<ProjectBuild> projectClass = ProjectBuild.class;


    public class ProjectBuild {

    }

    public static Properties getProps() {
        return System.getProperties();
    }
}
