package com.pip.game.data.equipment;

/**
 * װ����һ���������Ե�������
 * @author lighthu
 */
public class EquipmentAttribute {
    /**
     * ����ID
     */
    public String id;
    /**
     * ��������
     */
    public String name;
    /**
     * ��д
     */
    public String shortName;
    /**
     * ÿ1�������ռ�õ�װ�����Ӽ�ֵ
     */
    public float value;
    
    public EquipmentAttribute(String id, String name, String shortName, float value) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.value = value;
    }
}
