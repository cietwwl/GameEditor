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
public class Effect_DOUBLE_DAMAGE_ON_HIT extends Effect_PercentAdd{

    public Effect_DOUBLE_DAMAGE_ON_HIT(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genBuffPostDamage(PrintWriter out, String p1, String p2, String p3){
        // Effect_PercentAdd: 百分比float
        out.println("        if (isActive && context.isDamage()) {");
        out.println("            int rate = (int)(multiple * 100 * " + p1 + ");");
        out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
        out.println("                context.damageValue *= 2;");
        out.println("                context.threat *= 2;");
        out.println("                effectTimes++;");
        out.println("            }");
        out.println("        }");
    }

    public void genBuffPreDamage(PrintWriter out, String p1, String p2, String p3){
        // Effect_PercentAdd: 百分比float
        out.println("        if (isActive && context.isDamage()) {");
        out.println("            int rate = (int)(multiple * 100 * " + p1 + ");");
        out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
        out.println("                context.damageValue *= 2;");
        out.println("                context.threat *= 2;");
        out.println("                effectTimes++;");
        out.println("            }");
        out.println("        }");
    }

    public void genSkillPostDamage(int damageType, PrintWriter out, String p1){
        // Effect_PercentAdd: 百分比float
        out.println("        int rate = (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + "));");
        out.println("        if (CommonUtil.hit(RND, rate, 10000)) {");
        out.println("            context.damageValue *= 2;");
        out.println("            context.threat *= 2;");
        out.println("        }");
    }
}