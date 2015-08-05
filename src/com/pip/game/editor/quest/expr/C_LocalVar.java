package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.data.quest.pqe.ParserConstants;

/**
 * ���ʽģ�壺�Ƚ��������ֵ��һ��������Ϊ������
 * @author lighthu
 */
public class C_LocalVar extends AbstractExpr {
	public String name;
	public int op;
	public int constant;
	
	/**
	 * ����ָ�����������ģ�塣
	 * @param name �����������
	 */
	public C_LocalVar(String name) {
		this.name = name;
		this.op = ParserConstants.EQ;
		this.constant = 1;
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_LocalVar(name);
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
        StringBuffer buf = new StringBuffer();
        buf.append(name);
        buf.append(" ");
        buf.append(PQEUtils.op2str(op));
        buf.append(" ");
        buf.append(constant);
        return buf.toString();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return name;
	}

	/**
	 * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getLeftExpr().type == Expr0.TYPE_IDENTIFIER && expr.getRightExpr() != null &&
				expr.getRightExpr().type == Expr0.TYPE_NUMBER) {
			if (!name.equals(expr.getLeftExpr().value)) {
				return null;
			}
			C_LocalVar cond = new C_LocalVar(name);
			cond.op = expr.op;
			cond.constant = PQEUtils.translateNumberConstant(expr.getRightExpr().value);
			return cond;
		}
		return null;
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		StringBuffer buf = new StringBuffer();
        buf.append(name);
        buf.append(" ");
        buf.append(PQEUtils.op2nstr(op));
        buf.append(" ");
        buf.append(constant);
        return buf.toString();
	}

	// ������IPropertySource�ӿڵ�ʵ��

	/**
	 * ȡ�����������������ģ����2�����������������Ƚ�ֵ��
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		String[] opNames = new String[PQEUtils.COMPARE_OPS.length];
		for (int i = 0; i < PQEUtils.COMPARE_OPS.length; i++) {
			opNames[i] = PQEUtils.op2nstr(PQEUtils.COMPARE_OPS[i]);
		}
		return new IPropertyDescriptor[] {
				new PropertyDescriptor("name", "����"),
				new ComboBoxPropertyDescriptor("op", "������", opNames),
				new TextPropertyDescriptor("value", "�Ƚ�ֵ")
		};
	}

	/**
	 * ȡ�����Ե�ǰֵ��
	 */
	public Object getPropertyValue(Object id) {
		if ("name".equals(id)) {
			return name;
		} else if ("op".equals(id)) {
			for (int i = 0; i < PQEUtils.COMPARE_OPS.length; i++) {
				if (PQEUtils.COMPARE_OPS[i] == op) {
					return i;
				}
			}
		} else if ("value".equals(id)) {
			return String.valueOf(constant);
		}
		return null;
	}

	/**
	 * �������Ե�ǰֵ��
	 */
	public void setPropertyValue(Object id, Object value) {
		if ("op".equals(id)) {
			int newop = PQEUtils.COMPARE_OPS[((Integer)value).intValue()];
			if (newop != op) {
				op = newop;
				fireValueChanged();
			}
		} else if ("value".equals(id)) {
			int newValue = Integer.parseInt((String)value);
			if (newValue != constant) {
				constant = newValue;
				fireValueChanged();
			}
		}
	}
}
