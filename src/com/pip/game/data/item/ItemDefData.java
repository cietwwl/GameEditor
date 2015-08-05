package com.pip.game.data.item;


import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.i18n.I18NContext;
import com.pip.util.Utils;

/**
 * 物品数量对象，可用于对物品需求定义
 */
public class ItemDefData extends DataObject {
    /**
     * 消耗的物品id
     */
    public int itemId;
    /**
     * 消耗物品的数量
     */
    public int itemCount;

    public ItemDefData() {
        super();
    }
    
    public ItemDefData(int itemId, int itemCount) {
      this.itemId = itemId;
      this.itemCount = itemCount;
    }

    @Override
    public void update(DataObject obj) {
        ItemDefData oo = (ItemDefData) obj;
        id = oo.id;
        title = oo.title;
        description = oo.description;
        setCategoryName(oo.getCategoryName());
        
        itemId = oo.itemId;
        itemCount = oo.itemCount;
    }

    @Override
    public ItemDefData duplicate() {
        ItemDefData ret = new ItemDefData();
        ret.update(this);
        return ret;
    }

    @Override
    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
        description = elem.getAttributeValue("description");
        setCategoryName(elem.getAttributeValue("category"));
        if (getWholeCategoryName() == null) {
            setCategoryName("");
        }
        
        itemId = Integer.parseInt(elem.getAttributeValue("itemId"));
        itemCount = Integer.parseInt(elem.getAttributeValue("itemCount"));
    }

    @Override
    public Element save() {
        Element ret = new Element("itemDef");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        ret.addAttribute("description", description);
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }
        
        ret.addAttribute("itemId", String.valueOf(itemId));
        ret.addAttribute("itemCount", String.valueOf(itemCount));
        return ret;
    }

    @Override
    public boolean depends(DataObject obj) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean changed(DataObject obj) {
        return itemId != ((ItemDefData) obj).itemId || itemCount != ((ItemDefData) obj).itemCount;
    }

    @Override
    public boolean i18n(I18NContext context) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected ItemDefData clone() {
        ItemDefData ret = new ItemDefData();
        ret.itemId = itemId;
        ret.itemCount = itemCount;
        return ret;
    }

    public static String getDescString(List<ItemDefData> items, boolean showNum){
        if(items == null) return "";
        StringBuffer sbuf = new StringBuffer();
        for(ItemDefData itemnum: items){
            Item item = ProjectData.getActiveProject().findItemOrEquipment(itemnum.itemId);
            if(item!=null){
                sbuf.append(item.toString());
                if(showNum){
                    sbuf.append("(").append(itemnum.itemCount).append(")");
                }
                sbuf.append(";");
            }
        }
        return sbuf.toString();
    }
    

    public static String formatToString(List<ItemDefData> items){
        StringBuffer sbuf = new StringBuffer();
        if(items!=null){
            int[] ret = new int[items.size() * 2];
            int i = 0;
            for(ItemDefData itemnum : items){
                ret[i++] = itemnum.itemId;
                ret[i++] = itemnum.itemCount;
            }
            sbuf.append(Utils.intArrayToString(ret, ','));
        }
        System.out.println(sbuf.toString());
        return sbuf.toString();
    }
    
    public static List<ItemDefData> parseFromString(String str){
        List<ItemDefData> items = new ArrayList<ItemDefData>();
        if(str!=null){
            int[] vArr = Utils.stringToIntArray(str, ',');
            for(int i = 0; i< vArr.length - 1 ;){
                items.add(new ItemDefData(vArr[i++],vArr[i++]));
            }
        }
        return items;
    }
}
