package com.pip.game.data.quest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pip.game.editor.quest.QuestExportToExcel;

public class QuestBean {
    /**����ID*/
    private int questID;
    /**������*/
    private String questTitle;
    /**���񼶱�*/
    private int questLevel;
    /**��������*/
    private String questType;
    /**��ʼNPC*/
    private String questStartNpc;
    /**����NPC*/
    private String questFinishNpc;
    /**��ȡ������ʾ*/
    private String questPreDescription;
    /**����Ŀ��*/
    private String questTargets;
    /**����������*/
    private String questRewardsExp;
    /**�������Ǯ*/
    private String questRewardsMoney;
    /**������*/
    private String questRewardsItem;
    /**ǰ������*/
    private String questCondition;
    /**��ȡ��ʾ-������ʽ*/
    private final String REGEX_PREDESCRIPTION = "<c\\w+>|</[cl]>|</?i>|<n>\\d+,|\\(.+\\)</n>|<l>\\d+,";
    
    private final String REGEX_QUESTREWARDSITEM = "��Ʒ\\s*\\d+:";
    
    /**����ת��
     * str--ԭʼ�ַ���
     * regexStr--ת������
     * replaceStr--�滻�ı�
     * */
    public String regexString(String str, String regexStr, String replaceStr){
        if(str == null || str == ""){
            return "";
        }
        String resultStr = str;
        Pattern pattern = null;
        pattern = Pattern.compile(regexStr, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(str);

        if(matcher.find()){
            resultStr = matcher.replaceAll(replaceStr);
//            if(regexStr == REGEX_TARGETS) System.out.println(resultStr);
        }
        return resultStr;
    }
    
    private String regexTargetsString(String str, QuestExportToExcel q){
        if(str == null || str == ""){
            return "";
        }
        String resultStr = str;
        Pattern pattern = null;
        pattern = Pattern.compile("hasitem\\((\\d+),\\s+(\\d+)\\)", Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()){
            int id = Integer.parseInt(matcher.group(1));
            int num = Integer.parseInt(matcher.group(2));
            String itemTitle = q.itemTable.get(id);
            resultStr = matcher.replaceFirst("ӵ��" + num + "��" + "��" + itemTitle + "��");
//            System.out.println(resultStr);
            return regexTargetsString(resultStr, q);
        }
        else{
            return resultStr;
        }
    }
    
    private String regexTargetsString(String str){
        if(str == null || str == ""){
            return "";
        }
        String resultStr = str;
        Pattern pattern = null;
        pattern = Pattern.compile("\\s+==\\s+(1\\d+|[2-9]\\d*)");
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()){
            resultStr = matcher.replaceFirst(matcher.group(1) + "��");
//            System.out.println(resultStr);
            return regexTargetsString(resultStr);
        }
        else{
            return resultStr;
        }
    }
    
    private String regexQuestCondition(String str, QuestExportToExcel q){
        if(str == null || str == ""){
            return "";
        }
        String resultStr = str;
        Pattern pattern = null;
        pattern = Pattern.compile(".*TaskFinished\\((\\d+)\\).*");
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()){
            resultStr = matcher.replaceFirst(q.beforeQuest.get(Integer.parseInt(matcher.group(1))));
//            System.out.println(resultStr);
            return regexQuestCondition(resultStr, q);
        }
        else{
            return resultStr;
        }
    }
    
    public String getQuestRewardsExp() {
        return regexString(questRewardsExp, "", "");
//        return questRewardsItem;
    }
    
    public String getQuestRewardsMoney() {
        return regexString(questRewardsMoney, "", "");
//        return questRewardsItem;
    }
    
    public String getQuestRewardsItem() {
        return regexString(questRewardsItem, REGEX_QUESTREWARDSITEM, "");
//        return questRewardsItem;
    }
    
    public void setQuestRewardsExp(String questRewardsExp) {
        this.questRewardsExp = questRewardsExp;
    }
    
    public void setQuestRewardsMoney(String questRewardsMoney) {
        this.questRewardsMoney = questRewardsMoney;
    }
    
    public void setQuestRewardsItem(String questRewardsItem) {
        this.questRewardsItem = questRewardsItem;
    }
    public String getQuestCondition(QuestExportToExcel q) {
        return regexQuestCondition(questCondition, q);
//        return questCondition;
    }
    public void setQuestCondition(String questCondition) {
        this.questCondition = questCondition;
    }
    public String getQuestTargets(QuestExportToExcel q) {
        questTargets = regexTargetsString(questTargets, q);//�滻hasitem(id,num)
        questTargets = regexString(questTargets, "1\\s+==\\s+1", "��ɶԻ�");//�滻1 == 1
        questTargets = regexTargetsString(questTargets);//�滻ɱ������
        questTargets = regexString(questTargets, "\\s+==\\s+1", "");//�滻 == 1
        questTargets = regexString(questTargets, "_LEVEL", "�ȼ�");
        return questTargets;
    }
    public int getQuestLevel() {
        return questLevel;
    }
    public void setQuestLevel(int questLevel) {
        this.questLevel = questLevel;
    }
    public void setQuestTargets(String questTargets) {
        this.questTargets = questTargets;
    }
    public String getQuestPreDescription() {
        return regexString(questPreDescription, REGEX_PREDESCRIPTION, "");
    }
    public void setQuestPreDescription(String questPreDescription) {
        this.questPreDescription = questPreDescription;
    }
    public String getQuestStartNpc() {
        return questStartNpc;
    }
    public void setQuestStartNpc(String questStartNpc) {
        this.questStartNpc = questStartNpc;
    }
    
    public String getQuestFinishNpc() {
        return this.questFinishNpc;
    }
    public void setQuestFinishNpc(String questFinishNpc) {
        this.questFinishNpc = questFinishNpc;
    }
    public String getQuestType() {
        return questType;
    }
    public void setQuestType(String questType) {
        this.questType = questType;
    }
    public int getQuestID() {
        return questID;
    }
    public void setQuestID(int questID) {
        this.questID = questID;
    }
    public String getQuestTitle() {
        return questTitle;
    }
    public void setQuestTitle(String questTitle) {
        this.questTitle = questTitle;
    }

}
