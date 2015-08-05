package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.QuestPropertyDescriptor;

/**
 * @author wpjiang
 * 装备级别达到
 */
public class C_EquLevel extends AbstractExpr{
    
    private int equLevel;
    public IExpr createNew(QuestInfo qinfo) {
        // TODO Auto-generated method stub
        return new C_EquLevel();
    }

    public String getExpression() {
        // TODO Auto-generated method stub
        return "E_EquLevel(" + equLevel + ")";
    }

    public String getName() {
        // TODO Auto-generated method stub
        return "装备级别达到";
    }

    public boolean isCondition() {
        // TODO Auto-generated method stub
        return true;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        // TODO Auto-generated method stub
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("E_EquLevel") && 
                expr.getLeftExpr().getFunctionCall().getParamCount() == 1) {
            Expression param0 = expr.getLeftExpr().getFunctionCall().getParam(0);
            if (param0.getRightExpr() == null && param0.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                C_EquLevel ret = (C_EquLevel)createNew(qinfo);
                ret.equLevel = PQEUtils.translateNumberConstant(param0.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    public String toNatureString() {
        // TODO Auto-generated method stub
        return "装备级别达到" + equLevel;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        // TODO Auto-generated method stub
        return new IPropertyDescriptor[] {
                new TextPropertyDescriptor("equLevel", "装备级别达到"),
        };
    }

    public Object getPropertyValue(Object id) {
        // TODO Auto-generated method stub
        if(id.equals("equLevel")){
            return Integer.toString(equLevel);
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        // TODO Auto-generated method stub
        if(id.equals("equLevel")){
            int newValue = Integer.parseInt((String)value);
            if(newValue != equLevel){
                equLevel = newValue;
                fireValueChanged();
            }
        }
    }

}
