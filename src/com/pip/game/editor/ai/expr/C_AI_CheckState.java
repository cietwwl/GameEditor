package com.pip.game.editor.ai.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.quest.expr.AbstractExpr;
import com.pip.game.editor.quest.expr.IExpr;

/**
 * ���ʽģ�壬������״̬��
 * @author lighthu
 */
public class C_AI_CheckState extends AbstractExpr {
    public int state = 1;
    public boolean checkTrue = true;

    /**
     * ����ָ��ȫ�ֱ�����ģ�塣
     * @param name ȫ�ֱ�������
     */
    public C_AI_CheckState() {
    }
    
    /**
     * �ж����ģ����һ����������һ��������
     */
    public boolean isCondition() {
        return true;
    }

    /**
     * ȡ�����ɵı��ʽ��
     */
    public String getExpression() {
        String left = "AI_CheckState(" + state + ")";
        if (checkTrue) {
            return left;
        } else {
            return left + " == false";
        }
    }

    /**
     * ��ģ�崴���µı��ʽƬ�Ρ�
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new C_AI_CheckState();
    }

    /**
     * ȡ��ģ�����ơ�
     */
    public String getName() {
        return "������״̬...";
    }

    private String stateToStr() {
        switch (state) {
        case 1:
            return "��Ĭ";
        case 2:
            return "����";
        case 3:
            return "ѣ��";
        case 4:
            return "�־�";
        }
        return "δ֪";
    }
    
    /**
     * ת��Ϊ��Ȼ���Ա�ʾ��
     */
    public String toNatureString() {
        if (checkTrue) {
            return "���ﴦ��״̬��" + stateToStr();
        } else {
            return "���ﲻ����״̬��" + stateToStr();
        }
    }

    /**
     * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("AI_CheckState")) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 1) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                C_AI_CheckState ret = (C_AI_CheckState)createNew(qinfo);
                ret.state = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                if (expr.getRightExpr() == null) {
                    return ret;
                } else if (expr.getRightExpr().type == Expr0.TYPE_IDENTIFIER && expr.getRightExpr().value.equals("false")) {
                    ret.checkTrue = false;
                    return ret;
                }
            }
        }
        return null;
    }

    // ������IPropertySource�ӿڵ�ʵ��

    /**
     * ȡ�����������������ģ����1��������������
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] {
                new ComboBoxPropertyDescriptor("state", "״̬", new String[] { "��Ĭ", "����", "ѣ��", "�־�" }),
                new ComboBoxPropertyDescriptor("checkTrue", "���ɹ�", new String[] { "��", "��" })
        };
    }

    /**
     * ȡ�����Ե�ǰֵ��
     */
    public Object getPropertyValue(Object id) {
        if ("state".equals(id)) {
            return new Integer(state - 1);
        } else if ("checkTrue".equals(id)) {
            return checkTrue ? 0 : 1;
        }
        return null;
    }

    /**
     * �������Ե�ǰֵ��
     */
    public void setPropertyValue(Object id, Object value) {
        if ("state".equals(id)) {
            int newValue = ((Integer)value).intValue() + 1;
            if (newValue != state) {
                state = newValue;
                fireValueChanged();
            }
        } else if ("checkTrue".equals(id)) {
            boolean newValue = ((Integer)value).intValue() == 0;
            if (newValue != checkTrue) {
                checkTrue = newValue;
                fireValueChanged();
            }
        }
    }
}
