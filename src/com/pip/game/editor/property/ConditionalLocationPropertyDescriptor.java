package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapInfo;

/**
 *  Ù–‘√Ë ˆ£∫—°‘ÒŒª÷√°£
 */
public class ConditionalLocationPropertyDescriptor extends PropertyDescriptor {
    public ConditionalLocationPropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new ConditionalLocationCellEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new LocationLabelProvider(this);
    }

    public static class LocationLabelProvider extends LabelProvider {
        ConditionalLocationPropertyDescriptor rppd;
        
        public LocationLabelProvider(ConditionalLocationPropertyDescriptor rppd) {
            this.rppd = rppd;
        }
        
        public String getText(Object element) {
            Object[] info = (Object[])element;
            int[] location = new int[3];
            location[0] = ((Integer)info[0]).intValue();
            location[1] = ((Integer)info[1]).intValue();
            location[2] = ((Integer)info[2]).intValue();
            return GameMapInfo.locationToString(ProjectData.getActiveProject(), location, false, (String)info[3]);
        }
    }
}
