/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_PercentAdd;
import com.pip.game.data.skill.Effect_SelectSkill;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_COUNTER_ATTACK extends Effect_SelectSkill{

    public Effect_COUNTER_ATTACK(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genBuffFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_PercentAdd: °Ù·Ö±Èfloat
        out.println("        if (!isActive && context.hited() && context.isDamage()) {");
        out.println("            if (CommonUtil.hit(RND, (int)(multiple * 100 * " + p3 + "), 10000)) {");
        out.println("                context.activeSkills.add(SkillUtil.getSkill(Integer.parseInt(" + p1 + "), " + p2 + "));");
        out.println("                effectTimes++;");
        out.println("            }");
        out.println("        }");
    }
}
