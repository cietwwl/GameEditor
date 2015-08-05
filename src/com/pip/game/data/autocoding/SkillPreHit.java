/**
 * 
 */
package com.pip.game.data.autocoding;

import java.io.PrintWriter;

import com.pip.game.data.skill.EffectConfig;
import com.pip.game.data.skill.SkillConfig;

/**
 * @author jhkang
 *
 */
public class SkillPreHit {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public static void genProcCode(EffectConfig eff, PrintWriter out, int damageType, String p1) {
        switch (eff.getType()) {
            case EffectConfig.CHANGE_PHYSICAL_HIT:
                // Effect_PercentAdd: �ٷֱ�float
                if (damageType == SkillConfig.DAMAGE_PHYSICAL) {
                    out.println("        context.hitRate += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f;");
                } else {
                    throw new IllegalArgumentException("ֻ�������˺�����ʹ�øı�����������Ч����");
                }
                break;
            case EffectConfig.CHANGE_PHYSICAL_CRIT:
                // Effect_PercentAdd: �ٷֱ�float
                if (damageType == SkillConfig.DAMAGE_PHYSICAL) {
                    out.println("        context.critRate += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f;");
                } else {
                    throw new IllegalArgumentException("ֻ�������˺�����ʹ�øı���������Ч����");
                }
                break;
            case EffectConfig.CHANGE_PHYSICAL_DODGE:
                // Effect_PercentAdd: �ٷֱ�float
                if (damageType == SkillConfig.DAMAGE_PHYSICAL) {
                    out.println("        context.dodge += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f;");
                } else {
                    throw new IllegalArgumentException("ֻ�������˺�����ʹ�øı�����������Ч����");
                }
                break;
            case EffectConfig.CHANGE_MAGIC_CRIT:
                // Effect_PercentAdd: �ٷֱ�float
                if (damageType != SkillConfig.DAMAGE_PHYSICAL) {
                    out.println("        context.critRate += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f;");
                } else {
                    throw new IllegalArgumentException("�����˺�������ʹ�øı䷨��������Ч����");
                }
                break;
            case EffectConfig.CHANGE_MAGIC_HIT:
                // Effect_PercentAdd: �ٷֱ�float
                if (damageType != SkillConfig.DAMAGE_PHYSICAL) {
                    out.println("        context.hitRate += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f;");
                } else {
                    throw new IllegalArgumentException("�����˺�������ʹ�øı䷨��������Ч����");
                }
                break;
            case EffectConfig.CHANGE_MAGIC_DODGE:
                // Effect_PercentAdd: �ٷֱ�float
                if (damageType != SkillConfig.DAMAGE_PHYSICAL) {
                    out.println("        context.dodge += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f;");
                } else {
                    throw new IllegalArgumentException("�����˺�������ʹ�øı䷨��������Ч����");
                }
                break;
            case EffectConfig.TWO_HIT_ON_HIT:
                // Effect_Hit3Times: �޲���
                out.println("        context.activeSkills.add(context.skill);");
                out.println("        context.activeSkills.add(context.skill);");
                break;
        }        
    }

}
