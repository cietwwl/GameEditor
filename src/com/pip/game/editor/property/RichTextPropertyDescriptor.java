package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.DataObject;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.PQEUtils;

/**
 * ������������ϸ�ʽ�ı���
 * @author lighthu
 */
public class RichTextPropertyDescriptor extends PropertyDescriptor {
    protected QuestInfo questInfo;
    
    /**
     * ����һ�������༭������
     * @param id
     * @param displayName
     * @param qinfo ������Ϣ���������������Ϣ��
     */
    public RichTextPropertyDescriptor(Object id, String displayName, QuestInfo qinfo) {
        super(id, displayName);
        questInfo = qinfo;
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new RichTextCellEditor(parent, questInfo);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }
}
