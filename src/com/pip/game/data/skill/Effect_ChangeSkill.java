package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * Ч�����޸ļ��ܲ�����
 * @author lighthu
 */
public class Effect_ChangeSkill extends EffectConfig {
    private int type;
    private String[] affectSkills = new String[0];
    private float[] percent = new float[0];
    
    public Effect_ChangeSkill(int t) {
        type = t;
    }
    
    /**
     * ���ü�������
     */
    public void setLevelCount(int max) {
        affectSkills = Utils.realloc(affectSkills, max, "");
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
        return 2;
    }
    
    /**
     * ȡ�ò���������
     */
    public String getParamName(int index) {
        if (index == 0) {
            return "Ӱ�켼��";
        } else {
            return "����%";
        }
    }
    
    /**
     * ȡ�ò��������͡�
     * @return ������Integer, Float��String
     */
    public Class getParamClass(int index) {
        if (index == 0) {
            return SkillConfig[].class;
        } else {
            return Float.class;
        }
    }
    
    /**
     * ȡ��ĳ������������Ĳ���ֵ
     * @return ������int[], float[]��String[]
     */
    public Object getParam(int index) {
        if (index == 0) {
            return affectSkills;
        } else {
            return percent;
        }
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
