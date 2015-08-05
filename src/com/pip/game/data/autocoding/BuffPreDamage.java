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
public class BuffPreDamage {

    public static void genBuffPreDamage(EffectConfig eff, PrintWriter out, String p1, String p2) {
        switch(eff.getType()){

            case EffectConfig.CHANGE_BATTLE_PHYICAL_AP:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        if (isActive && context.damageType == CombatContext.DAMAGE_PHYSICAL) {");
                out.println("            context.attackPower += " + p1 + ";");
                out.println("            context.attackPowerRate += " + p2 + " / 100.0f;");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_BATTLE_MAGIC_AP:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        if (isActive && (context.damageType == CombatContext.DAMAGE_MAGIC || context.damageType == CombatContext.DAMAGE_DECMP)) {");
                out.println("            context.attackPower += " + p1 + ";");
                out.println("            context.attackPowerRate += " + p2 + " / 100.0f;");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_BATTLE_WEAPON_ATK:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        if (isActive && context.damageType == CombatContext.DAMAGE_PHYSICAL && context.source.equipments != null && context.source.equipments.getWeapon() != null) {");
                out.println("            GameItem weapon = context.source.equipments.getWeapon();");
                out.println("            int addDamage = CommonUtil.getCount(RND, weapon.getMinAttack(), weapon.getMaxAttack());");
                out.println("            context.attackPower += addDamage * " + p1 + " / 100.0f;");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_BATTLE_WEAPON_MATK:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        if (isActive && context.damageType == CombatContext.DAMAGE_MAGIC && context.source.equipments != null && context.source.equipments.getWeapon() != null) {");
                out.println("            GameItem weapon = context.source.equipments.getWeapon();");
                out.println("            context.attackPower += weapon.getMagicPower() * " + p1 + " / 100.0f;");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_THREAT:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        if (isActive) {");
                out.println("            context.threatAdd += multiple * " + p1 + ";");
                out.println("            context.threatAddRate += multiple * " + p2 + " / 100.0f;");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_CURE_EFFECT:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        if (isActive && context.damageType == CombatContext.DAMAGE_HEAL) {");
                out.println("            context.attackPower += multiple * " + p1 + ";");
                out.println("            context.attackPowerRate += multiple * " + p2 + " / 100.0f;");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.IGNORE_ARMOR:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        if (isActive && context.damageType == CombatContext.DAMAGE_PHYSICAL) {");
                out.println("            context.armor -= multiple * " + p1 + ";");
                out.println("            context.armorRate -= multiple * " + p2 + " / 100.0f;");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.IGNORE_MAGIC_ARMOR:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        if (isActive && context.damageType == CombatContext.DAMAGE_MAGIC) {");
                out.println("            context.armor -= multiple * " + p1 + ";");
                out.println("            context.armorRate -= multiple * " + p2 + " / 100.0f;");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_BATTLE_ARMOR:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        if (!isActive && context.damageType == CombatContext.DAMAGE_PHYSICAL) {");
                out.println("            context.armor += multiple * " + p1 + ";");
                out.println("            context.armorRate += multiple * " + p2 + " / 100.0f;");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_BATTLE_MAGIC_ARMOR:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        if (!isActive && context.damageType == CombatContext.DAMAGE_MAGIC) {");
                out.println("            context.armor += multiple * " + p1 + ";");
                out.println("            context.armorRate += multiple * " + p2 + " / 100.0f;");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            
        }
    }

}
