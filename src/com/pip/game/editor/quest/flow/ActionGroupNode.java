package com.pip.game.editor.quest.flow;

import java.util.*;

import com.pip.game.editor.quest.expr.A_Empty;
import com.pip.game.editor.quest.expr.IExpr;

/**
 * ������ڵ㡣һ��������ֻ��һ�������顣
 * @author lighthu
 */
public class ActionGroupNode extends FlowNode {
    
    public ActionGroupNode(Flow flow) {
        super(flow);
    }
    
    /**
     * �ж�һ���µı��ʽģ���Ƿ��ܹ����뱾�ڵ��
     * @param expr
     * @return
     */
    public boolean canAccept(IExpr expr) {
        return true;
    }
    
    /**
     * ��һ�����ʽģ����뱾�ڵ��
     * @param expr
     * @param x ����λ�ã������Viewer��
     * @param y ����λ�ã������Viewer��
     * @return �������ʧ�ܣ�����false��
     */
    public boolean accept(IExpr expr, int x, int y, boolean flatMode, List<Flow> flows) {
        // ���Ҳ���λ��
        int insertPos = 0;
        for (FlowNode child : children) {
            if (y >= child.getBounds().y) {
                insertPos++;
            }
        }
        if (expr.isCondition()) {
            // �����һ���������ʽ�����Զ�����2��������֧
            ConditionNode condNode = new ConditionNode(flow);
            condNode.setCondition(expr);
            ActionNode switch1 = new ActionNode(flow);
            switch1.setAction(new A_Empty());
            condNode.addChild(switch1);
            ActionNode switch2 = new ActionNode(flow);
            switch2.setAction(new A_Empty());
            condNode.addChild(switch2);
            insertChild(insertPos, condNode);
        } else {
            // ����һ�������ڵ�
            ActionNode actionNode = new ActionNode(flow);
            actionNode.setAction(expr);
            insertChild(insertPos, actionNode);
            
            // ��������ڵ�������ӽڵ㣬��Ԥ�ȴ�������
            actionNode.updateChildren(flatMode, flows);
        }
        return true;
    }
}
