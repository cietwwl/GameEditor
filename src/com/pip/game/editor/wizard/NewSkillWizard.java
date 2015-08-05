package com.pip.game.editor.wizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.pip.game.data.ProjectData;
import com.pip.game.data.skill.BuffConfig;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.skill.SkillExtPropManager;
import com.pip.game.editor.util.ExtPropManager;

public class NewSkillWizard implements Runnable{
    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView) page.findView(DataListView.ID);

        // ѯ���¼��ܵ����ƺ�����
        NewSkillDialog dlg = new NewSkillDialog(shell);
        if (dlg.open() != Dialog.OK) {
            return;
        }
        
        try {
            // �����µ�Buff����
            ProjectData proj = ProjectData.getActiveProject();
            SkillConfig newSkill = (SkillConfig)proj.newObject(SkillConfig.class, DataListView.getSelectObject());
            newSkill.title = dlg.getName();
            int type = (int) Math.pow(2, dlg.getType());
            newSkill.setType(type);
            SkillExtPropManager tmp = new SkillExtPropManager();
            tmp.fillWithProps(newSkill.extPropEntries);
            
            // ˢ��BUFF�б���ʼ�༭�¶���
            if (view != null) {
                view.refresh(SkillConfig.class);
                view.editObject(newSkill);
            }
            
            // ���汾���������б�
            proj.saveDataList(SkillConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
