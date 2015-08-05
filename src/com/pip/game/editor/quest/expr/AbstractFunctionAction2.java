package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;

/**
 * ���ʽģ�壺��һ�����������ĺ�������
 * @author lighthu
 */
public abstract class AbstractFunctionAction2 extends AbstractExpr {
	protected String funcName;
	public int param;
	protected String paramName;
	
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public AbstractFunctionAction2(String name, int param, String paramName) {
		funcName = name;
		this.param = param;
		this.paramName = paramName;
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
		return funcName + "(" + param + ")";
	}

	/**
	 * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals(funcName) && expr.getRightExpr() == null) {
			FunctionCall fc = expr.getLeftExpr().getFunctionCall();
			if (fc.getParamCount() != 1) {
				return null;
			}
			Expression param1 = fc.getParam(0);
			if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER) {
				AbstractFunctionAction2 ret = (AbstractFunctionAction2)createNew(qinfo);
				ret.param = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
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
				new TextPropertyDescriptor("param", paramName)
		};
	}

	/**
	 * ȡ�����Ե�ǰֵ��
	 */
	public Object getPropertyValue(Object id) {
		if ("param".equals(id)) {
			return String.valueOf(param);
		}
		return null;
	}

	/**
	 * �������Ե�ǰֵ��
	 */
	public void setPropertyValue(Object id, Object value) {
		if ("param".equals(id)) {
			int newValue = Integer.parseInt((String)value);
			if (newValue != param) {
				param = newValue;
				fireValueChanged();
			}
		}
	}
}
