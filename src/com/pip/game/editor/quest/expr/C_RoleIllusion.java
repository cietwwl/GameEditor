package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;

public class C_RoleIllusion extends AbstractExpr {
    
    private boolean roleIllusionFlag = false;
    public IExpr createNew(QuestInfo qinfo) {
        // TODO Auto-generated method stub
        return new C_RoleIllusion();
    }

    public String getExpression() {
        // TODO Auto-generated method stub
        int flag ;
        if(roleIllusionFlag){
            flag = 1;
        }else{
            flag = 0;
        }
        return "E_RoleIllusion(" +  flag + ")";
    }

    public String getName() {
        // TODO Auto-generated method stub
        return "角色幻化";
    }

    public boolean isCondition() {
        // TODO Auto-generated method stub
        return true;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        // TODO Auto-generated method stub
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("E_RoleIllusion") && 
                expr.getLeftExpr().getFunctionCall().getParamCount() == 1) {
            
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                C_RoleIllusion ret = (C_RoleIllusion)createNew(qinfo);
                ret.roleIllusionFlag = (PQEUtils.translateNumberConstant(param1.getLeftExpr().value) == 1);
                return ret;
            }
        }
        return null;
    }

    public String toNatureString() {
        // TODO Auto-generated method stub
        return "角色幻化" + (roleIllusionFlag?"是":"否");
    }
    public IPropertyDescriptor[] getPropertyDescriptors() {
        // TODO Auto-generated method stub
        return new IPropertyDescriptor[] {
                new ComboBoxPropertyDescriptor("roleIllusionFlag", "角色幻化", new String[] { "否", "是" })
        };
    }

    public Object getPropertyValue(Object id) {
        // TODO Auto-generated method stub
        if (id.equals("roleIllusionFlag")){
            return roleIllusionFlag ? 1 : 0;
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        // TODO Auto-generated method stub
        if (id.equals("roleIllusionFlag")){
            boolean newValue = ((Integer)value == 1);
            if (newValue != roleIllusionFlag) {
                roleIllusionFlag = newValue;
                fireValueChanged();
            }
        }
    }
}
