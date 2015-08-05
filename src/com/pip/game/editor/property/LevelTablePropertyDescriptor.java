package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.NPCTemplate;
import com.pip.game.data.item.Formula;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.editor.EditorApplication;

/**
 * 属性描述：级别映射表。
 */
public class LevelTablePropertyDescriptor extends PropertyDescriptor {
    public LevelTablePropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new LevelTableCellEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new LevelTableLabelProvider();
    }

    public static class LevelTableLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return element.toString();
        }
    }
}
