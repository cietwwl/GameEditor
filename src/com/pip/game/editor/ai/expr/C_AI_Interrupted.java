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
 * 表达式模板，检查是否刚刚被中断施法。
 * @author lighthu
 */
public class C_AI_Interrupted extends AbstractExpr {
    /**
     * 构造指定全局变量的模板。
     * @param name 全局变量名称
     */
    public C_AI_Interrupted() {
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
        return "AI_Interrupted()";
    }

    /**
     * 用模板创建新的表达式片段。
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new C_AI_Interrupted();
    }

    /**
     * 取得模板名称。
     */
    public String getName() {
        return "是否被中断施法...";
    }

    /**
     * 转换为自然语言表示。
     */
    public String toNatureString() {
        return "被中断施法";
    }

    /**
     * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("AI_Interrupted")) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 0) {
                return null;
            }
            C_AI_Interrupted ret = (C_AI_Interrupted)createNew(qinfo);
            if (expr.getRightExpr() == null) {
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
        return new IPropertyDescriptor[0];
    }

    /**
     * 取得属性当前值。
     */
    public Object getPropertyValue(Object id) {
        return null;
    }

    /**
     * 设置属性当前值。
     */
    public void setPropertyValue(Object id, Object value) {
    }
}
