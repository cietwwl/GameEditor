package com.pip.game.data;

import org.jdom.*;

/**
 * 定义一个地图格式。
 * @author lighthu
 */
public class MapFormat {
    public int id;
    public String title;
    public double scale;
    public String pkgName;
    public AnimationFormat aniFormat;
    
    public void load(ProjectConfig owner, Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
        scale = Double.parseDouble(elem.getAttributeValue("scale"));
        pkgName = elem.getAttributeValue("pkgname");
        int afid = Integer.parseInt(elem.getAttributeValue("animation_format"));
        aniFormat = owner.animationFormats.get(afid);
    }
    
    public String toString() {
        return title;
    }
}
