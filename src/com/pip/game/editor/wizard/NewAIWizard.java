package com.pip.game.editor.wizard;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.pip.game.data.ProjectData;
import com.pip.game.data.AI.AIData;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;

public class NewAIWizard implements Runnable{


    public void run() {
        Shell shell=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view=(DataListView)page.findView(DataListView.ID);
        InputDialog dlg=new InputDialog(shell,"新建AI","请输入AI名称:","新AI",new IInputValidator(){
            public String isValid(String newText){
                if(newText.trim().length()==0){
                    return "AI为空";
                }else{
                    return null;
                }
            }
        });
        if(dlg.open()!=InputDialog.OK){
            return;
        }
        String newname=dlg.getValue();
        try{
            ProjectData proj=ProjectData.getActiveProject();
            AIData forbidItem=(AIData)proj.newObject(AIData.class, DataListView.getSelectObject());
            forbidItem.title=newname;
            if(view!=null){
                view.refresh(AIData.class);
                view.editObject(forbidItem);
            }
            //保存本类型数据列表
            proj.saveDataList(AIData.class);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
