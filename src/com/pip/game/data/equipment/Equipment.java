package com.pip.game.data.equipment;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.data.i18n.I18NContext;
import com.pip.game.data.item.Item;
import com.pip.game.data.skill.BuffConfig;
import com.pip.game.editor.property.BuffPropertyDescriptor;

/**
 * װ����������
 * @author Joy
 */
public class Equipment extends Item {
    /**************************   ����   ***************************/
    
    //�������ļ��е�һ��(items/equipmentDef.xml types:entry:idx)
    /** װ������------���� */
    public static final int EQUI_TYPE_WEAPON = 0;
    /** װ������------���� */
    public static final int EQUI_TYPE_PROTECTOR = 1;
    /** װ������------��Ʒ */
    public static final int EQUI_TYPE_JEWELRY = 2;
    /** װ������------����װ�� */
    public static final int EQUI_TYPE_HORSE = 3;
    
    /** ���ԣ�����Ʒ��ϵ�� */
    public static final String PROPNAME_BASICRATE = "basicrate";
    /** ���ԣ�����Ʒ��ϵ�� */
    public static final String PROPNAME_EXTRARATE = "extrarate";
    /** ���ԣ�װ����ֵ */
    public static final String PROPNAME_EQUVALUE = "equvalue";
    /** ���ԣ�װ����ʾ��ֵ */
    public static final String PROPNAME_EQUSHOWNVALUE = "equshownvalue";
    
    /**************************   ����   ***************************/
    
    /** װ���;ö� */
    public int durability;
    
    /** ְҵ */
    public int job = -1;
    
    /** װ������ */
    public int equipmentType;
    
    /** װ�������� */
    public int equipmentOwner;
    
    /** �������� **/
    public int weaponType;
    
    /** ������������ */
    public int astrictInteligence;
    
    /** ������������ */
    public int astrictStamina;
    
    /** ������������ */
    public int astrictAgility;
    
    /** ������������ */
    public int astrictPower;
    
    /** ǰ׺ */
    public EquipmentPrefix prefix;
    /** ����Ʒ��ϵ�� */
    public float extraQuality;
    /** �Ƿ���ʾΪ���ǰ׺����������ʾ�䷽��Ʒ��*/
    public boolean showRandom;
    /** ��ʼ���� */
    public int holeCount = 1;
    /** ������ */
    public int maxHoleCount = 5;
    /** �Ƿ���������Ǽ� */
    public boolean canJudgeStar;
    /** �Ƿ������������ */
    public boolean canJudgePotential;
    
    /**
     * ������ֵ�����
     */
    public int markCharCount;
    
    /** �������� */
    public float[] appendAttributes;
    /** �����������ԣ������޸ģ�*/
    public float[] extendAttributes;
    
    /** װ����λ */
    public int place;
    
    /** ����BUFF ID��-1��ʾû�� */
    public int buffID = -1;
    /** ����BUFF���� */
    public int buffLevel;

    /** ���ԣ�BUFF ID */
    public static final String PROPNAME_BUFFID = "buffid";
    /** ���ԣ�BUFF���� */
    public static final String PROPNAME_BUFFLEVEL = "bufflevel";
    /** ���ԣ�BUFF��ֵ */
    public static final String PROPNAME_BUFFVALUE = "buffvalue";
   
    
    public Equipment(ProjectData owner) {
        super(owner);
        type = -1;
        mainType = -1;
        instance = true;
        prefix = new EquipmentPrefix(owner);
        taskFlag = false;
        appendAttributes = new float[owner.config.attrCalc.ATTRIBUTES.length];
        DataCalc = owner.config.getProjectCalc(getClass());
        resetIcon();
    }
    
    public DataObject duplicate() {
        Equipment copy = new Equipment(owner);
        copy.update(this);
        return copy;
    }

    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
        description = elem.getAttributeValue("desc");
        setCategoryName(elem.getAttributeValue("category"));
        if (getWholeCategoryName() == null) {
            setCategoryName("");
        }
        
