package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.editor.EditorApplication;

/**
 *  Ù–‘√Ë ˆ£∫—°‘ÒŒª÷√°£
 */
public class PatrolPathPropertyDescriptor extends PropertyDescriptor {
    private GameMapNPC mapNPC;
    
    public PatrolPathPropertyDescriptor(Object id, String displayName, GameMapNPC mapNPC) {
        super(id, displayName);
        this.mapNPC = mapNPC;
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new PatrolPathCellEditor(parent, mapNPC);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new PatrolPathLabelProvider();
    }

    public static class PatrolPathLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return (String)element;
        }
    }
}
