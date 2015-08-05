package com.pip.game.data;

import org.jdom.*;

/**
 * С��ʾ��
 * @author lighthu
 */
public class Hint {
    /**
     * ����û����𣨰�����
     */
    public int minLevel;
    /**
     * ����û����𣨰�����
     */
    public int maxLevel;
    /**
     * Ȩ��
     */
    public int weight;
    /**
     * ����
     */
    public String message;
    /**
     * ����������ͣ�-1��ʾ������
     */
    public int keyboardType;
    /**
     * ����������ͣ�-1��ʾ������
     */
    public int mouseType;
    
    public void load(Element elem) {
        minLevel = Integer.parseInt(elem.getAttributeValue("minlevel"));
        maxLevel = Integer.parseInt(elem.getAttributeValue("maxlevel"));
        weight = Integer.parseInt(elem.getAttributeValue("weight"));
        try {
            keyboardType = Integer.parseInt(elem.getAttributeValue("keyboard"));
        } catch (Exception e) {
            keyboardType = -1;
        }
        try {
            mouseType = Integer.parseInt(elem.getAttributeValue("mouse"));
        } catch (Exception e) {
            mouseType = -1;
        }
        message = elem.getText();
    }
}
