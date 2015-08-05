package com.pip.game.data;

import org.jdom.*;

import com.pip.game.data.i18n.I18NContext;

/**
 * 玩家货币定义。
 * @author lighthu
 */
public class Currency extends DataObject {
    // 数字类型货币
    public static final int CURRENCY_NUMBER = 1;
    // dict对象类型货币
    public static final int CURRENCY_DICTOBJECT = 2;
    
    public int type = CURRENCY_NUMBER;
    public Class dictObjectClass;
    
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
        type = Integer.parseInt(elem.getAttributeValue("type"));
        if (type == CURRENCY_DICTOBJECT) {
            try {
                dictObjectClass = Class.forName(elem.getAttributeValue("dictobjectclass"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public Element save() {
        Element ret = new Element("currency");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        ret.addAttribute("type", String.valueOf(type));
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
        String tmp = context.input(title, "Currency");
        if (tmp != null) {
            title = tmp;
            return true;
        } else {
            return false;
        }
    }
}
