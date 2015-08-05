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
public class BuffEnhance {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public static void genCode(EffectConfig eff, PrintWriter out, String p1, String p2) {
        switch(eff.getType()){

            case EffectConfig.CHANGE_PHYICAL_AP:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        pc.attackpowerup += multiple * " + p1 + ";");
                out.println("        pc.attackpowerdown += multiple * " + p1 + ";");
                out.println("        pc.attackpowerRate += multiple * " + p2 + " / 100.0f;");
                break;
            case EffectConfig.CHANGE_MAGIC_AP:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        pc.spellpowerRate += multiple * " + p2 + " / 100.0f;");
                out.println("        pc.spellpower += multiple * " + p1 + ";");
                break;
            case EffectConfig.CHANGE_WEAPON_ATK:
                // Effect_PercentAdd：百分比float
                out.println("        if (pc.unit.equipments.getWeapon() != null) {");
                out.println("            GameItem t = pc.unit.equipments.getWeapon();");
                out.println("            pc.attackpowerup += t.getMaxAttack() * multiple * " + p1 + " / 100.0f;");
                out.println("            pc.attackpowerdown += t.getMinAttack() * multiple * " + p1 + " / 100.0f;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_WEAPON_MATK:
                // Effect_PercentAdd：百分比float
                out.println("        if (pc.unit.equipments.getWeapon() != null) {");
                out.println("            GameItem t = pc.unit.equipments.getWeapon();");
                out.println("            pc.spellpower += t.getMagicPower() * multiple * " + p1 + " / 100.0f;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_ARMOR:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        pc.defenseRate += multiple * " + p2 + " / 100.0f;");
                out.println("        pc.defense += multiple * " + p1 + ";");
                out.println("        if (pc.defense < 0) {");
                out.println("            pc.defense = 0;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_PHYSICAL_HIT:
                // Effect_PercentAdd：百分比float
                out.println("        pc.hit += multiple * " + p1 + " / 100.0f;");
                break;
            case EffectConfig.CHANGE_PHYSICAL_CRIT:
                // Effect_PercentAdd：百分比float
                out.println("        pc.critical += multiple * " + p1 + " / 100.0f;");
                break;
            case EffectConfig.CHANGE_PHYSICAL_DODGE:
                // Effect_PercentAdd：百分比float
                out.println("        pc.dodge += multiple * " + p1 + " / 100.0f;");
                break;
            case EffectConfig.CHANGE_MAGIC_CRIT:
                // Effect_PercentAdd：百分比float
                out.println("        pc.spellcritical += multiple * " + p1 + " / 100.0f;");
                break;
            case EffectConfig.CHANGE_MAGIC_HIT:
                // Effect_PercentAdd：百分比float
                out.println("        pc.spellhit += multiple * " + p1 + " / 100.0f;");
                break;
            case EffectConfig.CHANGE_MAGIC_DODGE:
                // Effect_PercentAdd：百分比float
                out.println("        pc.spelldodge += multiple * " + p1 + " / 100.0f;");
                break;
            case EffectConfig.CHANGE_PHYSICAL_HIT_RATE:
                // Effect_FixValueAdd：数额int
                out.println("        pc.hitrating += multiple * " + p1 + ";");
                break;
            case EffectConfig.CHANGE_PHYSICAL_CRIT_RATE:
                // Effect_FixValueAdd：数额int
                out.println("        pc.criticalrating += multiple * " + p1 + ";");
                break;
            case EffectConfig.CHANGE_PHYSICAL_DODGE_RATE:
                // Effect_FixValueAdd：数额int
                out.println("        pc.dodgerating += multiple * " + p1 + ";");
                break;
            case EffectConfig.CHANGE_MAGIC_CRIT_RATE:
                // Effect_FixValueAdd：数额int
                out.println("        pc.spellcriticalrating += multiple * " + p1 + ";");
                break;
            case EffectConfig.CHANGE_MAGIC_HIT_RATE:
                // Effect_FixValueAdd：数额int
                out.println("        pc.spellhitrating += multiple * " + p1 + ";");
                break;
            case EffectConfig.CHANGE_MAGIC_DODGE_RATE:
                // Effect_FixValueAdd：数额int
                out.println("        pc.spelldodgerating += multiple * " + p1 + ";");
                break;
            case EffectConfig.CHANGE_MP_RENEW:
                // Effect_FixValueAdd：数额int
                out.println("        pc.manarestore += multiple * " + p1 + ";");
                break;
            case EffectConfig.CHANGE_HP_RENEW:
                // Effect_FixValueAdd：数额int
                out.println("        pc.healthrestore += multiple * " + p1 + ";");
                break;
            case EffectConfig.CHANGE_SPEED:
                // Effect_PercentAdd：百分比float
                out.println("        if (" + p1 + " > 0.0f) {");
                out.println("            pc.fast(multiple * " + p1 + " / 100.0f);");
                out.println("        } else {");
                out.println("            pc.slow(multiple * " + p1 + " / -100.0f);");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_MAXHP:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        pc.hp += multiple * " + p1 + ";");
                out.println("        pc.hpRate += multiple * " + p2 + " / 100.0f;");
                break;
            case EffectConfig.CHANGE_MAGIC_ARMOR:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        pc.spellDefenseRate += multiple * " + p2 + " / 100.0f;");
                out.println("        pc.spelldefense += multiple * " + p1 + ";");
                out.println("        if (pc.spelldefense < 0) {");
                out.println("            pc.spelldefense = 0;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_STA:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        pc.stamina *= 1.0f + multiple * " + p2 + " / 100.0f;");
                out.println("        pc.stamina += multiple * " + p1 + ";");
                break;
            case EffectConfig.CHANGE_AGI:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        pc.agility *= 1.0f + multiple * " + p2 + " / 100.0f;");
                out.println("        pc.agility += multiple * " + p1 + ";");
                break;
            case EffectConfig.CHANGE_STR:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        pc.strength *= 1.0f + multiple * " + p2 + " / 100.0f;");
                out.println("        pc.strength += multiple * " + p1 + ";");
                break;
            case EffectConfig.CHANGE_INT:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        pc.intellect *= 1.0f + multiple * " + p2 + " / 100.0f;");
                out.println("        pc.intellect += multiple * " + p1 + ";");
                break;
            case EffectConfig.CHANGE_BASIC_MAGIC_AP:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        pc.spellpower += multiple * " + p1 + ";");
                out.println("        pc.basicSpellPowerRate += multiple * " + p2 + " / 100.0f;");
                break;
            case EffectConfig.CHANGE_BASIC_HP:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        pc.hp += multiple * " + p1 + ";");
                out.println("        pc.basicHpRate += multiple * " + p2 + " / 100.0f;");
                break;
            case EffectConfig.CHANGE_BASIC_MP:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        pc.mp += multiple * " + p1 + ";");
                out.println("        pc.basicMpRate += multiple * " + p2 + " / 100.0f;");
                break;
            case EffectConfig.CHANGE_MAGIC_HEAL:
                // Effect_MultiAdd: 数额int，百分比float
                out.println("        pc.spellheal += multiple * " + p1 + ";");
                out.println("        pc.spellhealRate += multiple * " + p2 + " / 100.0f;");
                break;
            case EffectConfig.CHANGE_EXP_RATE:
                // Effect_PercentAdd: 百分比float
                out.println("        pc.expRatio += multiple * " + p1 + " / 100.0f;");
                break;
            case EffectConfig.CHANGE_HORSE_EXP_RATE:
                // Effect_PercentAdd: 百分比float
                out.println("        pc.horseExpRatio += multiple * " + p1 + " / 100.0f;");
                break;
            case EffectConfig.CHANGE_MONEY_RATE:
                // Effect_PercentAdd: 百分比float
                out.println("        pc.moneyRatio += multiple * " + p1 + " / 100.0f;");
                break;
        }
    }

}
