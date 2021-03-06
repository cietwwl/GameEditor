package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.quest.Quest;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.QuestPropertyDescriptor;

/**
 * 表达式模板：判断是否已完成过某任务。
 * @author lighthu
 */
public class C_TaskFinished extends AbstractExpr {
    public boolean checkTrue = true;
    public int taskID;
    
    /**
     * 构造指定全局变量的模板。
     * @param name 全局变量名称
     */
    public C_TaskFinished() {
    }
    
    /**
     * 判断这个模板是一个条件还是一个动作。
     */
    public boolean isCondition() {
        return true;
    }

    /**
     * 取得生成的表达式。
     */
    public String getExpression() {
        if (checkTrue) {
            return "TaskFinished(" + taskID + ")";
        } else {
            return "TaskFinished(" + taskID + ") == false";
        }
    }

    /**
     * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("TaskFinished") && 
                expr.getLeftExpr().getFunctionCall().getParamCount() == 1) {
            Expression param0 = expr.getLeftExpr().getFunctionCall().getParam(0);
            if (param0.getRightExpr() == null && param0.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                C_TaskFinished ret = (C_TaskFinished)createNew(qinfo);
                ret.taskID = PQEUtils.translateNumberConstant(param0.getLeftExpr().value);
                if (expr.getRightExpr() == null) {
                    return ret;
                } else if (expr.getRightExpr().type == Expr0.TYPE_NUMBER && PQEUtils.translateNumberConstant(expr.getRightExpr().value) == 0) {
                    ret.checkTrue = false;
                    return ret;
                }
            }
        }
        return null;
    }

    /**
     * 用模板创建新的表达式片段。
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new C_TaskFinished();
    }

    /**
     * 取得模板名称。
     */
    public String getName() {
        return "是否完成过任务...";
    }

    /**
     * 转换为自然语言表示。
     */
    public String toNatureString() {
        String qname = Quest.toString(ProjectData.getActiveProject(), taskID);
        if (checkTrue) {
            return "已完成过任务 " + qname;
        } else {
            return "未完成过任务 " + qname;
        }
    }

    // 下面是IPropertySource接口的实现

    /**
     * 取得属性描述符。这个模板有1个参数：参数。
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] {
                new QuestPropertyDescriptor("taskID", "目标任务"),
                new ComboBoxPropertyDescriptor("checkTrue", "条件成立", new String[] { "是", "否" })
        };
    }

    /**
     * 取得属性当前值。
     */
    public Object getPropertyValue(Object id) {
        if ("taskID".equals(id)) {
            return new Integer(taskID);
        } else if ("checkTrue".equals(id)) {
            return checkTrue ? 0 : 1;
        }
        return null;
    }

    /**
     * 设置属性当前值。
     */
    public void setPropertyValue(Object id, Object value) {
        if ("taskID".equals(id)) {
            int newValue = ((Integer)value).intValue();
            if (newValue != taskID) {
                taskID = newValue;
                fireValueChanged();
            }
        } else if ("checkTrue".equals(id)) {
            boolean newValue = ((Integer)value).intValue() == 0;
            if (newValue != checkTrue) {
                checkTrue = newValue;
                fireValueChanged();
            }
        }   
    }
}
