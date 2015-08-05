package com.pip.game.data;

import java.io.File;
import java.util.*;

import org.jdom.*;

import com.pip.game.data.i18n.I18NContext;
import com.pip.game.data.item.Item;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.skill.DescriptionPattern;
import com.pipimage.utils.Utils;

/**
 * NPC商店。
 * @author lighthu
 */
public class Shop extends DataObject {
    /**
     * 购买需求类型：金钱
     */
    public static final int TYPE_MONEY = 0;
    /**
     * 购买需求类型：i币（元宝），单位是1/100i
     */
    public static final int TYPE_IMONEY = 1;
    /**
     * 购买需求类型：物品
     */
    public static final int TYPE_ITEM = 2;
    /**
     * 购买需求类型：变量
     */
    public static final int TYPE_VARIABLE = 3;
    /**
     * 购买需求类型：级别
     */
    public static final int TYPE_LEVEL = 4;
    /**
     * 购买需求类型：消费代码
     */
    public static final int TYPE_CONSUMECODE = 5;
    
    public static final String[] TYPE_NAMES = {
        "money", "imoney", "item", "variable", "level", "consumecode"
    };
    
    /**
     * 物品购买需求。
     */
    public static class BuyRequirement {
        /** 需求类型 */
        public int type;
        /** 需求数量/级别/变量值 */
        public int amount;
        /** 需求物品 */
        public Item item;
        /** 是否扣除 */
        public boolean deduct;
        /** 变量名，只用于variable类型和consumecode类型 */
        public String varName = "";
        /** 检查变量时，变量条件的描述 */
        public String varDesc = "";
       
        public static String toString(List<BuyRequirement> list, boolean includeMoney, boolean includeIMoney) {
            return toString(list, includeMoney, includeIMoney, true);
        }
        
        public static String toString(List<BuyRequirement> list, boolean includeMoney, boolean includeIMoney, boolean includeVariable) {
            StringBuilder buf = new StringBuilder();
            for (Shop.BuyRequirement req : list) {
                if (!includeMoney && req.type == TYPE_MONEY && req.deduct) {
                    continue;
                }
                if (!includeIMoney && req.type == TYPE_IMONEY && req.deduct) {
                    continue;
                }
                if (buf.length() > 0) {
                    buf.append(";");
                }
                buf.append(req.toString());
            }
            return buf.toString();
        }
        
        public String toString() {
            switch (type) {
            case TYPE_MONEY:
                if (deduct) {
                    return "金钱" + amount;
                } else {
                    return "金钱达到" + amount;
                }
            case TYPE_IMONEY:
                if (deduct) {
                    return "元宝" + amount / 3600f;
                } else {
                    return "元宝达到" + amount / 3600f;
                }
            case TYPE_VARIABLE:
                return varName + "达到" + amount;
            case TYPE_LEVEL:
                return "级别达到" + amount;
            case TYPE_CONSUMECODE:
                return "消费代码" + varName;
            case TYPE_ITEM:
                if (deduct) {
                    return item.title + " x" + amount;
                } else {
                    return "拥有" + item.title + " x" + amount;
                }
            default:  // 扩展类型
                Currency currency = (Currency)ProjectData.getActiveProject().findDictObject(Currency.class, type);
                if (currency.type == Currency.CURRENCY_NUMBER) {
                    if (deduct) {
                        return currency.title + amount;
                    } else {
                        return currency.title + "达到" + amount;
                    }
                } else {
                    try {
                        DataObject reqObj = ProjectData.getActiveProject().findDictObject(currency.dictObjectClass, amount);
                        return currency.title + "达到" + reqObj.title;
                    } catch (Exception e) {
                        return e.toString();
                    }
                }
            }
        }
        
        public BuyRequirement dup() {
            BuyRequirement ret = new BuyRequirement();
            ret.type = type;
            ret.amount = amount;
            ret.item = item;
            ret.deduct = deduct;
            ret.varName = varName;
            ret.varDesc = varDesc;
            return ret;
        }
        
