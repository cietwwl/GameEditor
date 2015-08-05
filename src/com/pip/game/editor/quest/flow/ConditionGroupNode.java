package com.pip.game.editor.quest.flow;

import java.util.*;

import com.pip.game.editor.quest.expr.A_Empty;
import com.pip.game.editor.quest.expr.C_True;
import com.pip.game.editor.quest.expr.IExpr;

/**
 * ������ڵ㡣���ڶ���ڵ����һ����Ĺ�ϵ��
 * @author ybai
 */
public class ConditionGroupNode extends AbstractConditionNode {
    
    public List<ConditionNode> conditionNodes = new ArrayList<ConditionNode>();

    public ConditionGroupNode(Flow flow) {
        super(flow);
    }
    
    /**
     * ת��Ϊ�ַ�����
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for(ConditionNode conditionNode : conditionNodes) {
            sb.append(conditionNode.condition.toNatureString());
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public boolean hasCondition(ConditionNode cn) {
        return conditionNodes.contains(cn);
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
        // ���ڶ������е��ж��������Դ��������������жϻ����������������������֯��һ�������飩
        if (parent == null || !(parent instanceof ActionGroupNode)) {
            if (expr.isCondition()) {
                ConditionNode condNode = new ConditionNode(flow);
                condNode.setCondition(expr);
                if (x >= bounds.x + bounds.width - 20) {
                    // �����ĩλ�Σ���������Ϊ�����ӽڵ���ֵ� 
                    addChild(condNode);
                } else if (x <= bounds.x + 20 ) {
                    if(parent != null) {
                        parent.children.remove(this);
                        parent.addChild(condNode);              
                    } else {
                        this.flow.startNode = condNode;
                    }
                    condNode.addChild(this);
                    
                } else if (x > bounds.x + 20 && x <= bounds.x + 40) {
                    conditionNodes.add(condNode);
                    condNode.setParent(this);
                } else {
                    // �����������������Ϊ�����ӽڵ�ĸ��ڵ�
                    for (FlowNode child : children) {
                        condNode.addChild(child);
                    }
                    children.clear();
                    addChild(condNode);
                }
            } else {
                ActionGroupNode group = null;
                for (FlowNode child : children) {
                    if (child instanceof ActionGroupNode) {
                        group = (ActionGroupNode)child;
                        break;
                    }
                }
                if (group == null) {
                    group = new ActionGroupNode(flow);
                    addChild(group);
                }
                ActionNode actionNode = new ActionNode(flow);
                actionNode.setAction(expr);
                group.addChild(actionNode);
                actionNode.updateChildren(flatMode, flows);
            }
            return true;
        } else {
            // �ڶ������е��ж�����Ӧ������ֻ��2��������Ϊ��֧
            if (children.size() < 2 && !expr.isCondition()) {
                ConditionNode condNode = new ConditionNode(flow);
                condNode.setCondition(expr);
                addChild(condNode);
                return true;
            }
        }
        
        return false;
    }
    
}
