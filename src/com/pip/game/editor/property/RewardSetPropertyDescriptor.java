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
 * ������������֧������
 * @author lighthu
 */
public class RewardSetPropertyDescriptor extends PropertyDescriptor {
    protected QuestInfo questInfo;
    
    /**
     * ����һ�������༭������
     * @param id
     * @param displayName
     * @param qinfo ������Ϣ���������������Ϣ��
     * @param localOnly ���Ϊtrue����ֻ��ʾ�������
     */
    public RewardSetPropertyDescriptor(Object id, String displayName, QuestInfo qinfo) {
        super(id, displayName);
        questInfo = qinfo;
    }

    public CellEditor createPropertyEditor(Composite parent) {
        String[] titles = new String[questInfo.owner.rewards.size()];
        for (int i = 0; i < titles.length; i++) {
            titles[i] = questInfo.owner.rewards.get(i).toString();
        }
        CellEditor editor = new ComboBoxCellEditor(parent, titles, SWT.READ_ONLY);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new RewardSetLabelProvider();
    }

    /**
     * ����ѡ��ϵ���ʾ�ṩ�ߡ�����Ѷ��ѡ����\n�ֿ�ƴ��һ�����ַ�����
     * @author lighthu
     */
    public class RewardSetLabelProvider extends LabelProvider {
        public String getText(Object element) {
            int id = ((Integer)element).intValue();
            if (id >= 0 && id < questInfo.owner.rewards.size()) {
                return questInfo.owner.rewards.get(id).toString();
            }
            return "��";
        }
    }
}
