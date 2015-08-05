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
                // Effect_PercentAdd: 百分比float
                if (damageType == SkillConfig.DAMAGE_PHYSICAL) {
                    out.println("        context.hitRate += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f;");
                } else {
                    throw new IllegalArgumentException("只有物理伤害允许使用改变物理命中率效果。");
                }
                break;
            case EffectConfig.CHANGE_PHYSICAL_CRIT:
                // Effect_PercentAdd: 百分比float
                if (damageType == SkillConfig.DAMAGE_PHYSICAL) {
                    out.println("        context.critRate += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f;");
                } else {
                    throw new IllegalArgumentException("只有物理伤害允许使用改变物理暴击率效果。");
                }
                break;
            case EffectConfig.CHANGE_PHYSICAL_DODGE:
                // Effect_PercentAdd: 百分比float
                if (damageType == SkillConfig.DAMAGE_PHYSICAL) {
                    out.println("        context.dodge += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f;");
                } else {
                    throw new IllegalArgumentException("只有物理伤害允许使用改变物理闪避率效果。");
                }
                break;
            case EffectConfig.CHANGE_MAGIC_CRIT:
                // Effect_PercentAdd: 百分比float
                if (damageType != SkillConfig.DAMAGE_PHYSICAL) {
                    out.println("        context.critRate += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f;");
                } else {
                    throw new IllegalArgumentException("物理伤害不允许使用改变法术暴击率效果。");
                }
                break;
            case EffectConfig.CHANGE_MAGIC_HIT:
                // Effect_PercentAdd: 百分比float
                if (damageType != SkillConfig.DAMAGE_PHYSICAL) {
                    out.println("        context.hitRate += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f;");
                } else {
                    throw new IllegalArgumentException("物理伤害不允许使用改变法术命中率效果。");
                }
                break;
            case EffectConfig.CHANGE_MAGIC_DODGE:
                // Effect_PercentAdd: 百分比float
                if (damageType != SkillConfig.DAMAGE_PHYSICAL) {
                    out.println("        context.dodge += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f;");
                } else {
                    throw new IllegalArgumentException("物理伤害不允许使用改变法术闪避率效果。");
                }
                break;
            case EffectConfig.TWO_HIT_ON_HIT:
                // Effect_Hit3Times: 无参数
                out.println("        context.activeSkills.add(context.skill);");
                out.println("        context.activeSkills.add(context.skill);");
                break;
        }        
    }

}
