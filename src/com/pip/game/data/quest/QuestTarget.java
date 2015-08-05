package com.pip.game.data.quest;

import org.jdom.*;

/**
 * �����һ��Ŀ�ꡣ
 * @author lighthu
 */
public class QuestTarget {
	public Quest owner;
	/**
	 * Ŀ�����������
	 */
	public String condition = "";
	/**
	 * Ŀ�������ı���
	 */
	public String description = "";
	/**
	 * ���Ŀ����ʾ��
	 */
	public String hint = "";
	
	public QuestTarget(Quest own) {
		owner = own;
	}

    public boolean equals(Object o) {
        return this == o;
    }
    
    public void load(Element elem) {
    	condition = elem.getAttributeValue("condition");
//        description = elem.getText();
    	try {
            description = elem.getAttributeValue("description");
        }
        catch (Exception e) {
        }
        if(description == null){
            description = "";
        }
        hint = elem.getAttributeValue("hint");
        if (hint == null) {
            hint = "";
        }
    }
    
    public Element save() {
        Element ret = new Element("target");
        ret.addAttribute("condition", condition);
        ret.addAttribute("description", description);
        if (hint != null && hint.length() > 0) {
            ret.addAttribute("hint", hint);
        }
        //ret.setText(description);
        return ret;
    }
    
    public QuestTarget duplicate() {
    	QuestTarget ret = new QuestTarget(owner);
    	ret.condition = condition;
    	ret.description = description;
    	ret.hint = hint;
    	return ret;
    }
}
