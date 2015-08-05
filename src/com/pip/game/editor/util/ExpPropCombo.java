package com.pip.game.editor.util;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.jdom.Element;

public class ExpPropCombo extends ExtProp {
    private List ops;
    private int minValue;
    private Combo combo;
    private String[] effectElem;
    private String[] effect;
    public ExpPropCombo() {
       
    }
    @Override
    public void createControl(Composite container) {
        super.createControl(container);
        
        combo = new Combo(container, SWT.READ_ONLY);
        combo.setVisibleItemCount(10);
        combo.setItems(effectElem);
       
        combo.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
               
            }

        });
        if (value == null || value.equals("")) {
//            combo.setItems("");
        }
        else {
            setValue(value);
        }
        
    }
    
    @Override
    public void load(Element prop) {

        // TODO Auto-generated method stub
        super.load(prop);
        ops = prop.getChildren("option");
        effectElem = new String[ops.size()];
        for (int i = 0; i < ops.size(); i++) {
            Element op = (Element) ops.get(i);
            effectElem[i] = op.getAttributeValue("name");
        }
//        
//        List<Element> opeffect = prop.getChildren("option");
//        
//        for (int i = 0; i < opeffect.size(); i++) {
//            Element opb = (Element) opeffect.get(i);
//            effect[i] = opb.getAttributeValue("name");
//        }
        
    }
    @Override
    public String getValue() {
        if (combo != null && !combo.isDisposed()) {
            return combo.getSelectionIndex()+"";
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
        combo.select((Integer.parseInt(v)));
        return true;
    }
    
    
    /*
     * (non-Javadoc)
     * 
     * @see com.pip.game.editor.util.ExtProp#duplicate()
     */
    @Override
    public ExtProp duplicate() {
        ExpPropCombo p = new ExpPropCombo();
        this.copyTo(p);
        p.effectElem=this.effectElem;
        return p;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pip.game.editor.util.ExtProp#getValue()
     */
   

    

   

}
