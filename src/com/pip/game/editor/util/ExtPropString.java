/**
 * 
 */
package com.pip.game.editor.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.jdom.Element;

/**
 * @author jhkang
 *
 */
public class ExtPropString extends ExtProp {
    static boolean textWidth;
    public ExtPropString(){
        
    }
    @Override
    public void load(Element prop) {
        if(Integer.parseInt(prop.getAttributeValue("textWidth"))==1){
            ExtPropString.textWidth=true;
         }
        super.load(prop);
        
       
    }

    private Text textInput;

    /* (non-Javadoc)
     * @see com.pip.game.editor.util.ExtProp#getValue()
     */
    @Override
    public String getValue() {
        if(textInput!= null && !textInput.isDisposed()){
            return textInput.getText().trim();
        }
        return value;
    }

    /* (non-Javadoc)
     * @see com.pip.game.editor.util.ExtProp#setValue(java.lang.String)
     */
    @Override
    public boolean setValue(String v) {
        textInput.setText(v);
        textInput.setToolTipText(v);
        return false;
    }

    @Override
    public void createControl(Composite container) {
        super.createControl(container);
        textInput = new Text(container, SWT.BORDER);
        textInput.setSize(textInput.computeSize(60, -1));
        textInput.addModifyListener(new ModifyListener(){

            public void modifyText(ModifyEvent e) {
                setDirty(true);
            }
            
        });
        setValue(value);
        //////////

        if(ExtPropString.textWidth){
            RowData data = new RowData(60, -1);
            textInput.setLayoutData(data);
        }
    }

    @Override
    public ExtProp duplicate() {
        ExtPropString p = new ExtPropString();
        this.copyTo(p);
        return p;
    }

}
