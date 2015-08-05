package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * һ��Ѫ�����¼���BUFF��
 * @author lighthu
 */
public class Effect_HPActiveBuff extends EffectConfig {
    private int type;
    private float[] rate = new float[0];
    private int[] buffID = new int[0];
    private int[] buffLevel = new int[0];
    
    public Effect_HPActiveBuff(int t) {
        type = t;
    }
    
    /**
     * ���ü�������
     */
    public void setLevelCount(int max) {
        rate = Utils.realloc(rate, max);
        buffID = Utils.realloc(buffID, max);
        buffLevel = Utils.realloc(buffLevel, max);
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
            return "Ѫ��%";
        case 1:
            return "����";
        case 2:
            return "����";
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
            return BuffConfig.class;
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
            return buffID;
        case 2:
            return buffLevel;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
