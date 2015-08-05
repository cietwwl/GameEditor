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
import com.pip.game.data.item.Formula;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;

/**
 * �������䷽���򵼡�
 * @author lighthu
 */
public class NewFormulaWizard implements Runnable {
    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView)page.findView(DataListView.ID);
        
        // ѯ������
        InputDialog dlg = new InputDialog(shell, "�½��䷽", "�������䷽���ƣ�", "���䷽", new IInputValidator() {
            public String isValid(String newText) {
                if (newText.trim().length() == 0) {
                    return "�䷽���Ʋ���Ϊ�ա�";
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
            // �����µ�Formula����
            ProjectData proj = ProjectData.getActiveProject();
            Formula newObj = (Formula)proj.newObject(Formula.class, DataListView.getSelectObject());
            newObj.title = newname;
            
            // ˢ���б���ʼ�༭�¶���
            if (view != null) {
                view.refresh(Formula.class);
                view.editObject(newObj);
            }

            // ���汾���������б�
            proj.saveDataList(Formula.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
