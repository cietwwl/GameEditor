package com.pip.sanguo.data.quest.pqe;

/**
 * ���ʽ��
 * @author lighthu
 */
public class Expression extends SimpleNode {
    /**
     * ��������
     */
    public int op;

    public Expression(int id) {
        super(id);
    }

    public Expression(Parser p, int id) {
        super(p, id);
    }

    /**
     * ת��Ϊԭʼ�ַ�����ʾ��
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(jjtGetChild(0).toString());
        if (jjtGetNumChildren() == 2) {
        	buf.append(" ");
        	buf.append(PQEUtils.op2str(op));
        	buf.append(" ");
            buf.append(getRightExpr().toString());
        }
        return buf.toString();
    }
    
    /**
     * ת��Ϊ��Ȼ���Ա�ʾ��
     */
    public String toNatureString() {
        StringBuffer buf = new StringBuffer();
        buf.append(getLeftExpr().toNatureString());
        if (jjtGetNumChildren() == 2) {
        	buf.append(" ");
        	buf.append(PQEUtils.op2nstr(op));
        	buf.append(" ");
            buf.append(getRightExpr().toNatureString());
        }
        return buf.toString();
    }

    /**
     * ȡ����߱��ʽ��
     */
    public Expr0 getLeftExpr() {
        return (Expr0)jjtGetChild(0);
    }
    
    /**
     * ȡ�ò�������
     */
    public int getOp() {
        return op;
    }
    
    /**
     * ȡ���ұ߱��ʽ��
     */
    public Expr0 getRightExpr() {
        return (Expr0)(this.jjtGetNumChildren() == 1 ? null : this.jjtGetChild(1));
    }
    
    /**
     * ������߱��ʽ��
     */
    public void setLeftExpr(Expr0 expr) {
        if (children == null) {
            children = new Node[1];
        }
        expr.parent = this;
        children[0] = expr;
    }
    
    /**
     * ���ò�������
     */
    public void setOp(int value) {
        op = value;
    }
    
    /**
     * �����ұ߱��ʽ��
     */
    public void setRightExpr(Expr0 expr) {
        expr.parent = this;
        if (children == null) {
            children = new Node[] { null, expr };
        } else if (children.length == 1) {
            Node oldChild = children[0];
            children = new Node[] { oldChild, expr };
        } else {
            children[1] = expr;
        }
    }
}
