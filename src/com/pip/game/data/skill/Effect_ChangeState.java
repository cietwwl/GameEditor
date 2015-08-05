package com.pip.game.data.skill;

import com.pip.game.editor.property.StateCellEditor;
import com.pip.util.Utils;

/**
 * Ч�����ı�״̬
 * 
 * @author yqwang
 */
public class Effect_ChangeState extends EffectConfig {
    private int type;
    private int[] stateList = new int[0];
    public Effect_ChangeState(int t) {
        type = t;
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
        return "״̬";
    }
    
    /**
     * ȡ�ò��������͡�
     * @return ������Integer, Float��String
     */
    public Class getParamClass(int index) {
        return StateCellEditor.class;
    }
    
    /**
     * ȡ��ĳ������������Ĳ���ֵ
     * @return ������int[], float[]��String[]
     */
    public Object getParam(int index) {
        return stateList;
    }

    @Override
    public void setLevelCount(int max) {
        stateList = Utils.realloc(stateList, max);
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
