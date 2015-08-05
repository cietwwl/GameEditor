package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.LocationPropertyDescriptor;
import com.pip.game.editor.property.NPCPropertyDescriptor;
import com.pip.game.editor.property.VariablePropertyDescriptor;

/**
 * ���ʽģ�壺��ָ��λ��ǿ��ˢ��NPC��
 * @author lighthu
 */
public class A_RefreshNPCAt extends AbstractExpr {
	public int npcID;
	public int op;
	public int mapID;
	public int x;
	public int y;
	public int range;
	
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public A_RefreshNPCAt(QuestInfo qinfo) {
		npcID = -1;
		op = 1;
		questInfo = qinfo;
	}
	
	/**
	 * �ж����ģ����һ����������һ��������
	 */
	public boolean isCondition() {
		return false;
	}

	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new A_RefreshNPCAt(qinfo);
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "��ָ��λ��ǿ��ˢ��NPC";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "��ָ��λ��ǿ��ˢ��NPC " + GameMapNPC.toStringShort(ProjectData.getActiveProject(), npcID);
	}
	
	/**
	 * ȡ�����ɵı��ʽ��
	 */
	public String getExpression() {
		return "RefreshNPCAt(" + npcID + ", " + op + ", " + mapID + ", " + x + ", " + y + ", " + range + ")";
	}

	/**
	 * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("RefreshNPCAt") && expr.getRightExpr() == null) {
			FunctionCall fc = expr.getLeftExpr().getFunctionCall();
			if (fc.getParamCount() != 6) {
				return null;
			}
			Expression param1 = fc.getParam(0);
			Expression param2 = fc.getParam(1);
            Expression param3 = fc.getParam(2);
            Expression param4 = fc.getParam(3);
            Expression param5 = fc.getParam(4);
			Expression param6 = fc.getParam(5);
			if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER &&
				param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                param3.getRightExpr() == null && param3.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                param4.getRightExpr() == null && param4.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                param5.getRightExpr() == null && param5.getLeftExpr().type == Expr0.TYPE_NUMBER &&
				param6.getRightExpr() == null && param6.getLeftExpr().type == Expr0.TYPE_NUMBER) {
				A_RefreshNPCAt ret = (A_RefreshNPCAt)createNew(qinfo);
				ret.npcID = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                ret.op = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
                ret.mapID = PQEUtils.translateNumberConstant(param3.getLeftExpr().value);
                ret.x = PQEUtils.translateNumberConstant(param4.getLeftExpr().value);
                ret.y = PQEUtils.translateNumberConstant(param5.getLeftExpr().value);
                ret.range = PQEUtils.translateNumberConstant(param6.getLeftExpr().value);
				return ret;
			}
		}
		return null;
	}

	// ������IPropertySource�ӿڵ�ʵ��

	/**
	 * ȡ�����������������ģ����2���������ַ�������������������
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { 
				new NPCPropertyDescriptor("npcID", "Ŀ��NPC"),
				new ComboBoxPropertyDescriptor("op", "����", new String[] { "����", "����" }),
				new LocationPropertyDescriptor("location", "ˢ��λ��"),
				new TextPropertyDescriptor("range", "����뾶(��)")
		};
	}

	/**
	 * ȡ�����Ե�ǰֵ��
	 */
	public Object getPropertyValue(Object id) {
		if ("npcID".equals(id)) {
			return new Integer(npcID);
		} else if ("op".equals(id)) {
		    return new Integer(op);
		} else if ("location".equals(id)) {
		    return new int[] { mapID, x, y };
		} else if ("range".equals(id)) {
		    return String.valueOf(range / 8.00);
		}
		return null;
	}

	/**
	 * �������Ե�ǰֵ��
	 */
	public void setPropertyValue(Object id, Object value) {
		if ("npcID".equals(id)) {
			int newValue = ((Integer)value).intValue();
			if (newValue != npcID) {
				npcID = newValue;
				fireValueChanged();
			}
		} else if ("op".equals(id)) {
            int newValue = ((Integer)value).intValue();
            if (newValue != op) {
                op = newValue;
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
		} else if ("range".equals(id)) {
		    int newValue = (int)(Double.parseDouble((String)value) * 8);
            if (newValue != range) {
                range = newValue;
                fireValueChanged();
            }
		}
	}
}
