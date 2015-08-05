package com.pip.game.data.item;

import org.jdom.Element;

/**
 * 一个物品类型的配置。
 */
public class ItemTypeConfig {
    /**
     * 类型编号。
     */
    public int id;
    /**
     * 类型名称
     */
    public String title;
    /**
     * 分类ID.
     */
    public int category;
    /**
     * 分类名称
     */
    public String categoryTitle;
    /**
     * 对应的编辑器类。
     */
    public String editorClass;
    
    
    public String toString() {
        return categoryTitle + " -> " + title;
    }
    
    /**
     * 从XML节点载入。
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
