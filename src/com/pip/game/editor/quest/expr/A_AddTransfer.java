package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;

/**
 * @author tzhang
 *  添加传送点
 */
public class A_AddTransfer extends AbstractExpr {
    
    private int transferId;
    public IExpr createNew(QuestInfo qinfo) {
        return new A_AddTransfer();
    }

    public String getExpression() {
        return "E_AddTransfer(" + transferId + ")";
    }

    public String getName() {
        return "添加传送点";
    }

    public boolean isCondition() {
        return false;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("E_AddTransfer") && expr.getRightExpr() == null) {
            FunctionCall fc = expr.getLeftExpr().getFunctionCall();
            if (fc.getParamCount() != 1) {
                return null;
            }
            Expression param1 = fc.getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                A_AddTransfer ret = (A_AddTransfer)createNew(null);
                ret.transferId = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    public String toNatureString() {
        return "添加传送点 " + transferId;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] { 
                new TextPropertyDescriptor("transferId", "传送点ID")
        };
    }

    public Object getPropertyValue(Object id) {
        if ("transferId".equals(id)) {
            return String.valueOf(transferId);
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        if ("transferId".equals(id)) {
            int newValue = Integer.parseInt((String)value);
            if (newValue != transferId) {
                transferId = newValue;
                fireValueChanged();
            }
        }
    }

}
