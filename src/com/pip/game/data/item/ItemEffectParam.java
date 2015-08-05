package com.pip.game.data.item;

import java.util.List;

import org.jdom.Element;

/**
 * һ����Ʒʹ��Ч���Ĳ������塣
 * @author lighthu
 */
public class ItemEffectParam {
    /**
     * �������ƣ���������item.xml��effect��ǩ��������
     */
    public String name;
    /**
     * �����ڱ༭������ʾ������
     */
    public String title;
    /**
     * ��������������
     * ��ͬ���������ͻ��в�ͬ�����ݸ�ʽ��������֧�ֵ��������ͣ�
     *      integer          -  ����
     *      float            -  ������
     *      string           -  �ַ���
     *      location         -  ��ͼλ�ã������ʽΪ����ͼID,����X,����Y(�������ʽ����ѡ)
     *      leveltable       -  ���������õ���ֵ�������ʽΪ��1����ֵ,2����ֵ,3����ֵ........
     *      item             -  ��Ʒ��װ��
     *      choice(xxx,xxx)  -  ѡ��������0��ʾ��һ��ѡ��1��ʾ�ڶ���ѡ���Դ�����
     *      dictobj(<classname>)  -  ѡ��һ���ֵ����ݶ��󣬱������ID
     *      dataobj(<classname>)  -  ѡ��һ�����ݶ��󣬱������ID
     */
    public String dataType;
    /**
     * ������ȱʡֵ
     */
    public String defaultValue;
    
    /**
     * ��XML�ڵ����롣
     * @param elem
     */
    public void load(Element elem) {
        name = elem.getAttributeValue("name");
        title = elem.getAttributeValue("title");
        dataType = elem.getAttributeValue("datatype");
        defaultValue = elem.getAttributeValue("default");
    }
}
