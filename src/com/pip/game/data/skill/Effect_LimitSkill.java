package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * Ч��������Ӱ�켼�ܡ�
 * @author lighthu
 */
public class Effect_LimitSkill extends EffectConfig {
    private int type;
    private String[] affectSkills = new String[0];
    
    public Effect_LimitSkill(int t) {
        type = t;
    }
    
    /**
     * ���ü�������
     */
    public void setLevelCount(int max) {
        affectSkills = Utils.realloc(affectSkills, max, "");
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
        return "���ܱ�";
    }
    
    /**
     * ȡ�ò��������͡�
     * @return ������Integer, Float��String
     */
    public Class getParamClass(int index) {
        return SkillConfig[].class;
    }
    
    /**
     * ȡ��ĳ������������Ĳ���ֵ
     * @return ������int[], float[]��String[]
     */
    public Object getParam(int index) {
        return affectSkills;
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
