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
public class Effect_CHANGE_ARMOR extends Effect_MultiAdd{

    public Effect_CHANGE_ARMOR(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "PropertyEnhancer";
    }


    public void genBuffEnhance(PrintWriter out, String p1, String p2){
        // Effect_MultiAdd: ����int���ٷֱ�float
        out.println("        pc.defenseRate += multiple * " + p2 + " / 100.0f;");
        out.println("        pc.defense += multiple * " + p1 + ";");
        out.println("        if (pc.defense < 0) {");
        out.println("            pc.defense = 0;");
        out.println("        }");
    }
}
