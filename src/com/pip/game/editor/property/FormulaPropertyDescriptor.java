package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.item.Formula;

/**
 * ����������ѡ������䷽��
 */
public class FormulaPropertyDescriptor extends PropertyDescriptor {
    public FormulaPropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new FormulaCellEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new FormulaLabelProvider();
    }

    public static class FormulaLabelProvider extends LabelProvider {
        public String getText(Object element) {
            int formulaID = ((Integer)element).intValue();
            return Formula.toString(ProjectData.getActiveProject(), formulaID);
        }
    }
}
