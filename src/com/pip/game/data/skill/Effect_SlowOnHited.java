package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * Ч���������к���ٹ����ߡ�
 * @author lighthu
 */
public class Effect_SlowOnHited extends EffectConfig {
    private int type;
    private float[] rate = new float[0];
    private int[] slowLevel = new int[0];
    private int[] slowTime = new int[0];
    
    public Effect_SlowOnHited(int t) {
        type = t;
    }
    
    /**
     * ���ü�������
     */
    public void setLevelCount(int max) {
        rate = Utils.realloc(rate, max);
        slowLevel = Utils.realloc(slowLevel, max);
        slowTime = Utils.realloc(slowTime, max);
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
            return "����%";
        case 1:
            return "���ټ���";
        case 2:
            return "����ʱ��(����)";
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
            return Float.class;
        case 1:
            return Integer.class;
        case 2:
            return Integer.class;
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
            return rate;
        case 1:
            return slowLevel;
        case 2:
            return slowTime;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
