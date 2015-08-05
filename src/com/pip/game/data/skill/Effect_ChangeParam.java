package com.pip.game.data.skill;

import com.pip.game.editor.skill.ParamIndicator;
import com.pip.util.Utils;

/**
 * 效果：影响技能/BUFF参数，最多可以影响10个参数。
 * @author lighthu
 */
public class Effect_ChangeParam extends EffectConfig {
    private int type;
    private ParamIndicator[] paramInds = new ParamIndicator[0];
    private float[] value = new float[0];
    private float[] percent = new float[0];
    
    public Effect_ChangeParam(int t) {
        type = t;
    }
    
    /**
     * 设置级别数量
     */
    public void setLevelCount(int max) {
        paramInds = realloc(paramInds, max);
        value = Utils.realloc(value, max);
        percent = Utils.realloc(percent, max);
    }
    
    public static ParamIndicator[] realloc(ParamIndicator[] arr, int length) {
        ParamIndicator[] ret = new ParamIndicator[length];
        System.arraycopy(arr, 0, ret, 0, length > arr.length ? arr.length : length);
        for (int i = arr.length; i < ret.length; i++) {
            ret[i] = new ParamIndicator();
        }
        return ret;
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
            return "参数";
        case 1:
            return "数额";
        case 2:
            return "百分比";
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
            return ParamIndicator.class;
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
            return paramInds;
        case 1:
            return value;
        case 2:
            return percent;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
