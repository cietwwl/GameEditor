package com.pip.game.editor.skill;

/**
 * PropertySheetEntry�ؼ������޸��¼���Ϣʱ�䴦��
 * 
 * @author Administrator
 */
public interface IValueChanged {
    
    /**
     * ����PropertySheet�е�ֵ�����仯ʱ����������Ϣ
     */
    public void valueChanged(String id);
    
    /**
     * ����PropertySheet�е�ֵ��������ʱ����������Ϣ
     */
    public void valueError(String errorMessage);
}
