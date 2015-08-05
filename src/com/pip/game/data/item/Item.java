package com.pip.game.data.item;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.extprop.ExtPropEntries;
import com.pip.game.data.i18n.I18NContext;
import com.pip.game.data.i18n.I18NUtils;
import com.pip.game.data.quest.pqe.ExpressionList;
import com.pip.util.Utils;

/**
 * 物品的描述信息
 * 
 * @author Joy Yan
 * 
 */
public class Item extends DataObject {
    /**
     * 物品属性无效
     */
    public static final int ATTRIBUTE_NONE = -1;
    /**
     * 属性取值为否
     */
    public static final int ATTRIBUTE_VALUE_NO = 0;
    /**
     * 属性取值为是
     */
    public static final int ATTRIBUTE_VALUE_YES = 1;
    
    /**
     * 物品不可使用 
     */
    public static final int AVAILABLE_NO = 0;
    /**
     * 物品只有战斗中可使用 
     */
    public static final int AVAILABLE_BATTLE = 1;
    /**
     * 物品只有非战斗中可使用 
     */
    public static final int AVAILABLE_UN_BATTLE = 2;    
    /**
     * 物品任何时候都可使用 
     */
    public static final int AVAILABLE_EVER = 3;//changed by JS

    /**
     * 自动使用 
     * 默认自动：得到物品后，将给出系统默认是否使用的选择提示，玩家可以选择使用或则不使用;
     */
    public static final int AUTOUSE_DEFAULT = 0;
    /**
     * 自动使用
     * 定义自动：可针对此物品单独定义提示是否使用的描述和选项文字；
     */
    public static final int AUTOUSE_DEFINE = 1;
    /**
     * 自动使用
     * 后台自动：不做任何提示，得到物品后系统自动将其使用
     */
    public static final int AUTOUSE_BACKGROUND = 2;
    
    /**
     * 使用范围 
     * 0. 无目标 
     */
    public static final int AREA_UN_DEFINE = 0;
    /**
     * 使用范围 
     * 1. 自己 
     */
    public static final int AREA_SELF = 1;
    /**
     * 使用范围 
     * 2. 己方队友
     */
    public static final int AREA_TEAM = 2;
    /**
     * 使用范围 
     * 3. 敌方
     */
    public static final int AREA_ENEMY = 3;
    /**
     * 使用范围 
     * 4. 全部
     */
    public static final int AREA_ALL = 4;
    
    
    /**
     * 时间类型
     *  0:没有时效限制
     */
    public static final int TIME_TYPE_UNDEFINE = 0;
    /**
     * 时间类型
     *  1:绝对时效
     */
    public static final int TIME_TYPE_ABSOLUTELY = 1;
    /**
     * 时间类型
     *  2:相对时效
     */
    public static final int TIME_TYPE_RELATIVELY = 2;
    
    /**
     * 物品品质
     * 0.  普通（白）
     */
    public static final int QUALITY_WHITE = 0;
    /**
     * 物品品质
     * 1.  优良（绿）
     */
    public static final int QUALITY_GREEN = 1;
    /**
     * 物品品质
     * 2.  精良（蓝）
     */
    public static final int QUALITY_BLUE = 2;
    /**
     * 物品品质
     * 3.  史诗（紫）
     */
    public static final int QUALITY_PURPLE = 3;
    /**
     * 物品品质
     * 4.  传说（橙）
     */
    public static final int QUALITY_ORANGE = 4;
    /**
     * 物品品质
     * 5.  奖励（黄）
     */
    public static final int QUALITY_YELLOW = 5;
    
    /**
     * 是否绑定
     * 0.  不绑定
     */
    public static final int BIND_NO = 0;
    /**
     * 是否绑定
     * 1.  装备绑定
     */
    public static final int BIND_EQUIPMENT = 1;
    /**
     * 是否绑定
     * 2.  拾取绑定
     */
    public static final int BIND_PICK_UP = 2;

