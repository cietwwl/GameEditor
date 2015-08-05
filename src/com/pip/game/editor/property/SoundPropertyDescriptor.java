package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.Sound;

/**
 * ����������ѡ������������ա�
 */
public class SoundPropertyDescriptor extends PropertyDescriptor {
    public SoundPropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new SoundCellEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new SoundLabelProvider();
    }

    public static class SoundLabelProvider extends LabelProvider {
        public String getText(Object element) {
            int soundID = ((Integer)element).intValue();
            return Sound.toString(ProjectData.getActiveProject(), soundID);
        }
    }
}
