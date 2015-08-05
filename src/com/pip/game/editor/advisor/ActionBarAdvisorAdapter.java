package com.pip.game.editor.advisor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import com.pip.game.data.DataObject;
import com.pip.game.data.GameArea;
import com.pip.game.data.GameAreaInfo;
import com.pip.game.data.ProjectData;
import com.pip.game.data.WorldMapDataForExcel;
import com.pip.game.data.i18n.I18NError;
import com.pip.game.data.i18n.I18NProcessor;
import com.pip.game.data.i18n.LocaleConfig;
import com.pip.game.data.i18n.MessageFile;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.data.pkg.PackageFile;
import com.pip.game.data.pkg.PackageFileItem;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorPlugin;
import com.pip.game.editor.GenerateBuffClassDialog;
import com.pip.game.editor.GenericChooseDialog;
import com.pip.game.editor.NullInput;
import com.pip.game.editor.ParticleEffectManager;
import com.pip.game.editor.PlayerLocationMapMaker;
import com.pip.game.editor.area.GameMapExportToExcel;
import com.pip.game.editor.equipment.EquipmentExportToExcel;
import com.pip.game.editor.item.ItemExportToExcel;
import com.pip.game.editor.property.ExportQuestDialog;
import com.pip.game.editor.quest.QuestExportToExcel;
import com.pip.game.editor.util.MapExportPng;
import com.pip.game.editor.util.Settings;
import com.pip.image.workshop.DirectoryView;
import com.pip.image.workshop.TileLibView;
import com.pip.image.workshop.TileView;
import com.pip.image.workshop.WorkshopPlugin;
import com.pip.mango.jni.GLUtils;
import com.pip.mapeditor.data.ProjectOwner;
import com.pip.util.EFSUtil;
import com.pipimage.utils.Utils;
import com.swtdesigner.ResourceManager;

public class ActionBarAdvisorAdapter implements IGameActionBarAdvisor{
    private Action openglAction;
    // ����л���ĿĿ¼
    private Action switchProjectAction;
    private Action openWorkingDirAction;
//    private Action packEquipmentsAction;
    
    private Action i18nCodeAction2;
    private Action i18nCodeAction;
    private Action i18nDataAction;
    private Action i18nScriptAction;
    private Action i18nAdjustRefAction;
    private Action i18nCheckAction;
    private Action i18nMergeAction;

    private Action generatePackageAction;
    private Action cleanGabageAction;
    private Action optimizeAnimationAction;
    private Action adjustImageModeAction;
    private Action updatepriceAction;
    private Action updateAllNpcmapAction;
    private Action generateMapListAction;
    protected Action generateSkillClassesAction;
    private Action generateQuestClassesAction;
    protected Action generateAIClassesAction;
    protected Action generateBuffClassesAction;
    private Action generateVersionAction;
    /**���������ͼѡ��*/
    private Action generateWorldMapAction;
    /**���������ͼ��Ϣѡ��*/
    private Action generateWorldMapInfoAction;
    /**����������Ϣѡ��*/
    private Action exportQuestIndexForExcel;
    /**������Ʒ��Ϣѡ��*/
    private Action exportItemIndexForExcel;
    /**������Ʒ��Ϣѡ��(��ͼ��)*/
    private Action exportItemIndexForExcelWithIcon;
    /**����װ����Ϣѡ��*/
    private Action exportEquipmentIndexForExcel;
    /**����װ����Ϣѡ��(��ͼ��)*/
    private Action exportEquipmentIndexForExcelWithIcon;
    /** �������е�ͼpngͼƬ  */
    private Action exportMapPng;
    private IWorkbenchAction openPerspectiveDialogAction;
    private IWorkbenchAction closeAllPerspectivesAction;
    private IWorkbenchAction closePerspectiveAction;
    private IWorkbenchAction resetPerspectiveAction;
    private IWorkbenchAction savePerspectiveAction;
    private IWorkbenchAction editActionSetsAction;
    private Action viewDataListViewAction;
    private Action viewDirectoryAction;
    private Action viewTileViewAction;
    private Action viewTileLibraryAction;

    private Action redoAction;
    private Action undoAction;
    private IWorkbenchAction saveAllAction;
    private IWorkbenchAction saveAsAction;
    private IWorkbenchAction saveAction;
    private IWorkbenchAction closeAllAction;
    private IWorkbenchAction closeAction;
    private IWorkbenchAction exitAction;
    protected IWorkbenchWindow mainWindow;

    private Action sizeAction6;
    private Action sizeAction5;
    private Action sizeAction4;
    private Action sizeAction3;
    private Action sizeAction2;
    private Action sizeAction1;
    
    /** �������е�ͼNPC���ּ�������  */
    private Action exportMapNPCInfo;
    
    /** �������е�ͼ�б�����ͼ������Ϣ��NPC������ռ���ڴ��С�ȵ� */
    private Action exportMapInfo;
    /** pkg����ѹ�� */
    private Action extractPackageFileAction;
    
    private IBarAdvisorRegisterAction iABA;
    
    private Action userLocationAction;
    
    public ActionBarAdvisorAdapter(IBarAdvisorRegisterAction iABA) {
        this.iABA = iABA;
    }
    
