package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;

public class A_RandomSkill extends AbstractExpr{

    public String skillList;
    
    public A_RandomSkill() {
        skillList = "";
    }
    
    public IExpr createNew(QuestInfo qinfo) {
        return new A_RandomSkill();
    }

    public String getExpression() {
        return "E_RandomSkill(" + "\"" + skillList + "\"" + ")";
    }

    public String getName() {
        return "随机技能";
    }

    public boolean isCondition() {
        return false;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("E_RandomSkill") && expr.getRightExpr() == null) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 1) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_STRING) {
                A_RandomSkill ret = (A_RandomSkill)createNew(qinfo);
                ret.skillList = PQEUtils.translateStringConstant(param1.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    public String toNatureString() {
        return "随机使用技能";
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] { 
                new TextPropertyDescriptor("skillList", "技能列表")
        };
    }

    public Object getPropertyValue(Object id) {
        if ("skillList".equals(id)) {
            return skillList;
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        if ("skillList".equals(id)) {
            String newValue = (String)value;
            if (!newValue.equals(skillList)) {
                skillList = newValue;
                fireValueChanged();
            }
        }
    }

}
