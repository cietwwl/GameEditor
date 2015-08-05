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
public class Effect_REPEAT_ON_HIT extends Effect_PercentAdd{

    public Effect_REPEAT_ON_HIT(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genBuffFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_PercentAdd: 百分比float
        out.println("        if (context.hited() && isActive && context.target.isAlive()) {");
        out.println("            if (CommonUtil.hit(RND, (int)(100 * multiple * " + p1 + "), 10000)) {");
        out.println("               context.addAdditionSkill(context.skill, true);");
        out.println("            }");
        out.println("        }");
    }

    public void genSkillFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_PercentAdd: 百分比float
        out.println("        if (context.hited() && context.target.isAlive()) {");
        out.println("            if (CommonUtil.hit(RND, (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ")), 10000)) {");
        out.println("               context.addAdditionSkill(context.skill, true);");
        out.println("            }");
        out.println("        }");
    }
}