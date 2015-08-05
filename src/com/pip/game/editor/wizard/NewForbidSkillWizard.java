package com.pip.game.editor.wizard;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.pip.game.data.ProjectData;
import com.pip.game.data.forbid.ForbidSkill;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;

public class NewForbidSkillWizard implements Runnable{

        public void run() {
            Shell shell=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            IWorkbenchPage page=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            DataListView view=(DataListView)page.findView(DataListView.ID);
            InputDialog dlg=new InputDialog(shell,"�½����ܽ�����","�����뼼�ܽ���������:","�¼��ܽ�����",new IInputValidator(){
                public String isValid(String newText){
                    if(newText.trim().length()==0){
                        return "���ܽ��������Ʋ���Ϊ��";
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
                ForbidSkill ForbidSkill=(ForbidSkill)proj.newObject(ForbidSkill.class, DataListView.getSelectObject());
                ForbidSkill.title=newname;
                if(view!=null){
                    view.refresh(ForbidSkill.class);
                    view.editObject(ForbidSkill);
                }
                //���汾���������б�
                proj.saveDataList(ForbidSkill.class);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }
