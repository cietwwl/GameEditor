package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.VariablePropertyDescriptor;

/**
 * ���ʽģ�壺����һ��NPC�Ƿ��ڸ�����
 * @author lighthu
 */
public class C_FindNPCByType extends AbstractExpr {
    public int templateID;
    public int distance;
    public String varName;
    public boolean checkTrue = true;
    
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_FindNPCByType(QuestInfo qinfo) {
	    templateID = -1;
	    distance = 24;
	    varName = "�±���";
	    questInfo = qinfo;
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
        if (checkTrue) {
            return "FindNPCByType(" + templateID + ", " + distance + ", \"" + varName + "\")";
        } else {
            return "FindNPCByType(" + templateID + ", " + distance  + ", \"" + varName + "\") == 0";
        }
    }

    /**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_FindNPCByType(qinfo);
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "������ָ�����͵�NPC...";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
	    if (checkTrue) {
	        return "����" + (distance / 8.00) + "���ڴ�������Ϊ" + templateID + "��NPC";
	    } else {
            return "����" + (distance / 8.00) + "���ڲ���������Ϊ" + templateID + "��NPC";
	    }
	}

    /**
     * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("FindNPCByType")) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 3) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            Expression param2 = expr.getLeftExpr().getFunctionCall().getParam(1);
            Expression param3 = expr.getLeftExpr().getFunctionCall().getParam(2);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                param3.getRightExpr() == null && param3.getLeftExpr().type == Expr0.TYPE_STRING) {
                C_FindNPCByType ret = (C_FindNPCByType)createNew(qinfo);
                ret.templateID = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                ret.distance = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
                ret.varName = PQEUtils.translateStringConstant(param3.getLeftExpr().value);
                if (expr.getRightExpr() == null) {
                    return ret;
                } else if (expr.getRightExpr().type == Expr0.TYPE_NUMBER && PQEUtils.translateNumberConstant(expr.getRightExpr().value) == 0) {
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
                new TextPropertyDescriptor("templateID", "ģ��ID"),
                new TextPropertyDescriptor("distance", "��Ч����(��)"),
                new VariablePropertyDescriptor("varName", "���浽����", questInfo, true),
                new ComboBoxPropertyDescriptor("checkTrue", "��������", new String[] { "��", "��" })
        };
    }

    /**
     * ȡ�����Ե�ǰֵ��
     */
    public Object getPropertyValue(Object id) {
        if ("templateID".equals(id)) {
            return String.valueOf(templateID);
        } else if ("distance".equals(id)) {
            return String.valueOf(distance / 8.00);
        } else if ("varName".equals(id)) {
            return varName;
        } else if ("checkTrue".equals(id)) {
            return checkTrue ? 0 : 1;
        }
        return null;
    }

    /**
     * �������Ե�ǰֵ��
     */
    public void setPropertyValue(Object id, Object value) {
        if ("templateID".equals(id)) {
            int newValue = Integer.parseInt((String)value);
            if (newValue != templateID) {
                templateID = newValue;
                fireValueChanged();
            }
        } else if ("distance".equals(id)) {
            int newValue = (int)(Double.parseDouble((String)value) * 8);
            if (newValue != distance) {
                distance = newValue;
                fireValueChanged();
            }
        } else if ("varName".equals(id)) {
            String newValue = (String)value;
            if (!newValue.equals(varName)) {
                varName = newValue;
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
