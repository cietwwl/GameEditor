package com.pip.game.editor.wizard;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.pip.game.data.ProjectData;
import com.pip.game.data.MonsterGroup;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;

public class NewMonsterGroupWizard implements Runnable {

    public void run() {

        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView)page.findView(DataListView.ID);
        
        // ѯ���¶���������
        InputDialog dlg = new InputDialog(shell, "�½�������", "��������������ƣ�", "�¹�����", new IInputValidator() {
            public String isValid(String newText) {
                if (newText.trim().length() == 0) {
                    return "���������Ʋ���Ϊ�ա�";
                } else {
                    return null;
                }
            }
        });
        if (dlg.open() != InputDialog.OK) {
            return;
        }
        String newname = dlg.getValue();
        
        try {
            // �����µ�Animation����
            ProjectData proj = ProjectData.getActiveProject();
            MonsterGroup newMonsGrp = (MonsterGroup)proj.newObject(MonsterGroup.class, DataListView.getSelectObject());
            newMonsGrp.title = newname;
            
            // ˢ�¶����б���ʼ�༭�¶���
            if (view != null) {
                view.refresh(MonsterGroup.class);
                view.editObject(newMonsGrp);
            }

            // ���汾���������б�
            proj.saveDataList(MonsterGroup.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
}