        public boolean equals(Object o) {
            if (o == null || !(o instanceof BuyRequirement)) {
                return false;
            }
            BuyRequirement oo = (BuyRequirement)o;
            if (type != oo.type) {
                return false;
            }
            switch (type) {
            case TYPE_MONEY:
            case TYPE_IMONEY:
                return amount == oo.amount && deduct == oo.deduct;
            case TYPE_LEVEL:
                return amount == oo.amount;
            case TYPE_VARIABLE:
                return amount == oo.amount && varName.equals(oo.varName) && varDesc.equals(oo.varDesc);
            case TYPE_CONSUMECODE:
                return varName.equals(oo.varName);
            case TYPE_ITEM:
                return amount == oo.amount && item == oo.item && deduct == oo.deduct;
            default:
                Currency currency = (Currency)ProjectData.getActiveProject().findDictObject(Currency.class, type);
                if (currency.type == Currency.CURRENCY_NUMBER) {
                    return amount == oo.amount && deduct == oo.deduct;
                } else {
                    return amount == oo.amount;
                }
            }
        }
        
        public boolean isValid() {
            if (type == TYPE_VARIABLE) {
                if (varName.startsWith("__PLAYER_") || varName.startsWith("__TONG_") ||
                        varName.startsWith("__FACTION_") || varName.startsWith("__WORLD_") ||
                        varName.startsWith("__PARTY_")) {
                    return true;
                }
                return false;
            }
            if (type != TYPE_CONSUMECODE && amount == 0) {
                return false;
            }
            if (type == TYPE_ITEM && item == null) {
                return false;
            }
            if (type > TYPE_CONSUMECODE) {
                Currency currency = (Currency)ProjectData.getActiveProject().findDictObject(Currency.class, type);
                if (currency.type == Currency.CURRENCY_DICTOBJECT) {
                    try {
                        DataObject reqObj = ProjectData.getActiveProject().findDictObject(currency.dictObjectClass, amount);
                        return reqObj != null;
                    } catch (Exception e) {
                        return false;
                    }
                }
            }
            return true;
        }
        
        public static BuyRequirement load(ProjectData owner, Element elem) {
            BuyRequirement ret = new BuyRequirement();
            ret.type = nameToType(elem.getAttributeValue("type"));
            ret.amount = Integer.parseInt(elem.getAttributeValue("amount"));
            if (ret.type == TYPE_ITEM) {
                int itemID = Integer.parseInt(elem.getAttributeValue("itemid"));
                ret.item = owner.findItemOrEquipment(itemID);
                if (ret.item == null) {
                    return null;
                }
                ret.deduct = "true".equals(elem.getAttributeValue("deduct"));
            } else if (ret.type == TYPE_VARIABLE) {
                ret.varName = elem.getAttributeValue("variable");
                ret.varDesc = elem.getAttributeValue("vardesc");
            } else if (ret.type == TYPE_CONSUMECODE) {
                ret.varName = elem.getAttributeValue("code");
            } else {
                ret.deduct = "true".equals(elem.getAttributeValue("deduct"));
            }
            return ret;
        }
        
        public Element save() {
            if (!isValid()) {
                return null;
            }
            Element ret = new Element("requirement");
            ret.addAttribute("type", typeToName(type));
            ret.addAttribute("amount", String.valueOf(amount));
            if (type == TYPE_ITEM) {
                ret.addAttribute("itemid", String.valueOf(item.id));
                ret.addAttribute("deduct", deduct ? "true" : "false");
            } else if (type == TYPE_VARIABLE) {
                ret.addAttribute("variable", varName);
                ret.addAttribute("vardesc", varDesc);
            } else if (type == TYPE_CONSUMECODE) {
                ret.addAttribute("code", varName);
            } else {
                ret.addAttribute("deduct", deduct ? "true" : "false");
            }
            return ret;
        }
        
