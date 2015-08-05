package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.SkillPropertyDescriptor;

public class A_EnterStage extends AbstractExpr{
public int stageId;
    
    /**
     * ����ָ��ȫ�ֱ�����ģ�塣
     * @param name ȫ�ֱ�������
     */
    public A_EnterStage() {
        stageId = 1;
    }
    
    public IExpr createNew(QuestInfo qinfo) {
        return new A_EnterStage();
    }

    public String getExpression() {
        return "EnterStage("  + stageId  + ")";
    }

    public String getName() {
        return "����ս���׶�";
    }

    public boolean isCondition() {
        return false;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("EnterStage") && expr.getRightExpr() == null) {
            FunctionCall fc = expr.getLeftExpr().getFunctionCall();
            if (fc.getParamCount() != 1) {
                return null;
            }
            Expression param1 = fc.getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER)
                    {
                A_EnterStage ret = (A_EnterStage)createNew(qinfo);
                ret.stageId = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    public String toNatureString() {
        return "����ս���׶Σ�" + stageId;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] { 
                new TextPropertyDescriptor("stageId", "�׶�ID"),
        };
    }

    public Object getPropertyValue(Object id) {
      
        if ("stageId".equals(id)) {
            return String.valueOf(stageId);
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        if ("stageId".equals(id)) {
            int newValue = Integer.parseInt((String)value);
            if (newValue != stageId) {
                stageId = newValue;
                fireValueChanged();
            }
        }
    }
    
}
