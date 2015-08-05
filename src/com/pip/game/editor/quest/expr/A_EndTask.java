package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.RewardSetPropertyDescriptor;

/**
 * ���ʽģ�壺ǿ�ƽ�����
 * @author lighthu
 */
public class A_EndTask extends AbstractExpr {
    public int rewardID;
    
    /**
     * ����ָ��ȫ�ֱ�����ģ�塣
     * @param name ȫ�ֱ�������
     */
    public A_EndTask(QuestInfo qinfo) {
        questInfo = qinfo;
        if(qinfo instanceof QuestInfo)
        {
            if (qinfo != null && qinfo.owner.rewards.size() > 0) {
                rewardID = qinfo.owner.rewards.get(0).id;
            }
        }
    }
    
    /**
     * �ж����ģ����һ����������һ��������
     */
    public boolean isCondition() {
        return false;
    }

    /**
     * ȡ�����ɵı��ʽ��
     */
    public String getExpression() {
        return "EndTask(" + rewardID + ")";
    }

    /**
     * ��ģ�崴���µı��ʽƬ�Ρ�
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new A_EndTask(qinfo);
    }

    /**
     * ȡ��ģ�����ơ�
     */
    public String getName() {
        return "ǿ�ƽ�������";
    }

    /**
     * ת��Ϊ��Ȼ���Ա�ʾ��
     */
    public String toNatureString() {
        return "ǿ�ƽ������񣬽�����֧ " + rewardID;
    }

    /**
     * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("EndTask") && expr.getRightExpr() == null) {
            FunctionCall fc = expr.getLeftExpr().getFunctionCall();
            if (fc.getParamCount() != 1) {
                return null;
            }
            Expression param1 = fc.getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                A_EndTask ret = (A_EndTask)createNew(qinfo);
                ret.rewardID = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    // ������IPropertySource�ӿڵ�ʵ��

    /**
     * ȡ�����������������ģ����2���������ַ�������������������
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] { 
                new RewardSetPropertyDescriptor("rewardID", "������֧", questInfo)
        };
    }

    /**
     * ȡ�����Ե�ǰֵ��
     */
    public Object getPropertyValue(Object id) {
        if ("rewardID".equals(id)) {
            for (int i = 0; i < questInfo.owner.rewards.size(); i++) {
                if (questInfo.owner.rewards.get(i).id == rewardID) {
                    return i;
                }
            }
            return 0;
        }
        return null;
    }

    /**
     * �������Ե�ǰֵ��
     */
    public void setPropertyValue(Object id, Object value) {
        if ("rewardID".equals(id)) {
            int newValue = ((Integer)value).intValue();
            if (newValue >= 0 && newValue < questInfo.owner.rewards.size()) {
                newValue = questInfo.owner.rewards.get(newValue).id;
            } else {
                newValue = 0;
            }
            if (newValue != rewardID) {
                rewardID = newValue;
                fireValueChanged();
            }
        }
    }
}
