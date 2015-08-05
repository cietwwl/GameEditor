/**
 * 
 */
package com.pip.game.data.autocoding;

import java.util.ArrayList;

import com.pip.game.data.ProjectData;
import com.pip.game.data.skill.EffectConfig;
import com.pip.game.data.skill.EffectConfigManager;

/**
 * @author jhkang
 *
 */
public class SkillTypeFilteringEffect {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
//    public static int[] RELIVE_EFFECTS = new int[] {
//        EffectConfig.RELIVE_TARGET,
//        EffectConfig.ADD_DEBUFF_ON_HIT
//    };
//    
//    public static int[] AID_EFFECTS = new int[] {
//        EffectConfig.CHANGE_THREAT,
//        EffectConfig.CHANGE_MAGIC_CRIT,
//        EffectConfig.ADD_DEBUFF_ON_HIT,
//        EffectConfig.ADD_BUFF_ON_HIT,
//        EffectConfig.DOUBLE_DAMAGE_ON_HIT,
//        EffectConfig.CURE_TARGET,
//        EffectConfig.CURE_TARGET_IGNORE_MAX,
//        EffectConfig.DISPEL_DEBUFF,
//        EffectConfig.DISPEL_ALL_DEBUFF,
//     };
//    
//    public static int[] ATTACK_SKILL = new int[] {
//        EffectConfig.CHANGE_PHYICAL_AP,
//        EffectConfig.CHANGE_MAGIC_AP,
//        EffectConfig.CHANGE_WEAPON_ATK,
//        EffectConfig.CHANGE_WEAPON_MATK,
//        EffectConfig.CHANGE_THREAT,
//        EffectConfig.CHANGE_PHYSICAL_HIT,
//        EffectConfig.CHANGE_PHYSICAL_CRIT,
//        EffectConfig.CHANGE_PHYSICAL_DODGE,
//        EffectConfig.CHANGE_MAGIC_HIT,
//        EffectConfig.CHANGE_MAGIC_CRIT,
//        EffectConfig.CHANGE_MAGIC_DODGE,
//        EffectConfig.APPEND_MAGIC_DAMAGE,
//        EffectConfig.IGNORE_ARMOR,
//        EffectConfig.IGNORE_MAGIC_ARMOR,
//        EffectConfig.ADD_MP_ON_HIT,
//        EffectConfig.ADD_DEBUFF_ON_HIT,
//        EffectConfig.ADD_BUFF_ON_HIT,
//        EffectConfig.FIRST_THREAT_ON_HIT,
//        EffectConfig.FEAR_ON_HIT,
//        EffectConfig.SLOW_ON_HIT,
//        EffectConfig.PARALYZE_ON_HIT,
//        EffectConfig.STAY_ON_HIT,
//        EffectConfig.DUMB_ON_HIT,
//        EffectConfig.REPEAT_ON_HIT,
//        EffectConfig.DOUBLE_DAMAGE_ON_HIT,
//        EffectConfig.DEC_MP_ON_HIT,
//        EffectConfig.ADD_HP_ON_HIT,
//        EffectConfig.TWO_HIT_ON_HIT,
//        EffectConfig.DISPEL_BUFF,
//        EffectConfig.DISPEL_ALL_BUFF,
//        EffectConfig.INTERRUPT,
//        EffectConfig.CHANGE_THREAT_TOTAL,
//        EffectConfig.FIRST_THREAT_TEMP,
//        EffectConfig.TRANSPORT_TO_ME,
//        EffectConfig.TRANSPORT_TO_POS,
//     };
//    
//    public static String[] skillTypes = new String[]{"SkillConfig.TYPE_RELIVE", "SkillConfig.TYPE_AID", "SkillConfig.TYPE_ATTACK"};
//    
//    public static int[][] effectsMapping = new int[][]{RELIVE_EFFECTS, AID_EFFECTS, ATTACK_SKILL};
    
    public static int[] filter(int buffType) throws Exception{
     // 配置允许出现的效果
            ArrayList<Integer> ret = new ArrayList<Integer>();
            EffectConfigManager mgr = ProjectData.getActiveProject().effectConfigManager;
            for(Class clz:mgr.TYPE_CLASSES.values()){
                int buffSupport = mgr.getSkillSupport(clz);  
                if( (buffSupport & buffType) != 0){
                    ret.add(mgr.getTypeId(clz));
                }
            }
            int[] retInt = new int[ret.size()];
            for(int i=0; i<retInt.length; i++){
                retInt[i] = ret.get(i).intValue();
            }
            return retInt;
    }

}
