package com.pip.game.editor.property;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;

import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapInfo;

/**
 * 带条件的地点编辑器。
 */
public class ConditionalLocationCellEditor extends CellEditorAdapter {
    protected int[] location;
    protected String condition;
            
    public ConditionalLocationCellEditor(Composite parent) {
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
        return new Object[] { location[0], location[1], location[2], condition };
    }
    
    /**
     * The <code>TextCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method accepts
     * a text string (type <code>String</code>).
     *
     * @param value a text string (type <code>String</code>)
     */
    protected void doSetValue(Object value) {
        Assert.isTrue(text != null && (value instanceof Object[]));
        Object[] arr = (Object[])value;
        location = new int[3];
        location[0] = ((Integer)arr[0]).intValue();
        location[1] = ((Integer)arr[1]).intValue();
        location[2] = ((Integer)arr[2]).intValue();
        condition = (String)arr[3];
        text.setText(GameMapInfo.locationToString(ProjectData.getActiveProject(), location, false, condition));
    }

    protected void editText() {
        ChooseLocationDialog dlg = new ChooseLocationDialog(text.getShell(), condition);
        dlg.setLocation(location);
        if (dlg.open() == Dialog.OK) {
            location = dlg.getLocation();
            condition = dlg.getCondition();
            text.setText(GameMapInfo.locationToString(ProjectData.getActiveProject(), location, false, condition));
            fireApplyEditorValue();
            deactivate();
        }
    }
}
