package com.pip.game.editor.ai.expr;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.editor.property.SkillPropertyDescriptor;
import com.pip.game.editor.quest.expr.AbstractExpr;
import com.pip.game.editor.quest.expr.IExpr;
import com.pip.util.Utils;

/**
 * 表达式模板，注册定时器。
 * @author lighthu
 */
public class A_AI_RegisterTimer extends AbstractExpr {
    public String id = "timer1";
    public int startTime = 1000;
    public int interval = 3000;

    /**
     * 构造指定全局变量的模板。
     * @param name 全局变量名称
     */
    public A_AI_RegisterTimer() {
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
        return "AI_RegisterTimer(\"" + id + "\"," + startTime + "," + interval + ")";
    }

    /**
     * 用模板创建新的表达式片段。
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new A_AI_RegisterTimer();
    }

    /**
     * 取得模板名称。
     */
    public String getName() {
        return "注册定时器...";
    }

    /**
     * 转换为自然语言表示。
     */
    public String toNatureString() {
        return "注册定时器" + id;
    }
    
    /**
     * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("AI_RegisterTimer") && expr.getRightExpr() == null) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 3) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            Expression param2 = expr.getLeftExpr().getFunctionCall().getParam(1);
            Expression param3 = expr.getLeftExpr().getFunctionCall().getParam(2);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_STRING &&
                    param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param3.getRightExpr() == null && param3.getLeftExpr().type == Expr0.TYPE_NUMBER
                    ) {
                A_AI_RegisterTimer ret = (A_AI_RegisterTimer)createNew(qinfo);
                ret.id = PQEUtils.translateStringConstant(param1.getLeftExpr().value);
                ret.startTime = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
                ret.interval = PQEUtils.translateNumberConstant(param3.getLeftExpr().value);
                return ret;
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
                new TextPropertyDescriptor("startTime", "开始时间(毫秒)"),
                new TextPropertyDescriptor("interval", "间隔(毫秒)")
        };
    }

    /**
     * 取得属性当前值。
     */
    public Object getPropertyValue(Object id) {
        if ("id".equals(id)) {
            return this.id;
        } else if ("startTime".equals(id)) {
            return String.valueOf(startTime);
        } else if ("interval".equals(id)) {
            return String.valueOf(interval);
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
        } else if ("startTime".equals(id)) {
            int newValue;
            try {
                newValue = Integer.parseInt((String)value);
                if (newValue < 0) {
                    newValue = 0;
                }
            } catch (Exception e) {
                return;
            }
            if (newValue != startTime) {
                startTime = newValue;
                fireValueChanged();
            }
        } else if ("interval".equals(id)) {
            int newValue;
            try {
                newValue = Integer.parseInt((String)value);
                if (interval < 1) {
                    interval = 1;
                }
            } catch (Exception e) {
                return;
            }
            if (newValue != interval) {
                interval = newValue;
                fireValueChanged();
            }
        }
    }
}
