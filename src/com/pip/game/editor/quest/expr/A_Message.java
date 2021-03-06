package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.RichTextPropertyDescriptor;

/**
 * 表达式模板：显示提示消息。
 * @author lighthu
 */
public class A_Message extends AbstractNotifyAction {
	public String message;
	public int timeout;
	
	/**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public A_Message() {
		message = "";
		timeout = -1;
		notifyID = -1;
	}
	
	/**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new A_Message();
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "显示消息";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
		return "显示消息：" + message;
	}
	
	/**
	 * 取得生成的表达式。
	 */
	public String getExpression() {
		return "Message(\"" + PQEUtils.reverseConv(message) + "\", " + timeout + ", " + notifyID + ")";
	}

	/**
	 * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("Message") && expr.getRightExpr() == null) {
			FunctionCall fc = expr.getLeftExpr().getFunctionCall();
			if (fc.getParamCount() != 3) {
				return null;
			}
			Expression param1 = fc.getParam(0);
			Expression param2 = fc.getParam(1);
			Expression param3 = fc.getParam(2);
			if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_STRING &&
				param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER &&
				param3.getRightExpr() == null && param3.getLeftExpr().type == Expr0.TYPE_NUMBER) {
				A_Message ret = (A_Message)createNew(qinfo);
				ret.message = PQEUtils.translateStringConstant(param1.getLeftExpr().value);
				ret.timeout = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
				ret.notifyID = PQEUtils.translateNumberConstant(param3.getLeftExpr().value);
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
				new TextPropertyDescriptor("timeout", "超时(0.1秒)"),
				new ComboBoxPropertyDescriptor("notify", "是否通知", new String[] { "是", "否" })
		};
	}

	/**
	 * 取得属性当前值。
	 */
	public Object getPropertyValue(Object id) {
		if ("timeout".equals(id)) {
			return String.valueOf(timeout);
		} else if ("message".equals(id)) {
			return message;
		} else if ("notify".equals(id)) {
            return new Integer(notifyID != -1 ? 0 : 1);
        }
		return null;
	}

	/**
	 * 设置属性当前值。
	 */
	public void setPropertyValue(Object id, Object value) {
		if ("timeout".equals(id)) {
			int newValue = Integer.parseInt((String)value);
			if (newValue != timeout) {
				timeout = newValue;
				fireValueChanged();
			}
		} else if ("message".equals(id)) {
			String newValue = (String)value;
			if (!newValue.equals(message)) {
				message = newValue;
				fireValueChanged();
			}
		} else if ("notify".equals(id)) {
            int newValue = ((Integer)value).intValue();
            if (newValue == 0) {
                newValue = 0;
            } else {
                newValue = -1;
            }
            if (newValue != notifyID) {
                notifyID = newValue;
                fireValueChanged();
            }
        }
	}
}
