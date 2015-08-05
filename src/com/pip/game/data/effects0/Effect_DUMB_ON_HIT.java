/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_FearOnHit;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_DUMB_ON_HIT extends Effect_FearOnHit{

    public Effect_DUMB_ON_HIT(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genBuffFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_FearOnHit: 概率float，概率补充变量String，持续时间int
        out.println("        if (isActive && context.hited() && context.isDamage()) {");
        out.println("            boolean hit = false;");
        out.println("            if (" + p2 + ".length() == 0) {");
        out.println("                hit = CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000);");
        out.println("            } else {");
        out.println("                Float vo = context.skillParams.get(" + p2 + ");");
        out.println("                if (vo != null) {");
        out.println("                    hit = CommonUtil.hit(RND, (int)(multiple * 100 * (vo.floatValue() + " + p1 + ")), 10000);");
        out.println("                }");
        out.println("            }");
        out.println("            if (hit) {");
        out.println("                context.activeSkills.add(new DumbSkill(" + p3 + "));");
        out.println("                effectTimes++;");
        out.println("            }");
        out.println("        }");
    }

    public void genSkillFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_FearOnHit: 概率float，概率补充变量String，持续时间int
        out.println("        if (context.hited()) {");
        out.println("            boolean hit = false;");
        out.println("            if (" + p2 + ".length() == 0) {");
        out.println("                hit = CommonUtil.hit(RND, (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ")), 10000);");
        out.println("            } else {");
        out.println("                Float vo = context.skillParams.get(" + p2 + ");");
        out.println("                if (vo != null) {");
        out.println("                    hit = CommonUtil.hit(RND, (int)(100 * (vo.floatValue() + context.getSkillParam(this, \"" + p1 + "\", " + p1 + "))), 10000);");
        out.println("                }");
        out.println("            }");
        out.println("            if (hit) {");
        out.println("                context.activeSkills.add(new DumbSkill(context.getSkillParam(this, \"" + p3 + "\", " + p3 + ")));");
        out.println("            }");
        out.println("        }");
    }
}
