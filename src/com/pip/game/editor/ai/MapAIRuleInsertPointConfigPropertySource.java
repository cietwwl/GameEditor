package com.pip.game.editor.ai;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import com.pip.game.data.AI.AIRuleConfig;
import com.pip.game.data.AI.AIRuleInsertPointConfig;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.QuestVariable;
import com.pip.game.editor.property.AILogicPropertyDescriptor;

public class MapAIRuleInsertPointConfigPropertySource implements IPropertySource {
    AIRuleInsertPointConfig rule;
    Viewer viewer;
    AIEditor aiEditor;
    int contextMask;

    public MapAIRuleInsertPointConfigPropertySource(AIEditor aiEditor, AIRuleInsertPointConfig rule, Viewer viewer, int contextMask) {
        this.rule = rule;
        this.viewer = viewer;
        this.aiEditor = aiEditor;
        this.contextMask = contextMask;
    }

    public MapAIRuleInsertPointConfigPropertySource(AIRuleInsertPointConfig rule) {
        this.rule = rule;
    }

    public Object getEditableValue() {
        return this;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] ret = new IPropertyDescriptor[7];

        ret[0] = new AILogicPropertyDescriptor("��ʼ��", "��ʼ��", rule.steps[0], contextMask);
        ret[1] = new AILogicPropertyDescriptor("�����ͼ", "�����ͼ", rule.steps[1], contextMask);
        ret[2] = new AILogicPropertyDescriptor("����", "����", rule.steps[2], contextMask);
        ret[3] = new AILogicPropertyDescriptor("���ﱻ�Ƴ�", "���ﱻ�Ƴ�", rule.steps[3], contextMask);
        ret[4] = new AILogicPropertyDescriptor("����뿪��ͼ", "����뿪��ͼ", rule.steps[4], contextMask);
        ret[5] = new AILogicPropertyDescriptor("��⵽ʹ����Ʒ", "��⵽ʹ����Ʒ", rule.steps[5], contextMask);
        ret[6] = new AILogicPropertyDescriptor("update","update",rule.steps[6], contextMask);
        return ret;
    }

    public Object getPropertyValue(Object id) {
        if ("��ʼ��".equals(id)) {
            copyVarRuleToInfo(rule.steps[0]);
            return rule.steps[0];
        }
        else if ("�����ͼ".equals(id)) {
            copyVarRuleToInfo(rule.steps[1]);
            return rule.steps[1];
        }
        else if ("����".equals(id)) {
            copyVarRuleToInfo(rule.steps[2]);
            return rule.steps[2];
        }
        else if ("���ﱻ�Ƴ�".equals(id)) {
            copyVarRuleToInfo(rule.steps[3]);
            return rule.steps[3];
        }
        else if ("����뿪��ͼ".equals(id)) {
            copyVarRuleToInfo(rule.steps[4]);
            return rule.steps[4];
        }
        else if ("��⵽ʹ����Ʒ".equals(id)) {
            copyVarRuleToInfo(rule.steps[5]);
            return rule.steps[5];
        }
        else if("update".equals(id)) {
            copyVarRuleToInfo(rule.steps[6]);
            return rule.steps[6];
        }

        return null;
    }

    // ��rule�еı���copy��questinfo�еı���
    private void copyVarRuleToInfo(QuestInfo questInfo) {
        questInfo.variables.clear();
        for (QuestVariable var : rule.parent.variables) {
            questInfo.variables.add(var);
        }
    }

    // ��questinfo�еı����滻��rule�еı���
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
        if ("��ʼ��".equals(id)) {
            moveVarInfoToRule(rule.steps[0]);
        }
        else if ("�����ͼ".equals(id)) {
            moveVarInfoToRule(rule.steps[1]);
        }
        else if ("����".equals(id)) {
            moveVarInfoToRule(rule.steps[2]);
        }
        else if ("���ﱻ�Ƴ�".equals(id)) {
            moveVarInfoToRule(rule.steps[3]);
        }
        else if ("����뿪��ͼ".equals(id)) {
            moveVarInfoToRule(rule.steps[4]);
        }
        else if ("��⵽ʹ����Ʒ".equals(id)) {
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
