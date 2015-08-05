package com.pip.game.editor.wizard;

import java.io.*;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.pip.game.data.Animation;
import com.pip.game.data.GameArea;
import com.pip.game.data.ProjectData;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;

/**
 * �����¶������򵼡�
 * @author lighthu
 */
public class NewAnimationWizard implements Runnable {
    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView)page.findView(DataListView.ID);
        
        // ѯ���¶���������
        InputDialog dlg = new InputDialog(shell, "�½�����", "�����붯�����ƣ�", "�¶���", new IInputValidator() {
            public String isValid(String newText) {
                if (newText.trim().length() == 0) {
                    return "�������Ʋ���Ϊ�ա�";
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
            Animation newAni = (Animation)proj.newObject(Animation.class, DataListView.getSelectObject());
            newAni.title = newname;
            
            // ˢ�¶����б���ʼ�༭�¶���
            if (view != null) {
                view.refresh(Animation.class);
                view.editObject(newAni);
            }

            // ���汾���������б�
            proj.saveDataList(Animation.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
