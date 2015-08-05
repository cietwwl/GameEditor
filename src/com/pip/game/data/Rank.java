package com.pip.game.data;

import org.jdom.*;

import com.pip.game.data.i18n.I18NContext;

/**
 * ���ζ��塣
 * @author lighthu
 */
public class Rank extends DataObject {
    /**
     * ��ô˾��ε��������ֵ��������
     */
    public int minHonor;
    /**
     * ��ô˾��ε������������������0��ʾ��һ��
     */
    public int maxSeq;
    /**
     * ��ô˾��ε�������������������������磺5��ʾ����ǰ5%�����ܹ���ô˾��Ρ�
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
     * �������������Խ��й��ʻ������������Ҫ���ʻ����ַ���������ȡ������context�в��ҷ�������
     * @param context
     * @return �����ĳ�����Ա��滻������true�����򷵻�false��
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
