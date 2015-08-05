package com.pip.game.editor.ai.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
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

/**
 * ���ʽģ�壬����Ƿ����㹻��ʩ��ĳ���ܡ�
 * @author lighthu
 */
public class C_AI_CanCastSkill extends AbstractExpr {
    public int skillID;
    public int skillLevel;
    public boolean checkTrue = true;

    /**
     * ����ָ��ȫ�ֱ�����ģ�塣
     * @param name ȫ�ֱ�������
     */
    public C_AI_CanCastSkill() {
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
        int id = (skillID << 16) | skillLevel;
        String left = "AI_CanCastSkill(" + id + ")";
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
        return new C_AI_CanCastSkill();
    }

    /**
     * ȡ��ģ�����ơ�
     */
    public String getName() {
        return "�Ƿ����ʩ��ĳ����...";
    }

    /**
     * ת��Ϊ��Ȼ���Ա�ʾ��
     */
    public String toNatureString() {
        if (checkTrue) {
            return "����ʩ�ż��ܣ�" + SkillConfig.toString(ProjectData.getActiveProject(), skillID);
        } else {
            return "������ʩ�ż��ܣ�" + SkillConfig.toString(ProjectData.getActiveProject(), skillID);
        }
    }

    /**
     * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("AI_CanCastSkill")) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 1) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                C_AI_CanCastSkill ret = (C_AI_CanCastSkill)createNew(qinfo);
                int id = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                ret.skillID = id >> 16;
                ret.skillLevel = id & 0xFFFF;
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
                new SkillPropertyDescriptor("skillID", "����"),
                new TextPropertyDescriptor("skillLevel", "�ȼ�"),
                new ComboBoxPropertyDescriptor("checkTrue", "���ɹ�", new String[] { "��", "��" })
        };
    }

    /**
     * ȡ�����Ե�ǰֵ��
     */
    public Object getPropertyValue(Object id) {
        if ("skillID".equals(id)) {
            return new Integer(skillID);
        } else if ("skillLevel".equals(id)) {
            return String.valueOf(skillLevel);
        } else if ("checkTrue".equals(id)) {
            return checkTrue ? 0 : 1;
        }
        return null;
    }

    /**
     * �������Ե�ǰֵ��
     */
    public void setPropertyValue(Object id, Object value) {
        if ("skillID".equals(id)) {
            int newValue = ((Integer)value).intValue();
            if (newValue != skillID) {
                skillID = newValue;
                fireValueChanged();
            }
        } else if ("skillLevel".equals(id)) {
            int newValue;
            try {
                newValue = Integer.parseInt((String)value);
                if (newValue < 0) {
                    newValue = 0;
                }
            } catch (Exception e) {
                return;
            }
            if (newValue != skillLevel) {
                skillLevel = newValue;
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
