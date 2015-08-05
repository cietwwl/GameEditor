/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_ChangeParam;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_CHANGE_PARAM extends Effect_ChangeParam{

    public Effect_CHANGE_PARAM(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "ParamEnhancer";
    }

}
