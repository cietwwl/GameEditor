package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.editor.EditorApplication;

/**
 * �����������༭NPC���ܡ�
 */
public class NPCFunctionPropertyDescriptor extends PropertyDescriptor {
    public NPCFunctionPropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new NPCFunctionCellEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new NPCFunctionLabelProvider();
    }

    public static class NPCFunctionLabelProvider extends LabelProvider {
        public String getText(Object element) {
            String[] funcs = (String[])element;
            return funcs[0];
        }
    }
}
