/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_CritActiveBuff;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_CRIT_ACTIVE_BUFF extends Effect_CritActiveBuff{

    public Effect_CRIT_ACTIVE_BUFF(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genBuffFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_CritActiveBuff: ¸ÅÂÊfloat£¬BUFFID int£¬BUFF¼¶±ðint
        out.println("        if (isActive && context.critical() && context.isAttack()) {");
        out.println("            boolean hit = CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000);");
        out.println("            if (hit) {");
        out.println("                ((CombatUnit)((CombatUnit)context.source).getBuffs().addBuff(BuffUtil.createBuff(" + p2 + ", "
        + p3 + ", context.source, context.source, context.value));");
        out.println("                effectTimes++;");
        out.println("            }");
        out.println("        }");
    }
}
