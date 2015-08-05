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
public class Effect_RELIVE_TARGET extends Effect_PercentAdd{

    public Effect_RELIVE_TARGET(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genBuffFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_PercentAdd: 百分比float
        out.println("        if (context.source == context.target) {");
        out.println("            if (isActive && !context.source.isAlive()) {");
        out.println("                int hp = (int)(context.source.maxhp * " + p1 + " / 100.0f);");
        out.println("                int mp = (int)(context.source.maxmp * " + p1 + " / 100.0f);");
        out.println("                context.source.relive(hp, mp);");
        out.println("            }");
        out.println("        } else {");
        out.println("            if (isActive) {");
        out.println("                if (!context.target.isAlive()) {");
        out.println("                    // TODO: 向死亡的人发送一个复活请求");
        out.println("                } else {");
        out.println("                    if (!context.target.isAlive()) {");
        out.println("                        int hp = (int)(context.target.maxhp * " + p1 + " / 100.0f);");
        out.println("                        int mp = (int)(context.target.maxmp * " + p1 + " / 100.0f);");
        out.println("                        context.target.relive(hp, mp);");
        out.println("                    }");
        out.println("                }");
        out.println("            }");
        out.println("        }");
    }

    public void genSkillFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_PercentAdd: 百分比float
        out.println("        if (context.source == context.target) {");
        out.println("            if (isActive && !context.source.isAlive()) {");
        out.println("                int hp = (int)(context.source.maxhp * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f);");
        out.println("                int mp = (int)(context.source.maxmp * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f);");
        out.println("                context.source.relive(hp, mp);");
        out.println("            }");
        out.println("        } else {");
        out.println("            if (isActive) {");
        out.println("                if (!context.target.isAlive()) {");
        out.println("                    // TODO: 向死亡的人发送一个复活请求");
        out.println("                    if (context.target.type == GameObject.TYPE_PLAYER&&context.target.faction==context.source.faction) {");
        out.println("                        ReliveOption option = new ReliveOption(");
        out.println("                                ReliveOption.SKILL_ACTIVE, getName(), 14,");
        out.println("                               context.target.map.id, context.source.x,");
        out.println("                               context.source.y);");
        out.println("                       option.context = context;");
        out.println("                      ((Player)context.target).addReliveOption(option);");
        out.println("                   }");
        out.println("                 }");
        out.println("                } else {");
        out.println("                    if (!context.target.isAlive()) {");
        out.println("                        int hp = (int)(context.target.maxhp * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f);");
        out.println("                        int mp = (int)(context.target.maxmp * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f);");
        out.println("                        context.target.relive(hp, mp);");
        out.println("                    }");
        out.println("                }");
        out.println("        }");
    }
}