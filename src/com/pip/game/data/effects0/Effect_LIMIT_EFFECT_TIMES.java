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
public class Effect_LIMIT_EFFECT_TIMES extends Effect_FixValueAdd{

    public Effect_LIMIT_EFFECT_TIMES(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "Updatable";
    }

    public void genUpdate(PrintWriter out){
        String p1 = BuffConfig.getFieldName(this, 0, effectID, false);
        out.println("        if (effectTimes >= " + p1 + ") {");
        out.println("            return true;");
        out.println("        }");
    }
}
