package com.pip.game.data.skill;

import java.util.Arrays;

import com.pip.game.editor.skill.ParamIndicator;

/**
 * ָ��һ��Ч���Ĳ��������á�
 * @author lighthu
 */
public class EffectParamRef {
    public EffectConfig effect;
    public int index;
    public int effectID;     // Ч����Ч�������е�����
    
    public EffectParamRef(EffectConfig eff, int ind, int eid) {
        effect = eff;
        index = ind;
        effectID = eid;
    }
    
    public String getParamName() {
        if (effect.getShortName().length() == 0) {
            return effect.getParamName(index);
        } else {
            return effect.getShortName() + ":" + effect.getParamName(index);
        }
    }
    
    public Class getParamClass() {
        return effect.getParamClass(index);
    }
    
    /**
     * ȡ��ĳ����Ĳ���ֵ
     * @param level 1��ʼ�ļ���
     * @return �������Ͳ�ͬ������Integer, Float��String��
     */
    public Object getParamValue(int level) {
        Object arr = effect.getParam(index);
        if (arr instanceof int[]) {
            return ((int[])arr)[level - 1];
        } else if (arr instanceof float[]) {
            return ((float[])arr)[level - 1];
        } else if (arr instanceof String[]) {
            return ((String[])arr)[level - 1];
        } else if (arr instanceof ParamIndicator[]) {
            return ((ParamIndicator[])arr)[level - 1];
        } else if (arr instanceof int[][]) {
            int[] arr2 = ((int[][])arr)[level - 1];
            int[] ret = new int[arr2.length];
            System.arraycopy(arr2, 0, ret, 0, arr2.length);
            return ret;
        }
        throw new IllegalArgumentException();
    }

    /**
     * ����ĳ����Ĳ���ֵ
     * @param level 1��ʼ�ļ���
     * @param value �������Ͳ�ͬ����Integer��Float��String
     * @param �������ֵ�ı䣬����true
     */
    public boolean setParamValue(int level, Object value) {
        Object arr = effect.getParam(index);
        if (arr instanceof int[]) {
            if (((int[])arr)[level - 1] != ((Integer)value).intValue()) {
                ((int[])arr)[level - 1] = ((Integer)value).intValue();
                return true;
            } else {
                return false;
            }
        } else if (arr instanceof float[]) {
            if (((float[])arr)[level - 1] != ((Float)value).floatValue()) {
                ((float[])arr)[level - 1] = ((Float)value).floatValue();
                return true;
            } else {
                return false;
            }
        } else if (arr instanceof String[]) {
            if (!((String[])arr)[level - 1].equals((String)value)) {
                ((String[])arr)[level - 1] = (String)value;
                return true;
            } else {
                return false;
            }
        } else if (arr instanceof ParamIndicator[]) {
            ParamIndicator oldValue = ((ParamIndicator[])arr)[level - 1];
            if (!oldValue.toString().equals(value)) {
                oldValue.load((String)value);
                return true;
            } else {
                return false;
            }
        } else if (arr instanceof int[][]) {
            int[] newValue = (int[])value;
            int[] oldValue = ((int[][])arr)[level - 1];
            if (!Arrays.equals(oldValue, newValue)) {
                int[] newArr = new int[newValue.length];
                System.arraycopy(newValue, 0, newArr, 0, newValue.length);
                ((int[][])arr)[level - 1] = newArr;
                return true;
            } else {
                return false;
            }
        }
        throw new IllegalArgumentException();
    }
    
    /**
     * �Զ�ĳ����Ĳ���ֵ�����漶�����ֵ���������ֵ����һ��ֵ֮��Ĳ�ֵ�ȱȵ���
     * @param level 1��ʼ�ļ���
     * @param value �������Ͳ�ͬ����Integer��Float��String
     * @param �������ֵ�ı䣬����true
     */
    public boolean autoSetParamValues(int level, Object value) {
        Object arr = effect.getParam(index);
        if (arr instanceof int[]) {
            int[] iarr = (int[])arr;
            if (iarr[level - 1] != ((Integer)value).intValue()) {
                iarr[level - 1] = ((Integer)value).intValue();
                int delta = 0;
                if (level > 1) {
                    delta = iarr[level - 1] - iarr[level - 2];
                }
                for (int i = level; i < iarr.length; i++) {
                    iarr[i] = iarr[i - 1] + delta;
                }
                return true;
            } else {
                return false;
            }
        } else if (arr instanceof float[]) {
            float[] farr = (float[])arr;
            if (farr[level - 1] != ((Float)value).floatValue()) {
                farr[level - 1] = ((Float)value).floatValue();
                float delta = 0.0f;
                if (level > 1) {
                    delta = farr[level - 1] - farr[level - 2];
                }
                for (int i = level; i < farr.length; i++) {
                    farr[i] = farr[i - 1] + delta;
                }
                return true;
            } else {
                return false;
            }
        } else if (arr instanceof String[]) {
            String[] sarr = (String[])arr;
            if (!sarr[level - 1].equals((String)value)) {
                sarr[level - 1] = (String)value;
                for (int i = level; i < sarr.length; i++) {
                    sarr[i] = sarr[i - 1];
                }
                return true;
            } else {
                return false;
            }
        } else if (arr instanceof ParamIndicator[]) {
            ParamIndicator[] piarr = (ParamIndicator[])arr;
            if (!piarr[level - 1].toString().equals(value)) {
                piarr[level - 1].load((String)value);
                for (int i = level; i < piarr.length; i++) {
                    piarr[i].load((String)value);
                }
                return true;
            } else {
                return false;
            }
        } else if (arr instanceof int[][]) {
            int[][] iarr = (int[][])arr;
            int[] newValue = (int[])value;
            int[] oldValue = iarr[level - 1];
            if (!Arrays.equals(newValue, oldValue)) {
                for (int i = level - 1; i < iarr.length; i++) {
                    int[] newArr = new int[newValue.length];
                    System.arraycopy(newValue, 0, newArr, 0, newValue.length);
                    iarr[i] = newArr;
                }
                return true;
            } else {
                return false;
            }
        }
        throw new IllegalArgumentException();
    }
}
