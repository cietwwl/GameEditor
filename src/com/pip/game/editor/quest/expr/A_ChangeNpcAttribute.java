package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;

/**
 * @author wpjiang
 *  �ı�npc������
 */
public class A_ChangeNpcAttribute  extends AbstractExpr{
    private int attrID;
    private int value;

    public IExpr createNew(QuestInfo qinfo) {
        return new A_ChangeNpcAttribute();
    }

    public String getExpression() {
        return "ChangeNpcAttribute(" + attrID + ", " + value + ")";
    }

    public String getName() {
        return "�ı�NPC����";
    }

    public boolean isCondition() {
        return false;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        // TODO Auto-generated method stub
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("ChangeNpcAttribute") && 
                expr.getLeftExpr().getFunctionCall().getParamCount() == 2) {
            Expression param0 = expr.getLeftExpr().getFunctionCall().getParam(0);
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(1);
            if (param0.getRightExpr() == null && param0.getLeftExpr().type == Expr0.TYPE_NUMBER && 
                param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER
                ) {
                A_ChangeNpcAttribute ret = (A_ChangeNpcAttribute)createNew(qinfo);
                ret.attrID = PQEUtils.translateNumberConstant(param0.getLeftExpr().value);
                ret.value = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    public String toNatureString() {
        return "�ı�NPC����" + attrID + ": " + value;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] {
                new ComboBoxPropertyDescriptor("attrid", "����", new String[] { "����", "����", "�����ٷֱ�", "�����ٷֱ�", "��Ӫ", "����", "�Ƿ�ɼ�(1=��)" }),
                new TextPropertyDescriptor("value", "��ֵ")
        };
    }

    public Object getPropertyValue(Object id) {
        if (id.equals("attrid")) {
            return new Integer(attrID);
        } else if (id.equals("value")) {
            return String.valueOf(value);
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        if (id.equals("attrid")) {
            int newValue= ((Integer)value).intValue();
            if (newValue != attrID) {
                attrID = newValue;
                fireValueChanged();
            }
        } else if(id.equals("value")) {
            int newValue= Integer.parseInt((String)value);
            if (newValue != this.value) {
                this.value = newValue;
                fireValueChanged();
            }
        }
    }
}
