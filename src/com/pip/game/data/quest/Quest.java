package com.pip.game.data.quest;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.GameArea;
import com.pip.game.data.ProjectData;
import com.pip.game.data.i18n.I18NContext;
import com.pip.game.data.i18n.I18NUtils;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.map.GameMapObject;

import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.ExpressionList;
import com.pip.game.data.quest.pqe.FunctionCall;

import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.data.quest.pqe.PQEUtils.SystemVar;
import com.pip.game.editor.util.Settings;

import com.pip.util.Utils;

/**
 * 一个游戏任务。这个类只包含任务的基本属性，而任务的详细内容包含在对应的任务文件中。
 */
public class Quest extends DataObject {
    /**
     * 所属项目。
     */
    public ProjectData owner;
    /**
     * 对应的任务文件。
     */
    public java.io.File source;
    /**
     * 任务类型：0 - 普通，1 - 场景。
     */
    public int type;
    /**
     * 场景任务对应的地区ID.
     */
    public int areaID;
    /**
     * 重复类型：0 - 不可重复、1 - 每月可完成1次、2 - 每周可完成1次、3 - 每天可完成1次、4 - 无限重复, 5按时间段重复
     */
    public int repeatType;
    /**
     * 授予任务的NPC.
     */
    public String startNpc = "";
    /**
     * 交还任务的NPC。-1表示没有。
     */
    public String finishNpc = "";

    /**
     * 任务级别。
     */
    public int level;
    /**
     * 接受任务的前置任务条件
     */
    public String precondition = "";

    /**
     * 接受任务的拓展条件。
     */
    public String condition = "";
    /**
     * 接受任务时需要几个空的背包空间。
     */
    public int requireFreeBag;
    /**
     * 任务完成的条件，这个条件是由所有任务目标的条件联合组成的。
     */
    public String finishCondition = "";
    /**
     * 接受任务时的描述。
     */
    public String preDescription = "";
    /**
     * 完成任务时的描述。
     */
    public String postDescription = "";
    /**
     * 未完成任务时的描述。
     */
    public String unfinishDescription = "";

    /**
     * 起始时间
     */
    public String repeatBeginTime = "00:00:00";

    /**
     * 终止时间。当设置时间段重复才会有效
     */
    public String repeatEndTime = "00:00:00";
    /**
     * 任务目标。
     */
    public List<QuestTarget> targets = new ArrayList<QuestTarget>();
    /**
     * 任务奖励分支。
     */
    public List<QuestRewardSet> rewards = new ArrayList<QuestRewardSet>();
    /**
     * 任务目标全部达成时是否需要在客户端显示通知消息。
     */
    public boolean notifyFinish = true;
    /**
     * 是否在接受时自动共享给其他玩家。
     */
    public boolean autoShare = false;
    /**
     * 是否有效。
     */
    public boolean valid = true;
    /**
     * 是否随机任务，如果是，并且起始npc和结束npc相同时，则随机给客户端分配一个
     */
    public boolean isRandomQuest;

    /**
     * 实现Class名
     */
    public String implClass;

    public Quest(ProjectData owner) {
        this.owner = owner;
    }

    public int getID() {
        return id;
    }

    //仅用于任务导出使用
    public String getStartNpcName() {
        String npcName = "";
        if (startNpc == null || startNpc.equals("")) {
            npcName = "未设置";
        }
        else {
            /*
             * GameMapObject startNpc = GameMapNPC.findByID(owner, startNPC); if
             * (startNpc instanceof GameMapNPC) { npcName = startNpc.toString();
             * } else { npcName = "错误对象"; }
             */
            String[] npcString = Utils.splitString(startNpc, ';');
            for (int i = 0; i < npcString.length; i++) {
                int npcId = Utils.parseHex(npcString[i]);
                GameMapObject startNpc = GameMapNPC.findByID(owner, npcId);
                if (startNpc instanceof GameMapNPC) {
                    npcName += startNpc.toString() + ";";
                } else if (npcName == null) {
                    npcName = "未设置";
                }
                else {
                    npcName = "错误对象";
                }
            }
        }
        return npcName;
    }
    
