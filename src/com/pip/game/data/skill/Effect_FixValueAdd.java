package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * Ч�������̶�ֵ��ߡ�
 * @author lighthu
 */
public class Effect_FixValueAdd extends EffectConfig {
    private int type;
    private int[] addValue = new int[0];
    
    public Effect_FixValueAdd(int t) {
        type = t;
    }
    
    /**
     * ���ü�������
     */
    public void setLevelCount(int max) {
        addValue = Utils.realloc(addValue, max);
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
        return 1;
    }
    
    /**
     * ȡ�ò���������
     */
    public String getParamName(int index) {
        return "����";
    }
    
    /**
     * ȡ�ò��������͡�
     * @return ������Integer, Float��String
     */
    public Class getParamClass(int index) {
        return Integer.class;
    }
    
    /**
     * ȡ��ĳ������������Ĳ���ֵ
     * @return ������int[], float[]��String[]
     */
    public Object getParam(int index) {
        return addValue;
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
