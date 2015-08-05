package com.pip.game.data.map;

import com.pip.game.data.quest.pqe.ExpressionList;

/**
 * ��ͼ�е�һ�����ڡ�
 * @author lighthu
 */
public class GameMapExit extends GameMapObject {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_RECORD = 1;
    public static final int TYPE_RECALL = 2;
    public static final int TYPE_INTERNAL = 3;
    public static final int TYPE_CUSTOM = 4;
    
    /** Ŀ���ͼID */
    public int targetMap;
    /** Ŀ��Xλ�ã����أ�*/
    public int targetX;
    /** Ŀ��Yλ�ã����أ�*/
    public int targetY;
    /** �Ƿ���ʾ���� */
    public boolean showName = true;
    /** ͨ�����ͣ�0 - ��ͨ��1 - ��¼��ǰλ�á�2 - ���ؼ�¼λ�á�3 - Ѱ·�� 4-����Ӫ���� */
    public int exitType;
    /** ��¼λ�õı���������ұ����� */
    public String positionVarName = "";
    /** ͨ������ */
    public ExpressionList constraints = ExpressionList.fromString("");
    /** ��������*/
    public String constraintsDes = "";
    
    /** ������λ */
    public long mirrorSet = 1L;
    
    public String name = "";
    /**
     * �õ�ȫ���ƣ������������ƺͳ���λ�á�
     */
    public String toString() {
        return owner.name + " -> ���͵�" + this.id + "("+targetMap + ":" + targetX + "," + targetY + ")";
    }
}
