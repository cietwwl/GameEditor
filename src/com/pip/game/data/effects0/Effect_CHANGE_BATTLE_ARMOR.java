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
public class Effect_CHANGE_BATTLE_ARMOR extends Effect_MultiAdd{

    public Effect_CHANGE_BATTLE_ARMOR(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }

}
