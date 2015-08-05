package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectData;

/**
 *  Ù–‘√Ë ˆ£∫—°‘ÒNPCƒ£∞Â°£
 */
public class NPCTemplatePropertyDescriptor extends PropertyDescriptor {
    public NPCTemplatePropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new NPCTemplateCellEditor(parent);
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
            int templateID = 0;
            try{
                templateID = ((Integer)element).intValue();
            }catch(ClassCastException e){
                templateID = Integer.parseInt((String)element);
            }
            return NPCTemplate.toString(ProjectData.getActiveProject(), templateID);
        }
    }
}
