package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * 选择技能
 * 
 * @author yqwang
 */
public class Effect_SelectSkill extends EffectConfig {
    private int type;
    private String[] idlList = new String[0];
    private int[] levelList = new int[0];
    private float[] rateList = new float[0];
    public Effect_SelectSkill(int t) {
        type = t;
    }

    /**
     * 取得效果类型ID
     */
    public int getType() {
        return type;
    }

    /**
     * 取得参数个数
     */
    public int getParamCount() {
        return 3;
    }

    /**
     * 取得参数的名字
     */
    public String getParamName(int index) {
        switch (index) {
            case 0:
                return "技能";
            case 1:
                return "等级";
            case 2:
                return "概率%";
        }
        return null;
    }

    /**
     * 取得参数的类型。
     * 
     * @return 可能是Integer, Float或String
     */
    public Class getParamClass(int index) {
        switch (index) {
            case 0:
                return SkillConfig[].class;
            case 1:
                return Integer.class;
            case 2:
                return Float.class;
        }
        return null;
    }

    /**
     * 取得某个参数各级别的参数值
     * 
     * @return 可能是int[], float[]或String[]
     */
    public Object getParam(int index) {
        switch (index) {
            case 0:
                return idlList;
            case 1:
                return levelList;
            case 2:
                return rateList;
        }
        return null;
    }

    @Override
    public void setLevelCount(int max) {
        idlList = Utils.realloc(idlList, max, "");
        rateList = Utils.realloc(rateList, max);
        levelList = Utils.realloc(levelList, max);
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
