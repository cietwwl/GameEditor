package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;

/**
 * ���ʽģ�壺�ж�����Ŀ���Ƿ���ȫ����ɡ�
 * @author lighthu
 */
public class C_CanFinish extends AbstractExpr {
    public boolean checkTrue = true;
    
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_CanFinish() {
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
        if (checkTrue) {
            return "CanFinish()";
        } else {
            return "CanFinish() == false";
        }
    }

    /**
     * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("CanFinish") && 
                expr.getLeftExpr().getFunctionCall().getParamCount() == 0) {
            if (expr.getRightExpr() == null) {
                return createNew(qinfo);
            } else if (expr.getRightExpr().type == Expr0.TYPE_NUMBER && PQEUtils.translateNumberConstant(expr.getRightExpr().value) == 0) {
                C_CanFinish ret = (C_CanFinish)createNew(qinfo);
                ret.checkTrue = false;
                return ret;
            }
        }
        return null;
    }
    
    /**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_CanFinish();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "����Ŀ���Ƿ��Ѵ��";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
	    if (checkTrue) {
	        return "����Ŀ���Ѵ��";
	    } else {
	        return "����Ŀ��δ���";
	    }
	}

    // ������IPropertySource�ӿڵ�ʵ��

    /**
     * ȡ�����������������ģ����1��������������
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] {
                new ComboBoxPropertyDescriptor("checkTrue", "��������", new String[] { "��", "��" })
        };
    }

    /**
     * ȡ�����Ե�ǰֵ��
     */
    public Object getPropertyValue(Object id) {
        if ("checkTrue".equals(id)) {
            return checkTrue ? 0 : 1;
        }
        return null;
    }

    /**
     * �������Ե�ǰֵ��
     */
    public void setPropertyValue(Object id, Object value) {
        if ("checkTrue".equals(id)) {
            boolean newValue = ((Integer)value).intValue() == 0;
            if (newValue != checkTrue) {
                checkTrue = newValue;
                fireValueChanged();
            }
        }   
    }
}
