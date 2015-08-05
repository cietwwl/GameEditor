/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_Vampire;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_VAMPIRE_ON_HIT extends Effect_Vampire{

    public Effect_VAMPIRE_ON_HIT(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genBuffFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_Vampire: 转换比例float，有效范围int
        out.println("        if (!isActive && context.hited() && context.isDamage() && context.source.ref().equals(source)) {");
        out.println("            Player p = (Player)ObjectAccessor.getGameObject(source);");
        out.println("            if (p != null && p.isAlive()) {");
        out.println("                int value = (int)(context.damageValue * multiple * " + p1 + " / 100.0f);");
        out.println("                if (p.party == null) {");
        out.println("                    p.setHp(p.hp + value, true);");
        out.println("                } else {");
        out.println("                    List<Player> ps = p.party.getPlayerInRange(context.target.map.map, "
        + p2 + " * 8, context.target.getX(), context.target.getY());");
        out.println("                    for (Player pp : ps) {");
        out.println("                        if (pp.isAlive()) {");
        out.println("                         pp.setHp(pp.hp + value, true);");
        out.println("                        }");
        out.println("                    }");
        out.println("                }");
        out.println("            }");
        out.println("        }");
    }
}