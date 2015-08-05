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
 * һ����Ϸ���������ֻ��������Ļ������ԣ����������ϸ���ݰ����ڶ�Ӧ�������ļ��С�
 */
public class Quest extends DataObject {
    /**
     * ������Ŀ��
     */
    public ProjectData owner;
    /**
     * ��Ӧ�������ļ���
     */
    public java.io.File source;
    /**
     * �������ͣ�0 - ��ͨ��1 - ������
     */
    public int type;
    /**
     * ���������Ӧ�ĵ���ID.
     */
    public int areaID;
    /**
     * �ظ����ͣ�0 - �����ظ���1 - ÿ�¿����1�Ρ�2 - ÿ�ܿ����1�Ρ�3 - ÿ������1�Ρ�4 - �����ظ�, 5��ʱ����ظ�
     */
    public int repeatType;
    /**
     * ���������NPC.
     */
    public String startNpc = "";
    /**
     * ���������NPC��-1��ʾû�С�
     */
    public String finishNpc = "";

    /**
     * ���񼶱�
     */
    public int level;
    /**
     * ���������ǰ����������
     */
    public String precondition = "";

    /**
     * �����������չ������
     */
    public String condition = "";
    /**
     * ��������ʱ��Ҫ�����յı����ռ䡣
     */
    public int requireFreeBag;
    /**
     * ������ɵ��������������������������Ŀ�������������ɵġ�
     */
    public String finishCondition = "";
    /**
     * ��������ʱ��������
     */
    public String preDescription = "";
    /**
     * �������ʱ��������
     */
    public String postDescription = "";
    /**
     * δ�������ʱ��������
     */
    public String unfinishDescription = "";

    /**
     * ��ʼʱ��
     */
    public String repeatBeginTime = "00:00:00";

    /**
     * ��ֹʱ�䡣������ʱ����ظ��Ż���Ч
     */
    public String repeatEndTime = "00:00:00";
    /**
     * ����Ŀ�ꡣ
     */
    public List<QuestTarget> targets = new ArrayList<QuestTarget>();
    /**
     * ��������֧��
     */
    public List<QuestRewardSet> rewards = new ArrayList<QuestRewardSet>();
    /**
     * ����Ŀ��ȫ�����ʱ�Ƿ���Ҫ�ڿͻ�����ʾ֪ͨ��Ϣ��
     */
    public boolean notifyFinish = true;
    /**
     * �Ƿ��ڽ���ʱ�Զ������������ҡ�
     */
    public boolean autoShare = false;
    /**
     * �Ƿ���Ч��
     */
    public boolean valid = true;
    /**
     * �Ƿ������������ǣ�������ʼnpc�ͽ���npc��ͬʱ����������ͻ��˷���һ��
     */
    public boolean isRandomQuest;

    /**
     * ʵ��Class��
     */
    public String implClass;

    public Quest(ProjectData owner) {
        this.owner = owner;
    }

    public int getID() {
        return id;
    }

    //���������񵼳�ʹ��
    public String getStartNpcName() {
        String npcName = "";
        if (startNpc == null || startNpc.equals("")) {
            npcName = "δ����";
        }
        else {
            /*
             * GameMapObject startNpc = GameMapNPC.findByID(owner, startNPC); if
             * (startNpc instanceof GameMapNPC) { npcName = startNpc.toString();
             * } else { npcName = "�������"; }
             */
            String[] npcString = Utils.splitString(startNpc, ';');
            for (int i = 0; i < npcString.length; i++) {
                int npcId = Utils.parseHex(npcString[i]);
                GameMapObject startNpc = GameMapNPC.findByID(owner, npcId);
                if (startNpc instanceof GameMapNPC) {
                    npcName += startNpc.toString() + ";";
                } else if (npcName == null) {
                    npcName = "δ����";
                }
                else {
                    npcName = "�������";
                }
            }
        }
        return npcName;
    }
    
