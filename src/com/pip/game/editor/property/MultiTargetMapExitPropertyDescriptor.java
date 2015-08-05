package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.MultiTargetMapExit;

/**
 * 属性描述：编辑传送门列表功能。
 */
public class MultiTargetMapExitPropertyDescriptor extends PropertyDescriptor {
    GameMapInfo gameMapInfo;
    MultiTargetMapExit mapExit;
    public MultiTargetMapExitPropertyDescriptor(Object id, String displayName,GameMapInfo gmInfo,MultiTargetMapExit mapExit) {
        super(id, displayName);
        gameMapInfo = gmInfo;
        this.mapExit = mapExit;
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new GameMapExitCellEditor(parent,gameMapInfo,mapExit);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new GameMapExitLabelProvider();
    }

    public class GameMapExitLabelProvider extends LabelProvider {
        public String getText(Object element) {
//            return String.valueOf(element);
            return mapExit.toString();
        }
    }
}
