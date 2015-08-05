package com.pip.game.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jdom.Element;

import com.pip.game.data.i18n.I18NContext;
import com.pip.game.data.item.Monster;
import com.pip.game.data.item.SubMonsterGroup;
import com.pip.game.editor.EditorApplication;

public class MonsterGroup extends DataObject {
    
    /**
     * 数量下限
     */
    public static final int COUNT_MIN = 0;
    /**
     * 数量上限
     */
    public static final int COUNT_MAX = 200;
    
    public ProjectData owner;
    
//    public int groupLevel = 1;
    
    public List<SubMonsterGroup> subMonsterGroup = new ArrayList<SubMonsterGroup>();

    /** 追击速度 */
    public int speed;
    /** 巡逻速度 */
    public int walkSpeed;
    public int eyeshot;  //视野范围
    public int chaseDistance; //追击范围
    public int battleDistance;//拉入战斗范围
    public int rate; //被嵌套的怪物组的出现几率

    public static Map<Integer,NPCTemplate> monsters = new HashMap<Integer,NPCTemplate>();
    
    private List<Monster> ms;
        
    /**  被杀死的次数  **/
    public int killedCount;
    
    /** 是否能偷袭时拉出战斗 */
    public boolean canCheckout = true;
    
    public MonsterGroup(ProjectData owner){
        this.owner = owner;
        
        List<DataObject> l = owner.getDataListByType(NPCTemplate.class);
        for(DataObject obj : l){
            NPCTemplate npc = (NPCTemplate)obj;
            if(npc.type.id == 4){
                monsters.put(new Integer(npc.id),npc);
            }
        }
        //modified by tzhang
        
    }
        
    /**
     * 根据级别获得一个子怪物组    
     * @param level
     * @return
     */
    public SubMonsterGroup getSubMonsterGroupByLevel(int level) {
        for(SubMonsterGroup smg : subMonsterGroup) {
            if(level >= smg.countMin && level <= smg.countMax){
                return smg;
            }
        }
        
        return null;
    }
        
    public boolean changed(DataObject obj) {
        return super.changed(this,obj);
    }

    public boolean depends(DataObject obj) {
        for(SubMonsterGroup smg: subMonsterGroup) {
            if(smg.depends(obj)) {
                return true;
            }
        }
        return false;
    }

    public DataObject duplicate() {
        MonsterGroup mg = new MonsterGroup(owner);
        mg.update(this);
        return mg;
    }

    public String toString() {
        return id + ": " + title;
    }
    
    /**
     * 根据模板ID查找一个模板并取得模板名称。
     * @param project
     * @param templateID
     * @return
     */
    public static String toString(ProjectData project, int templateID) {
        MonsterGroup t = (MonsterGroup)project.findObject(MonsterGroup.class, templateID);
        if (t == null) {
            return "无";
        } else {
            return t.toString();
        }
    }
    
    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
//        groupLevel = Integer.parseInt(elem.getAttributeValue("level"));
        
        try {
            speed = Integer.parseInt(elem.getAttributeValue("speed"));
        }catch(Exception e) {            
        }
        try {
            walkSpeed = Integer.parseInt(elem.getAttributeValue("walkspeed"));
        } catch (Exception e) {
            walkSpeed = speed / 8;
        }
        try {
            eyeshot = Integer.parseInt(elem.getAttributeValue("eyeshot"));
        }catch(Exception e) {            
        }
        try {
            chaseDistance = Integer.parseInt(elem.getAttributeValue("chasedistance"));
        }catch(Exception e) {            
        }
        try {
            battleDistance = Integer.parseInt(elem.getAttributeValue("battledistance"));
        }catch(Exception e) {            
        }
        
        List<Element> items = elem.getChildren("subgroup");
        for(int i = 0 ; i < items.size() ; i ++){
            SubMonsterGroup _smg = new SubMonsterGroup(owner);
            _smg.load(items.get(i));
            subMonsterGroup.add(_smg);
        }
        setCategoryName(elem.getAttributeValue("category"));
        if (getWholeCategoryName() == null) {
            setCategoryName("");
        }
        