        level = Integer.parseInt(elem.getAttributeValue("level"));
        durability = Integer.parseInt(elem.getAttributeValue("durability"));
        job = Integer.parseInt(elem.getAttributeValue("job"));
        equipmentType = Integer.parseInt(elem.getAttributeValue("equipmentType"));
        if(elem.getAttributeValue("equipmentOwner") != null)
        equipmentOwner = Integer.parseInt(elem.getAttributeValue("equipmentOwner"));
        String wtStr = elem.getAttributeValue("weaponType");
        if(wtStr != null && !"".equals(wtStr)) {
            weaponType = Integer.parseInt(wtStr);            
        }
        
        int prefixID = Integer.parseInt(elem.getAttributeValue("prefix"));
        prefix = (EquipmentPrefix)owner.findObject(EquipmentPrefix.class, prefixID);
        if (prefix == null) {
            prefix = new EquipmentPrefix(owner);
        }
        showRandom = "true".equals(elem.getAttributeValue("showrandom"));
        astrictInteligence = Integer.parseInt(elem.getAttributeValue("astrictInteligence"));
        astrictStamina = Integer.parseInt(elem.getAttributeValue("astrictStamina"));
        astrictAgility = Integer.parseInt(elem.getAttributeValue("astrictAgility"));
        astrictPower = Integer.parseInt(elem.getAttributeValue("astrictPower"));
        playerLevel = Integer.parseInt(elem.getAttributeValue("playerLevel"));
        place = Integer.parseInt(elem.getAttributeValue("place"));
        quality = Integer.parseInt(elem.getAttributeValue("quality"));
        bind = Integer.parseInt(elem.getAttributeValue("bind"));
        sale = !"false".equals(elem.getAttributeValue("sale"));
        price = Integer.parseInt(elem.getAttributeValue("price"));
        try {
            holeCount = Integer.parseInt(elem.getAttributeValue("holecount"));
        } catch (Exception e) {
            holeCount = 1;
        }
        try {
            maxHoleCount = Integer.parseInt(elem.getAttributeValue("maxholecount"));
        } catch (Exception e) {
            maxHoleCount = 5;
        }
        String tmp = elem.getAttributeValue("judgestar");
        if (tmp == null) {
            // ȱʡ��װ���ܼ����Ǽ�
            if (quality == QUALITY_WHITE) {
                canJudgeStar = false;
            } else {
                canJudgeStar = true;
            }
        } else {
            canJudgeStar = "1".equals(tmp);
        }
        tmp = elem.getAttributeValue("judgepotential");
        if (tmp == null) {
            // ȱʡ��װ����Ʒ���ܼ�������
            if (quality == QUALITY_WHITE) {
                canJudgePotential = false;
            } else {
                canJudgePotential = true;
            }
        } else {
            canJudgePotential = "1".equals(tmp);
        }
        tmp = elem.getAttributeValue("mark");
        if (tmp == null){
            markCharCount = 0;
        }else{
            markCharCount = Integer.parseInt(tmp);
        }
        // temp
//        if (getType(place) == EQUI_TYPE_JEWELRY || getType(place) == EQUI_TYPE_HORSE) {
//            if (quality == QUALITY_WHITE) {
//                canJudgeStar = false;
//            } else {
//                canJudgeStar = true;
//            }
//            canJudgePotential = false;
//        }

        // �趨ȱʡͼ��
        try {
            iconIndex = Integer.parseInt(elem.getAttributeValue("iconIndex"));
        } catch (Exception e1) {
            resetIcon();
        }
       
        timeType = Integer.parseInt(elem.getAttributeValue("timeType"));
        if (timeType != TIME_TYPE_UNDEFINE) {
            time = Integer.parseInt(elem.getAttributeValue("time"));
        }
        
        extraQuality = Float.parseFloat(elem.getAttributeValue("extraquality"));
        try {
            buffID = Integer.parseInt(elem.getAttributeValue("buffid"));
            buffLevel = Integer.parseInt(elem.getAttributeValue("bufflevel"));
        } catch (Exception e) {
        }
        