        public void switchProject(ProjectData prj) {
            if (item != null) {
                item = prj.findItemOrEquipment(item.id);
            }
        }
    }
    /**
     * 商店物品类。
     * @author lighthu
     */
    public static class ShopItem {
        /** 物品 */
        public Item item;
        /** 初始数量，0表示不限制数量 */
        public int count;
        /** 剩余数量 */
        public int remain;
        /** 刷新间隔(毫秒), 0表示不刷新 */
        public int refresh;
        /** 上次刷新时间(毫秒) */
        public int lastRefreshTime;
        /** 折扣率，100表示不打折。折扣率只对金钱价格、i币价格和荣誉价格有效。 */
        public int discount = 100;
        /** 单个用户每次刷新最多购买数量，0表示不限制 */
        public int buyLimit;
        /** 购买需求 */
        public List<BuyRequirement> requirements = new ArrayList<BuyRequirement>();
        /** 是否可以用绑定元宝替代元宝购买 */
        public boolean allowUseBindImoney;
        /** 用户购买记录，刷新时清除 */
        public HashMap<Integer, Integer> buyRecords = new HashMap<Integer, Integer>();
        
        public ShopItem dup() {
            ShopItem ret = new ShopItem();
            ret.item = item;
            ret.count = count;
            ret.remain = remain;
            ret.refresh = refresh;
            ret.lastRefreshTime = lastRefreshTime;
            ret.discount = discount;
            ret.buyLimit = buyLimit;
            for (BuyRequirement br : requirements) {
                ret.requirements.add(br.dup());
            }
            ret.allowUseBindImoney = allowUseBindImoney;
            return ret;
        }
        
        /**
         * 更新物品，检查刷新。
         * @param time
         */
        public synchronized void update(int time) {
            if (lastRefreshTime == 0) {
                lastRefreshTime = time;
                remain = count;
                buyRecords.clear();
            } else if (refresh > 0 && time >= lastRefreshTime + refresh) {
                lastRefreshTime = time;
                remain = count;
                buyRecords.clear();
            }
        }
        
        public void switchProject(ProjectData prj) {
            for (BuyRequirement br : requirements) {
                br.switchProject(prj);
            }
            item = prj.findItemOrEquipment(item.id);
        }
    };
    
    /**
     * 所属项目。
     */
    public ProjectData owner;
    /**
     * 商店中的物品
     */
    public List<ShopItem> items = new ArrayList<ShopItem>();
    /**
     * 阵营
     */
    public int faction;
    
    public Shop(ProjectData owner) {
        this.owner = owner;
    }

    public int getID() {
        return id;
    }
    
    public String getTitle() {
        if (this.description != null && this.description.length() > 0) {
            return this.description;
        }
        return title;
    }

    public boolean equals(Object o){
        return this == o;
    }
    
    public String toString() {
        return id + ": " + title;
    }

    /**
     * 从另外一个对象复制内容。
     */
    public void update(DataObject obj) {
        Shop oo = (Shop)obj;
        id = oo.id;
        title = oo.title;
        description = oo.description;
        setCategoryName(oo.getCategoryName());
        faction = oo.faction;
        
        List<ShopItem> oldItems = items;
        items.clear();
        for (ShopItem item : oo.items) {
            ShopItem newItem = item.dup();
            if (owner != oo.owner) {
                newItem.switchProject(owner);
            }
            
            // 服务器模式重载时，如果有旧数据，则需要保存：剩余数量、上次刷新时间、购买记录
            for (ShopItem oldItem : oldItems) {
                if (oldItem.item.id == newItem.item.id) {
                    if (newItem.count > 0) {
                        newItem.remain = oldItem.remain;
                    }
                    if (newItem.refresh > 0) {
                        newItem.lastRefreshTime = oldItem.lastRefreshTime;
                    }
                    newItem.buyRecords = oldItem.buyRecords;
                }
            }
            
            items.add(newItem);
        }
    }
    
    /**
     * 复制出一个对象来。
     */
    public DataObject duplicate() {
        Shop ret = new Shop(owner);
        ret.update(this);
        return ret;
    }

    @Override
    public boolean changed(DataObject obj) {
        return changed(this, obj);
    }
    
