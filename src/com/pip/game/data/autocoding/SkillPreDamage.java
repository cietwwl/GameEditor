/**
 * 
 */
package com.pip.game.data.autocoding;

import java.io.PrintWriter;

import com.pip.game.data.effects.EffectAdapter;
import com.pip.game.data.skill.EffectConfig;
import com.pip.game.data.skill.SkillConfig;

/**
 * @author jhkang
 *
 */
public class SkillPreDamage {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public static void genCode(EffectConfig eff, PrintWriter out, int damageType, String p1, String p2) {
        switch(eff.getType()){

            case EffectConfig.CURE_TARGET:
                // Effect_FixValueAdd: ����int
                if (damageType != SkillConfig.DAMAGE_HEAL && damageType != SkillConfig.DAMAGE_ADDMP) {
                    throw new IllegalArgumentException("ֻ������/��������ʹ������Ŀ��Ч����");
                }
                out.println("        context.attackPower += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ");");
                break;
            case EffectConfig.CHANGE_PHYICAL_AP:
                // Effect_MultiAdd: ����int���ٷֱ�float
                if (damageType != SkillConfig.DAMAGE_PHYSICAL) {
                    throw new IllegalArgumentException("ֻ�������˺�����ʹ�øı���������Ч����");
                }
                out.println("        context.attackPower += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ");");
                out.println("        context.attackPowerRate += context.getSkillParam(this, \"" + p2 + "\", " + p2 + ") / 100.0f;");
                break;
            case EffectConfig.CHANGE_MAGIC_AP:
                // Effect_MultiAdd: ����int���ٷֱ�float
                if (damageType != SkillConfig.DAMAGE_MAGIC && damageType != SkillConfig.DAMAGE_DECMP) {
                    throw new IllegalArgumentException("ֻ�з����˺�/��������ʹ�øı䷨��������Ч����");
                }
                out.println("        context.attackPower += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ");");
                out.println("        context.attackPowerRate += context.getSkillParam(this, \"" + p2 + "\", " + p2 + ") / 100.0f;");
                break;
            case EffectConfig.CHANGE_WEAPON_ATK:
                // Effect_MultiAdd: ����int���ٷֱ�float
                if (damageType != SkillConfig.DAMAGE_PHYSICAL) {
                    throw new IllegalArgumentException("ֻ�������˺�����ʹ�øı�������������Ч����");
                }
                out.println("        if (context.source.equipments != null && context.source.equipments.getWeapon() != null) {");
                out.println("            GameItem weapon = context.source.equipments.getWeapon();");
                out.println("            int addDamage = CommonUtil.getCount(RND, weapon.getMinAttack(), weapon.getMaxAttack());");
                out.println("            context.attackPower += addDamage * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_WEAPON_MATK:
                // Effect_MultiAdd: ����int���ٷֱ�float
                if (damageType != SkillConfig.DAMAGE_MAGIC) {
                    throw new IllegalArgumentException("ֻ�з����˺�����ʹ�øı���������������Ч����");
                }
                out.println("        if (context.source.equipments != null && context.source.equipments.getWeapon() != null) {");
                out.println("            GameItem weapon = context.source.equipments.getWeapon();");
                out.println("            context.attackPower += weapon.getMagicPower() * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f;");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_THREAT:
                // Effect_MultiAdd: ����int���ٷֱ�float
                out.println("        context.threatAdd += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ");");
                out.println("        context.threatAddRate += context.getSkillParam(this, \"" + p2 + "\", " + p2 + ") / 100.0f;");
                break;
            case EffectConfig.IGNORE_ARMOR:
                // Effect_MultiAdd: ����int���ٷֱ�float
                out.println("        if (context.damageType == CombatContext.DAMAGE_PHYSICAL) {");
                out.println("            context.armor -= context.getSkillParam(this, \"" + p1 + "\", " + p1 + ");");
                out.println("            context.armorRate -= context.getSkillParam(this, \"" + p2 + "\", " + p2 + ") / 100.0f;");
                out.println("        }");
                break;
            case EffectConfig.IGNORE_MAGIC_ARMOR:
                // Effect_MultiAdd: ����int���ٷֱ�float
                out.println("        if (context.damageType == CombatContext.DAMAGE_MAGIC) {");
                out.println("            context.armor -= context.getSkillParam(this, \"" + p1 + "\", " + p1 + ");");
                out.println("            context.armorRate -= context.getSkillParam(this, \"" + p2 + "\", " + p2 + ") / 100.0f;");
                out.println("        }");
                break;
        }
    }

}
