package com.pip.game.editor.quest.flow;

import java.util.*;

import com.pip.game.data.DataObject;
import com.pip.game.data.quest.*;
import com.pip.game.data.quest.pqe.*;
import com.pip.game.editor.quest.TemplateManager;
import com.pip.game.editor.quest.expr.*;

/**
 * �������̡�һ�����̴�һ�������жϿ�ʼ������������ͬ�������жϷ�֧������ÿ����֧��һ�������������
 * �������е�ĳЩ���⶯�������Լ��������жϷ�֧������һ�����̿��ܻ���Ҫ������������洢��
 * @author lighthu
 */
public class Flow {
    // ��ʼ�����ڵ�
    protected AbstractConditionNode startNode;

    /**
     * ȡ�ÿ�ʼ�����ڵ㡣
     * @return
     */
    public AbstractConditionNode getStartNode() {
        return startNode;
    }

    /**
     * ���ÿ�ʼ�����ڵ㡣 
     * @param startNode
     */
    public void setStartNode(AbstractConditionNode startNode) {
        this.startNode = startNode;
    }
    
    /**
     * ����������������1���������������洢��
     * @return
     */
    public void save(QuestInfo questInfo) {
        // ��һ�����������������ҳ����еĶ����飬ÿ���������Ӧ��һ����������
        ActionGroupIterator itor = new ActionGroupIterator();
        iterate(itor);
        List<ActionGroupNode> actionGroups = itor.getActionGroups();
        
        // ÿ������������һ��Trigger����
        for (ActionGroupNode group : actionGroups) {
            questInfo.triggers.add(saveTrigger(group));
        }
    }
    
    // ��һ�������鱣��Ϊһ����������
    private QuestTrigger saveTrigger(ActionGroupNode group) {
        // �Ӵ������ڵ㿪ʼ��ǰѰ�ң�ÿһ�����ڵ㶼��һ��������ֱ���ҵ�һ�����ڵ�
        List<AbstractConditionNode> conditionList = new ArrayList<AbstractConditionNode>();
        FlowNode parent = group.getParent();
        while (parent != null) {
            if (parent instanceof AbstractConditionNode) {
                AbstractConditionNode cn = (AbstractConditionNode)parent;
                conditionList.add(0, cn);
                if(cn instanceof ConditionNode) {
                    IExpr cond = ((ConditionNode)cn).getCondition();
                    if (cond instanceof C_CloseChat || cond instanceof C_CloseMessage ||
                        cond instanceof C_AnswerQuestion) {
                        break;
                    }
                }
               
            } else {
                throw new IllegalArgumentException("����������һ�������жϷ���");
            }
            parent = parent.getParent();
        }
        
        // �����е�����ƴ������Ϊ����������
        if (conditionList.size() == 0) {
            throw new IllegalArgumentException("����������һ�������жϷ���");
        }
        StringBuffer buf = new StringBuffer();
        for (AbstractConditionNode cn : conditionList) {
            if (buf.length() > 0) {
                buf.append(", ");
            }
            if(cn instanceof ConditionNode) {
                buf.append(((ConditionNode)cn).getCondition().getExpression());                
            } else if(cn instanceof ConditionGroupNode) {
                StringBuffer buf2 = new StringBuffer();
                buf2.append("(");
                List<ConditionNode> cns = ((ConditionGroupNode)cn).conditionNodes;
                for(ConditionNode cn2 : cns) {
                    if (buf2.length() > 1) {
                        buf2.append(", ");
                    }
                    buf2.append(((ConditionNode)cn2).getCondition().getExpression()); 
                }
                
                buf2.append(")");
                buf.append(buf2);
            }
        }
        QuestTrigger ret = new QuestTrigger();
        ret.condition = buf.toString();
        
        // �Ѷ������е����ж���ƴ������Ϊ������������Ҫ����@If���������
        buf.setLength(0);
        for (FlowNode an : group.getChildren()) {
            if (buf.length() > 0) {
                buf.append(", ");
            }
            if (an instanceof ActionNode) {
                buf.append(((ActionNode)an).getAction().getExpression());
            } else if (an instanceof ConditionNode) {
                ConditionNode cn = (ConditionNode)an;
                if (cn.getChildren().size() == 0) {
                    buf.append(cn.getCondition().getExpression());
                } else if (cn.getChildren().size() == 2 && cn.getChildren().get(0) instanceof ActionNode &&
                        cn.getChildren().get(1) instanceof ActionNode) {
                    buf.append("If(");
                    buf.append(cn.getCondition().getExpression());
                    buf.append(", ");
                    buf.append(((ActionNode)cn.getChildren().get(0)).getAction().getExpression());
                    buf.append(", ");
                    buf.append(((ActionNode)cn.getChildren().get(1)).getAction().getExpression());
                    buf.append(")");
                } else {
                    throw new IllegalArgumentException("�������е���������������������ӽڵ㡣");
                }
            }
        }
        ret.action = buf.toString();
        return ret;
    }
    
