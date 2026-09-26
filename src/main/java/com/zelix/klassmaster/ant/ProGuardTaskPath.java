package com.zelix.klassmaster.ant;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

public class ProGuardTaskPath extends Path {
    public String fileName;

    public String getFileName() {
        return this.fileName;
    }

    public void setFile(String string) {
        this.fileName = string;
    }

    public ProGuardTaskPath(Project project) {
        super(project);
    }

    public void setDir(String string) {
        this.fileName = string;
    }
}
