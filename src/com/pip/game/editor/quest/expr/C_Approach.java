package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.LocationPropertyDescriptor;

/**
 * ���ʽģ�壺�ж�һ����ҽӽ���ͼĳ�����¼���
 * @author lighthu
 */
public class C_Approach extends AbstractExpr {
	public int mapID;
	public int x;
	public int y;
	/**
	 * �ӽ�����ľ���
	 */
	public int distance;
	
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_Approach() {
		mapID = 0;
		x = 0;
		y = 0;
		distance = 80;
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
		return "E_Approach(" + mapID + ", " + x + ", " + y + ", " + distance + ")";
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_Approach();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "�ӽ�ĳλ��...";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
        String targetName = GameMapInfo.locationToString(ProjectData.getActiveProject(), new int[] { mapID, x, y }, false);
        return "�ӽ� " + targetName;
	}

	/**
	 * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("E_Approach") && expr.getRightExpr() == null) {
			if (expr.getLeftExpr().getFunctionCall().getParamCount() != 4) {
				return null;
			}
			Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
			Expression param2 = expr.getLeftExpr().getFunctionCall().getParam(1);
			Expression param3 = expr.getLeftExpr().getFunctionCall().getParam(2);
			Expression param4 = expr.getLeftExpr().getFunctionCall().getParam(3);
			if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER &&
				param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER &&
				param3.getRightExpr() == null && param3.getLeftExpr().type == Expr0.TYPE_NUMBER &&
				param4.getRightExpr() == null && param4.getLeftExpr().type == Expr0.TYPE_NUMBER) {
				C_Approach ret = (C_Approach)createNew(qinfo);
				ret.mapID = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
				ret.x = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
				ret.y = PQEUtils.translateNumberConstant(param3.getLeftExpr().value);
				ret.distance = PQEUtils.translateNumberConstant(param4.getLeftExpr().value);
				return ret;
			}
		}
		return null;
	}

	// ������IPropertySource�ӿڵ�ʵ��

	/**
	 * ȡ�����������������ģ����3��������mapID��x��y��
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { 
				new LocationPropertyDescriptor("location", "Ŀ��λ��"),
				new TextPropertyDescriptor("distance", "����")
		};
	}

	/**
	 * ȡ�����Ե�ǰֵ��
	 */
	public Object getPropertyValue(Object id) {
		if ("location".equals(id)) {
            return new int[] { mapID, x, y};
        }else if("distance".equals(id)){
            return String.valueOf(distance);
        }
		return null;
	}

	/**
	 * �������Ե�ǰֵ��
	 */
	public void setPropertyValue(Object id, Object value) {
		if ("location".equals(id)) {
            int[] newValue = (int[])value;
            if (newValue[0] != mapID || newValue[1] != x || newValue[2] != y) {
                mapID = newValue[0];
                x = newValue[1];
                y = newValue[2];
                fireValueChanged();
            }
        }else if("distance".equals(id)){
            int newValue = Integer.parseInt((String)value);
            distance = newValue;
        }
	}
}
