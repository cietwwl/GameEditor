package com.pip.game.data.quest.pqe;

import java.util.HashMap;
import java.text.*;

import com.pip.game.data.ProjectData;

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

    public FunctionCall clone(Node parentNode){
        FunctionCall ret = (FunctionCall)super.clone(parentNode);
        ret.funcName = funcName;
        
        return ret;
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
        	String desc;
        	if (ProjectData.getActiveProject().config.pqeUtils.SYSTEM_FUNCS_MAP.containsKey(funcName)) {
        	    desc = ProjectData.getActiveProject().config.pqeUtils.SYSTEM_FUNCS_MAP.get(funcName).description;
        	} else {
        	    desc = "δ֪����" + funcName;
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
    
    /**
     * ����ʵ�ֱ��ʽ���ܶ�Ӧ��GTL���롣
     */
    public String toGTL() {
        // Set, Inc, Dec������Ҫ�������ͨ�ı����������
        if ("Set".equals(funcName) || "Inc".equals(funcName) || "Dec".equals(funcName)) {
            String varName = PQEUtils.translateStringConstant(getParam(0).getLeftExpr().value);
            String rightValue = getParam(1).toGTL();
            String op = "";
            if ("Set".equals(funcName)) {
                op = " = ";
            } else if ("Inc".equals(funcName)) {
                op = " += ";
            } else if ("Dec".equals(funcName)) {
                op = " -= ";
            }
            return varName + op + rightValue;
        }
        
        // Random������ֱ��ʵ��
        if ("Random".equals(funcName)) {
            return "Random()";
        }
        
        // If������Ҫ���⴦��һ�£�ת��Ϊһ��if���
        if ("If".equals(funcName)) {
            String param1 = getParam(1).toGTL();
            String param2 = getParam(2).toGTL();
            if ("1".equals(param1) || "0".equals(param1)) {
                param1 = "";
            } else {
                param1 += ";";
            }
            if ("1".equals(param2) || "0".equals(param2)) {
                param2 = "";
            } else {
                param2 += ";";
            }
            return "if (" + getParam(0).toGTL() + ") { " + param1 + " } else { " + 
                param2 + " }";
        }
        
        // ����������ǰ�����PQE_ǰ׺���ɡ�
        StringBuffer buf = new StringBuffer();
        buf.append("PQE_");
        buf.append(funcName);
        buf.append("(");
        for (int i = 0; i < getParamCount(); i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(getParam(i).toGTL());
        }
        buf.append(")");
        return buf.toString();
    }
}
