/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_CureOnHit;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_DEC_MP_ON_HIT extends Effect_CureOnHit{

    public Effect_DEC_MP_ON_HIT(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genBuffFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_CureOnHit: 概率float，固定值int，占上限比例float，占伤害比例float
        out.println("        if (isActive && context.hited() && context.isDamage()) {");
        out.println("            int rate = (int)(multiple * 100 * " + p1 + ");");
        out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
        out.println("                int decmp = " + p2 + ";");
        out.println("                decmp += context.target.maxmp * " + p3 + " / 100.0f;");
        out.println("                decmp += context.damageValue * " + p4 + " / 100.0f;");
        out.println("                context.activeSkills.add(new FixedDecMPSkill(decmp));");
        out.println("                effectTimes++;");
        out.println("            }");
        out.println("        }");
    }

    public void genSkillFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_CureOnHit: 概率float，固定值int，占上限比例float，占伤害比例float
        out.println("        if (context.hited() && context.isDamage()) {");
        out.println("            int rate = (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + "));");
        out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
        out.println("                int decmp = context.getSkillParam(this, \"" + p2 + "\", " + p2 + ");");
        out.println("                decmp += context.target.maxmp * context.getSkillParam(this, \"" + p3 + "\", " + p3 + ") / 100.0f;");
        out.println("                decmp += context.damageValue * context.getSkillParam(this, \"" + p4 + "\", " + p4 + ") / 100.0f;");
        out.println("                context.activeSkills.add(new FixedDecMPSkill(decmp));");
        out.println("            }");
        out.println("        }");
    }
}