    /**
     * ����һ������ű����Ѵ�����ת��Ϊһ�������������̡�
     * @param questInfo ������Ϣ�����Ǳ�ʶ��Ĵ����������triggers�������Ƴ���
     * @return ���̶�������顣
     */
    public static List<Flow> parseQuest(DataObject questInfo, boolean flatMode) {
        List<Flow> retList = new ArrayList<Flow>();
        while (true) {
            // �ҳ�һ�����̣�����Ҳ������򷵻ء�
            int oldCount = retList.size();
            Flow flow = recognize(questInfo, flatMode, retList);
            if (flow == null) {
                break;
            }
            
            // ����ҵ�һ�����̣����뷵��������
            retList.add(oldCount, flow);
        }
        
        // ɨ�����������п������ӽڵ��ActionNode���Զ��������ӽڵ㡣
        Flow[] fs = new Flow[retList.size()];
        retList.toArray(fs);
        for (Flow flow : fs) {
            UpdateChildrenIterator itor = new UpdateChildrenIterator(flatMode, retList);
            flow.iterate(itor);
        }
        return retList;
    }
    
    /**
     * ��һ�鴥������ʶ���һ����������
     * @param questInfo ������Ϣ����ʶ��Ĵ��������Ӵ������б���ɾ��
     * @return һ�����ܰ���������������������̡����һ�����̶�ʶ�𲻳���������null��
     */
    public static Flow recognize(DataObject questInfo2, boolean flatMode, List<Flow> flows) {
        QuestInfo questInfo = (QuestInfo)questInfo2;
        if (questInfo.triggers.size() == 0) {
            return null;
        }
        
        // ��ѡ��һ���ܹ���Ϊ������ʼ�Ĵ�������ʼʶ��Ҫ��Ϊһ�����̵���ʼ���������������Ҫ������һ��
        // �����������������������C_CloseMessage��C_CloseChat��C_AnswerQuestion�е�һ����
        QuestTrigger startTrigger = null;
        TemplateManager tmgr = questInfo.owner.owner.config.templateManager;
        for (QuestTrigger t : questInfo.triggers) {
            ExpressionList exprs = ExpressionList.fromString(t.condition);
            if (exprs.getExprCount() == 0) {
                continue;
            }
            int numChildren = exprs.jjtGetNumChildren();
            boolean isContinue = false;
            for(int i=0; i<numChildren; i++) {
                Node node = exprs.getExpr(i).jjtGetChild(0);
                if(node instanceof Expr0) {
                    IExpr expr = tmgr.recognize(exprs.getExpr(i), questInfo);
                    if (!flatMode && (expr instanceof C_CloseMessage || expr instanceof C_CloseChat || expr instanceof C_AnswerQuestion)) {
                        isContinue = true;
                    } else {
                        isContinue = false;
                        break;
                    }
                } else {                    
                    IExpr expr = tmgr.recognize((Expression )exprs.getExpr(i).jjtGetChild(0), questInfo);
                    if (!flatMode && (expr instanceof C_CloseMessage || expr instanceof C_CloseChat || expr instanceof C_AnswerQuestion)) {
                        isContinue = true;
                    }
                    
                }
            }
            
            if(isContinue) {
                continue;
            }
            
            startTrigger = t;
            break;
        }
        
        // ������еĴ���������������Ϊ������ʼ�ڵ������������Щ��������������
        if (startTrigger == null) {
            return null;
        }
        
        // ���ҵ�����ʼ����������Ϊһ������
        Flow basicFlow = recognize(startTrigger, questInfo);
        questInfo.triggers.remove(startTrigger);
        
        // ѭ��������������������Ƿ��������̽��кϲ���ֱ���Ҳ����ɺϲ��Ĵ�����Ϊֹ
        while (true) {
            int addCount = 0;
            for (int i = 0; i < questInfo.triggers.size(); i++) {
                QuestTrigger t = questInfo.triggers.get(i);
                Flow subFlow = recognize(t, questInfo);
                if (mergeFlow(basicFlow, subFlow, flatMode)) {
                    addCount++;
                    questInfo.triggers.remove(i);
                    i--;
                } else if (mergeFlow(subFlow, basicFlow, flatMode)) {
                    addCount++;
                    basicFlow = subFlow;
                    questInfo.triggers.remove(i);
                    i--;
                }
            }
            if (addCount == 0) {
                break;
            }
        }
        return basicFlow;
    }
    
