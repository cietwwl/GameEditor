package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.VariablePropertyDescriptor;

/**
 * ���ʽģ�壺�����������ĺ�����������һ�������Ǳ��������ڶ���������������
 * @author lighthu
 */
public abstract class AbstractFunctionAction1 extends AbstractExpr {
	protected String funcName;
	public String param1;
	public int param2;
	protected String paramName1;
	protected String paramName2;
	
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public AbstractFunctionAction1(String name, String param1, String paramName1, int param2, String paramName2) {
		funcName = name;
		this.param1 = param1;
		this.paramName1 = paramName1;
		this.param2 = param2;
		this.paramName2 = paramName2;
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
		return funcName + "(\"" + PQEUtils.reverseConv(param1) + "\", " + param2 + ")";
	}

	/**
	 * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals(funcName) && expr.getRightExpr() == null) {
			FunctionCall fc = expr.getLeftExpr().getFunctionCall();
			if (fc.getParamCount() != 2) {
				return null;
			}
			Expression param1 = fc.getParam(0);
			Expression param2 = fc.getParam(1);
			if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_STRING &&
				param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER) {
				AbstractFunctionAction1 ret = (AbstractFunctionAction1)createNew(qinfo);
				ret.param1 = PQEUtils.translateStringConstant(param1.getLeftExpr().value);
				ret.param2 = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
				return ret;
			}
		}
		return null;
	}
	
	/**
	 * �ж��Ƿ�ֻ����ʹ�����������
	 */
	protected boolean isLocalOnly() {
	    return true;
	}

	// ������IPropertySource�ӿڵ�ʵ��

	/**
	 * ȡ�����������������ģ����2���������ַ�������������������
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { 
				new VariablePropertyDescriptor("param1", paramName1, questInfo, isLocalOnly()),
				new TextPropertyDescriptor("param2", paramName2)
		};
	}

	/**
	 * ȡ�����Ե�ǰֵ��
	 */
	public Object getPropertyValue(Object id) {
		if ("param1".equals(id)) {
			return param1;
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
			String newValue = (String)value;
			if (!newValue.equals(param1)) {
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
