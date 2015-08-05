package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * �������к�һ�����ʼ�BUFF/DEBUFF��
 * @author lighthu
 */
public class Effect_AddBuff extends EffectConfig {
    private int type;
    private float[] rate = new float[0];
    private String[] rateVar = new String[0];
    private int[] buffID = new int[0];
    private int[] buffLevel = new int[0];
    
    public Effect_AddBuff(int t) {
        type = t;
    }
    
    /**
     * ���ü�������
     */
    public void setLevelCount(int max) {
        rate = Utils.realloc(rate, max);
        rateVar = Utils.realloc(rateVar, max, "");
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
        return 4;
    }
    
    /**
     * ȡ�ò���������
     */
    public String getParamName(int index) {
        switch (index) {
        case 0:
            return "����%";
        case 1:
            return "���ʱ���";
        case 2:
            return "����";
        case 3:
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
            return String.class;
        case 2:
            return BuffConfig.class;
        case 3:
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
            return rateVar;
        case 2:
            return buffID;
        case 3:
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
