package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.BuffPropertyDescriptor;
import com.pip.game.editor.property.SkillPropertyDescriptor;

/**
 * @author wpjiang
 *  Ìí¼Óbuff
 */
public class A_RemoveBuff extends AbstractExpr {
    
    private int bufferId;
    public IExpr createNew(QuestInfo qinfo) {
        // TODO Auto-generated method stub
        return new A_RemoveBuff();
    }

    public String getExpression() {
        // TODO Auto-generated method stub
        return "E_RemoveBuff(" + bufferId + ")";
    }

    public String getName() {
        // TODO Auto-generated method stub
        return "É¾³ýbuff";
    }

    public boolean isCondition() {
        // TODO Auto-generated method stub
        return false;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        // TODO Auto-generated method stub
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("E_RemoveBuff") && expr.getRightExpr() == null) {
            FunctionCall fc = expr.getLeftExpr().getFunctionCall();
            if (fc.getParamCount() != 1) {
                return null;
            }
            Expression param1 = fc.getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                A_RemoveBuff ret = (A_RemoveBuff)createNew(null);
                ret.bufferId = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    public String toNatureString() {
        // TODO Auto-generated method stub
        return "É¾³ýbuff " + bufferId;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        // TODO Auto-generated method stub
        return new IPropertyDescriptor[] { 
                new BuffPropertyDescriptor("bufferId", "É¾³ýbuff", BuffPropertyDescriptor.UseAllBuff)
        };
    }

    public Object getPropertyValue(Object id) {
        // TODO Auto-generated method stub
        if ("bufferId".equals(id)) {
            return bufferId;
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        // TODO Auto-generated method stub
        if ("bufferId".equals(id)) {
            int newValue = (Integer)value;
            if (newValue != bufferId) {
                bufferId = newValue;
                fireValueChanged();
            }
        } 
    }

}