    /**
     * ��һ��Trigger������ʶ���һ�����̶������е��������������������ָ��һ�������飬�������д�����
     * ������@If�������ñ����⴦��
     * @param trigger
     * @param questInfo
     * @return ����޷�ʶ�𣬷���null��
     */
    public static Flow recognize(QuestTrigger trigger, QuestInfo questInfo) {
        Flow ret = new Flow();
        
        // ����Ƿ���������û�������Ĵ������ǲ��ܴ��ڵ�
        ExpressionList exprs = ExpressionList.fromString(trigger.condition);
        if (exprs.getExprCount() == 0) {
            return null;
        }
        
        // ������������������, ��ʼ�ڵ������һ��ڵ�
        AbstractConditionNode currentNode = null;
        AbstractConditionNode startNode = null;
        int conditionCount = exprs.getExpr(0).jjtGetNumChildren();
        Node node = exprs.getExpr(0).jjtGetChild(0);
        TemplateManager tmgr = questInfo.owner.owner.config.templateManager;
        if(node instanceof Expr0) {
            startNode = new ConditionNode(ret);
            ((ConditionNode)startNode).setCondition(tmgr.recognize(exprs.getExpr(0), questInfo));
            currentNode = startNode;
        } else {
            startNode = new ConditionGroupNode(ret);
            for(int i=0; i<conditionCount; i++) {
                ConditionNode cn = new ConditionNode(ret);
                cn.setCondition(tmgr.recognize((Expression)exprs.getExpr(0).jjtGetChild(i), questInfo));
                ((ConditionGroupNode)startNode).conditionNodes.add(cn);
                cn.setParent(startNode);                
            }
            currentNode = startNode;
        }     

        for (int i = 1; i < exprs.getExprCount(); i++) {
            AbstractConditionNode newNode;
            
            conditionCount = exprs.getExpr(i).jjtGetNumChildren();
            Node node2 = exprs.getExpr(i).jjtGetChild(0);
            if(node2 instanceof Expr0) {
                newNode = new ConditionNode(ret);
                ((ConditionNode)newNode).setCondition(tmgr.recognize(exprs.getExpr(i), questInfo));
            } else {
                newNode = new ConditionGroupNode(ret);
                for(int j=0; j<conditionCount; j++) {
                    ConditionNode cn = new ConditionNode(ret);
                    cn.setCondition(tmgr.recognize((Expression)exprs.getExpr(i).jjtGetChild(j), questInfo));
                    ((ConditionGroupNode)newNode).conditionNodes.add(cn);
                    cn.setParent(newNode);
                }
            }
            
            currentNode.addChild(newNode);
            currentNode = newNode;
        }
        
        // �����еĶ���������һ����������ȥ�����������һ�������ĺ���
        ActionGroupNode groupNode = new ActionGroupNode(ret);
        currentNode.addChild(groupNode);
        exprs = ExpressionList.fromString(trigger.action);
        for (int i = 0; i < exprs.getExprCount(); i++) {
            Expression expr = exprs.getExpr(i);
            
            // @If�������⴦��
            if (expr.getRightExpr() == null && expr.getLeftExpr().type == Expr0.TYPE_FUNC) {
                FunctionCall fcall = expr.getLeftExpr().getFunctionCall();
                if ("If".equals(fcall.funcName) && fcall.getParamCount() == 3) {
                    Expression condExpr = fcall.getParam(0);
                    Expression trueAction = fcall.getParam(1);
                    Expression falseAction = fcall.getParam(2);
                    
                    ConditionNode ifNode = new ConditionNode(ret);
                    ifNode.setCondition(tmgr.recognize(condExpr, questInfo));
                    groupNode.addChild(ifNode);
                    
                    ActionNode trueNode = new ActionNode(ret);
                    trueNode.setAction(tmgr.recognize(trueAction, questInfo));
                    ifNode.addChild(trueNode);

                    ActionNode falseNode = new ActionNode(ret);
                    falseNode.setAction(tmgr.recognize(falseAction, questInfo));
                    ifNode.addChild(falseNode);
                    continue;
                }
            }
            
            // �����ڵ�һ����Ϊ��������
            ActionNode newAction = new ActionNode(ret);
            newAction.setAction(tmgr.recognize(expr, questInfo));
            groupNode.addChild(newAction);
        }
        
        // ����һ��Flow��������ʼ������Ϊ���ڵ㡣        
        ret.setStartNode(startNode);
        return ret;
    }
    
