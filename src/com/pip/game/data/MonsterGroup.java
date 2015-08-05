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
     * ��������
     */
    public static final int COUNT_MIN = 0;
    /**
     * ��������
     */
    public static final int COUNT_MAX = 200;
    
    public ProjectData owner;
    
//    public int groupLevel = 1;
    
    public List<SubMonsterGroup> subMonsterGroup = new ArrayList<SubMonsterGroup>();

    /** ׷���ٶ� */
    public int speed;
    /** Ѳ���ٶ� */
    public int walkSpeed;
    public int eyeshot;  //��Ұ��Χ
    public int chaseDistance; //׷����Χ
    public int battleDistance;//����ս����Χ
    public int rate; //��Ƕ�׵Ĺ�����ĳ��ּ���

    public static Map<Integer,NPCTemplate> monsters = new HashMap<Integer,NPCTemplate>();
    
    private List<Monster> ms;
        
    /**  ��ɱ���Ĵ���  **/
    public int killedCount;
    
    /** �Ƿ���͵Ϯʱ����ս�� */
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
     * ���ݼ�����һ���ӹ�����    
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
     * ����ģ��ID����һ��ģ�岢ȡ��ģ�����ơ�
     * @param project
     * @param templateID
     * @return
     */
    public static String toString(ProjectData project, int templateID) {
        MonsterGroup t = (MonsterGroup)project.findObject(MonsterGroup.class, templateID);
        if (t == null) {
            return "��";
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
     * ����û�����ĵȼ���Χ�Ƿ���Ч�������еȼ���Χ�����غϣ�
     * @param levelMin
     *          �ȼ�����
     * @param levelMax
     *          �ȼ�����
     * @param index �ų�����
     * @return
     */
    public boolean isRangeValid(int levelMin, int levelMax, int index){
        /*
         * ������Χ����Ƚϣ�ֻҪ����һ����Χ�е�����һ��������һ����Χ�а�����
         * ��������Χ�ཻ������û���ཻ
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
     * ������Ҫ��ӵĹ������Ƿ���ڵݹ�ĵ���
     * 
     * @param childGroup
     *          ��Ҫ��ӵ��ӹ�����
     * @return
     */
    public boolean isGroupValid(MonsterGroup childGroup){
        if(childGroup.id == this.id) {
            return false;
        }
        /* �õ���Ҫ��ӵĹ������ڰ������ӹ����� */
        List<MonsterGroup> children = childGroup.getChildrenGroup();
        if(children.size() > 0){
            for(MonsterGroup child : children){
                /* ��Ҫ��ӵĹ����飬�������Լ��������ӹ������ڵ�ǰ������ȵĻ���
                 * ���ж�Ϊ�ظ�
                 */
                if(child.id == this.id || !isGroupValid(child)){
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * ��ȡ�ù������ڰ��������й�����
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
     * ��ù������е����й�
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
            return new ArrayList<Monster>();//��ֹ���ú���addAll����
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
     * �������й����Ĺ�����
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
     * �����ӹ������еĹ�����
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
     * ����id��ȫ�ֲ���һ��������
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
     * �������������Խ��й��ʻ������������Ҫ���ʻ����ַ���������ȡ������context�в��ҷ�������
     * @param context
     * @return �����ĳ�����Ա��滻������true�����򷵻�false��
     */
    public boolean i18n(I18NContext context) {
        return false;
    }
}