    //仅用于任务导出使用
    public String getFinishNpcName() {
        String npcName = "";
        if (finishNpc == null || finishNpc.equals("")) {
            npcName = "未设置";
        }
        else {
            /*
             * GameMapObject startNpc = GameMapNPC.findByID(owner, startNPC); if
             * (startNpc instanceof GameMapNPC) { npcName = startNpc.toString();
             * } else { npcName = "错误对象"; }
             */
            String[] npcString = Utils.splitString(finishNpc, ';');
            for (int i = 0; i < npcString.length; i++) {
                int npcId = Utils.parseHex(npcString[i]);
                GameMapObject startNpc = GameMapNPC.findByID(owner, npcId);
                if (startNpc instanceof GameMapNPC) {
                    npcName += startNpc.toString() + ";";
                } else if (npcName == null) {
                    npcName = "未设置";
                }
                else {
                    npcName = "错误对象";
                }
            }
        }
        return npcName;
    }

    public int[] getStartNpc() {
        int[] npc = null;
        if (startNpc == null || startNpc.equals("")) {
            npc = new int[] { -1 };
        }
        else {

            String[] npcString = Utils.splitString(startNpc, ';');
            npc = new int[npcString.length];
            for (int i = 0; i < npcString.length; i++) {
                int npcId = Utils.parseHex(npcString[i]);
                npc[i] = npcId;
            }
        }
        return npc;
    }

    public int[] getEndNpc() {
        int[] npc = null;
        if (finishNpc == null || finishNpc.equals("")) {
            npc = new int[] { -1 };
        }
        else {

            String[] npcString = Utils.splitString(finishNpc, ';');
            npc = new int[npcString.length];
            for (int i = 0; i < npcString.length; i++) {
                int npcId = Utils.parseHex(npcString[i]);
                npc[i] = npcId;
            }
        }
        return npc;
    }

