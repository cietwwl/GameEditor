package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.ItemPropertyDescriptor;

/**
 * ����ʽģ�壺Ϊ�û�������Ʒ��
 * @author lighthu
 */
public class A_AddItem extends AbstractExpr {
	public int itemID;
	public int count;
	
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public A_AddItem() {
		itemID = 1;
		count = 1;
	}
	
	/**
	 * �ж����ģ����һ����������һ��������
	 */
	public boolean isCondition() {
		return false;
	}

	/**
	 * ��ģ�崴���µı���ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new A_AddItem();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "������Ʒ";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "������Ʒ " + itemID + " " + count + " ��";
	}
	
	/**
	 * ȡ�����ɵı���ʽ��
	 */
	public String getExpression() {
		return "AddItem(" + itemID + ", " + count + ")";
	}

	/**
	 * ʶ��һ������ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı���ʽƬ�ζ��󣬷��򷵻�null��
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("AddItem") && expr.getRightExpr() == null) {
			FunctionCall fc = expr.getLeftExpr().getFunctionCall();
			if (fc.getParamCount() != 2) {
				return null;
			}
			Expression param1 = fc.getParam(0);
			Expression param2 = fc.getParam(1);
			if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER &&
				param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER) {
				A_AddItem ret = (A_AddItem)createNew(null);
				ret.itemID = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
				ret.count = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
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
		        new ItemPropertyDescriptor("itemID", "��Ʒ"),
				new TextPropertyDescriptor("count", "����")
		};
	}

	/**
	 * ȡ�����Ե�ǰֵ��
	 */
	public Object getPropertyValue(Object id) {
		if ("itemID".equals(id)) {
			return itemID;
		} else if ("count".equals(id)) {
			return String.valueOf(count);
		}
		return null;
	}

	/**
	 * �������Ե�ǰֵ��
	 */
	public void setPropertyValue(Object id, Object value) {
	    if ("itemID".equals(id)) {
            int newValue = (Integer)value;
            if (newValue != itemID) {
                itemID = newValue;
                fireValueChanged();
            }
		} else if ("count".equals(id)) {
			int newValue = Integer.parseInt((String)value);
			if (newValue != count) {
				count = newValue;
				fireValueChanged();
			}
		}
	}

 
}