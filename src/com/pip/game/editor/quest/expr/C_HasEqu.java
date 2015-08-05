package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.ItemPropertyDescriptor;

/**
 * ����ʽģ�壺�ж��Ƿ���ָ��װ��
 * @author Jeffrey
 */
public class C_HasEqu extends AbstractExpr {
    public boolean checkTrue = true;
    public int itemID;
    
    /**
     * ����ָ��ȫ�ֱ�����ģ�塣
     * @param name ȫ�ֱ�������
     */
    public C_HasEqu() {
    }
    
    /**
     * �ж����ģ����һ����������һ��������
     */
    public boolean isCondition() {
        return true;
    }

    /**
     * ȡ�����ɵı���ʽ��
     */
    public String getExpression() {
        if (checkTrue) {
            return "HasEqu(" + itemID +")";
        } else {
            return "HasEqu(" + itemID +") == false";
        }
    }

    /**
     * ʶ��һ������ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı���ʽƬ�ζ��󣬷��򷵻�null��
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("HasEqu") && 
                expr.getLeftExpr().getFunctionCall().getParamCount() == 1) {
            Expression param0 = expr.getLeftExpr().getFunctionCall().getParam(0);
            if (param0.getRightExpr() == null && param0.getLeftExpr().type == Expr0.TYPE_NUMBER ) {
                C_HasEqu ret = (C_HasEqu)createNew(qinfo);
                ret.itemID = PQEUtils.translateNumberConstant(param0.getLeftExpr().value);
                if (expr.getRightExpr() == null) {
                    return ret;
                } else if (expr.getRightExpr().type == Expr0.TYPE_NUMBER && PQEUtils.translateNumberConstant(expr.getRightExpr().value) == 0) {
                    ret.checkTrue = false;
                    return ret;
                }
            }
        }
        return null;
    }

    /**
     * ��ģ�崴���µı���ʽƬ�Ρ�
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new C_HasEqu();
    }

    /**
     * ȡ��ģ�����ơ�
     */
    public String getName() {
        return "�Ƿ�װ��...";
    }

    /**
     * ת��Ϊ��Ȼ���Ա�ʾ��
     */
    public String toNatureString() {
        if (checkTrue) {
            return "װ��  " + ProjectData.getActiveProject().findItemOrEquipment(itemID);
        } else {
            return "δװ�� " + ProjectData.getActiveProject().findItemOrEquipment(itemID);
        }
    }

    // ������IPropertySource�ӿڵ�ʵ��

    /**
     * ȡ�����������������ģ����1��������������
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] {
                new ItemPropertyDescriptor("itemID", "װ��"),
                new ComboBoxPropertyDescriptor("checkTrue", "��������", new String[] { "��", "��" })
        };
    }

    /**
     * ȡ�����Ե�ǰֵ��
     */
    public Object getPropertyValue(Object id) {
        if ("itemID".equals(id)) {
            return itemID;
        } else if ("checkTrue".equals(id)) {
            return checkTrue ? 0 : 1;
        }
        return null;
    }

    /**
     * �������Ե�ǰֵ��
     */
    public void setPropertyValue(Object id, Object value) {
        if ("itemID".equals(id)) {
            int newValue = (Integer)value;
            if (newValue != itemID) {
                itemID = newValue;
                fireValueChanged();
            }
        } else if ("checkTrue".equals(id)) {
            boolean newValue = ((Integer)value).intValue() == 0;
            if (newValue != checkTrue) {
                checkTrue = newValue;
                fireValueChanged();
            }
        }   
    }
}