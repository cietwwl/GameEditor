/**
 * 
 */
package com.pip.game.editor.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.jdom.Element;

/**
 * @author jhkang
 *
 */
public class ExtPropSpinner extends ExtProp{

    private int minValue;
    private Spinner spinner;
    
    /**
     * 增长最大数 
     */
    private int maxValue;
    /**
     * 增长步长
     */
    private int increment;
    @Override
    public void createControl(Composite container) {
        super.createControl(container);
        spinner = new Spinner(container, SWT.BORDER);
        spinner.addModifyListener(new ModifyListener(){

            public void modifyText(ModifyEvent e) {
                setDirty(true);
            }
            
        });
        spinner.setMaximum(maxValue);
        spinner.setIncrement(increment);
        spinner.setPageIncrement(increment);
        if(value == null || value.equals("")){
            spinner.setMinimum(minValue);
        }else{
            setValue(value);
        }
    }

    @Override
    public void load(Element prop) {
        super.load(prop);
        minValue = Integer.parseInt(prop.getAttributeValue("minValue"));
        maxValue = Integer.parseInt(prop.getAttributeValue("maxValue"));
        increment = Integer.parseInt(prop.getAttributeValue("increment"));
    }

    @Override
    public String getValue() {
        if(spinner !=null && !spinner.isDisposed()){
            return spinner.getText();
        }
        return value;
    }

    @Override
    public boolean setValue(String v) {
        int sel = Integer.parseInt(v);
        spinner.setSelection(sel);
        return true;
    }

    @Override
    public ExtProp duplicate() {
        ExtPropSpinner p = new ExtPropSpinner();
        this.copyTo(p);
        p.minValue = this.minValue;
        p.maxValue = this.maxValue;
        p.increment = this.increment;
        return p;
    }

    
}
