package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;

/**
 * ���ʽģ�壺�жϴ����������ĺ����ķ���ֵ�Ƿ�Ϊtrue��
 * @author lighthu
 */
public abstract class AbstractFunctionCheck4 extends AbstractExpr {
	protected String funcName;
	public int param1;
	public int param2;
	protected String paramName1;
	protected String paramName2;
	
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public AbstractFunctionCheck4(String name, int p1, String n1, int p2, String n2) {
		funcName = name;
		param1 = p1;
		param2 = p2;
		paramName1 = n1;
		paramName2 = n2;
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
		return funcName + "(" + param1 + ", " + param2 + ")";
	}

	/**
	 * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals(funcName) && expr.getRightExpr() == null) {
			if (expr.getLeftExpr().getFunctionCall().getParamCount() != 2) {
				return null;
			}
			Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
			Expression param2 = expr.getLeftExpr().getFunctionCall().getParam(1);
			if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER &&
				param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER) {
				AbstractFunctionCheck4 ret = (AbstractFunctionCheck4)createNew(qinfo);
				ret.param1 = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
				ret.param2 = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
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
				new TextPropertyDescriptor("param1", paramName1),
				new TextPropertyDescriptor("param2", paramName2)
		};
	}

	/**
	 * ȡ�����Ե�ǰֵ��
	 */
	public Object getPropertyValue(Object id) {
		if ("param1".equals(id)) {
			return String.valueOf(param1);
		} else if ("param2".equals(id)) {
			return String.valueOf(param2);
		}
		return null;
	}

	/**
	 * �������Ե�ǰֵ��
	 */
	public void setPropertyValue(Object id, Object value) {
		if ("param1".equals(id)) {
			int newValue = Integer.parseInt((String)value);
			if (newValue != param1) {
				param1 = newValue;
				fireValueChanged();
			}
		} else if ("param2".equals(id)) {
			int newValue = Integer.parseInt((String)value);
			if (newValue != param2) {
				param2 = newValue;
				fireValueChanged();
			}
		}
	}
}