    /**
     * 从一个shop元素中载入商店。
     */
    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
        description = elem.getAttributeValue("description");
        if(description == null){
            description = "";
        }
        setCategoryName(elem.getAttributeValue("category"));
        if (getWholeCategoryName() == null) {
            setCategoryName("");
        }
        try{
            faction = Integer.parseInt(elem.getAttributeValue("faction"));
        }catch(Exception e){
            
        }
        
        items.clear();
        List itemElems = elem.getChildren("shopitem");
        for (int i = 0; i < itemElems.size(); i++) {
            ShopItem item = loadItem((Element)itemElems.get(i));
            if (item != null) {
                items.add(item);
            }
        }
    }
    
    /*
     * 从一个shopitem元素中载入商品。
     */
    protected ShopItem loadItem(Element elem) {
        ShopItem ret = new ShopItem();
        int itemID = Integer.parseInt(elem.getAttributeValue("itemid"));
        ret.item = owner.findItemOrEquipment(itemID);
        if (ret.item == null) {
            return null;
        }
        ret.count = Integer.parseInt(elem.getAttributeValue("count"));
        ret.remain = ret.count;
        ret.refresh = Integer.parseInt(elem.getAttributeValue("refresh")) * 1000;
        ret.discount = Integer.parseInt(elem.getAttributeValue("discount"));
        ret.buyLimit = Integer.parseInt(elem.getAttributeValue("buylimit"));
        List reqElems = elem.getChildren("requirement");
        for (int i = 0; i < reqElems.size(); i++) {
            BuyRequirement req = BuyRequirement.load(owner, (Element)reqElems.get(i));
            if (req != null ){
                ret.requirements.add(req);
            }
        }
        ret.allowUseBindImoney = "1".equals(elem.getAttributeValue("allowusebindimoney"));
        return ret;
    }
    
    /*
     * 需求项目类型名字转换为类型ID。
     */
    public static int nameToType(String typeName) {
        for (int i = 0; i < TYPE_NAMES.length; i++) {
            if (TYPE_NAMES[i].equals(typeName)) {
                return i;
            }
        }
        return Integer.parseInt(typeName);
    }
    
    /*
     * 需求项目类型ID转换为类型名字。
     */
    public static String typeToName(int id) {
        if (id < TYPE_NAMES.length) {
            return TYPE_NAMES[id];
        } else {
            return String.valueOf(id);
        }
    }
    
    /**
     * 把对象保存到一个shop元素中。
     */
    public Element save() {
        Element ret = new Element("shop");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        ret.addAttribute("description", description);
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }
        ret.addAttribute("faction", String.valueOf(faction));
        
        for (ShopItem item : items) {
            ret.addContent(saveItem(item));
        }
        return ret;
    }
    
    /*
     * 把一个商品保存到shopitem元素中。
     */
    protected Element saveItem(ShopItem item) {
        Element ret = new Element("shopitem");
        ret.addAttribute("itemid", String.valueOf(item.item.id));
        ret.addAttribute("count", String.valueOf(item.count));
        ret.addAttribute("refresh", String.valueOf(item.refresh / 1000));
        ret.addAttribute("discount", String.valueOf(item.discount));
        ret.addAttribute("buylimit", String.valueOf(item.buyLimit));
        for (BuyRequirement req : item.requirements) {
            Element elem = req.save();
            if (elem != null) {
                ret.addContent(elem);
            }
        }
        ret.addAttribute("allowusebindimoney", item.allowUseBindImoney ? "1" : "0");
        return ret;
    }
    
    public boolean depends(DataObject obj) {
        return false;
    }

    /**
     * 对这个对象的属性进行国际化处理，如果有需要国际化的字符串，则提取出来到context中查找翻译结果。
     * @param context
     * @return 如果有某个属性被替换，返回true，否则返回false。
     */
    public boolean i18n(I18NContext context) {
        String tmp = context.input(title, "Shop");
        if (tmp != null) {
            title = tmp;
            return true;
        } else {
            return false;
        }
    }
}
