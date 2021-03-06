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
 * 表达式模板：比较任务变量值和一个常量作为条件。
 * @author lighthu
 */
public class C_LocalVar extends AbstractExpr {
	public String name;
	public int op;
	public int constant;
	
	/**
	 * 构造指定任务变量的模板。
	 * @param name 任务变量名称
	 */
	public C_LocalVar(String name) {
		this.name = name;
		this.op = ParserConstants.EQ;
		this.constant = 1;
	}
	
	/**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_LocalVar(name);
	}
	
	/**
	 * 判断这个模板是一个条件还是一个动作。
	 */
	public boolean isCondition() {
		return true;
	}
	
	/**
	 * 取得生成的表达式。
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
	 * 取得模板名称。
	 */
	public String getName() {
		return name;
	}

	/**
	 * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
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
	 * 转换为自然语言表示。
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

	// 下面是IPropertySource接口的实现

	/**
	 * 取得属性描述符。这个模板有2个参数：操作符、比较值。
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		String[] opNames = new String[PQEUtils.COMPARE_OPS.length];
		for (int i = 0; i < PQEUtils.COMPARE_OPS.length; i++) {
			opNames[i] = PQEUtils.op2nstr(PQEUtils.COMPARE_OPS[i]);
		}
		return new IPropertyDescriptor[] {
				new PropertyDescriptor("name", "变量"),
				new ComboBoxPropertyDescriptor("op", "操作符", opNames),
				new TextPropertyDescriptor("value", "比较值")
		};
	}

	/**
	 * 取得属性当前值。
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
	 * 设置属性当前值。
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
