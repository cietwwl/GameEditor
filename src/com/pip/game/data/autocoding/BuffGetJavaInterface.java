/**
 * 
 */
package com.pip.game.data.autocoding;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.pip.game.data.skill.EffectConfig;
import com.pip.util.Utils;

/**
 * @author jhkang
 *
 */
public class BuffGetJavaInterface {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
    
    public static HashMap<String, String> makeInterfaceMapping() throws Exception{
        HashMap<String, String> ret = new HashMap<String, String>();
        String dir = "E:/workspace//Game-Editor1.0/src";
        String path = BuffGetJavaInterface.class.getName().replace(".", "/")+".java";
        String content = Utils.loadFileContent( new File(dir+"/"+path) );
//        System.out.println(content);
        String[] lines = content.split("\n");
        ArrayList<String> types = new ArrayList<String>();
        String code = null;
        for(String line:lines){
            String trim = line.trim();
            if(trim.startsWith("case")){
                String type = trim.split("EffectConfig")[1].replace(".", "").replace(":", "");
                types.add(type);
            }else if(trim.startsWith("break")){
                for(String type:types){
                    ret.put(type, code);
                }
                types.clear();
            }else if(trim.startsWith("ret.add")){//ret.add("CombatEffect");
                code = "return "+trim.substring(trim.indexOf("\""), trim.lastIndexOf("\"")+1);
            }else if(trim.startsWith("throw new")){
                code = trim;
            }
        }
        return ret;
    }

