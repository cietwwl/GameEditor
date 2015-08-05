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
 * 表达式模板：刷新NPC(场景AI)。
 * @author lighthu
 */
public class A_RefreshMapAiNPC extends AbstractExpr {
	public int npcID;
	public int mapID;
	public int x;
	public int y;
	
	/**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public A_RefreshMapAiNPC(QuestInfo qinfo) {
		npcID = -1;
		questInfo = qinfo;
	}
	
	/**
	 * 判断这个模板是一个条件还是一个动作。
	 */
	public boolean isCondition() {
		return false;
	}

	/**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new A_RefreshMapAiNPC(qinfo);
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "刷新NPC(场景AI)";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
		return "刷新NPC(场景AI) " + GameMapNPC.toStringShort(ProjectData.getActiveProject(), npcID);
	}
	
	/**
	 * 取得生成的表达式。
	 */
	public String getExpression() {
		return "RefreshMapAiNPC(" + npcID + ", " + x  + ", " + y +")";
	}

	/**
	 * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
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

	// 下面是IPropertySource接口的实现

	/**
	 * 取得属性描述符。
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { 
				new NPCPropertyDescriptor("npcID", "目标NPC"),
				new LocationPropertyDescriptor("location", "目标位置")
		};
	}

	/**
	 * 取得属性当前值。
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
	 * 设置属性当前值。
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
