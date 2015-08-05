package com.pip.sanguo.data.quest.pqe;

/**
 * 表达式。
 * @author lighthu
 */
public class Expression extends SimpleNode {
    /**
     * 操作符。
     */
    public int op;

    public Expression(int id) {
        super(id);
    }

    public Expression(Parser p, int id) {
        super(p, id);
    }

    /**
     * 转换为原始字符串表示。
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
     * 转换为自然语言表示。
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
     * 取得左边表达式。
     */
    public Expr0 getLeftExpr() {
        return (Expr0)jjtGetChild(0);
    }
    
    /**
     * 取得操作符。
     */
    public int getOp() {
        return op;
    }
    
    /**
     * 取得右边表达式。
     */
    public Expr0 getRightExpr() {
        return (Expr0)(this.jjtGetNumChildren() == 1 ? null : this.jjtGetChild(1));
    }
    
    /**
     * 设置左边表达式。
     */
    public void setLeftExpr(Expr0 expr) {
        if (children == null) {
            children = new Node[1];
        }
        expr.parent = this;
        children[0] = expr;
    }
    
    /**
     * 设置操作符。
     */
    public void setOp(int value) {
        op = value;
    }
    
    /**
     * 设置右边表达式。
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
