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
public class A_Custom extends AbstractExpr {
	public String exprStr;
	
	/**
	 * ���췽����
	 */
	public A_Custom() {
		exprStr = "a()";
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new A_Custom();
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
        return exprStr;
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "�Զ��嶯��";
	}

	/**
	 * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getRightExpr() == null) {
			A_Custom ret = new A_Custom();
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
