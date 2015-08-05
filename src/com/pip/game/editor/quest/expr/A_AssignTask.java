package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.quest.Quest;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.property.QuestPropertyDescriptor;

/**
 * ���ʽģ�壺ǿ��ָ������
 * @author lighthu
 */
public class A_AssignTask extends AbstractExpr {
    public int questID;
    
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public A_AssignTask() {
	    questID = -1;
	}
	
	/**
     * �ж����ģ����һ����������һ��������
     */
    public boolean isCondition() {
        return false;
    }

    /**
     * ȡ�����ɵı��ʽ��
     */
    public String getExpression() {
        return "AssignTask(" + questID + ")";
    }
    
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new A_AssignTask();
	}

    /**
     * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("AssignTask") && expr.getRightExpr() == null) {
            FunctionCall fc = expr.getLeftExpr().getFunctionCall();
            if (fc.getParamCount() != 1) {
                return null;
            }
            Expression param1 = fc.getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                A_AssignTask ret = (A_AssignTask)createNew(qinfo);
                ret.questID = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }
	
	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "ǿ��ָ������";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
	    String qname = Quest.toString(ProjectData.getActiveProject(), questID);
        return "ǿ��ָ������ " + qname;
	}

    // ������IPropertySource�ӿڵ�ʵ��

    /**
     * ȡ�����������������ģ����2���������ַ�������������������
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] { 
                new QuestPropertyDescriptor("questID", "Ŀ������")
        };
    }

    /**
     * ȡ�����Ե�ǰֵ��
     */
    public Object getPropertyValue(Object id) {
        if ("questID".equals(id)) {
            return new Integer(questID);
        }
        return null;
    }

    /**
     * �������Ե�ǰֵ��
     */
    public void setPropertyValue(Object id, Object value) {
        if ("questID".equals(id)) {
            int newValue = ((Integer)value).intValue();
            if (newValue != questID) {
                questID = newValue;
                fireValueChanged();
            }
        }
    }

  
}
