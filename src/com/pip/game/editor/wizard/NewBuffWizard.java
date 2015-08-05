package com.pip.game.editor.wizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.pip.game.data.ProjectData;
import com.pip.game.data.skill.BuffConfig;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.skill.BuffExtPropManager;

/**
 * 创建新BUFF的向导。
 * @author lighthu
 */
public class NewBuffWizard implements Runnable {
    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView)page.findView(DataListView.ID);

        // 询问新BUFF的名称和类型
        NewBuffDialog dlg = new NewBuffDialog(shell);
        if (dlg.open() != Dialog.OK) {
            return;
        }
        
        try {
            // 创建新的Buff对象
            ProjectData proj = ProjectData.getActiveProject();
            BuffConfig newBuff = (BuffConfig)proj.newObject(BuffConfig.class, DataListView.getSelectObject());
            newBuff.title = dlg.getName();
            int type = (int) Math.pow(2, dlg.getType());
            newBuff.setBuffType(type);
            
            BuffExtPropManager tmp = new BuffExtPropManager();
            tmp.fillWithProps(newBuff.extPropEntries);
            
            // 刷新BUFF列表并开始编辑新对象
            if (view != null) {
                view.refresh(BuffConfig.class);
                view.editObject(newBuff);
            }
            
            // 保存本类型数据列表
            proj.saveDataList(BuffConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
