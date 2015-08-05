package com.pip.sanguo.data.quest.pqe;

import java.util.HashMap;

public class Expr0 extends SimpleNode {
    public static final int TYPE_NUMBER = 0;
    public static final int TYPE_STRING = 1;
    public static final int TYPE_IDENTIFIER = 2;
    public static final int TYPE_FUNC = 3;

    /**
     * ���͡�
     */
    public int type;
    /**
     * ����/����ֵ��
     */
    public String value;

    public Expr0(int id) {
        super(id);
    }

    public Expr0(Parser p, int id) {
        super(p, id);
    }

    /**
     * ת��Ϊԭʼ�ַ�����ʾ��
     */
    public String toString() {
        switch (type) {
        case TYPE_NUMBER:
        case TYPE_STRING:
        case TYPE_IDENTIFIER:
            return value;
        case TYPE_FUNC:
            return jjtGetChild(0).toString();
        default:
            return "";
        }
    }

    /**
     * ת��Ϊ��Ȼ���Ա�ʾ��
     */
    public String toNatureString() {
        switch (type) {
        case TYPE_NUMBER:
        case TYPE_STRING:
        	return value;
        case TYPE_IDENTIFIER:
        	if (PQEUtils.varDesc.containsKey(value)) {
        		return PQEUtils.varDesc.get(value);
        	}
            return value;
        case TYPE_FUNC:
            return getFunctionCall().toNatureString();
        default:
            return "";
        }
    }
    
    /**
     * ȡ�����͡�
     */
    public int getType() {
        return type;
    }
    
    /**
     * �������͡�
     */
    public void setType(int t) {
        type = t;
    }
    
    /**
     * ȡ��ֵ��
     */
    public String getValue() {
        return value;
    }
    
    /**
     * ����ֵ��
     */
    public void setValue(String s) {
        value = s;
    }
    
    /**
     * ȡ�ú������á�
     */
    public FunctionCall getFunctionCall() {
        return (FunctionCall)jjtGetChild(0);
    }
    
    /**
     * ���ú������á�
     */
    public void setFunctionCall(FunctionCall fc) {
        fc.parent = this;
        children = new Node[] { fc };
    }
}
