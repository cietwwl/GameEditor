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
public class Effect_CHANGE_PHYSICAL_HIT_RATE extends Effect_FixValueAdd{

    public Effect_CHANGE_PHYSICAL_HIT_RATE(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "PropertyEnhancer";
    }


    public void genBuffEnhance(PrintWriter out, String p1, String p2){
        // Effect_FixValueAdd£ºÊý¶îint
        out.println("        pc.hitrating += multiple * " + p1 + ";");
    }
}