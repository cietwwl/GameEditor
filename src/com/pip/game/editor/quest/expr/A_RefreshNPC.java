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
import com.pip.game.editor.property.NPCPropertyDescriptor;
import com.pip.game.editor.property.VariablePropertyDescriptor;

/**
 * 表达式模板：强制刷新NPC。
 * @author lighthu
 */
public class A_RefreshNPC extends AbstractExpr {
	public int npcID;
	public int op;
	
	/**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public A_RefreshNPC(QuestInfo qinfo) {
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
		return new A_RefreshNPC(qinfo);
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "强制刷新NPC";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
		return "强制刷新NPC " + GameMapNPC.toStringShort(ProjectData.getActiveProject(), npcID);
	}
	
	/**
	 * 取得生成的表达式。
	 */
	public String getExpression() {
		return "RefreshNPC(" + npcID + ", " + op + ")";
	}

	/**
	 * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("RefreshNPC") && expr.getRightExpr() == null) {
			FunctionCall fc = expr.getLeftExpr().getFunctionCall();
			if (fc.getParamCount() != 2) {
				return null;
			}
			Expression param1 = fc.getParam(0);
			Expression param2 = fc.getParam(1);
			if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER &&
				param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER) {
				A_RefreshNPC ret = (A_RefreshNPC)createNew(qinfo);
				ret.npcID = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
				ret.op = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
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
				new ComboBoxPropertyDescriptor("op", "操作", new String[] { "查找", "复制" })
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
		}
	}
}
