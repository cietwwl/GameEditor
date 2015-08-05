package com.pip.game.editor.quest.flow;

import java.util.*;

import org.eclipse.swt.graphics.Rectangle;

import com.pip.game.editor.quest.expr.IExpr;

/**
 * ���������еĽڵ�Ĺ�ͬ���ࡣ
 * @author lighthu
 */
public abstract class FlowNode {
    // ���ڵ�
    protected FlowNode parent;
    // �ӽ��
    protected List<FlowNode> children = new ArrayList<FlowNode>();
    // ����Ļ�ϲ��ֵ�λ��
    protected Rectangle bounds;
        
    //������flow
    protected Flow flow;
    
    public FlowNode(Flow flow) {
        this.flow = flow;
    }
    
    public Flow getFlow(){
        return flow;
    }
    
    /**
     * ȡ�ø��ڵ㡣
     * @return
     */
    public FlowNode getParent() {
        return parent;
    }
    
    /**
     * ���ø��ڵ㡣
     * @param parent
     */
    public void setParent(FlowNode parent) {
        this.parent = parent;
    }
    
    /**
     * ȡ���ӽڵ��б�
     * @return
     */
    public List<FlowNode> getChildren() {
        return children;
    }
    
    /**
     * ���һ���ӽڵ㡣
     */
    public void addChild(FlowNode node) {
        node.setParent(this);
        children.add(node);
    }
    
    /**
     * ����һ���ӽڵ㡣
     */
    public void insertChild(int pos, FlowNode node) {
        node.setParent(this);
        children.add(pos, node);
    }
    
    /**
     * ������ʾλ�á�
     * @param b
     */
    public void setBounds(Rectangle b) {
        bounds = b;
    }
    
    /**
     * ȡ����ʾλ�á�
     * @return
     */
    public Rectangle getBounds() {
        return bounds;
    }
    
    /**
     * �ж�һ���µı��ʽģ���Ƿ��ܹ����뱾�ڵ��
     * @param expr
     * @return
     */
    public boolean canAccept(IExpr expr) {
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
        return false;
    }
}
