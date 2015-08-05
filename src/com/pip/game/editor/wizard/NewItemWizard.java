package com.pip.game.editor.wizard;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.pip.game.data.DataObjectCategory;
import com.pip.game.data.ProjectData;
import com.pip.game.data.item.Item;
import com.pip.game.editor.DataListView;

/**
 * �����¹ؿ����򵼡�
 * 
 * @author Joy Yan
 */
public class NewItemWizard implements Runnable {
    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView) page.findView(DataListView.ID);
        
        // ȱʡ����
        Object[] obj = view.getSelectedObjects();
        DataObjectCategory type = null;
        if (obj.length > 0) {
            if (obj[0] instanceof DataObjectCategory) {
                type = (DataObjectCategory)obj[0];
            } else if (obj[0] instanceof Item) {
                Item item = (Item)obj[0];
                type = item.owner.findCategory(Item.class, item.getCategoryName());
            }
        }
        
        // ѯ������Ʒ������
        InputDialog dlg = new InputDialog(shell, "�½���Ʒ", "��������Ʒ���ƣ�", "����Ʒ", new IInputValidator() {
            public String isValid(String newText) {
                if (newText.trim().length() == 0) {
                    return "��Ʒ���Ʋ���Ϊ�ա�";
                } else {
                    return null;
                }
            }
        });
        if (dlg.open() != InputDialog.OK) {
            return;
        }
        
        try {
            ProjectData proj = ProjectData.getActiveProject();
            Item newItem = proj.newItem(type, DataListView.getSelectObject());
            newItem.title = dlg.getValue();

            // ˢ����Ʒ�б���ʼ�༭����Ʒ
            if (view != null) {
                view.refresh(Item.class);
                view.editObject(newItem);
            }

            // ���汾���������б�
            proj.saveDataList(Item.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
