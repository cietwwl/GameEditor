/**
 * 
 */
package com.pip.game.data.autocoding;

import java.util.ArrayList;
import java.util.Arrays;

import com.pip.game.data.ProjectData;
import com.pip.game.data.skill.EffectConfig;
import com.pip.game.data.skill.EffectConfigManager;

/**
 * @author jhkang
 *
 */
public class BuffFilteringEffect {

    public static final int[] supportBuffEffects = new int[] {
        EffectConfig.CHANGE_PHYICAL_AP,
        EffectConfig.CHANGE_MAGIC_AP,
        EffectConfig.CHANGE_BASIC_MAGIC_AP,
        EffectConfig.CHANGE_MAGIC_HEAL,
        EffectConfig.CHANGE_WEAPON_ATK,
        EffectConfig.CHANGE_WEAPON_MATK,
        EffectConfig.CHANGE_THREAT,
        EffectConfig.CHANGE_ARMOR,
        EffectConfig.CHANGE_MAGIC_ARMOR,
        EffectConfig.CHANGE_PHYSICAL_HIT,
        EffectConfig.CHANGE_PHYSICAL_CRIT,
        EffectConfig.CHANGE_PHYSICAL_DODGE,
        EffectConfig.CHANGE_MAGIC_HIT,
        EffectConfig.CHANGE_MAGIC_CRIT,
        EffectConfig.CHANGE_MAGIC_DODGE,
        EffectConfig.CHANGE_PHYSICAL_HIT_RATE,
        EffectConfig.CHANGE_PHYSICAL_CRIT_RATE,
        EffectConfig.CHANGE_PHYSICAL_DODGE_RATE,
        EffectConfig.CHANGE_MAGIC_HIT_RATE,
        EffectConfig.CHANGE_MAGIC_CRIT_RATE,
        EffectConfig.CHANGE_MAGIC_DODGE_RATE,
        EffectConfig.REDUCE_PHYSICAL_DAMAGE,
        EffectConfig.REDUCE_MAGIC_DAMAGE,
        EffectConfig.CHANGE_MP_RENEW,
        EffectConfig.CHANGE_HP_RENEW,
        EffectConfig.CHANGE_SPEED,
        EffectConfig.CHANGE_MAXHP,
        EffectConfig.CHANGE_BASIC_HP,
        EffectConfig.CHANGE_BASIC_MP,
        EffectConfig.CHANGE_CURE_EFFECT,
        EffectConfig.CHANGE_STA,
        EffectConfig.CHANGE_AGI,
        EffectConfig.CHANGE_STR,
        EffectConfig.CHANGE_INT,
        EffectConfig.APPEND_MAGIC_DAMAGE,
        EffectConfig.IGNORE_ARMOR,
        EffectConfig.IGNORE_MAGIC_ARMOR,
        EffectConfig.ADD_MP_ON_HIT,
        EffectConfig.ADD_DEBUFF_ON_HIT,
        EffectConfig.ADD_BUFF_ON_HIT,
        EffectConfig.FIRST_THREAT_ON_HIT,
        EffectConfig.FEAR_ON_HIT,
        EffectConfig.SLOW_ON_HIT,
        EffectConfig.PARALYZE_ON_HIT,
        EffectConfig.STAY_ON_HIT,
        EffectConfig.DUMB_ON_HIT,
        EffectConfig.REPEAT_ON_HIT,
        EffectConfig.DOUBLE_DAMAGE_ON_HIT,
        EffectConfig.DEC_MP_ON_HIT,
        EffectConfig.ADD_HP_ON_HIT,
        EffectConfig.TWO_HIT_ON_HIT,
        EffectConfig.RELIVE_TARGET,
        EffectConfig.IMMUNE_PHYICAL_ATTACK,
        EffectConfig.IMMUNE_MAGIC_ATTACK,
        EffectConfig.IMMUNE_SLOW_ATTACK,
        EffectConfig.IMMUNE_FEAR,
        EffectConfig.IMMUNE_DUMB,
        EffectConfig.IMMUNE_PARALYZE,
        EffectConfig.IMMUNE_STAY,
        EffectConfig.IMMUNE_BREAKATTACK,
        EffectConfig.COUNTER_ATTACK,
        EffectConfig.BOUNCE,
        EffectConfig.ADD_DEBUFF_ON_HITED,
        EffectConfig.ADD_BUFF_ON_HITED,
        EffectConfig.SLOW_ON_HITED,
        EffectConfig.CHANGE_MP_USE,
        EffectConfig.SET_VARIABLE,
        EffectConfig.H_O_T,
        EffectConfig.D_O_T,
        EffectConfig.MPHOT,
        EffectConfig.MPDOT,
        EffectConfig.MP_SHIELD,
        EffectConfig.VAMPIRE_ON_HIT,
        EffectConfig.CHANGE_CD_TIME,
        EffectConfig.CHANGE_DISTANCE,
        EffectConfig.CHANGE_ACT_TIME,
        EffectConfig.CHANGE_RANGE,
        EffectConfig.CANNOT_MOVE,
        EffectConfig.HP_ACTIVE_BUFF,
        EffectConfig.CRIT_ACTIVE_BUFF,
        EffectConfig.CRITED_ACTIVE_BUFF,
        EffectConfig.LIMIT_EFFECT_TIMES,
        EffectConfig.LIMIT_SKILL,
        EffectConfig.CHANGE_BATTLE_PHYICAL_AP,
        EffectConfig.CHANGE_BATTLE_MAGIC_AP,
        EffectConfig.CHANGE_BATTLE_WEAPON_ATK,
        EffectConfig.CHANGE_BATTLE_WEAPON_MATK,
        EffectConfig.CHANGE_BATTLE_PHYSICAL_HIT,
        EffectConfig.CHANGE_BATTLE_PHYSICAL_CRIT,
        EffectConfig.CHANGE_BATTLE_PHYSICAL_DODGE,
        EffectConfig.CHANGE_BATTLE_PHYSICAL_CRITED,
        EffectConfig.CHANGE_BATTLE_MAGIC_HIT,
        EffectConfig.CHANGE_BATTLE_MAGIC_CRIT,
        EffectConfig.CHANGE_BATTLE_MAGIC_DODGE,
        EffectConfig.CHANGE_BATTLE_MAGIC_CRITED,
        EffectConfig.CHANGE_BATTLE_ARMOR,
        EffectConfig.CHANGE_BATTLE_MAGIC_ARMOR,
        EffectConfig.CHANGE_EXP_RATE,
        EffectConfig.CHANGE_HORSE_EXP_RATE,
        EffectConfig.CHANGE_MONEY_RATE,
        EffectConfig.CHANGE_PARAM,
        EffectConfig.REMOVE_ON_BATTLE_END,
    };
    static{
        Arrays.sort(supportBuffEffects);
    }
    public static int[] filterBuffEffects(int buffType) throws Exception{
        ArrayList<Integer> ret = new ArrayList<Integer>();
        EffectConfigManager manager = ProjectData.getActiveProject().effectConfigManager;
        for (int typeID : manager.TYPE_CLASSES.keySet()) {
            Class clz = manager.TYPE_CLASSES.get(typeID);
            int buffSupport = manager.getBuffSupport(clz); 
            if( (buffSupport & buffType) != 0){
                ret.add(typeID);
            }
        }
        int[] retInt = new int[ret.size()];
        for(int i=0; i<retInt.length; i++){
            retInt[i] = ret.get(i).intValue();
        }
        return retInt;
    }

}
