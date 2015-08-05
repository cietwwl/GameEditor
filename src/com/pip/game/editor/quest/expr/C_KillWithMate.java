package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectData;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.NPCTemplatePropertyDescriptor;
import com.pip.game.editor.property.VariablePropertyDescriptor;

/**
 * ���ʽģ�壺�����Һ���żһ��ɱ��ĳ����
 * @author lighthu
 */
public class C_KillWithMate extends AbstractExpr {
	public int templateID;
	public String varName;
	public int max;
	
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_KillWithMate(QuestInfo qinfo) {
		templateID = 0;
		if (qinfo != null && qinfo.variables.size() > 0) {
		    varName = qinfo.variables.get(0).name;
		} else {
		    varName = "killCount";
		}
		max = 10;
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
		return "E_KillWithMate(" + templateID + ", \"" + PQEUtils.reverseConv(varName) + "\", " + max + ")";
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_KillWithMate(qinfo);
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "��Һ���żһ��ɱ������...";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
	    String name = NPCTemplate.toString(ProjectData.getActiveProject(), templateID);
		return "��Һ���żһ��ɱ������ " + name;
	}

	/**
	 * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("E_KillWithMate") && expr.getRightExpr() == null) {
			if (expr.getLeftExpr().getFunctionCall().getParamCount() != 3) {
				return null;
			}
			Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
			Expression param2 = expr.getLeftExpr().getFunctionCall().getParam(1);
			Expression param3 = expr.getLeftExpr().getFunctionCall().getParam(2);
			if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER &&
				param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_STRING &&
				param3.getRightExpr() == null && param3.getLeftExpr().type == Expr0.TYPE_NUMBER) {
				C_KillWithMate ret = (C_KillWithMate)createNew(qinfo);
				ret.templateID = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
				ret.varName = PQEUtils.translateStringConstant(param2.getLeftExpr().value);
				ret.max = PQEUtils.translateNumberConstant(param3.getLeftExpr().value);
				return ret;
			}
		}
		return null;
	}

	// ������IPropertySource�ӿڵ�ʵ��

	/**
	 * ȡ�����������������ģ����3��������mapID��x��y��
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { 
				new NPCTemplatePropertyDescriptor("templateID", "����"),
				new VariablePropertyDescriptor("varName", "������", questInfo, true),
				new TextPropertyDescriptor("max", "�������")
		};
	}

	/**
	 * ȡ�����Ե�ǰֵ��
	 */
	public Object getPropertyValue(Object id) {
		if ("templateID".equals(id)) {
			return new Integer(templateID);
		} else if ("varName".equals(id)) {
			return varName;
		} else if ("max".equals(id)) {
			return String.valueOf(max);
		}
		return null;
	}

	/**
	 * �������Ե�ǰֵ��
	 */
	public void setPropertyValue(Object id, Object value) {
		if ("templateID".equals(id)) {
			int newValue = ((Integer)value).intValue();
			if (newValue != templateID) {
				templateID = newValue;
				fireValueChanged();
			}
		} else if ("varName".equals(id)) {
			String newValue = (String)value;
			if (!newValue.equals(varName)) {
				varName = newValue;
				fireValueChanged();
			}
		} else if ("max".equals(id)) {
			int newValue = Integer.parseInt((String)value);
			if (newValue != max) {
				max = newValue;
				fireValueChanged();
			}
		}
	}

  
}
