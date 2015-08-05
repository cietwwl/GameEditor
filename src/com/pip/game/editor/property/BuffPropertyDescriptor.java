package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.skill.BuffConfig;

/**
 * 属性描述：选择BUFF，允许空（不允许选择被动技能BUFF）。
 */
public class BuffPropertyDescriptor extends PropertyDescriptor {
   
    
    public final static byte UseAllBuff = 1;
    public final static byte UsePartBuff = 2;
    /**
     * 是否可以使用所有buff
     */
    private byte type;
    public BuffPropertyDescriptor(Object id, String displayName, byte type) {
        super(id, displayName);
        this.type = type;
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new BuffCellEditor(parent, type);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new BuffLabelProvider();
    }

    public static class BuffLabelProvider extends LabelProvider {
        public String getText(Object element) {
            int buffID = ((Integer)element).intValue();
            return BuffConfig.toString(ProjectData.getActiveProject(), buffID);
        }
    }
}
