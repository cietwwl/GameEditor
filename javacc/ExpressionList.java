package com.pip.sanguo.data.quest.pqe;

import java.io.StringReader;

/**
 * 表达式列表是一组表达式的集合，这些表达式之间用逗号隔开。
 * @author lighthu
 */
public class ExpressionList extends SimpleNode {
    public ExpressionList(int id) {
        super(id);
    }

    public ExpressionList(Parser p, int id) {
        super(p, id);
    }

    /**
     * 转换为原始字符串表示。
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < jjtGetNumChildren(); i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(jjtGetChild(i).toString());
        }
        return buf.toString();
    }

    /**
     * 转换为自然语言表示。
     */
    public String toNatureString() {
        if (jjtGetNumChildren() == 0) {
            return "无";
        }
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < jjtGetNumChildren(); i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(getExpr(i).toNatureString());
        }
        return buf.toString();
    }

    /**
     * 取得表达式数量。
     */
    public int getExprCount() {
        return jjtGetNumChildren();
    }

    /**
     * 取得指定位置的表达式。
     */
    public Expression getExpr(int index) {
        return (Expression) jjtGetChild(index);
    }

    /**
     * 删除指定位置的表达式。
     */
    public void deleteExpr(int index) {
        Node[] newarr = new Node[children.length - 1];
        System.arraycopy(children, 0, newarr, 0, index);
        System.arraycopy(children, index + 1, newarr, index, newarr.length - index);
        children = newarr;
    }

    /**
     * 在指定位置添加一个新的表达式。
     */
    public void addExpr(int index, Expression expr) {
        if (index < 0 || index > children.length) {
            index = children.length;
        }
        Node[] newarr = new Node[children.length + 1];
        System.arraycopy(children, 0, newarr, 0, index);
        expr.parent = this;
        newarr[index] = expr;
        System.arraycopy(children, index, newarr, index + 1, children.length - index);
        children = newarr;
    }

    /**
     * 从字符串中解析出ExpressionList对象。
     */
    public static ExpressionList fromString(String str) {
        try {
            Parser t = new Parser(new StringReader(str));
            return t.Parse();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 把字符串的ExpressionList转换为自然语言。
     */
    public static String toNatureString(String str) {
        ExpressionList list = fromString(str);
        if (list == null) {
            return "语法错误";
        } else {
            return list.toNatureString();
        }
    }
}
