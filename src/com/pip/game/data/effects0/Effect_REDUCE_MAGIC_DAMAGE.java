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
public class Effect_REDUCE_MAGIC_DAMAGE extends Effect_MultiAdd{

    public Effect_REDUCE_MAGIC_DAMAGE(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genBuffPostDamage(PrintWriter out, String p1, String p2, String p3){
        // Effect_MultiAdd: 数额int，百分比float
        out.println("        if (!isActive && context.type instanceof MagicSkill) {");
        out.println("            int value = multiple * " + p1 + ";");
        out.println("            value += context.damageValue * multiple * " + p2 + " / 100.0f;");
        out.println("            if (value > context.damageValue) {");
        out.println("                value = context.damageValue;");
        out.println("            }");
        out.println("            if (context.damageValue > 0) {");
        out.println("                context.threat -= value * context.threat / context.damageValue;");
        out.println("                context.damageValue -= value;");
        out.println("            }");
        out.println("            effectTimes++;");
        out.println("        }");
    }

    public void genBuffPreDamage(PrintWriter out, String p1, String p2, String p3){
        // Effect_MultiAdd: 数额int，百分比float
        out.println("        if (!isActive && context.type instanceof MagicSkill) {");
        out.println("            int value = multiple * " + p1 + ";");
        out.println("            value += context.damageValue * multiple * " + p2 + " / 100.0f;");
        out.println("            if (value > context.damageValue) {");
        out.println("                value = context.damageValue;");
        out.println("            }");
        out.println("            if (context.damageValue > 0) {");
        out.println("                context.threat -= value * context.threat / context.damageValue;");
        out.println("                context.damageValue -= value;");
        out.println("            }");
        out.println("            effectTimes++;");
        out.println("        }");
    }
}
