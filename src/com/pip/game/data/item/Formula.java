package com.pip.game.data.item;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.Shop.BuyRequirement;
import com.pip.game.data.i18n.I18NContext;
import com.pip.game.data.skill.SkillConfig;

/**
 * ��Ʒ���칫ʽ��
 */
public class Formula extends DataObject {
    public static final int PRODUCT_ITEM = 0;
    public static final int PRODUCT_DROPGROUP = 1;
    
    /**
     * ������Ŀ��
     */
    public ProjectData owner;
    /**
     * ���ܼ���
     */
    public int level;
    /**
     * �����ж�����
     */
    public int movePoint;
    /**
     * �������� 
     */
    public List<BuyRequirement> requirements = new ArrayList<BuyRequirement>();
    /**
     * ��Ʒ���͡�
     */
    public int productType = PRODUCT_ITEM;
    /**
     * ��С��Ʒ����
     */
    public int minAmount = 1;
    /**
     * ����Ʒ����
     */
    public int maxAmount = 1;
    /**
     * ������ID�������Ʒ����Ϊ������ʱʹ�ã�
     */
    public int groupID;
    /**
     * ��ƷID�������Ʒ����Ϊ��Ʒ����ָ����ƷID�������Ʒ����Ϊ�����飬��ָ��������ʾ�������Ʒ����
     */
    public int itemID;

    public Formula(ProjectData owner) {
        this.owner = owner;
    }

    public boolean depends(DataObject obj) {
        return false;
    }

    public DataObject duplicate() {
        Formula copy = new Formula(owner);
        copy.update(this);
        return copy;
    }

    @Override
    public boolean changed(DataObject obj) {
        return changed(this, obj);
    }
    
    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
        description = elem.getAttributeValue("description");
        setCategoryName(elem.getAttributeValue("category"));
        if (getWholeCategoryName() == null) {
            setCategoryName("");
        }

        level = Integer.parseInt(elem.getAttributeValue("level"));
        try {
            movePoint = Integer.parseInt(elem.getAttributeValue("movepoint"));
        } catch (Exception e) {
        }

        // ������Ʒ
        List reqElems = elem.getChildren("requirement");
        for (int i = 0; i < reqElems.size(); i++) {
            BuyRequirement req = BuyRequirement.load(owner, (Element)reqElems.get(i));
            if (req != null ){
                requirements.add(req);
            }
        }
        
        // ��Ʒ��Ϣ
        if ("dropgroup".equals(elem.getAttributeValue("producttype"))) {
            this.productType = PRODUCT_DROPGROUP;
        } else {
            this.productType = PRODUCT_ITEM;
        }
        minAmount = Integer.parseInt(elem.getAttributeValue("minamount"));
        maxAmount = Integer.parseInt(elem.getAttributeValue("maxamount"));
        itemID = Integer.parseInt(elem.getAttributeValue("itemid"));
        groupID = Integer.parseInt(elem.getAttributeValue("groupid"));
    }

    public Element save() {
        Element ret = new Element("formula");
        
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        ret.addAttribute("description", description);
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }
        
        ret.addAttribute("level", String.valueOf(level));
        ret.addAttribute("movepoint", String.valueOf(movePoint));
        
        // ������Ʒ
        for (BuyRequirement br : requirements) {
            Element elem = br.save();
            if (elem != null) {
                ret.addContent(elem);
            }
        }
        
        // ��Ʒ��Ϣ
        if (productType == PRODUCT_DROPGROUP) {
            ret.addAttribute("producttype", "dropgroup");
        } else {
            ret.addAttribute("producttype", "item");
        }
        ret.addAttribute("minamount", String.valueOf(minAmount));
        ret.addAttribute("maxamount", String.valueOf(maxAmount));
        ret.addAttribute("itemid", String.valueOf(itemID));
        ret.addAttribute("groupid", String.valueOf(groupID));
        return ret;
    }

    public void update(DataObject obj) {
        Formula itemCopy = (Formula) obj;
        id = itemCopy.id;
        title = itemCopy.title;
        description = itemCopy.description;
        setCategoryName(itemCopy.getCategoryName());
        level = itemCopy.level;
        movePoint = itemCopy.movePoint;
        
        requirements.clear();
        for (BuyRequirement br : itemCopy.requirements) {
            requirements.add(br.dup());
        }
        
        productType = itemCopy.productType;
        minAmount = itemCopy.minAmount;
        maxAmount = itemCopy.maxAmount;
        itemID = itemCopy.itemID;
        groupID = itemCopy.groupID;
        
        if (owner != itemCopy.owner) {
            for (BuyRequirement br : requirements) {
                br.switchProject(owner);
            }
        }
    }
    
    public String toString() {
        return id + ":" + title;
    }

    public boolean equals(Object obj){
        if(obj instanceof Formula){
            return ((Formula)obj).id == id;
        }
        return false;
    }
    
    /**
     * �����䷽ID�����䷽�����֡�
     * @return
     */
    public static String toString(ProjectData project, int id) {
        Formula ret = (Formula)project.findObject(Formula.class, id);
        if (ret == null) {
            return "��Ч�䷽";
        }
        return ret.toString();
    }
    
    /**
     * �������������Խ��й��ʻ������������Ҫ���ʻ����ַ���������ȡ������context�в��ҷ�������
     * @param context
     * @return �����ĳ�����Ա��滻������true�����򷵻�false��
     */
    public boolean i18n(I18NContext context) {
        boolean changed = false;
        String tmp = context.input(title, "Formula");
        if (tmp != null) {
            title = tmp;
            changed = true;
        }
        tmp = context.input(description, "Formula");
        if (tmp != null) {
            description = tmp;
            changed = true;
        }
        return changed;
    }
}
