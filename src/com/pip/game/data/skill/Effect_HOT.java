package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * Ч����XX���ڻظ�XXѪ��
 * @author lighthu
 */
public class Effect_HOT extends EffectConfig {
    private int type;
    private int[] time = new int[0];
    private int[] tick = new int[0];
    private int[] total = new int[0];
    private float[] percent = new float[0];
    
    public Effect_HOT(int t) {
        type = t;
    }
    
    /**
     * ���ü�������
     */
    public void setLevelCount(int max) {
        time = Utils.realloc(time, max);
        tick = Utils.realloc(tick, max);
        total = Utils.realloc(total, max);
        percent = Utils.realloc(percent, max);
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
            return "����ʱ��(��)";
        case 1:
            return "ÿ�����(��)";
        case 2:
            return "�̶���";
        case 3:
            return "�˺�ת����%";
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
            return Integer.class;
        case 2:
            return Integer.class;
        case 3:
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
            return time;
        case 1:
            return tick;
        case 2:
            return total;
        case 3:
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
