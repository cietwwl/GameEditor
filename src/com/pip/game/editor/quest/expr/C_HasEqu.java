package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.ItemPropertyDescriptor;

/**
 * 表达式模板：判断是否穿了指定装备
 * @author Jeffrey
 */
public class C_HasEqu extends AbstractExpr {
    public boolean checkTrue = true;
    public int itemID;
    
    /**
     * 构造指定全局变量的模板。
     * @param name 全局变量名称
     */
    public C_HasEqu() {
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
        if (checkTrue) {
            return "HasEqu(" + itemID +")";
        } else {
            return "HasEqu(" + itemID +") == false";
        }
    }

    /**
     * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("HasEqu") && 
                expr.getLeftExpr().getFunctionCall().getParamCount() == 1) {
            Expression param0 = expr.getLeftExpr().getFunctionCall().getParam(0);
            if (param0.getRightExpr() == null && param0.getLeftExpr().type == Expr0.TYPE_NUMBER ) {
                C_HasEqu ret = (C_HasEqu)createNew(qinfo);
                ret.itemID = PQEUtils.translateNumberConstant(param0.getLeftExpr().value);
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

    /**
     * 用模板创建新的表达式片段。
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new C_HasEqu();
    }

    /**
     * 取得模板名称。
     */
    public String getName() {
        return "是否装备...";
    }

    /**
     * 转换为自然语言表示。
     */
    public String toNatureString() {
        if (checkTrue) {
            return "装备  " + ProjectData.getActiveProject().findItemOrEquipment(itemID);
        } else {
            return "未装备 " + ProjectData.getActiveProject().findItemOrEquipment(itemID);
        }
    }

    // 下面是IPropertySource接口的实现

    /**
     * 取得属性描述符。这个模板有1个参数：参数。
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return new IPropertyDescriptor[] {
                new ItemPropertyDescriptor("itemID", "装备"),
                new ComboBoxPropertyDescriptor("checkTrue", "条件成立", new String[] { "是", "否" })
        };
    }

    /**
     * 取得属性当前值。
     */
    public Object getPropertyValue(Object id) {
        if ("itemID".equals(id)) {
            return itemID;
        } else if ("checkTrue".equals(id)) {
            return checkTrue ? 0 : 1;
        }
        return null;
    }

    /**
     * 设置属性当前值。
     */
    public void setPropertyValue(Object id, Object value) {
        if ("itemID".equals(id)) {
            int newValue = (Integer)value;
            if (newValue != itemID) {
                itemID = newValue;
                fireValueChanged();
            }
        } else if ("checkTrue".equals(id)) {
            boolean newValue = ((Integer)value).intValue() == 0;
            if (newValue != checkTrue) {
                checkTrue = newValue;
                fireValueChanged();
            }
        }   
    }
}
