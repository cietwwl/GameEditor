package com.pip.game.data;


/**
 * ����װ�����ԵĽӿڡ�ÿ����Ŀ���뿪��һ��ʵ�ִ˽ӿڵ���������װ���ļ�ֵ�����ԡ�
 * װ���������Ե���ؼ��㹫ʽΪ��
 *    װ�����Լ�ֵ = SUM(����ֵ*�������Լ�ֵ) + ��������
 *    װ�����Լ�ֵ = װ���ȼ� * (Ʒ��ϵ�� + ����Ʒ��ϵ��) * ��ֵ����
 * @author lighthu
 */
public interface IDataCalculator {
    /**
     * ����װ���������Լ�ֵ��
     * @param dataObject װ������
     * @return װ���ļ�ֵ����
     */
    float getValue(DataObject dataObject);
    
    /**
     * ����װ����ʾ��ֵ��
     * 
     * ybai������װ��������ʾ�ļ�ֵ��ʽ�õĲ���װ���������ļ�ֵ�����ԼӴ˽ӿ�
     * 
     * @param dataObject װ������
     * @return װ������ʾ��ֵ����
     */
    public float getShownValue(DataObject dataObject);
    
    /**
     * �����Ѿ��޸ĺ��װ������ֵ������װ���ĸ���Ʒ��ϵ����
     * @param dataObject һ��Equipment�������е�appendAttributes�Ѿ����ֹ��޸�
     */
    void calculateExtraQuality(DataObject dataObject);
    /**
     * ����װ���ı�׼�ۼۡ�
     * @param dataObject װ������
     * @return ��׼�۸�
     */ 
    int getPrice(DataObject dataObject);
    /**
     * ����װ��������;öȡ�
     * @param dataObject װ������
     * @return �;ö�
     */
    int getDurability(DataObject dataObject);
    /**
     * ȡ��ĳ��Ʒ�ʶ�Ӧ������Ʒ��ϵ����
     * @param quality װ��Ʒ��
     * @return Ʒ��ϵ��
     */
    float getQualityAddTion(int quality);
    /**
     * �ڰ��ձ�׼��ʽ�����װ������֮���ٸ�����Ŀ�ص����ֵ�������������߼����һЩ�������ԡ�
     * @param dataObject װ������
     */
    void adjustAttributes(DataObject dataObject);
    /**
     * ����ĳ��װ���ϵ�ĳ�����Եļ�ֵ��ĳЩ���Եļ�ֵ�Ǹ���װ���ȼ���̬�ı�ġ�
     * @param attrID ����ID
     * @param dataObject װ������
     * @return ����ʵ�ʼ�ֵ 
     */
    float getAttributeValue(int attrID, DataObject dataObject);
    /**
     * ���㱦ʯ�ĸ������Լ�ֵ��
     * @param level ��ʯ����Ʒ�ȼ�
     * @return ��ʯ�ļ�ֵ����
     */
    float getJewelValue(int level);
}
