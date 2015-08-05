package com.pip.game.data.extprop;

import org.jdom.Element;

/**
 * ��data��ʹ��,�������༭��(swt)��ص�����;
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
     * load skill.xml �е�prop�ڵ�
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