    //���������񵼳�ʹ��
    public String getFinishNpcName() {
        String npcName = "";
        if (finishNpc == null || finishNpc.equals("")) {
            npcName = "δ����";
        }
        else {
            /*
             * GameMapObject startNpc = GameMapNPC.findByID(owner, startNPC); if
             * (startNpc instanceof GameMapNPC) { npcName = startNpc.toString();
             * } else { npcName = "�������"; }
             */
            String[] npcString = Utils.splitString(finishNpc, ';');
            for (int i = 0; i < npcString.length; i++) {
                int npcId = Utils.parseHex(npcString[i]);
                GameMapObject startNpc = GameMapNPC.findByID(owner, npcId);
                if (startNpc instanceof GameMapNPC) {
                    npcName += startNpc.toString() + ";";
                } else if (npcName == null) {
                    npcName = "δ����";
                }
                else {
                    npcName = "�������";
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
            // ��������
            GameArea ga = (GameArea) owner.findObject(GameArea.class, areaID);
            if (ga == null) {
                return "��������(δ֪����)";
            }
            else {
                return "��������(" + ga.title + ")";
            }
        }
        else {
            String ret = "";
            // if (startNPC == -1) {
            // ret = "��ʼ��δ����";
            // } else {
            // GameMapObject startNpc = GameMapNPC.findByID(owner, startNPC);
            // if (startNpc instanceof GameMapNPC) {
            // ret = "��ʼ��" + startNpc.toString();
            // } else {
            // ret = "��ʼ���������";
            // }
            // }
            if (startNpc == null || startNpc.equals("")) {
                ret = "��ʼ��δ����";
            }
            else {
                ret = "��ʼ��";
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
                        ret = "�������";
                    }
                }
            }
            ret += "��";
            // if (finishNPC == -1) {
            // ret += "������δ����";
            // } else {
            // GameMapObject finishNpc = GameMapNPC.findByID(owner, finishNPC);
            // if (finishNpc instanceof GameMapNPC) {
            // ret += "������" + finishNpc.toString();
            // } else {
            // ret += "�������������";
            // }
            // }
            if (finishNpc == null || finishNpc.equals("")) {
                ret = "������δ����";
            }
            else {
                ret = "������";
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
                        ret = "�������";
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
        // ��Ϊû�л���QuestInfo���󣬵���QuestInfo�����޷��Ƚϣ�����ֻ��ȫ�����¡�
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
     * ����һ����������֡�
     * 
     * @param project
     * @param questID
     * @return
     */
    public static String toString(ProjectData project, int questID) {
        Quest q = (Quest) project.findObject(Quest.class, questID);
        if (q == null) {
            return "δ֪����";
        }
        else {
            return q.toString();
        }
    }

    /**
     * У���ϸ�ʽ�ַ����������е�NPC���úͳ���λ�����ø��³����µİ汾��
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
     * <l>192,������:35,26</l>
     */
    private static String validateLocationText(ProjectData proj, String text) throws Exception {
        int pos1 = text.indexOf(',');
        if (pos1 == -1) {
            throw new Exception("�����ַ������õĳ�����ʽ����");
        }
        int pos2 = text.lastIndexOf(':');
        if (pos2 == -1) {
            throw new Exception("�����ַ������õĳ�����ʽ����");
        }
        String loc = text.substring(pos2 + 1);
        int mapID;
        try {
            mapID = Integer.parseInt(text.substring(0, pos1));
        }
        catch (Exception e) {
            throw new Exception("�����ַ������õĳ�����ʽ����");
        }
        GameMapInfo gmi = GameMapInfo.findByID(proj, mapID);
        if (gmi == null) {
            throw new Exception("�����ַ��������õĳ��������ڣ�" + mapID);
        }
        if (gmi.name.contains(":")) {
            throw new Exception("�����ַ��������õĳ������ְ���':'��" + mapID);
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
     * <n>917508,����(�ɶ�����:19,22)</n>
     */
    private static String validateNPCText(ProjectData proj, String text) throws Exception {
        int pos1 = text.indexOf(',');
        if (pos1 == -1) {
            throw new Exception("�����ַ������õ�NPC��ʽ����");
        }
        int npcID;
        try {
            npcID = Integer.parseInt(text.substring(0, pos1));
        }
        catch (Exception e) {
            throw new Exception("�����ַ������õ�NPC��ʽ����");
        }
        GameMapNPC npc = (GameMapNPC) GameMapNPC.findByID(proj, npcID);
        if (npc == null) {
            throw new Exception("�����ַ��������õ�NPC�����ڣ�" + npcID);
        }
        if (npc.owner.name.contains(":")) {
            throw new Exception("�����ַ��������õĳ��������в��ܰ���':'���š�");
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
     * ���õ����ı��ַ����е�NPC���úͳ������ö�����һ�顣
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
            throw new Exception(e.getMessage() + ", ����ID��" + this.id);
        }
    }

    // ************added by tzhang 2010-7-13*********
    /**
     * �����Զ����ɵ�����
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
        out.println("   �������ƣ� " + quest.title);
        out.println("****/");

        out.print("public class " + className + " extends " + quest.getBaseClass());
        out.println(" {");
        // ���캯��
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
            //ǰ���������ڻ��ϵ
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
            //ǰ�������������ϵ
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
     * �������������Խ��й��ʻ������������Ҫ���ʻ����ַ���������ȡ������context�в��ҷ�������
     * @param context
     * @return �����ĳ�����Ա��滻������true�����򷵻�false��
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
