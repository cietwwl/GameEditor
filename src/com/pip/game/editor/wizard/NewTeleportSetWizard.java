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
import com.pip.game.data.Shop;
import com.pip.game.data.TeleportSet;
import com.pip.game.data.quest.Quest;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;
import com.pip.mapeditor.data.MapFile;
import com.pip.util.EFSUtil;
import com.pipimage.utils.Utils;

/**
 * �����´���������򵼡�
 * @author lighthu
 */
public class NewTeleportSetWizard implements Runnable {
    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView)page.findView(DataListView.ID);

        // ѯ�������������
        InputDialog dlg = new InputDialog(shell, "�½���վ", "��������վ���ƣ�", "����վ", new IInputValidator() {
            public String isValid(String newText) {
                if (newText.trim().length() == 0) {
                    return "��վ���Ʋ���Ϊ�ա�";
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
            // �����µ�TeleportSet����
            ProjectData proj = ProjectData.getActiveProject();
            TeleportSet newShop = (TeleportSet)proj.newObject(TeleportSet.class, DataListView.getSelectObject());
            newShop.title = newname;
            
            // ˢ���̵��б���ʼ�༭�¶���
            if (view != null) {
                view.refresh(TeleportSet.class);
                view.editObject(newShop);
            }
            
            // ���汾���������б�
            proj.saveDataList(TeleportSet.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
