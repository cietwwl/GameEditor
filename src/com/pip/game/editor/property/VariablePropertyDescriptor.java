package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.DataObject;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.PQEUtils;

/**
 * ��������������������������ȫ�ֱ�����Ҳ�����Ǿֲ�������
 * @author lighthu
 */
public class VariablePropertyDescriptor extends PropertyDescriptor {
    protected QuestInfo questInfo;
    protected boolean localOnly;
    
    /**
     * ����һ�������༭������
     * @param id
     * @param displayName
     * @param qinfo ������Ϣ���������������Ϣ��
     * @param localOnly ���Ϊtrue����ֻ��ʾ�������
     */
    public VariablePropertyDescriptor(Object id, String displayName, QuestInfo qinfo, boolean localOnly) {
        super(id, displayName);
        questInfo = qinfo;
        this.localOnly = localOnly;
    }

    public CellEditor createPropertyEditor(Composite parent) {
        String[] titles, values;
        if (localOnly) {
            titles = new String[questInfo.variables.size()];
            values = new String[questInfo.variables.size()];
            for (int i = 0; i < questInfo.variables.size(); i++) {
                titles[i] = questInfo.variables.get(i).name;
                values[i] = questInfo.variables.get(i).name;
            }
        } else {
            titles = new String[questInfo.variables.size() + questInfo.owner.owner.config.pqeUtils.SYSTEM_VARS.length];
            values = new String[questInfo.variables.size() + questInfo.owner.owner.config.pqeUtils.SYSTEM_VARS.length];
            for (int i = 0; i < questInfo.variables.size(); i++) {
                titles[i] = questInfo.variables.get(i).name;
                values[i] = questInfo.variables.get(i).name;
            }
            for (int i = 0; i < questInfo.owner.owner.config.pqeUtils.SYSTEM_VARS.length; i++) {
                titles[i + questInfo.variables.size()] = questInfo.owner.owner.config.pqeUtils.SYSTEM_VARS[i].description;
                values[i + questInfo.variables.size()] = questInfo.owner.owner.config.pqeUtils.SYSTEM_VARS[i].name;
            }
        }
        CellEditor editor = new FreeComboCellEditor(parent, titles, values);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }
}
