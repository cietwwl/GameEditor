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
    
    /********************* �������� *********************/
    
    /** ������Ʒ */
    public static final int DROP_TYPE_ITEM = 0;
    /** ����װ�� */
    public static final int DROP_TYPE_EQUI = 1;
    /** ����һ�������� */
    public static final int DROP_TYPE_DROPGROUP = 2;
    /** �����Ǯ */
    public static final int DROP_TYPE_MONEY = 3;
    /** ���侭�� */
    public static final int DROP_TYPE_EXP = 4;
    
    /********************* �������� *********************/
    
    /**
     * ��������
     * 
     * ������Ʒ��0
     * ����װ����1
     * ����һ�������飺2
     * �����Ǯ��3
     * ���侭�飺4
     * 
     * >100 ������չ���ң��������Ͷ����currency.xml
     */
    public int dropType;
    /** �������Ʒ�������ID */
    public int dropID;
    /** ����Ʒ�����Ȩ�� */
    public int dropWeight;
    /** ����Ʒ������������ */
    public int quantityMax;
    /** ����Ʒ�������С���� */
    public int quantityMin;
    /** ��������ݶ�����ʱ���� */
    public DataObject dropObj;
    /** ��ʱ����ĵ����� */
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
            
            /* ��Ч����Ʒ�����洢 */
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
            return "��Ǯ" + "(" + quantityMin + "~" + quantityMax + ")";
        case DROP_TYPE_EXP:
            return "����" + "(" + quantityMin + "~" + quantityMax + ")";
        default:
            // ��չ���ҵ���
            String title = null;
            if(this.title == null || "".equals(this.title)){
                Currency c = (Currency)ProjectData.getActiveProject().findDictObject(Currency.class, dropType);
                title = c.title;
            }else{
                title = this.title;
            }
            return title + "(" + quantityMin + "~" + quantityMax + ")";
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
