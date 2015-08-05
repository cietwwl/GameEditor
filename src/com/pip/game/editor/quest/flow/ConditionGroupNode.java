package com.pip.game.editor.quest.flow;

import java.util.*;

import com.pip.game.editor.quest.expr.A_Empty;
import com.pip.game.editor.quest.expr.C_True;
import com.pip.game.editor.quest.expr.IExpr;

/**
 * 条件组节点。用于多个节点组成一个或的关系。
 * @author ybai
 */
public class ConditionGroupNode extends AbstractConditionNode {
    
    public List<ConditionNode> conditionNodes = new ArrayList<ConditionNode>();

    public ConditionGroupNode(Flow flow) {
        super(flow);
    }
    
    /**
     * 转换为字符串。
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
     * 判断一个新的表达式模板是否能够插入本节点后。
     * @param expr
     * @return
     */
    public boolean canAccept(IExpr expr) {
        return true;
    }
    
    /**
     * 把一个表达式模板插入本节点后。
     * @param expr
     * @param x 插入位置（相对于Viewer）
     * @param y 插入位置（相对于Viewer）
     * @return 如果插入失败，返回false。
     */
    public boolean accept(IExpr expr, int x, int y, boolean flatMode, List<Flow> flows) {        
        // 不在动作组中的判断条件可以带有任意多个后续判断或后续动作（后续动作被组织成一个动作组）
        if (parent == null || !(parent instanceof ActionGroupNode)) {
            if (expr.isCondition()) {
                ConditionNode condNode = new ConditionNode(flow);
                condNode.setCondition(expr);
                if (x >= bounds.x + bounds.width - 20) {
                    // 如果在末位段，新条件加为其他子节点的兄弟 
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
                    // 正常情况，新条件加为现有子节点的父节点
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
            // 在动作组中的判断条件应该有且只有2个动作作为分支
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
