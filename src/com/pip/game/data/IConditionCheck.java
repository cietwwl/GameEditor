package com.pip.game.data;

import com.pip.game.data.quest.pqe.ExpressionList;

/**
 * �˽ӿڱ�ʾ�����ṩ�����������ʽ��������PlayerӦʵ�ִ˽ӿڡ�
 * @author lighthu
 */
public interface IConditionCheck {
    /**
     * ִ��һ�����ʽ���㣬�����ؼ�������
     * @param expr
     * @return ��0��ʾtrue��0��ʾfalse
     */
    int checkCondition(ExpressionList expr);
}
