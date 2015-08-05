package com.pip.game.editor.quest.flow;

import java.util.List;

import com.pip.game.editor.quest.expr.*;

/**
 * ���ڱ�ʾһ�������Ľڵ㡣
 * @author lighthu
 */
public class ActionNode extends FlowNode {
    public ActionNode(Flow flow) {
        super(flow);
    }
    
    // ����ڵ��Ӧ�Ķ������ʽ
    protected IExpr action;
    
    /**
     * ���ö������ʽ��
     * @param expr
     */
    public void setAction(IExpr expr) {
        action = expr;
    }
    
    /**
     * ȡ�ö������ʽ��
     * @return
     */
    public IExpr getAction() {
        return action;
    }
    
    /**
     * ת��Ϊ�ַ�����
     */
    public String toString() {
        return action.toNatureString();
    }
    
    /**
     * ȡ������ڵ����ӵ�е�����ӽڵ�����
     */
    public int getMaxChildren() {
        // Message��Chat�������������֪ͨID���Ϳ���ӵ��һ���ӽڵ㡣Question�������������֪ͨID���Ϳ���ӵ
        // �к�ѡ����Ŀ��ͬ�������ֽڵ㡣
        if (action instanceof A_Chat) {
            A_Chat chatAction = (A_Chat)action;
            if (chatAction.notifyID != -1) {
                return 1;
            }
        } else if (action instanceof A_Message) {
            A_Message messageAction = (A_Message)action;
            if (messageAction.notifyID != -1) {
                return 1;
            }
        } else if (action instanceof A_Question) {
            A_Question questionAction = (A_Question)action;
            if (questionAction.notifyID != -1) {
                return questionAction.options.split("\n").length;
            }
        }
        return 0;
    }
    
    /**
     * ����һ���ӽڵ㡣����ӽڵ㲻��������������null��
     * @param index
     * @return
     */
    public IExpr createChild(int index) {
        // Message��Chat�������������֪ͨID���Ϳ���ӵ��һ���ӽڵ㡣Question�������������֪ͨID���Ϳ���ӵ
        // �к�ѡ����Ŀ��ͬ�������ֽڵ㡣
        if (action instanceof A_Chat) {
            A_Chat chatAction = (A_Chat)action;
            if (chatAction.notifyID != -1 && index == 0) {
                C_CloseChat ret = new C_CloseChat();
                ret.constant = chatAction.notifyID;
                return ret;
            }
        } else if (action instanceof A_Message) {
            A_Message messageAction = (A_Message)action;
            if (messageAction.notifyID != -1 && index == 0) {
                C_CloseMessage ret = new C_CloseMessage();
                ret.constant = messageAction.notifyID;
                return ret;
            }
        } else if (action instanceof A_Question) {
            A_Question questionAction = (A_Question)action;
            if (questionAction.notifyID != -1) {
                C_AnswerQuestion ret = new C_AnswerQuestion();
                ret.param1 = questionAction.notifyID;
                ret.param2 = index;
                return ret;
            }
        }
        return null;
    }
    
