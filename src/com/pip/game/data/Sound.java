package com.pip.game.data;

import java.io.File;

import org.jdom.Element;

import com.pip.game.data.i18n.I18NContext;
import com.pipimage.utils.Utils;

/**
 * 声音文件对象。
 * @author lighthu
 */
public class Sound extends DataObject {
    /**
     * 所属项目。
     */
    public ProjectData owner;
    /**
     * 声音文件。
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
     * 查找一个声音的名字。
     * @param project
     * @param buffID
     * @return
     */
    public static String toString(ProjectData project, int soundID) {
        Sound q = (Sound)project.findObject(Sound.class, soundID);
        if (q == null) {
            return "没有声音";
        } else {
            return q.toString();
        }
    }

    /**
     * 对这个对象的属性进行国际化处理，如果有需要国际化的字符串，则提取出来到context中查找翻译结果。
     * @param context
     * @return 如果有某个属性被替换，返回true，否则返回false。
     */
    public boolean i18n(I18NContext context) {
        return false;
    }
}
