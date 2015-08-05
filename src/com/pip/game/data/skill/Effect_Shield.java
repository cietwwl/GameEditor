package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * Ч�����������ܡ�
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
     * ���ü�������
     */
    public void setLevelCount(int max) {
        total = Utils.realloc(total, max);
        percent = Utils.realloc(percent, max);
        rate = Utils.realloc(rate, max);
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
        return 3;
    }
    
    /**
     * ȡ�ò���������
     */
    public String getParamName(int index) {
        switch (index) {
        case 0:
            return "����";
        case 1:
            return "��������%";
        case 2:
            return "������%";
        }
        throw new IllegalArgumentException();
    }
    
    /**
     * ȡ�ò��������͡�
     * @return ������Integer, Float��String
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
     * ȡ��ĳ������������Ĳ���ֵ
     * @return ������int[], float[]��String[]
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