    public static void make(EffectConfig eff, Set ret) {
        switch(eff.getType()){
            case EffectConfig.CHANGE_PHYICAL_AP:
            case EffectConfig.CHANGE_MAGIC_AP:
            case EffectConfig.CHANGE_WEAPON_ATK:
            case EffectConfig.CHANGE_WEAPON_MATK:
                ret.add("PropertyEnhancer");
                break;
            case EffectConfig.CHANGE_THREAT:
                ret.add("CombatEffect");
                break;
            case EffectConfig.CHANGE_ARMOR:
            case EffectConfig.CHANGE_PHYSICAL_HIT:
            case EffectConfig.CHANGE_PHYSICAL_CRIT:
            case EffectConfig.CHANGE_PHYSICAL_DODGE:
            case EffectConfig.CHANGE_MAGIC_CRIT:
            case EffectConfig.CHANGE_MP_RENEW:
            case EffectConfig.CHANGE_HP_RENEW:
            case EffectConfig.CHANGE_SPEED:
            case EffectConfig.CHANGE_MAXHP:
                ret.add("PropertyEnhancer");
                break;
            case EffectConfig.CHANGE_CURE_EFFECT:
            case EffectConfig.APPEND_MAGIC_DAMAGE:
            case EffectConfig.IGNORE_ARMOR:
            case EffectConfig.ADD_MP_ON_HIT:
            case EffectConfig.ADD_DEBUFF_ON_HIT:
            case EffectConfig.ADD_BUFF_ON_HIT:
            case EffectConfig.FIRST_THREAT_ON_HIT:
            case EffectConfig.FEAR_ON_HIT:
            case EffectConfig.SLOW_ON_HIT:
            case EffectConfig.PARALYZE_ON_HIT:
            case EffectConfig.STAY_ON_HIT:
            case EffectConfig.REPEAT_ON_HIT:
            case EffectConfig.DOUBLE_DAMAGE_ON_HIT:
            case EffectConfig.DEC_MP_ON_HIT:
            case EffectConfig.ADD_HP_ON_HIT:
            case EffectConfig.TWO_HIT_ON_HIT:
            case EffectConfig.RELIVE_TARGET:
            case EffectConfig.IMMUNE_SKILL:
            case EffectConfig.APPEND_DAMAGE_PHYSICAL:
            case EffectConfig.APPEND_DAMAGE_MAGIC:
                ret.add("CombatEffect");
                break;
            case EffectConfig.CURE_TARGET:
                throw new IllegalArgumentException("不能使用治疗目标效果，如果想提高治疗效果，请使用提高治疗量效果。");
            case EffectConfig.IMMUNE_PHYICAL_ATTACK:
            case EffectConfig.IMMUNE_MAGIC_ATTACK:
            case EffectConfig.IMMUNE_SLOW_ATTACK:
            case EffectConfig.COUNTER_ATTACK:
            case EffectConfig.BOUNCE:
                ret.add("CombatEffect");
                break;
            case EffectConfig.CHANGE_MP_USE:
                ret.add("SkillEnhancer");
                break;
            case EffectConfig.SET_VARIABLE:
                ret.add("CombatEffect");
                break;
            case EffectConfig.H_O_T:
            case EffectConfig.D_O_T:
                ret.add("Updatable");
                break;
            case EffectConfig.MP_SHIELD:
            case EffectConfig.VAMPIRE_ON_HIT:
                ret.add("CombatEffect");
                break;
            case EffectConfig.CHANGE_CD_TIME:
            case EffectConfig.CHANGE_DISTANCE:
            case EffectConfig.CHANGE_ACT_TIME:
            case EffectConfig.CHANGE_RANGE:
                ret.add("SkillEnhancer");
                break;
            case EffectConfig.CURE_TARGET_IGNORE_MAX:    
                throw new IllegalArgumentException("不能使用治疗目标效果，如果想提高治疗效果，请使用提高治疗量效果。");
            case EffectConfig.CHANGE_MAGIC_ARMOR:
                ret.add("PropertyEnhancer");
                break;
            case EffectConfig.IGNORE_MAGIC_ARMOR:
            case EffectConfig.REDUCE_PHYSICAL_DAMAGE:
            case EffectConfig.REDUCE_MAGIC_DAMAGE:
                ret.add("CombatEffect");
                break;
            case EffectConfig.CHANGE_MAGIC_HIT:
            case EffectConfig.CHANGE_MAGIC_DODGE:
                ret.add("PropertyEnhancer");
                break;
            case EffectConfig.CANNOT_MOVE:
            case EffectConfig.MPHOT:
            case EffectConfig.MPDOT:
                ret.add("BuffUpdatable");
                break;
            case EffectConfig.CHANGE_STA:
            case EffectConfig.CHANGE_AGI:
            case EffectConfig.CHANGE_STR:
            case EffectConfig.CHANGE_INT:
                ret.add("PropertyEnhancer");
                break;
            case EffectConfig.HP_ACTIVE_BUFF:
            case EffectConfig.LIMIT_EFFECT_TIMES:
                ret.add("BuffUpdatable");
                break;
            case EffectConfig.CRIT_ACTIVE_BUFF:
            case EffectConfig.CRITED_ACTIVE_BUFF:
            case EffectConfig.DUMB_ON_HIT:
            case EffectConfig.IMMUNE_FEAR:
            case EffectConfig.IMMUNE_DUMB:
            case EffectConfig.IMMUNE_PARALYZE:
            case EffectConfig.IMMUNE_STAY:
                ret.add("CombatEffect");
                break;
            case EffectConfig.CHANGE_BASIC_MAGIC_AP:
            case EffectConfig.CHANGE_BASIC_HP:
            case EffectConfig.CHANGE_BASIC_MP:
            case EffectConfig.CHANGE_MAGIC_HEAL:
                ret.add("PropertyEnhancer");
                break;
            case EffectConfig.LIMIT_SKILL:
            case EffectConfig.CHANGE_BATTLE_PHYICAL_AP:
            case EffectConfig.CHANGE_BATTLE_MAGIC_AP:
            case EffectConfig.CHANGE_BATTLE_WEAPON_ATK:
            case EffectConfig.CHANGE_BATTLE_WEAPON_MATK:
            case EffectConfig.CHANGE_BATTLE_PHYSICAL_HIT:
            case EffectConfig.CHANGE_BATTLE_PHYSICAL_CRIT:
            case EffectConfig.CHANGE_BATTLE_PHYSICAL_DODGE:
            case EffectConfig.CHANGE_BATTLE_MAGIC_CRIT:
            case EffectConfig.CHANGE_BATTLE_MAGIC_HIT:
            case EffectConfig.CHANGE_BATTLE_MAGIC_DODGE:
            case EffectConfig.CHANGE_BATTLE_PHYSICAL_CRITED:
            case EffectConfig.CHANGE_BATTLE_MAGIC_CRITED:
            case EffectConfig.CHANGE_BATTLE_ARMOR:
            case EffectConfig.CHANGE_BATTLE_MAGIC_ARMOR:
                ret.add("CombatEffect");
                break;
            case EffectConfig.CHANGE_EXP_RATE:
            case EffectConfig.CHANGE_HORSE_EXP_RATE:
            case EffectConfig.CHANGE_MONEY_RATE:
                ret.add("PropertyEnhancer");
                break;
            case EffectConfig.CHANGE_PHYSICAL_CRIT_RATE:
            case EffectConfig.CHANGE_PHYSICAL_HIT_RATE:
            case EffectConfig.CHANGE_PHYSICAL_DODGE_RATE:
            case EffectConfig.CHANGE_MAGIC_CRIT_RATE:
            case EffectConfig.CHANGE_MAGIC_HIT_RATE:
            case EffectConfig.CHANGE_MAGIC_DODGE_RATE:
                ret.add("PropertyEnhancer");
                break;
            case EffectConfig.ADD_DEBUFF_ON_HITED:
            case EffectConfig.ADD_BUFF_ON_HITED:
            case EffectConfig.SLOW_ON_HITED:
                ret.add("CombatEffect");
                break;
            case EffectConfig.CHANGE_PARAM:
                ret.add("ParamEnhancer");
                break;
            case EffectConfig.REMOVE_ON_BATTLE_END:
                ret.add("BuffUpdatable");
                break;
            case EffectConfig.IMMUNE_BREAKATTACK:
                ret.add("CombatEffect");
                break;
        }
    }

}
