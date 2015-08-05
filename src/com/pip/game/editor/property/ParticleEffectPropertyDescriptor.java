package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * 属性描述：粒子效果选择。
 */
public class ParticleEffectPropertyDescriptor extends PropertyDescriptor {
    public ParticleEffectPropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new ParticleEffectCellEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new ParticleEffectLabelProvider();
    }

    public static class ParticleEffectLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return (String)element;
        }
    }
}
