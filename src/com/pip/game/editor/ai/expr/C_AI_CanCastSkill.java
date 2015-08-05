package com.pip.game.editor.ai.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.editor.property.SkillPropertyDescriptor;
import com.pip.game.editor.quest.expr.AbstractExpr;
import com.pip.game.editor.quest.expr.IExpr;

/**
 * 表达式模板，检查是否有足够蓝施放某技能。
 * @author lighthu
 */
public class C_AI_CanCastSkill extends AbstractExpr {
    public int skillID;
    public int skillLevel;
    public boolean checkTrue = true;

    /**
     * 构造指定全局变量的模板。
     * @param name 全局变量名称
     */
    public C_AI_CanCastSkill() {
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
        int id = (skillID << 16) | skillLevel;
        String left = "AI_CanCastSkill(" + id + ")";
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
        return new C_AI_CanCastSkill();
    }

    /**
     * 取得模板名称。
     */
    public String getName() {
        return "是否可以施放某技能...";
    }

    /**
     * 转换为自然语言表示。
     */
    public String toNatureString() {
        if (checkTrue) {
            return "可以施放技能：" + SkillConfig.toString(ProjectData.getActiveProject(), skillID);
        } else {
            return "不可以施放技能：" + SkillConfig.toString(ProjectData.getActiveProject(), skillID);
        }
    }

    /**
     * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("AI_CanCastSkill")) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 1) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER) {
                C_AI_CanCastSkill ret = (C_AI_CanCastSkill)createNew(qinfo);
                int id = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
                ret.skillID = id >> 16;
                ret.skillLevel = id & 0xFFFF;
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
                new SkillPropertyDescriptor("skillID", "技能"),
                new TextPropertyDescriptor("skillLevel", "等级"),
                new ComboBoxPropertyDescriptor("checkTrue", "检查成功", new String[] { "是", "否" })
        };
    }

    /**
     * 取得属性当前值。
     */
    public Object getPropertyValue(Object id) {
        if ("skillID".equals(id)) {
            return new Integer(skillID);
        } else if ("skillLevel".equals(id)) {
            return String.valueOf(skillLevel);
        } else if ("checkTrue".equals(id)) {
            return checkTrue ? 0 : 1;
        }
        return null;
    }

    /**
     * 设置属性当前值。
     */
    public void setPropertyValue(Object id, Object value) {
        if ("skillID".equals(id)) {
            int newValue = ((Integer)value).intValue();
            if (newValue != skillID) {
                skillID = newValue;
                fireValueChanged();
            }
        } else if ("skillLevel".equals(id)) {
            int newValue;
            try {
                newValue = Integer.parseInt((String)value);
                if (newValue < 0) {
                    newValue = 0;
                }
            } catch (Exception e) {
                return;
            }
            if (newValue != skillLevel) {
                skillLevel = newValue;
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
