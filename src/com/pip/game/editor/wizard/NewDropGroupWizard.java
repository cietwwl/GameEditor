package com.pip.game.editor.wizard;

import java.io.*;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.pip.game.data.GameArea;
import com.pip.game.data.ProjectData;
import com.pip.game.data.item.DropGroup;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;
import com.pip.mapeditor.data.MapFile;
import com.pip.util.EFSUtil;
import com.pipimage.utils.Utils;

/**
 * �����¹ؿ����򵼡�
 * @author lighthu
 */
public class NewDropGroupWizard implements Runnable {
    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView)page.findView(DataListView.ID);

        // ѯ���¹ؿ�������
        InputDialog dlg = new InputDialog(shell, "�½�������", "��������������ƣ�", "�µ�����", new IInputValidator() {
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
            // �����µ�DropGroup����
            ProjectData proj = ProjectData.getActiveProject();
            DropGroup newArea = (DropGroup)proj.newObject(DropGroup.class, DataListView.getSelectObject());
            newArea.title = newname;
            
            if (view != null) {
                view.refresh(DropGroup.class);
                view.editObject(newArea);
            }
            
            // ���汾���������б�
            proj.saveDataList(DropGroup.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
