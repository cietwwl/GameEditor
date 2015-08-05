package com.pip.game.editor.property;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;

/**
 * 地图内框选一个矩形
 * @author ybai
 *
 */
public class SelectMapAreaCellEditor extends CellEditorAdapter {

    public int[] area = new int[5];

    public SelectMapAreaCellEditor(Composite parent) {
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
        return area;
    }

    /**
     * The <code>TextCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method accepts
     * a text string (type <code>String</code>).
     *
     * @param value a text string (type <code>String</code>)
     */
    protected void doSetValue(Object value) {
        Assert.isTrue(text != null && (value instanceof int[]));
        area = (int[])value;
            
        text.setText(area[1] + "," + area[2] + "," + area[3] + "," + area[4]);
    }
    
    protected void editText() {
        SelectMapAreaPathDialog dlg = new SelectMapAreaPathDialog(text.getShell());
        dlg.setArea(area);
        if (dlg.open() == Dialog.OK) {
            area = dlg.getArea();
            text.setText(area[1] + "," + area[2] + "," + area[3] + "," + area[4]);
            fireApplyEditorValue();
            deactivate();
        }
    }
}
