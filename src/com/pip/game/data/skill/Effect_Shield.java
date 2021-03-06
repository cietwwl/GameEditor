package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * 效果：法力护盾。
 * @author lighthu
 */
public class Effect_Shield extends EffectConfig {
    private int type;
    private int[] total = new int[0];
    private float[] percent = new float[0];
    private float[] rate = new float[0];
    
    public Effect_Shield(int t) {
        type = t;
    }
    
    /**
     * 设置级别数量
     */
    public void setLevelCount(int max) {
        total = Utils.realloc(total, max);
        percent = Utils.realloc(percent, max);
        rate = Utils.realloc(rate, max);
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
            return "总量";
        case 1:
            return "抵消比例%";
        case 2:
            return "消耗率%";
        }
        throw new IllegalArgumentException();
    }
    
    /**
     * 取得参数的类型。
     * @return 可能是Integer, Float或String
     */
    public Class getParamClass(int index) {
        switch (index) {
        case 0:
            return Integer.class;
        case 1:
            return Float.class;
        case 2:
            return Float.class;
        }
        throw new IllegalArgumentException();
    }
    
    /**
     * 取得某个参数各级别的参数值
     * @return 可能是int[], float[]或String[]
     */
    public Object getParam(int index) {
        switch (index) {
        case 0:
            return total;
        case 1:
            return percent;
        case 2:
            return rate;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
