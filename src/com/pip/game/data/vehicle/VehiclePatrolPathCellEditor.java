package com.pip.game.data.vehicle;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;

import com.pip.game.data.map.GameMapNPC;
import com.pip.game.editor.property.CellEditorAdapter;
import com.pip.game.editor.property.ChoosePatrolPathDialog;

public class VehiclePatrolPathCellEditor extends CellEditorAdapter {

    protected int patrolPathId;
    protected XyGameMapVehicle mapNPC;

    public VehiclePatrolPathCellEditor(Composite parent, XyGameMapVehicle mapNPC) {
        super(parent);
        this.mapNPC = mapNPC;
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
            patrolPathId = Integer.parseInt(tmp.substring(0, tmp.indexOf('(')));
        }
        text.setText((String)value);
    }
    
    protected void editText() {
        XyVehicleChoosePatrolPathDialog dlg = new XyVehicleChoosePatrolPathDialog(text.getShell());
        dlg.setPatrolPathId(patrolPathId, mapNPC);
        if (dlg.open() == Dialog.OK) {
            patrolPathId = dlg.getPatrolPathId();
            text.setText(String.valueOf(patrolPathId));
            fireApplyEditorValue();
            deactivate();
        }
    }
}
