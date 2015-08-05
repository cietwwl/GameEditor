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
import com.pip.game.data.GiftGroup;
import com.pip.game.data.ProjectData;
import com.pip.game.data.Title;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;

/**
 * 创建新奖励组的向导。
 * @author lighthu
 */
public class NewGiftGroupWizard implements Runnable {
    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView)page.findView(DataListView.ID);
        
        // 询问新奖励组的名称
        InputDialog dlg = new InputDialog(shell, "新建奖励组", "请输入奖励组名称：", "新奖励组", new IInputValidator() {
            public String isValid(String newText) {
                if (newText.trim().length() == 0) {
                    return "奖励组名称不能为空。";
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
            // 创建新的GiftGroup对象
            ProjectData proj = ProjectData.getActiveProject();
            GiftGroup newGroup = (GiftGroup)proj.newObject(GiftGroup.class, DataListView.getSelectObject());
            newGroup.title = newname;
            
            // 刷新列表并开始编辑新对象
            if (view != null) {
                view.refresh(GiftGroup.class);
                view.editObject(newGroup);
            }

            // 保存本类型数据列表
            proj.saveDataList(GiftGroup.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
