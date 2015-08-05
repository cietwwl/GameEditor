package com.pip.game.data.quest;

import org.jdom.*;
import java.util.*;

/**
 * �����һ��������֧��һ����֧���԰��������Ʒ��
 */
public class QuestRewardSet {
	public Quest owner;
	/**
	 * ��֧ID��
	 */
	public int id;
	/**
	 * �Ƿ����������������
	 */
	public boolean isFinishReward;
	/**
	 * �����
	 */
	public List<QuestRewardItem> rewardItems = new ArrayList<QuestRewardItem>();

    public QuestRewardSet(Quest owner) {
        this.owner = owner;
    }

    public int getID() {
        return id;
    }
    
    public String toString() {
        return "��֧" + id + (isFinishReward ? "(��������)" : "");
    }

    public boolean equals(Object o) {
        return this == o;
    }
    
    public String getRewardMoney() {
        if(rewardItems == null || rewardItems.size() == 0){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<rewardItems.size(); i++){
            if(rewardItems.get(i).rewardType == QuestRewardItem.REWARD_MONEY) {
                sb.append(rewardItems.get(i).rewardValue);
                if(i != rewardItems.size() - 1)
                    sb.append("\n");
            }
           
        }
        return sb.toString();
    }
    
    public String getRewardExp() {
        if(rewardItems == null || rewardItems.size() == 0){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<rewardItems.size(); i++){
            if(rewardItems.get(i).rewardType == QuestRewardItem.REWARD_EXP) {
                sb.append(rewardItems.get(i).rewardValue);
                if(i != rewardItems.size() - 1)
                    sb.append("\n");
            }
           
        }
        return sb.toString();
    }
    
    public String getRewardImoney() {
        if(rewardItems == null || rewardItems.size() == 0){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<rewardItems.size(); i++){
            if(rewardItems.get(i).rewardType == QuestRewardItem.REWARD_IMONEY) {
                sb.append(rewardItems.get(i).rewardValue);
                if(i != rewardItems.size() - 1)
                    sb.append("\n");
            }
           
        }
        return sb.toString();
    }
    
    public String getRewardItem(){
        if(rewardItems == null || rewardItems.size() == 0){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<rewardItems.size(); i++){
            if(rewardItems.get(i).rewardType != QuestRewardItem.REWARD_MONEY &&
                    rewardItems.get(i).rewardType != QuestRewardItem.REWARD_EXP && 
                    rewardItems.get(i).rewardType != QuestRewardItem.REWARD_IMONEY) {
                sb.append(rewardItems.get(i).toString());
                if(i != rewardItems.size() - 1)
                    sb.append("\n");
            }
            
        }
//        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    
    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        isFinishReward = "true".equals(elem.getAttributeValue("isfinishreward"));
        List itemElems = elem.getChildren("rewarditem");
        for (int i = 0; i < itemElems.size(); i++) {
        	QuestRewardItem item = new QuestRewardItem(this);
        	item.load((Element)itemElems.get(i));
        	rewardItems.add(item);
        }
    }
    
    public Element save() {
        Element ret = new Element("rewardset");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("isfinishreward", isFinishReward ? "true" : "false");
        Collections.sort(rewardItems,new Comparator<QuestRewardItem>(){
            public int compare(QuestRewardItem q1, QuestRewardItem q2) {
                int t1 = q1.rewardType;
                int t2 = q2.rewardType;
                if (t1 < t2) {
                    return -1;
                } else if (t1 == t2) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        for (QuestRewardItem item : rewardItems) {
        	ret.addContent(item.save());
        }
        return ret;
    }
    
    public QuestRewardSet duplicate() {
    	QuestRewardSet ret = new QuestRewardSet(owner);
    	ret.id = id;
    	ret.isFinishReward = isFinishReward;
    	for (QuestRewardItem item : rewardItems) {
    		QuestRewardItem newItem = item.duplicate();
    		newItem.owner = ret;
    		ret.rewardItems.add(newItem);
    	}
    	return ret;
    }
}
