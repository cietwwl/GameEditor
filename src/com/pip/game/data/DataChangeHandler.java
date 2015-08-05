package com.pip.game.data;

/**
 * �����޸ļ����������ڷ�������������ʱ�����޸ĵ����ݡ�
 * @author lighthu
 */
public interface DataChangeHandler {
    /**
     * ����¶���֪ͨ��
     * @param obj ����ӵĶ���
     */
    void dataObjectAdded(DataObject obj);
    /**
     * ����ɾ��֪ͨ��
     * @param obj ��ɾ�����϶���
     */
    void dataObjectRemoved(DataObject obj);
    /**
     * ���󼴽����޸�֪ͨ��
     * @param obj �޸�ǰ�Ķ���
     */
    void dataObjectChanging(DataObject obj);
    /**
     * �����޸�֪ͨ��
     * @param obj �޸ĺ���¶���
     */
    void dataObjectChanged(DataObject obj);
}
