package com.pip.game.data.talent;

import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.i18n.I18NContext;

public class TalentTree extends DataObject {
    private Element rootElement;

    public TalentTree(ProjectData projectData) {

    }

    @Override
    public boolean changed(DataObject obj) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean depends(DataObject obj) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public DataObject duplicate() {
        TalentTree copy = new TalentTree(null);
        copy.rootElement = this.rootElement;
        return copy;
    }

    @Override
    public void load(Element elem) {
        this.title = elem.getAttributeValue("name");
        this.id = Integer.parseInt(elem.getAttributeValue("id"));
        setCategoryName(elem.getAttributeValue("category"));
        if (getWholeCategoryName() == null) {
            setCategoryName("");
        }
        rootElement = new Element(elem.getName());
        for(Attribute att:(List<Attribute>)elem.getAttributes()){
            rootElement.addAttribute(att);
        }
        rootElement.setChildren(elem.getChildren());
    }

    @Override
    public Element save() {
        if(rootElement == null){
            rootElement = new Element("TalentTree");
            rootElement.addAttribute("id", String.valueOf(id));
            rootElement.addAttribute("name", title);
            rootElement.addAttribute("treePoints","0");
            if (getWholeCategoryName() != null) {
                rootElement.addAttribute("category", getWholeCategoryName());
            }
        }
        Element copy = new Element(rootElement.getName());
        for(Attribute att:(List<Attribute>)rootElement.getAttributes()){
            copy.addAttribute(att);
        }
        copy.setChildren(rootElement.getChildren());
        return copy;
    }

    @Override
    public void update(DataObject obj) {
        // TODO Auto-generated method stub

    }

    public Element getRootElement() {
        return rootElement;
    }

    public void setRootElement(Element root) {
        rootElement = root;
    }

    /**
     * �������������Խ��й��ʻ������������Ҫ���ʻ����ַ���������ȡ������context�в��ҷ�������
     * @param context
     * @return �����ĳ�����Ա��滻������true�����򷵻�false��
     */
    public boolean i18n(I18NContext context) {
        return false;
    }
}
