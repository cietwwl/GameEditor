package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.skill.SkillConfig;

/**
 *  Ù–‘√Ë ˆ£∫—°‘ÒNPCƒ£∞Â°£
 */
public class SkillPropertyDescriptor extends PropertyDescriptor {
    public SkillPropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new SkillCellEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new SkillLabelProvider();
    }

    public static class SkillLabelProvider extends LabelProvider {
        public String getText(Object element) {
            int skillID = ((Integer)element).intValue();
            return SkillConfig.toString(ProjectData.getActiveProject(), skillID);
        }
    }
}
