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
public class A_AlterEyeSight  extends AbstractExpr{
    
    private int sightCount;
    private byte areaSight;
    public IExpr createNew(QuestInfo qinfo) {
        // TODO Auto-generated method stub
        return new A_AlterEyeSight();
    }

    public String getExpression() {
        // TODO Auto-generated method stub
        return "E_AlterEyeSight(" + sightCount + ", " + areaSight + ")";
    }

    public String getName() {
        // TODO Auto-generated method stub
        return "���ӻ谵��Ұ";
    }

    public boolean isCondition() {
        // TODO Auto-generated method stub
        return false;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        // TODO Auto-generated method stub
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("E_AlterEyeSight") && 
                expr.getLeftExpr().getFunctionCall().getParamCount() == 2) {
            Expression param0 = expr.getLeftExpr().getFunctionCall().getParam(0);
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(1);
            if (param0.getRightExpr() == null && param0.getLeftExpr().type == Expr0.TYPE_NUMBER && 
                param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER ) {
                A_AlterEyeSight ret = (A_AlterEyeSight)createNew(qinfo);
                ret.sightCount = PQEUtils.translateNumberConstant(param0.getLeftExpr().value);
                ret.areaSight = (byte) PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    public String toNatureString() {
        // TODO Auto-generated method stub
        return "���ӻ谵��Ұ����" + sightCount + "��Χ" + areaSight;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        // TODO Auto-generated method stub
        return new IPropertyDescriptor[] {
                new TextPropertyDescriptor("sightCount", "����"),
                new ComboBoxPropertyDescriptor("areaSight", "��Χ", new String[] { "��Χ��", "��ɫ������" })
        };
    }

    public Object getPropertyValue(Object id) {
        // TODO Auto-generated method stub
        if(id.equals("sightCount")){
            return Integer.toString(sightCount);
        }else if(id.equals("areaSight")){
            return new Integer(areaSight);
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        if(id.equals("sightCount")){
            int newValue= Integer.parseInt((String)value);
            if(newValue != sightCount){
                sightCount = newValue;
                fireValueChanged();
            }
        }else if(id.equals("areaSight")){
            Integer temp = (Integer)value;
            byte newValue= temp.byteValue();
            if(newValue != areaSight){
                areaSight = newValue;
                fireValueChanged();
            }
        }
    }

}
