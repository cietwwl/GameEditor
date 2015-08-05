package com.pip.game.editor.wizard;

import java.io.*;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.pip.game.data.Animation;
import com.pip.game.data.Faction;
import com.pip.game.data.GameArea;
import com.pip.game.data.GiftGroup;
import com.pip.game.data.ProjectData;
import com.pip.game.data.Title;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;

/**
 * �����½�������򵼡�
 * @author lighthu
 */
public class NewGiftGroupWizard implements Runnable {
    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView)page.findView(DataListView.ID);
        
        // ѯ���½����������
        InputDialog dlg = new InputDialog(shell, "�½�������", "�����뽱�������ƣ�", "�½�����", new IInputValidator() {
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
            // �����µ�GiftGroup����
            ProjectData proj = ProjectData.getActiveProject();
            GiftGroup newGroup = (GiftGroup)proj.newObject(GiftGroup.class, DataListView.getSelectObject());
            newGroup.title = newname;
            
            // ˢ���б���ʼ�༭�¶���
            if (view != null) {
                view.refresh(GiftGroup.class);
                view.editObject(newGroup);
            }

            // ���汾���������б�
            proj.saveDataList(GiftGroup.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
