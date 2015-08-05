package com.pip.game.data.AI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.i18n.I18NContext;
import com.pip.game.data.quest.Quest;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.QuestTrigger;
import com.pip.game.data.quest.QuestVariable;
import com.pip.util.Utils;

public class AIRule {
    public ProjectData owner;
    
    private static final int STATE_START_STAGE = 0;
    private static final int STATE_GET_STAGE = 1;
    private static final int STATE_START_VAR = 2;
    private static final int STATE_GET_VAR = 3;
    private static final int STATE_START_TRIGGER = 4;
    private static final int STATE_GET_TRIGGER = 5;
    // **************
    private static final int STATE_START_INSERTPOINT = 6;
    private static final int STATE_START_INSERTPOINT_TRIGGER = 7;
    private static final int STATE_GET_INSERTPOINT_TRIGGER = 8;
    private static final int STATE_START_COMBAT = 9;
    private static final int STATE_START_COMBAT_STAGE = 10;
    private static final int STATE_GET_COMBAT_STAGE = 11;
    private static final int STATE_START_COMBAT_TRIGGER = 12;
    private static final int STATE_GET_COMBAT_TRIGGER = 13;
    private static final int STATE_START_COMMON = 14;
    private static final int STATE_START_COMMON_STAGE = 15;
    private static final int STATE_GET_COMMON_STAGE = 16;
    private static final int STATE_START_COMMON_TRIGGER = 17;
    private static final int STATE_GET_COMMON_TRIGGER = 18;
    // **************

    /**
     * AI的变量。
     */
    public List<QuestVariable> variables = new ArrayList<QuestVariable>();

    public List<AIRuleConfig> aiRules = new ArrayList<AIRuleConfig>();
    
    // *****************
    public AIRuleInsertPointConfig insertPoints;

    public List<AIRuleConfig> combatStatus = new ArrayList<AIRuleConfig>();

    public List<AIRuleConfig> commonStatus = new ArrayList<AIRuleConfig>();

    public AIRule(ProjectData owner) {
        this.owner = owner;
        insertPoints = new AIRuleInsertPointConfig(this);
    }
    
    // *****************

    // public void update(Object obj) {
    // AIRule oo = (AIRule) obj;
    //
    // this.aiRules = new ArrayList<AIRuleConfig>();
    // this.variables = new ArrayList<QuestVariable>();
    // for (AIRuleConfig rc : oo.aiRules) {
    // addRuleConfig(rc);
    // }
    // for (QuestVariable var : oo.variables) {
    // variables.add(var);
    // }
    // }

    public void update(Object obj) {
        AIRule oo = (AIRule) obj;
        this.combatStatus = new ArrayList<AIRuleConfig>();
        this.commonStatus = new ArrayList<AIRuleConfig>();
        this.variables = new ArrayList<QuestVariable>();
        this.insertPoints = null;
        for (AIRuleConfig rc : oo.combatStatus) {
            addCombatRuleConfig(rc);
        }
        for (AIRuleConfig rc : oo.commonStatus) {
            addCommonRuleConfig(rc);
        }
        for (QuestVariable var : oo.variables) {
            variables.add(var);
        }
        insertPoints = oo.insertPoints;
        insertPoints.parent = this;
    }

    public void addRuleConfig(AIRuleConfig aiRuleConfig) {
        aiRules.add(aiRuleConfig);
        aiRuleConfig.parent = this;
        // **********
        // insertPoints.parent = this;
        // ***********

    }

    // ****************
    public void addCombatRuleConfig(AIRuleConfig combatAIRuleConfig) {
        combatStatus.add(combatAIRuleConfig);
        combatAIRuleConfig.parent = this;
    }

    public void addCommonRuleConfig(AIRuleConfig commonAIRuleConfig) {
        commonStatus.add(commonAIRuleConfig);
        commonAIRuleConfig.parent = this;
    }

    // ****************

