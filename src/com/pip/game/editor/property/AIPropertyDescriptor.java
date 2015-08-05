package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.skill.BuffConfig;
import com.pip.game.editor.property.BuffPropertyDescriptor.BuffLabelProvider;

public class AIPropertyDescriptor extends PropertyDescriptor{
    
    public AIPropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }
    
    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new AICellEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new AILabelProvider();
    }

    public static class AILabelProvider extends LabelProvider {
        public String getText(Object element) {
            int aiId = ((Integer)element).intValue();
            return Integer.toString(aiId);
        }
    }
}
