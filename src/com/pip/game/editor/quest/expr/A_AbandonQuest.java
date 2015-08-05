package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;

/**
 * @author wpjiang
 *  放弃任务
 */
public class A_AbandonQuest extends AbstractExpr {
    
    private int questId;
    public IExpr createNew(QuestInfo qinfo) {
        return new A_AbandonQuest();
    }

    public String getExpression() {
        return "E_AbandonQuest(" + questId + ")";
    }

    public String getName() {
        return "放弃任务";
    }

    public boolean isCondition() {
        return false;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("E_AbandonQuest") && expr.getRightExpr() == null) {
            FunctionCall fc = expr.getLeftExpr().getFunctionCall();
            if (fc.getParamCount() != 1) {
                return null;
            }
            Expression param1 = fc.getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                A_AbandonQuest ret = (A_AbandonQuest)createNew(null);
                ret.questId = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    public String toNatureString() {
        return "放弃任务 " + questId;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] { 
                new TextPropertyDescriptor("questId", "任务Id")
        };
    }

    public Object getPropertyValue(Object id) {
        if ("questId".equals(id)) {
            return String.valueOf(questId);
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        if ("questId".equals(id)) {
            int newValue = Integer.parseInt((String)value);
            if (newValue != questId) {
                questId = newValue;
                fireValueChanged();
            }
        }
    }

}
