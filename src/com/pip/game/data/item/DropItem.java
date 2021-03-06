package com.pip.game.data.item;

import org.jdom.Element;

import com.pip.game.data.Currency;
import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.i18n.I18NContext;

/**
 * 
 * @author Joy Yan
 *
 */
public class DropItem extends DataObject {
    
    /********************* 常量定义 *********************/
    
    /** 掉落物品 */
    public static final int DROP_TYPE_ITEM = 0;
    /** 掉落装备 */
    public static final int DROP_TYPE_EQUI = 1;
    /** 掉落一个掉落组 */
    public static final int DROP_TYPE_DROPGROUP = 2;
    /** 掉落金钱 */
    public static final int DROP_TYPE_MONEY = 3;
    /** 掉落经验 */
    public static final int DROP_TYPE_EXP = 4;
    /** 掉落魔钻*/
    public static final int DROP_TYPE_IMONEY = 5;
    
    
    /********************* 常量定义 *********************/
    
    /**
     * 掉落类型
     * 
     * 掉落物品：0
     * 掉落装备：1
     * 掉落一个掉落组：2
     * 掉落金钱：3
     * 掉落经验：4
     * 
     * >100 掉落扩展货币，货币类型定义见currency.xml
     */
    public int dropType;
    /** 掉落的物品或者组的ID */
    public int dropID;
    /** 该物品掉落的权重 */
    public int dropWeight;
    /** 该物品掉落的最大数量 */
    public int quantityMax;
    /** 该物品掉落的最小数量 */
    public int quantityMin;
    /** 掉落的数据对象，临时对象 */
    public DataObject dropObj;
    /** 临时计算的掉落率 */
    public double dropRate;
    
    public boolean depends(DataObject obj) {
        return false;
    }

    public DataObject duplicate() {
        DropItem copy = new DropItem();
        copy.update(this);
        return copy;
    }

    @Override
    public boolean changed(DataObject obj) {
        return changed(this, obj);
    }

    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        dropID = Integer.parseInt(elem.getAttributeValue("dropID"));
        dropType = Integer.parseInt(elem.getAttributeValue("dropType"));
        dropWeight = Integer.parseInt(elem.getAttributeValue("dropWeight"));
        quantityMax = Integer.parseInt(elem.getAttributeValue("quantityMax"));
        quantityMin = Integer.parseInt(elem.getAttributeValue("quantityMin"));
    }

    public Element save() {
        try {
            DataObject depends = null;
            ProjectData projData = ProjectData.getActiveProject();
            switch (dropType) {
            case DROP_TYPE_ITEM:
                depends = projData.findItem(dropID);
                break;
            case DROP_TYPE_EQUI:
                depends = projData.findEquipment(dropID);
                break;
            case DROP_TYPE_DROPGROUP:
                depends = projData.findObject(DropGroup.class, dropID);
                break;
            default:
                depends = this;
                break;
            }
            
            /* 无效的物品放弃存储 */
            if(depends == null) {
                return null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        
        Element ret = new Element("DropItem");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("dropID", String.valueOf(dropID));
        ret.addAttribute("dropType", String.valueOf(dropType));
        ret.addAttribute("dropWeight", String.valueOf(dropWeight));
        ret.addAttribute("quantityMax", String.valueOf(quantityMax));
        ret.addAttribute("quantityMin", String.valueOf(quantityMin));
        
        return ret;
    }

    public void update(DataObject obj) {
        DropItem copy = (DropItem)obj;
        id = copy.id;
        dropID = copy.dropID;
        dropType = copy.dropType;
        dropWeight = copy.dropWeight;
        quantityMax = copy.quantityMax;
        quantityMin = copy.quantityMin;
        dropRate = copy.dropRate;
    }
    
    public String toString() {
        switch (dropType) {
        case DROP_TYPE_ITEM:
            if (dropObj == null) { 
                dropObj = ProjectData.getActiveProject().findItem(dropID);
            }
            return String.valueOf(dropObj) + "(" + quantityMin + "~" + quantityMax + ")";
        case DROP_TYPE_EQUI:
            if (dropObj == null) { 
                dropObj = ProjectData.getActiveProject().findEquipment(dropID);
            }
            return String.valueOf(dropObj) + "(" + quantityMin + "~" + quantityMax + ")";
        case DROP_TYPE_DROPGROUP:
            if (dropObj == null) { 
                dropObj = ProjectData.getActiveProject().findObject(DropGroup.class, dropID);
            }
            return String.valueOf(dropObj) + "(" + quantityMin + "~" + quantityMax + ")";
        case DROP_TYPE_MONEY:
            return "金钱" + "(" + quantityMin + "~" + quantityMax + ")";
        case DROP_TYPE_EXP:
            return "经验" + "(" + quantityMin + "~" + quantityMax + ")";
        default:
            // 扩展货币掉落
            Currency c = (Currency)ProjectData.getActiveProject().findDictObject(Currency.class, dropType);
            return c.title + "(" + quantityMin + "~" + quantityMax + ")";
        }
    }

    /**
     * 对这个对象的属性进行国际化处理，如果有需要国际化的字符串，则提取出来到context中查找翻译结果。
     * @param context
     * @return 如果有某个属性被替换，返回true，否则返回false。
     */
    public boolean i18n(I18NContext context) {
        return false;
    }
}
