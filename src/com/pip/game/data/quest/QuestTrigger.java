package com.pip.game.data.quest;

import java.util.*;
import org.jdom.*;

/**
 * �����е�һ��������������������2�����֣����������ʹ���������
 * @author lighthu
 */
public class QuestTrigger {
    /**
     * ����������
     */
    public String condition;
    /**
     * ����������
     */
    public String action;
    /**
     * �����������滻��������İ汾��
     */
    public String serverCondition;
    /**
     * �����������滻��������İ汾��
     */
    public String serverAction;

    public Element save() {
        Element ret = new Element("trigger");
        ret.addAttribute("condition", condition);
        ret.addAttribute("action", action);
        if (serverCondition != null) {
            ret.addAttribute("servercondition", serverCondition);
        }
        if (serverAction != null) {
            ret.addAttribute("serveraction", serverAction);
        }
        return ret;
    }
    
    public void load(Element elem) {
        condition = elem.getAttributeValue("condition");
        action = elem.getAttributeValue("action");
        condition = condition.replaceAll("\\\\r\\\\n", "\\\\n");
        action = action.replaceAll("\\\\r\\\\n", "\\\\n");
        
        serverCondition = elem.getAttributeValue("servercondition");
        serverAction = elem.getAttributeValue("serveraction");
        if (serverCondition != null) {
            serverCondition = serverCondition.replaceAll("\\\\r\\\\n", "\\\\n");
        }
        if (serverAction != null) {
            serverAction = serverAction.replaceAll("\\\\r\\\\n", "\\\\n");
        }
    }
}
