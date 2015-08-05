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
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;

/**
 * �����³ƺŵ��򵼡�
 * @author lighthu
 */
public class NewTitleWizard implements Runnable {
    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView)page.findView(DataListView.ID);
        
        // ѯ���¶���������
        InputDialog dlg = new InputDialog(shell, "�½��ƺ�", "������ƺ����ƣ�", "�³ƺ�", new IInputValidator() {
            public String isValid(String newText) {
                if (newText.trim().length() == 0) {
                    return "�ƺ����Ʋ���Ϊ�ա�";
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
            // �����µ�Title����
            ProjectData proj = ProjectData.getActiveProject();
            Title newTitle = (Title)proj.newObject(Title.class, DataListView.getSelectObject());
            newTitle.title = newname;
            newTitle.faction = (Faction)ProjectData.getActiveProject().findDictObject(Faction.class, 5);
            
            // ˢ���б���ʼ�༭�¶���
            if (view != null) {
                view.refresh(Title.class);
                view.editObject(newTitle);
            }

            // ���汾���������б�
            proj.saveDataList(Title.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
