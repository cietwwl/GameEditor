/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_PercentAdd;

/**
 * @author jhkang
 */
public class Effect_IMMUNE_BREAKATTACK extends Effect_PercentAdd{

    public Effect_IMMUNE_BREAKATTACK(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genBuffPostHit(PrintWriter out, String p1, String p2){
        // Effect_PercentAdd: °Ù·Ö±Èfloat
        out.println("        if (!isActive && context.hited() && context.skill instanceof BreakAttackSkill) {");
        out.println("            int rate = (int)(multiple * 100 * " + p1 + ");");
        out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
        out.println("                context.attackResult = CombatContext.ATTACKRESULT_IMMUNE;");
        out.println("               ActiveSkillSequence ass = new ActiveSkillSequence(context.sequence,context.source,context.target,context.skill);");
        out.println("               context.target.getBattle().addSequence(ass);");
        out.println("                effectTimes++;");
        out.println("            }");
        out.println("        }");
    }
}