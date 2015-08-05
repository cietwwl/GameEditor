package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.item.Item;
import com.pip.game.data.map.GamePatrolPath;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.property.PatrolPathPropertyDescriptor.PatrolPathLabelProvider;


public class PathPropertyDescriptor extends PropertyDescriptor {
    
    public PathPropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new PathCellEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }
    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new PathLabelProvider();
    }

    public static class PathLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return String.valueOf(element);
        }
    }

}
