package com.pip.game.editor.quest.flow;

/**
 * ���̱����ļ�������
 * @author lighthu
 */
public interface IFlowIterator {
    /**
     * ����һ���ڵ㡣
     * @param node
     * @return ���Ҫֹͣ����������true��
     */
    public boolean walk(FlowNode node);
}
