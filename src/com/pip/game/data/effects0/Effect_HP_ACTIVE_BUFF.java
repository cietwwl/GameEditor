/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_HPActiveBuff;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_HP_ACTIVE_BUFF extends Effect_HPActiveBuff{

    public Effect_HP_ACTIVE_BUFF(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "Updatable";
    }
    
    public void genUpdate(PrintWriter out){
        out.println("        Combatable targetUnit = (Combatable)ObjectAccessor.getGameObject(owner);");
        out.println("        if (targetUnit == null) {");
        out.println("            return true;");
        out.println("        }");
        out.println("        if (hpBuffActive && targetUnit.hp >= targetUnit.maxhp * hp_buff_rate / 100.0f) {");
        out.println("            targetUnit.getBuffs().removeBuff(hp_buff_id);");
        out.println("        } else if (!hpBuffActive && targetUnit.hp < targetUnit.maxhp * hp_buff_rate / 100.0f) {");
        out.println("            targetUnit.getBuffs().addBuff(BuffUtil.createBuff(hp_buff_id, hp_buff_level, targetUnit, targetUnit, 0));");
        out.println("        }");
    }
    
    public void genCustomerFields(PrintWriter out){
        // 如果有血量激活BUFF的效果，需要记录是否已经加过BUFF了
        out.println("    boolean hpBuffActive;");
    }
}