        try {
            canCheckout = elem.getAttributeValue("canCheckout").equals("1");
        }catch(Exception e) {   
            canCheckout = true;
        }
        
    }

    public Element save() {
        Element ret = new Element("monstergroup");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
//        ret.addAttribute("level",String.valueOf(groupLevel));
        
        ret.addAttribute("speed", String.valueOf(speed));
        ret.addAttribute("walkspeed", String.valueOf(walkSpeed));
        ret.addAttribute("eyeshot", String.valueOf(eyeshot));
        ret.addAttribute("chasedistance", String.valueOf(chaseDistance));
        ret.addAttribute("battledistance", String.valueOf(battleDistance));
        
        for(SubMonsterGroup subMg : subMonsterGroup){
            ret.addContent(subMg.save());
        }
        
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }
        ret.addAttribute("canCheckout", String.valueOf(canCheckout ? "1" : "0"));
        
        return ret;
    }

    public void update(DataObject obj) {
        MonsterGroup mg = (MonsterGroup)obj;
        update2(mg);
        subMonsterGroup.clear();
        for(SubMonsterGroup subMg : mg.subMonsterGroup){
            subMonsterGroup.add(subMg);
        }
    }
    
    private void update2(MonsterGroup mg) {
        id = mg.id;
        title = mg.title;
//        groupLevel = mg.groupLevel;
        speed = mg.speed;
        walkSpeed = mg.walkSpeed;
        eyeshot = mg.eyeshot;
        chaseDistance = mg.chaseDistance;
        battleDistance = mg.battleDistance;
        canCheckout = mg.canCheckout;
    }
    
    /**
     * 检测用户输入的等级范围是否有效（和现有等级范围不能重合）
     * @param levelMin
     *          等级下限
     * @param levelMax
     *          等级上限
     * @param index 排除索引
     * @return
     */
    public boolean isRangeValid(int levelMin, int levelMax, int index){
        /*
         * 两个范围互相比较，只要其中一个范围中的任意一点在另外一个范围中包含，
         * 则两个范围相交，否则没有相交
         */
        for (int i = 0; i < subMonsterGroup.size(); i++){
            if (index == i) {
                continue;
            }
            
            SubMonsterGroup sub = subMonsterGroup.get(i);
            if((levelMin >= sub.countMin && levelMin <= sub.countMax)
               || (levelMax >= sub.countMin && levelMax <= sub.countMax)
               || (sub.countMin >= levelMin && sub.countMin <= levelMax)
               || (sub.countMax >= levelMin && sub.countMax < levelMax)){
                return false;
            }
        }
        return true;
    }
    
    /**
     * 返回需要添加的怪物组是否存在递归的调用
     * 
     * @param childGroup
     *          需要添加的子怪物组
     * @return
     */
    public boolean isGroupValid(MonsterGroup childGroup){
        if(childGroup.id == this.id) {
            return false;
        }
        /* 得到需要添加的怪物组内包含的子怪物组 */
        List<MonsterGroup> children = childGroup.getChildrenGroup();
        if(children.size() > 0){
            for(MonsterGroup child : children){
                /* 需要添加的怪物组，但凡其自己或者其子怪物组于当前对象相等的话，
                 * 则判断为重复
                 */
                if(child.id == this.id || !isGroupValid(child)){
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * 获取该怪物组内包含的所有怪物组
     * @return
     */
    private List<MonsterGroup> getChildrenGroup(){
        ProjectData projData = ProjectData.getActiveProject();
        List<MonsterGroup> ret = new ArrayList<MonsterGroup>();
        for(SubMonsterGroup subDrop : subMonsterGroup){
            for(MonsterGroup monsterGroup : subDrop.monsterGrp){
                ret.add((MonsterGroup)projData.findObject(MonsterGroup.class, monsterGroup.id));
            }
        }
        return ret;
    }
    
    /**
     * 获得怪物组中的所有怪
     * @return
     */
    public List<Monster> getAllMonster(){
        List<Monster> monsters = new ArrayList<Monster>();
        for(SubMonsterGroup mg : subMonsterGroup){
            if(mg.monsterGroup != null && mg.monsterGroup.size() > 0){
                for(Monster m : mg.monsterGroup){
                    monsters.add(m);
                }
            }
            if(mg.monsterGrp != null && mg.monsterGrp.size() > 0){
                for(MonsterGroup smg : mg.monsterGrp){
                    monsters.addAll(smg.getAllMonster());
                }
            }
        }
        return monsters;
    }
    
    public List<Monster> getCombatMonsters(int partyLevel, boolean calcRate){
        SubMonsterGroup sg = getSubMonsterGroupByLevel(partyLevel);
        if(sg == null){
            return new ArrayList<Monster>();//防止调用函数addAll出错
        }
        List<Monster> monsters = new ArrayList<Monster>();
        for(Monster m : sg.monsterGroup){
            monsters.add(m);
        }
        for(MonsterGroup mg : sg.monsterGrp){
            Random r = new Random();
            if(r.nextInt(100) < mg.rate){
                monsters.addAll(mg.getCombatMonsters(partyLevel,false));    
            }
            
        }
        List<Monster> ls = new ArrayList<Monster>();
        Random r = new Random();
        for(Monster m : monsters){
            if(calcRate){
                if(r.nextInt(100) < m.rate){
                    ls.add(m);
                }
            }else{
                ls.add(m);
            }
//            if(ls.size()== 5){
//                break;
//            }
        }
        return ls;
    }
    
    /**
     * 更新所有关联的怪物组
     * @param pro
     */
    public static void updateAllRelateMonsterGroup(ProjectData pro) {
        List<DataObject> l = pro.getDataListByType(MonsterGroup.class);

        for(DataObject obj : l) {
            MonsterGroup mg = (MonsterGroup)obj;
            List<SubMonsterGroup> subMonsterGroup = mg.subMonsterGroup;
            updateAllRelateSubMonsterGroup(pro, subMonsterGroup);
        }
    }
    
    /**
     * 更新子怪物组中的怪物组
     * @param pro
     * @param subMonsterGroup
     */
    private static void updateAllRelateSubMonsterGroup(ProjectData pro, List<SubMonsterGroup> subMonsterGroup) {
        for(SubMonsterGroup subMg : subMonsterGroup) {
            for(MonsterGroup mgrp : subMg.monsterGrp) {
                updateAllRelateSubMonsterGroup(pro, mgrp.subMonsterGroup);
                
                MonsterGroup mg = findMonsterGroupById(pro, mgrp.id);
                if(mg != null) {
                    mgrp.update(mg);
                }
            }
        }
    }
    
    /**
     * 根据id在全局查找一个怪物组
     * @param pro
     * @param id
     * @return
     */
    public static MonsterGroup findMonsterGroupById(ProjectData pro, int id) {
        List<DataObject> l = pro.getDataListByType(MonsterGroup.class);
        for(DataObject obj : l) {
            MonsterGroup mg = (MonsterGroup)obj;
            if(mg.id == id) {
                return mg;
            }
        }
        return null;
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
