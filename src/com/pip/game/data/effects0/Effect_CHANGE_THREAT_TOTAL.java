/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_PercentAdd;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_CHANGE_THREAT_TOTAL extends Effect_PercentAdd{

    public Effect_CHANGE_THREAT_TOTAL(int t) {
        super(t);
    }
    
    public String getJavaInterface() throws Exception{
        throw new Exception("此效果不支持buff");
    }


    public void genSkillFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_PercentAdd: 百分比
        out.println("        if (context.hited()) {");
        out.println("            float rate = 1.0f + context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f;");
        out.println("            context.source.changeThreat(context.target, rate);");
        out.println("        }");
    }
}