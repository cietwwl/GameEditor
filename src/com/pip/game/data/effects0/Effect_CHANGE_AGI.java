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
public class Effect_CHANGE_AGI extends Effect_MultiAdd{

    public Effect_CHANGE_AGI(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "PropertyEnhancer";
    }


    public void genBuffEnhance(PrintWriter out, String p1, String p2){
        // Effect_MultiAdd: 数额int，百分比float
        out.println("        pc.agility *= 1.0f + multiple * " + p2 + " / 100.0f;");
        out.println("        pc.agility += multiple * " + p1 + ";");
    }
}
