package com.pip.game.editor.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jdom.Element;

import com.pip.game.data.extprop.ExtPropEntry;
import com.pip.game.editor.DefaultDataObjectEditor;

/**
 * 扩展属性支持工具;<br/>
 * 通过配置文件自动生成属性编辑控件,提供修改属性值的功能<br/>
 * @author jhkang
 *
 */
public abstract class ExtProp extends ExtPropEntry {
    public String label;
    
    private DefaultDataObjectEditor listener;
    
    /**
     * 默认值
     */
    protected String defaultValue;
    
    /**
     * Only create label
     * @param container
     */
    public void createControl(Composite container){
        if(label != null && !label.equals("")){
            Label l = new Label(container, SWT.NONE);
            l.setText(label);
        }
    }

    public void setDirty(boolean t){
        if(this.listener!=null){
            this.listener.setDirty(t);
            value = getValue();
        }
    }
    
    public void setHandler(DefaultDataObjectEditor handle) {
        this.listener = handle;
    }
    
    public void setLoadedValue(String v){
        value = v;
    }
    
    public abstract String getValue();
    
    /**
     * 复制基本属性
     * @param o
     */
    protected void copyTo(ExtProp o){
        o.label = this.label;
        o.key = this.key;
        o.value = defaultValue;
    }
    
    /**
     * 
     * @return true if successfully set value
     */
    public abstract boolean setValue(String v);
    
    public abstract ExtProp duplicate();

    /**
     * load 配置文件中定义的属性, 或者数据文件中的值
     */
    @Override
    public void load(Element prop) {
        super.load(prop);
        label = prop.getAttributeValue("label");
        defaultValue = prop.getAttributeValue("defaultValue");
    }
}
