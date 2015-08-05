package com.pip.game.data.item;

import java.util.List;

import org.jdom.Element;

/**
 * 一个物品使用效果的参数定义。
 * @author lighthu
 */
public class ItemEffectParam {
    /**
     * 参数名称，此名称是item.xml里effect标签的属性名
     */
    public String name;
    /**
     * 参数在编辑器中显示的名称
     */
    public String title;
    /**
     * 参数的数据类型
     * 不同的数据类型会有不同的数据格式，下面是支持的数据类型：
     *      integer          -  整数
     *      float            -  浮点数
     *      string           -  字符串
     *      location         -  地图位置，保存格式为：地图ID,像素X,像素Y(条件表达式，可选)
     *      leveltable       -  按级别配置的数值表，保存格式为：1级数值,2级数值,3级数值........
     *      item             -  物品或装备
     *      choice(xxx,xxx)  -  选择索引，0表示第一个选择，1表示第二个选择，以此类推
     *      dictobj(<classname>)  -  选择一个字典数据对象，保存对象ID
     *      dataobj(<classname>)  -  选择一个数据对象，保存对象ID
     */
    public String dataType;
    /**
     * 参数的缺省值
     */
    public String defaultValue;
    
    /**
     * 从XML节点载入。
     * @param elem
     */
    public void load(Element elem) {
        name = elem.getAttributeValue("name");
        title = elem.getAttributeValue("title");
        dataType = elem.getAttributeValue("datatype");
        defaultValue = elem.getAttributeValue("default");
    }
}