    public void loadAI(File aiFile) {
        combatStatus.clear();
        commonStatus.clear();
        // insertPoints = null;
        aiRules.clear();
        variables.clear();

        if (aiFile.exists()) {
            try {
                String content = Utils.loadText(aiFile);

                StringBuffer buf = new StringBuffer();
                content = content.replaceAll("\\r\\n", "\n");
                char[] arr = content.toCharArray();
                String stageName = null;
                QuestInfo questInfo = null;
                int counter = 0;
                AIRuleConfig aiRuleConfig = null;
                int counter2 = 0;

                int state = STATE_START_VAR;
                for (int i = 0; i < arr.length; i++) {
                    char ch = arr[i];
                    switch (state) {
                        case STATE_START_VAR:
                            if (buf.toString().endsWith("var:")) {
                                state = STATE_GET_VAR;
                                buf.setLength(0);
                                break;
                            }
                            else if (buf.toString().endsWith("insertPoint:")) {
                                state = STATE_START_INSERTPOINT;
                                buf.setLength(0);
                                break;
                            }
                            buf.append(ch);
                            break;
                        case STATE_GET_VAR:
                            if (ch == '\n') {
                                QuestVariable var = new QuestVariable();
                                var.name = buf.toString().trim();
                                variables.add(var);
                                counter2 = 0;
                                state = STATE_START_VAR;
                                buf.setLength(0);
                                break;
                            }
                            buf.append(ch);
                            break;
                        case STATE_START_INSERTPOINT:
                            if (ch == '\n') {
                                state = STATE_START_INSERTPOINT_TRIGGER;
                                counter2 = 0;
                                buf.setLength(0);
                                break;
                            }
                            buf.append(ch);
                            break;
                        case STATE_START_INSERTPOINT_TRIGGER:
                            if (ch == '[') {
                                state = STATE_GET_INSERTPOINT_TRIGGER;
                                buf.setLength(0);
                                counter--;
                                break;
                            }
                            buf.append(ch);
                            break;
                        case STATE_GET_INSERTPOINT_TRIGGER:
                            if (ch == ']') {
                                counter++;
                                if (counter == 0) {
                                    questInfo = new QuestInfo(new Quest(owner));
                                    questInfo.ownerName = "AI";
                                    questInfo.loadFromText(buf.toString());

                                    insertPoints.steps[counter2++] = questInfo;

                                    if (counter2 > insertPoints.steps.length - 1) {
                                        state = STATE_START_COMBAT;
                                    }
                                    else {
                                        state = STATE_START_INSERTPOINT_TRIGGER;
                                    }
                                    buf.setLength(0);
                                    break;
                                }
                            }
                            else if (ch == '[') {
                                counter--;
                            }
                            buf.append(ch);
                            break;
                        case STATE_START_COMBAT:
                            if (buf.toString().endsWith("combat:")) {
                                state = STATE_START_COMBAT_STAGE;
                                buf.setLength(0);
                                break;
                            }
                            buf.append(ch);
                            break;
                        case STATE_START_COMBAT_STAGE:
                            if (buf.toString().endsWith("stage:")) {
                                state = STATE_GET_COMBAT_STAGE;
                                buf.setLength(0);
                                break;
                            }
                            else if (buf.toString().endsWith("common:")) {
                                state = STATE_START_COMMON;
                                // buf.setLength(0);
                                break;
                            }
                            buf.append(ch);
                            break;
                        case STATE_GET_COMBAT_STAGE:
                            if (ch == '\n') {
                                stageName = buf.toString().trim();
                                aiRuleConfig = new AIRuleConfig(this, stageName);
                                addCombatRuleConfig(aiRuleConfig);
                                counter2 = 0;
                                counter = 0;
                                state = STATE_START_COMBAT_TRIGGER;
                                buf.setLength(0);
                                break;
                            }
                            buf.append(ch);
                            break;
                        case STATE_START_COMBAT_TRIGGER:
                            if (ch == '[') {
                                state = STATE_GET_COMBAT_TRIGGER;
                                buf.setLength(0);
                                counter--;
                                break;
                            }
                            buf.append(ch);
                            break;
                        case STATE_GET_COMBAT_TRIGGER:
                            if (ch == ']') {
                                counter++;
                                if (counter == 0) {
                                    questInfo = new QuestInfo(new Quest(owner));
                                    questInfo.ownerName = "AI";
                                    questInfo.loadFromText(buf.toString());

                                    aiRuleConfig.steps[counter2++] = questInfo;

                                    if (counter2 > aiRuleConfig.steps.length - 1) {
                                        state = STATE_START_COMBAT_STAGE;
                                    }
                                    else {
                                        state = STATE_START_COMBAT_TRIGGER;
                                    }
                                    buf.setLength(0);
                                    break;
                                }
                            }
                            else if (ch == '[') {
                                counter--;
                            }
                            buf.append(ch);
                            break;
                        case STATE_START_COMMON:
                            if (buf.toString().endsWith("common:")) {
                                state = STATE_START_COMMON_STAGE;
                                buf.setLength(0);
                                break;
                            }
                            buf.append(ch);
                            break;
                        case STATE_START_COMMON_STAGE:
                            if (buf.toString().endsWith("stage:")) {
                                state = STATE_GET_COMMON_STAGE;
                                buf.setLength(0);
                                break;
                            }
                            buf.append(ch);
                            break;
                        case STATE_GET_COMMON_STAGE:
                            if (ch == '\n') {
                                stageName = buf.toString().trim();
                                aiRuleConfig = new AIRuleConfig(this, stageName);
                                addCommonRuleConfig(aiRuleConfig);
                                counter2 = 0;
                                counter = 0;
                                state = STATE_START_COMMON_TRIGGER;
                                buf.setLength(0);
                                break;
                            }
                            buf.append(ch);
                            break;
                        case STATE_START_COMMON_TRIGGER:
                            if (ch == '[') {
                                state = STATE_GET_COMMON_TRIGGER;
                                buf.setLength(0);
                                counter--;
                                break;
                            }
                            buf.append(ch);
                            break;
                        case STATE_GET_COMMON_TRIGGER:
                            if (ch == ']') {
                                counter++;
                                if (counter == 0) {
                                    questInfo = new QuestInfo(new Quest(ProjectData.getActiveProject()));
                                    questInfo.ownerName = "AI";
                                    questInfo.loadFromText(buf.toString());

                                    aiRuleConfig.steps[counter2++] = questInfo;

                                    if (counter2 > aiRuleConfig.steps.length - 1) {
                                        state = STATE_START_COMMON_STAGE;
                                    }
                                    else {
                                        state = STATE_START_COMMON_TRIGGER;
                                    }
                                    buf.setLength(0);
                                    break;
                                }
                            }
                            else if (ch == '[') {
                                counter--;
                            }
                            buf.append(ch);
                            break;
                    }
                }
                // ---------------------------------------------------
                // int state = STATE_START_VAR;
                // for (int i = 0; i < arr.length; i++) {
                // char ch = arr[i];
                // switch (state) {
                // case STATE_START_STAGE: // 开始获取一个stage
                // if (buf.toString().endsWith("stage:")) {
                // state = STATE_GET_STAGE;
                // buf.setLength(0);
                // break;
                // }
                //
                // buf.append(ch);
                // break;
                // case STATE_GET_STAGE:// 获取一个stage
                // if (ch == '\n') {
                // stageName = buf.toString().trim();
                // aiRuleConfig = new AIRuleConfig(stageName);
                // addRuleConfig(aiRuleConfig);
                // counter2 = 0;
                // state = STATE_START_TRIGGER;
                // buf.setLength(0);
                // }
                // else {
                // buf.append(ch);
                // }
                // break;
                // case STATE_START_VAR: // 开始获取变量 或者stage
                // if (buf.toString().endsWith("var:")) {
                // state = STATE_GET_VAR;
                // buf.setLength(0);
                // break;
                // }
                // else if (buf.toString().endsWith("stage:")) {
                // state = STATE_START_STAGE;
                // break;
                // }
                //
                // buf.append(ch);
                // break;
                // case STATE_GET_VAR: // 获取变量
                // if (ch == '\n') {
                // QuestVariable var = new QuestVariable();
                // var.name = buf.toString().trim();
                // variables.add(var);
                // counter2 = 0;
                // state = STATE_START_VAR;
                // buf.setLength(0);
                // }
                // else {
                // buf.append(ch);
                // }
                // break;
                // case STATE_START_TRIGGER: // 开始获取一组触发器
                // if (ch == '[') {
                // state = STATE_GET_TRIGGER;
                // counter = -1;
                // buf.setLength(0);
                // }
                // else {
                // buf.append(ch);
                // }
                // break;
                // case STATE_GET_TRIGGER: // 获取一组触发器
                // if (ch == ']') {
                // counter++;
                // if (counter == 0) {
                // questInfo = new QuestInfo(new
                // Quest(ProjectData.getActiveProject()));
                // questInfo.loadFromText(buf.toString());
                //
                // aiRuleConfig.steps[counter2++] = questInfo;
                //
                // if (counter2 > aiRuleConfig.steps.length - 1) {
                // state = STATE_START_STAGE;
                // }
                // else {
                // state = STATE_START_TRIGGER;
                // }
                // buf.setLength(0);
                //
                // break;
                // }
                // }
                // else if (ch == '[') {
                // counter--;
                // }
                // buf.append(ch);
                // break;
                // default:
                // buf.append(ch);
                // break;
                //
                // }
                // }

            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    public void save(File file) {
        try {
            String ai = getAIRuleDesc();
            if (ai != null && "".equals(ai) == false) {
                //假如该文件不存在，则创建该文件
                if(!file.exists()){
                    file.createNewFile();
                }
                Utils.saveText(getAIRuleDesc(), file);
            }

        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getAIRuleDesc() {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < variables.size(); i++) {
            sb.append("var: ");
            sb.append(variables.get(i).name);
            sb.append("\r\n");
        }
        // ***************
        sb.append("insertPoint: ");
        sb.append("\r\n");
        sb.append(insertPoints.toString());
        sb.append("\r\n");

        sb.append("combat: ");
        sb.append("\r\n");

        for (int i = 0; i < combatStatus.size(); i++) {
            AIRuleConfig rc = combatStatus.get(i);
            sb.append("stage: ");
            sb.append(rc.getName());
            sb.append("\r\n");
            sb.append(rc.toString());
        }
        sb.append("common: ");
        sb.append("\r\n");
        for (int i = 0; i < commonStatus.size(); i++) {
            AIRuleConfig rc = commonStatus.get(i);
            sb.append("stage: ");
            sb.append(rc.getName());
            sb.append("\r\n");
            sb.append(rc.toString());
        }
        return sb.toString();
        // **************
        // for (int i = 0; i < aiRules.size(); i++) {
        // AIRuleConfig rc = aiRules.get(i);
        //
        // sb.append("stage: ");
        // sb.append(rc.getName());
        // sb.append("\r\n");
        //
        // sb.append(rc.toString());
        //
        // }
        //
        // return sb.toString();
    }

    public static final int IP_INIT = 1000;
    public static final int IP_JOIN_MAP = 1001;
    public static final int IP_DEATH = 1002;
    public static final int IP_REVIVE = 1003;
    public static final int IP_REMOVE_FROM_MAP = 1004;
    public static final int IP_REMOVE_FROM_WORLD = 1005;
    public static final int IP_UPDATE = 1006;

    /**
     * 取指定插入点中的Triggers
     * 
     * @param trigger
     *            上边的常量 1000-1005
     * @return List<QuestTrigger>
     */
    public List<QuestTrigger> getTriggersFromInsertPoint(int ip) {
        return insertPoints.steps[ip - 1000].triggers;
    }

    public static final int COMBAT = 0;
    public static final int COMMON = 1;

    public static final int STATE_STEP_JOIN = 2000;
    public static final int STATE_STEP_JUMPOUT = 2001;
    public static final int STATE_STEP_MAIN = 2002;

    /**
     * 通过阶段名字取得指定
     * 
     * @param state
     *            COMBAT或者COMMON
     * @param stepName
     *            阶段名
     * @param trigger
     *            上边的常量 2000-2002
     * @return List<QuestTrigger>
     */
    public List<QuestTrigger> getTriggersByStepName(int state, String stepName, int unit) {
        List<AIRuleConfig> list = null;
        AIRuleConfig aiRule = null;
        if (state == COMBAT) {
            list = combatStatus;
        }
        else if (state == COMMON) {
            list = commonStatus;
        }
        else {
            return null;
        }
        for (AIRuleConfig ai : list) {
            if (stepName.equals(ai.getName())) {
                aiRule = ai;
                break;
            }
        }
        if (aiRule == null) {
            return null;
        }
        return aiRule.steps[unit - 2000].triggers;
    }

    /**
     * 对这个对象的属性进行国际化处理，如果有需要国际化的字符串，则提取出来到context中查找翻译结果。
     * @param context
     * @return 如果有某个属性被替换，返回true，否则返回false。
     */
    public boolean i18n(I18NContext context) {
        boolean changed = false;
        for (QuestVariable var : variables) {
            String tmp = context.input(var.name, "Quest Variable");
            if (tmp != null) {
                var.name = tmp;
                changed = true;
            }
        }
        for (AIRuleConfig rule : aiRules) {
            if (rule.i18n(context)) {
                changed = true;
            }
        }
        if (insertPoints != null && insertPoints.i18n(context)) {
            changed = true;
        }
        for (AIRuleConfig rule : combatStatus) {
            if (rule.i18n(context)) {
                changed = true;
            }
        }
        for (AIRuleConfig rule : commonStatus) {
            if (rule.i18n(context)) {
                changed = true;
            }
        }
        return changed;
    }
}
