/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_MultiAdd;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_CHANGE_PHYICAL_AP extends Effect_MultiAdd{

    public Effect_CHANGE_PHYICAL_AP(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "PropertyEnhancer";
    }


    public void genBuffEnhance(PrintWriter out, String p1, String p2){
        // Effect_MultiAdd: 数额int，百分比float
        out.println("        pc.attackpowerup += multiple * " + p1 + ";");
        out.println("        pc.attackpowerdown += multiple * " + p1 + ";");
        out.println("        pc.attackpowerRate += multiple * " + p2 + " / 100.0f;");
    }

    public void genSkillPreDamage(PrintWriter out,  int damageType, String p1, String p2){
        // Effect_MultiAdd: 数额int，百分比float
        if (damageType != SkillConfig.DAMAGE_PHYSICAL) {
        throw new IllegalArgumentException("只有物理伤害允许使用改变物理攻击力效果。");
        }
        out.println("        context.attackPower += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ");");
        out.println("        context.attackPowerRate += context.getSkillParam(this, \"" + p2 + "\", " + p2 + ") / 100.0f;");
    }
}