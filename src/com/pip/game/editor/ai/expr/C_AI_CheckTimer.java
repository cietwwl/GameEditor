package com.pip.game.editor.ai.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.quest.expr.AbstractExpr;
import com.pip.game.editor.quest.expr.IExpr;

/**
 * 表达式模板，检查怪物状态。
 * @author lighthu
 */
public class C_AI_CheckTimer extends AbstractExpr {
    public String id = "timer1";
    public boolean checkTrue = true;

    /**
     * 构造指定全局变量的模板。
     * @param name 全局变量名称
     */
    public C_AI_CheckTimer() {
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
        String left = "AI_CheckTimer(\"" + id + "\")";
        if (checkTrue) {
            return left;
        } else {
            return left + " == false";
        }
    }

    /**
     * 用模板创建新的表达式片段。
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new C_AI_CheckTimer();
    }

    /**
     * 取得模板名称。
     */
    public String getName() {
        return "检查定时器...";
    }

    /**
     * 转换为自然语言表示。
     */
    public String toNatureString() {
        if (checkTrue) {
            return "定时器" + id + "触发";
        } else {
            return "定时器" + id + "未触发";
        }
    }

    /**
     * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("AI_CheckTimer")) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 1) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_STRING) {
                C_AI_CheckTimer ret = (C_AI_CheckTimer)createNew(qinfo);
                ret.id = PQEUtils.translateStringConstant(param1.getLeftExpr().value);
                if (expr.getRightExpr() == null) {
                    return ret;
                } else if (expr.getRightExpr().type == Expr0.TYPE_IDENTIFIER && expr.getRightExpr().value.equals("false")) {
                    ret.checkTrue = false;
                    return ret;
                }
            }
        }
        return null;
    }

    // 下面是IPropertySource接口的实现

    /**
     * 取得属性描述符。这个模板有1个参数：参数。
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] {
                new TextPropertyDescriptor("id", "定时器名称"),
                new ComboBoxPropertyDescriptor("checkTrue", "检查成功", new String[] { "是", "否" })
        };
    }

    /**
     * 取得属性当前值。
     */
    public Object getPropertyValue(Object id) {
        if ("id".equals(id)) {
            return this.id;
        } else if ("checkTrue".equals(id)) {
            return checkTrue ? 0 : 1;
        }
        return null;
    }

    /**
     * 设置属性当前值。
     */
    public void setPropertyValue(Object id, Object value) {
        if ("id".equals(id)) {
            String newValue = (String)value;
            if (newValue.length() != 0 && !newValue.equals(this.id)) {
                this.id = newValue;
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
