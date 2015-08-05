package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.quest.Quest;

/**
 *  Ù–‘√Ë ˆ£∫—°‘ÒNPCƒ£∞Â°£
 */
public class QuestPropertyDescriptor extends PropertyDescriptor {
    public QuestPropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new QuestCellEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new QuestLabelProvider();
    }

    public static class QuestLabelProvider extends LabelProvider {
        public String getText(Object element) {
            int questID = ((Integer)element).intValue();
            return Quest.toString(ProjectData.getActiveProject(), questID);
        }
    }
}