    /**
     * 所属项目。
     */
    public ProjectData owner;
    /**
     * 物品大分类。
     */
    public int mainType = 0;
    /**
     * 物品类型
     */
    public int type = 0;
    /**
     * 物品堆叠数量，必须大于0
     */
    public int addition = 1;
    /**
     * 是否实例类型，如果是实例的话，堆叠数量必须为1；
     */
    public boolean instance;
    /**
     * 是否是任务物品
     */
    public boolean taskFlag;
    /**
     * 物品是否可使用 
     * 0. 否：不可直接使用的物品，在物品管理中将不显示使用功能项 
     * 1. 战斗中可用 
     * 2. 非战斗中可用 
     * 3. 任何时候可用
     */
    public int available;
    /**
     * 使用限制职业：
     * 0 - 不限制
     * 1-n 按ProjectConfig.PLAYER_CLAZZ顺序的职业ID
     */
    public int useClazz = -1;
    /**
     * 使用确认字符串，空串表示不需要确认。
     */
    public String useConfirm = "";
    
    public String additionalPrompt = "";
    /**
     * 物品品质
     * 0.  普通（白）
     * 1.  优良（绿）
     * 2.  精良（蓝）
     * 3.  史诗（紫）
     * 4.  传说（橙）
     * 5.  奖励（黄）
     */
    public int quality;
    /**
     * 是否消耗：
     */
    public boolean waste = true;
    /**
     * 自动使用 
     * -1. 不可以自动使用 
     *  0. 默认自动：得到物品后，将给出系统默认是否使用的选择提示，玩家可以选择使用或则不使用； 
     *  1. 定义自动：可针对此物品单独定义提示是否使用的描述和选项文字；
     *  2. 后台自动：不做任何提示，得到物品后系统自动将其使用
     */
    public int autoUse;
    /**
     * 物品级别
     */
    public int level = 1;
    /**
     * 使用范围 
     * 0. 无目标 
     * 1. 自己 
     * 2. 己方队友
     * 3. 敌方
     * 4. 全部
     */
    public int area;
    /**
     * 使用该物品的玩家等级下限
     *  -1：没有限制
     *  >0: 实际等级
     */
    public int playerLevel = 1;
    /**
     * 使用该物品的玩家等级上限
     */
    public int playerMaxLevel = 100;
    /**
     * 冷却组
     */
    public int[] coldDownGroup = new int[0];
    /**
     * 冷却时间（毫秒）
     */
    public int coldDownTime;
    /**
     * 回合制冷却组
     */
    public int[] roundColdDownGroup = new int[0];
    /**
     * 回合制冷却回合数。
     */
    public int coldDownRound;
    /**
     * 回合制冷却是否支持跨战斗。
     */
    public boolean coldDownRoundCrossBattle = false;
    /**
     * 时间类型
     *  0:没有时效限制
     *  1:绝对时效
     *  2:相对时效
     */
    public int timeType;
    /**
     * 使用时效；
     * 当实时效型为绝对时效时，存放的是指定日期时间根据Java标准得到的确定秒数；
     * 当实时效型为相对时效时，记录一个时间（单位：秒）；
     */
    public int time;
    /**
     * 施法时间，单位（毫秒）；
     * 物品使用后多长时间后才真正出效果，这段时间通过进度条来显示进度，
     * 中途可以按键取消使用，物品不消失，效果不出现。
     */
    public int schedule;
    /**
     * 价格
     */
    public int price;
    /**
     * 能否被出售
     *  0：否
     *  1：是
     */
    public boolean sale = true;
    /**
     * 是否绑定
     * 0.  不绑定
     * 1.  装备绑定
     * 2.  拾取绑定
     */
    public int bind;
    /**
     * 图标索引
     */
    public int iconIndex;
    /**
     * 使用距离
     */
    public int distance;
    /**
     * 是否允许丢弃
     */
    public boolean canDelete = true;
    /**
     * 是否允许移至仓库
     */
    public boolean movable = true;
    /**
     * 使用次数
     */
    public int useCount;
    /**
     * 最多拥有数量,0不限制
     */
    public int maxOwnCount;
    /**
     * 附加条件
     */
    public ExpressionList additionalCondition = ExpressionList.fromString("");
    /**
     * 物品扩展属性。
     */
    public ExtPropEntries extPropEntries = new ExtPropEntries();
    /**
     * 使用效果列表
     */
    public List<ItemEffect> effects = new ArrayList<ItemEffect>();
        
