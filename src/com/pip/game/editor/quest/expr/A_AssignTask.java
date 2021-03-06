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
 * 表达式模板：强制指派任务。
 * @author lighthu
 */
public class A_AssignTask extends AbstractExpr {
    public int questID;
    
	/**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public A_AssignTask() {
	    questID = -1;
	}
	
	/**
     * 判断这个模板是一个条件还是一个动作。
     */
    public boolean isCondition() {
        return false;
    }

    /**
     * 取得生成的表达式。
     */
    public String getExpression() {
        return "AssignTask(" + questID + ")";
    }
    
	/**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new A_AssignTask();
	}

    /**
     * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
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
	 * 取得模板名称。
	 */
	public String getName() {
		return "强制指派任务";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
	    String qname = Quest.toString(ProjectData.getActiveProject(), questID);
        return "强制指派任务 " + qname;
	}

    // 下面是IPropertySource接口的实现

    /**
     * 取得属性描述符。这个模板有2个参数：字符串参数和整数参数。
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] { 
                new QuestPropertyDescriptor("questID", "目标任务")
        };
    }

    /**
     * 取得属性当前值。
     */
    public Object getPropertyValue(Object id) {
        if ("questID".equals(id)) {
            return new Integer(questID);
        }
        return null;
    }

    /**
     * 设置属性当前值。
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
