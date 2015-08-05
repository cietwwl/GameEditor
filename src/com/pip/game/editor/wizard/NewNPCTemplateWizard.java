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
import com.pip.game.data.NPCTemplate;
import com.pip.game.data.NPCType;
import com.pip.game.data.ProjectData;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;

/**
 * ������NPCģ����򵼡�
 * @author lighthu
 */
public class NewNPCTemplateWizard implements Runnable {
    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView)page.findView(DataListView.ID);

        // ѯ���¹ؿ�������
        InputDialog dlg = new InputDialog(shell, "�½�NPCģ��", "������NPCģ�����ƣ�", "��NPCģ��", new IInputValidator() {
            public String isValid(String newText) {
                if (newText.trim().length() == 0) {
                    return "NPCģ�����Ʋ���Ϊ�ա�";
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
            // �����µ�NPCDef����
            ProjectData proj = ProjectData.getActiveProject();
            NPCTemplate newNPC = (NPCTemplate)proj.newObject(NPCTemplate.class, DataListView.getSelectObject());
            newNPC.title = newname;
            newNPC.type = (NPCType)proj.getDictDataListByType(NPCType.class).get(0);
            
            // ˢ��NPCģ���б���ʼ�༭�¶���
            if (view != null) {
                view.refresh(NPCTemplate.class);
                view.editObject(newNPC);
            }

            // ���汾���������б�
            proj.saveDataList(NPCTemplate.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
