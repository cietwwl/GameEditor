package com.pip.game.editor.ai;

public class AITargetType {
    /**
     * Ŀ������
     * 0 - ��һ��ޣ�1 - �ڶ���ޣ�2 - �������...�Դ�����
     * -1 - ��ޱ������Ŀ�꣬-2 ��ޱ��г���һ���������Ŀ��
     * -3 - �����佫��-4 - ���д̿ͣ�-5 - ����ıʿ��-6 - ���з�ʿ
     * -7 - �Լ�
     */
    public int targetType = 0;
    
    public static final int[] TYPE_MAP = { 0, 1, 2, 3, -1, -2, -3, -4, -5, -6, -7 };
    public static final String[] TYPE_NAMES = {
        "��һ���Ŀ��", "�ڶ����Ŀ��", "�������Ŀ��", "���ĳ��Ŀ��", 
        "���Ŀ��", "����һ���������Ŀ��",
        "�����佫", "���д̿�", "����ıʿ", "���з�ʿ",
        "�Լ�"
    };
    
    public static int type2index(int t) {
        for (int i = 0; i < TYPE_MAP.length; i++) {
            if (t == TYPE_MAP[i]) {
                return i;
            }
        }
        return 0;
    }
    
    public String getDisplayString() {
        if (targetType > 0) {
            return "��" + (targetType + 1) + "���Ŀ��";
        }
        switch (targetType) {
        case 0:
            return "��һ���Ŀ��";
        case -1:
            return "���Ŀ��";
        case -2:
            return "����һ���������Ŀ��";
        case -3:
            return "�����佫";
        case -4:
            return "���д̿�";
        case -5:
            return "����ıʿ";
        case -6:
            return "���з�ʿ";
        case -7:
            return "�Լ�";
        }
        return "����Ŀ��";
    }
    
    public String toString() {
        return String.valueOf(targetType);
    }
    
    public void parse(String str) {
        try {
            targetType = Integer.parseInt(str);
        } catch (Exception e) {
        }
    }
}
