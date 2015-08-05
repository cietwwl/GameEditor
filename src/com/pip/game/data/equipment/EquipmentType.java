package com.pip.game.data.equipment;

import java.util.LinkedHashMap;

import org.jdom.Element;

public class EquipmentType {
    public String name;
    public int id;
    
    public void load(Element elem){
        name = elem.getAttributeValue("name");
        String str = elem.getAttributeValue("idx");
        if(str!=null && str.equals("")==false){
            id = Integer.parseInt(str); 
        }
    }
    
    public static LinkedHashMap<Integer, EquipmentType> equipmentTypes = new LinkedHashMap<Integer, EquipmentType>();
}
