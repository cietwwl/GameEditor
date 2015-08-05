package com.pip.game.data;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.pip.game.data.i18n.I18NContext;

/**
 * �������顣һ������NPC��Ӧһ���������顣
 * @author lighthu
 */
public class TeleportSet extends DataObject {
    /**
     * �������ࡣ
     * @author lighthu
     */
    public static class Teleport {
        /** �������� */
        public String name = "";
        /** Ŀ���ͼID */
        public int mapID;
        /** Ŀ��Xλ�� */
        public int x;
        /** Ŀ��Yλ�� */
        public int y;
        /** �������� */
        public List<Shop.BuyRequirement> requirements = new ArrayList<Shop.BuyRequirement>();
        
        public Teleport dup() {
            Teleport ret = new Teleport();
            ret.name = name;
            ret.mapID = mapID;
            ret.x = x;
            ret.y = y;
            for (Shop.BuyRequirement br : requirements) {
                ret.requirements.add(br.dup());
            }
            return ret;
        }
    };
    
    /**
     * ������Ŀ��
     */
    public ProjectData owner;
    /**
     * ����Ŀ���б�
     */
    public List<Teleport> items = new ArrayList<Teleport>();
    
    public TeleportSet(ProjectData owner) {
        this.owner = owner;
    }

    public int getID() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }

    public boolean equals(Object o){
        return this == o;
    }
    
    public String toString() {
        return id + ": " + title;
    }

    /**
     * ������һ�����������ݡ�
     */
    public void update(DataObject obj) {
        TeleportSet oo = (TeleportSet)obj;
        id = oo.id;
        title = oo.title;
        description = oo.description;
        setCategoryName(oo.getCategoryName());
        
        items.clear();
        for (Teleport item : oo.items) {
            Teleport newItem = item.dup();
            items.add(newItem);
        }
    }
    
    /**
     * ���Ƴ�һ����������
     */
    public DataObject duplicate() {
        TeleportSet ret = new TeleportSet(owner);
        ret.update(this);
        return ret;
    }

    @Override
    public boolean changed(DataObject obj) {
        return changed(this, obj);
    }
    
    /**
     * ��һ��teleportsetԪ�������봫�͵㼯�ϡ�
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
        
        items.clear();
        List itemElems = elem.getChildren("teleport");
        for (int i = 0; i < itemElems.size(); i++) {
            Teleport item = loadItem((Element)itemElems.get(i));
            if (item != null) {
                items.add(item);
            }
        }
    }
    
    /*
     * ��һ��teleportԪ�������봫�͵㡣
     */
    private Teleport loadItem(Element elem) {
        Teleport ret = new Teleport();
        ret.name = elem.getAttributeValue("name");
        ret.mapID = Integer.parseInt(elem.getAttributeValue("mapid"));
        ret.x = Integer.parseInt(elem.getAttributeValue("x"));
        ret.y = Integer.parseInt(elem.getAttributeValue("y"));
        List reqElems = elem.getChildren("requirement");
        for (int i = 0; i < reqElems.size(); i++) {
            Shop.BuyRequirement req = Shop.BuyRequirement.load(owner, (Element)reqElems.get(i));
            if (req != null ){
                ret.requirements.add(req);
            }
        }
        return ret;
    }
    
    /**
     * �Ѷ��󱣴浽һ��teleportsetԪ���С�
     */
    public Element save() {
        Element ret = new Element("teleportset");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        ret.addAttribute("description", description);
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }
        
        for (Teleport item : items) {
            ret.addContent(saveItem(item));
        }
        return ret;
    }
    
    /*
     * ��һ����Ʒ���浽teleportԪ���С�
     */
    private Element saveItem(Teleport item) {
        Element ret = new Element("teleport");
        ret.addAttribute("name", item.name);
        ret.addAttribute("mapid", String.valueOf(item.mapID));
        ret.addAttribute("x", String.valueOf(item.x));
        ret.addAttribute("y", String.valueOf(item.y));
        for (Shop.BuyRequirement req : item.requirements) {
            Element elem = req.save();
            if (elem != null) {
                ret.addContent(elem);
            }
        }
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
        for (Teleport teleport : items) {
            String tmp = context.input(teleport.name, "Teleport");
            if (tmp != null) {
                teleport.name = tmp;
                changed = true;
            }
        }
        return changed;
    }
}
