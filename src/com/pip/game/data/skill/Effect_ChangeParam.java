package com.pip.game.data.skill;

import com.pip.game.editor.skill.ParamIndicator;
import com.pip.util.Utils;

/**
 * Ч����Ӱ�켼��/BUFF������������Ӱ��10��������
 * @author lighthu
 */
public class Effect_ChangeParam extends EffectConfig {
    private int type;
    private ParamIndicator[] paramInds = new ParamIndicator[0];
    private float[] value = new float[0];
    private float[] percent = new float[0];
    
    public Effect_ChangeParam(int t) {
        type = t;
    }
    
    /**
     * ���ü�������
     */
    public void setLevelCount(int max) {
        paramInds = realloc(paramInds, max);
        value = Utils.realloc(value, max);
        percent = Utils.realloc(percent, max);
    }
    
    public static ParamIndicator[] realloc(ParamIndicator[] arr, int length) {
        ParamIndicator[] ret = new ParamIndicator[length];
        System.arraycopy(arr, 0, ret, 0, length > arr.length ? arr.length : length);
        for (int i = arr.length; i < ret.length; i++) {
            ret[i] = new ParamIndicator();
        }
        return ret;
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
            return "����";
        case 2:
            return "�ٷֱ�";
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
            return ParamIndicator.class;
        case 1:
            return Float.class;
        case 2:
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
            return paramInds;
        case 1:
            return value;
        case 2:
            return percent;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
