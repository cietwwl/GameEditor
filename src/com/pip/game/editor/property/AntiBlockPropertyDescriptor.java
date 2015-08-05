package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.data.quest.pqe.ExpressionList;
import com.pip.game.editor.EditorApplication;

public class AntiBlockPropertyDescriptor extends PropertyDescriptor {
    public AntiBlockPropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new AntiBlockCellEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new AntiBlockLabelProvider();
    }

    public static class AntiBlockLabelProvider extends LabelProvider {
        public String getText(Object element) {
            int[] value = (int[])element;
            return AntiBlockCellEditor.getText(value);
        }
    }
}