    public String getTargetsCondition() {
        if (targets == null || targets.size() == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < targets.size(); i++) {
            sb.append((targets.get(i)).condition);
            if (i != targets.size() - 1)
                sb.append("\n");
        }
        // sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    
    public String getRewardsMoney() {
        if (rewards == null || rewards.size() == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < rewards.size(); i++) {
            sb.append((rewards.get(i)).getRewardMoney());
            if (i != rewards.size() - 1)
                sb.append("\n");
        }
        // sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    
    public String getRewardsExp() {
        if (rewards == null || rewards.size() == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < rewards.size(); i++) {
            sb.append((rewards.get(i)).getRewardExp());
            if (i != rewards.size() - 1)
                sb.append("\n");
        }
        // sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    
    public String getRewardsImoney() {
        if (rewards == null || rewards.size() == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < rewards.size(); i++) {
            sb.append((rewards.get(i)).getRewardImoney());
            if (i != rewards.size() - 1)
                sb.append("\n");
        }
        // sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public String getRewardsItem() {
        if (rewards == null || rewards.size() == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < rewards.size(); i++) {
            sb.append((rewards.get(i)).getRewardItem());
            if (i != rewards.size() - 1)
                sb.append("\n");
        }
        // sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public String getComments() {
        if (type == 1) {
            // 场景任务
            GameArea ga = (GameArea) owner.findObject(GameArea.class, areaID);
            if (ga == null) {
                return "场景任务(未知场景)";
            }
            else {
                return "场景任务(" + ga.title + ")";
            }
        }
        else {
            String ret = "";
            // if (startNPC == -1) {
            // ret = "起始：未设置";
            // } else {
            // GameMapObject startNpc = GameMapNPC.findByID(owner, startNPC);
            // if (startNpc instanceof GameMapNPC) {
            // ret = "起始：" + startNpc.toString();
            // } else {
            // ret = "起始：错误对象";
            // }
            // }
            if (startNpc == null || startNpc.equals("")) {
                ret = "起始：未设置";
            }
            else {
                ret = "起始：";
                String[] npcString = Utils.splitString(startNpc, ';');
                for (int i = 0; i < npcString.length; i++) {
                    int npcId = Utils.parseHex(npcString[i]);
                    GameMapObject startNpc = GameMapNPC.findByID(owner, npcId);

                    if (startNpc != null && startNpc instanceof GameMapNPC) {
                        if (i == 0) {
                            ret = startNpc.toString();
                        }
                        else {
                            ret = "," + startNpc.toString();
                        }
                    }
                    else {
                        ret = "错误对象";
                    }
                }
            }
            ret += "，";
            // if (finishNPC == -1) {
            // ret += "结束：未设置";
            // } else {
            // GameMapObject finishNpc = GameMapNPC.findByID(owner, finishNPC);
            // if (finishNpc instanceof GameMapNPC) {
            // ret += "结束：" + finishNpc.toString();
            // } else {
            // ret += "结束：错误对象";
            // }
            // }
            if (finishNpc == null || finishNpc.equals("")) {
                ret = "结束：未设置";
            }
            else {
                ret = "结束：";
                String[] npcString = Utils.splitString(finishNpc, ';');
                for (int i = 0; i < npcString.length; i++) {

                    int npcId = Utils.parseHex(npcString[i]);
                    GameMapObject endNpc = GameMapNPC.findByID(owner, npcId);
                    if (endNpc != null && endNpc instanceof GameMapNPC) {
                        if (i == 0) {
                            ret = endNpc.toString();
                        }
                        else {
                            ret = "," + endNpc.toString();
                        }
                    }
                    else {
                        ret = "错误对象";
                    }
                }
            }
            return ret;
        }
    }

    public String toString() {
        return id + ": " + title;
    }

    public boolean equals(Object o) {
        return this == o;
    }

    public void update(DataObject obj) {
        Quest oo = (Quest) obj;
        id = oo.id;
        source = oo.source;
        title = oo.title;
        description = oo.description;
        setCategoryName(oo.getCategoryName());

        type = oo.type;
        areaID = oo.areaID;
        startNpc = oo.startNpc;
        repeatType = oo.repeatType;
        repeatBeginTime = oo.repeatBeginTime;
        repeatEndTime = oo.repeatEndTime;
        finishNpc = oo.finishNpc;
        level = oo.level;
        precondition = oo.precondition;
        condition = oo.condition;
        requireFreeBag = oo.requireFreeBag;
        finishCondition = oo.finishCondition;
        preDescription = oo.preDescription;
        postDescription = oo.postDescription;
        unfinishDescription = oo.unfinishDescription;
        notifyFinish = oo.notifyFinish;
        autoShare = oo.autoShare;
        valid = oo.valid;
        targets.clear();
        for (QuestTarget target : oo.targets) {
            QuestTarget newTarget = target.duplicate();
            newTarget.owner = this;
            targets.add(newTarget);
        }
        rewards.clear();
        for (QuestRewardSet reward : oo.rewards) {
            QuestRewardSet newReward = reward.duplicate();
            newReward.owner = this;
            rewards.add(newReward);
        }
    }

    public DataObject duplicate() {
        Quest ret = new Quest(owner);
        ret.update(this);
        return ret;
    }

    @Override
    public boolean changed(DataObject obj) {
        // 因为没有缓存QuestInfo对象，导致QuestInfo可能无法比较，所以只能全部更新。
        return true;
    }

    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        source = new java.io.File(owner.baseDir, "Quests/" + elem.getAttributeValue("source"));
        title = elem.getAttributeValue("title");
        description = elem.getChild("desc").getText();
        setCategoryName(elem.getAttributeValue("category"));
        if (getWholeCategoryName() == null) {
            setCategoryName("");
        }

        type = Integer.parseInt(elem.getAttributeValue("type"));
        areaID = Integer.parseInt(elem.getAttributeValue("areaid"));
        // startNpc = Utils.parseHex(elem.getAttributeValue("startnpc"));
        startNpc = elem.getAttributeValue("startnpc");
        String repeatTypeStr = elem.getAttributeValue("repeattype");
        if (repeatTypeStr == null || repeatTypeStr.equals("")) {
            repeatTypeStr = "0";
        }
        repeatType = Integer.parseInt(repeatTypeStr);
        // finishNpc = Utils.parseHex(elem.getAttributeValue("finishnpc"));
        finishNpc = elem.getAttributeValue("finishnpc");
        level = Integer.parseInt(elem.getAttributeValue("level"));
        try {
            precondition = elem.getAttributeValue("precondition");
        }
        catch (Exception e) {
        }
        finally {
            if (precondition == null) {
                precondition = "";
            }
        }

        try {
            repeatBeginTime = elem.getAttributeValue("repeatbegintime");
            repeatEndTime = elem.getAttributeValue("repeatendtime");
        }
        catch (Exception e) {
        }
        finally {
            if (repeatBeginTime == null) {
                repeatBeginTime = "00:00:00";
            }
            if (repeatEndTime == null) {
                repeatEndTime = "00:00:00";
            }

        }
        condition = elem.getAttributeValue("condition");
        try {
            requireFreeBag = Integer.parseInt(elem.getAttributeValue("requirefreebag"));
        }
        catch (Exception e) {
        }
        finishCondition = elem.getAttributeValue("finishcondition");
        preDescription = elem.getChild("predesc").getText();
        postDescription = elem.getChild("postdesc").getText();
        try {
            unfinishDescription = elem.getChild("unfindesc").getText();
        }
        catch (Exception e) {
            unfinishDescription = description;
        }
        notifyFinish = !("0".equals(elem.getAttributeValue("notifyfinish")));
        autoShare = "1".equals(elem.getAttributeValue("autoshare"));
        valid = !("0".equals(elem.getAttributeValue("valid")));

        List targetElems = elem.getChildren("target");
        for (int i = 0; i < targetElems.size(); i++) {
            QuestTarget target = new QuestTarget(this);
            target.load((Element) targetElems.get(i));
            targets.add(target);
        }

        List rewardElems = elem.getChildren("rewardset");
        for (int i = 0; i < rewardElems.size(); i++) {
            QuestRewardSet target = new QuestRewardSet(this);
            target.load((Element) rewardElems.get(i));
            rewards.add(target);
        }

        implClass = elem.getAttributeValue("class");
        if(implClass == null) {
            implClass = Settings.questPackage + "." + getClassName(Settings.questClassPrefix);
        }
    }

    public Element save() {
        Element ret = new Element("quest");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("source", source.getName());
        ret.addAttribute("title", title);
        Element descElem = new Element("desc");
        descElem.setText(description);
        ret.addContent(descElem);
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }

        ret.addAttribute("type", String.valueOf(type));
        ret.addAttribute("areaid", String.valueOf(areaID));
        // ret.addAttribute("startnpc", "0x" + Integer.toHexString(startNPC));
        ret.addAttribute("startnpc", startNpc);
        ret.addAttribute("repeattype", String.valueOf(repeatType));
        ret.addAttribute("repeatbegintime", repeatBeginTime);
        ret.addAttribute("repeatendtime", repeatEndTime);
        // ret.addAttribute("finishnpc", "0x" + Integer.toHexString(finishNPC));
        ret.addAttribute("finishnpc", finishNpc);
        ret.addAttribute("level", String.valueOf(level));
        ret.addAttribute("precondition", precondition);
        ret.addAttribute("condition", condition);
        ret.addAttribute("requirefreebag", String.valueOf(requireFreeBag));
        ret.addAttribute("finishcondition", finishCondition);
        descElem = new Element("predesc");
        descElem.setText(preDescription);
        ret.addContent(descElem);
        descElem = new Element("postdesc");
        descElem.setText(postDescription);
        ret.addContent(descElem);
        descElem = new Element("unfindesc");
        descElem.setText(unfinishDescription);
        ret.addContent(descElem);
        ret.addAttribute("notifyfinish", notifyFinish ? "1" : "0");
        ret.addAttribute("autoshare", autoShare ? "1" : "0");
        ret.addAttribute("valid", valid ? "1" : "0");

        for (QuestTarget target : targets) {
            ret.addContent(target.save());
        }

        for (QuestRewardSet reward : rewards) {
            ret.addContent(reward.save());
        }

        if (implClass != null) {
            ret.addAttribute("class", implClass);
        }

        return ret;
    }

    public boolean depends(DataObject obj) {
        return false;
    }

    /**
     * 查找一个任务的名字。
     * 
     * @param project
     * @param questID
     * @return
     */
    public static String toString(ProjectData project, int questID) {
        Quest q = (Quest) project.findObject(Quest.class, questID);
        if (q == null) {
            return "未知任务";
        }
        else {
            return q.toString();
        }
    }

    /**
     * 校验混合格式字符串，把其中的NPC引用和场景位置引用更新成最新的版本。
     * 
     * @param text
     * @return
     */
    public static String validateMixedText(ProjectData proj, String text) throws Exception {
        StringBuilder sb = new StringBuilder();
        int start = 0;
        while (true) {
            int pos1 = text.indexOf("<l>", start);
            int pos2 = text.indexOf("<n>", start);
            if (pos1 == -1 && pos2 == -1) {
                sb.append(text.substring(start));
                break;
            }
            else if (pos1 != -1 && (pos2 == -1 || pos1 < pos2)) {
                pos2 = text.indexOf("</l>", pos1);
                if (pos2 == -1) {
                    sb.append(text.substring(start));
                    break;
                }
                sb.append(text.substring(start, pos1));
                sb.append("<l>");
                sb.append(validateLocationText(proj, text.substring(pos1 + 3, pos2)));
                sb.append("</l>");
                start = pos2 + 4;
            }
            else {
                pos1 = pos2;
                pos2 = text.indexOf("</n>", pos1);
                if (pos2 == -1) {
                    sb.append(text.substring(start));
                    break;
                }
                sb.append(text.substring(start, pos1));
                sb.append("<n>");
                sb.append(validateNPCText(proj, text.substring(pos1 + 3, pos2)));
                sb.append("</n>");
                start = pos2 + 4;
            }
        }
        return sb.toString();
    }

    /*
     * <l>192,许田镇:35,26</l>
     */
    private static String validateLocationText(ProjectData proj, String text) throws Exception {
        int pos1 = text.indexOf(',');
        if (pos1 == -1) {
            throw new Exception("描述字符串引用的场景格式错误。");
        }
        int pos2 = text.lastIndexOf(':');
        if (pos2 == -1) {
            throw new Exception("描述字符串引用的场景格式错误。");
        }
        String loc = text.substring(pos2 + 1);
        int mapID;
        try {
            mapID = Integer.parseInt(text.substring(0, pos1));
        }
        catch (Exception e) {
            throw new Exception("描述字符串引用的场景格式错误。");
        }
        GameMapInfo gmi = GameMapInfo.findByID(proj, mapID);
        if (gmi == null) {
            throw new Exception("描述字符串中引用的场景不存在：" + mapID);
        }
        if (gmi.name.contains(":")) {
            throw new Exception("描述字符串中引用的场景名字包含':'：" + mapID);
        }
        String showName = gmi.name;
        pos1 = showName.indexOf('(');
        pos2 = showName.indexOf('|');
        int splitPos = -1;
        if (pos1 == -1) {
            splitPos = pos2;
        }
        else {
            if (pos2 == -1) {
                splitPos = pos1;
            }
            else {
                splitPos = Math.min(pos1, pos2);
            }
        }
        if (splitPos != -1) {
            showName = showName.substring(0, splitPos);
        }
        return mapID + "," + showName + ":" + loc;
    }

    /*
     * <n>917508,卫兵(成都城外:19,22)</n>
     */
    private static String validateNPCText(ProjectData proj, String text) throws Exception {
        int pos1 = text.indexOf(',');
        if (pos1 == -1) {
            throw new Exception("描述字符串引用的NPC格式错误。");
        }
        int npcID;
        try {
            npcID = Integer.parseInt(text.substring(0, pos1));
        }
        catch (Exception e) {
            throw new Exception("描述字符串引用的NPC格式错误。");
        }
        GameMapNPC npc = (GameMapNPC) GameMapNPC.findByID(proj, npcID);
        if (npc == null) {
            throw new Exception("描述字符串中引用的NPC不存在：" + npcID);
        }
        if (npc.owner.name.contains(":")) {
            throw new Exception("描述字符串中引用的场景名称中不能包含':'符号。");
        }
        String showName = npc.name;
        if (showName.contains("|")) {
            showName = showName.substring(0, showName.indexOf('|'));
        }
        String mapName = npc.owner.name;
        if (mapName.contains("|")) {
            mapName = mapName.substring(0, mapName.indexOf('|'));
        }
        return npc.getGlobalID() + "," + showName + "(" + mapName + ":" + (npc.x / 8) + "," + (npc.y / 8) + ")";
    }

    /**
     * 把用到的文本字符串中的NPC引用和场景引用都更新一遍。
     */
    public void validateMixedText() throws Exception {
        try {
            this.preDescription = validateMixedText(this.owner, this.preDescription);
            this.description = validateMixedText(this.owner, this.description);
            this.postDescription = validateMixedText(this.owner, this.postDescription);
            this.unfinishDescription = validateMixedText(this.owner, this.unfinishDescription);
            for (QuestTarget target : this.targets) {
                target.description = validateMixedText(this.owner, target.description);
                target.hint = validateMixedText(this.owner, target.hint);
            }

            if (!this.source.exists()) {
                QuestInfo qi = new QuestInfo(this);
                qi.load();
                for (QuestTrigger qt : qi.triggers) {
                    ExpressionList el = ExpressionList.fromString(qt.condition);
                    el.validateMixedText(this.owner);
                    qt.condition = el.toString();
                    el = ExpressionList.fromString(qt.action);
                    el.validateMixedText(this.owner);
                    qt.action = el.toString();
                }
                qi.save();
            }
        }
        catch (Exception e) {
            throw new Exception(e.getMessage() + ", 任务ID：" + this.id);
        }
    }

    // ************added by tzhang 2010-7-13*********
    /**
     * 计算自动生成的类名
     * 
     * @param classPrefix
     * @return
     */
    public String getClassName(String classPrefix) {
        String idStr = String.valueOf(id);
        while (idStr.length() < 3) {
            idStr = "0" + idStr;
        }
        return classPrefix + idStr;
    }

    public String getBaseClass() {
        if(owner.config.BaseQuestClass == null) {
            return "BaseQuest";
        } else {
            return owner.config.BaseQuestClass;
        }
    }
    
    public void generateJava(PrintWriter out, String packageName, String classPrefix) throws Exception {
        generateJava(this, out, packageName, classPrefix);
    }

    public static void generateJava(Quest quest, PrintWriter out, String packageName, String classPrefix) throws Exception {
        // package & import

        QuestInfo qi = new QuestInfo(quest);
        PQEUtils pqeUtils = qi.owner.owner.config.pqeUtils;
        try {
            qi.load();
        }catch(Exception e) {      
            e.printStackTrace();
        }

        String[] localVars = qi.getVariables();

        out.println("package " + packageName + ";");
        out.println();

        for(String importStr : quest.owner.config.pqeUtils.autoGenImports) {
            out.println(importStr);
        }

        out.println();

        String className = quest.getClassName(classPrefix);

        out.println("/****");
        out.println("   任务名称： " + quest.title);
        out.println("****/");

        out.print("public class " + className + " extends " + quest.getBaseClass());
        out.println(" {");
        // 构造函数
        out.println("   public " + className + "(Quest quest, QuestInfo questInfo) throws IOException {");
        out.println("       super(quest, questInfo);");
        
        for (QuestTrigger qt : qi.triggers) {
            String s = ExpressionList.convertVarNameToIndex(qt.condition, localVars); 
            if(s.equals("E_Kill")){
                out.println("     dirtyTag |= 1;");
            }
            else if(s.equals("E_KillPlayerFaction")){
                out.println("     dirtyTag |= 1 << 1;");
            }
            else if(s.equals("E_UseItem")){
                out.println("     dirtyTag |= 1 << 2;");
            }else{
                out.println("     dirtyTag = 0;");
                break;
            }
          }
        
        out.println("   }");
        // TODO Auto-generated constructor stub

        // ---------------------canAccept()------------------
        out.println("   public boolean canAccept(Player p) {");
        
        String fc = ExpressionList.convertVarNameToIndex(quest.precondition, localVars);
        int index = fc.indexOf(";");
        if(index > 0) {
            //前置任务属于或关系
            String[] cs = fc.split(";");
            out.print("       if(");
            for(int i=0; i<cs.length; i++) {
                if(i > 0) {
                    out.print(" && ");
                }
                out.print("!(");
                ExpressionList percondition = ExpressionList.fromString(cs[i]);                
                quest.parserEl(out, percondition, 0,quest.id,pqeUtils.TransMap);
                out.print(")");

            }
            out.print(")");
            out.println("{");       
            out.println("           return false;");
            out.println("       }");  
            
        } else {
            //前置任务属于与关系
            ExpressionList percondition = ExpressionList.fromString(fc);
            if (percondition.getExprCount() > 0) {
                out.print("       if(!(");
                quest.parserEl(out, percondition, 0,quest.id,pqeUtils.TransMap);

                out.print("))");
                out.println("{");
                out.println("           return false;");
                out.println("       }");
            }
        }
        
        
        fc = ExpressionList.convertVarNameToIndex(quest.condition, localVars);
        ExpressionList acceptel = ExpressionList.fromString(fc);
        if (acceptel.getExprCount() > 0) {
            out.print("       if(!(");
            quest.parserEl(out, acceptel, 0,quest.id,pqeUtils.TransMap);

            out.print("))");
            out.println("{");
            out.println("           return false;");
            out.println("       }");
        }
        out.println("       return true;");
        out.println("   }");
        out.println();

        // ---------------------canFinish()------------------
        out.println("   public boolean canFinish(Player p) {");

        fc = ExpressionList.convertVarNameToIndex(quest.finishCondition, localVars);
        ExpressionList finishel = ExpressionList.fromString(fc);
        if (finishel.getExprCount() > 0) {
            out.print("       if(!(");
            quest.parserEl(out, finishel, 0,quest.id,pqeUtils.TransMap);
            out.print("))");
            out.println("{");
            out.println("           return false;");
            out.println("       }");
        }
        out.println("       return true;");
        out.println("   }");
        out.println();

        // ---------------------update()------------------
        out.println("   public void update(Player p) {");

        for (QuestTrigger qt : qi.triggers) {
            fc = ExpressionList.convertVarNameToIndex(qt.condition, localVars);
            ExpressionList triggerConditonel = ExpressionList.fromString(fc);
            if (triggerConditonel.getExprCount() > 0) {
                out.print("       if(");
                quest.parserEl(out, triggerConditonel, 0,quest.id,pqeUtils.TransMap);
                out.println("){");
            }
            out.print("           ");

            fc = ExpressionList.convertVarNameToIndex(qt.action, localVars);
            ExpressionList triggerActionel = ExpressionList.fromString(fc);
            String s = triggerActionel.getExpr(0).getLeftExpr().toString();
            if (triggerActionel.getExpr(0).getLeftExpr().toString().equalsIgnoreCase("1")) {
                out.println("       }");
                continue;
            }
            quest.parserEl(out, triggerActionel, 1,quest.id,pqeUtils.TransMap);
            out.println(";");
            out.println("       }");
        }

        out.println("   }");
        out.println();

        // ---------------------finish()------------------
        out.println("   public void finish(Player p) {");
        out.println("   }");
        out.println();

        out.println("}");
        quest.implClass = packageName + "." + className;
    }
    
    public void parserEl(PrintWriter out, ExpressionList el, int state, int id,Map<String, String> map) {
        for (int i = 0; i < el.getExprCount(); i++) {
            parserExpr(out, el.getExpr(i), map,id);
            if (i < el.getExprCount() - 1) {
                if (state == 0)
                    out.println(" && ");
                if (state == 1){
                    out.println(";");   
                }
            }
            
        }
    }

    public void parserExpr(PrintWriter out, Expression expr, Map<String, String> conditionMap,int id) {
        if (expr.jjtGetChild(0) instanceof Expression) {
            for (int i = 0; i < expr.jjtGetNumChildren(); i++) {
                parserExpr(out, (Expression) expr.jjtGetChild(i), conditionMap,id);
                if (i < expr.jjtGetNumChildren() - 1)
                    out.print(" || ");
            }
        } else {
            parserExpr0(out, expr.getLeftExpr(), conditionMap, id);
            if (expr.getRightExpr() != null) {
                out.print(PQEUtils.op2str(expr.getOp()));
                parserExpr0(out, expr.getRightExpr(), conditionMap, id);
            }
        }
    }
    
    private void parserExpr0(PrintWriter out, Expr0 expr0, Map<String, String> conditionMap,int id) {
        if (expr0.type == Expr0.TYPE_NUMBER || expr0.type == Expr0.TYPE_STRING) {
            out.print(expr0.toString());
        } else if (expr0.type == Expr0.TYPE_IDENTIFIER) {
            String varName = expr0.toString();
            SystemVar sysVar = ProjectData.getActiveProject().config.pqeUtils.SYSTEM_VARS_MAP.get(varName);
            if (varName.startsWith("__")) {
                out.print("p.getQuestVM().getGlobalValue(\"" + varName + "\")");
            } else if (sysVar != null) {
                out.print(conditionMap.get(varName));
            } else if ("true".equals(varName)) {
                out.print("true");
            } else if ("false".equals(varName)) {
                out.print("false");
            } else {
                out.print("p.getQuestVM().stores.get(" + id + ").getValue(" + varName.substring(1) + ")");
            }
        } else if (expr0.type == Expr0.TYPE_FUNC) {
            FunctionCall fc = expr0.getFunctionCall();
            PQEUtils.SystemFunc func = ProjectData.getActiveProject().config.pqeUtils.SYSTEM_FUNCS_MAP.get(fc.funcName);
            out.print(conditionMap.get(fc.funcName) + "(");
            for (int j = 0; j < func.paramType.length; j++) {
                if (j != 0) {
                    out.print(",");
                }
                parserExpr(out, fc.getParam(j), conditionMap, id);
            }
            if (fc.funcName.equalsIgnoreCase("addItem") || fc.funcName.equalsIgnoreCase("removeItem")) {
                out.print(",");
                out.print("\""+"QUEST "+String.valueOf(id)+"\"");
            }
            out.print(")");
        }
    }
    
    /**
     * 对这个对象的属性进行国际化处理，如果有需要国际化的字符串，则提取出来到context中查找翻译结果。
     * @param context
     * @return 如果有某个属性被替换，返回true，否则返回false。
     */
    public boolean i18n(I18NContext context) {
        String tmp;
        boolean changed = false;
        tmp = context.input(title, "Quest Title(" + id + ")");
        if (tmp != null) {
            title = tmp;
            changed = true;
        }
        tmp = context.input(description, "Quest(" + id + ")");
        if (tmp != null) {
            description = tmp;
            changed = true;
        }
        tmp = context.input(preDescription, "Quest(" + id + ")");
        if (tmp != null) {
            preDescription = tmp;
            changed = true;
        }
        tmp = context.input(postDescription, "Quest(" + id + ")");
        if (tmp != null) {
            postDescription = tmp;
            changed = true;
        }
        tmp = context.input(unfinishDescription, "Quest(" + id + ")");
        if (tmp != null) {
            unfinishDescription = tmp;
            changed = true;
        }
        ExpressionList exprList = ExpressionList.fromString(finishCondition);
        if (I18NUtils.processExpressionList(exprList, context, id, null)) {
            finishCondition = exprList.toString();
            changed = true;
        }
        for (QuestTarget target : targets) {
            exprList = ExpressionList.fromString(target.condition);
            if (I18NUtils.processExpressionList(exprList, context, id, null)) {
                target.condition = exprList.toString();
                changed = true;
            }
            tmp = context.input(target.description, "Quest Target(" + id + ")");
            if (tmp != null) {
                target.description = tmp;
                changed = true;
            }
            tmp = context.input(target.hint, "Quest Target(" + id + ")");
            if (tmp != null) {
                target.hint = tmp;
                changed = true;
            }
        }

        QuestInfo qinfo = new QuestInfo(this);
        try {
            qinfo.load();
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
        if (qinfo.i18n(context)) {
            qinfo.save();
        }
        return changed;
    }
}
