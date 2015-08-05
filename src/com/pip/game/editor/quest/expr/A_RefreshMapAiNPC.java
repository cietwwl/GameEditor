package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

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
 * ���ʽģ�壺ˢ��NPC(����AI)��
 * @author lighthu
 */
public class A_RefreshMapAiNPC extends AbstractExpr {
	public int npcID;
	public int mapID;
	public int x;
	public int y;
	
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public A_RefreshMapAiNPC(QuestInfo qinfo) {
		npcID = -1;
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
		return new A_RefreshMapAiNPC(qinfo);
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "ˢ��NPC(����AI)";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "ˢ��NPC(����AI) " + GameMapNPC.toStringShort(ProjectData.getActiveProject(), npcID);
	}
	
	/**
	 * ȡ�����ɵı��ʽ��
	 */
	public String getExpression() {
		return "RefreshMapAiNPC(" + npcID + ", " + x  + ", " + y +")";
	}

	/**
	 * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("RefreshMapAiNPC") && expr.getRightExpr() == null) {
			FunctionCall fc = expr.getLeftExpr().getFunctionCall();
			if (fc.getParamCount() != 3) {
				return null;
			}
			Expression param1 = fc.getParam(0);
			Expression param2 = fc.getParam(1);
			Expression param3 = fc.getParam(2);
			if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER &&
				param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER &&
				param3.getRightExpr() == null && param3.getLeftExpr().type == Expr0.TYPE_NUMBER) {
				A_RefreshMapAiNPC ret = (A_RefreshMapAiNPC)createNew(qinfo);
				ret.npcID = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
				ret.x = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
				ret.y = PQEUtils.translateNumberConstant(param3.getLeftExpr().value);
				return ret;
			}
		}
		return null;
	}

	// ������IPropertySource�ӿڵ�ʵ��

	/**
	 * ȡ��������������
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { 
				new NPCPropertyDescriptor("npcID", "Ŀ��NPC"),
				new LocationPropertyDescriptor("location", "Ŀ��λ��")
		};
	}

	/**
	 * ȡ�����Ե�ǰֵ��
	 */
	public Object getPropertyValue(Object id) {
		if ("npcID".equals(id)) {
			return new Integer(npcID);
		} else if ("location".equals(id)) {
            return new int[] { mapID, x, y};
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
