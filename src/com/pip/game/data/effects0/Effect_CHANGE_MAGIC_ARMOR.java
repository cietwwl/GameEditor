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
public class Effect_CHANGE_MAGIC_ARMOR extends Effect_MultiAdd{

    public Effect_CHANGE_MAGIC_ARMOR(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "PropertyEnhancer";
    }


    public void genBuffEnhance(PrintWriter out, String p1, String p2){
        // Effect_MultiAdd: 数额int，百分比float
        out.println("        pc.spellDefenseRate += multiple * " + p2 + " / 100.0f;");
        out.println("        pc.spelldefense += multiple * " + p1 + ";");
        out.println("        if (pc.spelldefense < 0) {");
        out.println("            pc.spelldefense = 0;");
        out.println("        }");
    }
}