        // ���ָ����ǰ׺����ʹ��ǰ׺�����������ԣ��������뱣��ʱ�ֹ��༭������
        if (prefix.id != -1) {
            generateAttributes();
        } else {
            List children = elem.getChildren("attribute");
            for (Object child : children) {
                Element childElem = (Element)child;
                
                String id = childElem.getAttributeValue("id");
                float value = Float.parseFloat(childElem.getAttributeValue("value"));
                int index = owner.config.attrCalc.findIndexOfAttribute(id);
                if (index >= 0) {
                    appendAttributes[index] = value;
                }
            }
            DataCalc.adjustAttributes(this);
            
            // ʹ����������Ը�����ʱǰ׺����
            prefix.updatePriors(this, appendAttributes);
        }
    }

    public Element save() {
        Element ret = new Element("equipment");
        
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        ret.addAttribute("desc",description);
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }
        
        ret.addAttribute("level",String.valueOf(level));
        ret.addAttribute("durability",String.valueOf(durability));
        ret.addAttribute("job",String.valueOf(job));
        ret.addAttribute("equipmentType",String.valueOf(equipmentType));
        ret.addAttribute("equipmentOwner",String.valueOf(equipmentOwner));
        ret.addAttribute("weaponType",String.valueOf(weaponType));
        ret.addAttribute("astrictInteligence",String.valueOf(astrictInteligence));
        ret.addAttribute("astrictStamina",String.valueOf(astrictStamina));
        ret.addAttribute("astrictAgility",String.valueOf(astrictAgility));
        ret.addAttribute("astrictPower",String.valueOf(astrictPower));
        ret.addAttribute("playerLevel",String.valueOf(playerLevel));
        
        ret.addAttribute("place",String.valueOf(place));
        ret.addAttribute("bind",String.valueOf(bind));
        ret.addAttribute("quality",String.valueOf(quality));
        ret.addAttribute("iconIndex", String.valueOf(iconIndex));
        
        ret.addAttribute("sale", sale ? "true" : "false");
        ret.addAttribute("price",String.valueOf(price));
        
        ret.addAttribute("holecount",String.valueOf(holeCount));
        ret.addAttribute("maxholecount",String.valueOf(maxHoleCount));
        
        ret.addAttribute("judgestar",canJudgeStar ? "1" : "0");
        ret.addAttribute("judgepotential",canJudgePotential ? "1" : "0");
        ret.addAttribute("mark",String.valueOf(markCharCount));
        ret.addAttribute("timeType",String.valueOf(timeType));
        if(timeType > ATTRIBUTE_NONE){
            ret.addAttribute("time",String.valueOf(time));
        }
        
        ret.addAttribute("prefix", String.valueOf(prefix.id));
        ret.addAttribute("showrandom", showRandom ? "true" : "false");
        ret.addAttribute("extraquality", String.valueOf(extraQuality));
        ret.addAttribute("buffid", String.valueOf(buffID));
        ret.addAttribute("bufflevel", String.valueOf(buffLevel));
        
        // ���ָ����ǰ׺������Ҫ�������ԣ����򱣴�����
        if (prefix.id == -1) {
            for (int i = 0; i < appendAttributes.length; i++) {
                if (appendAttributes[i] <= 0.0f) {
                    continue;
                }
                Element childElem = new Element("attribute");
                childElem.addAttribute("id", owner.config.attrCalc.ATTRIBUTES[i].id);
                childElem.addAttribute("value", String.valueOf(appendAttributes[i]));
                ret.addContent(childElem);
            }
        }
        
        return ret;
    }

    public void update(DataObject obj) {
        Equipment equi = (Equipment)obj;
        id = equi.id;
        title = equi.title;
        setCategoryName(equi.getCategoryName());
        
        description = equi.description;
        level = equi.level;
        durability = equi.durability;
        job = equi.job;
        equipmentType = equi.equipmentType;
        equipmentOwner = equi.equipmentOwner;
        weaponType = equi.weaponType;
        astrictInteligence = equi.astrictInteligence;
        astrictStamina = equi.astrictStamina;
        astrictAgility = equi.astrictAgility;
        astrictPower = equi.astrictPower;
        playerLevel = equi.playerLevel;
        place = equi.place;
        iconIndex = equi.iconIndex;
        bind = equi.bind;
        quality = equi.quality;
        sale = equi.sale;
        price = equi.price;
        timeType = equi.timeType;
        time = equi.time;
        holeCount = equi.holeCount;
        maxHoleCount = equi.maxHoleCount;
        canJudgeStar = equi.canJudgeStar;
        canJudgePotential = equi.canJudgePotential;
        markCharCount = equi.markCharCount;
        if (equi.prefix.id != -1) {
            prefix = equi.prefix;
        } else if (prefix.id == -1) {
            prefix.update(equi.prefix);
        } else {
            prefix = new EquipmentPrefix(owner);
            prefix.update(equi.prefix);
        }
        showRandom = equi.showRandom;
        extraQuality = equi.extraQuality;
        buffID = equi.buffID;
        buffLevel = equi.buffLevel;
        System.arraycopy(equi.appendAttributes, 0, appendAttributes, 0, appendAttributes.length);
        if (equi.extendAttributes == null) {
            extendAttributes = null;
        } else {
            extendAttributes = new float[equi.extendAttributes.length];
            System.arraycopy(equi.extendAttributes, 0, extendAttributes, 0, extendAttributes.length);
        }
        
        if (owner != equi.owner) {
            if (prefix.owner != owner) {
                prefix = (EquipmentPrefix)owner.findObject(EquipmentPrefix.class, prefix.id);
            }
        }
    }
    
    public void resetIcon() {
        if (equipmentType == EQUI_TYPE_WEAPON) {
            iconIndex = owner.config.WEAPON_ICON[this.weaponType];
        } else {
            iconIndex = owner.config.EQU_PLACE_ICON[this.place];
        }
    }
    
    /**
     * ȡ��ĳ�����Ե�ֵ��
     * @param property ����ID��ȡֵ��AttributeCalculator�ĳ����������м����������
     *    ������������������ʹ�ñ�׼ֵ���ϸ��ӹ�����������������������������Ƿ���0
     *    ������������������Ҫ���ϱ�׼ֵ
     *    ���ߣ�����ʹ�ñ�׼ֵ���ϸ��ӻ��׼������
     * @return
     */
    public int getAttribute(int attrID) {
        return Math.round(getAttributeImpl(attrID));
    }

    public float getAttributeImpl(int attrID) {
        if (attrID < 100) {
            return appendAttributes[attrID];
        } else {
            return extendAttributes[attrID - 100];
        }
    }
    
    public boolean equals(Object obj){
        if(obj instanceof Equipment){
            return ((Equipment)obj).id == id;
        }
        return false;
    }
    
    /**
     * ���ص�ǰװ����������
     * @return
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] ret = new IPropertyDescriptor[owner.config.attrCalc.ATTRIBUTES.length + 7];
        ret[0] = new PropertyDescriptor(PROPNAME_BASICRATE, "����Ʒ��ϵ��");
        ret[1] = new TextPropertyDescriptor(PROPNAME_EXTRARATE, "����Ʒ��ϵ��");
        ret[2] = new PropertyDescriptor(PROPNAME_EQUVALUE, "װ����ֵ");
        ret[3] = new PropertyDescriptor(PROPNAME_EQUSHOWNVALUE, "��ʾ��ֵ");
        int c = owner.config.attrCalc.ATTRIBUTES.length;
        for (int i = 0; i < c; i++) {
            EquipmentAttribute attr = owner.config.attrCalc.ATTRIBUTES[i];
            ret[i + 4] = new TextPropertyDescriptor(attr.id, attr.name);
        }
        ret[c + 4] = new BuffPropertyDescriptor(PROPNAME_BUFFID, "��Ч����", BuffPropertyDescriptor.UsePartBuff);
        ret[c + 5] = new TextPropertyDescriptor(PROPNAME_BUFFLEVEL, "��Ч����");
        ret[c + 6] = new PropertyDescriptor(PROPNAME_BUFFVALUE, "��Ч��ֵ");
        return ret;
    }
    
    /**
     * �����޸ĺ����¼���۸���;öȡ�
     */
    public void recalcPriceAndDurability() {
        price = this.DataCalc.getPrice(this);
        durability = this.DataCalc.getDurability(this);
    }
    
    /**
     * ����װ����λ�õ�װ������
     * @param place
     * @return
     */
    public static int getType(int place){
        String name = ProjectData.getActiveProject().config.PLACE_NAMES[place];
        return ProjectData.getActiveProject().config.bodyPartMap.get(name).typeId;
    }
    
    /**
     * ����װ����λ�õ�װ������
     * @return
     */
    public int getType() {
        String name = owner.config.PLACE_NAMES[place];
        return owner.config.bodyPartMap.get(name).typeId;
    }
    
    public String getTitle() {
        return title + "(" + level + "/" + playerLevel + ")";
    }
    
    /**
     * ����װ����ֵ��
     * @return
     */
    public float getValue() {
        float ret = DataCalc.getValue(this);
        if (ret < 0.0f) {
            return 0.0f;
        } else {
            return ret;
        }
    }
    
    /**
     * ����װ����ֵ��
     * @return
     */
    public float getShownValue() {
        float ret = DataCalc.getShownValue(this);
        if (ret < 0.0f) {
            return 0.0f;
        } else {
            return ret;
        }
    }    
    
    /**
     * idx base 0
     * @param point
     * @return level , base 0
     */
    public static int getImageLevel(int point, int place){
        int ret = 0;
        int[] steps = ProjectData.getActiveProject().config.PLACE_IMAGE_STEP[place];
        if(steps == null){
            //throw new NullPointerException("װ������û������.");
            return 0; //û�з���������Ҫ�����װ��
        }
        for(int i=0; i<steps.length; i++){
            if(point<steps[i]){
                ret = i;
                break;
            }
        }
        
        if(ret == 0) {
            ret = 12; //������󼶱�ʹ����ߵȼ�װ��, ��ʱ�ĳ�10����11~15���Ժ���
        }
        return ret;
    }
    
    /**
     * ����װ��������Ч�ļ�ֵ��
     */
    public float getBuffValue() {
        BuffConfig buff = (BuffConfig)owner.findObject(BuffConfig.class, buffID);
        if (buff == null) {
            return 0.0f;
        }
        if (buffLevel < 1 || buffLevel > buff.maxLevel) {
            return 0.0f;
        }
        return buff.value[buffLevel - 1];
    }
    
    /**
     * ����һ��װ���ĸ������Ա��ڵ��ñ�����ǰ������������װ�����������ԣ���Ʒ�ȼ���Ʒ�ʡ���λ������Ʒ��ϵ����ǰ׺��
     */
    public void generateAttributes() {
        float value = DataCalc.getValue(this);
        value -= getBuffValue();         // �۳���Ч��ֵ
        float[] attrs = prefix.generateAttributes(this, value);
        System.arraycopy(attrs, 0, appendAttributes, 0, attrs.length);
        DataCalc.adjustAttributes(this);
    }

    /**
     * �������������Խ��й��ʻ������������Ҫ���ʻ����ַ���������ȡ������context�в��ҷ�������
     * @param context
     * @return �����ĳ�����Ա��滻������true�����򷵻�false��
     */
    public boolean i18n(I18NContext context) {
        String tmp = context.input(title, "Equipment");
        if (tmp != null) {
            title = tmp;
            return true;
        } else {
            return false;
        }
    }
}
