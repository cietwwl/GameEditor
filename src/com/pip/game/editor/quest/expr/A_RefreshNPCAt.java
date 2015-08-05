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
 * 表达式模板：在指定位置强制刷新NPC。
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
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public A_RefreshNPCAt(QuestInfo qinfo) {
		npcID = -1;
		op = 1;
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
		return new A_RefreshNPCAt(qinfo);
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "在指定位置强制刷新NPC";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
		return "在指定位置强制刷新NPC " + GameMapNPC.toStringShort(ProjectData.getActiveProject(), npcID);
	}
	
	/**
	 * 取得生成的表达式。
	 */
	public String getExpression() {
		return "RefreshNPCAt(" + npcID + ", " + op + ", " + mapID + ", " + x + ", " + y + ", " + range + ")";
	}

	/**
	 * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
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

	// 下面是IPropertySource接口的实现

	/**
	 * 取得属性描述符。这个模板有2个参数：字符串参数和整数参数。
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { 
				new NPCPropertyDescriptor("npcID", "目标NPC"),
				new ComboBoxPropertyDescriptor("op", "操作", new String[] { "查找", "复制" }),
				new LocationPropertyDescriptor("location", "刷新位置"),
				new TextPropertyDescriptor("range", "随机半径(码)")
		};
	}

	/**
	 * 取得属性当前值。
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
	 * 设置属性当前值。
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
