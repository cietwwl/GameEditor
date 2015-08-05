package com.pip.game.editor;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;

import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.editor.util.Settings;
import com.pip.mango.jni.GLUtils;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    public void preWindowOpen() {
        Settings.loadSetting();
        
        com.pip.image.workshop.Settings.loadSetting();
        ProjectData proj = new ProjectData();
        ProjectData.setActiveProject(proj);
        try {
            proj.load(Settings.workingDir, this.getClass().getClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.openError(null, "载入数据错误", e.toString());
        }

        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setShowCoolBar(true);
        configurer.setInitialSize(new Point(1200, 800));
        configurer.setShowStatusLine(true);
        configurer.setTitle("PIP-Game数据编辑器 - "+Settings.workingDir);
        configurer.addEditorAreaTransfer(FileTransfer.getInstance());
        configurer.configureEditorAreaDropListener(new FileDropTargetAdapter(configurer.getWindow()));
    }

    public void postWindowCreate() {
        super.postWindowCreate();
        Shell shell = getWindowConfigurer().getWindow().getShell();
        shell.setMaximized(true);
        DataListView view = (DataListView)getWindowConfigurer().getWindow().getActivePage().findView(DataListView.ID);
        if (view != null) {
            try {
                view.setup();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void postWindowClose() {
        super.postWindowClose();
        Settings.saveSetting();
        com.pip.image.workshop.Settings.saveSetting();
        ParticleEffectManager.clear();
    }

    static class FileDropTargetAdapter extends DropTargetAdapter {
        IWorkbenchWindow window;
        
        public FileDropTargetAdapter(IWorkbenchWindow window) {
            this.window = window;
        }

        public void dragOver(DropTargetEvent event) {
            event.feedback = DND.FEEDBACK_NONE | DND.FEEDBACK_SCROLL;
            event.detail = DND.DROP_NONE;
            FileTransfer transfer = FileTransfer.getInstance();
            Object data = transfer.nativeToJava(event.currentDataType);
            if (data != null) {
                event.detail = DND.DROP_COPY;
            }
        }
    
        public void drop(DropTargetEvent event) {
            if (event.data == null) {
                return;
            }
            FileTransfer transfer = FileTransfer.getInstance();
            Object data = transfer.nativeToJava(event.currentDataType);
            if (data == null) {
                return;
            }
            String[] files = (String[])event.data;
            for (int i = 0; i < files.length; i++) {
                try {
                    IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(files[i]));
                    IDE.openEditorOnFileStore(window.getActivePage(), fileStore);
                } catch (Exception e) {
                }
            }
        }
    }
}
