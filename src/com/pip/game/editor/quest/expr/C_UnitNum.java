package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;

public class C_UnitNum extends AbstractExpr{

    public int name;
    public int op;
    public int num;
    
    public C_UnitNum() {
        name = 0;
        op = 1;
        num = 1; 
    }
    
    public IExpr createNew(QuestInfo qinfo) {
        return new C_UnitNum();
    }

    public String getExpression() {
        return "E_UnitNum(" + name  + ","  + op + "," + num + ")";
    }

    public String getName() {
        return "战斗中成员数量";
    }

    public boolean isCondition() {
        return true;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("E_UnitNum") && expr.getRightExpr() == null) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 3) {
                return null;
            }
            Expression param0 = expr.getLeftExpr().getFunctionCall().getParam(0);
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(1);
            Expression param2 = expr.getLeftExpr().getFunctionCall().getParam(2);
            
            if (param0.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER
                  && param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER
                  && param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                C_UnitNum ret = (C_UnitNum)createNew(qinfo);
                ret.name = PQEUtils.translateNumberConstant(param0.getLeftExpr().value);
                ret.op = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                ret.num = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    public String toNatureString() {
        if(name == 0)
        {   
            switch(op){
                case 0:
                    return "友方成员数量" + " > " + num; 
                case 1:
                    return "友方成员数量" + " = " + num;
                case 2:
                    return "友方成员数量" + " < " + num;
            }
        }
        if(name == 1)
        {
            
            switch(op){
                case 0:
                    return "敌方成员数量" + " > " + num; 
                case 1:
                    return "敌方成员数量" + " = " + num;
                case 2:
                    return "敌方成员数量" + " < " + num;
            }
        }
        return "";
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] { 
                new ComboBoxPropertyDescriptor("name","战斗单位",new String[]{"友方成员","敌方成员"}),
                new ComboBoxPropertyDescriptor("op","操作符",new String[]{">","=","<"}),
                new TextPropertyDescriptor("num", "数量")
        };
    }

    public Object getPropertyValue(Object id) {
        if ("name".equals(id)) {
            return name;
        } else if ("op".equals(id)) {
            return op;
        } else if ("num".equals(id)) {
            return String.valueOf(num);
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        if ("name".equals(id)) {
            int newValue = (Integer)value;
            if (newValue != name) {
                name = newValue;
                fireValueChanged();
            }
        } else if ("op".equals(id)) {
            int newValue = (Integer)value;
            if (newValue != op) {
                op = newValue;
                fireValueChanged();
            }
        }  else if ("num".equals(id)) {
            int newValue = Integer.parseInt((String)value);
            if (newValue != num) {
                num = newValue;
                fireValueChanged();
            }
        }      
    }

}
