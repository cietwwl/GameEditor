/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_SetVariable;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_SET_VARIABLE extends Effect_SetVariable{

    public Effect_SET_VARIABLE(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genBuffPreHit(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_SetVariable：变量名string，变量值float(重复3次)
        out.println("        if (isActive) {");
        out.println("            if (" + p1 + ".length() > 0) {");
        out.println("                context.skillParams.put(" + p1 + ", new Float(" + p2 + "));");
        out.println("            }");
        out.println("            if (" + p3 + ".length() > 0) {");
        out.println("                context.skillParams.put(" + p3 + ", new Float(" + p4 + "));");
        out.println("            }");
        out.println("            if (" + p5 + ".length() > 0) {");
        out.println("                context.skillParams.put(" + p5 + ", new Float(" + p6 + "));");
        out.println("            }");
        out.println("        }");
    }
}
