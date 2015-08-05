package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * Ч�������к����Ŀ�ꡣ
 * @author lighthu
 */
public class Effect_SlowOnHit extends EffectConfig {
    private int type;
    private float[] rate = new float[0];
    private String[] rateVar = new String[0];
    private int[] slowLevel = new int[0];
    private String[] slowLevelVar = new String[0];
    private int[] slowTime = new int[0];
    private String[] slowTimeVar = new String[0];
    
    public Effect_SlowOnHit(int t) {
        type = t;
    }
    
    /**
     * ���ü�������
     */
    public void setLevelCount(int max) {
        rate = Utils.realloc(rate, max);
        rateVar = Utils.realloc(rateVar, max, "");
        slowLevel = Utils.realloc(slowLevel, max);
        slowLevelVar = Utils.realloc(slowLevelVar, max, "");
        slowTime = Utils.realloc(slowTime, max);
        slowTimeVar = Utils.realloc(slowTimeVar, max, "");
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
        return 6;
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
            return "���ټ���";
        case 3:
            return "���ټ������";
        case 4:
            return "����ʱ��(����)";
        case 5:
            return "����ʱ�����";
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
            return Integer.class;
        case 3:
            return String.class;
        case 4:
            return Integer.class;
        case 5:
            return String.class;
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
            return slowLevel;
        case 3:
            return slowLevelVar;
        case 4:
            return slowTime;
        case 5:
            return slowTimeVar;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
