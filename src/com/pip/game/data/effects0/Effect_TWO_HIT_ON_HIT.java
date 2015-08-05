/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_Hit3Times;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_TWO_HIT_ON_HIT extends Effect_Hit3Times{

    public Effect_TWO_HIT_ON_HIT(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genBuffFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_Hit3Times: 无参数
        out.println("        context.activeSkills.add(context.skill);");
        out.println("        context.activeSkills.add(context.skill);");
        out.println("        effectTimes++;");
    }

    public void genSkillPreHit(PrintWriter out, int damageType, String p1){
        // Effect_Hit3Times: 无参数
        out.println("        context.activeSkills.add(context.skill);");
        out.println("        context.activeSkills.add(context.skill);");
    }
}
