/**
 * 
 */
package com.pip.game.data.autocoding;

import java.io.PrintWriter;

import com.pip.game.data.skill.EffectConfig;

/**
 * @author jhkang
 *
 */
public class BuffPostHit {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public static void genBuffPostHit(EffectConfig eff, PrintWriter out, String p1, String p2) {
        switch(eff.getType()){

            case EffectConfig.IMMUNE_PHYICAL_ATTACK:
                // Effect_PercentAdd: 百分比float
                out.println("        if (!isActive && context.hited() && context.damageType == CombatContext.DAMAGE_PHYSICAL) {");
                out.println("            int rate = (int)(multiple * 100 * " + p1 + ");");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                context.attackResult = CombatContext.ATTACKRESULT_IMMUNE;");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.IMMUNE_MAGIC_ATTACK:
                // Effect_PercentAdd: 百分比float
                out.println("        if (!isActive && context.hited() && context.damageType == CombatContext.DAMAGE_MAGIC) {");
                out.println("            int rate = (int)(multiple * 100 * " + p1 + ");");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                context.attackResult = CombatContext.ATTACKRESULT_IMMUNE;");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.IMMUNE_SLOW_ATTACK:
                // Effect_PercentAdd: 百分比float
                out.println("        if (!isActive && context.hited() && context.skill instanceof SlowSkill) {");
                out.println("            int rate = (int)(multiple * 100 * " + p1 + ");");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                context.attackResult = CombatContext.ATTACKRESULT_IMMUNE;");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.IMMUNE_FEAR:
                // Effect_PercentAdd: 百分比float
                out.println("        if (!isActive && context.hited() && context.skill instanceof FearSkill) {");
                out.println("            int rate = (int)(multiple * 100 * " + p1 + ");");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                context.attackResult = CombatContext.ATTACKRESULT_IMMUNE;");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.IMMUNE_DUMB:
                // Effect_PercentAdd: 百分比float
                out.println("        if (!isActive && context.hited() && context.skill instanceof DumbSkill) {");
                out.println("            int rate = (int)(multiple * 100 * " + p1 + ");");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                context.attackResult = CombatContext.ATTACKRESULT_IMMUNE;");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.IMMUNE_PARALYZE:
                // Effect_PercentAdd: 百分比float
                out.println("        if (!isActive && context.hited() && context.skill instanceof ParalyzeSkill) {");
                out.println("            int rate = (int)(multiple * 100 * " + p1 + ");");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                context.attackResult = CombatContext.ATTACKRESULT_IMMUNE;");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.IMMUNE_STAY:
                // Effect_PercentAdd: 百分比float
                out.println("        if (!isActive && context.hited() && context.skill instanceof StaySkill) {");
                out.println("            int rate = (int)(multiple * 100 * " + p1 + ");");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                context.attackResult = CombatContext.ATTACKRESULT_IMMUNE;");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.IMMUNE_BREAKATTACK:
                // Effect_PercentAdd: 百分比float
                out.println("        if (!isActive && context.hited() && context.skill instanceof BreakAttackSkill) {");
                out.println("            int rate = (int)(multiple * 100 * " + p1 + ");");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                context.attackResult = CombatContext.ATTACKRESULT_IMMUNE;");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            
        }
    }

}
