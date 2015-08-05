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
public class Effect_CURE_TARGET extends Effect_FixValueAdd{

    public Effect_CURE_TARGET(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genSkillPreDamage(PrintWriter out,  int damageType, String p1, String p2){
        // Effect_FixValueAdd: ����int
        if (damageType != SkillConfig.DAMAGE_HEAL && damageType != SkillConfig.DAMAGE_ADDMP) {
        throw new IllegalArgumentException("ֻ������/��������ʹ������Ŀ��Ч����");
        }
        out.println("        context.attackPower += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ");");
    }
}
