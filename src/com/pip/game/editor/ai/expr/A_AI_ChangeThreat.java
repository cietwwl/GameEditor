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
 * ���ʽģ�壬�ı�Ŀ����вֵ��
 * @author lighthu
 */
public class A_AI_ChangeThreat extends AbstractExpr {
    public int percent;

    /**
     * ����ָ��ȫ�ֱ�����ģ�塣
     * @param name ȫ�ֱ�������
     */
    public A_AI_ChangeThreat() {
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
        return "AI_ChangeThreat(" + percent + ")";
    }

    /**
     * ��ģ�崴���µı��ʽƬ�Ρ�
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new A_AI_ChangeThreat();
    }

    /**
     * ȡ��ģ�����ơ�
     */
    public String getName() {
        return "�ı�Ŀ����вֵ...";
    }

    /**
     * ת��Ϊ��Ȼ���Ա�ʾ��
     */
    public String toNatureString() {
        return "�ı�Ŀ����вֵ" + percent + "%";
    }
    
    /**
     * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("AI_ChangeThreat") && expr.getRightExpr() == null) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 1) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                A_AI_ChangeThreat ret = (A_AI_ChangeThreat)createNew(qinfo);
                ret.percent = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
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
                new TextPropertyDescriptor("percent", "�ٷֱ�")
        };
    }

    /**
     * ȡ�����Ե�ǰֵ��
     */
    public Object getPropertyValue(Object id) {
        if ("percent".equals(id)) {
            return String.valueOf(percent);
        }
        return null;
    }

    /**
     * �������Ե�ǰֵ��
     */
    public void setPropertyValue(Object id, Object value) {
        if ("percent".equals(id)) {
            int newValue;
            try {
                newValue = Integer.parseInt((String)value);
                if (newValue < -100) {
                    newValue = -100;
                }
                if (newValue > 1000) {
                    newValue = 1000;
                }
            } catch (Exception e) {
                return;
            }
            if (newValue != percent) {
                percent = newValue;
                fireValueChanged();
            }
        }
    }
}
