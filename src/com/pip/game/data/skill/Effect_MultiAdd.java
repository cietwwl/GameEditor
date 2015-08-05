package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * Ч�������̶�ֵ��߻򰴱�����ߡ�
 * @author lighthu
 */
public class Effect_MultiAdd extends EffectConfig {
    private int type;
    private int[] addValue = new int[0];
    private float[] addPercent = new float[0];
    
    public Effect_MultiAdd(int t) {
        type = t;
    }
    
    /**
     * ���ü�������
     */
    public void setLevelCount(int max) {
        addValue = Utils.realloc(addValue, max);
        addPercent = Utils.realloc(addPercent, max);
    }
    
    /**
     * ȡ��Ч������ID
     */
    public int getType() {
        return type;
    }
    
    /**
     * ȡ�ò�������
     */
    public int getParamCount() {
        return 2;
    }
    
    /**
     * ȡ�ò���������
     */
    public String getParamName(int index) {
        if (index == 0) {
            return "����";
        } else {
            return "����%";
        }
    }
    
    /**
     * ȡ�ò��������͡�
     * @return ������Integer, Float��String
     */
    public Class getParamClass(int index) {
        if (index == 0) {
            return Integer.class;
        } else {
            return Float.class;
        }
    }
    
    /**
     * ȡ��ĳ������������Ĳ���ֵ
     * @return ������int[], float[]��String[]
     */
    public Object getParam(int index) {
        if (index == 0) {
            return addValue;
        } else {
            return addPercent;
        }
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
