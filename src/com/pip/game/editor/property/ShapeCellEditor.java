package com.pip.game.editor.property;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;

import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapInfo;
import com.pip.util.Utils;

/**
 * ·¶Î§±à¼­Æ÷
 */
public class ShapeCellEditor extends CellEditorAdapter {
    
    protected ShapeData area; 
            
    public ShapeCellEditor(Composite parent) {
        super(parent);
    }

    /**
     * The <code>TextCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method returns
     * the text string.
     *
     * @return the text string
     */
    protected Object doGetValue() {
        String value =  ShapeData.formatShapeDataToString(area);
        return value == null?"":value;
    }
    
    /**
     * The <code>TextCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method accepts
     * a text string (type <code>String</code>).
     *
     * @param value a text string (type <code>String</code>)
     */
    protected void doSetValue(Object value) {
        Assert.isTrue(text != null && (value instanceof String));
        if(value==null){
            value = "";
        }
        area = ShapeData.parseShapeDataFromString((String)value);
        if(area!=null){
            text.setText(area.toString());
        }else{
            text.setText("");
        }
    }

    protected void editText() {
        ChooseShapeDialog dlg = new ChooseShapeDialog(text.getShell());
        if(area!=null){
        	dlg.setArea(area);
        }
        if (dlg.open() == Dialog.OK) {
            area = dlg.getArea();
            if(area!=null){
                text.setText(area.toString());
            }else{
                text.setText("");
            }
            fireApplyEditorValue();
            deactivate();
        }
    }
}
