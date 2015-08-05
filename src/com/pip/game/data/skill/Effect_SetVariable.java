package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * Ч�������ټ������ġ�
 * @author lighthu
 */
public class Effect_SetVariable extends EffectConfig {
    private int type;
    private String[] name1 = new String[0];
    private float[] value1 = new float[0];
    private String[] name2 = new String[0];
    private float[] value2 = new float[0];
    private String[] name3 = new String[0];
    private float[] value3 = new float[0];
    
    public Effect_SetVariable(int t) {
        type = t;
    }
    
    /**
     * ���ü�������
     */
    public void setLevelCount(int max) {
        name1 = Utils.realloc(name1, max, "");
        value1 = Utils.realloc(value1, max);
        name2 = Utils.realloc(name2, max, "");
        value2 = Utils.realloc(value2, max);
        name3 = Utils.realloc(name3, max, "");
        value3 = Utils.realloc(value3, max);
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
            return "����1";
        case 1:
            return "ֵ1";
        case 2:
            return "����2";
        case 3:
            return "ֵ2";
        case 4:
            return "����3";
        case 5:
            return "ֵ3";
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
            return String.class;
        case 1:
            return Float.class;
        case 2:
            return String.class;
        case 3:
            return Float.class;
        case 4:
            return String.class;
        case 5:
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
            return name1;
        case 1:
            return value1;
        case 2:
            return name2;
        case 3:
            return value2;
        case 4:
            return name3;
        case 5:
            return value3;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
