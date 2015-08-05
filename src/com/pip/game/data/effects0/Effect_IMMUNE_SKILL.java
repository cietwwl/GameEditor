package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_SelectSkill;

/**
 * √‚“ﬂººƒ‹
 * 
 * @author yqwang
 */
public class Effect_IMMUNE_SKILL extends Effect_SelectSkill {
    public Effect_IMMUNE_SKILL(int t) {
        super(t);
    }

    public void genBuffFinished(PrintWriter out, String p1, String p2, String p3, String p4, String p5, String p6) {
        out.println("        if (context.hited() && context.skill.getId() == SkillUtil.getSkillId(Integer.parseInt(" + p1 + "), " + p2 + ")) {");
        out.println("            boolean hit = CommonUtil.hit(RND, (int)(multiple * 100 * " + p3 + "), 10000);");
        out.println("            if (hit) {");
        out.println("                context.attackResult = CombatContext.ATTACKRESULT_IMMUNE;");
        out.println("               SkillResultSequence srs = new SkillResultSequence(context.target,context.skill,context.attackResult);");
        out.println("               context.target.getBattle().addSequence(srs);");
        out.println("            }");
        out.println("        }");
    }

    public void genSkillFinished(PrintWriter out, String p1, String p2, String p3, String p4, String p5, String p6) {
        out.println("        if (context.hited() && context.skill.getId() == SkillUtil.getSkillId(Integer.parseInt(" + p1 + "), " + p2 + ")) {");
        out.println("            boolean hit = CommonUtil.hit(RND, (int)(100 * context.getSkillParam(this, \"" + p3
                + "\", " + p3 + ")), 10000);");
        out.println("            if (hit) {");
        out.println("                context.attackResult = CombatContext.ATTACKRESULT_IMMUNE;");
        out.println("               SkillResultSequence srs = new SkillResultSequence(context.target,context.skill,context.attackResult);");
        out.println("               context.target.getBattle().addSequence(srs);");
        out.println("            }");
        out.println("        }");
    }
    
    @Override
    public String getJavaInterface() throws Exception {
        return "-1";
    }
}
