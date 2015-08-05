package com.pip.game.editor.ai;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import com.pip.game.data.AI.AIRuleConfig;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.QuestVariable;
import com.pip.game.editor.property.AILogicPropertyDescriptor;

public class AIRuleConfigPropertySource implements IPropertySource {
    public static final int COMBAT_STATUS = 1;
    public static final int COMMON_STATUS = 2;
    int status;
    AIRuleConfig rule;
    Viewer viewer;
    AIEditor aiEditor;
    int contextMask;

    // public AIRuleConfigPropertySource(AIRuleConfig rule) {
    // this.rule = rule;
    // }

    public AIRuleConfigPropertySource(AIEditor aiEditor, AIRuleConfig rule, Viewer viewer, int status, int contextMask) {
        this.rule = rule;
        this.viewer = viewer;
        this.status = status;
        this.aiEditor = aiEditor;
        this.contextMask = contextMask;
    }

    public Object getEditableValue() {
        return this;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] ret = new IPropertyDescriptor[3];

        ret[0] = new AILogicPropertyDescriptor("进入阶段定义", "进入阶段定义", rule.steps[0], contextMask);
        ret[1] = new AILogicPropertyDescriptor("跳出阶段定义", "跳出阶段定义", rule.steps[1], contextMask);
        ret[2] = new AILogicPropertyDescriptor("阶段AI主体", "阶段AI主体", rule.steps[2], contextMask);
        // ret[3] = new ConditionPropertyDescriptor("退出", "退出", rule.steps[3]);

        return ret;
    }

    public Object getPropertyValue(Object id) {
        if ("进入阶段定义".equals(id)) {
            copyVarRuleToInfo(rule.steps[0]);
            return rule.steps[0];
        }
        else if ("跳出阶段定义".equals(id)) {
            copyVarRuleToInfo(rule.steps[1]);
            return rule.steps[1];
        }
        else if ("阶段AI主体".equals(id)) {
            copyVarRuleToInfo(rule.steps[2]);
            return rule.steps[2];
        }

        return null;
    }

    // 将rule中的变量copy成questinfo中的变量
    private void copyVarRuleToInfo(QuestInfo questInfo) {
        questInfo.variables.clear();
        for (QuestVariable var : rule.parent.variables) {
            questInfo.variables.add(var);
        }

    }

    // 将questinfo中的变量替换成rule中的变量
    private void moveVarInfoToRule(QuestInfo questInfo) {
        rule.parent.variables.clear();

        for (QuestVariable var : questInfo.variables) {
            rule.parent.variables.add(var);
        }

        for (QuestInfo qi : rule.steps) {
            qi.variables.clear();
        }

    }

    public boolean isPropertySet(Object id) {
        return false;
    }

    public void resetPropertyValue(Object id) {
    }

    public void setPropertyValue(Object id, Object value) {
        // if("进入条件".equals(id)) {
        // moveVarInfoToRule(rule.steps[0]);
        // } else if("初始化".equals(id)) {
        // moveVarInfoToRule(rule.steps[1]);
        // } else if("执行体".equals(id)) {
        // moveVarInfoToRule(rule.steps[2]);
        // } else if("退出".equals(id)) {
        // moveVarInfoToRule(rule.steps[3]);
        // }
        if ("进入阶段定义".equals(id)) {
            moveVarInfoToRule(rule.steps[0]);
        }
        else if ("跳出阶段定义".equals(id)) {
            moveVarInfoToRule(rule.steps[1]);
        }
        else if ("阶段AI主体".equals(id)) {
            moveVarInfoToRule(rule.steps[2]);
        }

        for (QuestInfo qi : rule.parent.insertPoints.steps) {
            copyVarRuleToInfo(qi);
        }
        if (status == COMBAT_STATUS) {
            for (AIRuleConfig aiConfig : rule.parent.commonStatus) {
                for (QuestInfo questInfo : aiConfig.steps) {
                    copyVarRuleToInfo(questInfo);
                }
            }
        }
        else if (status == COMMON_STATUS) {
            for (AIRuleConfig aiConfig : rule.parent.combatStatus) {
                for (QuestInfo questInfo : aiConfig.steps) {
                    copyVarRuleToInfo(questInfo);
                }
            }
        }
        
        aiEditor.setDirty(true);

        viewer.refresh();
    }
}
