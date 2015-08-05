package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.ItemPropertyDescriptor;

/**
 * ���ʽģ�壺�ж��Ƿ�ʹ����ĳ��Ʒ��
 * @author lighthu
 */
public class C_UseItem extends AbstractExpr {
    
    public int itemID;
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_UseItem() {
	    itemID = -1;
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_UseItem();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "���ʹ����Ʒ...";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
	    return "���ʹ����Ʒ " + ProjectData.getActiveProject().findItemOrEquipment(itemID);
	}
	
	/**
     * ȡ�����������������ģ����1��������������
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] { 
                new ItemPropertyDescriptor("itemID", "ʹ����Ʒ")
        };
    }

    /**
     * ȡ�����Ե�ǰֵ��
     */
    public Object getPropertyValue(Object id) {
        if ("itemID".equals(id)) {
            return new Integer(itemID);
        }
        return null;
    }

    /**
     * �������Ե�ǰֵ��
     */
    public void setPropertyValue(Object id, Object value) {
        if ("itemID".equals(id)) {
            int newValue = ((Integer)value).intValue();
            if (newValue != itemID) {
                itemID = newValue;
                fireValueChanged();
            }
        }
    }

    public String getExpression() {
        return "E_UseItem(" + itemID + ")";
    }

    public boolean isCondition() {
        return true;
    }

    /**
     * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("E_UseItem") && expr.getRightExpr() == null) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 1) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                C_UseItem ret = (C_UseItem)createNew(qinfo);
                ret.itemID = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

   
    
    
}
