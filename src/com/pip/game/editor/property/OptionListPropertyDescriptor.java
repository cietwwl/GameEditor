package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * ��������������ѡ���б�����ѡ���б�����\n�ָ����Ķ���ַ�����
 */
public class OptionListPropertyDescriptor extends PropertyDescriptor {
    public OptionListPropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new OptionListCellEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new OptionListLabelProvider();
    }

    /**
     * ����ѡ��ϵ���ʾ�ṩ�ߡ�����Ѷ��ѡ����\n�ֿ�ƴ��һ�����ַ�����
     * @author lighthu
     */
    public static class OptionListLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return ((String)element).replace("\n", "��");
        }
    }
}
