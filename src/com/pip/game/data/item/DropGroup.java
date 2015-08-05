package com.pip.game.data.item;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.equipment.Equipment;
import com.pip.game.data.i18n.I18NContext;

/**
 * ���������ݽṹ������һ���ӵ������б�͵�ǰ���������Ȩ��
 * @author Joy Yan
 *
 */
public class DropGroup extends DataObject{
    /****************************************************/
    
    /**
     * �ȼ�����
     */
    public static final int LEVEL_MIN = 0;
    /**
     * �ȼ�����
     */
    public static final int LEVEL_MAX = 200;
    /**
     * ���������
     */
    public static final int GROUP_TYPE_WORLD = 0;
    /**
     * ��ͨ������
     */
    public static final int GROUP_TYPE_NORMAL = 1;
    /****************************************************/
    
    public ProjectData owner;
    /**
     * ������������
     */
    public int quantityMax = 1;
    
    /**
     * ������������
     */
    public int quantityMin = 1;
    
    /**
     * ����������
     * ������䣺0
     * ��ͨ���䣺1
     */
    public int groupType = GROUP_TYPE_NORMAL;
    
    /**
     * �Ƿ���Ч��
     */
    public boolean valid = true;
    /**
     * ������䣺��С���）�e������
     */
    public int minMonsterLevel = 1;
    /**
     * ������䣺�����Ｖ�𣨺���
     */
    public int maxMonsterLevel = 100;
    /**
     * ������䣺�����ʣ���λ1%%��
     */
    public int dropRate = 1;
    
    /**
     * ������ģʽ�£��Ƿ��ѱ�̯ƽ������������ظ�����
     */
    public boolean flat = false;
    
    /**
     * �ӵ�����
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
        return "���ĵ�δ֪����";
    }
    
    /**
     * ����û�����ĵȼ���Χ�Ƿ���Ч�������еȼ���Χ�����غϣ�
     * @param levelMin
     *          �ȼ�����
     * @param levelMax
     *          �ȼ�����
     * @param job ְҵ
     * @param index �ų�����
     * @return
     */
    public boolean isRangeValid(int levelMin, int levelMax, int job, int index){
        /*
         * ������Χ����Ƚϣ�ֻҪ����һ����Χ�е�����һ��������һ����Χ�а�����
         * ��������Χ�ཻ������û���ཻ
         */
        for (int i = 0; i < subGroup.size(); i++){
            if (index == i) {
                continue;
            }
            
            SubDropGroup sub = subGroup.get(i);
            if (sub.job >= 0 && job >= 0 && sub.job != job) {
                // ���ְҵ��ͬ�������ص�
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
     * ��ȡ�õ������ڰ��������е�����
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
     * ��õ������п��ܵ����������Ʒ������װ��
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
     * ������Ҫ��ӵĵ������Ƿ���ڵݹ�ĵ���
     * 
     * @param childGroup
     *          ��Ҫ��ӵ��ӵ�����
     * @return
     */
    public boolean isGroupValid(DropGroup childGroup){
        /* �õ���Ҫ��ӵĵ������ڰ������ӵ����� */
        List<DropGroup> children = childGroup.getChildrenGroup();
        if(children.size() > 0){
            for(DropGroup child : children){
                /* ��Ҫ��ӵĵ����飬�������Լ��������ӵ������ڵ�ǰ������ȵĻ���
                 * ���ж�Ϊ�ظ�
                 */
                if(child.equals(this) || !isGroupValid(child)){
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * �ж������������Ƿ���ȣ���id��ΪΨһ��ʾ
     */
    public boolean equals(Object obj){
        if(obj instanceof DropGroup){
            return ((DropGroup)obj).id == id;
        }
        return false;
    }

    /**
     * ����һ������������֡�
     */
    public static String toString(ProjectData project, int id) {
        DropGroup q = (DropGroup) project.findObject(DropGroup.class, id);
        if (q == null) {
            return "��Ч������";
        } else {
            return q.toString();
        }
    }

    /**
     * �ѵ�����̯ƽ�����Ч�ʡ�����̯ƽ�ĵ�����������������
     * 1. �������ӵ�����ֻ����1����Ʒ��
     * 2. �������ӵ������кͱ���������ȫ��ͬ����Ҽ������á�
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
     * �������Ƿ��<br>
     * ֻҪ���ֵ������е��䲻�󶨵���Ʒ��������Ϊ����<br>
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
     * �������������Խ��й��ʻ������������Ҫ���ʻ����ַ���������ȡ������context�в��ҷ�������
     * @param context
     * @return �����ĳ�����Ա��滻������true�����򷵻�false��
     */
    public boolean i18n(I18NContext context) {
        return false;
    }
}
