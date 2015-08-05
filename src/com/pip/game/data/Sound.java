package com.pip.game.data;

import java.io.File;

import org.jdom.Element;

import com.pip.game.data.i18n.I18NContext;
import com.pipimage.utils.Utils;

/**
 * �����ļ�����
 * @author lighthu
 */
public class Sound extends DataObject {
    /**
     * ������Ŀ��
     */
    public ProjectData owner;
    /**
     * �����ļ���
     */
    public java.io.File source;

    public Sound(ProjectData owner) {
        this.owner = owner;
    }

    public int getID() {
        return id;
    }

    public boolean equals(Object o){
        return this == o;
    }
    
    public String toString() {
        return id + ": " + title;
    }

    public void update(DataObject obj) {
        Sound oo = (Sound)obj;
        id = oo.id;
        source = oo.source;
        title = oo.title;
        description = oo.description;
        setCategoryName(oo.getCategoryName());
    }
    
    public DataObject duplicate() {
        Sound ret = new Sound(owner);
        ret.update(this);
        return ret;
    }

    @Override
    public boolean changed(DataObject obj) {
        Sound oo = (Sound)obj;
        return !source.equals(oo.source);
    }
    
    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        String sourceName = elem.getAttributeValue("source");
        if (sourceName != null) {
            source = new java.io.File(owner.baseDir, "Sounds/" + sourceName);
        }
        title = elem.getAttributeValue("title");
        description = elem.getAttributeValue("description");
        if(description == null){
            description = "";
        }
        setCategoryName(elem.getAttributeValue("category"));
        if (getWholeCategoryName() == null) {
            setCategoryName("");
        }
    }
    
    private String toRelative(String path) {
        String basePath = new java.io.File(owner.baseDir, "Sounds").getAbsolutePath();
        path = path.substring(basePath.length() + 1);
        path = path.replace('\\', '/');
        return path;
    }
    
    public Element save() {
        Element ret = new Element("sound");
        ret.addAttribute("id", String.valueOf(id));
        if (source != null) {
            ret.addAttribute("source", toRelative(source.getAbsolutePath()));
        }
        ret.addAttribute("title", title);
        ret.addAttribute("description", description);
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }
        return ret;
    }
    
    public boolean depends(DataObject obj) {
        return false;
    }

    /**
     * ����һ�����������֡�
     * @param project
     * @param buffID
     * @return
     */
    public static String toString(ProjectData project, int soundID) {
        Sound q = (Sound)project.findObject(Sound.class, soundID);
        if (q == null) {
            return "û������";
        } else {
            return q.toString();
        }
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
