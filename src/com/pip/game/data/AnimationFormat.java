package com.pip.game.data;

import org.jdom.*;

/**
 * 定义一个动画格式。
 * @author lighthu
 */
public class AnimationFormat {
    public int id;
    public String title;
    public double scale;
    public int headWidth;
    public int headHeight;
    public String dirName;
    
    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
        scale = Double.parseDouble(elem.getAttributeValue("scale"));
        headWidth = Integer.parseInt(elem.getAttributeValue("head_width"));
        headHeight = Integer.parseInt(elem.getAttributeValue("head_height"));
        dirName = elem.getAttributeValue("dirname");
    }
}
