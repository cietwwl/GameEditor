package com.pip.game.editor.ai.expr;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.editor.property.SkillPropertyDescriptor;
import com.pip.game.editor.quest.expr.AbstractExpr;
import com.pip.game.editor.quest.expr.IExpr;
import com.pip.util.Utils;

/**
 * ���ʽģ�壬ע�ᶨʱ����
 * @author lighthu
 */
public class A_AI_RegisterTimer extends AbstractExpr {
    public String id = "timer1";
    public int startTime = 1000;
    public int interval = 3000;

    /**
     * ����ָ��ȫ�ֱ�����ģ�塣
     * @param name ȫ�ֱ�������
     */
    public A_AI_RegisterTimer() {
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
        return "AI_RegisterTimer(\"" + id + "\"," + startTime + "," + interval + ")";
    }

    /**
     * ��ģ�崴���µı��ʽƬ�Ρ�
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new A_AI_RegisterTimer();
    }

    /**
     * ȡ��ģ�����ơ�
     */
    public String getName() {
        return "ע�ᶨʱ��...";
    }

    /**
     * ת��Ϊ��Ȼ���Ա�ʾ��
     */
    public String toNatureString() {
        return "ע�ᶨʱ��" + id;
    }
    
    /**
     * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("AI_RegisterTimer") && expr.getRightExpr() == null) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 3) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            Expression param2 = expr.getLeftExpr().getFunctionCall().getParam(1);
            Expression param3 = expr.getLeftExpr().getFunctionCall().getParam(2);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_STRING &&
                    param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param3.getRightExpr() == null && param3.getLeftExpr().type == Expr0.TYPE_NUMBER
                    ) {
                A_AI_RegisterTimer ret = (A_AI_RegisterTimer)createNew(qinfo);
                ret.id = PQEUtils.translateStringConstant(param1.getLeftExpr().value);
                ret.startTime = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
                ret.interval = PQEUtils.translateNumberConstant(param3.getLeftExpr().value);
                return ret;
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
                new TextPropertyDescriptor("id", "��ʱ������"),
                new TextPropertyDescriptor("startTime", "��ʼʱ��(����)"),
                new TextPropertyDescriptor("interval", "���(����)")
        };
    }

    /**
     * ȡ�����Ե�ǰֵ��
     */
    public Object getPropertyValue(Object id) {
        if ("id".equals(id)) {
            return this.id;
        } else if ("startTime".equals(id)) {
            return String.valueOf(startTime);
        } else if ("interval".equals(id)) {
            return String.valueOf(interval);
        }
        return null;
    }

    /**
     * �������Ե�ǰֵ��
     */
    public void setPropertyValue(Object id, Object value) {
        if ("id".equals(id)) {
            String newValue = (String)value;
            if (newValue.length() != 0 && !newValue.equals(this.id)) {
                this.id = newValue;
                fireValueChanged();
            }
        } else if ("startTime".equals(id)) {
            int newValue;
            try {
                newValue = Integer.parseInt((String)value);
                if (newValue < 0) {
                    newValue = 0;
                }
            } catch (Exception e) {
                return;
            }
            if (newValue != startTime) {
                startTime = newValue;
                fireValueChanged();
            }
        } else if ("interval".equals(id)) {
            int newValue;
            try {
                newValue = Integer.parseInt((String)value);
                if (interval < 1) {
                    interval = 1;
                }
            } catch (Exception e) {
                return;
            }
            if (newValue != interval) {
                interval = newValue;
                fireValueChanged();
            }
        }
    }
}
