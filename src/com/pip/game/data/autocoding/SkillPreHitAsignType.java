/**
 * 
 */
package com.pip.game.data.autocoding;

import java.io.PrintWriter;

import com.pip.game.data.skill.SkillConfig;

/**
 * @author jhkang
 *
 */
public class SkillPreHitAsignType {
    public static void genDamageTypeAsignCode(int damageType, PrintWriter out) {
        switch (damageType) {
            case SkillConfig.DAMAGE_PHYSICAL:
                out.println("        context.damageType = CombatContext.DAMAGE_PHYSICAL;");
                break;
            case SkillConfig.DAMAGE_MAGIC:
                out.println("        context.damageType = CombatContext.DAMAGE_MAGIC;");
                break;
            case SkillConfig.DAMAGE_DECMP:
                out.println("        context.damageType = CombatContext.DAMAGE_DECMP;");
                break;
            case SkillConfig.DAMAGE_DEBUFF:
                out.println("        context.damageType = CombatContext.DAMAGE_DEBUFF;");
                break;
            case SkillConfig.DAMAGE_HEAL:
                out.println("        context.damageType = CombatContext.DAMAGE_HEAL;");
                break;
            case SkillConfig.DAMAGE_ADDMP:
                out.println("        context.damageType = CombatContext.DAMAGE_ADDMP;");
                break;
            case SkillConfig.DAMAGE_BUFF:
                out.println("        context.damageType = CombatContext.DAMAGE_BUFF;");
                break;
            }        
    }
}
