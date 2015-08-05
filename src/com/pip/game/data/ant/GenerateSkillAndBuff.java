package com.pip.game.data.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.pip.game.data.ProjectData;
import com.pip.game.editor.util.Settings;

public class GenerateSkillAndBuff extends Task {
    
    protected String source;
    protected String target;

    @Override
    public void execute() throws BuildException {
        ProjectData pd = new ProjectData();
        try {
            ProjectData.setActiveProject(pd);
            pd.load(new File(source));
            Settings.exportClassDir = new File(target);
            Settings.skillPackage = "peony.game.skill";
            Settings.buffPackage = "peony.game.buff";
            pd.generateBuffClasses("gbk");
            pd.generateSkillClasses("gbk");
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new BuildException(e);
        }
    }
    
    public void setSource(String source){
        this.source = source;
    }
    
    public void setTarget(String target){
        this.target = target;
    }
}
