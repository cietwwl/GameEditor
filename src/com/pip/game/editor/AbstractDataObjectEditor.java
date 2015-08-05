package com.pip.game.editor;

import org.eclipse.swt.widgets.Composite;


/**
 * 通用数据对象编辑控件接口。
 */
public abstract class AbstractDataObjectEditor extends Composite {
    protected DefaultDataObjectEditor owner;
    
    public AbstractDataObjectEditor(Composite parent, int style, DefaultDataObjectEditor owner) {
        super(parent, style);
        this.owner = owner;
    }
    
    /**
     * 把界面上当前输入的值保存到编辑对象中。
     * @throws Exception
     */
    public abstract void save() throws Exception;
    
    /**
     * 把编辑对象的值设置到界面中。
     * @throws Exception
     */
    public abstract void load() throws Exception;
}


