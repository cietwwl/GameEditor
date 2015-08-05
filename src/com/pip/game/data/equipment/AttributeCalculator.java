package com.pip.game.data.equipment;

import java.io.File;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.util.Utils;

/**
 * 管理装备属性配置。
 * @author lighthu
 */
public class AttributeCalculator {
    /*
     * 可修改的属性。
     */
    public EquipmentAttribute[] ATTRIBUTES;
    
    /**
     * 从Item目录下的equipAttributes.xml中读取数据初始化EquipmentAttribute[]
     * @return
     */
    public AttributeCalculator(ProjectConfig config) {
        File filePath = config.getOwner().baseDir;
        Document doc = null;
        try {
            doc = Utils.loadDOM(new File(filePath,"Items"+ File.separator + "equipAttribute.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Element eleRoot = doc.getRootElement();
        
        List list = eleRoot.getChildren("equipAttributes");
        int attriCnt = list.size();
        EquipmentAttribute[] equipAttributes = new EquipmentAttribute[attriCnt];
        
        for(int i=0;i<attriCnt;i++){
            Element ele = (Element) list.get(i);
            
            String key = ele.getAttributeValue("key");
            String fullName= ele.getAttributeValue("fullName");
            String shortName= ele.getAttributeValue("shortName");
            String w = ele.getAttributeValue("weight");
            float weight=Float.parseFloat(w);
            
            EquipmentAttribute ea = new EquipmentAttribute(key,fullName,shortName,weight);
            equipAttributes[i] =  ea;
        }
        ATTRIBUTES = equipAttributes;
    }
    
    /**
     * 查找指定属性的索引。
     * @param id
     * @return
     */
    public int findIndexOfAttribute(String id) {
        for (int i = 0; i < ATTRIBUTES.length; i++) {
            if (ATTRIBUTES[i].id.equals(id)) {
                return i;
            }
        }
        return -1;
    }
}
