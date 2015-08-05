package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.item.Item;

public class ItemPropertyDescriptor extends PropertyDescriptor{

    public ItemPropertyDescriptor(Object id, String displayName) {
        super(id, displayName);
    }
    
    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new ItemCellEditor(parent);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new ItemProvider();
    }

    /**
     * ��Ʒ���ƣ�������ƷID������Ʒ��
     * @author Joy
     */
    public static class ItemProvider extends LabelProvider {
        public String getText(Object element) {
            int itemId = ((Integer)element).intValue();
            Item item = ProjectData.getActiveProject().findItemOrEquipment(itemId);
            if(item != null){                
                return item.toString();
            }
            else{
                return "��Ч����Ʒ";
            }
        }
    }

}
