/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_HOT;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_MPHOT extends Effect_HOT{

    public Effect_MPHOT(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "Updatable";
    }

}
