package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * Ч��������������2�Ρ�
 * @author lighthu
 */
public class Effect_Hit3Times extends EffectConfig {
    private int type;
    
    public Effect_Hit3Times(int t) {
        type = t;
    }
    
    /**
     * ���ü�������
     */
    public void setLevelCount(int max) {
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
        return 0;
    }
    
    /**
     * ȡ�ò���������
     */
    public String getParamName(int index) {
        throw new IllegalArgumentException();
    }
    
    /**
     * ȡ�ò��������͡�
     * @return ������Integer, Float��String
     */
    public Class getParamClass(int index) {
        throw new IllegalArgumentException();
    }
    
    /**
     * ȡ��ĳ������������Ĳ���ֵ
     * @return ������int[], float[]��String[]
     */
    public Object getParam(int index) {
        throw new IllegalArgumentException();
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
