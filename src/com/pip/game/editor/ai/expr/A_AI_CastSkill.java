package com.pip.game.editor.ai.expr;

import java.util.ArrayList;
import java.util.List;

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
import com.pip.util.Utils;

/**
 * 表达式模板，随机立刻施放一个技能。
 * @author lighthu
 */
public class A_AI_CastSkill extends AbstractExpr {
    public int[] skillIDs = new int[10];
    public int[] skillLevels = new int[10];

    /**
     * 构造指定全局变量的模板。
     * @param name 全局变量名称
     */
    public A_AI_CastSkill() {
    }
    
    /**
     * 判断这个模板是一个条件还是一个动作。
     */
    public boolean isCondition() {
        return false;
    }

    /**
     * 取得生成的表达式。
     */
    public String getExpression() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < skillIDs.length; i++) {
            if (skillIDs[i] > 0) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append((skillIDs[i] << 16) | skillLevels[i]);
            }
        }
        return "AI_CastSkill(\"" + sb.toString() + "\")";
    }

    /**
     * 用模板创建新的表达式片段。
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new A_AI_CastSkill();
    }

    /**
     * 取得模板名称。
     */
    public String getName() {
        return "立刻施放技能...";
    }

    /**
     * 转换为自然语言表示。
     */
    public String toNatureString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < skillIDs.length; i++) {
            if (skillIDs[i] > 0) {
                if (sb.length() > 0) {
                    sb.append(" | ");
                }
                sb.append(SkillConfig.toString(ProjectData.getActiveProject(), skillIDs[i]));
            }
        }
        return "立刻施放技能：" +  sb.toString();
    }
    
    /**
     * 解析格式为id1,id2,id3的技能列表，设置skillIDs和skillLevels数组。
     * @param str
     */
    protected void parseSkillList(String str) {
        String[] ids = Utils.splitString(str, ',');
        for (int i = 0; i < ids.length; i++) {
            try {
                int id = Integer.parseInt(ids[i]);
                skillIDs[i] = id >> 16;
                skillLevels[i] = id & 0xFFFF;
            } catch (Exception e) {
            }
        }
    }

    /**
     * 识别一个表达式是否匹配本模板。如果匹配，返回一个新的表达式片段对象，否则返回null。
     */
    public IExpr recognize(QuestInfo qinfo, Expression expr) {
        if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("AI_CastSkill") && expr.getRightExpr() == null) {
            if (expr.getLeftExpr().getFunctionCall().getParamCount() != 1) {
                return null;
            }
            Expression param1 = expr.getLeftExpr().getFunctionCall().getParam(0);
            if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_STRING) {
                A_AI_CastSkill ret = (A_AI_CastSkill)createNew(qinfo);
                ret.parseSkillList(PQEUtils.translateStringConstant(param1.getLeftExpr().value));
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
        List<IPropertyDescriptor> list = new ArrayList<IPropertyDescriptor>();
        for (int i = 0; i < skillIDs.length; i++) {
            list.add(new SkillPropertyDescriptor("skillid" + i, "技能"));
            list.add(new TextPropertyDescriptor("skilllevel" + i, "等级"));
        }
        IPropertyDescriptor[] ret = new IPropertyDescriptor[list.size()];
        list.toArray(ret);
        return ret;
    }

    /**
     * 取得属性当前值。
     */
    public Object getPropertyValue(Object id) {
        String idStr = (String)id;
        if (idStr.startsWith("skillid")) {
            int index = Integer.parseInt(idStr.substring("skillid".length()));
            return new Integer(skillIDs[index]);
        } else if (idStr.startsWith("skilllevel")) {
            int index = Integer.parseInt(idStr.substring("skilllevel".length()));
            return String.valueOf(skillLevels[index]);
        }
        return null;
    }

    /**
     * 设置属性当前值。
     */
    public void setPropertyValue(Object id, Object value) {
        String idStr = (String)id;
        if (idStr.startsWith("skillid")) {
            int index = Integer.parseInt(idStr.substring("skillid".length()));
            int newValue = ((Integer)value).intValue();
            if (newValue != skillIDs[index]) {
                skillIDs[index] = newValue;
                fireValueChanged();
            }
        } else if (idStr.startsWith("skilllevel")) {
            int index = Integer.parseInt(idStr.substring("skilllevel".length()));
            int newValue;
            try {
                newValue = Integer.parseInt((String)value);
            } catch (Exception e) {
                return;
            }
            if (newValue != skillLevels[index]) {
                skillLevels[index] = newValue;
                fireValueChanged();
            }
        }
    }
}
