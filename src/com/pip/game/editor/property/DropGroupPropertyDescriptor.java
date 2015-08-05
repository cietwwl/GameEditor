package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.item.DropGroup;

/**
 *  Ù–‘√Ë ˆ£∫—°‘ÒµÙ¬‰◊È°£
 */
public class DropGroupPropertyDescriptor extends PropertyDescriptor {
    public DropGroupPropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new DropGroupCellEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new DropGroupLabelProvider();
    }

    public static class DropGroupLabelProvider extends LabelProvider {
        public String getText(Object element) {
            int dropGroupID = ((Integer)element).intValue();
            return DropGroup.toString(ProjectData.getActiveProject(), dropGroupID);
        }
    }
}
