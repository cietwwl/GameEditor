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
 * 创建新配方的向导。
 * @author lighthu
 */
public class NewFormulaWizard implements Runnable {
    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView)page.findView(DataListView.ID);
        
        // 询问名称
        InputDialog dlg = new InputDialog(shell, "新建配方", "请输入配方名称：", "新配方", new IInputValidator() {
            public String isValid(String newText) {
                if (newText.trim().length() == 0) {
                    return "配方名称不能为空。";
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
            // 创建新的Formula对象
            ProjectData proj = ProjectData.getActiveProject();
            Formula newObj = (Formula)proj.newObject(Formula.class, DataListView.getSelectObject());
            newObj.title = newname;
            
            // 刷新列表并开始编辑新对象
            if (view != null) {
                view.refresh(Formula.class);
                view.editObject(newObj);
            }

            // 保存本类型数据列表
            proj.saveDataList(Formula.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
