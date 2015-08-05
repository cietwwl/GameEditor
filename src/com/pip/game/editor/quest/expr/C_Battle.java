package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;

public class C_Battle extends AbstractExpr {
    
    private boolean battleFlag = false;
    public IExpr createNew(QuestInfo qinfo) {
        // TODO Auto-generated method stub
        return new C_Battle();
    }

    public String getExpression() {
        // TODO Auto-generated method stub
        int flag ;
        if(battleFlag){
            flag = 1;
        }else{
            flag = 0;
        }
        return "E_Battle(" +  flag + ")";
    }

    public String getName() {
        // TODO Auto-generated method stub
        return "«–¥Ë’Ω∂∑";
    }

    public boolean isCondition() {
        // TODO Auto-generated method stub
        return true;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        // TODO Auto-generated method stub
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("E_Battle") && 
                expr.getLeftExpr().getFunctionCall().getParamCount() == 1) {
            
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                C_Battle ret = (C_Battle)createNew(qinfo);
                ret.battleFlag = (PQEUtils.translateNumberConstant(param1.getLeftExpr().value) == 1);
                return ret;
            }
        }
        return null;
    }

    public String toNatureString() {
        // TODO Auto-generated method stub
        return "«–¥Ë’Ω∂∑" + (battleFlag?" «":"∑Ò");
    }
    public IPropertyDescriptor[] getPropertyDescriptors() {
        // TODO Auto-generated method stub
        return new IPropertyDescriptor[] {
                new ComboBoxPropertyDescriptor("battleFlag", "«–¥Ë’Ω∂∑", new String[] { "∑Ò", " «" })
        };
    }

    public Object getPropertyValue(Object id) {
        // TODO Auto-generated method stub
        if (id.equals("battleFlag")){
            return battleFlag ? 1 : 0;
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        // TODO Auto-generated method stub
        if (id.equals("battleFlag")){
            boolean newValue = ((Integer)value == 1);
            if (newValue != battleFlag) {
                battleFlag = newValue;
                fireValueChanged();
            }
        }
    }
}
