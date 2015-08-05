package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;

/**
 * 表达式模板：判断是否使用了某物品。
 * @author lighthu
 */
public class C_HasAction extends AbstractExpr {
    
    public int type;
	/**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public C_HasAction() {
	    type = -1;
	}
	
	/**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_HasAction();
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "玩家做过动作...";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
	    return "玩家做过动作 " + type;
	}
	
	/**
     * 取得属性描述符。这个模板有1个参数：参数。
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] { 
                new ComboBoxPropertyDescriptor("type","动作",new String[]{"星级鉴定","资质鉴定","镶嵌宝石"}),
        };
    }

    /**
     * 取得属性当前值。
     */
    public Object getPropertyValue(Object id) {
        if ("type".equals(id)) {
            return new Integer(type);
        }
        return null;
    }

    /**
     * 设置属性当前值。
     */
    public void setPropertyValue(Object id, Object value) {
        if ("type".equals(id)) {
            int newValue = ((Integer)value).intValue();
            if (newValue != type) {
                type = newValue;
                fireValueChanged();
            }
        }
    }

    public String getExpression() {
        return "E_HasAction(" + type + ")";
    }

    public boolean isCondition() {
        return true;
    }

    /**
     * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("E_HasAction") && expr.getRightExpr() == null) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 1) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                C_HasAction ret = (C_HasAction)createNew(qinfo);
                ret.type = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                return ret;
            }
        }
        return null;
    }
    
    
}
