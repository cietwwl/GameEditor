package com.pip.game.data.AI;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.pip.game.data.ProjectData;
import com.pip.game.editor.util.Settings;

public class GenerateComplexAi extends Task{
    protected String source;
    protected String target;

    public void execute() throws BuildException {
        ProjectData proj = new ProjectData();
        try {
            ProjectData.setActiveProject(proj);
            proj.load(new File(source));
            Settings.exportClassDir = new File(target);//shaft/buff/auto
//            Settings.skillPackage = "shaft.skill.auto";
//            Settings.buffPackage = "shaft.buff.auto";
//            proj.generateBuffClasses("UTF-8");
//            proj.generateSkillClasses("UTF-8");
            Settings.aiPackage = "shaft.ai.auto";
            proj.generateAIClasses("UTF-8");
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new BuildException(e);
        }
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
