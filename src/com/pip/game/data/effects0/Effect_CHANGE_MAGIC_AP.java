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
public class Effect_CHANGE_MAGIC_AP extends Effect_MultiAdd{

    public Effect_CHANGE_MAGIC_AP(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "PropertyEnhancer";
    }


    public void genBuffEnhance(PrintWriter out, String p1, String p2){
        // Effect_MultiAdd: 数额int，百分比float
        out.println("        pc.spellpowerRate += multiple * " + p2 + " / 100.0f;");
        out.println("        pc.spellpower += multiple * " + p1 + ";");
    }

    public void genSkillPreDamage(PrintWriter out,  int damageType, String p1, String p2) throws EffectRejectException{
        // Effect_MultiAdd: 数额int，百分比float
        if (damageType != SkillConfig.DAMAGE_MAGIC && damageType != SkillConfig.DAMAGE_DECMP) {
            throw new EffectRejectException("只有法术伤害/抽蓝允许使用改变法术攻击力效果。");
        }
        out.println("        context.attackPower += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ");");
        out.println("        context.attackPowerRate += context.getSkillParam(this, \"" + p2 + "\", " + p2 + ") / 100.0f;");
    }
}
