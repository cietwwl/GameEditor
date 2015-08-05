package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * Ч������ʱ����Ϊ��һ��ޡ�
 * @author lighthu
 */
public class Effect_FirstThreatTemp extends EffectConfig {
    private int type;
    private int[] keepTime = new int[0];
    
    public Effect_FirstThreatTemp(int t) {
        type = t;
    }
    
    /**
     * ���ü�������
     */
    public void setLevelCount(int max) {
        keepTime = Utils.realloc(keepTime, max);
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
        return "����ʱ��(����)";
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
        return keepTime;
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
