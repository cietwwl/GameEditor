package com.pip.game.data.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.i18n.I18NContext;

/**
 * �ӵ��������ݽṹ���ӵ������п��԰���һ����Ʒ��Ҳ���԰���һ��������
 * @author Administrator
 *
 */
public class SubDropGroup extends DataObject{
    /**
     * �ӵ�����ĵȼ�����
     */
    public int levelMax;
    
    /**
     * �ӵ�����ĵȼ�����
     */
    public int levelMin;
    
    /**
     * ����ְҵ��-1��ʾ������
     */
    public int job;
    
    /**
     * ������Ʒ��������Ʒ�п�������Ʒ��װ������һ��������
     */
    public List<DropItem> dropGroup = new ArrayList<DropItem>();
    
    public boolean depends(DataObject obj) {
        // TODO Auto-generated method stub
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
        levelMax = Integer.parseInt(elem.getAttributeValue("levelMax"));
        levelMin = Integer.parseInt(elem.getAttributeValue("levelMin"));
        job = Integer.parseInt(elem.getAttributeValue("job"));
        
        List<Element> dropList = elem.getChildren("DropItem");
        dropGroup.clear();
        int totalWeight = 0;
        for (Element dropElem : dropList){
            DropItem dropItem = new DropItem();
            dropItem.load(dropElem);
            dropGroup.add(dropItem);
            totalWeight += dropItem.dropWeight;
        }
        
        // ���������
        if (totalWeight > 0) {
            for (DropItem item : dropGroup) {
                item.dropRate = (double)item.dropWeight / totalWeight;
            }
        }
    }

    public Element save() {
        Element ret = new Element("SubGroup");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("levelMax", String.valueOf(levelMax));
        ret.addAttribute("levelMin", String.valueOf(levelMin));
        ret.addAttribute("job", String.valueOf(job));
        
        for (DropItem item: dropGroup) {
            item.dropWeight = (int)(item.dropRate * 1000000);
            Element elem = item.save();
            if(elem != null) {
                ret.addContent(elem);
            }
        }
        return ret;
    }

    public void update(DataObject obj) {
        SubDropGroup subDropGroup = (SubDropGroup)obj;

        levelMax = subDropGroup.levelMax;
        levelMin = subDropGroup.levelMin;
        job = subDropGroup.job;

        dropGroup.clear();
        for (DropItem drop : subDropGroup.dropGroup) {
            dropGroup.add((DropItem) drop.duplicate());
        }
    }
    
    public String toString(){
        String str = "[" + levelMin + "-" + levelMax + "]��";
        if (job >= 0) {
            str += "(" + ProjectData.getActiveProject().config.PLAYER_CLAZZ[job] + ")";
        }
        return str;
    }
    
    /**
     * �ڸõ������ڴ���һ���µĵ�����Ʒ����֤idΨһ
     * @return
     */
    public DropItem getNewDropItem(){
        DropItem drop = new DropItem();
        
        drop.id = 0;
        while(findDropItem(drop.id) != null){
            drop.id++;
        }
        return drop;
    }
    
    /**
     * ����DropItem��id��������
     * @param id
     * @return
     */
    private DropItem findDropItem(int id){
        for (DropItem item: dropGroup) {
            if(item.id == id){
                return item;
            }
        }
        return null;
    }

