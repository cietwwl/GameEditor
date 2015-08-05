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
public class Effect_IGNORE_ARMOR extends Effect_MultiAdd{

    public Effect_IGNORE_ARMOR(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genSkillPreDamage(PrintWriter out,  int damageType, String p1, String p2){
        // Effect_MultiAdd: 数额int，百分比float
        out.println("        if (context.type instanceof PhysicalSkill) {");
        out.println("            context.armor -= context.getSkillParam(this, \"" + p1 + "\", " + p1 + ");");
        out.println("            context.armorRate -= context.getSkillParam(this, \"" + p2 + "\", " + p2 + ") / 100.0f;");
        out.println("        }");
    }
}