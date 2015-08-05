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
 * ���ʽģ�壺��ʾ��ʾ��Ϣ��
 * @author lighthu
 */
public class A_Message extends AbstractNotifyAction {
	public String message;
	public int timeout;
	
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public A_Message() {
		message = "";
		timeout = -1;
		notifyID = -1;
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new A_Message();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "��ʾ��Ϣ";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "��ʾ��Ϣ��" + message;
	}
	
	/**
	 * ȡ�����ɵı��ʽ��
	 */
	public String getExpression() {
		return "Message(\"" + PQEUtils.reverseConv(message) + "\", " + timeout + ", " + notifyID + ")";
	}

	/**
	 * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
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

	// ������IPropertySource�ӿڵ�ʵ��

	/**
	 * ȡ�����������������ģ����2���������ַ�������������������
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { 
				new RichTextPropertyDescriptor("message", "��Ϣ", questInfo),
				new TextPropertyDescriptor("timeout", "��ʱ(0.1��)"),
				new ComboBoxPropertyDescriptor("notify", "�Ƿ�֪ͨ", new String[] { "��", "��" })
		};
	}

	/**
	 * ȡ�����Ե�ǰֵ��
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
	 * �������Ե�ǰֵ��
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
