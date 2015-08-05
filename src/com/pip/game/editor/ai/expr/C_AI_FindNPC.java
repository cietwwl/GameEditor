package com.pip.game.editor.ai.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.property.NPCPropertyDescriptor;
import com.pip.game.editor.quest.expr.AbstractExpr;
import com.pip.game.editor.quest.expr.IExpr;

/**
 * 表达式模板，查找NPC。
 * @author lighthu
 */
public class C_AI_FindNPC extends AbstractExpr {
    public int npcID;
    public int dist = 160;
    public boolean checkTrue = true;

    /**
     * 构造指定全局变量的模板。
     * @param name 全局变量名称
     */
    public C_AI_FindNPC() {
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
        String left = "AI_FindNPC(" + npcID + "," + dist + ")";
        if (checkTrue) {
            return left;
        } else {
            return left + " == false";
        }
    }

    /**
     * 用模板创建新的表达式片段。
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new C_AI_FindNPC();
    }

    /**
     * 取得模板名称。
     */
    public String getName() {
        return "查找NPC...";
    }

    /**
     * 转换为自然语言表示。
     */
    public String toNatureString() {
        if (checkTrue) {
            return "在" + (dist / 8.0) + "码范围内找到" + GameMapNPC.toStringShort(ProjectData.getActiveProject(), npcID);
        } else {
            return "在" + (dist / 8.0) + "码范围内未找到" + GameMapNPC.toStringShort(ProjectData.getActiveProject(), npcID);
        }
    }

    /**
     * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("AI_FindNPC")) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 2) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            Expression param2 = expr.getLeftExpr().getFunctionCall().getParam(1);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER &&
                    param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                C_AI_FindNPC ret = (C_AI_FindNPC)createNew(qinfo);
                ret.npcID = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                ret.dist = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
                if (expr.getRightExpr() == null) {
                    return ret;
                } else if (expr.getRightExpr().type == Expr0.TYPE_IDENTIFIER && expr.getRightExpr().value.equals("false")) {
                    ret.checkTrue = false;
                    return ret;
                }
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
                new NPCPropertyDescriptor("npcID", "目标NPC"),
                new TextPropertyDescriptor("dist", "范围半径(码)"),
                new ComboBoxPropertyDescriptor("checkTrue", "检查成功", new String[] { "是", "否" })
        };
    }

    /**
     * 取得属性当前值。
     */
    public Object getPropertyValue(Object id) {
        if ("npcID".equals(id)) {
            return new Integer(npcID);
        } else if ("dist".equals(id)) {
            return String.valueOf(dist / 8.0);
        } else if ("checkTrue".equals(id)) {
            return checkTrue ? 0 : 1;
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
        } else if ("dist".equals(id)) {
            int newValue;
            try {
                newValue = (int)(Double.parseDouble((String)value) * 8);
            } catch (Exception e) {
                return;
            }
            if (newValue != dist) {
                dist = newValue;
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