    /**
     * ���԰�һ���µ����̹ҽӵ����е������С��ҽӵ���������¼��֣�
     * 1. �����̵���ʼ1����������������������ͬ����������¿ɽ��������һ����ͬ���̺�����֧��
     * 2. �����̵�����������������������ͬ����������°Ѷ�����ϲ����ɡ�
     * 3. ��������C_CloseChat��C_CloseMessage��C_AnswerQuestion���е�һ��������ʼ����֪ͨIDƥ��
     * �������̵Ķ������ж�Ӧ��һ��A_Chat��A_Message��A_Question��������������°��µ���������
     * �ҽӵ��˶�����
     * @param basicFlow ��������
     * @param newFlow ���ϲ�������
     * @return ����ϲ��ɹ�������true�����򷵻�false��
     */
    private static boolean mergeFlow(Flow basicFlow, Flow newFlow, boolean flatMode) {
//        if(newFlow.getStartNode() instanceof ConditionGroupNode ||
//                basicFlow.getStartNode() instanceof ConditionGroupNode) {
//            return false;
//        }
        AbstractConditionNode newFlowConditionNode = (AbstractConditionNode)newFlow.getStartNode();
        AbstractConditionNode basicFlowConditionNode = (AbstractConditionNode)basicFlow.getStartNode();
        
        boolean merge = false;
        if(newFlowConditionNode instanceof ConditionNode) {
            IExpr cond = ((ConditionNode)newFlowConditionNode).getCondition();
            if (cond instanceof C_CloseChat) {
                // C_CloseChat����ƥ��A_Chat����
                int notifyID = ((C_CloseChat)cond).constant;
                FlowNode chatNode = basicFlow.findChat(notifyID);
                if (chatNode != null) {
                    chatNode.addChild(newFlow.getStartNode());
                    return true;
                }
            } else if (cond instanceof C_CloseMessage) {
                // C_CloseMessage����ƥ��A_Message����
                int notifyID = ((C_CloseMessage)cond).constant;
                FlowNode messageNode = basicFlow.findMessage(notifyID);
                if (messageNode != null) {
                    messageNode.addChild(newFlow.getStartNode());
                    return true;
                }
            } else if (cond instanceof C_AnswerQuestion && !flatMode) {
                // C_AnswerQuestion����ƥ��A_Question����
                int notifyID = ((C_AnswerQuestion)cond).param1;
                FlowNode questionNode = basicFlow.findQuestion(notifyID);
                if (questionNode != null) {
                    questionNode.addChild(newFlow.getStartNode());
                    return true;
                }
            } else {
                merge = true;
            }
        } else {
            merge = true;
        }
        if(merge) {
            // ��������3������������ȽϿ�ʼ�����Ƿ�ƥ��
            AbstractConditionNode newCond = newFlowConditionNode;
            AbstractConditionNode existCond = basicFlowConditionNode;
            if (isConditionEqual(newCond, existCond)) {
                // ѭ���Ƚϣ�ֱ���ҵ���һ������ȵĽڵ�Ϊֹ
                while (true) {
                    // �����һ���ڵ㻹�������������������Ƿ��ܺ��������̵���һ������ƥ��
                    if (newCond.getChildren().size() == 1 && newCond.getChildren().get(0) instanceof AbstractConditionNode) {
                        AbstractConditionNode newCond2 = (AbstractConditionNode)newCond.getChildren().get(0);
                        boolean found = false;
                        for (int i = 0; i < existCond.getChildren().size(); i++) {
                            FlowNode n = existCond.getChildren().get(i);
                            if (n instanceof AbstractConditionNode && isConditionEqual((AbstractConditionNode)n, newCond2)) {
                                // ƥ���ˣ��������²���
                                found = true;
                                existCond = (AbstractConditionNode)n;
                                break;
                            }
                        }
                        if (found) {
                            newCond = newCond2;
                            continue;
                        }
                    }
                    
                    // û��ƥ���ˡ�����¾ɽڵ����һ�ڵ㶼�Ƕ������ˣ���ϲ����������飬������½ڵ�ĺ����ڵ�
                    // �ҳɾɽڵ��һ�����ӽڵ㡣
                    if (newCond.getChildren().size() == 1 && newCond.getChildren().get(0) instanceof ActionGroupNode) {
                        ActionGroupNode newGroup = (ActionGroupNode)newCond.getChildren().get(0);
                        for (int i = 0; i < existCond.getChildren().size(); i++) {
                            FlowNode n = existCond.getChildren().get(i);
                            if (n instanceof ActionGroupNode) {
                                // ���Ƕ����飬�ϲ�����ɺϲ�
                                for (FlowNode nn : newGroup.getChildren()) {
                                    nn.setParent(n);
                                }
                                ((ActionGroupNode)n).getChildren().addAll(newGroup.getChildren());
                                return true;
                            }
                        }
                    }
                    for (FlowNode child : newCond.getChildren()) {
                        existCond.addChild(child);
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    // �������в���ָ��֪ͨID�ĶԻ�������
    private FlowNode findChat(int nid) {
        NotifyActionIterator itor = new NotifyActionIterator(A_Chat.class, nid);
        iterate(itor);
        return itor.getFoundNode();
    }
    
    // �������в���ָ��֪ͨID����ʾ��Ϣ������
    private FlowNode findMessage(int nid) {
        NotifyActionIterator itor = new NotifyActionIterator(A_Message.class, nid);
        iterate(itor);
        return itor.getFoundNode();
    }

    // �������в���ָ��֪ͨID�����ʶ�����
    private FlowNode findQuestion(int nid) {
        NotifyActionIterator itor = new NotifyActionIterator(A_Question.class, nid);
        iterate(itor);
        return itor.getFoundNode();
    }
    
    /**
     * �������������е����ʽڵ㡣
     */
    public List<A_Question> findQuestions() {
        QuestionIterator itor = new QuestionIterator();
        iterate(itor);
        return itor.getQuestions();
    }
    
    /**
     * �������������еĶ�����ڵ㡣
     */
    public List<ActionGroupNode> findActionGroups() {
        ActionGroupIterator itor = new ActionGroupIterator();
        iterate(itor);
        return itor.getActionGroups();
    }
    
    /**
     * �ж����������Ƿ��Ч��
     * @param cond1
     * @param cond2
     * @return
     */
    private static boolean isConditionEqual(AbstractConditionNode cond1, AbstractConditionNode cond2) {
        if(cond1 instanceof ConditionNode && cond2 instanceof ConditionNode) {
            return ((ConditionNode)cond1).getCondition().getExpression().equals(((ConditionNode)cond2).getCondition().getExpression()); 
        } else if(cond1 instanceof ConditionGroupNode && cond2 instanceof ConditionGroupNode) {
            int count = ((ConditionGroupNode)cond1).conditionNodes.size();
            if(count != ((ConditionGroupNode)cond2).conditionNodes.size()) {
                return false;
            }
            
            for(int i=0; i<count; i++) {
                ConditionNode cond11 = ((ConditionGroupNode)cond1).conditionNodes.get(i);
                ConditionNode cond22 = ((ConditionGroupNode)cond2).conditionNodes.get(i);
                
                if(cond11.getCondition().getExpression().equals(cond22.getCondition().getExpression()) == false) {
                    return false;
                }
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * ��ȱ���һ�����̵����нڵ㡣
     * @param itor
     */
    public void iterate(IFlowIterator itor) {
        iterate(startNode, itor);
    }
    
    /**
     * ��ȱ���һ�����������ӽ�㡣
     * @param startNode
     * @param itor
     */
    public static void iterate(FlowNode startNode, IFlowIterator itor) {
        List<FlowNode> stack = new ArrayList<FlowNode>();
        stack.add(startNode);
        while (stack.size() > 0) {
            FlowNode n = stack.remove(0);
            if(n instanceof ConditionGroupNode) {
                itor.walk(n);
                for(ConditionNode conditionNode : ((ConditionGroupNode)n).conditionNodes) {
                    if (itor.walk(conditionNode)) {
                        return;
                    }
                }
            } else {
                if (itor.walk(n)) {
                    return;
                }                
            }
            stack.addAll(n.getChildren());
        }
    }

    /**
     * ���ڲ������������ж������ö������
     * @author lighthu
     */
    class ActionGroupIterator implements IFlowIterator {
        List<ActionGroupNode> actionGroups = new ArrayList<ActionGroupNode>();
        
        public boolean walk(FlowNode node) {
            if (node instanceof ActionGroupNode && ((ActionGroupNode)node).getChildren().size() > 0) {
                actionGroups.add((ActionGroupNode)node);
            }
            return false;
        }
        
        public List<ActionGroupNode> getActionGroups() {
            return actionGroups;
        }
    }
    
    /**
     * ���ڲ���������ָ��֪ͨID�Ķ�����ö������
     * @author lighthu
     */
    class NotifyActionIterator implements IFlowIterator {
        FlowNode foundNode;
        Class nodeClass;
        int notifyID;
        
        public NotifyActionIterator(Class cls, int notifyID) {
            nodeClass = cls;
            this.notifyID = notifyID;
        }
        
        public boolean walk(FlowNode node) {
            if (node instanceof ActionNode) {
                IExpr expr = ((ActionNode)node).getAction();
                if (nodeClass.isInstance(expr)) {
                    if (((AbstractNotifyAction)expr).notifyID == notifyID) {
                        foundNode = node;
                        return false;
                    }
                }
            }
            return false;
        }
        
        public FlowNode getFoundNode() {
            return foundNode;
        }
    }
    
    /**
     * �����Զ���������ActionNode�ӽڵ��ö������
     * @author lighthu
     */
    static class UpdateChildrenIterator implements IFlowIterator {
        private boolean flatMode;
        private List<Flow> flows;
        
        public UpdateChildrenIterator(boolean flatMode, List<Flow> flows) {
            this.flatMode = flatMode;
            this.flows = flows;
        }
        
        public boolean walk(FlowNode node) {
            if (node instanceof ActionNode) {
                ((ActionNode)node).updateChildren(flatMode, flows);
            }
            return false;
        }
    }
    
    /**
     * ���ڲ����������������ʽڵ��ö������
     * @author lighthu
     */
    static class QuestionIterator implements IFlowIterator {
        List<A_Question> questions = new ArrayList<A_Question>();
        
        public boolean walk(FlowNode node) {
            if (node instanceof ActionNode) {
                IExpr expr = ((ActionNode)node).getAction();
                if (expr instanceof A_Question) {
                    questions.add((A_Question)expr);
                }
            }
            return false;
        }
        
        public List<A_Question> getQuestions() {
            return questions;
        }
    }
}
