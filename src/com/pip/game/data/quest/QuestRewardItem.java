package com.pip.game.data.quest;

import org.jdom.Element;

import com.pip.game.data.Currency;
import com.pip.game.data.ProjectData;

/**
 * ��������һ�������һ������������ǽ�Ǯ�����������顢��Ʒ����װ����
 * @author lighthu
 */
public class QuestRewardItem {
	/** �������ͣ���Ǯ */
	public static final int REWARD_MONEY = 1;
	/** �������ͣ����� */
	public static final int REWARD_EXP = 2;
	/** �������ͣ���Ʒ��װ�� */
	public static final int REWARD_ITEM = 3;
	/** �������ͣ�Ԫ�� */
	public static final int REWARD_IMONEY = 4;
	/** ��������>100�ı�ʾ��չ���� */
	
	public QuestRewardSet owner;
	/**
	 * �������͡�
	 */
	public int rewardType = REWARD_MONEY;
	/**
	 * ��������������ǽ�Ǯ���������飬���ǽ������������������Ʒ��װ�������ǽ�����Ʒ��ģ��ID��
	 */
	public int rewardValue;
	/**
	 * �������������Ʒ���������ָ��������������
	 */
	public int itemCount;
	
	public QuestRewardItem(QuestRewardSet own) {
		owner = own;
	}

    public boolean equals(Object o) {
        return this == o;
    }
    
    public String toString() {
    	switch (rewardType) {
    	case REWARD_MONEY:
    		return "��Ǯ" + rewardValue;
    	case REWARD_EXP:
    		return "����" + rewardValue;
    	case REWARD_ITEM:
    		return "��Ʒ " + ProjectData.getActiveProject().findItemOrEquipment(rewardValue) + " x" + itemCount;
    	case REWARD_IMONEY:
    	    return "Ԫ�� " + rewardValue;
    	default:
    	    Currency currency = (Currency)ProjectData.getActiveProject().findDictObject(Currency.class, rewardType);
    	    if (currency != null) {
    	        return currency.title + rewardValue;
    	    } else {
    	        return "δ֪";
    	    }
    	}
    }
    
    public void load(Element elem) {
    	rewardType = Integer.parseInt(elem.getAttributeValue("type"));
    	rewardValue = Integer.parseInt(elem.getAttributeValue("value"));
    	try {
    		itemCount = Integer.parseInt(elem.getAttributeValue("count"));
    	} catch (Exception e) {
    		itemCount = 1;
    	}
    }
    
    public Element save() {
        Element ret = new Element("rewarditem");
        ret.addAttribute("type", String.valueOf(rewardType));
        ret.addAttribute("value", String.valueOf(rewardValue));
        if (itemCount != 1) {
        	ret.addAttribute("count", String.valueOf(itemCount));
        }
        return ret;
    }
    
    public QuestRewardItem duplicate() {
    	QuestRewardItem ret = new QuestRewardItem(owner);
    	ret.rewardType = rewardType;
    	ret.rewardValue = rewardValue;
    	ret.itemCount = itemCount;
    	return ret;
    }
}
