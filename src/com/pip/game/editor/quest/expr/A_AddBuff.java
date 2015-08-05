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
 *  添加buff
 */
public class A_AddBuff extends AbstractExpr {
    
    private int skillId;
    private int level;
    public IExpr createNew(QuestInfo qinfo) {
        // TODO Auto-generated method stub
        return new A_AddBuff();
    }

    public String getExpression() {
        // TODO Auto-generated method stub
        return "E_AddBuff(" + skillId + "," + level + ")";
    }

    public String getName() {
        // TODO Auto-generated method stub
        return "添加buff";
    }

    public boolean isCondition() {
        // TODO Auto-generated method stub
        return false;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        // TODO Auto-generated method stub
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("E_AddBuff") && expr.getRightExpr() == null) {
            FunctionCall fc = expr.getLeftExpr().getFunctionCall();
            if (fc.getParamCount() != 2) {
                return null;
            }
            Expression param1 = fc.getParam(0);
            Expression param2 = fc.getParam(1);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                A_AddBuff ret = (A_AddBuff)createNew(null);
                ret.skillId = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                ret.level = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    public String toNatureString() {
        // TODO Auto-generated method stub
        return "添加buff " + skillId + " 级别 " + level;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        // TODO Auto-generated method stub
        return new IPropertyDescriptor[] { 
                new BuffPropertyDescriptor("skillId", "添加BUFF", BuffPropertyDescriptor.UseAllBuff),
                new TextPropertyDescriptor("level", "级别")
        };
    }

    public Object getPropertyValue(Object id) {
        // TODO Auto-generated method stub
        if ("skillId".equals(id)) {
            return skillId;
        }else if ("level".equals(id)) {
            return String.valueOf(level);
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        // TODO Auto-generated method stub
        if ("skillId".equals(id)) {
            int newValue = (Integer)value;
            if (newValue != skillId) {
                skillId = newValue;
                fireValueChanged();
            }
        } else if ("level".equals(id)) {
            int newValue = Integer.parseInt((String)value);
            if (newValue != level) {
                level = newValue;
                fireValueChanged();
            }
        }
    }

}
