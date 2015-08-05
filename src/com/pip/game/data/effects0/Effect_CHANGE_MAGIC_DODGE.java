/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_PercentAdd;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_CHANGE_MAGIC_DODGE extends Effect_PercentAdd{

    public Effect_CHANGE_MAGIC_DODGE(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "PropertyEnhancer";
    }


    public void genBuffEnhance(PrintWriter out, String p1, String p2){
        // Effect_PercentAdd：百分比float
        out.println("        pc.spelldodge += multiple * " + p1 + " / 100.0f;");
    }

    public void genSkillPreHit(PrintWriter out, int damageType, String p1){
        // Effect_PercentAdd: 百分比float
        if (damageType != SkillConfig.DAMAGE_PHYSICAL) {
        out.println("        context.dodge += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f;");
        } else {
        throw new IllegalArgumentException("物理伤害不允许使用改变法术闪避率效果。");
        }
    }
}
