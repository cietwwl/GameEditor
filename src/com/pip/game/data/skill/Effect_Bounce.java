package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * Ч����һ�����ʷ����˺����ɷ����̶��˺����򷴵��ܵ��˺��İٷֱȡ�
 * @author lighthu
 */
public class Effect_Bounce extends EffectConfig {
    private int type;
    private float[] rate = new float[0];
    private int[] damageType = new int[0];
    private int[] value = new int[0];
    private float[] percent = new float[0];
    
    public Effect_Bounce(int t) {
        type = t;
    }
    
    /**
     * ���ü�������
     */
    public void setLevelCount(int max) {
        rate = Utils.realloc(rate, max);
        damageType = Utils.realloc(damageType, max);
        value = Utils.realloc(value, max);
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
            return "����%";
        case 1:
            return "�˺�����";
        case 2:
            return "�̶�ֵ";
        case 3:
            return "ռ�˺�����%";
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
            return rate;
        case 1:
            return damageType;
        case 2:
            return value;
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
