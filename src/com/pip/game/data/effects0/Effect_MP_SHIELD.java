/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_Shield;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_MP_SHIELD extends Effect_Shield{

    public Effect_MP_SHIELD(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genBuffPostDamage(PrintWriter out, String p1, String p2, String p3){
        // Effect_Shield: 总量int，抵消比例float，消耗率float
        out.println("        if (!isActive && context.isDamage()) {");
        out.println("            int absorb = (int)(context.damageValue * " + p2 + " / 100.0f);");
        out.println("            if (absorb > remainAbsorb) {");
        out.println("                absorb = remainAbsorb;");
        out.println("            }");
        out.println("            int needMp = (int)(absorb * " + p3 + " / 100.0f);");
        out.println("            if (needMp > context.target.mp) {");
        out.println("                needMp = context.target.mp;");
        out.println("                absorb = (int)(needMp * 100.0f / " + p3 + ");");
        out.println("            }");
        out.println("            if (absorb > 0) {");
        out.println("                context.damageValue -= absorb;");
        out.println("                remainAbsorb -= absorb;");
        out.println("                context.target.setMp(context.target.mp - needMp, true);");
        out.println("            }");
        out.println("            effectTimes++;");
        out.println("        }");
    }

    public void genBuffPreDamage(PrintWriter out, String p1, String p2, String p3){
        // Effect_Shield: 总量int，抵消比例float，消耗率float
        out.println("        if (!isActive && context.isDamage()) {");
        out.println("            int absorb = (int)(context.damageValue * " + p2 + " / 100.0f);");
        out.println("            if (absorb > remainAbsorb) {");
        out.println("                absorb = remainAbsorb;");
        out.println("            }");
        out.println("            int needMp = (int)(absorb * " + p3 + " / 100.0f);");
        out.println("            if (needMp > context.target.mp) {");
        out.println("                needMp = context.target.mp;");
        out.println("                absorb = (int)(needMp * 100.0f / " + p3 + ");");
        out.println("            }");
        out.println("            if (absorb > 0) {");
        out.println("                context.damageValue -= absorb;");
        out.println("                remainAbsorb -= absorb;");
        out.println("                context.target.setMp(context.target.mp - needMp, true);");
        out.println("            }");
        out.println("            effectTimes++;");
        out.println("        }");
    }

    @Override
    public void genConstructorCodes(PrintWriter out) {
        String p1 = BuffConfig.getFieldName(this, 0, effectID, false);
        out.println("        remainAbsorb = " + p1 + ";");
    }
    
    
}