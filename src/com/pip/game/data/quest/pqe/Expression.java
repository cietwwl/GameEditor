package com.pip.game.data.quest.pqe;

import com.pip.game.data.ProjectData;

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

    public Expression clone(Node parentNode){
        Expression ret = (Expression)super.clone(parentNode);
        ret.op = op;
        
        return ret;
    }
    
    /**
     * ת��Ϊԭʼ�ַ�����ʾ��
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        
        if(children.length > 0) {
            if(children[0] instanceof Expression) {
                buf.append("(");
                for(Node node : children) {
                    if(buf.length() > 1) {
                        buf.append(", ");
                    }
                    buf.append(((Expression)node).toString());
                }
                buf.append(")");
                return buf.toString();
            }           
        }
        
        
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
        
        if(children.length > 0) {
            if(children[0] instanceof Expression) {
                buf.append("("); 
                for(Node node : children) {
                    if(buf.length() > 1) {
                        buf.append(" || ");                    
                    }
                    buf.append(((Expression)node).toNatureString());
                }
                buf.append(")");  
                return buf.toString();
            }           
        }
        
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

    /**
     * ���һ�����ʽ�Ƿ�����﷨����򲻿�ʶ��ı����������������Ĳ��������Ƿ���ȷ��
     * @return ���ر��ʽ��������
     * @exception ������ʽ�޷����룬�׳��쳣������������Ϣ
     */
    public int checkSyntax(String[] localVars) throws PQEException {
        if(children.length > 0) {
            if(children[0] instanceof Expression) {
                for(Node node : children) {
                    ((Expression)node).checkSyntax(localVars);
                }
                
                return 0;
            }           
        }
        if (getRightExpr() == null) {
            return getLeftExpr().checkSyntax(localVars);
        } else {
            int type1 = getLeftExpr().checkSyntax(localVars);
            int type2 = getRightExpr().checkSyntax(localVars);
            if (type1 != 0 || type2 != 0) {
                throw new PQEException("ֻ�����ͱ��ʽ�������ڱȽϡ�");
            }
            return 0;
        }
    }

    /**
     * ���һ�����ʽ�Ƿ��ܹ��ɿͻ���ִ�С��������������ʽ�Ƿ���Ч�����ȱ���ͨ��
     * checkSyntax�ļ����ܵ��ô˷�����
     */
    public boolean isClientSupport(String[] localVars) {
        if(children.length > 0) {
            if(children[0] instanceof Expression) {
                for(Node node : children) {
                    if(((Expression)node).isClientSupport(localVars) == false) {
                        return false;
                    }
                }
                return true;
            }
        }
        
        if (getRightExpr() == null) {
            return getLeftExpr().isClientSupport(localVars);
        } else {
            return getLeftExpr().isClientSupport(localVars) && getRightExpr().isClientSupport(localVars);
        }
    }

    /**
     * �������ʽ�п��ܻ��޸ĵı������֡�
     * @param retList
     */
    public void searchAffectLocalVar(java.util.Set<String> retList) {
        if(children.length > 0) {
            if(children[0] instanceof Expression) {
                for(Node node : children) {
                    ((Expression)node).searchAffectLocalVar(retList);
                }
                return;
            }
        }
        getLeftExpr().searchAffectLocalVar(retList);
        if (getRightExpr() != null) {
            getRightExpr().searchAffectLocalVar(retList);
        }
    }

    /**
     * �������ʽ�п�����֮�н�����NPC��
     * @param retList
     */
    public void searchRelateNPC(java.util.Set<Integer> retList) {
        if(children.length > 0) {
            if(children[0] instanceof Expression) {
                for(Node node : children) {
                    ((Expression)node).searchRelateNPC(retList);
                }
                return;
            }
        }
        getLeftExpr().searchRelateNPC(retList);
        if (getRightExpr() != null) {
            getRightExpr().searchRelateNPC(retList);
        }
    }
    
    /**
     * �ѱ��ʽ���õ��ľֲ�������ת��Ϊ������
     */
    public void convertVarNameToIndex(String[] localVars) {
        if(children.length > 0) {
            if(children[0] instanceof Expression) {
                for(Node node : children) {
                    ((Expression)node).convertVarNameToIndex(localVars);
                }
                return;
            }
        }
        getLeftExpr().convertVarNameToIndex(localVars);
        if (getRightExpr() != null) {
            getRightExpr().convertVarNameToIndex(localVars);
        }
    }
    
    /**
     * ������ʽ�����������¼����롣��������������ĳ���¼����п��ܳ�������ô���ʽ���¼�����������
     * �¼���Ӧ������ֵ�������������ĳ������ܲ��������κ�һ���¼�����ô����������EVENT_MASK_CYCLE��
     * ��ʾ��ÿһ��CYCLE�����ܱ�������
     */
    public int getEventMask() {
        // �����ֵ���ڣ������޷��ж��������ʲôʱ����ܳ���������EVENT_MASK_CYCLE��
        if (getRightExpr() == null) {
            return getLeftExpr().getEventMask();
        } else {
            return PQEUtils.EVENT_MASK_CYCLE;
        }
    }
    
    /**
     * ����ʵ�ֱ��ʽ���ܶ�Ӧ��GTL���롣
     */
    public String toGTL() {
        if (getRightExpr() != null) {
            return "(" + getLeftExpr().toGTL() + " " + PQEUtils.op2str(op) + " " + getRightExpr().toGTL() + ")";
        } else {
            return getLeftExpr().toGTL();
        }
    }

    /**
     * �ѻ�ϸ�ʽ�ַ������õ���NPC���õĳ����ص����ø���һ�¡�
     * @throws Exception
     */
    public void validateMixedText(ProjectData proj) throws Exception {
        if(children.length > 0) {
            if(children[0] instanceof Expression) {
                for(Node node : children) {
                    ((Expression)node).validateMixedText(proj);
                }
                return;
            }
        }
        
        if (getRightExpr() == null) {
             getLeftExpr().validateMixedText(proj);
        } else {
            getLeftExpr().validateMixedText(proj);
             getRightExpr().validateMixedText(proj);
        }
    }
}
