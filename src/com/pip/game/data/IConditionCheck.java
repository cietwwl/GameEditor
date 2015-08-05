package com.pip.game.data;

import com.pip.game.data.quest.pqe.ExpressionList;

/**
 * 此接口表示可以提供计算条件表达式的能力。Player应实现此接口。
 * @author lighthu
 */
public interface IConditionCheck {
    /**
     * 执行一个表达式计算，并返回计算结果。
     * @param expr
     * @return 非0表示true，0表示false
     */
    int checkCondition(ExpressionList expr);
}
