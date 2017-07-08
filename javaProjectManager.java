import com.github.liketechnik.projects.JavaProject;

import java.io.File;

public class javaProjectManager extends JavaProject {

    @Override
    public String getMainClassName() {
        return "com.github.liketechnik.Build";
    }

    @Override
    public boolean buildJarArchive() {
        return true;
    }
}
