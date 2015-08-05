package com.pip.game.data.item;

import java.util.List;

import org.jdom.Element;

/**
 * һ����ƷЧ�������á�
 */
public class ItemEffectConfig {
    /**
     * Ч�������ͱ�ţ���ID��item.xml��effect��ǩ��type����ֵ
     */
    public int id;
    /**
     * ��Ч���ļ������ֱ���
     */
    public String name;
    /**
     * �ڱ༭������ʾ��Ч������
     */
    public String title;
    /**
     * ��Ч����ʵ����
     */
    public String implClass;
    /**
     * ��Ч���Ĳ���
     */
    public ItemEffectParam[] paramDefs;
    
    /**
     * ��XML�ڵ����롣
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
     * �������ֲ������Զ��塣
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
