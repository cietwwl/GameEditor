package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.property.BuffPropertyDescriptor;

/**
 * 是否有宠物
 * @author wpjiang
 *  如果判定没有宠物的时候，宠物级别为0
 */
public class C_HasPetLevel extends  AbstractExpr{
    private int petLevel;
    private boolean checkTrue = true;
    
    public IExpr createNew(QuestInfo qinfo) {
        return new C_HasPetLevel();
    }

    public String getName() {
        return "是否有宠物级别达到";
    }

    public String toNatureString() {
        if (checkTrue) {
            return "宠物级别到" + petLevel;
        } else {
            return "没有宠物级别到 " + petLevel;
        }
    }

    public String getExpression() {
        if (checkTrue) {
            return "E_HasPetLevel(" + petLevel + ")";
        } else {
            return "E_HasPetLevel(" + petLevel + ") == false";
        }
    }

    public boolean isCondition() {
        return true;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("E_HasPetLevel") && 
                expr.getLeftExpr().getFunctionCall().getParamCount() == 1) {
            Expression param0 = expr.getLeftExpr().getFunctionCall().getParam(0);
            if (param0.getRightExpr() == null && param0.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                C_HasPetLevel ret = (C_HasPetLevel)createNew(qinfo);
                ret.petLevel = PQEUtils.translateNumberConstant(param0.getLeftExpr().value);
                if (expr.getRightExpr() == null) {
                    return ret;
                } else if (expr.getRightExpr().type == Expr0.TYPE_NUMBER && PQEUtils.translateNumberConstant(expr.getRightExpr().value) == 0) {
                    ret.checkTrue = false;
                    return ret;
                }
            }
        }
        return null;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] {
                new TextPropertyDescriptor("petLevel", "宠物级别"),
                new ComboBoxPropertyDescriptor("checkTrue", "条件成立", new String[] { "否", "是" })
        };
    }

    public Object getPropertyValue(Object id) {
        if(id.equals("petLevel")){
            return Integer.toString(petLevel);
        }else if (id.equals("checkTrue")){
            return checkTrue ? 1 : 0;
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        if(id.equals("petLevel")){
            int newValue = Integer.parseInt((String)value);
            if (newValue != petLevel) {
                petLevel = newValue;
                fireValueChanged();
            }
            
        }else if (id.equals("checkTrue")){
            boolean newValue = (Integer.parseInt((String)value) == 1);
            if (newValue != checkTrue) {
                checkTrue = newValue;
                petLevel = 0;  
                fireValueChanged();
            }
        }
    }

}
