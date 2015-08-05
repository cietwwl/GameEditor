package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.NPCPropertyDescriptor;
import com.pip.game.editor.property.RichTextPropertyDescriptor;

/**
 * 表达式模板：NPC喊话(战斗中)。
 * @author tzhang
 */
public class A_NpcShoutInBattle extends AbstractExpr {
	public String message;
	/**
	 * 优先级
	 */
	public int active;
	
	/**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public A_NpcShoutInBattle() {
		message = "";
		active = 0;
	}
	
	/**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new A_NpcShoutInBattle();
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "NPC喊话(战斗中)";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
		return "NPC喊话(战斗中)：" + message;
	}
	
	/**
	 * 取得生成的表达式。
	 */
	public String getExpression() {
		return "NpcShoutInBattle(\"" + PQEUtils.reverseConv(message) + "\", " + active +")";
	}

	/**
	 * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("NpcShoutInBattle") && expr.getRightExpr() == null) {
			FunctionCall fc = expr.getLeftExpr().getFunctionCall();
			if (fc.getParamCount() != 2) {
				return null;
			}
			Expression param1 = fc.getParam(0);
			Expression param2 = fc.getParam(1);
			if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_STRING &&
				param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER ) {
				A_NpcShoutInBattle ret = (A_NpcShoutInBattle)createNew(qinfo);
				ret.message = PQEUtils.translateStringConstant(param1.getLeftExpr().value);
				ret.active = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
				return ret;
			}
		}
		return null;
	}

	// 下面是IPropertySource接口的实现

	/**
	 * 取得属性描述符。这个模板有2个参数：字符串参数和整数参数。
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { 
		        new RichTextPropertyDescriptor("message", "消息", questInfo),
				new TextPropertyDescriptor("active", "优先级"),
		};
	}

	/**
	 * 取得属性当前值。
	 */
	public Object getPropertyValue(Object id) {
		if ("active".equals(id)) {
			return String.valueOf(active);
		} else if ("message".equals(id)) {
			return message;
		} 
		return null;
	}

	/**
	 * 设置属性当前值。
	 */
	public void setPropertyValue(Object id, Object value) {
		if ("active".equals(id)) {
			int newValue = Integer.parseInt((String)value);
			if (newValue != active) {
				active = newValue;
				fireValueChanged();
			}
		} else if ("message".equals(id)) {
			String newValue = (String)value;
			if (!newValue.equals(message)) {
				message = newValue;
				fireValueChanged();
			}
		}
	}

    public boolean isCondition() {
        return false;
    }

  
   
}
