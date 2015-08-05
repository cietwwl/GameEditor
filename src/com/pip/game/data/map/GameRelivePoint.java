package com.pip.game.data.map;

import com.pip.game.data.quest.pqe.ExpressionList;

/**
 * 地图中的一个复活点。
 * @author lighthu
 */
public class GameRelivePoint extends GameMapObject {
    /**
     * 使用此复活点的限制条件。
     */
    public ExpressionList condition = ExpressionList.fromString("");
    /**
     * 跳转位置。
     */
    public int[] jumpPosition = new int[] { -1, 0, 0 };

    /**
     * 得到全名称。
     */
    public String toString() {
        String name = owner.name + " -> 复活点" + this.id + "(" + x + "," + y + ")";
        if (condition.toString().length() > 0) {
            name = name + "(" + condition.toNatureString() + ")";
        }
        return name;
    }
}
