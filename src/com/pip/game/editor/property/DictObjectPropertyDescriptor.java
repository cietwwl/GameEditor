package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;

/**
 * 通用字典对象属性描述。
 */
public class DictObjectPropertyDescriptor extends PropertyDescriptor {
    private Class clazz;
    
    public DictObjectPropertyDescriptor(Object id, String displayName, Class clazz) {
        super(id, displayName);
        this.clazz = clazz;
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new DictObjectCellEditor(parent, clazz);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new DictObjectLabelProvider();
    }

    public class DictObjectLabelProvider extends LabelProvider {
        public String getText(Object element) {
            DataObject obj = ProjectData.getActiveProject().findDictObject(clazz, ((Integer)element).intValue());
            if (obj == null) {
                return "无";
            } else {
                return obj.toString();
            }
        }
    }
}
