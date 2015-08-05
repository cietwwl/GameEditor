package com.pip.game.editor;

import org.eclipse.swt.widgets.Composite;


/**
 * ͨ�����ݶ���༭�ؼ��ӿڡ�
 */
public abstract class AbstractDataObjectEditor extends Composite {
    protected DefaultDataObjectEditor owner;
    
    public AbstractDataObjectEditor(Composite parent, int style, DefaultDataObjectEditor owner) {
        super(parent, style);
        this.owner = owner;
    }
    
    /**
     * �ѽ����ϵ�ǰ�����ֵ���浽�༭�����С�
     * @throws Exception
     */
    public abstract void save() throws Exception;
    
    /**
     * �ѱ༭�����ֵ���õ������С�
     * @throws Exception
     */
    public abstract void load() throws Exception;
}


