package com.pip.game.data.item;

import org.jdom.Element;

/**
 * һ����Ʒ���͵����á�
 */
public class ItemTypeConfig {
    /**
     * ���ͱ�š�
     */
    public int id;
    /**
     * ��������
     */
    public String title;
    /**
     * ����ID.
     */
    public int category;
    /**
     * ��������
     */
    public String categoryTitle;
    /**
     * ��Ӧ�ı༭���ࡣ
     */
    public String editorClass;
    
    
    public String toString() {
        return categoryTitle + " -> " + title;
    }
    
    /**
     * ��XML�ڵ����롣
     * @param elem
     */
    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
        editorClass = elem.getAttributeValue("editor");
        category = Integer.parseInt(elem.getParent().getAttributeValue("id"));
        categoryTitle = elem.getParent().getAttributeValue("title");
    }
}
