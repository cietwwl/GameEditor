package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.SkillPropertyDescriptor;

public class C_SkillCountAI extends AbstractExpr{

    public int skillId;
    public int count;
    
    public C_SkillCountAI(){
        skillId = 1;
        count = 1;
    }
    
    public IExpr createNew(QuestInfo qinfo) {
        return new C_SkillCountAI();
    }

    public String getExpression() {
        return "E_SkillCount(" + skillId  + ","  + count + ")";
    }

    public String getName() {
        return "怪物使用技能次数";
    }

    public boolean isCondition() {
        return true;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("E_SkillCount") && expr.getRightExpr() == null) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 2) {
                return null;
            }
            Expression param0 = expr.getLeftExpr().getFunctionCall().getParam(0);
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(1);
            
            if (param0.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER
                  && param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER)
                {
                C_SkillCountAI ret = (C_SkillCountAI)createNew(qinfo);
                ret.skillId = PQEUtils.translateNumberConstant(param0.getLeftExpr().value);
                ret.count = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    public String toNatureString() {
        return "怪物使用技能 "+ skillId +" 次数达到 " + count;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] { 
                new SkillPropertyDescriptor("skillId", "技能ID"),
                new TextPropertyDescriptor("count", "使用次数")
        };          
    }

    public Object getPropertyValue(Object id) {
        if ("skillId".equals(id)) {
            return skillId;
        } else if ("count".equals(id)) {
            return String.valueOf(count);
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        if ("skillId".equals(id)) {
            int newValue = (Integer)value;
            if (newValue != skillId) {
                skillId = newValue;
                fireValueChanged();
            }
        } else if ("count".equals(id)) {
            int newValue = Integer.parseInt((String)value);
            if (newValue != count) {
                count = newValue;
                fireValueChanged();
            }
        }
        
    }

}
