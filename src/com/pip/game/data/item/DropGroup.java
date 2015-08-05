package com.pip.game.data.item;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.equipment.Equipment;
import com.pip.game.data.i18n.I18NContext;

/**
 * 掉落组数据结构，包含一个子掉落组列表和当前掉落组的总权重
 * @author Joy Yan
 *
 */
public class DropGroup extends DataObject{
    /****************************************************/
    
    /**
     * 等级下限
     */
    public static final int LEVEL_MIN = 0;
    /**
     * 等级上限
     */
    public static final int LEVEL_MAX = 200;
    /**
     * 世界掉落组
     */
    public static final int GROUP_TYPE_WORLD = 0;
    /**
     * 普通掉落组
     */
    public static final int GROUP_TYPE_NORMAL = 1;
    /****************************************************/
    
    public ProjectData owner;
    /**
     * 掉落数量上限
     */
    public int quantityMax = 1;
    
    /**
     * 掉落数量下限
     */
    public int quantityMin = 1;
    
    /**
     * 掉落组类型
     * 世界掉落：0
     * 普通掉落：1
     */
    public int groupType = GROUP_TYPE_NORMAL;
    
    /**
     * 是否有效。
     */
    public boolean valid = true;
    /**
     * 世界掉落：最小怪物級別（含）
     */
    public int minMonsterLevel = 1;
    /**
     * 世界掉落：最大怪物级别（含）
     */
    public int maxMonsterLevel = 100;
    /**
     * 世界掉落：掉落率（单位1%%）
     */
    public int dropRate = 1;
    
    /**
     * 服务器模式下，是否已被摊平处理过，避免重复处理。
     */
    public boolean flat = false;
    
    /**
     * 子掉落组
     */
    public List<SubDropGroup> subGroup = new ArrayList<SubDropGroup>();
    
    public DropGroup(ProjectData owner){
        this.owner = owner;
    }

    public boolean depends(DataObject obj) {
        // TODO Auto-generated method stub
        return false;
    }
    
    public DataObject duplicate() {
        DropGroup group = new DropGroup(owner);
        group.update(this);
        return group;
    }

    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
        setCategoryName(elem.getAttributeValue("category"));
        if (getWholeCategoryName() == null) {
            setCategoryName("");
        }
        
        quantityMin = Integer.parseInt(elem.getAttributeValue("quantityMin"));
        quantityMax = Integer.parseInt(elem.getAttributeValue("quantityMax"));
        groupType = Integer.parseInt(elem.getAttributeValue("groupType"));
        if (groupType == GROUP_TYPE_WORLD) {
            try {
                minMonsterLevel = Integer.parseInt(elem.getAttributeValue("minMonsterLevel"));
                maxMonsterLevel = Integer.parseInt(elem.getAttributeValue("maxMonsterLevel"));
                dropRate = Integer.parseInt(elem.getAttributeValue("dropRate"));
            } catch (Exception e) {
            }
        }
        valid = !("0".equals(elem.getAttributeValue("valid")));

