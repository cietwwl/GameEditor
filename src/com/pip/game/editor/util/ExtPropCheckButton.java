package com.pip.game.editor.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.jdom.Element;

public class ExtPropCheckButton extends ExtProp {

    private Button checkButton;

    // private EffectSetHurtFormula effectSetHurtFormula;
    /**
     * 
     */
    public ExtPropCheckButton() {
        // TODO Auto-generated constructor stub
    }

    public void load(Element prop) {

        // TODO Auto-generated method stub
        super.load(prop);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pip.game.editor.util.ExtProp#duplicate()
     */
    @Override
    public ExtProp duplicate() {
        ExtPropCheckButton p = new ExtPropCheckButton();
        this.copyTo(p);
        return p;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pip.game.editor.util.ExtProp#getValue()
     */
    @Override
    public String getValue() {
        if (checkButton != null && !checkButton.isDisposed()) {
            return String.valueOf(checkButton.getSelection());
        }
        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pip.game.editor.util.ExtProp#setValue(java.lang.String)
     */
    @Override
    public boolean setValue(String v) {
        if(v.equals("true")){
            this.checkButton.setSelection(true);
        }else{
            this.checkButton.setSelection(false);
        }
        return true;
    }

    @Override
    public void createControl(Composite container) {
        // super.createControl(container);
        this.checkButton = new Button(container, SWT.CHECK);
        this.checkButton.setText(this.label);
        checkButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                setDirty(true);
            }

        });
        setValue(value);

    }
}