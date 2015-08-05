/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_FixValueAdd;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_APPEND_MAGIC_DAMAGE extends Effect_FixValueAdd{

    public Effect_APPEND_MAGIC_DAMAGE(int t) {
        super(t);
    }

    public String getJavaInterface(){
        return "CombatEffect";
    }

    public void genBuffPostDamage(PrintWriter out, String p1, String p2, String p3){
        // Effect_FixValueAdd: 数额int
        out.println("        if (isActive && context.isDamage()) {");
        out.println("            context.appendSpellDamage(multiple * " + p1 + ");");
        out.println("            effectTimes++;");
        out.println("        }");
    }

    public void genBuffPreDamage(PrintWriter out, String p1, String p2, String p3){
        // Effect_FixValueAdd: 数额int
        out.println("        if (isActive && context.isDamage()) {");
        out.println("            context.appendSpellDamage(multiple * " + p1 + ");");
        out.println("            effectTimes++;");
        out.println("        }");
    }

    public void genSkillPostDamage(int damageType, PrintWriter out, String p1){
        // Effect_FixValueAdd: 数额int
        if (damageType != SkillConfig.DAMAGE_PHYSICAL) {
            throw new IllegalArgumentException("只有物理伤害允许使用此效果。");
            }
        out.println("        context.appendSpellDamage(context.getSkillParam(this, \"" + p1 + "\", " + p1 + "));");
    }
}