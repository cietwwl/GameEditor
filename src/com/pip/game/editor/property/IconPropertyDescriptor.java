package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.ProjectData;

/**
 *  Ù–‘√Ë ˆ:Õº±Í—°‘Ò<br>
 * @author hsniu
 *
 */
public class IconPropertyDescriptor extends PropertyDescriptor {
    String cfgName;
    
    public IconPropertyDescriptor(Object id, String displayName,String cfgName) {
        super(id, displayName);
        this.cfgName = cfgName;
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new IconCellEditor(parent,ProjectData.getActiveProject().config.iconSeries.get(cfgName));
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new IconLabelProvider();
    }

    public static class IconLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return String.valueOf(element);
        }
    }
}