    /**
     * 备注。
     */
    public String remark = "";
    
    public Item(ProjectData owner) {
        this.owner = owner;
        DataCalc = owner.config.getProjectCalc(getClass());
    }

    public boolean depends(DataObject obj) {
        return false;
    }

    public DataObject duplicate() {
        Item itemCopy = new Item(owner);
        itemCopy.update(this);
        return itemCopy;
    }

    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
        description = elem.getAttributeValue("desc");
        remark = elem.getAttributeValue("remark");
        if(remark == null) {
            remark = "";
        }
        setCategoryName(elem.getAttributeValue("category"));
        if (getWholeCategoryName() == null) {
            setCategoryName("");
        }
        
        type = Byte.parseByte(elem.getAttributeValue("type"));
        mainType = owner.config.findItemType(type).category;
        addition = Integer.parseInt(elem.getAttributeValue("addition"));
        taskFlag = Boolean.parseBoolean(elem.getAttributeValue("taskflag"));
        bind = Integer.parseInt(elem.getAttributeValue("bind"));
        quality = Integer.parseInt(elem.getAttributeValue("quality"));

        try {
            iconIndex = Integer.parseInt(elem.getAttributeValue("iconIndex"));
        } catch (NumberFormatException e1) {
            iconIndex = 0;
        }
        sale = Boolean.parseBoolean(elem.getAttributeValue("sale"));
        if (sale) {            
            price = Integer.parseInt(elem.getAttributeValue("price"));
        }
        
        instance = Boolean.parseBoolean(elem.getAttributeValue("instance"));
        
        String str = elem.getAttributeValue("additionalCondition");
        if (str != null) {
            additionalCondition = ExpressionList.fromString(str);
        }
        //additionalCondition = elem.getAttributeValue("additionalCondition");

        try {
            level = Integer.parseInt(elem.getAttributeValue("level"));
        } catch (Exception e) {
            level = 1;
        }
        try {
            timeType = Integer.parseInt(elem.getAttributeValue("timetype"));
        } catch (Exception e) {
            timeType = TIME_TYPE_UNDEFINE;
        }
        if (timeType != TIME_TYPE_UNDEFINE) {
            try {
                time = Integer.parseInt(elem.getAttributeValue("time"));
            } catch (Exception e) {
                time = 0;
            }
        }
        try{
            maxOwnCount = Integer.parseInt(elem.getAttributeValue("maxOwnCount"));
        }catch(Exception e){
            
        }

