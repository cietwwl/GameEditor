package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapObject;

/**
 * 属性描述：选择NPC。
 */
public class NPCPropertyDescriptor extends PropertyDescriptor {
    public NPCPropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new NPCCellEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new NPCLabelProvider();
    }

    /**
     * NPC名字显示，根据NPC ID查找NPC。
     * @author lighthu
     */
    public static class NPCLabelProvider extends LabelProvider {
        public String getText(Object element) {
            int npcID = ((Integer)element).intValue();
            return GameMapObject.toString(ProjectData.getActiveProject(), npcID);
        }
    }
}
