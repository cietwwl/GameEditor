package com.pip.game.data.equipment;

import org.jdom.Element;

import java.util.*;

import com.pip.game.data.DataObject;
import com.pip.game.data.Faction;
import com.pip.game.data.ProjectData;
import com.pip.game.data.Title;
import com.pip.game.data.i18n.I18NContext;
import com.pip.game.data.skill.BuffConfig;

/**
 * 套装定义。
 * @author lighthu
 */
public class SuiteConfig extends DataObject {
    /**
     * 所属项目。
     */
    public ProjectData owner;
    /**
     * 包含装备。
     */
    public List<Equipment> equipments = new ArrayList<Equipment>();
    
    
    /**
     * 前缀名，西游项目用于套装部位名称的显示
     */
    public String prefixName = "";

    /**
     * 套装效果定义。
     * @author lighthu
     */
    public static class SuiteEffect {
        public int count;
        public int buffID;
        public int buffLevel;
        
        public SuiteEffect dup() {
            SuiteEffect ret = new SuiteEffect();
            ret.count = count;
            ret.buffID = buffID;
            ret.buffLevel = buffLevel;
            return ret;
        }
    }
    
    /**
     * 套装效果列表。
     */
    public List<SuiteEffect> effects = new ArrayList<SuiteEffect>();
    
    public SuiteConfig(ProjectData owner) {
        this.owner = owner;
    }

    public int getID() {
        return id;
    }
    
    public String toString() {
        return id + ": " + title;
    }

    public boolean equals(Object o) {
        return this == o;
    }
    
    public void update(DataObject obj) {
        SuiteConfig oo = (SuiteConfig)obj;
        id = oo.id;
        title = oo.title;
        description = oo.description;
        prefixName = oo.prefixName;
        setCategoryName(oo.getCategoryName());
        
        equipments.clear();
        equipments.addAll(oo.equipments);
        effects.clear();
        for (SuiteEffect eff : oo.effects) {
            effects.add(eff.dup());
        }
        
        if (owner != oo.owner) {
            List<Equipment> newEqus = new ArrayList<Equipment>();
            for (Equipment equ : equipments) {
                newEqus.add((Equipment)owner.findEquipment(equ.id));
            }
            equipments = newEqus;
        }
    }
    
    public DataObject duplicate() {
        SuiteConfig ret = new SuiteConfig(owner);
        ret.update(this);
        return ret;
    }

    @Override
    public boolean changed(DataObject obj) {
        return changed(this, obj);
    }
    
    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
        description = elem.getAttributeValue("description");
        
        prefixName = elem.getAttributeValue("prefixName");
        if(prefixName == null){
            prefixName = "";
        }
        
        setCategoryName(elem.getAttributeValue("category"));
        if (getWholeCategoryName() == null) {
            setCategoryName("");
        }
        
        equipments.clear();
        List list = elem.getChildren("equipment");
        for (int i = 0; i < list.size(); i++) {
            int eid = Integer.parseInt(((Element)list.get(i)).getAttributeValue("id"));
            Equipment equ = owner.findEquipment(eid);
            if (equ != null) {
                equipments.add(equ);
            }
        }

        effects.clear();
        list = elem.getChildren("effect");
        for (int i = 0; i < list.size(); i++) {
            SuiteEffect eff = new SuiteEffect();
            Element elem2 = (Element)list.get(i);
            eff.count = Integer.parseInt(elem2.getAttributeValue("count"));
            eff.buffID = Integer.parseInt(elem2.getAttributeValue("buffid"));
            eff.buffLevel = Integer.parseInt(elem2.getAttributeValue("bufflevel"));
            if (owner.findObject(BuffConfig.class, eff.buffID) != null) {
                effects.add(eff);
            }
        }
    }
    
    public Element save() {
        Element ret = new Element("suite");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        ret.addAttribute("description", description);
        ret.addAttribute("prefixName", prefixName);
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }
        
        for (Equipment equ : equipments) {
            Element elem = new Element("equipment");
            elem.addAttribute("id", String.valueOf(equ.id));
            ret.addContent(elem);
        }
        for (SuiteEffect eff : effects) {
            Element elem = new Element("effect");
            elem.addAttribute("count", String.valueOf(eff.count));
            elem.addAttribute("buffid", String.valueOf(eff.buffID));
            elem.addAttribute("bufflevel", String.valueOf(eff.buffLevel));
            ret.addContent(elem);
        }
        return ret;
    }
    
    public boolean depends(DataObject obj) {
        if (obj instanceof BuffConfig) {
            int buffID = ((BuffConfig)obj).id;
            for (SuiteEffect eff : effects) {
                if (eff.buffID == buffID) {
                    return true;
                }
            }
        } else if (obj instanceof Equipment) {
            return equipments.contains(obj);
        }
        return false;
    }

    /**
     * 对这个对象的属性进行国际化处理，如果有需要国际化的字符串，则提取出来到context中查找翻译结果。
     * @param context
     * @return 如果有某个属性被替换，返回true，否则返回false。
     */
    public boolean i18n(I18NContext context) {
        boolean changed = false;
        String tmp = context.input(title, "SuiteConfig");
        if (tmp != null) {
            title = tmp;
            changed = true;
        }
        tmp = context.input(prefixName, "SuiteConfig");
        if (tmp != null) {
            prefixName = tmp;
            changed = true;
        }
        return changed;
    }
}

