package com.pip.game.editor.wizard;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.pip.game.data.ProjectData;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.talent.TalentTree;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;

public class NewTalentTreeWizard implements Runnable{

    public NewTalentTreeWizard(){
        
    }
    
    public void run(){
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        InputDialog input = new InputDialog(shell, "新建技能树", "请输入名称", "给我起个名字吧", new IInputValidator(){

            public String isValid(String newText) {
                if(newText.equals("")){
                    return "名称不能为空";
                }
                return null;
            }
            
        });
        int ret = input.open();
        if(ret == InputDialog.CANCEL){
            return;
        }
        String name = input.getValue();
        ProjectData proj = ProjectData.getActiveProject();
        TalentTree newTalentTree;
        try {
            newTalentTree = (TalentTree) proj.newObject(TalentTree.class, DataListView.getSelectObject());
        }
        catch (Exception e) {
            MessageDialog.openError(shell, "Error", "Create talent tree failed\n"+e);
            e.printStackTrace();
            return;
        }
        newTalentTree.title = name;
        try {
            proj.saveDataList(TalentTree.class);
        }
        catch (Exception e) {
            MessageDialog.openError(shell, "Error", "save talent tree failed\n"+e);
            e.printStackTrace();
        }
    }
}
