package com.pip.sanguo.data.quest.pqe;

import java.util.HashMap;
import java.text.*;

/**
 * �������á�
 * @author lighthu
 */
public class FunctionCall extends SimpleNode {
    /**
     * ��������
     */
    public String funcName;
    
    public FunctionCall(int id) {
        super(id);
    }

    public FunctionCall(Parser p, int id) {
        super(p, id);
    }

    /**
     * ת��Ϊԭʼ�ַ�����ʾ��
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(funcName);
        buf.append("(");
        for (int i = 0; i < jjtGetNumChildren(); i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(jjtGetChild(i).toString());
        }
        buf.append(")");
        return buf.toString();
    }
    
    /**
     * ת��Ϊ��Ȼ���Ա�ʾ��
     */
    public String toNatureString() {
        try {
        	String desc = PQEUtils.funcDesc.get(funcName);
        	if (desc == null) {
        		return "δ֪����" + funcName;
        	}
        	Object[] params = new Object[getParamCount()];
        	for (int i = 0; i < params.length; i++) {
        		params[i] = getParam(i).toNatureString();
        	}
        	return MessageFormat.format(desc, params);
        } catch (Exception e) {
        	return "�﷨����";
        }
    }

    /**
     * ȡ�ò���������
     */
    public int getParamCount() {
        return jjtGetNumChildren();
    }
    
    /**
     * ȡ��ָ��λ�õĲ�����
     */
    public Expression getParam(int index) {
        return (Expression)jjtGetChild(index);
    }
    
    /**
     * ɾ��ָ��λ�õĲ�����
     */
    public void deleteParam(int index) {
        Node[] newarr = new Node[children.length - 1];
        System.arraycopy(children, 0, newarr, 0, index);
        System.arraycopy(children, index + 1, newarr, index, newarr.length - index);
        children = newarr;
    }
    
    /**
     * ��ָ��λ�����һ���µĲ�����
     */
    public void addParam(int index, Expression expr) {
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
}
