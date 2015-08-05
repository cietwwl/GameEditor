package com.pip.game.data.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.pip.game.data.ProjectData;

public class BuildComplexFileVersion extends Task {
    protected String source;

    public void execute() throws BuildException {
        ProjectData proj = new ProjectData();
        try {
            ProjectData.setActiveProject(proj);
            proj.load(new File(source));
            proj.generateResourceVersionXML();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new BuildException(e);
        }
    }

    public void setSource(String source) {
        this.source = source;
    }
    
}
