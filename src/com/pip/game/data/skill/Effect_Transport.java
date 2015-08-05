package com.pip.game.data.skill;

import com.pip.util.Utils;

/**
 * Ч����һ�����ʷ����˺����ɷ����̶��˺����򷴵��ܵ��˺��İٷֱȡ�
 * @author lighthu
 */
public class Effect_Transport extends EffectConfig {
    private int type;
    private int[][] pos = new int[0][];
    
    public Effect_Transport(int t) {
        type = t;
    }
    
    /**
     * ���ü�������
     */
    public void setLevelCount(int max) {
        pos = realloc(pos, max);
    }

    public static int[][] realloc(int[][] arr, int length) {
        int[][] ret = new int[length][];
        System.arraycopy(arr, 0, ret, 0, length > arr.length ? arr.length : length);
        for (int i = 0; i < ret.length; i++) {
            if (ret[i] == null) {
                ret[i] = new int[3];
            }
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
        return 1;
    }
    
    /**
     * ȡ�ò���������
     */
    public String getParamName(int index) {
        return "λ��";
    }
    
    /**
     * ȡ�ò��������͡�
     * @return ������Integer, Float��String
     */
    public Class getParamClass(int index) {
        return int[].class;
    }
    
    /**
     * ȡ��ĳ������������Ĳ���ֵ
     * @return ������int[], float[]��String[]
     */
    public Object getParam(int index) {
        return pos;
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
