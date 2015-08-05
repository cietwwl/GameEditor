/**
 * 
 */
package com.pip.game.data.autocoding;

import java.io.PrintWriter;

import com.pip.game.data.skill.EffectConfig;

/**
 * @author jhkang
 *
 */
public class SkillPostDamage {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public static void genCode(int type, PrintWriter out, String p1) {
        switch (type) {
            case EffectConfig.APPEND_MAGIC_DAMAGE:
                // Effect_FixValueAdd: 数额int
                out.println("        context.appendSpellDamage(context.getSkillParam(this, \"" + p1 + "\", " + p1 + "));");
                break;
            case EffectConfig.DOUBLE_DAMAGE_ON_HIT:
                // Effect_PercentAdd: 百分比float
                out.println("        int rate = (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + "));");
                out.println("        if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("            context.damage *= 2;");
                out.println("            context.threat *= 2;");
                out.println("        }");
                break;
            }        
    }

}
