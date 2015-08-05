package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;

public class A_SummonUnit extends AbstractExpr{

    public String monstersList;
    /**
     * 优先级
     */
    public int active;
    
    public A_SummonUnit() {
        monstersList = "";
        active = 0;
    }
    
    public IExpr createNew(QuestInfo qinfo) {
        return new A_SummonUnit();
    }

    public String getExpression() {
        return "E_SummonUnit(" + "\""+ "" + monstersList + "\"" + ","+ active +")";
    }

    public String getName() {
        return "召唤怪物";
    }

    public boolean isCondition() {
        return false;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("E_SummonUnit") && expr.getRightExpr() == null) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 2) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            Expression param2 = expr.getLeftExpr().getFunctionCall().getParam(1);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_STRING &&
                    param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                A_SummonUnit ret = (A_SummonUnit)createNew(qinfo);
                ret.monstersList = PQEUtils.translateStringConstant(param1.getLeftExpr().value);
                ret.active = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    public String toNatureString() {
        return "战斗中召唤怪物";
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] { 
                new TextPropertyDescriptor("monstersList", "怪物模板id"),
                new TextPropertyDescriptor("active", "优先级"),
        };
    }

    public Object getPropertyValue(Object id) {
        if ("active".equals(id)) {
            return String.valueOf(active);
        }else if ("monstersList".equals(id)) {
            return monstersList;
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        if ("active".equals(id)) {
            int newValue = Integer.parseInt((String)value);
            if (newValue != active) {
                active = newValue;
                fireValueChanged();
            }
        }else if ("monstersList".equals(id)) {
            String newValue = (String)value;
            if (!newValue.equals(monstersList)) {
                monstersList = newValue;
                fireValueChanged();
            }
        }
    }

}
