package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;

public class A_CallFriend extends AbstractExpr{

    public int faction;
    /**
     * 优先级
     */
    public int active;
    
    public A_CallFriend() {
        faction = 1;
        active = 0;
    }
    
    public IExpr createNew(QuestInfo qinfo) {
        return new A_CallFriend();
    }
    
    public String getExpression() {
        return "E_CallFriend(" + faction + "," + active +")";
    }
    
    
    public String getName() {
        return "呼唤玩家";
    }

    public boolean isCondition() {
        return false;
    }
    
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("E_CallFriend") && expr.getRightExpr() == null) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 2) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            Expression param2 = expr.getLeftExpr().getFunctionCall().getParam(1);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                A_CallFriend ret = (A_CallFriend)createNew(qinfo);
                ret.faction = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                ret.active = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }
    public String toNatureString() {
        return "战斗中呼唤玩家";
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] { 
                new TextPropertyDescriptor("faction", "魔写1，神写2"),
                new TextPropertyDescriptor("active", "优先级"),
        };
    }

    public Object getPropertyValue(Object id) {
        if ("faction".equals(id)) {
            return String.valueOf(faction);
        }else if ("active".equals(id)) {
            return String.valueOf(active);
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        if ("faction".equals(id)) {
            int newValue = Integer.parseInt((String)value);
            if (newValue != faction) {
                faction = newValue;
                fireValueChanged();
            }
        }else if ("active".equals(id)) {
            int newValue = Integer.parseInt((String)value);
            if (newValue != active) {
                active = newValue;
                fireValueChanged();
            }
        }
    }

}
