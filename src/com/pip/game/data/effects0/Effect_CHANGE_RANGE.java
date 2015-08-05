/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_ChangeSkill;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_CHANGE_RANGE extends Effect_ChangeSkill{

    public Effect_CHANGE_RANGE(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "SkillEnhancer";
    }

}
