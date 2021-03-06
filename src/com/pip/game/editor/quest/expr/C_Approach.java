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
 * 表达式模板：判断一个玩家接近地图某个点事件。
 * @author lighthu
 */
public class C_Approach extends AbstractExpr {
	public int mapID;
	public int x;
	public int y;
	/**
	 * 接近任务的距离
	 */
	public int distance;
	
	/**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public C_Approach() {
		mapID = 0;
		x = 0;
		y = 0;
		distance = 80;
	}
	
	/**
	 * 判断这个模板是一个条件还是一个动作。
	 */
	public boolean isCondition() {
		return true;
	}

	/**
	 * 取得生成的表达式。
	 */
	public String getExpression() {
		return "E_Approach(" + mapID + ", " + x + ", " + y + ", " + distance + ")";
	}
	
	/**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_Approach();
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "接近某位置...";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
        String targetName = GameMapInfo.locationToString(ProjectData.getActiveProject(), new int[] { mapID, x, y }, false);
        return "接近 " + targetName;
	}

	/**
	 * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
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

	// 下面是IPropertySource接口的实现

	/**
	 * 取得属性描述符。这个模板有3个参数：mapID，x，y。
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { 
				new LocationPropertyDescriptor("location", "目标位置"),
				new TextPropertyDescriptor("distance", "距离")
		};
	}

	/**
	 * 取得属性当前值。
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
	 * 设置属性当前值。
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
