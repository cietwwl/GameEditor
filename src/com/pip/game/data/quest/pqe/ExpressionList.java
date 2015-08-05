package com.pip.game.data.quest.pqe;

import java.io.StringReader;

import com.pip.game.data.ProjectData;

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
                buf.append(" && ");
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
    
    /**
     * ���һ�����ʽ�Ƿ�����﷨����򲻿�ʶ��ı����������������Ĳ��������Ƿ���ȷ��
     * @exception ������ʽ�޷����룬�׳��쳣������������Ϣ
     */
    public void checkSyntax(String[] localVars, boolean isCondition) throws PQEException {
        for (int i = 0; i < getExprCount(); i++) {
            int dataType = getExpr(i).checkSyntax(localVars);
            if (isCondition && dataType != 0) {
                throw new PQEException("��Ϊ�����ı��ʽ���뷵�����ͽ����" + getExpr(i).toString());
            }
        }
    }
    
    /**
     * ���һ�����ʽ�Ƿ��ܹ��ɿͻ���ִ�С��������������ʽ�Ƿ���Ч�����ȱ���ͨ��
     * checkSyntax�ļ����ܵ��ô˷�����
     */
    public boolean isClientSupport(String[] localVars) {
        for (int i = 0; i < getExprCount(); i++) {
            if (!getExpr(i).isClientSupport(localVars)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * �������ʽ�п��ܻ��޸ĵı������֡�
     * @param retList
     */
    public void searchAffectLocalVar(java.util.Set<String> retList) {
        for (int i = 0; i < getExprCount(); i++) {
            getExpr(i).searchAffectLocalVar(retList);
        }
    }
    
    /**
     * �������ʽ�п�����֮�н�����NPC��
     * @param retList
     */
    public void searchRelateNPC(java.util.Set<Integer> retList) {
        for (int i = 0; i < getExprCount(); i++) {
            getExpr(i).searchRelateNPC(retList);
        }
    }
    
    /**
     * �ѱ��ʽ���õ��ľֲ�������ת��Ϊ������
     */
    public void convertVarNameToIndex(String[] localVars) {
        for (int i = 0; i < getExprCount(); i++) {
            getExpr(i).convertVarNameToIndex(localVars);
        }
    }
    
    /**
     * �ѱ��ʽ���õ��ľֲ�������ת��Ϊ������
     */
    public static String convertVarNameToIndex(String str, String[] localVars) {
        ExpressionList exprList = ExpressionList.fromString(str);
        if (exprList != null) {
            exprList.convertVarNameToIndex(localVars);
            return exprList.toString();
        }
        return str;
    }
 
    /**
     * ������ʽ�����������¼����롣һ�����ʽ�б��п��ܰ���������ʽ����Щ���ʽ�Ĵ����¼��Ľ���
     * ���Ǳ��ʽ�б�Ĵ����¼���
     */
    public int getEventMask() {
        int mask = 0;
        for (int i = 0; i < getExprCount(); i++) {            
            int m = 0;
            int count = getExpr(i).jjtGetNumChildren();
            if(count > 0 && getExpr(i).jjtGetChild(0) instanceof Expression) {
                //����ǻ��ϵ�ĸ��ϱ��ʽ������������
//                for(int j=0; j<count; j++) {
//                    m = ((Expression)getExpr(i).jjtGetChild(j)).getEventMask();
//                    
//                    if (m == PQEUtils.EVENT_MASK_CYCLE) {
//                        return m;
//                    }
//                    mask |= m;
//                    return mask;
//                }
                return mask;
            } else {
                m = getExpr(i).getEventMask();
            }            
            
            if (m == PQEUtils.EVENT_MASK_CYCLE) {
                return m;
            }
            mask |= m;
        }
        return mask;
    }
    
    /**
     * �ѻ�ϸ�ʽ�ַ������õ���NPC���õĳ����ص����ø���һ�¡�
     * @throws Exception
     */
    public void validateMixedText(ProjectData proj) throws Exception {
        for (int i = 0; i < getExprCount(); i++) {
            getExpr(i).validateMixedText(proj);
        }
    }
}
