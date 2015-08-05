package com.pip.game.data.item;

import java.util.List;

import org.jdom.Element;

/**
 * 一个物品效果的配置。
 */
public class ItemEffectConfig {
    /**
     * 效果的类型编号，此ID是item.xml里effect标签的type属性值
     */
    public int id;
    /**
     * 此效果的简略文字编码
     */
    public String name;
    /**
     * 在编辑器中显示的效果类型
     */
    public String title;
    /**
     * 此效果的实现类
     */
    public String implClass;
    /**
     * 此效果的参数
     */
    public ItemEffectParam[] paramDefs;
    
    /**
     * 从XML节点载入。
     * @param elem
     */
    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        name = elem.getAttributeValue("name");
        title = elem.getAttributeValue("title");
        implClass = elem.getAttributeValue("implclass");
        
        List list = elem.getChildren("param");
        paramDefs = new ItemEffectParam[list.size()];
        for (int i = 0; i < list.size(); i++) {
            paramDefs[i] = new ItemEffectParam();
            paramDefs[i].load((Element)list.get(i));
        }
    }
    
    /**
     * 根据名字查找属性定义。
     * @param name
     * @return
     */
    public ItemEffectParam getParamDef(String name) {
        for (ItemEffectParam param : paramDefs) {
            if (param.name.equals(name)) {
                return param;
            }
        }
        return null;
    }
}