    /**
     * �������ñ�ʹָ��������Ŀ�ĵ����ʷ���ָ��Ҫ��
     * @param index
     * @param percent
     */
    public void setWeightByPercent(int index, double percent, Set<Integer> lockIDs) {
        // ��������������ռ�İٷֱ�
        double lockPer = 0.0;
        for (DropItem item : dropGroup) {
            if (lockIDs.contains(item.id)) {
                lockPer += item.dropRate;
            }
        }
        
        // 100%���⴦��������������ȫ������Ϊ0
        if (percent + lockPer > 0.99999f) {
            for (int i = 0; i < dropGroup.size(); i++) {
                DropItem item = dropGroup.get(i);
                if (lockIDs.contains(item.id)) {
                    continue;
                } else if (i == index) {
                    item.dropRate = 1.0 - lockPer;
                } else {
                    item.dropRate = 0.0;
                }
            }
            return;
        }
        
        // �����µĵ�����
        double unlockTotal = 1.0 - lockPer - dropGroup.get(index).dropRate;
        if (unlockTotal < 0.00001f) {
            // ��������϶���ʣ�µĶ�Ϊ0���������϶�
            return;
        }
        for (int i = 0; i < dropGroup.size(); i++) {
            DropItem item = dropGroup.get(i);
            if (lockIDs.contains(item.id)) {
                continue;
            } else if (i == index) {
                item.dropRate = percent;
            } else {
                item.dropRate = item.dropRate * (1.0 - lockPer - percent) / unlockTotal;
            }
        }
    }
    
    /**
     * �������ñ�ʹ����δ�����ĵ�����Ŀ���ֵ����ʡ�
     */
    public void averageWeight(Set<Integer> lockIDs) {
        // ��������������ռ�İٷֱ�
        double lockPer = 0.0;
        int lockCount = 0;
        for (DropItem item : dropGroup) {
            if (lockIDs.contains(item.id)) {
                lockPer += item.dropRate;
                lockCount++;
            }
        }
       
        // ����ʣ��ƽ��
        if (lockCount == dropGroup.size()) {
            return;
        }
        double newPer = (1.0 - lockPer) / (dropGroup.size() - lockCount);
        for (DropItem item : dropGroup) {
            if (!lockIDs.contains(item.id)) {
                item.dropRate = newPer;
            }
        }
    }
    
    /**
     * �ѵ�����̯ƽ�����Ч�ʡ�����̯ƽ�ĵ�����������������
     * 1. �������ӵ�����ֻ����1����Ʒ��
     * 2. �������ӵ������кͱ���������ȫ��ͬ����Ҽ������á�
     * @param parent ����������
     */
    public void makeFlat(DropGroup parent) {
        List<DropItem> newList = new ArrayList<DropItem>();
        for (DropItem di : dropGroup) {
            if (di.dropType == DropItem.DROP_TYPE_DROPGROUP && di.quantityMin == 1 && di.quantityMax == 1) {
                // ����ǰ����ӵ����飬�����ӵ�����ֻ����һ��������ܺϲ�
                DropGroup subGroup = (DropGroup)parent.owner.findObject(DropGroup.class, di.dropID);
                if (subGroup == null) {
                    throw new IllegalArgumentException("���ݴ��󣬵�·�鲻���ڣ�" + di.dropID);
                }
                
                // �ϲ�ǰ�ӵ���������̯ƽ����
                subGroup.makeFlat();
                
                // ���Ұ����ĵ��������Ƿ��а������鼶�����õ�����
                SubDropGroup mergeGroup = null;
                for (SubDropGroup sdg : subGroup.subGroup) {
                    if (sdg.levelMin <= levelMin && sdg.levelMax >= levelMax && 
                            (sdg.job == job || sdg.job == -1)) {
                        mergeGroup = sdg;
                        break;
                    }
                }
                
                // ����ҵ������������ʺϲ�
                if (mergeGroup == null) {
                    newList.add(di);
                } else {
                    for (DropItem dii : mergeGroup.dropGroup) {
                        DropItem newdi = (DropItem)dii.duplicate();
                        newdi.dropRate *= di.dropRate;
                        newList.add(newdi);
                    }
                }
            } else {
                newList.add(di);
            }
        }
        
        // �ϲ�������ɺ󣬸��ݵ����������趨����Ȩ��
        dropGroup = newList;
        for (DropItem di : dropGroup) {
            di.dropWeight = (int)(di.dropRate * 1000000);
        }
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