    /**
     * ���µ����ӽڵ�����Ӧ��ǰ�����ı仯��
     */
    public void updateChildren(boolean flatMode, List<Flow> flows) {
        // Message��Chat�������������֪ͨID���Ϳ���ӵ��һ���ӽڵ㡣Question�������������֪ͨID���Ϳ���ӵ
        // �к�ѡ����Ŀ��ͬ�������ֽڵ㡣
        if (action instanceof A_Chat) {
            A_Chat chatAction = (A_Chat)action;
            if (chatAction.notifyID == -1) {
                children.clear();
                return;
            }
            while (children.size() > 1) {
                children.remove(children.size() - 1);
            }
            if (children.size() == 0) {
                ConditionNode newChild = new ConditionNode(flow);
                newChild.setCondition(new C_CloseChat());
                newChild.addChild(new ActionGroupNode(flow));
                addChild(newChild);
            }
            C_CloseChat chatCond = (C_CloseChat)((ConditionNode)children.get(0)).getCondition();
            chatCond.constant = chatAction.notifyID;
        } else if (action instanceof A_Message) {
            A_Message msgAction = (A_Message)action;
            if (msgAction.notifyID == -1) {
                children.clear();
                return;
            }
            while (children.size() > 1) {
                children.remove(children.size() - 1);
            }
            if (children.size() == 0) {
                ConditionNode newChild = new ConditionNode(flow);
                newChild.setCondition(new C_CloseMessage());
                newChild.addChild(new ActionGroupNode(flow));
                addChild(newChild);
            }
            C_CloseMessage msgCond = (C_CloseMessage)((ConditionNode)children.get(0)).getCondition();
            msgCond.constant = msgAction.notifyID;
        } else if (action instanceof A_Question) {
            A_Question qstAction = (A_Question)action;
            if (!flatMode) {
                // �ڷ�̯ƽģʽ�£��������ʵĻش���������ʽڵ���ӽڵ㡣
                
                // ���notifyID�޸�Ϊ-1����ô���лش�ڵ㶼Ҫɾ����
                if (qstAction.notifyID == -1) {
                    children.clear();
                    return;
                }
                
                // ÿһ�������Ӧһ���ش�ڵ㣬�����ѭ���ҳ�ƥ���
                int optionCount = qstAction.options.split("\n").length;
                boolean[] flags = new boolean[optionCount];
                for (int i = 0; i < children.size(); i++) {
                    IExpr expr = ((ConditionNode)children.get(i)).getCondition();
                    if (!(expr instanceof C_AnswerQuestion) || ((C_AnswerQuestion)expr).param2 >= optionCount) {
                        children.remove(i);
                        i--;
                    } else {
                        flags[((C_AnswerQuestion)expr).param2] = true;
                        ((C_AnswerQuestion)expr).param1 = qstAction.notifyID;
                    }
                }
                
                // ����û��ƥ��ģ������µĽڵ㡣
                for (int i = 0; i < optionCount; i++) {
                    if (!flags[i]) {
                        ConditionNode newChild = new ConditionNode(flow);
                        C_AnswerQuestion aq = new C_AnswerQuestion();
                        aq.param1 = qstAction.notifyID;
                        aq.param2 = i;
                        newChild.setCondition(aq);
                        newChild.addChild(new ActionGroupNode(flow));
                        addChild(newChild);
                    }
                }
            } else {
                // ��̯ƽģʽ�£��������ʵĻش���һ��������Flow��
                
                // ���notifyID�޸�Ϊ-1���޺����ڵ㡣
                if (qstAction.notifyID == -1) {
                    return;
                }
                
                // ÿһ�������Ӧһ���ش�Flow�������ѭ���ҳ�ƥ���
                int optionCount = qstAction.options.split("\n").length;
                boolean[] flags = new boolean[optionCount];
                for (int i = 0; i < flows.size(); i++) {
                    if (flows.get(i).getStartNode() instanceof ConditionNode) {
                        IExpr expr = ((ConditionNode)flows.get(i).getStartNode()).getCondition();
                        if (expr instanceof C_AnswerQuestion) {
                            C_AnswerQuestion ca = (C_AnswerQuestion)expr;
                            if (ca.param1 == qstAction.notifyID) {
                                if (ca.param2 >= optionCount) {
                                    flows.remove(i);
                                    i--;
                                } else {
                                    flags[ca.param2] = true;
                                }
                            }
                        }
                    }
                }
                
                // ����û��ƥ��ģ������µĽڵ㡣
                for (int i = 0; i < optionCount; i++) {
                    if (!flags[i]) {
                        Flow newFlow = new Flow();
                        ConditionNode newChild = new ConditionNode(newFlow);
                        C_AnswerQuestion aq = new C_AnswerQuestion();
                        aq.param1 = qstAction.notifyID;
                        aq.param2 = i;
                        newChild.setCondition(aq);
                        newChild.addChild(new ActionGroupNode(newFlow));
                        newFlow.setStartNode(newChild);
                        flows.add(newFlow);
                    }
                }
            }
        }
    }

    /**
     * �ж�һ���µı��ʽģ���Ƿ��ܹ����뱾�ڵ��
     * @param expr
     * @return
     */
    public boolean canAccept(IExpr expr) {
        // ֻ����һ���������һ���¶����Ͻ�ռλ�����ڵ�
        if ((action instanceof A_Empty) && !expr.isCondition()) {
            return true;
        }
        
        return false;
    }
    
    /**
     * ��һ�����ʽģ����뱾�ڵ��
     * @param expr
     * @param x ����λ�ã������Viewer��
     * @param y ����λ�ã������Viewer��
     * @return �������ʧ�ܣ�����false��
     */
    public boolean accept(IExpr expr, int x, int y, boolean flatMode, List<Flow> flows) {
        // �����ǰ�ڵ���һ��ռλ������ֱ���滻���ʽ
        if ((action instanceof A_Empty) && !expr.isCondition()) {
            action = expr;
            updateChildren(flatMode, flows);
            return true;
        }
        
        return false;
    }
}
