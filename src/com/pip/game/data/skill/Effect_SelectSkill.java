package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * ѡ����
 * 
 * @author yqwang
 */
public class Effect_SelectSkill extends EffectConfig {
    private int type;
    private String[] idlList = new String[0];
    private int[] levelList = new int[0];
    private float[] rateList = new float[0];
    public Effect_SelectSkill(int t) {
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
        return 3;
    }

    /**
     * ȡ�ò���������
     */
    public String getParamName(int index) {
        switch (index) {
            case 0:
                return "����";
            case 1:
                return "�ȼ�";
            case 2:
                return "����%";
        }
        return null;
    }

    /**
     * ȡ�ò��������͡�
     * 
     * @return ������Integer, Float��String
     */
    public Class getParamClass(int index) {
        switch (index) {
            case 0:
                return SkillConfig[].class;
            case 1:
                return Integer.class;
            case 2:
                return Float.class;
        }
        return null;
    }

    /**
     * ȡ��ĳ������������Ĳ���ֵ
     * 
     * @return ������int[], float[]��String[]
     */
    public Object getParam(int index) {
        switch (index) {
            case 0:
                return idlList;
            case 1:
                return levelList;
            case 2:
                return rateList;
        }
        return null;
    }

    @Override
    public void setLevelCount(int max) {
        idlList = Utils.realloc(idlList, max, "");
        rateList = Utils.realloc(rateList, max);
        levelList = Utils.realloc(levelList, max);
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
