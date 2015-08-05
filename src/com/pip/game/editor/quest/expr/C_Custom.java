package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.ExpressionList;

/**
 * �û��Զ�����޷���ģ�崦��ĸ��ӱ��ʽ��
 * @author lighthu
 */
public class C_Custom extends AbstractExpr {
	public String exprStr;
	
	/**
	 * ���췽����
	 */
	public C_Custom() {
		exprStr = "a() == 1";
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_Custom();
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
        return exprStr;
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "�Զ�������";
	}

	/**
	 * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getRightExpr() != null) {
			C_Custom ret = new C_Custom();
			ret.exprStr = expr.toString();
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return ExpressionList.toNatureString(exprStr);
	}

	// ������IPropertySource�ӿڵ�ʵ��

	/**
	 * ȡ�����������������ģ����1�����������ʽ��
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { 
				new TextPropertyDescriptor("value", "���ʽ")
		};
	}

	/**
	 * ȡ�����Ե�ǰֵ��
	 */
	public Object getPropertyValue(Object id) {
		if ("value".equals(id)) {
			return exprStr;
		}
		return null;
	}

	/**
	 * �������Ե�ǰֵ��
	 */
	public void setPropertyValue(Object id, Object value) {
		if ("value".equals(id)) {
			String newValue = (String)value;
			if (!newValue.equals(exprStr)) {
				exprStr = newValue;
				fireValueChanged();
			}
		}
	}
}
