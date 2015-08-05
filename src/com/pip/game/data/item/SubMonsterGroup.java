package com.pip.game.data.item;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.MonsterGroup;
import com.pip.game.data.i18n.I18NContext;

public class SubMonsterGroup extends DataObject{
    /**
     * �ӹ�����������������
     */
    public int countMax;
    
    /**
     * �ӹ�����������������
     */
    public int countMin;
        
    /**
     * �ӹ������еĹ���
     */
    public List<Monster> monsterGroup = new ArrayList<Monster>();
    
    /**
     * �ӹ�������Ҳ�ɰ���������
     */
    public List<MonsterGroup> monsterGrp = new ArrayList<MonsterGroup>();
    
    public ProjectData owner;
    
    public SubMonsterGroup(ProjectData pro){
        this.owner = pro;
    }
    
    public boolean depends(DataObject obj) {
        for(Monster m: monsterGroup) {
            if(m.depends(obj)) {
                return true;
            }
        }
        return false;
    }

    public DataObject duplicate() {
        SubDropGroup subDropGroup = new SubDropGroup();
        subDropGroup.update(this);
        return subDropGroup;
    }

    @Override
    public boolean changed(DataObject obj) {
        return changed(this, obj);
    }

    public void load(Element elem) {
        countMax = Integer.parseInt(elem.getAttributeValue("countMax"));
        countMin = Integer.parseInt(elem.getAttributeValue("countMin"));
//        job = Integer.parseInt(elem.getAttributeValue("job"));
        
        List<Element> monsterList = elem.getChildren("monster");
        monsterGroup.clear();
        for (Element monsterElem : monsterList){
            Monster monster = new Monster();
            monster.load(monsterElem);
            monsterGroup.add(monster);
        }
        
        List<Element> monsterGrpList = elem.getChildren("monstergroup");
        monsterGrp.clear();        
        for (Element monsterElem : monsterGrpList){
            MonsterGroup mg = new MonsterGroup(owner);
            mg.id = Integer.parseInt(monsterElem.getAttributeValue("id"));
            mg.title = monsterElem.getAttributeValue("title");
            mg.rate = Integer.parseInt(monsterElem.getAttributeValue("rate"));
            monsterGrp.add(mg);
        }
        
    }

    public Element save() {
        Element ret = new Element("subgroup");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("countMax", String.valueOf(countMax));
        ret.addAttribute("countMin", String.valueOf(countMin));
        
        for (Monster monster: monsterGroup) {
            ret.addContent(monster.save());
        }
        
        for (MonsterGroup mg: monsterGrp) {
            Element e = new Element("monstergroup");
            e.addAttribute("id",String.valueOf(mg.id));
            e.addAttribute("title", title == null ? "" : mg.title);
            e.addAttribute("rate",String.valueOf(mg.rate));
            ret.addContent(e);
        }
        return ret;
    }

    public void update(DataObject obj) {
        SubMonsterGroup subDropGroup = (SubMonsterGroup)obj;

        countMax = subDropGroup.countMax;
        countMin = subDropGroup.countMin;

        monsterGroup.clear();
        for (Monster m : subDropGroup.monsterGroup) {
            monsterGroup.add((Monster) m.duplicate());
        }
    }
    
    public String toString(){
        String str = "[" + countMin + "-" + countMax + "]��";

        return str;
    }
    
    /**
     * �ڸù������ڴ���һ���µĹ�����Ʒ����֤idΨһ
     * @return
     */
    public Monster getNewMonsterItem(){
        Monster drop = new Monster();
        
        drop.monsterId = 0;
        while(findMonsterItem(drop.monsterId) != null){
            drop.monsterId++;
        }
        return drop;
    }
    
    /**
     * ����DropItem��id��������
     * @param id
     * @return
     */
    private Monster findMonsterItem(int id){
        for (Monster item: monsterGroup) {
            if(item.monsterId == id){
                return item;
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
