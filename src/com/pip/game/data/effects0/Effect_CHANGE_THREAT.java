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
public class Effect_CHANGE_THREAT extends Effect_MultiAdd{

    public Effect_CHANGE_THREAT(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }


    public void genSkillPreDamage(PrintWriter out,  int damageType, String p1, String p2){
        // Effect_MultiAdd: ����int���ٷֱ�float
        out.println("        context.threatAdd += context.getSkillParam(this, \"" + p1 + "\", " + p1 + ");");
        out.println("        context.threatAddRate += context.getSkillParam(this, \"" + p2 + "\", " + p2 + ") / 100.0f;");
    }
}