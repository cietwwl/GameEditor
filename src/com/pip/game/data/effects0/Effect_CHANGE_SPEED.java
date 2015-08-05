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
public class Effect_CHANGE_SPEED extends Effect_PercentAdd{

    public Effect_CHANGE_SPEED(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "PropertyEnhancer";
    }


    public void genBuffEnhance(PrintWriter out, String p1, String p2){
        // Effect_PercentAdd£º°Ù·Ö±Èfloat
        out.println("        if (" + p1 + " > 0.0f) {");
        out.println("            pc.fast(multiple * " + p1 + " / 100.0f);");
        out.println("        } else {");
        out.println("            pc.slow(multiple * " + p1 + " / -100.0f);");
        out.println("        }");
    }
}
