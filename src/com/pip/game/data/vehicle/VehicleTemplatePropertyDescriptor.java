package com.pip.game.data.vehicle;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.ProjectData;

public class VehicleTemplatePropertyDescriptor extends PropertyDescriptor {

    public VehicleTemplatePropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new VehicleTemplateCellEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new VehicleTemplateLabelProvider();
    }

    public static class VehicleTemplateLabelProvider extends LabelProvider {
        public String getText(Object element) {
            int templateID = 0;
            try{
                templateID = ((Integer)element).intValue();
            }catch(ClassCastException e){
                templateID = Integer.parseInt((String)element);
            }
            return Vehicle.toString(ProjectData.getActiveProject(), templateID);
        }
    }
}
