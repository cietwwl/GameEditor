package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * 附加伤害
 * @author yqwang
 */
public class Effect_AppendDamage extends EffectConfig {
    
    private int type;
    private int[] damageList = new int[0];
    private int[] buffsList = new int[0];
    private int[] propsList = new int[0];
    
    public Effect_AppendDamage(int t) {
        type = t;
    }

    @Override
    public Object getParam(int index) {
        switch (index) {
            case 0:
                return damageList;
            case 1:
                return buffsList;
            case 2:
                return propsList;
        }
        return null;
    }

    @Override
    public Class getParamClass(int index) {
        switch (index) {
            case 0:
                return Integer.class;
            case 1:
                return Integer.class;
            case 2:
                return Integer.class;
        }
        return null;
    }

    @Override
    public int getParamCount() {
        return 3;
    }

    @Override
    public String getParamName(int index) {
        switch (index) {
            case 0:
                return "伤害值";
            case 1:
                return "受buff影响";
            case 2:
                return "受属性影响";
        }
        return null;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setLevelCount(int max) {
        damageList = Utils.realloc(damageList, max);
        buffsList = Utils.realloc(buffsList, max);
        propsList = Utils.realloc(propsList, max);
    }

    @Override
    public String getJavaInterface() throws Exception {
        return "CombatEffect";
    }
}
