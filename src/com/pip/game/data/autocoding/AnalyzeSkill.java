/**
 * 
 */
package com.pip.game.data.autocoding;

import com.pip.game.data.skill.EffectConfig;

/**
 * @author jhkang
 * 
 */
public class AnalyzeSkill {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public static float process(EffectConfig eff, float ret, int skillLevel) {
        switch (eff.getType()) {
            case EffectConfig.CHANGE_PHYICAL_AP:
            case EffectConfig.CHANGE_MAGIC_AP:
                ret += ((int[]) eff.getParam(0))[skillLevel];
                ret *= 1.0f + ((float[]) eff.getParam(1))[skillLevel] / 100.0f;
                break;
            case EffectConfig.APPEND_MAGIC_DAMAGE:
                ret += ((int[]) eff.getParam(0))[skillLevel];
                break;
        }
        return ret;
    }

}
