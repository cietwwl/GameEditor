package com.pip.game.editor.quest.flow;

import java.util.List;

import com.pip.game.editor.quest.expr.C_True;
import com.pip.game.editor.quest.expr.IExpr;

/**
 * ���ڱ�ʾһ�������жϵĽڵ㡣
 * @author lighthu
 */
public class ConditionNode extends AbstractConditionNode{
    // ����ڵ��Ӧ���жϱ��ʽ
    protected IExpr condition;
    
    public ConditionNode(Flow flow) {
        super(flow);
    }
    /**
     * �����жϱ��ʽ��
     * @param expr
     */
    public void setCondition(IExpr expr) {
        condition = expr;
    }
    
    /**
     * ȡ���жϱ��ʽ��
     * @return
     */
    public IExpr getCondition() {
        return condition;
    }

    /**
     * ת��Ϊ�ַ�����
     */
    public String toString() {
        return condition.toNatureString();
    }

    /**
     * �ж�һ���µı��ʽģ���Ƿ��ܹ����뱾�ڵ��
     * @param expr
     * @return
     */
    public boolean canAccept(IExpr expr) {
        // ���ڶ������е��ж��������Դ��������������жϻ����������������������֯��һ�������飩
        if (parent == null || !(parent instanceof ActionGroupNode)) {
            return true;
        }
        
        // �ڶ������е��ж�����Ӧ������ֻ��2��������Ϊ��֧
        if (children.size() < 2 && !expr.isCondition()) {
            return true;
        }
        
        return false;
    }
    
    /**
     * ��һ�����ʽģ����뱾�ڵ��
     * @param expr
     * @param x ����λ�ã������Viewer��
     * @param y ����λ�ã������Viewer��
     * @return �������ʧ�ܣ�����false��
     */
    public boolean accept(IExpr expr, int x, int y, boolean flatMode, List<Flow> flows) {
        // �����ǰ�ڵ���һ��ռλ������ֱ���滻���ʽ
        if ((condition instanceof C_True) && expr.isCondition()) {
            condition = expr;
            return true;
        }
        if(this.parent instanceof ConditionGroupNode) {
            if(((ConditionGroupNode)parent).hasCondition(this)) {
                ((ConditionGroupNode)parent).accept(expr, x, y, flatMode, flows);
                return true;
            }           
        }
        
        // ���ڶ������е��ж��������Դ��������������жϻ����������������������֯��һ�������飩
        if (parent == null || !(parent instanceof ActionGroupNode)) {
            if (expr.isCondition()) {
                ConditionNode condNode = new ConditionNode(flow);
                condNode.setCondition(expr);
                if (x >= bounds.x + bounds.width - 20) {
                    // �����ĩλ�Σ���������Ϊ�����ӽڵ���ֵ� 
                    addChild(condNode);
                } else if (x <= bounds.x + 20) {
                    if(parent != null) {
                        parent.children.remove(this);
                        parent.addChild(condNode);              
                    } else {
                        this.flow.startNode = condNode;
                    }
                    condNode.addChild(this);
                } else if (x > bounds.x + 20 && x <= bounds.x + 40) {
                
                    //����ConditionGroup       
                    ConditionGroupNode cgn = new ConditionGroupNode(flow);
                    
                    if(parent != null) {
                        parent.children.remove(this);  
                        parent.children.add(cgn);
                    }
                    
                    cgn.setParent(parent);
                    
                    for(FlowNode fn : children) {
                        cgn.addChild(fn);
                        fn.setParent(cgn);
                    }
                    
                    cgn.conditionNodes.add(this);
                    cgn.conditionNodes.add(condNode); 
                                        
                    condNode.setParent(cgn);
                    
                    this.setParent(cgn);

                    if(flow.startNode == this) {
                        flow.startNode = cgn;
                    }
                    this.children.clear();
                    
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
