package com.pip.game.data.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;

public class BuildFileVersion extends Task {
    
    protected String source;
    
    @Override
    public void execute() throws BuildException {
        ProjectData pd = new ProjectData();
        try {
            ProjectData.setActiveProject(pd);
            pd.load(new File(source), this.getClass().getClassLoader());
            pd.generateResourceVersionXML();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new BuildException(e);
        }
    }

    public void setSource(String source){
        this.source = source;
    }
}
