package com.pip.game.data;

import org.jdom.*;

import com.pip.game.data.i18n.I18NContext;

/**
 * 阵营定义。
 * @author lighthu
 */
public class Faction extends DataObject {
    public String toString() {
        return id + ": " + title;
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
    }
    
    public Element save() {
        Element ret = new Element("faction");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        ret.setText(description);
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
        String tmp = context.input(title, "Faction");
        if (tmp != null) {
            title = tmp;
            return true;
        } else {
            return false;
        }
    }
}
