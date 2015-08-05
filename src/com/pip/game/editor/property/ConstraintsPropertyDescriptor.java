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

/**
 * 属性描述：公式限制。
 */
public class ConstraintsPropertyDescriptor extends PropertyDescriptor {
    public ConstraintsPropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new ConstraintsCellEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new ConstraintsLabelProvider();
    }

    public static class ConstraintsLabelProvider extends LabelProvider {
        public String getText(Object element) {
            String expr = (String)element;
            return ExpressionList.toNatureString(expr);
        }
    }
}
