package com.pip.sanguo.data.quest.pqe;

import java.util.HashMap;

public class Expr0 extends SimpleNode {
    public static final int TYPE_NUMBER = 0;
    public static final int TYPE_STRING = 1;
    public static final int TYPE_IDENTIFIER = 2;
    public static final int TYPE_FUNC = 3;

    /**
     * 类型。
     */
    public int type;
    /**
     * 常量/变量值。
     */
    public String value;

    public Expr0(int id) {
        super(id);
    }

    public Expr0(Parser p, int id) {
        super(p, id);
    }

    /**
     * 转换为原始字符串表示。
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
     * 转换为自然语言表示。
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
     * 取得类型。
     */
    public int getType() {
        return type;
    }
    
    /**
     * 设置类型。
     */
    public void setType(int t) {
        type = t;
    }
    
    /**
     * 取得值。
     */
    public String getValue() {
        return value;
    }
    
    /**
     * 设置值。
     */
    public void setValue(String s) {
        value = s;
    }
    
    /**
     * 取得函数调用。
     */
    public FunctionCall getFunctionCall() {
        return (FunctionCall)jjtGetChild(0);
    }
    
    /**
     * 设置函数调用。
     */
    public void setFunctionCall(FunctionCall fc) {
        fc.parent = this;
        children = new Node[] { fc };
    }
}
