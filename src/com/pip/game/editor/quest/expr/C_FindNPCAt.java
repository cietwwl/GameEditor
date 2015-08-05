package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.LocationPropertyDescriptor;
import com.pip.game.editor.property.VariablePropertyDescriptor;

/**
 * ���ʽģ�壺����һ��NPC�Ƿ���ĳ�㸽����
 * @author lighthu
 */
public class C_FindNPCAt extends AbstractExpr {
    public String varName;
    public int mapID;
    public int x;
    public int y;
    public int distance;
    public boolean checkTrue = true;
    
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_FindNPCAt(QuestInfo qinfo) {
	    varName = "�±���";
	    mapID = 0;
	    x = 0;
	    y = 0;
	    distance = 80;
	    questInfo = qinfo;
	}
	
    /**
     * �ж����ģ����һ����������һ��������
     */
    public boolean isCondition() {
        return true;
    }

    /**
     * ȡ�����ɵı��ʽ��
     */
    public String getExpression() {
        if (checkTrue) {
            return "FindNPCAt(" + varName + ", " + mapID + ", " + x + ", " + y + ", " + distance + ")";
        } else {
            return "FindNPCAt(" + varName + ", " + mapID + ", " + x + ", " + y + ", " + distance + ") == 0";
        }
    }

    /**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_FindNPCAt(qinfo);
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "NPC��ĳλ�ø���...";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
	    String targetName = GameMapInfo.locationToString(ProjectData.getActiveProject(), new int[] { mapID, x, y }, false);
	    if (checkTrue) {
	        return "NPC " + varName + " �� " + targetName + " ���� " + (distance / 8.00) + " ���� ";
	    } else {
	        return "NPC " + varName + " ���� " + targetName + " ���� " + (distance / 8.00) + " ���� ";
	    }
	}

    /**
     * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("FindNPCAt")) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 5) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            Expression param2 = expr.getLeftExpr().getFunctionCall().getParam(1);
            Expression param3 = expr.getLeftExpr().getFunctionCall().getParam(2);
            Expression param4 = expr.getLeftExpr().getFunctionCall().getParam(3);
            Expression param5 = expr.getLeftExpr().getFunctionCall().getParam(4);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_IDENTIFIER &&
                param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                param3.getRightExpr() == null && param3.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                param4.getRightExpr() == null && param4.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                param5.getRightExpr() == null && param5.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                C_FindNPCAt ret = (C_FindNPCAt)createNew(qinfo);
                ret.varName = param1.getLeftExpr().value;
                ret.mapID = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
                ret.x = PQEUtils.translateNumberConstant(param3.getLeftExpr().value);
                ret.y = PQEUtils.translateNumberConstant(param4.getLeftExpr().value);
                ret.distance = PQEUtils.translateNumberConstant(param5.getLeftExpr().value);
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

    // ������IPropertySource�ӿڵ�ʵ��

    /**
     * ȡ�����������������ģ����1��������������
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] { 
                new VariablePropertyDescriptor("varName", "����ID�ı���", questInfo, true),
                new LocationPropertyDescriptor("location", "Ŀ��λ��"),
                new TextPropertyDescriptor("distance", "��Ч����(��)"),
                new ComboBoxPropertyDescriptor("checkTrue", "��������", new String[] { "��", "��" })
        };
    }

    /**
     * ȡ�����Ե�ǰֵ��
     */
    public Object getPropertyValue(Object id) {
        if ("varName".equals(id)) {
            return varName;
        } else if ("distance".equals(id)) {
            return String.valueOf(distance / 8.00);
        } else if ("checkTrue".equals(id)) {
            return checkTrue ? 0 : 1;
        } else if ("location".equals(id)) {
            return new int[] { mapID, x, y };
        }
        return null;
    }

    /**
     * �������Ե�ǰֵ��
     */
    public void setPropertyValue(Object id, Object value) {
        if ("varName".equals(id)) {
            String newValue = (String)value;
            if (!newValue.equals(varName)) {
                varName = newValue;
                fireValueChanged();
            }
        } else if ("distance".equals(id)) {
            int newValue = (int)(Double.parseDouble((String)value) * 8);
            if (newValue != distance) {
                distance = newValue;
                fireValueChanged();
            }
        } else if ("checkTrue".equals(id)) {
            boolean newValue = ((Integer)value).intValue() == 0;
            if (newValue != checkTrue) {
                checkTrue = newValue;
                fireValueChanged();
            }
        } else if ("location".equals(id)) {
            int[] newValue = (int[])value;
            if (newValue[0] != mapID || newValue[1] != x || newValue[2] != y) {
                mapID = newValue[0];
                x = newValue[1];
                y = newValue[2];
                fireValueChanged();
            }
        }
    }
}
