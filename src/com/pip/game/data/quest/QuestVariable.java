package com.pip.game.data.quest;

import java.util.*;
import org.jdom.*;

/**
 * �����е�һ��������
 * @author lighthu
 */
public class QuestVariable {
    /**
     * �������ơ�
     */
    public String name = "";
    
    public Element save() {
        Element ret = new Element("variable");
        ret.addAttribute("name", name);
        return ret;
    }
    
    public void load(Element elem) {
        name = elem.getAttributeValue("name");
    }
    
    public String toString() {
        return name;
    }
}
