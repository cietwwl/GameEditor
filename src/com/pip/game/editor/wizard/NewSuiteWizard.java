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
import com.pip.game.data.ProjectData;
import com.pip.game.data.Title;
import com.pip.game.data.equipment.SuiteConfig;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;

/**
 * ��������װ���򵼡�
 * @author lighthu
 */
public class NewSuiteWizard implements Runnable {
    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView)page.findView(DataListView.ID);
        
        // ѯ������װ������
        InputDialog dlg = new InputDialog(shell, "�½���װ", "��������װ���ƣ�", "����װ", new IInputValidator() {
            public String isValid(String newText) {
                if (newText.trim().length() == 0) {
                    return "��װ���Ʋ���Ϊ�ա�";
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
            // �����µ���װ����
            ProjectData proj = ProjectData.getActiveProject();
            SuiteConfig newSuite = (SuiteConfig)proj.newObject(SuiteConfig.class, DataListView.getSelectObject());
            newSuite.title = newname;
            
            // ˢ���б���ʼ�༭�¶���
            if (view != null) {
                view.refresh(SuiteConfig.class);
                view.editObject(newSuite);
            }

            // ���汾���������б�
            proj.saveDataList(SuiteConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
