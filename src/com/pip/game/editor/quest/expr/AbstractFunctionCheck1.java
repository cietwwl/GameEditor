package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;

/**
 * ���ʽģ�壺һ���޲��������ķ���ֵ��һ�������Ƚϡ�
 * @author lighthu
 */
public abstract class AbstractFunctionCheck1 extends AbstractExpr {
	protected String funcName;
	public int op;
	public int constant;
	protected String paramName;
	
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public AbstractFunctionCheck1(String name, int op, int c, String paramName) {
		funcName = name;
		this.op = op;
		constant = c;
		this.paramName = paramName;
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
		return funcName + "() " + PQEUtils.op2str(op) + " " + constant;
	}

	/**
	 * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals(funcName) &&
				expr.getRightExpr() != null && expr.op == op && expr.getRightExpr().type == Expr0.TYPE_NUMBER) {
			AbstractFunctionCheck1 ret = (AbstractFunctionCheck1)createNew(qinfo);
			ret.op = op;
			ret.constant = PQEUtils.translateNumberConstant(expr.getRightExpr().value);
			return ret;
		}
		return null;
	}

	// ������IPropertySource�ӿڵ�ʵ��

	/**
	 * ȡ�����������������ģ����1���������Ƚ�ֵ��
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { 
				new TextPropertyDescriptor("value", paramName)
		};
	}

	/**
	 * ȡ�����Ե�ǰֵ��
	 */
	public Object getPropertyValue(Object id) {
		if ("value".equals(id)) {
			return String.valueOf(constant);
		}
		return null;
	}

	/**
	 * �������Ե�ǰֵ��
	 */
	public void setPropertyValue(Object id, Object value) {
		if ("value".equals(id)) {
			int newValue = Integer.parseInt((String)value);
			if (newValue != constant) {
				constant = newValue;
				fireValueChanged();
			}
		}
	}
}
