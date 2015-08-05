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
 * 表达式模板，尝试逃离当前目标。
 * @author lighthu
 */
public class A_AI_Escape extends AbstractExpr {
    public int dist;

    /**
     * 构造指定全局变量的模板。
     * @param name 全局变量名称
     */
    public A_AI_Escape() {
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
        return "AI_Escape(" + dist + ")";
    }

    /**
     * 用模板创建新的表达式片段。
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new A_AI_Escape();
    }

    /**
     * 取得模板名称。
     */
    public String getName() {
        return "逃离当前目标...";
    }

    /**
     * 转换为自然语言表示。
     */
    public String toNatureString() {
        return "逃离当前目标" + (dist / 8.0) + "码";
    }
    
    /**
     * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("AI_Escape") && expr.getRightExpr() == null) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 1) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                A_AI_Escape ret = (A_AI_Escape)createNew(qinfo);
                ret.dist = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
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
                new TextPropertyDescriptor("dist", "距离")
        };
    }

    /**
     * 取得属性当前值。
     */
    public Object getPropertyValue(Object id) {
        if ("dist".equals(id)) {
            return String.valueOf(dist / 8.0);
        }
        return null;
    }

    /**
     * 设置属性当前值。
     */
    public void setPropertyValue(Object id, Object value) {
        if ("dist".equals(id)) {
            int newValue;
            try {
                newValue = (int)(Double.parseDouble((String)value) * 8);
            } catch (Exception e) {
                return;
            }
            if (newValue != dist) {
                dist = newValue;
                fireValueChanged();
            }
        }
    }
}
