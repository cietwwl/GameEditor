package com.pip.game.data.vehicle;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class VehiclePatrolPathPropertyDescriptor extends PropertyDescriptor {

 private XyGameMapVehicle mapNPC;
    
    public VehiclePatrolPathPropertyDescriptor(Object id, String displayName, XyGameMapVehicle mapNPC) {
        super(id, displayName);
        this.mapNPC = mapNPC;
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new VehiclePatrolPathCellEditor(parent, mapNPC);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new PatrolPathLabelProvider();
    }

    public static class PatrolPathLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return (String)element;
        }
    }
}
