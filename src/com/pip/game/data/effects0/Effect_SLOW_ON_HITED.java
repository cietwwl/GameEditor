/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_SlowOnHited;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_SLOW_ON_HITED extends Effect_SlowOnHited{

    public Effect_SLOW_ON_HITED(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genBuffFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_SlowOnHited: 概率float，减速级别 int，减速时间int
        out.println("        if (!isActive && context.hited() && context.isDamage()) {");
        out.println("            if (CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000)) {");
        out.println("                context.passiveSkills.add(new SlowSkill(" + p2 + ", " + p3 + "));");
        out.println("                effectTimes++;");
        out.println("            }");
        out.println("        }");
    }
}
