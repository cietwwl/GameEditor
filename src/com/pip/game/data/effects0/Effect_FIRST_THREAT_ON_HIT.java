/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_FirstThreat;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_FIRST_THREAT_ON_HIT extends Effect_FirstThreat{

    public Effect_FIRST_THREAT_ON_HIT(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genBuffFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_FirstThreat: 无参数
        out.println("        if (isActive && context.hited() && context.isAttack()) {");
        out.println("            ThreatList.makeFirstThreat(context.source, context.target, -1);");
        out.println("            effectTimes++;");
        out.println("        }");
    }

    public void genSkillFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_FirstThreat: 无参数
        out.println("        if (context.hited()) {");
        out.println("            ThreatList.makeFirstThreat(context.source, context.target, -1);");
        out.println("        }");
    }
}