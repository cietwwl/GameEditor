package com.pip.game.editor.wizard;

import java.io.*;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.pip.game.data.DataObject;
import com.pip.game.data.GameArea;
import com.pip.game.data.ProjectData;
import com.pip.game.data.quest.Quest;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;
import com.pip.mapeditor.data.MapFile;
import com.pip.util.EFSUtil;
import com.pipimage.utils.Utils;

/**
 * 创建新任务的向导。
 * @author lighthu
 */
public class NewQuestWizard implements Runnable {
    private int initType = -1;
    private int initID;
    
    public NewQuestWizard() {
    }
    
    public NewQuestWizard(int initType, int initID) {
        this.initType = initType;
        this.initID = initID;
    }
    
    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView)page.findView(DataListView.ID);

        // 询问新任务的名称
        InputDialog dlg = new InputDialog(shell, "新建任务", "请输入任务名称：", "新任务", new IInputValidator() {
            public String isValid(String newText) {
                if (newText.trim().length() == 0) {
                    return "任务名称不能为空。";
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
            // 创建新的Quest对象
            ProjectData proj = ProjectData.getActiveProject();
            Object parent;
            if (view.getEditingClass().asSubclass(Quest.class) != null) {
                parent = DataListView.getSelectObject();
            } else {
                parent = proj.getCategoryListByType(Quest.class).get(0);
            }
            Quest newQuest = (Quest)proj.newObject(Quest.class, parent);
            newQuest.title = newname;
            int aid = 1;
            String fname = newQuest.id + ".txt";
            while (new File(proj.baseDir, "Quests/" + fname).exists()) {
                fname = newQuest.id + "_" + aid + ".txt";
                aid++;
            }
            File questFile = new File(proj.baseDir, "Quests/" + fname);
            newQuest.source = questFile;
            if (this.initType == 0) {
                newQuest.type = 0;
                newQuest.startNpc = Integer.toHexString(initID);
                newQuest.finishNpc = Integer.toHexString(initID);
            } else if (this.initType == 1) {
                newQuest.type = 1;
                newQuest.areaID = initID;
            }
            
            // 刷新任务列表并开始编辑新任务
            if (view != null) {
                view.refresh(Quest.class);
                view.editObject(newQuest);
            }
            
            // 保存本类型数据列表
            proj.saveDataList(Quest.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
