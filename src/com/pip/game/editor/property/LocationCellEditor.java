package com.pip.game.editor.property;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;

import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapInfo;

/**
 * µØµã±à¼­Æ÷¡£
 */
public class LocationCellEditor extends CellEditorAdapter {
    protected int[] location;
            
    public LocationCellEditor(Composite parent) {
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
        return new int[] { location[0], location[1], location[2] };
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
        location = (int[])value;
        text.setText(GameMapInfo.locationToString(ProjectData.getActiveProject(), location, false));
    }

    protected void editText() {
        ChooseLocationDialog dlg = new ChooseLocationDialog(text.getShell());
        dlg.setLocation(location);
        if (dlg.open() == Dialog.OK) {
            location = dlg.getLocation();
            
            text.setText(GameMapInfo.locationToString(ProjectData.getActiveProject(), location, false));
            fireApplyEditorValue();
            deactivate();
        }
    }
}
