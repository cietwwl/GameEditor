package com.pip.game.editor.property;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;


public class PathCellEditor extends CellEditorAdapter {

    protected int patrolPathId;
    

    public PathCellEditor(Composite parent) {
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
        return new Integer(patrolPathId);
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
        if("Î´Ö¸¶¨".equals(value)) {
            patrolPathId = -1;
        } else {
            String tmp = (String)value;
            patrolPathId = Integer.parseInt((String)value);
        }
        text.setText((String)value);
    }
    
    protected void editText() {
        ChoosePathDialog dlg = new ChoosePathDialog(text.getShell());
        dlg.setPathId(patrolPathId);
        if (dlg.open() == Dialog.OK) {
            patrolPathId = dlg.getPathId();
            text.setText(String.valueOf(patrolPathId));
            fireApplyEditorValue();
            deactivate();
        }
    }
}
