package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;

/**
 * ���ʽģ�壺�������Ƿ�����ĳ������
 * @author lighthu
 */
public class C_IsChannel extends AbstractExpr {
	public String channelName;
	
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_IsChannel() {
	    channelName = "CCCCCPiP";
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
		return "IsChannel(\"" + PQEUtils.reverseConv(channelName) + "\")";
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_IsChannel();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "�����������...";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "�����������" + channelName;
	}

	/**
	 * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("IsChannel") && expr.getRightExpr() == null) {
			if (expr.getLeftExpr().getFunctionCall().getParamCount() != 1) {
				return null;
			}
			Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
			if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_STRING) {
				C_IsChannel ret = (C_IsChannel)createNew(qinfo);
				ret.channelName = PQEUtils.translateStringConstant(param1.getLeftExpr().value);
				return ret;
			}
		}
		return null;
	}

	// ������IPropertySource�ӿڵ�ʵ��

	/**
	 * ȡ�����������������ģ����3��������mapID��x��y��
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { 
				new TextPropertyDescriptor("channelName", "��������")
		};
	}

	/**
	 * ȡ�����Ե�ǰֵ��
	 */
	public Object getPropertyValue(Object id) {
		if ("channelName".equals(id)) {
			return channelName;
		}
		return null;
	}

	/**
	 * �������Ե�ǰֵ��
	 */
	public void setPropertyValue(Object id, Object value) {
		if ("channelName".equals(id)) {
			String newValue = (String)value;
			if (!newValue.equals(channelName)) {
			    channelName = newValue;
				fireValueChanged();
			}
		}
	}
}
