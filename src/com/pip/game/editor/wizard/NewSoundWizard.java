package com.pip.game.editor.wizard;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.pip.game.data.ProjectData;
import com.pip.game.data.Sound;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;

/**
 * �����������ļ����򵼡�
 * @author lighthu
 */
public class NewSoundWizard implements Runnable {
    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView)page.findView(DataListView.ID);

        // ѯ�������������
        InputDialog dlg = new InputDialog(shell, "�½�����", "���������ƣ�", "������", new IInputValidator() {
            public String isValid(String newText) {
                if (newText.trim().length() == 0) {
                    return "���Ʋ���Ϊ�ա�";
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
            // �����µ�Sound����
            ProjectData proj = ProjectData.getActiveProject();
            Sound newSound = (Sound)proj.newObject(Sound.class, DataListView.getSelectObject());
            newSound.title = newname;
            
            // ˢ���̵��б���ʼ�༭�¶���
            if (view != null) {
                view.refresh(Sound.class);
                view.editObject(newSound);
            }
            
            // ���汾���������б�
            proj.saveDataList(Sound.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
