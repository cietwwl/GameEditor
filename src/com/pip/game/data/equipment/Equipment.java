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
 * 装备数据属性
 * @author Joy
 */
public class Equipment extends Item {
    /**************************   常量   ***************************/
    
    //和配置文件中的一致(items/equipmentDef.xml types:entry:idx)
    /** 装备类型------武器 */
    public static final int EQUI_TYPE_WEAPON = 0;
    /** 装备类型------防具 */
    public static final int EQUI_TYPE_PROTECTOR = 1;
    /** 装备类型------饰品 */
    public static final int EQUI_TYPE_JEWELRY = 2;
    /** 装备类型------坐骑装备 */
    public static final int EQUI_TYPE_HORSE = 3;
    
    /** 属性：基本品质系数 */
    public static final String PROPNAME_BASICRATE = "basicrate";
    /** 属性：附加品质系数 */
    public static final String PROPNAME_EXTRARATE = "extrarate";
    /** 属性：装备价值 */
    public static final String PROPNAME_EQUVALUE = "equvalue";
    /** 属性：装备显示价值 */
    public static final String PROPNAME_EQUSHOWNVALUE = "equshownvalue";
    
    /**************************   常量   ***************************/
    
    /** 装备耐久度 */
    public int durability;
    
    /** 职业 */
    public int job = -1;
    
    /** 装备类型 */
    public int equipmentType;
    
    /** 装备所有者 */
    public int equipmentOwner;
    
    /** 武器类型 **/
    public int weaponType;
    
    /** 智力限制下限 */
    public int astrictInteligence;
    
    /** 耐力限制下限 */
    public int astrictStamina;
    
    /** 敏捷限制下限 */
    public int astrictAgility;
    
    /** 力量限制下限 */
    public int astrictPower;
    
    /** 前缀 */
    public EquipmentPrefix prefix;
    /** 附加品质系数 */
    public float extraQuality;
    /** 是否显示为随机前缀（仅用于显示配方产品）*/
    public boolean showRandom;
    /** 初始孔数 */
    public int holeCount = 1;
    /** 最大孔数 */
    public int maxHoleCount = 5;
    /** 是否允许鉴定星级 */
    public boolean canJudgeStar;
    /** 是否允许鉴定资质 */
    public boolean canJudgePotential;
    
    /**
     * 允许刻字的数量
     */
    public int markCharCount;
    
    /** 附加属性 */
    public float[] appendAttributes;
    /** 附加衍生属性（不可修改）*/
    public float[] extendAttributes;
    
    /** 装备部位 */
    public int place;
    
    /** 附加BUFF ID，-1表示没有 */
    public int buffID = -1;
    /** 附加BUFF级别 */
    public int buffLevel;

    /** 属性：BUFF ID */
    public static final String PROPNAME_BUFFID = "buffid";
    /** 属性：BUFF级别 */
    public static final String PROPNAME_BUFFLEVEL = "bufflevel";
    /** 属性：BUFF价值 */
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
            // 缺省白装不能鉴定星级
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
            // 缺省白装和饰品不能鉴定资质
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

        // 设定缺省图标
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
        
        // 如果指定了前缀，则使用前缀重新生成属性，否则载入保存时手工编辑的属性
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
            
            // 使用载入的属性更新临时前缀配置
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
        
        // 如果指定了前缀，则不需要保存属性，否则保存属性
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
     * 取得某项属性的值。
     * @param property 参数ID，取值见AttributeCalculator的常量，其中有几条特殊规则：
     *    武器，物理攻击上下限使用标准值加上附加攻击力计算得来，附加物理攻击力总是返回0
     *    武器，法术攻击力需要加上标准值
     *    防具，护甲使用标准值加上附加护甲计算得来
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
     * 返回当前装备类型属性
     * @return
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] ret = new IPropertyDescriptor[owner.config.attrCalc.ATTRIBUTES.length + 7];
        ret[0] = new PropertyDescriptor(PROPNAME_BASICRATE, "基本品质系数");
        ret[1] = new TextPropertyDescriptor(PROPNAME_EXTRARATE, "附加品质系数");
        ret[2] = new PropertyDescriptor(PROPNAME_EQUVALUE, "装备价值");
        ret[3] = new PropertyDescriptor(PROPNAME_EQUSHOWNVALUE, "显示价值");
        int c = owner.config.attrCalc.ATTRIBUTES.length;
        for (int i = 0; i < c; i++) {
            EquipmentAttribute attr = owner.config.attrCalc.ATTRIBUTES[i];
            ret[i + 4] = new TextPropertyDescriptor(attr.id, attr.name);
        }
        ret[c + 4] = new BuffPropertyDescriptor(PROPNAME_BUFFID, "特效类型", BuffPropertyDescriptor.UsePartBuff);
        ret[c + 5] = new TextPropertyDescriptor(PROPNAME_BUFFLEVEL, "特效级别");
        ret[c + 6] = new PropertyDescriptor(PROPNAME_BUFFVALUE, "特效价值");
        return ret;
    }
    
    /**
     * 属性修改后，重新计算价格和耐久度。
     */
    public void recalcPriceAndDurability() {
        price = this.DataCalc.getPrice(this);
        durability = this.DataCalc.getDurability(this);
    }
    
    /**
     * 根据装备部位得到装备类型
     * @param place
     * @return
     */
    public static int getType(int place){
        String name = ProjectData.getActiveProject().config.PLACE_NAMES[place];
        return ProjectData.getActiveProject().config.bodyPartMap.get(name).typeId;
    }
    
    /**
     * 根据装备部位得到装备类型
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
     * 计算装备价值。
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
     * 计算装备价值。
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
            //throw new NullPointerException("装备档次没有配置.");
            return 0; //没有分数，不需要穿这个装备
        }
        for(int i=0; i<steps.length; i++){
            if(point<steps[i]){
                ret = i;
                break;
            }
        }
        
        if(ret == 0) {
            ret = 12; //超过最大级别，使用最高等级装备, 临时改成10级，11~15级以后用
        }
        return ret;
    }
    
    /**
     * 计算装备附加特效的价值。
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
     * 生成一件装备的附加属性表。在调用本方法前，必须先设置装备的以下属性：物品等级、品质、部位、附加品质系数、前缀。
     */
    public void generateAttributes() {
        float value = DataCalc.getValue(this);
        value -= getBuffValue();         // 扣除特效价值
        float[] attrs = prefix.generateAttributes(this, value);
        System.arraycopy(attrs, 0, appendAttributes, 0, attrs.length);
        DataCalc.adjustAttributes(this);
    }

    /**
     * 对这个对象的属性进行国际化处理，如果有需要国际化的字符串，则提取出来到context中查找翻译结果。
     * @param context
     * @return 如果有某个属性被替换，返回true，否则返回false。
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
