package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.item.Item;
import com.pip.game.data.map.GameMapInfo;

public class MirrorSetPropertyDescriptor extends PropertyDescriptor{
    private GameMapInfo mapInfo;
    
    public MirrorSetPropertyDescriptor(Object id, String displayName, GameMapInfo mapInfo) {
        super(id, displayName);
        this.mapInfo = mapInfo;
    }
    
    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new MirrorSetCellEditor(parent, mapInfo);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new MirrorSetLabelProvider(mapInfo);
    }

    public static class MirrorSetLabelProvider extends LabelProvider {
        private GameMapInfo mapInfo;
        
        public MirrorSetLabelProvider(GameMapInfo mapInfo) {
            this.mapInfo = mapInfo;
        }
        
        public String getText(Object element) {
            long mirrorSet = ((Long)element).longValue();
            return MirrorSetCellEditor.getMirrorSetText(mapInfo, mirrorSet);
        }
    }

}
