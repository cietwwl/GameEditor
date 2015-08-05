package com.pip.game.data.quest;

import java.util.*;
import org.jdom.*;

/**
 * ������������һ��������
 * @author lighthu
 */
public class QuestAction {
    /**
     * �������͡�
     */
    public String command = "";
    /**
     * ����������
     */
    public List<String> params = new ArrayList<String>();

    public Element save() {
        Element ret = new Element("action");
        ret.addAttribute("command", command);
        for (String param : params) {
            Element elem = new Element("param");
            elem.addAttribute("value", param);
            ret.getMixedContent().add(elem);
        }
        return ret;
    }
    
    public void load(Element elem) {
        command = elem.getAttributeValue("command");
        List list = elem.getChildren("param");
        for (Object obj : list) {
            Element elem2 = (Element)obj;
            params.add(elem2.getAttributeValue("value"));
        }
    }
}
