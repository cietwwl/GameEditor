package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.pip.game.data.DataObject;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.editor.property.VariablePropertyDescriptor;

/**
 * ���ʽģ�壺����ʵ��ID�Ƴ�NPC��
 * @author lighthu
 */
public class A_RemoveNPC extends AbstractExpr {
    public String varName;
	
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public A_RemoveNPC(QuestInfo qinfo) {
        varName = "�±���";
        questInfo = qinfo;
    }
	
	/**
	 * �ж����ģ����һ����������һ��������
	 */
	public boolean isCondition() {
		return false;
	}

	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new A_RemoveNPC(qinfo);
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "����ʵ��ID�Ƴ�NPC...";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
	    return "�Ƴ�����" + varName + "ָ����NPC";
	}
	
	/**
	 * ȡ�����ɵı��ʽ��
	 */
	public String getExpression() {
		return "RemoveNPC(" + varName + ")";
	}

	/**
	 * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("RemoveNPC") && expr.getRightExpr() == null) {
			FunctionCall fc = expr.getLeftExpr().getFunctionCall();
			if (fc.getParamCount() != 1) {
				return null;
			}
			Expression param1 = fc.getParam(0);
			if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_IDENTIFIER) {
				A_RemoveNPC ret = (A_RemoveNPC)createNew(qinfo);
				ret.varName = param1.getLeftExpr().value;
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
		        new VariablePropertyDescriptor("varName", "����ID�ı���", questInfo, true)
		};
	}

	/**
	 * ȡ�����Ե�ǰֵ��
	 */
	public Object getPropertyValue(Object id) {
	    if ("varName".equals(id)) {
            return varName;
        }
		return null;
	}

	/**
	 * �������Ե�ǰֵ��
	 */
	public void setPropertyValue(Object id, Object value) {
	    if ("varName".equals(id)) {
            String newValue = (String)value;
            if (!newValue.equals(varName)) {
                varName = newValue;
                fireValueChanged();
            }
        }
	}
}
