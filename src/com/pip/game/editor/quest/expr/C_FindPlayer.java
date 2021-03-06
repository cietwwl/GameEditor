package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;

/**
 * 表达式模板：查找一个玩家是否在附近。
 * @author lighthu
 */
public class C_FindPlayer extends AbstractExpr {
    public int playerID;
    public int distance;

    /**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public C_FindPlayer() {
        playerID = -1;
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
        return "FindPlayer(" + playerID + ", " + distance + ")";
    }

    /**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_FindPlayer();
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "某玩家在附近...";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
		return "玩家 " + playerID + " 在附近 " + (distance / 8.00) + " 码内 ";
	}
	
    /**
     * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("FindPlayer") && expr.getRightExpr() == null) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 2) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            Expression param2 = expr.getLeftExpr().getFunctionCall().getParam(1);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                C_FindPlayer ret = (C_FindPlayer)createNew(qinfo);
                ret.playerID = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                ret.distance = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }

    // 下面是IPropertySource接口的实现

    /**
     * 取得属性描述符。这个模板有1个参数：参数。
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] { 
                new TextPropertyDescriptor("playerID", "玩家ID"),
                new TextPropertyDescriptor("distance", "有效距离(码)")
        };
    }

    /**
     * 取得属性当前值。
     */
    public Object getPropertyValue(Object id) {
        if ("playerID".equals(id)) {
            return String.valueOf(playerID);
        } else if ("distance".equals(id)) {
            return String.valueOf(distance / 8.00);
        }
        return null;
    }

    /**
     * 设置属性当前值。
     */
    public void setPropertyValue(Object id, Object value) {
        if ("playerID".equals(id)) {
            int newValue = Integer.parseInt((String)value);
            if (newValue != playerID) {
                playerID = newValue;
                fireValueChanged();
            }
        } else if ("distance".equals(id)) {
            int newValue = (int)(Double.parseDouble((String)value) * 8);
            if (newValue != distance) {
                distance = newValue;
                fireValueChanged();
            }
        }
    }
}
