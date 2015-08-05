package com.pip.game.data;

import org.jdom.Element;

import com.pip.game.data.i18n.I18NContext;

/**
 * �ƺš�
 */
public class Title extends DataObject {
    /** �����ƺ� */
    public static final int TYPE_OTHER = 0;
    /** ��ְ�ƺ� */
    public static final int TYPE_OFFICIAL = 1;
    /** ���ҳƺ� */
    public static final int TYPE_COUNTRY = 2;
    
    /**
     * ������Ŀ��
     */
    public ProjectData owner;
    /**
     * �ƺ����͡�
     */
    public int type;
    /**
     * ��Ҫ����
     */
    public int level = 1;
    /**
     * �۸��������һ���
     */
    public int price;
    /**
     * ٺ»��0��ʾû�У�
     */
    public int salary;
    /**
     * ������Ӫ�����������������Ӫ���ɶһ���
     */
    public Faction faction;
    /**
     * ��Ӧ����Ч��
     */
    public int buffID;
    /**
     * ��ӦЧ������
     */
    public int buffLevel;

    public Title(ProjectData owner) {
        this.owner = owner;
    }

    public int getID() {
        return id;
    }
    
    public String toString() {
        return id + ": " + title;
    }

    public boolean equals(Object o) {
        return this == o;
    }
    
    public void update(DataObject obj) {
        Title oo = (Title)obj;
        id = oo.id;
        title = oo.title;
        description = oo.description;
        setCategoryName(oo.getCategoryName());
        
        type = oo.type;
        level = oo.level;
        price = oo.price;
        salary = oo.salary;
        faction = oo.faction;
        buffID = oo.buffID;
        buffLevel = oo.buffLevel;
        
        if (owner != oo.owner) {
            faction = (Faction)owner.findDictObject(Faction.class, faction.id);
        }
    }
    
    public DataObject duplicate() {
        Title ret = new Title(owner);
        ret.update(this);
        return ret;
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
        
        type = Integer.parseInt(elem.getAttributeValue("type"));
        level = Integer.parseInt(elem.getAttributeValue("level"));
        price = Integer.parseInt(elem.getAttributeValue("price"));
        salary = Integer.parseInt(elem.getAttributeValue("salary"));
        int factionID = Integer.parseInt(elem.getAttributeValue("faction"));
        faction = (Faction)owner.findDictObject(Faction.class, factionID);
        buffID = Integer.parseInt(elem.getAttributeValue("buff"));
        try {
            buffLevel = Integer.parseInt(elem.getAttributeValue("bufflevel"));
        } catch (Exception e) {
            buffLevel = 1;
        }
    }
    
    public Element save() {
        Element ret = new Element("title");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        ret.addAttribute("description", description);
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }
        
        ret.addAttribute("type", String.valueOf(type));
        ret.addAttribute("level", String.valueOf(level));
        ret.addAttribute("price", String.valueOf(price));
        ret.addAttribute("salary", String.valueOf(salary));
        ret.addAttribute("faction", String.valueOf(faction.id));
        ret.addAttribute("buff", String.valueOf(buffID));
        ret.addAttribute("bufflevel", String.valueOf(buffLevel));
        return ret;
    }
    
    public boolean depends(DataObject obj) {
        return false;
    }
    
    /**
     * �������������Խ��й��ʻ������������Ҫ���ʻ����ַ���������ȡ������context�в��ҷ�������
     * @param context
     * @return �����ĳ�����Ա��滻������true�����򷵻�false��
     */
    public boolean i18n(I18NContext context) {
        boolean changed = false;
        String tmp = context.input(title, "Title");
        if (tmp != null) {
            title = tmp;
            changed = true;
        }
        tmp = context.input(description, "Title");
        if (tmp != null) {
            description = tmp;
            changed = true;
        }
        return changed;
    }
}
