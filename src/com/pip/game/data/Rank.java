package com.pip.game.data;

import org.jdom.*;

import com.pip.game.data.i18n.I18NContext;

/**
 * 军衔定义。
 * @author lighthu
 */
public class Rank extends DataObject {
    /**
     * 获得此军衔的最低荣誉值（包含）
     */
    public int minHonor;
    /**
     * 获得此军衔的最低排名（包含），0表示第一名
     */
    public int maxSeq;
    /**
     * 获得此军衔的最低排名比例（包含），例如：5表示排名前5%的人能够获得此军衔。
     */
    public float maxPercent;
    
    public String toString() {
        return title;
    }
        
    public void update(DataObject obj) {}
    
    public DataObject duplicate() {
        return null;
    }

    @Override
    public boolean changed(DataObject obj) {
        return changed(this, obj);
    }
    
    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
        description = elem.getText();
        minHonor = Integer.parseInt(elem.getAttributeValue("minhonor"));
        maxSeq = Integer.parseInt(elem.getAttributeValue("maxseq"));
        maxPercent = Float.parseFloat(elem.getAttributeValue("maxpercent"));
    }
    
    public Element save() {
        Element ret = new Element("rank");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        ret.setText(description);
        ret.addAttribute("minhonor", String.valueOf(minHonor));
        ret.addAttribute("maxseq", String.valueOf(maxSeq));
        ret.addAttribute("maxpercent", String.valueOf(maxPercent));
        return ret;
    }
    
    public boolean depends(DataObject obj) {
        return false;
    }
    
    /**
     * 对这个对象的属性进行国际化处理，如果有需要国际化的字符串，则提取出来到context中查找翻译结果。
     * @param context
     * @return 如果有某个属性被替换，返回true，否则返回false。
     */
    public boolean i18n(I18NContext context) {
        String tmp = context.input(title, "Rank");
        if (tmp != null) {
            title = tmp;
            return true;
        } else {
            return false;
        }
    }
}
