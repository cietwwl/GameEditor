package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.NPCPropertyDescriptor;
import com.pip.game.editor.property.RichTextPropertyDescriptor;
import com.pip.game.editor.property.SkillPropertyDescriptor;

public class A_NpcAttack extends AbstractExpr{

    public int skillId;
    public int level;
    public int active;
    public int target;
    
    /**
     * ����ָ��ȫ�ֱ�����ģ�塣
     * @param name ȫ�ֱ�������
     */
    public A_NpcAttack() {
        skillId = 1;
        level = 1;
        active = 0;
        target = 1;
    }
    
    public IExpr createNew(QuestInfo qinfo) {
        return new A_NpcAttack();
    }

    public String getExpression() {
        return "NpcAttack("  + skillId  + "," + level + "," + active + "," + target +")";
    }

    public String getName() {
        return "NPC����";
    }

    public boolean isCondition() {
        return false;
    }

    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("NpcAttack") && expr.getRightExpr() == null) {
            FunctionCall fc = expr.getLeftExpr().getFunctionCall();
            if (fc.getParamCount() != 4) {
                return null;
            }
            Expression param1 = fc.getParam(0);
            Expression param2 = fc.getParam(1);
            Expression param3 = fc.getParam(2);
            Expression param4 = fc.getParam(3);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER
             && param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER 
             && param3.getRightExpr() == null && param3.getLeftExpr().type == Expr0.TYPE_NUMBER
             && param4.getRightExpr() == null && param4.getLeftExpr().type == Expr0.TYPE_NUMBER)
                    {
                A_NpcAttack ret = (A_NpcAttack)createNew(qinfo);
                ret.skillId = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                ret.level = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
                ret.active = PQEUtils.translateNumberConstant(param3.getLeftExpr().value);
                ret.target = PQEUtils.translateNumberConstant(param4.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    public String toNatureString() {
        switch(target){
            case 0:
                return "NPC������" + skillId +"����" + level + "����" + active + "Ŀ��" + "�ѷ�";
            case 1:
                return "NPC������" + skillId +"����" + level + "����" + active + "Ŀ��" + "�з�";
            case 2:
                return "NPC������" + skillId +"����" + level + "����" + active + "Ŀ��" + "�Լ�";
        }
        return "NPC������" + skillId +"����" + level + "����" + active + "Ŀ��" + target;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] { 
                new SkillPropertyDescriptor("skillId", "����"),
                new TextPropertyDescriptor("level", "����"),
                new TextPropertyDescriptor("active", "����"),
                new ComboBoxPropertyDescriptor("target", "Ŀ��", new String[] { "�ѷ�", "�з�","�Լ�" })
        };
    }

    public Object getPropertyValue(Object id) {
      
        if ("skillId".equals(id)) {
            return new Integer(skillId);
        }else if ("level".equals(id)) {
            return String.valueOf(level);
        }
        else if("active".equals(id)){
            return String.valueOf(active);
        }
        else if("target".equals(id)){
            return target;
        }
        return null;
    }

    public void setPropertyValue(Object id, Object value) {
        if ("skillId".equals(id)) {
            int newValue = ((Integer)value).intValue();
            if (newValue != skillId) {
                skillId = newValue;
                fireValueChanged();
            }
        }
        else if ("level".equals(id)) {
            int newValue = Integer.parseInt((String)value);
            if (newValue != level) {
                level = newValue;
                fireValueChanged();
            }
        }
        else if ("active".equals(id)) {
            int newValue = Integer.parseInt((String)value);
            if (newValue != active) {
                active = newValue;
                fireValueChanged();
            }
        }
        else if ("target".equals(id)) {
            int newValue = (Integer)value;
            if (newValue != target) {
                target = newValue;
                fireValueChanged();
            }
        }
    }

}
