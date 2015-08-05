package com.pip.game.data;


/**
 * 计算装备属性的接口。每个项目必须开发一个实现此接口的类来计算装备的价值和属性。
 * 装备附加属性的相关计算公式为：
 *    装备属性价值 = SUM(属性值*单点属性价值) + 附加属性
 *    装备属性价值 = 装备等级 * (品质系数 + 附加品质系数) * 价值常量
 * @author lighthu
 */
public interface IDataCalculator {
    /**
     * 计算装备附加属性价值。
     * @param dataObject 装备对象
     * @return 装备的价值点数
     */
    float getValue(DataObject dataObject);
    
    /**
     * 计算装备显示价值。
     * 
     * ybai：由于装备动画显示的价值公式用的不是装备的真正的价值，所以加此接口
     * 
     * @param dataObject 装备对象
     * @return 装备的显示价值点数
     */
    public float getShownValue(DataObject dataObject);
    
    /**
     * 根据已经修改后的装备属性值，计算装备的附加品质系数。
     * @param dataObject 一个Equipment对象，其中的appendAttributes已经被手工修改
     */
    void calculateExtraQuality(DataObject dataObject);
    /**
     * 计算装备的标准售价。
     * @param dataObject 装备对象
     * @return 标准价格
     */ 
    int getPrice(DataObject dataObject);
    /**
     * 计算装备的最大耐久度。
     * @param dataObject 装备对象
     * @return 耐久度
     */
    int getDurability(DataObject dataObject);
    /**
     * 取得某个品质对应的属性品质系数。
     * @param quality 装备品质
     * @return 品质系数
     */
    float getQualityAddTion(int quality);
    /**
     * 在按照标准公式计算出装备属性之后，再根据项目特点对数值进行修正，或者计算出一些附加属性。
     * @param dataObject 装备对象
     */
    void adjustAttributes(DataObject dataObject);
    /**
     * 计算某个装备上的某个属性的价值。某些属性的价值是根据装备等级动态改变的。
     * @param attrID 属性ID
     * @param dataObject 装备对象
     * @return 属性实际价值 
     */
    float getAttributeValue(int attrID, DataObject dataObject);
    /**
     * 计算宝石的附加属性价值。
     * @param level 宝石的物品等级
     * @return 宝石的价值点数
     */
    float getJewelValue(int level);
}
