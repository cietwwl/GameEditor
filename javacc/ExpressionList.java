package com.pip.sanguo.data.quest.pqe;

import java.io.StringReader;

/**
 * ���ʽ�б���һ����ʽ�ļ��ϣ���Щ���ʽ֮���ö��Ÿ�����
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
     * ת��Ϊԭʼ�ַ�����ʾ��
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
     * ת��Ϊ��Ȼ���Ա�ʾ��
     */
    public String toNatureString() {
        if (jjtGetNumChildren() == 0) {
            return "��";
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
     * ȡ�ñ��ʽ������
     */
    public int getExprCount() {
        return jjtGetNumChildren();
    }

    /**
     * ȡ��ָ��λ�õı��ʽ��
     */
    public Expression getExpr(int index) {
        return (Expression) jjtGetChild(index);
    }

    /**
     * ɾ��ָ��λ�õı��ʽ��
     */
    public void deleteExpr(int index) {
        Node[] newarr = new Node[children.length - 1];
        System.arraycopy(children, 0, newarr, 0, index);
        System.arraycopy(children, index + 1, newarr, index, newarr.length - index);
        children = newarr;
    }

    /**
     * ��ָ��λ�����һ���µı��ʽ��
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
     * ���ַ����н�����ExpressionList����
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
     * ���ַ�����ExpressionListת��Ϊ��Ȼ���ԡ�
     */
    public static String toNatureString(String str) {
        ExpressionList list = fromString(str);
        if (list == null) {
            return "�﷨����";
        } else {
            return list.toNatureString();
        }
    }
}
