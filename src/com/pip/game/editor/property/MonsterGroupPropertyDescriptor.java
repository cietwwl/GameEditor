package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.MonsterGroup;
import com.pip.game.data.ProjectData;

/**
 * 属性描述：选择怪物组模板。
 */
public class MonsterGroupPropertyDescriptor extends PropertyDescriptor {
    public MonsterGroupPropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new MonsterGroupCellEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new NPCTemplateLabelProvider();
    }

    public static class NPCTemplateLabelProvider extends LabelProvider {
        public String getText(Object element) {
            int templateID = ((Integer)element).intValue();
            return MonsterGroup.toString(ProjectData.getActiveProject(), templateID);
        }
    }
}
