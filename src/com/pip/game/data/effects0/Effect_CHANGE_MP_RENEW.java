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
public class Effect_CHANGE_MP_RENEW extends Effect_FixValueAdd{

    public Effect_CHANGE_MP_RENEW(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "PropertyEnhancer";
    }


    public void genBuffEnhance(PrintWriter out, String p1, String p2){
        // Effect_FixValueAdd£ºÊý¶îint
        out.println("        pc.manarestore += multiple * " + p1 + ";");
    }
}