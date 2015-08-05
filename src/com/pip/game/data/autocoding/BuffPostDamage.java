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
public class BuffPostDamage {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public static void genBuffPostDamage(EffectConfig eff, PrintWriter out, String p1, String p2, String p3) {
        switch(eff.getType()){

            case EffectConfig.APPEND_MAGIC_DAMAGE:
                // Effect_FixValueAdd: 数额int
                out.println("        if (isActive && context.isDamage()) {");
                out.println("            context.appendSpellDamage(multiple * " + p1 + ");");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.DOUBLE_DAMAGE_ON_HIT:
                // Effect_PercentAdd: 百分比float
                out.println("        if (isActive && context.isDamage()) {");
                out.println("            int rate = (int)(multiple * 100 * " + p1 + ");");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                context.damage *= 2;");
                out.println("                context.threat *= 2;");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.MP_SHIELD:
                // Effect_Shield: 总量int，抵消比例float，消耗率float
                out.println("        if (!isActive && context.isDamage()) {");
                out.println("            int absorb = (int)(context.damage * " + p2 + " / 100.0f);");
                out.println("            if (absorb > remainAbsorb) {");
                out.println("                absorb = remainAbsorb;");
                out.println("            }");
                out.println("            int needMp = (int)(absorb * " + p3 + " / 100.0f);");
                out.println("            if (needMp > context.target.mp) {");
                out.println("                needMp = context.target.mp;");
                out.println("                absorb = (int)(needMp * 100.0f / " + p3 + ");");
                out.println("            }");
                out.println("            if (absorb > 0) {");
                out.println("                context.damage -= absorb;");
                out.println("                remainAbsorb -= absorb;");
                out.println("                context.target.setMp(context.target.mp - needMp, true);");
                out.println("            }");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.REDUCE_PHYSICAL_DAMAGE:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        if (!isActive && context.damageType == CombatContext.DAMAGE_PHYSICAL) {");
                out.println("            int value = multiple * " + p1 + ";");
                out.println("            value += context.damage * multiple * " + p2 + " / 100.0f;");
                out.println("            if (value > context.damageValue) {");
                out.println("                value = context.damage;");
                out.println("            }");
                out.println("            if (context.damage > 0) {");
                out.println("                context.threat -= value * context.threat / context.damage;");
                out.println("                context.damage -= value;");
                out.println("            }");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.REDUCE_MAGIC_DAMAGE:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        if (!isActive && context.damageType == CombatContext.DAMAGE_MAGIC) {");
                out.println("            int value = multiple * " + p1 + ";");
                out.println("            value += context.damage * multiple * " + p2 + " / 100.0f;");
                out.println("            if (value > context.damageValue) {");
                out.println("                value = context.damage;");
                out.println("            }");
                out.println("            if (context.damage > 0) {");
                out.println("                context.threat -= value * context.threat / context.damage;");
                out.println("                context.damage -= value;");
                out.println("            }");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            
        }
    }

}
