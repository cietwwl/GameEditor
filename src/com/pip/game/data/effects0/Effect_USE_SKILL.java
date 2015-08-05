package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.BuffConfig;
import com.pip.game.data.skill.Effect_SelectSkill;
import com.pip.game.data.skill.SkillConfig;

/**
 * ´¥·¢¼¼ÄÜ
 * @author yqwang
 */
public class Effect_USE_SKILL extends Effect_SelectSkill{

    public Effect_USE_SKILL(int t) {
        super(t);
    }
    
    public void genBuffFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        out.println("        if (context.hited()) {");
        out.println("            boolean hit = CommonUtil.hit(RND, (int)(multiple * 100 * " + p3 + "), 10000);");
        out.println("            if (hit) {");
        out.println("                context.activeSkills.add(SkillUtil.getSkill(Integer.parseInt(" + p1 + "), " + p2 + "));");
        out.println("            }");
        out.println("        }");
    }

    public void genSkillFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        out.println("        if (context.hited()) {");
        out.println("            boolean hit = CommonUtil.hit(RND, (int)(100 * context.getSkillParam(this, \"" + p3 + "\", " + p3 + ")), 10000);");
        out.println("            if (hit) {");
        out.println("                context.activeSkills.add(SkillUtil.getSkill(Integer.parseInt(" + p1 + "), " + p2 + "));");
        out.println("            }");
        out.println("        }");
    }
}
