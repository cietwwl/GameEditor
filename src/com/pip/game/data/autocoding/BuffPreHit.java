/**
 * 
 */
package com.pip.game.data.autocoding;

import java.io.File;
import java.io.PrintWriter;

import com.pip.game.data.skill.EffectConfig;
import com.pip.util.Utils;

/**
 * @author jhkang
 *
 */
public class BuffPreHit {
    
    public static void main(String args[]) throws Exception{
    }

    public static void genBuffPreHit(EffectConfig eff, PrintWriter out, String p1, String p2, String p3, String p4,
            String p5, String p6) {
        switch(eff.getType()){

            case EffectConfig.CHANGE_BATTLE_PHYSICAL_HIT:
                // Effect_PercentAdd: 百分比float
                out.println("        if (isActive && context.damageType == CombatContext.DAMAGE_PHYSICAL) {");
                out.println("            context.hitRate += " + p1 + " / 100.0f;");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_BATTLE_PHYSICAL_CRIT:
                // Effect_PercentAdd: 百分比float
                out.println("        if (isActive && context.damageType == CombatContext.DAMAGE_PHYSICAL) {");
                out.println("            context.critRate += " + p1 + " / 100.0f;");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_BATTLE_PHYSICAL_DODGE:
                // Effect_PercentAdd: 百分比float
                out.println("        if (!isActive && context.damageType == CombatContext.DAMAGE_PHYSICAL) {");
                out.println("            context.dodge += " + p1 + " / 100.0f;");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_BATTLE_PHYSICAL_CRITED:
                // Effect_PercentAdd: 百分比float
                out.println("        if (!isActive && context.damageType == CombatContext.DAMAGE_PHYSICAL) {");
                out.println("            context.critRate += " + p1 + " / 100.0f;");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_BATTLE_MAGIC_CRIT:
                // Effect_PercentAdd: 百分比float
                out.println("        if (isActive && context.damageType != CombatContext.DAMAGE_PHYSICAL) {");
                out.println("            context.critRate += " + p1 + " / 100.0f;");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_BATTLE_MAGIC_HIT:
                // Effect_PercentAdd: 百分比float
                out.println("        if (isActive && context.damageType != CombatContext.DAMAGE_PHYSICAL) {");
                out.println("            context.hitRate += " + p1 + " / 100.0f;");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_BATTLE_MAGIC_DODGE:
                // Effect_PercentAdd: 百分比float
                out.println("        if (!isActive && context.damageType != CombatContext.DAMAGE_PHYSICAL) {");
                out.println("            context.dodge += " + p1 + " / 100.0f;");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_BATTLE_MAGIC_CRITED:
                // Effect_PercentAdd: 百分比float
                out.println("        if (!isActive && context.damageType != CombatContext.DAMAGE_PHYSICAL) {");
                out.println("            context.critRate += " + p1 + " / 100.0f;");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.SET_VARIABLE:
                // Effect_SetVariable：变量名string，变量值float(重复3次)
                out.println("        if (isActive) {");
                out.println("            if (" + p1 + ".length() > 0) {");
                out.println("                context.skillParams.put(" + p1 + ", new Float(" + p2 + "));");
                out.println("            }");
                out.println("            if (" + p3 + ".length() > 0) {");
                out.println("                context.skillParams.put(" + p3 + ", new Float(" + p4 + "));");
                out.println("            }");
                out.println("            if (" + p5 + ".length() > 0) {");
                out.println("                context.skillParams.put(" + p5 + ", new Float(" + p6 + "));");
                out.println("            }");
                out.println("        }");
                break;
            
        }
    }

}