    public void fillCoolBar(ICoolBarManager coolBar) {
        final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
        coolBar.add(toolBarManager);

        toolBarManager.add(saveAction);

        toolBarManager.add(new Separator());

        toolBarManager.add(undoAction);

        toolBarManager.add(redoAction);

        toolBarManager.add(new Separator());

        toolBarManager.add(openPerspectiveDialogAction);
        
        toolBarManager.add(openglAction);
    }

    public void fillMenuBar(IMenuManager menuBar) {
        MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
        menuBar.add(fileMenu);

        fileMenu.add(openWorkingDirAction);
        fileMenu.add(switchProjectAction);
        
        final MenuManager menuManager_3 = new MenuManager("�����ͼ");
        fileMenu.add(menuManager_3);
        
        menuManager_3.add(generateWorldMapInfoAction);
        
        menuManager_3.add(generateWorldMapAction);
        menuManager_3.add(userLocationAction);

        resourceManagerMenu = new MenuManager("��Դ����");
        fileMenu.add(resourceManagerMenu);
        
        resourceManagerMenu.add(generateVersionAction);
        resourceManagerMenu.add(generatePackageAction);
        
        resourceManagerMenu.add(new Separator());
        
        resourceManagerMenu.add(generateBuffClassesAction);
        resourceManagerMenu.add(generateSkillClassesAction);
        resourceManagerMenu.add(generateQuestClassesAction);
        resourceManagerMenu.add(generateAIClassesAction);

        resourceManagerMenu.add(new Separator());

        resourceManagerMenu.add(updateAllNpcmapAction);
        resourceManagerMenu.add(updatepriceAction);
        resourceManagerMenu.add(cleanGabageAction);
        resourceManagerMenu.add(optimizeAnimationAction);
        resourceManagerMenu.add(adjustImageModeAction);

        resourceManagerMenu.add(new Separator());
        
        resourceManagerMenu.add(generateMapListAction);
        resourceManagerMenu.add(exportQuestIndexForExcel);
        resourceManagerMenu.add(exportItemIndexForExcel);
        resourceManagerMenu.add(exportItemIndexForExcelWithIcon);
        resourceManagerMenu.add(exportEquipmentIndexForExcel);
        resourceManagerMenu.add(exportEquipmentIndexForExcelWithIcon);
        resourceManagerMenu.add(exportMapPng);
        resourceManagerMenu.add(exportMapNPCInfo);
        resourceManagerMenu.add(exportMapInfo);
        resourceManagerMenu.add(extractPackageFileAction);

        final MenuManager menuManager_2 = new MenuManager("���ʻ�");
        fileMenu.add(menuManager_2);

        menuManager_2.add(i18nDataAction);
        menuManager_2.add(i18nCodeAction);
        menuManager_2.add(i18nCodeAction2);
        menuManager_2.add(i18nScriptAction);
        menuManager_2.add(i18nCheckAction);
        menuManager_2.add(i18nMergeAction);
        menuManager_2.add(i18nAdjustRefAction);

        fileMenu.add(new Separator());

        fileMenu.add(saveAction);

        fileMenu.add(saveAsAction);

        fileMenu.add(saveAllAction);

        fileMenu.add(new Separator());

        fileMenu.add(closeAction);

        fileMenu.add(closeAllAction);

        fileMenu.add(new Separator());
        fileMenu.add(exitAction);

        final MenuManager menuManager = new MenuManager("&Edit",
                IWorkbenchActionConstants.M_EDIT);
        menuBar.add(menuManager);

        menuManager.add(undoAction);

        menuManager.add(redoAction);

        final MenuManager sizeMenu = new MenuManager("&Screen Size");
        menuManager.add(sizeMenu);

        sizeMenu.add(sizeAction1);

        sizeMenu.add(sizeAction2);

        sizeMenu.add(sizeAction3);

        sizeMenu.add(sizeAction4);

        sizeMenu.add(sizeAction5);

        sizeMenu.add(sizeAction6);
        
        final MenuManager viewMenu = new MenuManager("&View");
        menuBar.add(viewMenu);

        viewMenu.add(viewDataListViewAction);  
        viewMenu.add(viewDirectoryAction);
        viewMenu.add(viewTileLibraryAction);
        viewMenu.add(viewTileViewAction);
        
        final MenuManager emulatorMenu = new MenuManager("&Emulator");
        menuBar.add(emulatorMenu);
    }

