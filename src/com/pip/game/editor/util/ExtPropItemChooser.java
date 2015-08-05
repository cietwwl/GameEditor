/**
 * 
 */
package com.pip.game.editor.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;

/**
 * @author jhkang
 *
 */
public class ExtPropItemChooser extends ExtProp {

    private ItemChooser itemChooser;

    @Override
    public void createControl(Composite container) {
        super.createControl(container);
        itemChooser = new ItemChooser(container, SWT.NONE);
        itemChooser.setModifyListener(new ModifyListener(){

            public void modifyText(ModifyEvent e) {
                setDirty(true);
            }
        });
        if(value == null || value.equals("")){
        }else{
            setValue(value);
        }
    }

    @Override
    public String getValue() {
        if(itemChooser!=null && !itemChooser.isDisposed()){
            return itemChooser.getItemID()+"";
        }
        return value.toString();
    }

    @Override
    public boolean setValue(String v) {
        itemChooser.setItemID(Integer.parseInt(v));
        return true;
    }

    @Override
    public ExtProp duplicate() {
        ExtPropItemChooser p = new ExtPropItemChooser();
        this.copyTo(p);
        return p;
    }

}
