package com.pip.game.editor.ai;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import com.pip.game.data.AI.AIRuleConfig;
import com.pip.game.data.AI.AIRuleInsertPointConfig;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.QuestVariable;
import com.pip.game.editor.property.AILogicPropertyDescriptor;

public class AIRuleInsertPointConfigPropertySource implements IPropertySource {
    AIRuleInsertPointConfig rule;
    Viewer viewer;
    AIEditor aiEditor;
    int contextMask;

    public AIRuleInsertPointConfigPropertySource(AIEditor aiEditor, AIRuleInsertPointConfig rule, Viewer viewer, int contextMask) {
        this.rule = rule;
        this.viewer = viewer;
        this.aiEditor = aiEditor;
        this.contextMask = contextMask;
    }

    public AIRuleInsertPointConfigPropertySource(AIRuleInsertPointConfig rule) {
        this.rule = rule;
    }

    public Object getEditableValue() {
        return this;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] ret = new IPropertyDescriptor[7];

        ret[0] = new AILogicPropertyDescriptor("初始化", "初始化", rule.steps[0], contextMask);
        ret[1] = new AILogicPropertyDescriptor("加入地图", "加入地图", rule.steps[1], contextMask);
        ret[2] = new AILogicPropertyDescriptor("死亡", "死亡", rule.steps[2], contextMask);
        ret[3] = new AILogicPropertyDescriptor("复活", "复活", rule.steps[3], contextMask);
        ret[4] = new AILogicPropertyDescriptor("从地图中移除", "从地图中移除", rule.steps[4], contextMask);
        ret[5] = new AILogicPropertyDescriptor("从世界中移除", "从世界中移除", rule.steps[5], contextMask);
        ret[6] = new AILogicPropertyDescriptor("update","update",rule.steps[6], contextMask);
        return ret;
    }

    public Object getPropertyValue(Object id) {
        if ("初始化".equals(id)) {
            copyVarRuleToInfo(rule.steps[0]);
            return rule.steps[0];
        }
        else if ("加入地图".equals(id)) {
            copyVarRuleToInfo(rule.steps[1]);
            return rule.steps[1];
        }
        else if ("死亡".equals(id)) {
            copyVarRuleToInfo(rule.steps[2]);
            return rule.steps[2];
        }
        else if ("复活".equals(id)) {
            copyVarRuleToInfo(rule.steps[3]);
            return rule.steps[3];
        }
        else if ("从地图中移除".equals(id)) {
            copyVarRuleToInfo(rule.steps[4]);
            return rule.steps[4];
        }
        else if ("从世界中移除".equals(id)) {
            copyVarRuleToInfo(rule.steps[5]);
            return rule.steps[5];
        }
        else if("update".equals(id)) {
            copyVarRuleToInfo(rule.steps[6]);
            return rule.steps[6];
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
        if ("初始化".equals(id)) {
            moveVarInfoToRule(rule.steps[0]);
        }
        else if ("加入地图".equals(id)) {
            moveVarInfoToRule(rule.steps[1]);
        }
        else if ("死亡".equals(id)) {
            moveVarInfoToRule(rule.steps[2]);
        }
        else if ("复活".equals(id)) {
            moveVarInfoToRule(rule.steps[3]);
        }
        else if ("从地图中移除".equals(id)) {
            moveVarInfoToRule(rule.steps[4]);
        }
        else if ("从世界中移除".equals(id)) {
            moveVarInfoToRule(rule.steps[5]);
        }
        else if ("update".equals(id))
        {
            moveVarInfoToRule(rule.steps[6]);
        }
        for(AIRuleConfig aiConfig : rule.parent.combatStatus) {
            for(QuestInfo questInfo : aiConfig.steps) {
                copyVarRuleToInfo(questInfo);
            }
        }
        for(AIRuleConfig aiConfig : rule.parent.commonStatus) {
            for(QuestInfo questInfo : aiConfig.steps) {
                copyVarRuleToInfo(questInfo);
            }
        }
        aiEditor.setDirty(true);
        viewer.refresh();
    }
}
