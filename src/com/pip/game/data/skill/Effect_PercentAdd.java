package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * Ч������������ߡ�
 * @author lighthu
 */
public class Effect_PercentAdd extends EffectConfig {
    private int type;
    private float[] addPercent = new float[0];
    
    public Effect_PercentAdd(int t) {
        type = t;
    }
    
    /**
     * ���ü�������
     */
    public void setLevelCount(int max) {
        addPercent = Utils.realloc(addPercent, max);
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
        return "����%";
    }
    
    /**
     * ȡ�ò��������͡�
     * @return ������Integer, Float��String
     */
    public Class getParamClass(int index) {
        return Float.class;
    }
    
    /**
     * ȡ��ĳ������������Ĳ���ֵ
     * @return ������int[], float[]��String[]
     */
    public Object getParam(int index) {
        return addPercent;
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
