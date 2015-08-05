package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.item.DropGroup;

/**
 * 通用对象属性描述。
 */
public class DataObjectPropertyDescriptor extends PropertyDescriptor {
    private Class clazz;
    
    public DataObjectPropertyDescriptor(Object id, String displayName, Class clazz) {
        super(id, displayName);
        this.clazz = clazz;
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new DataObjectCellEditor(parent, clazz);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new DataObjectLabelProvider();
    }

    public class DataObjectLabelProvider extends LabelProvider {
        public String getText(Object element) {
            DataObject obj = ProjectData.getActiveProject().findObject(clazz, ((Integer)element).intValue());
            if (obj == null) {
                return "无";
            } else {
                return obj.toString();
            }
        }
    }
}
