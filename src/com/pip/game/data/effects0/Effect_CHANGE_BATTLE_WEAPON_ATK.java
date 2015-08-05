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
public class Effect_CHANGE_BATTLE_WEAPON_ATK extends Effect_PercentAdd{

    public Effect_CHANGE_BATTLE_WEAPON_ATK(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }

}
