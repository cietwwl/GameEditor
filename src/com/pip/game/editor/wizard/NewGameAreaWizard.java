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

import com.pip.game.data.GameArea;
import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;
import com.pip.mapeditor.data.MapFile;
import com.pip.util.EFSUtil;
import com.pipimage.utils.Utils;

/**
 * �����¹ؿ����򵼡�
 * @author lighthu
 */
public class NewGameAreaWizard implements Runnable {
    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView)page.findView(DataListView.ID);

        // ѯ���¹ؿ�������
        InputDialog dlg = new InputDialog(shell, "�½��ؿ�", "������ؿ����ƣ�", "�¹ؿ�", new IInputValidator() {
            public String isValid(String newText) {
                if (newText.trim().length() == 0) {
                    return "�ؿ����Ʋ���Ϊ�ա�";
                } else {
                    return null;
                }
            }
        });
        if (dlg.open() != InputDialog.OK) {
            return;
        }
        
        // ѡ��һ����ͼ�ļ�
        FileDialog fdlg = new FileDialog(shell, SWT.OPEN);
        String path = ProjectData.getActiveProject().config.getPipLibDir().getAbsolutePath();
        fdlg.setFilterPath(path);
        fdlg.setFilterExtensions(new String[] { "*.map", "*.*" });
        fdlg.setFilterNames(new String[] { "��ͼ�ļ�(*.map)", "�����ļ�(*.*)" });
        String file = null;
        while(true){
            file = fdlg.open();
            if(file == null){
                return;
            }
            try {
                path = new File(path).getCanonicalPath();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
//            if(file.startsWith(path)==false){
//                MessageDialog.openError(shell, "����", "��ͼ�ļ�����λ��������ļ��л������ļ�����\n" + path );
//            }else{
//                break;
//            }
            break;
        }
        
        // ����ͼ�ļ��Ƿ���Ч
        try {
            MapFile mf = new MapFile();
            mf.load(new File(file));
            if (mf.getMaps().size() == 0) {
                MessageDialog.openError(shell, "����", "��ͼ�ļ���������Ҫ����һ�ŵ�ͼ��");
                return;
            }
        } catch (Exception e) {
            MessageDialog.openError(shell, "����", e.toString());
            return;
        }
        
        String newname = dlg.getValue();
        try {
            // �����µ�AreaDef����
            ProjectData proj = ProjectData.getActiveProject();
            GameArea newArea = (GameArea)proj.newObject(GameArea.class, DataListView.getSelectObject());
            newArea.title = newname;
            String dirName = String.valueOf(newArea.id);
            int nextID = 1;
            while (new File(proj.baseDir, "Areas/" + dirName).exists()) {
                dirName = newArea.id + "_" + nextID;
                nextID++;
            }
            newArea.source = new File(proj.baseDir, "Areas/" + dirName);
            newArea.source.mkdirs();
            
            // ������ͼ�ļ�
            newArea.setFile(0, new File(file), -2, null, null);
            
            // ˢ�¹ؿ��б���ʼ�༭�¹ؿ�
            if (view != null) {
                view.refresh(GameArea.class);
                view.editObject(newArea);
            }
            
            // ���汾���������б�
            proj.saveDataList(GameArea.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
