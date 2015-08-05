package com.pip.game.data;

import org.jdom.*;

/**
 * 定义一个地图格式。
 * @author lighthu
 */
public class ClientModel {
    public String id;
    public MapFormat mapFormat;
    
    public void load(ProjectConfig owner, Element elem) {
        id = elem.getAttributeValue("id");
        int mfid = Integer.parseInt(elem.getAttributeValue("map_format"));
        mapFormat = owner.mapFormats.get(mfid);
    }
}