        loadSubDropGroup(elem);
        
    }
    public void loadSubDropGroup(Element elem) {
        List<Element> list = elem.getChildren("SubGroup");
        
        subGroup.clear();
        for (Element dropElem : list) {
            SubDropGroup subDrop = new SubDropGroup();
            subDrop.load(dropElem);
            subGroup.add(subDrop);
        }
    }

    public Element save() {
        Element ret = new Element("DropGroup");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }
        
        ret.addAttribute("quantityMin", String.valueOf(quantityMin));
        ret.addAttribute("quantityMax", String.valueOf(quantityMax));
        ret.addAttribute("groupType", String.valueOf(groupType));
        if (groupType == GROUP_TYPE_WORLD) {
            ret.addAttribute("minMonsterLevel", String.valueOf(minMonsterLevel));
            ret.addAttribute("maxMonsterLevel", String.valueOf(maxMonsterLevel));
            ret.addAttribute("dropRate", String.valueOf(dropRate));
        }
        ret.addAttribute("valid", valid ? "1" : "0");
        
        for (SubDropGroup sub: subGroup) {
            ret.getMixedContent().add(sub.save());
        }
        return ret;
    }

    public void update(DataObject obj) {
        DropGroup group = (DropGroup)obj;
        id = group.id;
        title = group.title;
        setCategoryName(group.getCategoryName());
        quantityMin = group.quantityMin;
        quantityMax = group.quantityMax;
        groupType = group.groupType;
        valid = group.valid;
        minMonsterLevel = group.minMonsterLevel;
        maxMonsterLevel = group.maxMonsterLevel;
        dropRate = group.dropRate;
        
        subGroup.clear();
        for(SubDropGroup sub : group.subGroup){
            subGroup.add((SubDropGroup)sub.duplicate());
        }
    }

    @Override
    public boolean changed(DataObject obj) {
        return changed(this, obj);
    }
    
    public String toString() {
        return id + ":" + title;
    }
    
    public static String toSting(int id) {
        DropGroup drop = (DropGroup)ProjectData.getActiveProject().findObject(DropGroup.class, id);
        if(drop != null){
            return drop.toString();
        }
        return "无聊的未知掉落";
    }
    
    /**
     * 检测用户输入的等级范围是否有效（和现有等级范围不能重合）
     * @param levelMin
     *          等级下限
     * @param levelMax
     *          等级上限
     * @param job 职业
     * @param index 排除索引
     * @return
     */
    public boolean isRangeValid(int levelMin, int levelMax, int job, int index){
        /*
         * 两个范围互相比较，只要其中一个范围中的任意一点在另外一个范围中包含，
         * 则两个范围相交，否则没有相交
         */
        for (int i = 0; i < subGroup.size(); i++){
            if (index == i) {
                continue;
            }
            
            SubDropGroup sub = subGroup.get(i);
            if (sub.job >= 0 && job >= 0 && sub.job != job) {
                // 如果职业不同，不算重叠
                continue;
            }
            if((levelMin >= sub.levelMin && levelMin <= sub.levelMax)
               || (levelMax >= sub.levelMin && levelMax <= sub.levelMax)
               || (sub.levelMin >= levelMin && sub.levelMin <= levelMax)
               || (sub.levelMax >= levelMin && sub.levelMax < levelMax)){
                return false;
            }
        }
        return true;
    }
    
    /**
     * 获取该掉落组内包含的所有掉落组
     * @return
     */
    private List<DropGroup> getChildrenGroup(){
        ProjectData projData = ProjectData.getActiveProject();
        List<DropGroup> ret = new ArrayList<DropGroup>();
        for(SubDropGroup subDrop : subGroup){
            for(DropItem drop : subDrop.dropGroup){
                if(drop.dropType == DropItem.DROP_TYPE_DROPGROUP){
                    ret.add((DropGroup)projData.findObject(DropGroup.class, drop.dropID));
                }
            }
        }
        return ret;
    }
    
    /**
     * 获得掉落组中可能掉落的所有物品，包括装备
     */
    public List<DropItem> getAllDropItems(List<DropItem> dropItems) {
        if(dropItems == null) {
            dropItems = new ArrayList<DropItem>();
        }
        
        ProjectData projData = ProjectData.getActiveProject();
        for(SubDropGroup subDrop : subGroup){
            for(DropItem drop : subDrop.dropGroup){
                if(drop.dropType == DropItem.DROP_TYPE_DROPGROUP){
                    DropGroup dp =  (DropGroup)projData.findObject(DropGroup.class, drop.dropID);
                    dropItems.addAll(dp.getAllDropItems(dropItems));
                } else if(drop.dropType == DropItem.DROP_TYPE_ITEM || drop.dropType == DropItem.DROP_TYPE_EQUI) {
                    dropItems.add(drop);
                }
            }
        }
        
        return dropItems;
    }
    
    /**
     * 返回需要添加的掉落组是否存在递归的调用
     * 
     * @param childGroup
     *          需要添加的子掉落组
     * @return
     */
    public boolean isGroupValid(DropGroup childGroup){
        /* 得到需要添加的掉落组内包含的子掉落组 */
        List<DropGroup> children = childGroup.getChildrenGroup();
        if(children.size() > 0){
            for(DropGroup child : children){
                /* 需要添加的掉落组，但凡其自己或者其子掉落组于当前对象相等的话，
                 * 则判断为重复
                 */
                if(child.equals(this) || !isGroupValid(child)){
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * 判断两个掉落组是否相等，以id作为唯一标示
     */
    public boolean equals(Object obj){
        if(obj instanceof DropGroup){
            return ((DropGroup)obj).id == id;
        }
        return false;
    }

    /**
     * 查找一个掉落组的名字。
     */
    public static String toString(ProjectData project, int id) {
        DropGroup q = (DropGroup) project.findObject(DropGroup.class, id);
        if (q == null) {
            return "无效掉落组";
        } else {
            return q.toString();
        }
    }

    /**
     * 把掉落组摊平以提高效率。可以摊平的掉落组有以下条件：
     * 1. 包含的子掉落组只掉落1个物品。
     * 2. 包含的子掉落组有和本掉落组完全相同的玩家级别设置。
     */
    public void makeFlat() {
        if (flat) {
            return;
        }
        flat = true;
        for (SubDropGroup sdg : subGroup) {
            sdg.makeFlat(this);
        }
    }
    
    /**
     * 掉落组是否绑定<br>
     * 只要发现掉落组中掉落不绑定的物品，掉落组为不绑定<br>
     * @return
     */
    public boolean isBound(){
        List<DropItem> dropItems = getAllDropItems(null);
        for(DropItem di : dropItems){
            if(di.dropType == DropItem.DROP_TYPE_ITEM){
                Item item = owner.findItem(di.dropID);
                if(item != null && item.bind != Item.BIND_PICK_UP){
                    return false;
                }
            }else if(di.dropType == DropItem.DROP_TYPE_EQUI){
                Equipment equ = owner.findEquipment(di.dropID);
                if(equ != null && equ.bind != Item.BIND_PICK_UP){
                    return false;
                }
            }
        }
        return true;
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
