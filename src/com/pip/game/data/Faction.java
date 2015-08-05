package com.pip.game.data;

import org.jdom.*;

import com.pip.game.data.i18n.I18NContext;

/**
 * ��Ӫ���塣
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
     * �������������Խ��й��ʻ������������Ҫ���ʻ����ַ���������ȡ������context�в��ҷ�������
     * @param context
     * @return �����ĳ�����Ա��滻������true�����򷵻�false��
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
