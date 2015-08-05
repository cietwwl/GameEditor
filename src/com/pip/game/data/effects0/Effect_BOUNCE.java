/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_Bounce;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_BOUNCE extends Effect_Bounce{

    public Effect_BOUNCE(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genBuffFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_Bounce: 概率float，伤害类型int，固定值int，占伤害比例float
        out.println("        if (!isActive && context.hited() && context.isDamage()) {");
        out.println("            if (CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000)) {");
        out.println("                int dmg = " + p3 + ";");
        out.println("                dmg += context.damageValue * " + p4 + " / 100.0f;");
        out.println("                context.passiveSkills.add(new FixedDamageSkill(" + p2 + ", dmg));");
        out.println("                effectTimes++;");
        out.println("            }");
        out.println("        }");
    }
}
