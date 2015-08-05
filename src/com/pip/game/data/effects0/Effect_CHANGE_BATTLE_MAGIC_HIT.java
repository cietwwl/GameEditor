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
public class Effect_CHANGE_BATTLE_MAGIC_HIT extends Effect_PercentAdd{

    public Effect_CHANGE_BATTLE_MAGIC_HIT(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genBuffPreHit(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_PercentAdd: °Ù·Ö±Èfloat
        out.println("        if (isActive && !(context.type instanceof PhysicalSkill)) {");
        out.println("            context.hitRate += " + p1 + " / 100.0f;");
        out.println("            effectTimes++;");
        out.println("        }");
    }
}