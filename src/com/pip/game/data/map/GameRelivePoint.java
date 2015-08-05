package com.pip.game.data.map;

import com.pip.game.data.quest.pqe.ExpressionList;

/**
 * ��ͼ�е�һ������㡣
 * @author lighthu
 */
public class GameRelivePoint extends GameMapObject {
    /**
     * ʹ�ô˸���������������
     */
    public ExpressionList condition = ExpressionList.fromString("");
    /**
     * ��תλ�á�
     */
    public int[] jumpPosition = new int[] { -1, 0, 0 };

    /**
     * �õ�ȫ���ơ�
     */
    public String toString() {
        String name = owner.name + " -> �����" + this.id + "(" + x + "," + y + ")";
        if (condition.toString().length() > 0) {
            name = name + "(" + condition.toNatureString() + ")";
        }
        return name;
    }
}
