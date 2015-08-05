package com.pip.game.data;

import org.jdom.*;

import com.pip.game.data.i18n.I18NContext;

/**
 * ��һ��Ҷ��塣
 * @author lighthu
 */
public class Currency extends DataObject {
    // �������ͻ���
    public static final int CURRENCY_NUMBER = 1;
    // dict�������ͻ���
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
     * �������������Խ��й��ʻ������������Ҫ���ʻ����ַ���������ȡ������context�в��ҷ�������
     * @param context
     * @return �����ĳ�����Ա��滻������true�����򷵻�false��
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
