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
public class Effect_CHANGE_BASIC_MAGIC_AP extends Effect_MultiAdd{

    public Effect_CHANGE_BASIC_MAGIC_AP(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "PropertyEnhancer";
    }


    public void genBuffEnhance(PrintWriter out, String p1, String p2){
        // Effect_MultiAdd: ����int���ٷֱ�float
        out.println("        pc.spellpower += multiple * " + p1 + ";");
        out.println("        pc.basicSpellPowerRate += multiple * " + p2 + " / 100.0f;");
    }
}