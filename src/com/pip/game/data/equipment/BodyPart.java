package com.pip.game.data.equipment;

import org.jdom.Element;

/**
 * 装备部位,注意不是挂接点
 * @author jhkang
 *
 */
public class BodyPart {
    public String name;
    public int typeId;
    /**
     * 装备动画所在的目录名称
     */
    public String dirKey;
    public int[] placeImageStep;
    public float weigth;
    public void load(Element elem){
        name = elem.getAttributeValue("name");
        dirKey = elem.getAttributeValue("key");
        String str = elem.getAttributeValue("weight");
        if(str!=null && str.equals("")==false){
            weigth = Float.parseFloat(str); 
        }
        str = elem.getAttributeValue("typeId");
        if(str!=null && str.equals("")==false){
            typeId = Integer.parseInt(str); 
        }
        str = elem.getAttributeValue("step");
        if(str!=null && str.equals("")==false){
            String[] vs = str.split(",");
            int j = 0;
            placeImageStep = new int[vs.length];
            for(String v:vs){
                placeImageStep[j] = Integer.parseInt(v); 
                j++;
            }
        }
    }
}
