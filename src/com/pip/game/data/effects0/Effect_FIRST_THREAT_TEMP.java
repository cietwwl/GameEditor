/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_FirstThreatTemp;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_FIRST_THREAT_TEMP extends Effect_FirstThreatTemp{

    public Effect_FIRST_THREAT_TEMP(int t) {
        super(t);
    }
    
    public String getJavaInterface() throws Exception{
        throw new Exception("此效果不支持buff");
    }


    public void genSkillFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_FirstThreatTemp: 持续时间(毫秒)
        out.println("        if (context.hited()) {");
        out.println("            int keepTime = context.getSkillParam(this, \"" + p1 + "\", " + p1 + ");");
        out.println("            ThreatList.makeFirstThreat(context.target, context.source, keepTime);");
        out.println("        }");
    }
}