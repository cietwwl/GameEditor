/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_AddBuff;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_ADD_BUFF_ON_HITED extends Effect_AddBuff{

    public Effect_ADD_BUFF_ON_HITED(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genBuffFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_AddBuff: 概率float，概率补充变量String，BUFFID int，BUFF级别int
        out.println("        if (!isActive && context.hited()) {");
        out.println("            boolean hit = false;");
        out.println("            if (" + p2 + ".length() == 0) {");
        out.println("                hit = CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000);");
        out.println("            } else {");
        out.println("                Float vo = context.skillParams.get(" + p2 + ");");
        out.println("                if (vo != null) {");
        out.println("                    hit = CommonUtil.hit(RND, (int)(multiple * 100 * (vo.floatValue() + " + p1 + ")), 10000);");
        out.println("                }");
        out.println("            }");
        out.println("            if (hit) {");
        out.println("               if(context.target.getBattle() == null){");
        out.println("                  return;");
        out.println("               }");
        out.println("               AbstractBuffEx buff = (AbstractBuffEx)BuffUtil.createBuff("+p3+", "+p4+", context.source, context.target, context.value);");
        out.println("               ((CombatUnit)context.target).getBuffs().addBuff(buff);");
        out.println("               AddBuffSequence abs = new AddBuffSequence(context.target,buff);");
        out.println("               abs.setParent(context.sequence);");
        out.println("               context.target.getBattle().addSequence(abs);");
        out.println("               effectTimes++;");
        out.println("            }");
        out.println("        }");
    }
}