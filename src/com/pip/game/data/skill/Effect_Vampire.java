package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * Ч���������к�Ϊ������ȫ�ӻ�Ѫ��
 * @author lighthu
 */
public class Effect_Vampire extends EffectConfig {
    private int type;
    private float[] percent = new float[0];
    private int[] range = new int[0];
    
    public Effect_Vampire(int t) {
        type = t;
    }
    
    /**
     * ���ü�������
     */
    public void setLevelCount(int max) {
        percent = Utils.realloc(percent, max);
        range = Utils.realloc(range, max);
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
        switch (index) {
        case 0:
            return "ת������%";
        case 1:
            return "��Ч��Χ(��)";
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
            return percent;
        case 1:
            return range;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
