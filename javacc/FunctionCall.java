package com.pip.sanguo.data.quest.pqe;

import java.util.HashMap;
import java.text.*;

/**
 * 函数调用。
 * @author lighthu
 */
public class FunctionCall extends SimpleNode {
    /**
     * 函数名。
     */
    public String funcName;
    
    public FunctionCall(int id) {
        super(id);
    }

    public FunctionCall(Parser p, int id) {
        super(p, id);
    }

    /**
     * 转换为原始字符串表示。
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
     * 转换为自然语言表示。
     */
    public String toNatureString() {
        try {
        	String desc = PQEUtils.funcDesc.get(funcName);
        	if (desc == null) {
        		return "未知函数" + funcName;
        	}
        	Object[] params = new Object[getParamCount()];
        	for (int i = 0; i < params.length; i++) {
        		params[i] = getParam(i).toNatureString();
        	}
        	return MessageFormat.format(desc, params);
        } catch (Exception e) {
        	return "语法错误";
        }
    }

    /**
     * 取得参数数量。
     */
    public int getParamCount() {
        return jjtGetNumChildren();
    }
    
    /**
     * 取得指定位置的参数。
     */
    public Expression getParam(int index) {
        return (Expression)jjtGetChild(index);
    }
    
    /**
     * 删除指定位置的参数。
     */
    public void deleteParam(int index) {
        Node[] newarr = new Node[children.length - 1];
        System.arraycopy(children, 0, newarr, 0, index);
        System.arraycopy(children, index + 1, newarr, index, newarr.length - index);
        children = newarr;
    }
    
    /**
     * 在指定位置添加一个新的参数。
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