    public void makeActions(IWorkbenchWindow window) {
        this.mainWindow = window;
        
        exitAction = ActionFactory.QUIT.create(window);
        iABA.registerAction(exitAction);
        {
            closeAction = ActionFactory.CLOSE.create(window);
            iABA.registerAction(closeAction);
        }
        {
            closeAllAction = ActionFactory.CLOSE_ALL.create(window);
            iABA.registerAction(closeAllAction);
        }
        {
            saveAction = ActionFactory.SAVE.create(window);
            iABA.registerAction(saveAction);
        }
        {
            saveAsAction = ActionFactory.SAVE_AS.create(window);
            iABA.registerAction(saveAsAction);
        }
        {
            saveAllAction = ActionFactory.SAVE_ALL.create(window);
            iABA.registerAction(saveAllAction);
        }

        sizeAction1 = new Action("176x208") {
            public void run() {
                com.pip.image.workshop.Settings.setScreenSize(176, 208);
            }
        };

        sizeAction2 = new Action("240x320") {
            public void run() {
                com.pip.image.workshop.Settings.setScreenSize(240, 320);
            }
        };

        sizeAction3 = new Action("320x240") {
            public void run() {
                com.pip.image.workshop.Settings.setScreenSize(320, 240);
            }
        };

        sizeAction4 = new Action("480x320") {
            public void run() {
                com.pip.image.workshop.Settings.setScreenSize(480, 320);
            }
        };

        sizeAction5 = new Action("640x360") {
            public void run() {
                com.pip.image.workshop.Settings.setScreenSize(640, 360);
            }
        };

        sizeAction6 = new Action("960x640") {
            public void run() {
                com.pip.image.workshop.Settings.setScreenSize(960, 640);
            }
        };
        
        undoAction = new Action("&Undo") {
            public void run() {
                this.firePropertyChange("chosen", this, this);
            }
        };
        undoAction.setEnabled(false);
        undoAction.setImageDescriptor(ResourceManager.getPluginImageDescriptor(WorkshopPlugin.getDefault(), "icons/undo_edit(1).gif"));
        undoAction.setDisabledImageDescriptor(ResourceManager.getPluginImageDescriptor(WorkshopPlugin.getDefault(), "icons/undo_edit.gif"));
        undoAction.setAccelerator(SWT.CTRL | 'z');

        redoAction = new Action("&Redo") {
            public void run() {
                this.firePropertyChange("chosen", this, this);
            }
        };
        redoAction.setEnabled(false);
        redoAction.setImageDescriptor(ResourceManager.getPluginImageDescriptor(WorkshopPlugin.getDefault(), "icons/redo_edit(1).gif"));
        redoAction.setDisabledImageDescriptor(ResourceManager.getPluginImageDescriptor(WorkshopPlugin.getDefault(), "icons/redo_edit.gif"));
        redoAction.setAccelerator(SWT.CTRL | 'y');

        viewDataListViewAction = new Action("��Ŀ") {
            public void run() {
                try {
                    mainWindow.getActivePage().showView(DataListView.ID);
                } catch (Exception e) {
                }
            }
        };
        viewDataListViewAction.setHoverImageDescriptor(ResourceManager.getPluginImageDescriptor(EditorPlugin.getDefault(), "icons/project.gif"));

        viewDirectoryAction = new Action("��Դ�����") {
            public void run() {
                try {
                    mainWindow.getActivePage().showView(DirectoryView.ID);
                } catch (Exception e) {
                }
            }
        };
        viewDirectoryAction.setHoverImageDescriptor(ResourceManager.getPluginImageDescriptor(WorkshopPlugin.getDefault(), "icons/items.gif"));
        
        viewTileLibraryAction = new Action("��ͼ�زĿ�") {
            public void run() {
                try {
                    mainWindow.getActivePage().showView(TileLibView.ID);
                } catch (Exception e) {
                }
            }
        };
        viewTileLibraryAction.setImageDescriptor(ResourceManager.getPluginImageDescriptor(WorkshopPlugin.getDefault(), "icons/tilelib.gif"));

        viewTileViewAction = new Action("��ͼԤ��") {
            public void run() {
                try {
                    mainWindow.getActivePage().showView(TileView.ID);
                } catch (Exception e) {
                }
            }
        };
        viewTileViewAction.setImageDescriptor(ResourceManager.getPluginImageDescriptor(WorkshopPlugin.getDefault(), "icons/tiles.gif"));
        {
            editActionSetsAction = ActionFactory.EDIT_ACTION_SETS.create(window);
            iABA.registerAction(editActionSetsAction);
        }
        {
            savePerspectiveAction = ActionFactory.SAVE_PERSPECTIVE.create(window);
            iABA.registerAction(savePerspectiveAction);
        }
        {
            resetPerspectiveAction = ActionFactory.RESET_PERSPECTIVE.create(window);
            iABA.registerAction(resetPerspectiveAction);
        }
        {
            closePerspectiveAction = ActionFactory.CLOSE_PERSPECTIVE.create(window);
            iABA.registerAction(closePerspectiveAction);
        }
        {
            closeAllPerspectivesAction = ActionFactory.CLOSE_ALL_PERSPECTIVES.create(window);
            iABA.registerAction(closeAllPerspectivesAction);
        }
        {
            openPerspectiveDialogAction = ActionFactory.OPEN_PERSPECTIVE_DIALOG.create(window);
            iABA.registerAction(openPerspectiveDialogAction);
        }

        switchProjectAction = new Action("�л�����Ŀ¼...") {
            public void run() {
                switchProject();
            }
        };
        
        openWorkingDirAction = new Action("���빤��Ŀ¼"){
            public void run(){
                openWorkingDir();
            }
        };
        
        generateVersionAction = new Action("�Զ�����client_pkg����...") {
            public void run() {
                try {
                    ProjectData.getActiveProject().generateResourceVersionXML();
                    MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
                } catch (Exception e) {
                    e.printStackTrace();
                    MessageDialog.openInformation(mainWindow.getShell(), "����", e.toString());
                }
            }
        };

        generateBuffClassesAction = new Action("�Զ�����BUFFʵ����...") {
            public void run() {
                GenerateBuffClassDialog dlg = new GenerateBuffClassDialog(mainWindow.getShell());
                dlg.folder = Settings.exportClassDir.getAbsolutePath();
                dlg.packageName = Settings.buffPackage;
                dlg.prefix = Settings.buffClassPrefix;
                if (dlg.open() == Dialog.OK) {
                    Settings.exportClassDir = new File(dlg.folder);
                    Settings.buffPackage = dlg.packageName;
                    Settings.buffClassPrefix = dlg.prefix;
                    try {
                        ProjectData.getActiveProject().generateBuffClasses("GBK");
                        MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
                    } catch (Exception e) {
                        e.printStackTrace();
                        MessageDialog.openInformation(mainWindow.getShell(), "����", e.toString());
                    }
                }
            }
        };

        generateSkillClassesAction = new Action("�Զ����ɼ���ʵ����...") {
            public void run() {
                GenerateBuffClassDialog dlg = new GenerateBuffClassDialog(mainWindow.getShell());
                dlg.folder = Settings.exportClassDir.getAbsolutePath();
                dlg.packageName = Settings.skillPackage;
                dlg.prefix = Settings.skillClassPrefix;
                if (dlg.open() == Dialog.OK) {
                    Settings.exportClassDir = new File(dlg.folder);
                    Settings.skillPackage = dlg.packageName;
                    Settings.skillClassPrefix = dlg.prefix;
                    try {
                        ProjectData.getActiveProject().generateSkillClasses("GBK");
                        MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
                    } catch (Exception e) {
                        e.printStackTrace();
                        MessageDialog.openInformation(mainWindow.getShell(), "����", e.toString());
                    }
                }
            }
        };

        generateQuestClassesAction = new Action("�Զ���������ʵ����...") {
            public void run() {
                GenerateBuffClassDialog dlg = new GenerateBuffClassDialog(mainWindow.getShell());
                dlg.folder = Settings.exportClassDir.getAbsolutePath();
                dlg.packageName = Settings.questPackage;
                dlg.prefix = Settings.questClassPrefix;
                if (dlg.open() == Dialog.OK) {
                    Settings.exportClassDir = new File(dlg.folder);
                    Settings.questPackage = dlg.packageName;
                    Settings.questClassPrefix = dlg.prefix;
                    try {
                        ProjectData.getActiveProject().generateQuestClasses("GBK");
                        MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
                    } catch (Exception e) {
                        e.printStackTrace();
                        MessageDialog.openInformation(mainWindow.getShell(), "�e�`", e.toString());
                    }
                }
            }
        };
        
        generateAIClassesAction = new Action("�Զ�����AIʵ����...") {
            public void run() {
                GenerateBuffClassDialog dlg = new GenerateBuffClassDialog(mainWindow.getShell());
                dlg.folder = Settings.exportClassDir.getAbsolutePath();
                dlg.packageName = Settings.aiPackage;
                dlg.prefix = Settings.aiClassPrefix;
                if (dlg.open() == Dialog.OK) {
                    Settings.exportClassDir = new File(dlg.folder);
                    Settings.aiPackage = dlg.packageName;
                    Settings.aiClassPrefix = dlg.prefix;
                    try {
                        ProjectData.getActiveProject().generateAIClasses("GBK");
                        MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
                    } catch (Exception e) {
                        e.printStackTrace();
                        MessageDialog.openInformation(mainWindow.getShell(), "�e�`", e.toString());
                    }
                }
            }
        };
        
        generateMapListAction = new Action("���������б�...") {
            public void run() {
                FileDialog dlg = new FileDialog(mainWindow.getShell(), SWT.SAVE);
                dlg.setFilterExtensions(new String[] { "*.txt", "*.*" });
                dlg.setFilterNames(new String[] { "�ı��ļ�(*.txt)", "�����ļ�(*.*)" });
                String outFile = dlg.open();
                if (outFile != null) {
                    try {
                        String text = ProjectData.getActiveProject().generateMapList();
                        Utils.saveFileContent(new File(outFile), text);
                        MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
                    } catch (Exception e) {
                        MessageDialog.openInformation(mainWindow.getShell(), "����", e.toString());
                        e.printStackTrace();
                    }
                }
            }
        };
//        packEquipmentsAction = new Action("�������װ������"){
//            public void run(){
//                try{
//                    EquipmentPacker ep = new EquipmentPacker();
//                    ep.packAll();
//                }catch(Exception e){
//                    MessageDialog.openError(mainWindow.getShell(), "����", e.toString());
//                    e.printStackTrace();
//                }
//            }
//        };
        updateAllNpcmapAction = new Action("���/��������NPC���ú͵�ͼ����...") {
            public void run() {
                try {
                    ProjectData.getActiveProject().validateMixedText();
                    MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
                } catch (Exception e) {
                    e.printStackTrace();
                    MessageDialog.openInformation(mainWindow.getShell(), "����", e.toString());
                }
            }
        };

        updatepriceAction = new Action("��������װ���۸�/�;�...") {
            public void run() {
                try {
                    ProjectData.getActiveProject().updateEquipmentPrices();
                    MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
                } catch (Exception e) {
                    e.printStackTrace();
                    MessageDialog.openInformation(mainWindow.getShell(), "����", e.toString());
                }
            }
        };

        cleanGabageAction = new Action("����û���õ�����Դ...") {
            public void run() {
                try {
                    ProjectData.getActiveProject().cleanGabage(mainWindow.getShell());
                } catch (Exception e) {
                    e.printStackTrace();
                    MessageDialog.openInformation(mainWindow.getShell(), "����", e.toString());
                }
            }
        };
        
        optimizeAnimationAction = new Action("�Ż������ļ�Ŀ¼...") {
            public void run() {
                try {
                    ProjectData.getActiveProject().optimizeAnimations(mainWindow.getShell());
                } catch (Exception e) {
                    e.printStackTrace();
                    MessageDialog.openInformation(mainWindow.getShell(), "����", e.toString());
                }
            }
        };
        
        adjustImageModeAction = new Action("��������ͼƬ�ļ�...") {
            public void run() {
                ProjectData.getActiveProject().modifyMergeMode(mainWindow.getShell());
            }
        };
        
        exportQuestIndexForExcel = new Action("����������Ϣ..."){
            public void run(){
                String questNameKey = null;
                String equipNameKey = null;
                ExportQuestDialog eqd = new ExportQuestDialog(mainWindow.getShell());
                if (eqd.open() == Dialog.OK) {
                    questNameKey = eqd.getQuestNameKey();
                    equipNameKey = eqd.getEquipNameKey();
                } else {
                    return;
                }
                
                FileDialog fd = new FileDialog(mainWindow.getShell(), SWT.SAVE);
                fd.setFilterExtensions(new String[] { "*.xls", "*.*" });
                String inFile = fd.open();
                System.out.println("inFile===" + inFile);
                if(inFile != null){
                    QuestExportToExcel qete = new QuestExportToExcel(questNameKey, equipNameKey);
                    qete.saveQuestToExcel(inFile);
                }
                MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
            }
        };
        
        exportItemIndexForExcel = new Action("������Ʒ��Ϣ..."){
            public void run(){
                FileDialog fd = new FileDialog(mainWindow.getShell(), SWT.SAVE);
                fd.setFilterExtensions(new String[] { "*.xls", "*.*" });
                String inFile = fd.open();
                System.out.println("inFile===" + inFile);
                if(inFile != null){
                    ItemExportToExcel export = new ItemExportToExcel(false);
                    export.saveItemToExcel(inFile);
                }
                MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
            }
        };
        
        exportItemIndexForExcelWithIcon = new Action("������Ʒ��Ϣ(��ͼ��)..."){
            public void run(){
                FileDialog fd = new FileDialog(mainWindow.getShell(), SWT.SAVE);
                fd.setFilterExtensions(new String[] { "*.xls", "*.*" });
                String inFile = fd.open();
                System.out.println("inFile===" + inFile);
                if(inFile != null){
                    ItemExportToExcel export = new ItemExportToExcel(true);
                    export.saveItemToExcel(inFile);
                }
                MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
            }
        };
        
        exportEquipmentIndexForExcel = new Action("����װ����Ϣ..."){
            public void run(){
                FileDialog fd = new FileDialog(mainWindow.getShell(), SWT.SAVE);
                fd.setFilterExtensions(new String[] { "*.xls", "*.*" });
                String inFile = fd.open();
                System.out.println("inFile===" + inFile);
                if(inFile != null){
                    EquipmentExportToExcel export = new EquipmentExportToExcel(false);
                    export.saveEquipmentToExcel(inFile);
                }
                MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
            }
        };
        
        exportEquipmentIndexForExcelWithIcon = new Action("����װ����Ϣ(��ͼ��)..."){
            public void run(){
                FileDialog fd = new FileDialog(mainWindow.getShell(), SWT.SAVE);
                fd.setFilterExtensions(new String[] { "*.xls", "*.*" });
                String inFile = fd.open();
                System.out.println("inFile===" + inFile);
                if(inFile != null){
                    EquipmentExportToExcel export = new EquipmentExportToExcel(true);
                    export.saveEquipmentToExcel(inFile);
                }
                MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
            }
        };
        
        exportMapPng = new Action("�������е�ͼPng..."){
            public void run(){
                MapExportPng.exportMapPng();
                MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ�!������data/map_png");
            }
        };
        
        exportMapInfo = new Action("�������е�ͼ��Ϣ..."){
            public void run(){
                FileDialog fd = new FileDialog(mainWindow.getShell(), SWT.SAVE);
                fd.setFilterExtensions(new String[] { "*.xls", "*.*" });
                String inFile = fd.open();
                if(inFile != null){
                    GameMapExportToExcel export = new GameMapExportToExcel();
                    export.saveGameMapToExcel(inFile);
                    MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
                }
            }
        };
        
        extractPackageFileAction = new Action("�ؿ��ļ����..."){
            public void run(){
                FileDialog fd = new FileDialog(mainWindow.getShell(), SWT.OPEN);
                fd.setFilterExtensions(new String[] { "*.pkg" });
                fd.setFilterNames(new String[] { "�ؿ��ļ�(*.pkg)" });
                String inFile = fd.open();
                if (inFile != null) {
                    try {
                        PackageFile pkg = new PackageFile();
                        pkg.load(new File(inFile));
                        File dir = new File(new File(inFile).getParentFile(), new File(inFile).getName() + "_extract");
                        dir.mkdirs();
                        for (PackageFileItem item : pkg.getFiles()) {
                            Utils.saveFileData(new File(dir, item.name), item.data);
                        }
                    } catch (Exception e) {
                        MessageDialog.openError(mainWindow.getShell(), "����", e.toString());
                    }
                }
            }
        };
        
        exportMapNPCInfo = new Action("�������е�ͼNPC���ּ�����...") {
            public void run(){
                DirectoryDialog dlg = new DirectoryDialog(mainWindow.getShell());
                dlg.setText("�����ؿ�NPC���ּ�����");
                dlg.setMessage("��ѡ�񵼳�Ŀ¼��");
                String newPath = dlg.open();
                ProjectData proj = ProjectData.getActiveProject();
                List<DataObject> areas = null;
                try {
                    areas = proj.getDataListByType(GameArea.class);
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
                File dir = new File(newPath,"���йؿ�NPC.txt");
                File tmpFile = null;
                if (dir.exists()) {
                    deleteFile(dir);
                }
                try {
                    dir.createNewFile();
                    tmpFile = File.createTempFile("desc", ".txt");
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
                OutputStreamWriter dos = null;
                try {
                    dos = new OutputStreamWriter(new FileOutputStream(tmpFile));
                }catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                if(newPath != null) {
                    if(areas.size() != 0) {
                        for (int i = 0; i < areas.size(); i++) {
                            GameArea ga = (GameArea)areas.get(i);
                            GameAreaInfo areaInfo = new GameAreaInfo(ga);
                            try {
                                areaInfo.load();
                            }catch (Exception e2) {
                                e2.printStackTrace();
                            }
                            List<GameMapInfo> gmis = areaInfo.maps;
                            for(int j = 0;j < gmis.size();j++) {
                                try {
                                    
                                    GameMapInfo gmi = gmis.get(j);
                                    for(GameMapObject gmo:gmi.objects){
                                        String str = gmo.toString() + "(" + gmo.x + "," + gmo.y + ")" + "\r\n";
//                                  if(str.indexOf("���͵�") > -1){
//                                      continue; 
//                                  }
                                    if(str.indexOf("�����") > -1) {
                                        continue;
                                    }
                                    if(str.indexOf("com.pip.game.data.map") > -1) {
                                        continue;
                                    }
                                        dos.write(str);
                                    }
                                }catch (Exception e) {
                                    MessageDialog.openError(mainWindow.getShell(), "����", e.toString());
                                }
                            }
                        }
                        try {
                            dos.close();
                            EFSUtil.copyFile(tmpFile, dir);         
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        tmpFile.delete();
                    }
                }
                MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
            }
        };

        generatePackageAction = new Action("���ɿͻ������ذ�...") {
            public void run() {
                try {
                    ProjectData.getActiveProject().makeClientPackages();
                } catch (Exception e) {
                    e.printStackTrace();
                    MessageDialog.openInformation(mainWindow.getShell(), "ʧ��", "����ʧ�ܣ�");
                    return;
                }
                MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
                
            }
        };
        
        generateWorldMapAction = new Action("���������ͼ���ذ�..."){
            public void run(){
                FileDialog dlg = new FileDialog(mainWindow.getShell(), SWT.OPEN);
                dlg.setFilterExtensions(new String[] { "*.map", "*.*" });
                dlg.setFilterNames(new String[] { "��ͼ�ļ�(*.map)", "�����ļ�(*.*)" });
                String inFile = dlg.open();
                if(inFile != null){
                    File mapf = new File(inFile);
                    ProjectData.getActiveProject().makeWorldMapPackages(mapf);
                    MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
                }
            }
        };
        
        generateWorldMapInfoAction = new Action("���������ͼ��ϸ��Ϣ����"){
            public void run(){
                FileDialog dlg = new FileDialog(mainWindow.getShell(), SWT.OPEN);
                dlg.setFilterExtensions(new String[] { "*.xls", "*.*" });
                dlg.setFilterNames(new String[] { "��ͼ��Ϣ�ļ�(*.xls)", "�����ļ�(*.*)" });
                String inFile = dlg.open();
                if(inFile != null){
                    WorldMapDataForExcel.save(inFile);
                    MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
                }
            }
        };

        i18nDataAction = new Action("������Ŀ����...") {
            public void run() {
                try {
                    List<LocaleConfig> locales = LocaleConfig.getLocales(ProjectData.getActiveProject());
                    if (locales.size() == 0) {
                        throw new Exception("û�������������ԡ�");
                    }
                    GenericChooseDialog dlg = new GenericChooseDialog(mainWindow.getShell(), "ѡ������", locales);
                    if (dlg.open() == Dialog.OK) {
                        LocaleConfig locale = (LocaleConfig)dlg.getSelection();
                        I18NProcessor proc = new I18NProcessor(ProjectData.getActiveProject(), locale);
                        proc.process(true, false);
                        if (I18NError.hasError()) {
                            mainWindow.getActivePage().openEditor(new NullInput(), com.pip.game.editor.I18NErrorEditor.ID);
                        } else {
                            MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    MessageDialog.openError(mainWindow.getShell(), "����", e.toString());
                }
            }
        };

        i18nCodeAction = new Action("����Դ����...") {
            public void run() {
                try {
                    List<LocaleConfig> locales = LocaleConfig.getLocales(ProjectData.getActiveProject());
                    if (locales.size() == 0) {
                        throw new Exception("û�������������ԡ�");
                    }
                    DirectoryDialog dlg = new DirectoryDialog(mainWindow.getShell());
                    dlg.setText("Դ����Ŀ¼");
                    dlg.setMessage("��ѡ��Դ����Ŀ¼��");
                    String newPath = dlg.open();
                    if (newPath == null) {
                        return;
                    }
                    GenericChooseDialog dlg2 = new GenericChooseDialog(mainWindow.getShell(), "ѡ������", locales);
                    if (dlg2.open() == Dialog.OK) {
                        LocaleConfig locale = (LocaleConfig)dlg2.getSelection();
                        I18NProcessor proc = new I18NProcessor(new File(newPath), locale);
                        proc.process(true, false);
                        if (I18NError.hasError()) {
                            mainWindow.getActivePage().openEditor(new NullInput(), com.pip.game.editor.I18NErrorEditor.ID);
                        } else {
                            MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
                        }
                    }
                } catch (Throwable e) {
                    MessageDialog.openError(mainWindow.getShell(), "����", e.toString());
                }
            }
        };

        i18nCodeAction2 = new Action("����Դ����(����ȡ)...") {
            public void run() {
                try {
                    List<LocaleConfig> locales = LocaleConfig.getLocales(ProjectData.getActiveProject());
                    if (locales.size() == 0) {
                        throw new Exception("û�������������ԡ�");
                    }
                    DirectoryDialog dlg = new DirectoryDialog(mainWindow.getShell());
                    dlg.setText("Դ����Ŀ¼");
                    dlg.setMessage("��ѡ��Դ����Ŀ¼��");
                    String newPath = dlg.open();
                    if (newPath == null) {
                        return;
                    }
                    GenericChooseDialog dlg2 = new GenericChooseDialog(mainWindow.getShell(), "ѡ������", locales);
                    if (dlg2.open() == Dialog.OK) {
                        LocaleConfig locale = (LocaleConfig)dlg2.getSelection();
                        I18NProcessor proc = new I18NProcessor(new File(newPath), locale);
                        proc.process(false, false);
                        if (I18NError.hasError()) {
                            mainWindow.getActivePage().openEditor(new NullInput(), com.pip.game.editor.I18NErrorEditor.ID);
                        } else {
                            MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
                        }
                    }
                } catch (Throwable e) {
                    MessageDialog.openError(mainWindow.getShell(), "����", e.toString());
                }
            }
        };
        
        i18nScriptAction = new Action("����ű�(������)...") {
            public void run() {
                try {
                    List<LocaleConfig> locales = LocaleConfig.getLocales(ProjectData.getActiveProject());
                    if (locales.size() == 0) {
                        throw new Exception("û�������������ԡ�");
                    }
                    DirectoryDialog dlg = new DirectoryDialog(mainWindow.getShell());
                    dlg.setText("�ű�Ŀ¼");
                    dlg.setMessage("��ѡ��ű�Ŀ¼��");
                    String newPath = dlg.open();
                    if (newPath == null) {
                        return;
                    }
                    GenericChooseDialog dlg2 = new GenericChooseDialog(mainWindow.getShell(), "ѡ������", locales);
                    if (dlg2.open() == Dialog.OK) {
                        LocaleConfig locale = (LocaleConfig)dlg2.getSelection();
                        I18NProcessor proc = new I18NProcessor(new File(newPath), locale, I18NProcessor.MODE_SCRIPT);
                        proc.process(true, false);
                        if (I18NError.hasError()) {
                            mainWindow.getActivePage().openEditor(new NullInput(), com.pip.game.editor.I18NErrorEditor.ID);
                        } else {
                            MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
                        }
                    }
                } catch (Throwable e) {
                    MessageDialog.openError(mainWindow.getShell(), "����", e.toString());
                }
            }
        };
        
        i18nMergeAction = new Action("�ϲ������ļ�...") {
            public void run() {
                // ѡ�����ļ�
                FileDialog fdlg = new FileDialog(mainWindow.getShell(), SWT.OPEN);
                fdlg.setText("��ѡ����Ҫ��������ļ���");
                fdlg.setFilterExtensions(new String[] { "*.xls" });
                fdlg.setFilterNames(new String[] { "Excel������" });
                String newFile = fdlg.open();
                if (newFile == null) {
                    return;
                }
                
                // ѡ����ļ�
                fdlg = new FileDialog(mainWindow.getShell(), SWT.OPEN);
                fdlg.setText("��ѡ����������ı��ľ��ļ���");
                fdlg.setFilterExtensions(new String[] { "*.xls" });
                fdlg.setFilterNames(new String[] { "Excel������" });
                String oldFile = fdlg.open();
                if (oldFile == null) {
                    return;
                }
                
                try {
                    MessageFile.merge(newFile, oldFile);
                    MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
                } catch (Exception e) {
                    MessageDialog.openError(mainWindow.getShell(), "����", e.toString());
                }
            }
        };

        i18nCheckAction = new Action("��鷭���ļ�...") {
            public void run() {
                // ѡ���ļ�
                FileDialog fdlg = new FileDialog(mainWindow.getShell(), SWT.OPEN);
                fdlg.setText("��ѡ����Ҫ�����ļ���");
                fdlg.setFilterExtensions(new String[] { "*.xls" });
                fdlg.setFilterNames(new String[] { "Excel������" });
                String newFile = fdlg.open();
                if (newFile == null) {
                    return;
                }
                
                try {
                    // ѡ������
                    List<LocaleConfig> locales = LocaleConfig.getLocales(ProjectData.getActiveProject());
                    if (locales.size() == 0) {
                        throw new Exception("û�������������ԡ�");
                    }
                    GenericChooseDialog dlg = new GenericChooseDialog(mainWindow.getShell(), "ѡ������", locales);
                    if (dlg.open() == Dialog.OK) {
                        LocaleConfig locale = (LocaleConfig)dlg.getSelection();
                        MessageFile mf = new MessageFile(new File(newFile), "zh_CN", locale.id);
                        if (mf.getErrorCount() > 0) {
                            fdlg = new FileDialog(mainWindow.getShell(), SWT.SAVE);
                            fdlg.setText("��ѡ�񵼳������ļ���");
                            fdlg.setFilterExtensions(new String[] { "*.xls" });
                            fdlg.setFilterNames(new String[] { "Excel������" });
                            newFile = fdlg.open();
                            if (newFile != null) {
                                mf.reportErrorToExcel(newFile);
                                MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�ѵ���" + mf.getErrorCount() + "������");
                            }
                        } else {
                            MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "û�з��ִ���");
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    MessageDialog.openError(mainWindow.getShell(), "����", e.toString());
                }
            }
        };

        i18nAdjustRefAction = new Action("У�������ļ��е�NPC����...") {
            public void run() {
                // ѡ���ļ�
                FileDialog fdlg = new FileDialog(mainWindow.getShell(), SWT.OPEN);
                fdlg.setText("��ѡ����Ҫ�����ļ���");
                fdlg.setFilterExtensions(new String[] { "*.xls" });
                fdlg.setFilterNames(new String[] { "Excel������" });
                String newFile = fdlg.open();
                if (newFile == null) {
                    return;
                }
                
                try {
                    MessageFile.adjustRef(ProjectData.getActiveProject(), newFile);
                    MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ��������׼�����");
                } catch (Throwable e) {
                    e.printStackTrace();
                    MessageDialog.openError(mainWindow.getShell(), "����", e.toString());
                }
            }
        };
        
        userLocationAction = new Action("�����û��ֲ�ͼ...") {
            public void run() {
                FileDialog dlg = new FileDialog(mainWindow.getShell(), SWT.OPEN);
                dlg.setFilterExtensions(new String[] { "*.txt", "*.*" });
                dlg.setFilterNames(new String[] { "�ı��ļ�(*.txt)", "�����ļ�(*.*)" });
                String inFile = dlg.open();
                if(inFile != null) {
                    try {
                        new PlayerLocationMapMaker(new File(inFile)).make();
                        MessageDialog.openInformation(mainWindow.getShell(), "�ɹ�", "�����ɹ���");
                    } catch (Throwable e) {
                        e.printStackTrace();
                        MessageDialog.openError(mainWindow.getShell(), "����", e.toString());
                    }
                }
            }
        };
        
        openglAction = new Action("OpenGLģʽ", IAction.AS_CHECK_BOX) {
            public void run() {
                GLUtils.glEnabled = !GLUtils.glEnabled;
                DataListView dlv = (DataListView)mainWindow.getActivePage().findView(DataListView.ID);
                if (dlv != null) {
                    dlv.initFirstGLWin();
                }
                if (GLUtils.glEnabled) {
                    ParticleEffectManager.init(ProjectData.getActiveProject());
                }
            }
        };
        openglAction.setImageDescriptor(ResourceManager.getPluginImageDescriptor(WorkshopPlugin.getDefault(), "icons/gl.gif"));
        openglAction.setToolTipText("����/�ر�OpenGLģʽ");
        openglAction.setChecked(GLUtils.glEnabled);
    }
    
    protected void openWorkingDir() {
        String cmd = "explorer.exe \"" + Settings.workingDir + "\"";
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
        }        
    }

    private ProjectOwner prjOwner;
    public MenuManager resourceManagerMenu;
    // �����Ի���ѡ���µ�����Ŀ¼��
    private void switchProject() {
        if (!mainWindow.getActivePage().closeAllEditors(true)) {            
            return;
        }
        ProjectData prj = ProjectData.getActiveProject();
        DirectoryDialog dlg = new DirectoryDialog(mainWindow.getShell());
        dlg.setFilterPath(prj.baseDir.getAbsolutePath());
        dlg.setText("ѡ��Ŀ¼");
        dlg.setMessage("��ѡ����ĿĿ¼��");
        String newPath = dlg.open();
        if (newPath != null) {
            Settings.workingDir = new java.io.File(newPath);
            try {
                prj.load(new java.io.File(newPath), prj.config.getProjectClassLoader());
                if(prjOwner==null){
                    prjOwner = ProjectOwner.find(newPath, true);
                }
                prjOwner.refreshFileMap(newPath, true);
                if (GLUtils.glEnabled) {
                    ParticleEffectManager.init(prj);
                }
            } catch (Exception e) {
                MessageDialog.openError(mainWindow.getShell(), "�������ݴ���", e.toString());
                return;
            }
            
            try {
                IViewPart viewPart = mainWindow.getActivePage().findView(DataListView.ID);
                if (viewPart != null) {
                    mainWindow.getActivePage().hideView(viewPart);
                }
                DataListView view = (DataListView)mainWindow.getActivePage().showView(DataListView.ID);
                if (view != null) {
                    view.setup();
                }
            } catch (Exception e) {
                e.printStackTrace();
                MessageDialog.openError(null, "�������ݴ���", e.toString());
            }

        }
    }
    
    /**
     * ɾ��һ���ļ�
     * @param dir
     */
    public static void deleteFile(File dir){
        if(dir.isFile()) {
            dir.delete();
        }
    }

}
