package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;

public class C_HpMpSurplus extends AbstractExpr {
    private boolean hpFlag = false;
    private int rest;
    public IExpr createNew(QuestInfo qinfo) {
        // TODO Auto-generated method stub
        return new C_HpMpSurplus();
    }
    public String getExpression() {
        // TODO Auto-generated method stub
        int flag;
        if(hpFlag){
            flag = 0;
        }else {
            flag = 1;
        }
        return "E_HpMpSurplus(" + flag + ", " + rest + ")";
    }

    public String getName() {
        // TODO Auto-generated method stub
        return "ÑªÁ¿£¨À¶Á¿£©Ê£Óà";
       
    }

    public boolean isCondition() {
        // TODO Auto-generated method stub
        return true;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        // TODO Auto-generated method stub
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("E_HpMpSurplus") && 
                expr.getLeftExpr().getFunctionCall().getParamCount() == 2) {
            
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            Expression param2 = expr.getLeftExpr().getFunctionCall().getParam(1);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                C_HpMpSurplus ret = (C_HpMpSurplus)createNew(qinfo);
                ret.hpFlag = (PQEUtils.translateNumberConstant(param1.getLeftExpr().value) == 1);
                ret.rest = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
                
                return ret;
            }
        }
        return null;

    }

    public String toNatureString() {
        // TODO Auto-generated method stub
        if(hpFlag){
            return "À¶Á¿Ê£Óà" + rest;
        }else{
            return "ÑªÁ¿Ê£Óà" + rest;
        }
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        // TODO Auto-generated method stub
        return new IPropertyDescriptor[] {
                new TextPropertyDescriptor("rest", "Ê£ÓàÁ¿"),
                new ComboBoxPropertyDescriptor("hpFlag", "ÑªÁ¿£¨À¶Á¿£©", new String[] { "ÑªÁ¿", "À¶Á¿" })
        };
    }

    public Object getPropertyValue(Object id) {
        // TODO Auto-generated method stub
        if(id.equals("rest")){
            return Integer.toString(rest);
        }else if (id.equals("hpFlag")){
            return hpFlag ? 1 : 0;
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        // TODO Auto-generated method stub
        if(id.equals("rest")){
            int newValue = Integer.parseInt((String)value);
            if (newValue != rest) {
                rest = newValue;
                fireValueChanged();
            }
            
        }else if (id.equals("hpFlag")){
            boolean newValue = ((Integer)value == 1);
            if (newValue != hpFlag) {
                hpFlag = newValue;
                rest = 0;  
                fireValueChanged();
            }
        }
        
    }
    
}
