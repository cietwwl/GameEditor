package com.pip.game.data.extprop;

import org.jdom.Element;

/**
 * 在data中使用,不包含编辑器(swt)相关的内容;
 * @author jhkang
 *
 */
public class ExtPropEntry {

    public String key;
    /**
     */
    public String value = "";

    public String toString(){
        return key.concat("="+value);
    }
    
    public ExtPropEntry() {
        super();
    }

    /**
     * load skill.xml 中的prop节点
     * @param prop
     */
    public void load(Element prop) {
        key = prop.getAttributeValue("key");
        value = prop.getAttributeValue("value");
    }

    public String getKey() {
        return key;
    }
    
    public String getValue(){
        return value;
    }
}