        available = Integer.parseInt(elem.getAttributeValue("available"));
        if (available != AVAILABLE_NO) {
            try {
                useClazz = Integer.parseInt(elem.getAttributeValue("useclazz"));
            } catch (Exception e) {
                useClazz = -1;
            }
            useConfirm = elem.getAttributeValue("useconfirm");
            if (useConfirm == null) {
                useConfirm = "";
            }
            additionalPrompt = elem.getAttributeValue("additionalPrompt");
            if(additionalPrompt == null){
                additionalPrompt = "";
            }
            try{
                playerMaxLevel = Integer.parseInt(elem.getAttributeValue("playerMaxLevel"));
            }catch(Exception e){
                playerMaxLevel = 100;//缺省等级上限为100级
                
            }
            waste = Boolean.parseBoolean(elem.getAttributeValue("waste"));
            area = Integer.parseInt(elem.getAttributeValue("area"));
            coldDownGroup = Utils.stringToIntArray(elem.getAttributeValue("colddowngroup"), ',');
            coldDownTime = Integer.parseInt(elem.getAttributeValue("coldDownTime"));
            
            // 回合制的CD
            try {
                roundColdDownGroup = Utils.stringToIntArray(elem.getAttributeValue("roundcolddowngroup"), ',');
                coldDownRound = Integer.parseInt(elem.getAttributeValue("colddownround"));
                coldDownRoundCrossBattle = "1".equals(elem.getAttributeValue("colddownroundcrossbattle"));
            } catch (Exception e) {
            }
            
            autoUse = Integer.parseInt(elem.getAttributeValue("autouse"));
            schedule = Integer.parseInt(elem.getAttributeValue("schedule"));
            playerLevel = Integer.parseInt(elem.getAttributeValue("playerlevel"));
            
            try {
                distance = Integer.parseInt(elem.getAttributeValue("distance"));
            } catch (NumberFormatException e1) {
                distance = 15*8; //默认值15码
            }
            
            List children = elem.getChildren("effect");
            for (int i = 0; children != null && i < children.size(); i++) {
                ItemEffect e = new ItemEffect();
                e.load((Element) children.get(i));
                effects.add(e);
            }
        }
        extPropEntries.loadExtData(elem.getChild("extProps"));
        if(elem.getAttributeValue("movable") != null){
            movable = Boolean.parseBoolean(elem.getAttributeValue("movable"));
        }
        if(elem.getAttributeValue("canDelete") != null){
            canDelete = Boolean.parseBoolean(elem.getAttributeValue("canDelete"));
        }
        try{
            useCount = Integer.parseInt(elem.getAttributeValue("useCount"));
        }catch(Exception e){
            useCount = 0;
        }
    }

    public Element save() {
        Element ret = new Element("item");
        
        ret.addAttribute("additionalCondition", additionalCondition.toString());
        
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        ret.addAttribute("desc",description);
        ret.addAttribute("remark",remark);
        
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }
        
        ret.addAttribute("type",String.valueOf(type));
        ret.addAttribute("addition",String.valueOf(addition));
        ret.addAttribute("taskflag",String.valueOf(taskFlag));
        ret.addAttribute("iconIndex",String.valueOf(iconIndex));
        
        ret.addAttribute("bind",String.valueOf(bind));
        ret.addAttribute("quality",String.valueOf(quality));
        ret.addAttribute("instance",String.valueOf(instance));
        
        ret.addAttribute("sale",String.valueOf(sale));
        if (sale) {
            ret.addAttribute("price",String.valueOf(price));
        }
        ret.addAttribute("level",String.valueOf(level));
        ret.addAttribute("timetype",String.valueOf(timeType));
        if (timeType > ATTRIBUTE_NONE) {
            ret.addAttribute("time",String.valueOf(time));
        }
        
        ret.addAttribute("available",String.valueOf(available));
        if(available != AVAILABLE_NO){
            ret.addAttribute("useclazz", String.valueOf(useClazz));
            ret.addAttribute("useconfirm", useConfirm);
            ret.addAttribute("additionalPrompt",additionalPrompt);
            ret.addAttribute("playerMaxLevel",String.valueOf(playerMaxLevel));
            ret.addAttribute("waste",String.valueOf(waste));
            ret.addAttribute("area",String.valueOf(area));
            
            ret.addAttribute("colddowngroup",Utils.intArrayToString(coldDownGroup, ','));
            ret.addAttribute("coldDownTime",String.valueOf(coldDownTime));

            // 回合制的CD
            ret.addAttribute("roundcolddowngroup", Utils.intArrayToString(roundColdDownGroup, ','));
            ret.addAttribute("colddownround", String.valueOf(coldDownRound));
            ret.addAttribute("colddownroundcrossbattle", coldDownRoundCrossBattle ? "1" : "0");
            
            ret.addAttribute("autouse",String.valueOf(autoUse));
            ret.addAttribute("schedule",String.valueOf(schedule));
            ret.addAttribute("playerlevel",String.valueOf(playerLevel));
            
            ret.addAttribute("distance",String.valueOf(distance));
            
            for (int i = 0; i < effects.size(); i++) {
                Element child = effects.get(i).save();
                if(child != null){
                    ret.getMixedContent().add(child);
                }
            }
        }
        ret.addAttribute("canDelete",String.valueOf(canDelete));
        ret.addAttribute("movable",String.valueOf(movable));
        ret.addAttribute("useCount", String.valueOf(useCount));
        if (maxOwnCount != 0) {
            ret.addAttribute("maxOwnCount",String.valueOf(maxOwnCount));
        }
        Element el = new Element("extProps");
        extPropEntries.saveToDom(el);
        ret.addContent(el);
        return ret;
        
    }

    public void update(DataObject obj) {
        Item itemCopy = (Item) obj;
        id = itemCopy.id;
        addition = itemCopy.addition;
        instance = itemCopy.instance;
        area = itemCopy.area;
        autoUse = itemCopy.autoUse;
        waste = itemCopy.waste;
        available = itemCopy.available;
        useClazz = itemCopy.useClazz;
        useConfirm = itemCopy.useConfirm;
        additionalPrompt = itemCopy.additionalPrompt;
        playerMaxLevel = itemCopy.playerMaxLevel;
        quality = itemCopy.quality;
        bind = itemCopy.bind;
        description = itemCopy.description;
        remark = itemCopy.remark;
        level = itemCopy.level;
        playerLevel = itemCopy.playerLevel;
        coldDownTime = itemCopy.coldDownTime;
        coldDownGroup = itemCopy.coldDownGroup;
        roundColdDownGroup = itemCopy.roundColdDownGroup;
        coldDownRound = itemCopy.coldDownRound;
        coldDownRoundCrossBattle = itemCopy.coldDownRoundCrossBattle;
        price = itemCopy.price;
        sale = itemCopy.sale;
        taskFlag = itemCopy.taskFlag;
        iconIndex = itemCopy.iconIndex;
        time = itemCopy.time;
        distance = itemCopy.distance;
        schedule = itemCopy.schedule;
        title = itemCopy.title;
        timeType = itemCopy.timeType;
        type = itemCopy.type;
        mainType = itemCopy.mainType;
        extPropEntries.copyFrom(itemCopy.extPropEntries);
        setCategoryName(itemCopy.getCategoryName());
        additionalCondition = itemCopy.additionalCondition;
        canDelete = itemCopy.canDelete;
        movable = itemCopy.movable;
        useCount = itemCopy.useCount;
        maxOwnCount = itemCopy.maxOwnCount;
        
        effects.clear();
        for (int i = 0; i < itemCopy.effects.size(); i++) {
            effects.add((ItemEffect)itemCopy.effects.get(i).clone());
        }
    }

    @Override
    public boolean changed(DataObject obj) {
        return changed(this, obj);
    }
    
    public String toString() {
        return id + ":" + title;
    }

    public boolean equals(Object obj){
        if(obj instanceof Item){
            return ((Item)obj).id == id;
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
        String tmp = context.input(title, "Item");
        if (tmp != null) {
            title = tmp;
            changed = true;
        }
        tmp = context.input(useConfirm, "Item");
        if (tmp != null) {
            useConfirm = tmp;
            changed = true;
        }
        tmp = context.input(additionalPrompt, "Item");
        if (tmp != null) {
            additionalPrompt = tmp;
            changed = true;
        }
        tmp = context.input(remark, "Item");
        if (tmp != null) {
            remark = tmp;
            changed = true;
        }
        tmp = context.input(description, "Item");
        if (tmp != null) {
            description = tmp;
            changed = true;
        }
        if (I18NUtils.processExpressionList(additionalCondition, context, 0, "Item")) {
            changed = true;
        }
        return changed;
    }
}
