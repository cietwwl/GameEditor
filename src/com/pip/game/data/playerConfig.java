package com.pip.game.data;

import java.io.File;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.pip.game.data.equipment.EquipmentAttribute;
import com.pip.util.Utils;

public class playerConfig {
    /**
     * ������侭�飬��0����ʼ
     */
    public static int[] LEVEL_EXP;
    
    /**
     * ��������Ǯ����0����ʼ
     */
    public static int[] LEVEL_MONEY;
    
    /** ְҵѡ�� */
    public static String[] COMBO_JOB;
    
    /** ְҵѡ�� */
    public static String[] COMBO_JOB_EXT ;
    
    /** ʱЧ����ѡ�� */
    public static String[] COMBO_TIME_TYPE;

    /** ��ѡ�� */
    public static String[] COMBO_BIND;

    /** Ʒ��ѡ�� */
    public static String[] COMBO_QUALITY;
    
    /** Ʒ�ʶ�Ӧ����ɫ */
    public static int[] QUALITY_COLOR;
    
    /** ȫ��װ������ѡ�� */
    public static String[] COMBO_PLACE;

    /** ȫ��װ���������� */
    public static String[] PLACE_NAMES;
    
    
    public static float[] QUALITY_ADDITION;
    
    /**
     * ���޸ĵ����ԡ�
     */
    public static EquipmentAttribute[] ATTRIBUTES;
    /**
     * ��װ����λռ�ܼ�ֵ�ı���
     */
    public static float[] RATE_PLACE;
    
    /**
     * ���������Ĺ��������޺�ƽ����������ȵı��ʡ�
     */
    public static float[][] WEAPON_RANGE;
    public static float[][] WEAPON_MRANGE;
    
    public static void load(File baseDir) throws Exception {
        Document doc = Utils.loadDOM(new File(baseDir, "player.config"));
        Element element = null;
        List list = null;
        String attr = null;
        String[] values = null;
        
        element = doc.getRootElement();
        attr = element.getAttributeValue("LEVEL_EXP");
        values = attr.split(",");
        LEVEL_EXP = new int[values.length];
        for(int i=0; i<values.length; i++) {
            LEVEL_EXP[i] = Integer.parseInt(values[i].trim());
        }
        attr = element.getAttributeValue("LEVEL_MONEY");
        values = attr.split(",");
        LEVEL_MONEY = new int[values.length];
        for(int i=0; i<values.length; i++) {
            LEVEL_MONEY[i] = Integer.parseInt(values[i].trim());
        }
        
        attr = element.getAttributeValue("JOB");
        COMBO_JOB = attr.split(",");
        
        attr = element.getAttributeValue("JOB_EXT");
        COMBO_JOB_EXT = attr.split(",");
        
        attr = element.getAttributeValue("TIME_TYPE");
        COMBO_TIME_TYPE = attr.split(",");
        
        list = doc.getRootElement().getChildren("equiment");
        element = (Element)list.get(0);
        attr = element.getAttributeValue("QUALITY");
        COMBO_QUALITY = attr.split(",");
        
        attr = element.getAttributeValue("QUALITY_COLOR");
        values = attr.split(",");
        QUALITY_COLOR = new int[values.length];
        for(int i=0; i<values.length; i++) {
            QUALITY_COLOR[i] = Integer.parseInt(values[i].trim().substring(2), 16);
        }
        attr = element.getAttributeValue("QUALITY_ADDITION");
        values = attr.split(",");
        QUALITY_ADDITION = new float[values.length];
        for(int i=0; i<values.length; i++) {
            QUALITY_ADDITION[i] = Float.parseFloat(values[i].trim());
        }
        attr = element.getAttributeValue("BIND_TYPE");
        COMBO_BIND = attr.split(",");
        
        element = doc.getRootElement().getChild("equiment");
        element = element.getChild("variableAttrs");
        list = element.getChildren("variableAttr");
        ATTRIBUTES = new EquipmentAttribute[list.size()]; 
        for(int i=0; i<list.size(); i++) {
            element = (Element)list.get(i);
            String id = element.getAttributeValue("id");
            String name = element.getAttributeValue("name");
            String shortname = element.getAttributeValue("shortname");
            String value = element.getAttributeValue("value");
            float fvalue = Float.parseFloat(value.trim());
            ATTRIBUTES[i] = new EquipmentAttribute(id, name, shortname, fvalue);
        }
        
        element = doc.getRootElement().getChild("equiment");
        element = element.getChild("PLACES");
        list = element.getChildren("PLACE");
        PLACE_NAMES = new String[list.size()];
        RATE_PLACE = new float[list.size()];
        for(int i=0; i<list.size(); i++) {
            element = (Element)list.get(i);
            PLACE_NAMES[i] = element.getAttributeValue("name");
            RATE_PLACE[i] = Float.parseFloat(element.getAttributeValue("rate").trim());
        }
        
        element = doc.getRootElement().getChild("equiment");
        element = element.getChild("COMBO_PLACES");
        list = element.getChildren("COMBO_PLACE");
        COMBO_PLACE = new String[list.size()];
        for(int i=0; i<list.size(); i++) {
            element = (Element)list.get(i);
            COMBO_PLACE[i] = element.getAttributeValue("name");
        }
        
        element = doc.getRootElement().getChild("equiment");
        element = element.getChild("WEAPONS");
        list = element.getChildren("WEAPON");
        WEAPON_RANGE = new float[list.size()][2];
        WEAPON_MRANGE = new float[list.size()][2];
        for(int i=0; i<list.size(); i++) {
            element = (Element)list.get(i);
            WEAPON_RANGE[i][0] = Float.parseFloat(element.getAttributeValue("range_min").trim());
            WEAPON_RANGE[i][1] = Float.parseFloat(element.getAttributeValue("range_max").trim());
            WEAPON_MRANGE[i][0] = Float.parseFloat(element.getAttributeValue("mrange_min").trim());
            WEAPON_MRANGE[i][1] = Float.parseFloat(element.getAttributeValue("mrange_max").trim());
        }
    }
}
