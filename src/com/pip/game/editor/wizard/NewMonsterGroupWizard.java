package com.pip.game.editor.wizard;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.pip.game.data.ProjectData;
import com.pip.game.data.MonsterGroup;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;

public class NewMonsterGroupWizard implements Runnable {

    public void run() {

        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView)page.findView(DataListView.ID);
        
        // 询问新动画的名称
        InputDialog dlg = new InputDialog(shell, "新建怪物组", "请输入怪物组名称：", "新怪物组", new IInputValidator() {
            public String isValid(String newText) {
                if (newText.trim().length() == 0) {
                    return "怪物组名称不能为空。";
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
            // 创建新的Animation对象
            ProjectData proj = ProjectData.getActiveProject();
            MonsterGroup newMonsGrp = (MonsterGroup)proj.newObject(MonsterGroup.class, DataListView.getSelectObject());
            newMonsGrp.title = newname;
            
            // 刷新动画列表并开始编辑新对象
            if (view != null) {
                view.refresh(MonsterGroup.class);
                view.editObject(newMonsGrp);
            }

            // 保存本类型数据列表
            proj.saveDataList(MonsterGroup.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
}
