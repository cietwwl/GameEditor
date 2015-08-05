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
 * ���ʽģ�壺NPC����(ս����)��
 * @author tzhang
 */
public class A_NpcShoutInBattle extends AbstractExpr {
	public String message;
	/**
	 * ���ȼ�
	 */
	public int active;
	
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public A_NpcShoutInBattle() {
		message = "";
		active = 0;
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new A_NpcShoutInBattle();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "NPC����(ս����)";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "NPC����(ս����)��" + message;
	}
	
	/**
	 * ȡ�����ɵı��ʽ��
	 */
	public String getExpression() {
		return "NpcShoutInBattle(\"" + PQEUtils.reverseConv(message) + "\", " + active +")";
	}

	/**
	 * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
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

	// ������IPropertySource�ӿڵ�ʵ��

	/**
	 * ȡ�����������������ģ����2���������ַ�������������������
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { 
		        new RichTextPropertyDescriptor("message", "��Ϣ", questInfo),
				new TextPropertyDescriptor("active", "���ȼ�"),
		};
	}

	/**
	 * ȡ�����Ե�ǰֵ��
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
	 * �������Ե�ǰֵ��
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
