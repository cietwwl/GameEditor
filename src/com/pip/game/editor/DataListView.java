package com.pip.game.editor;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.pip.game.data.DataObject;
import com.pip.game.data.DataObjectCategory;
import com.pip.game.data.GameArea;
import com.pip.game.data.IGameDataListener;
import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.data.AI.AIData;
import com.pip.game.data.equipment.Equipment;
import com.pip.game.data.item.Item;
import com.pip.game.data.quest.Quest;
import com.pip.game.editor.item.ItemTreeViewer;
import com.pip.mango.jni.GLUtils;
import com.pip.mango.jni.GLWindow;
import com.pip.util.FileWatcher;
import com.pip.util.IFileModificationListener;

/**
 * 项目中所有类型的数据对象的列表。这个View用一个CTabFolder来划分数据类型，每一个Tab表示 一类数据。
 * 
 * @author lighthu
 */
public class DataListView extends ViewPart implements IFileModificationListener, IGameDataListener {
    public static Display mainDisplay;
    // 包含所有数据类型的面板
    protected CTabFolder dataTypeTabFolder;
    // 各数据类型的Tab
    protected CTabItem[] dataTypeTabs;
    // 各数据类型的表格
    protected TreeViewer[] dataTreeViewers;
    // 各数据类型的表格
    protected Tree[] dataListTree;
    // 各数据类型的过滤输入框
    protected Text[] dataListFilterText;

    // 命令：新建/删除数据对象
    protected Action newAction, deleteAction, newCategory, copyAction;
    public static final String ID = "com.pip.game.editor.DataListView"; //$NON-NLS-1$
    
    private Composite container;
    private GLWindow glDumbWin;

    /**
     * Create contents of the view part
     * 
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {
        mainDisplay = parent.getDisplay();
        createActions();
        initializeMenu();
        initializeToolBar();

        container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout());

        dataTypeTabFolder = new CTabFolder(container, SWT.NONE);
        
        initFirstGLWin();
    }
    
    public void initFirstGLWin() {
        if (GLUtils.glEnabled && glDumbWin == null) {
            glDumbWin = new GLWindow(container.handle);
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
        if (glDumbWin != null) {
            glDumbWin.dispose();
        }
        ParticleEffectManager.clear();
    }

    /**
     * 在项目配置修改后，重新设置项目窗口。
     */
    public void setup() {
        ProjectData.getActiveProject().setDataListener(this);
        if (GLUtils.glEnabled) {
            ParticleEffectManager.init(ProjectData.getActiveProject());
        }
        watchDataFiles();

        if (dataTypeTabs != null) {
            for (CTabItem titem : dataTypeTabs) {
                titem.dispose();
            }
        }
        
        // 创建所有数据类型的Tab
        ProjectConfig config = ProjectData.getActiveProject().config;
        if (config.getEditableClasses() == null) {
            return;
        }
        int typeCount = config.getEditableClasses().length;
        dataTypeTabs = new CTabItem[typeCount];
        dataTreeViewers = new TreeViewer[typeCount];
        dataListTree = new Tree[typeCount];
        dataListFilterText = new Text[typeCount];
        for (int i = 0; i < typeCount; i++) {
            // 创建Tab对象
            dataTypeTabs[i] = new CTabItem(dataTypeTabFolder, SWT.NONE);
            dataTypeTabs[i].setText(ProjectData.getActiveProject().config.getTypeName(ProjectData.getActiveProject().config.getEditableClasses()[i]));
            Composite tabComp = new Composite(dataTypeTabFolder, SWT.NONE);
            GridLayout gd_comp = new GridLayout(1, true);
            gd_comp.marginBottom = 0;
            gd_comp.marginHeight = 0;
            gd_comp.marginLeft = 0;
            gd_comp.marginRight = 0;
            gd_comp.marginTop = 0;
            gd_comp.marginWidth = 0;
            gd_comp.verticalSpacing = 0;
            gd_comp.horizontalSpacing = 0;
            tabComp.setLayout(gd_comp);

            // 创建过滤输入框
            dataListFilterText[i] = new Text(tabComp, SWT.BORDER);
            GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, false);
            dataListFilterText[i].setLayoutData(gd_text);
            dataListFilterText[i].addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                    if (e.character == '\r') {
                        fireFilterText(e.widget);
                    }
                }
            });

            // 创建树对象
            if (ProjectData.getActiveProject().getEditableIndexByType(Item.class) == i  
                    || ProjectData.getActiveProject().getEditableIndexByType(Equipment.class) == i) {
                dataTreeViewers[i] = new ItemTreeViewer(tabComp, SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI);
            }
            else {
                dataTreeViewers[i] = new TreeViewer(tabComp, SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI);
                dataTreeViewers[i].getTree().setLinesVisible(true);
            }
            dataTreeViewers[i].setLabelProvider(new DataObjectLabelProvider());
            dataTreeViewers[i].setContentProvider(new ProjectDataListProvider(ProjectData.getActiveProject().config.getEditableClasses()[i]));
            dataTreeViewers[i].addDoubleClickListener(new IDoubleClickListener() {
                public void doubleClick(DoubleClickEvent event) {
                    StructuredSelection sel = (StructuredSelection) event.getSelection();
                    if (sel.isEmpty()) {
                        return;
                    }
                    Object obj = sel.getFirstElement();
                    if (obj instanceof DataObject) {
                        editObject((DataObject) obj);
                    }
                    else if (obj instanceof DataObjectCategory) {
                        expandOrCollapseNode((TreeViewer) event.getViewer(), obj);
                    }
                    else if (obj instanceof String) {
                        // 新建分类
                        InputDialog dlg = new InputDialog(getSite().getShell(), "新建分类", "请输入新分类的名称：", "新分类",
                                new IInputValidator() {
                                    public String isValid(String newText) {
                                        if (newText.trim().length() == 0) {
                                            return "分类名称不能为空。";
                                        }
                                        else {
                                            return null;
                                        }
                                    }
                                });
                        if (dlg.open() != InputDialog.OK) {
                            return;
                        }
                        String newname = dlg.getValue();
                        int clsIndex = dataTypeTabFolder.getSelectionIndex();
                        ProjectData proj = ProjectData.getActiveProject();
                        try {
                            Class cls = proj.config.getEditableClasses()[clsIndex];
                            proj.newCategory(cls, newname);
                            dataTreeViewers[clsIndex].refresh();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            MessageDialog.openError(getSite().getShell(), "错误", e.toString());
                        }
                    }
                }
            });
            dataListTree[i] = dataTreeViewers[i].getTree();
            dataListTree[i].setHeaderVisible(true);
            GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true);
            dataListTree[i].setLayoutData(gd_tree);

            // 设置DND
            setupDragAndDrop(dataTreeViewers[i]);

            // 设置树表格列
            final TreeColumn column1 = new TreeColumn(dataListTree[i], SWT.LEFT);
            column1.setWidth(200);
            column1.setText("ID");

            final TreeColumn column2 = new TreeColumn(dataListTree[i], SWT.LEFT);
            column2.setWidth(200);
            column2.setText("标题");

            final TreeColumn column3 = new TreeColumn(dataListTree[i], SWT.LEFT);
            column3.setWidth(200);
            column3.setText("备注");

            // 创建右键菜单
            MenuManager mgr = new MenuManager();
            mgr.add(newAction);
            mgr.add(deleteAction);
            mgr.add(newCategory);
            mgr.add(copyAction);
            Menu menu = mgr.createContextMenu(dataListTree[i]);
            dataListTree[i].setMenu(menu);

            // 设置表格内容
            dataTreeViewers[i].setInput(new Object());

            // 把表格加入Tab
            dataTypeTabs[i].setControl(tabComp);
        }
    }

    protected void expandOrCollapseNode(TreeViewer viewer, Object node) {
        if (viewer.getExpandedState(node)) {
            viewer.collapseToLevel(node, 1);
        }
        else {
            viewer.expandToLevel(node, 1);
        }
    }

    /**
     * Create the actions
     */
    public void createActions() {
        newAction = new Action("新建(&N)...") {
            public void run() {
                onNew();
            }
        };

        deleteAction = new Action("删除(&D)") {
            public void run() {
                onDelete();
            }
        };

        newCategory = new Action("新建分类(&C)...") {
            public void run() {
                onNewCategory();
            }
        };
        //复制中，如果对象保存到多个文件中，就会出现很不同对象共享部分数据的BUG（比如任务），不建议使用复制对象
        copyAction = new Action("复制对象") {
            public void run() {
                onCopy();
            }
        };

        // Create the actions
    }

    /**
     * Initialize the toolbar
     */
    private void initializeToolBar() {
        IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
    }

    /**
     * Initialize the menu
     */
    private void initializeMenu() {
        IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();

        // menuManager.add(switchProjectAction);
    }

    @Override
    public void setFocus() {
        // Set the focus
        dataTypeTabFolder.setFocus();
    }

    /**
     * 刷新数据列表。
     */
    public void refresh() {
        for (ContentViewer viewer : dataTreeViewers) {
            viewer.refresh();
        }
    }

    /**
     * 刷新某一类型的数据列表。
     */
    public void refresh(Class cls) {
        for (int i = 0; i < ProjectData.getActiveProject().config.getEditableClasses().length; i++) {
            if (ProjectData.getActiveProject().config.getEditableClasses()[i] == cls) {
                dataTreeViewers[i].refresh();
                break;
            }
        }
    }

    /**
     * 刷新一个对象的显示。
     */
    public void refreshPostion(DataObject obj) {
        Class clazz = obj.getClass();
        for (int i = 0; i < ProjectData.getActiveProject().config.getEditableClasses().length; i++) {
            if (ProjectData.getActiveProject().config.getEditableClasses()[i] == clazz) {
                try {
                    expandOrCollapseNode(dataTreeViewers[i], obj.cate);
                }
                catch (Exception e) {
                }

                break;
            }
        }
    }

    /**
     * 刷新一个对象的显示。
     */
    public void refresh(DataObject obj) {
        Class clazz = obj.getClass();
        for (int i = 0; i < ProjectData.getActiveProject().config.getEditableClasses().length; i++) {
            if (ProjectData.getActiveProject().config.getEditableClasses()[i] == clazz) {
                dataTreeViewers[i].refresh(obj);
                break;
            }
        }
    }

    /**
     * 打开一个数据对象的编辑器。
     */
    public static void editObject(DataObject obj) {
        String editorID = ProjectData.getActiveProject().config.getEditorID(obj.getClass());
        try {

            // 如果发现新建的是其父类，则强制转换为子类并完全拥有父类的属性
            Class[] cls = ProjectData.getActiveProject().config.getEditableClasses();
            for (int i = 0; i < cls.length; i++) {
                if (cls[i].getSuperclass() == obj.getClass()) {
                    Constructor cons = cls[i].getConstructor(ProjectData.class);
                    ProjectData proj = ProjectData.getActiveProject();
                    DataObject newObj = (DataObject) cons.newInstance(proj);
                    newObj.update(obj);
                    newObj.cate = obj.cate;
                    proj.deleteObject(obj);
                    obj = newObj;
                    proj.addObjectToList(cls[i], obj, obj);
                    proj.saveDataList(cls[i]);
                    break;
                }
            }
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new DataObjectInput(obj), editorID);
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "错误", "打开编辑器失败，原因：\n" + e.toString());
        }
    }

    /**
     * 强制停止编辑某个对象。
     */
    private void stopEdit(DataObject obj) {
        IEditorPart editor = getSite().getWorkbenchWindow().getActivePage().findEditor(new DataObjectInput(obj));
        if (editor != null) {
            getSite().getWorkbenchWindow().getActivePage().closeEditor(editor, false);
        }
    }

    // 新建当前类型的数据对象。
    private void onNew() {
        try {
            int tabIndex = dataTypeTabFolder.getSelectionIndex();
            Runnable wizard = ProjectData.getActiveProject().config.getCreateWizard(ProjectData.getActiveProject().config.getEditableClasses()[tabIndex]);
            wizard.run();
            this.refresh();
        }
        catch (Exception e) {
            e.printStackTrace();
            MessageDialog.openError(getSite().getShell(), "错误", "创建对象失败，原因：\n" + e.toString());
        }
    }

    
    //删除所选中的分类或数据对象
    private void onDelete(){
        int tabIndex = dataTypeTabFolder.getSelectionIndex();
        StructuredSelection sel = (StructuredSelection)dataTreeViewers[tabIndex].getSelection();
        if (sel.isEmpty()) {
            return;
        }
        Object[] selObjs = sel.toArray();
        for(Object obj : selObjs){
            Object[] delobjs = findWillDeletObjects(obj);
            Object[] delcates = findWillDeleteCategory(obj);
            try{
                if(!MessageDialog.openConfirm(getSite().getShell(), "删除确认", obj.toString())){
                    return;
                }
                if(selObjs.length > 0){
                    onDeleteObjects(delobjs);//删除直接对象和关联的对象
                }
                if(delcates.length > 0){
                    onDeleteCategorys(delcates);//删除分类
                }
            }catch(Exception e){
                
            }
        }
        dataTreeViewers[tabIndex].refresh();
    }
    
    //删除当前选中的分类及其下的分类
    private void onDeleteCategorys(Object[] categorys) throws Exception {
        int index = ProjectData.getActiveProject().getIndexByType((((DataObjectCategory) categorys[0]).dataClass).getSuperclass());
        for (int i = 0; i < categorys.length; i++) {
            DataObjectCategory pc = ((DataObjectCategory) categorys[i]).parent;
            if (pc != null) {
                pc.cates.remove(categorys[i]);// 从父分类移除
            }
            else {
                if (ProjectData.getActiveProject().dataCateLists[index].contains(categorys[i])) {
                    ProjectData.getActiveProject().dataCateLists[index].remove(categorys[i]);// 从列表移除
                }
                else {
                    throw new Exception("分类删除错误！");
                }
            }
        }
    }

    
 // 删除当前选中及其下的数据对象。
    private void onDeleteObjects(Object[] selObjs) {
    
        // 找出所有依赖于选中对象的数据对象
        List<DataObject> relateObjects = ProjectData.getActiveProject().findRelateObjects(selObjs);
        
        // 提示用户确认删除
        StringBuffer buf = new StringBuffer();
        buf.append("请确认是否删除以下数据对象：\n");
        for (Object obj : selObjs) {
            if(obj instanceof Item){
                /* 物品 */
                buf.append("物品");
                buf.append(": ");
                buf.append(obj.toString());
                buf.append("\n");
            }
            else if(obj instanceof Equipment){
                /* 装备 */
                buf.append("装备 ");
                buf.append(": ");
                buf.append(obj.toString());
                buf.append("\n");
            }
            else{
                /* 普通类型 */
                buf.append(ProjectData.getActiveProject().config.getTypeName(obj.getClass()));
                buf.append(": ");
                buf.append(obj.toString());
                buf.append("\n");
            }
        }
        if (relateObjects.size() > 0) {
            buf.append("下列相关对象也将一起被删除：\n");
            for (DataObject obj : relateObjects) {
                buf.append(ProjectData.getActiveProject().config.getTypeName(obj.getClass()));
                buf.append(": ");
                buf.append(obj.toString());
                buf.append("\n");
            }
        }
        buf.setLength(buf.length() - 1);
        if (!MessageDialog.openConfirm(getSite().getShell(), "删除确认", buf.toString())) {
            return;
        }
        
        // 删除所有选中对象和相关对象
        for (Object obj : selObjs) {
            DataObject dobj = (DataObject)obj;
            stopEdit(dobj);
            ProjectData.getActiveProject().deleteObject(dobj);
        }
        for (DataObject obj : relateObjects) {
            stopEdit(obj);
            ProjectData.getActiveProject().deleteObject(obj);
        }
        try {
            ProjectData.getActiveProject().saveAll();
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.openError(getSite().getShell(), "错误", "保存数据失败，原因：\n" + e.toString());
        }
        // 刷新列表
        refresh();
    }
    
    //找到要删除的选中及其下的所有分类
    private Object[] findWillDeleteCategory(Object obj){
        ArrayList<Object> objects = new ArrayList<Object>();
        if(obj instanceof DataObjectCategory){
            objects.add(obj);
            List<DataObjectCategory> cates = ((DataObjectCategory)obj).cates;
            for(int i = 0; i < cates.size(); i++){
                Object[] inobj = findWillDeleteCategory(cates.get(i));
                for(int j = 0; j < inobj.length; j++){
                    objects.add(inobj[j]);
                }
            }
        }
        return objects.toArray();
    }
    
    //找到要删除的选中及其下的所有对象
    private Object[] findWillDeletObjects(Object obj){
        ArrayList<Object> objects = new ArrayList<Object>();
        if(obj instanceof DataObjectCategory){
            List<DataObjectCategory> cates = ((DataObjectCategory)obj).cates;
            for(int i = 0; i < cates.size(); i++){
                Object[] inobj =findWillDeletObjects(cates.get(i));
                for(int j = 0; j < inobj.length; j++){
                    objects.add(inobj[j]);
                }
            }
            for(DataObject object : ((DataObjectCategory)obj).objects){
                objects.add(object);
            }
        }else if(obj instanceof DataObject){
            objects.add(obj);
        }
        return objects.toArray();
    }

    private void onCopy() {
        int tabIndex = dataTypeTabFolder.getSelectionIndex();
        StructuredSelection sel = (StructuredSelection) dataTreeViewers[tabIndex].getSelection();

        if (sel.isEmpty()) {
            MessageDialog.openError(getSite().getShell(), "错误", "我的神啊!赐给我一个可以复制的对象吧！");
            return;
        }
        Object obj = ((TreeSelection) sel).getFirstElement();
        if (obj instanceof DataObject) {
            try {
                DataObject copyObj = null;
                if (obj instanceof Equipment) {
                    copyObj = ProjectData.getActiveProject().newEquipment(((Equipment) obj).cate, obj);
                }else if (obj instanceof Item) {
                    copyObj = ProjectData.getActiveProject().newItem(((Item) obj).cate, obj);
                }else if (obj instanceof Quest || obj instanceof GameArea || obj instanceof AIData) {
                    MessageDialog.openError(getSite().getShell(), "错误", "这个是不可以复制的！！！！");
                    return ;
                } else {
                    copyObj = ProjectData.getActiveProject().newObject(obj.getClass(), getSelectObject());
                }
                ProjectData proj = ProjectData.getActiveProject();
                proj.deleteObject(copyObj);
                int id = copyObj.id;
                DataObjectCategory cate = ((DataObject) obj).cate;
                // 如果发现新建的是其父类，则强制转换为子类并完全拥有父类的属性
                Class[] cls = ProjectData.getActiveProject().config.getEditableClasses();
                for (int i = 0; i < cls.length; i++) {
                    if (cls[i].getSuperclass() == copyObj.getClass()) {
                        Constructor cons = cls[i].getConstructor(ProjectData.class);
                        DataObject newObj = (DataObject) cons.newInstance(proj);
                        newObj.update((DataObject) copyObj);
                        copyObj = newObj;
                        break;
                    }
                }
                ;

                copyObj.update((DataObject) obj);
                copyObj.id = id;
                copyObj.cate = cate;

                proj.addObjectToList(copyObj.getClass(), copyObj, copyObj);
                proj.saveDataList(copyObj.getClass());
                Class[] types = new Class[] { copyObj.getClass() };
//                proj.load(proj.baseDir);
                copyObj = proj.findObject(copyObj.getClass(), copyObj.id);
                editObject(copyObj);
                this.refresh(copyObj);
                this.refresh();
                refreshPostion(copyObj);
            }catch (Exception e) {
                MessageDialog.openError(getSite().getShell(), "错误", "我的神啊!" + e.toString());
            }
        }else {
            MessageDialog.openError(getSite().getShell(), "错误", "我的神啊!切换复制对象吧！");
        }
    }

    private void onNewCategory() {
        int tabIndex = dataTypeTabFolder.getSelectionIndex();
        StructuredSelection sel = (StructuredSelection) dataTreeViewers[tabIndex].getSelection();

        if (sel.isEmpty()) {
            return;
        }
        // 新建分类
        InputDialog dlg = new InputDialog(getSite().getShell(), "新建分类", "请输入新分类的名称：", "新分类", new IInputValidator() {
            public String isValid(String newText) {
                if (newText.trim().length() == 0) {
                    return "分类名称不能为空。";
                }
                else {
                    return null;
                }
            }
        });
        if (dlg.open() != InputDialog.OK) {
            return;
        }
        Object obj = ((TreeSelection) sel).getFirstElement();
        String newname = dlg.getValue();

        int clsIndex = dataTypeTabFolder.getSelectionIndex();
        ProjectData proj = ProjectData.getActiveProject();
        try {
            Class cls = ProjectData.getActiveProject().config.getEditableClasses()[clsIndex];

            // 鼠标选中的位置可能会不同
            if (obj instanceof DataObjectCategory) {
                proj.newCategory((DataObjectCategory) obj, cls, newname);
            }
            else if (obj instanceof DataObject) {
                proj.newCategory(((DataObject) obj).cate, cls, newname);
            }else if(obj instanceof String){
                proj.newCategory(cls, newname);//当选中"新建分类..."时，执行右键，而不是双击时
            }

            dataTreeViewers[clsIndex].refresh();
        }
        catch (Exception e) {
            e.printStackTrace();
            MessageDialog.openError(getSite().getShell(), "错误", e.toString());
        }

    }

    /**
     * 取得选中的对象。
     * 
     * @return
     */
    public Object[] getSelectedObjects() {
        int tabIndex = dataTypeTabFolder.getSelectionIndex();
        StructuredSelection sel = (StructuredSelection) dataTreeViewers[tabIndex].getSelection();
        return sel.toArray();
    }
    
    public Class getEditingClass() {
        int tabIndex = dataTypeTabFolder.getSelectionIndex();
        ProjectConfig config = ProjectData.getActiveProject().config;
        return config.getEditableClasses()[tabIndex];
    }

    /**
     * 过滤器文本内容修改。
     */
    public void fireFilterText(Widget widget) {
        int index = -1;
        for (int i = 0; i < dataListFilterText.length; i++) {
            if (dataListFilterText[i] == widget) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return;
        }
        String newText = dataListFilterText[index].getText();
        ProjectDataListProvider provider = (ProjectDataListProvider) dataTreeViewers[index].getContentProvider();
        provider.setFilterText(newText);
        dataTreeViewers[index].setSelection(new StructuredSelection());
        dataTreeViewers[index].refresh(true);
    }

    /*
     * 监控项目中的所有数据文件，以防误存覆盖。
     */
    public void watchDataFiles() {
        if (ProjectData.getActiveProject().config.supportDataClasses == null) {
            return;
        }
        FileWatcher.unwatch(this);
        for (Class cls : ProjectData.getActiveProject().config.supportDataClasses) {
            File f = ProjectData.getActiveProject().getDataFile(cls);
            if (f.exists()) {
                FileWatcher.watch(f, this);
            }
        }
    }

    /**
     * 当编辑器保存数据时，暂时取消监听，以避免没有必要的警告。
     */
    public void saveStart(Class cls) {
        File f = ProjectData.getActiveProject().getDataFile(cls);
        if (f.exists()) {
            FileWatcher.unwatch(f, this);
        }
    }

    public void saveEnd(Class cls) {
        File f = ProjectData.getActiveProject().getDataFile(cls);
        if (f.exists()) {
            FileWatcher.watch(f, this);
        }
    }

    /**
     * 文件监控通知。
     */
    public void fileModified(File f) {
        for (Class cls : ProjectData.getActiveProject().config.supportDataClasses) {
            File f1 = ProjectData.getActiveProject().getDataFile(cls);
            if (f1.equals(f)) {
                getSite().getShell().getDisplay().asyncExec(new DataChangedHandler(cls));
                break;
            }
        }
    }

    class DataChangedHandler implements Runnable {
        private Class changedClass;

        public DataChangedHandler(Class cls) {
            changedClass = cls;
        }

        public void run() {
            String dataName = ProjectData.getActiveProject().config.getTypeName(changedClass);
            if (dataName == null) {
                dataName = changedClass.getName();
            }
            String msg = dataName + "数据被外部程序改变，是否重载？\n" + "注：如果选择是，所有已打开的编辑窗口将被关闭，数据不会保存；如果选择否，"
                    + "那么在你继续编辑并保存数据时，将有可能覆盖别人的修改！";
            if (MessageDialog.openConfirm(getSite().getShell(), "警告", msg) == false) {
                return;
            }
            try {
                // 重载
                ProjectData proj = ProjectData.getActiveProject();
                proj.load(proj.baseDir, proj.config.getProjectClassLoader());

                // 刷新列表
                for (TreeViewer tv : dataTreeViewers) {
                    tv.refresh();
                }

                // 关闭所有编辑器
                IEditorReference[] refs = getSite().getWorkbenchWindow().getActivePage().getEditorReferences();
                for (IEditorReference ref : refs) {
                    IEditorPart editor = ref.getEditor(false);
                    if (editor != null) {
                        getSite().getWorkbenchWindow().getActivePage().closeEditor(editor, false);
                    }
                }
                if (GLUtils.glEnabled) {
                    ParticleEffectManager.init(proj);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                MessageDialog.openError(getSite().getShell(), "错误", e.toString());
            }
        }
    }

    /*
     * 设置拖动支持。
     */
    protected void setupDragAndDrop(TreeViewer viewer) {
        Tree tree = viewer.getTree();
        final DragSource treeDragSource = new DragSource(tree, DND.DROP_MOVE);
        treeDragSource.addDragListener(new DragSourceAdapter() {
            /**
             * 判断是否允许拖动。任意对象都可以拖动。分类不允许拖动。
             */
            public void dragStart(DragSourceEvent event) {
                Object[] sels = getSelectedObjects();
                if (sels.length == 0) {
                    event.doit = false;
                }
                else {
                    event.doit = false;
                    for (int i = 0; i < sels.length; i++) {
                        if (sels[i] instanceof DataObject) {
                            event.doit = true;
                            break;
                        }
                    }
                }
            }

            /**
             * 设置拖动数据，一行一个对象，格式为：类名:id。
             */
            public void dragSetData(DragSourceEvent event) {
                Object[] sels = getSelectedObjects();
                StringBuffer buf = new StringBuffer();
                for (int i = 0; i < sels.length; i++) {
                    if (i > 0) {
                        buf.append("\n");
                    }
                    if (sels[i] instanceof DataObject) {
                        buf.append(sels[i].getClass().getName() + ":" + ((DataObject) sels[i]).id);
                    }
                }
                event.data = buf.toString();
            }

            public void dragFinished(DragSourceEvent event) {
            }
        });
        treeDragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });

        final DropTarget treeDropTarget = new DropTarget(tree, DND.DROP_MOVE);
        treeDropTarget.addDropListener(new DropTargetAdapter() {
            public void dragEnter(DropTargetEvent event) {
            }

            public void dragLeave(DropTargetEvent event) {
            }

            public void dragOperationChanged(DropTargetEvent event) {
            }

            /**
             * 检查当前目标是否允许拖放。允许拖动到一个新分类中（加到最后），或者拖动到一个指定对象（插入到前面）。
             */
            public void dragOver(DropTargetEvent event) {
                event.feedback = DND.FEEDBACK_NONE | DND.FEEDBACK_SCROLL;
                event.detail = DND.DROP_NONE;
                if (event.item != null) {
                    TextTransfer textTransfer = TextTransfer.getInstance();
                    String data = (String) textTransfer.nativeToJava(event.currentDataType);
                    if (data == null) {
                        return;
                    }
                    TreeItem titem = (TreeItem) event.item;
                    Object targetObj = titem.getData();
                    if (targetObj instanceof DataObjectCategory || targetObj instanceof DataObject) {
                        event.detail = DND.DROP_MOVE;
                    }
                }
            }

            /**
             * 拖动结束。
             */
            public void drop(DropTargetEvent event) {
                if (event.data == null || event.item == null) {
                    return;
                }
                TreeItem titem = (TreeItem) event.item;
                Object targetObj = titem.getData();
                String data = (String) event.data;
                String[] items = data.split("\n");
                try {
                    boolean changed = false;
                    Class changedClass = null;
                    ProjectData proj = ProjectData.getActiveProject();
                    if (targetObj instanceof DataObjectCategory) {
                        // 拖动到一个分类的最后
                        DataObjectCategory targetCate = (DataObjectCategory) targetObj;
                        for (String line : items) {
                            String[] sec = line.split(":");
                            Class cls = ProjectData.getActiveProject().config.getProjectClassLoader().loadClass(sec[0]);
                            if (cls == null) {
                                return;
                            }
                            DataObject dobj = proj.findObject(cls, Integer.parseInt(sec[1]));
                            if (dobj != null && !targetCate.name.equals(dobj.getCategoryName())) {
                                proj.changeObjectCategory(dobj, targetCate);
                                changed = true;
                                changedClass = cls;
                            }
                        }
                    }
                    else if (targetObj instanceof DataObject) {
                        // 拖动到一个对象的前面
                        DataObject tobj = (DataObject) targetObj;
                        changedClass = tobj.getClass();
                        DataObjectCategory targetCate = proj.findCategory(changedClass, tobj.getCategoryName());
                        int index = targetCate.objects.indexOf(tobj);
                        for (String line : items) {
                            String[] sec = line.split(":");
                            Class cls = ProjectData.getActiveProject().config.getProjectClassLoader().loadClass(sec[0]);
                            if (cls == null) {
                                return;
                            }
                            DataObject dobj = proj.findObject(cls, Integer.parseInt(sec[1]));
                            if (dobj == null || dobj == tobj) {
                                continue;
                            }
                            if (!targetCate.name.equals(dobj.getCategoryName())) {
                                proj.changeObjectCategory(dobj, targetCate);
                            }
                            int oldIndex = targetCate.objects.indexOf(dobj);
                            if (oldIndex < index) {
                                targetCate.objects.remove(dobj);
                                index--;
                                targetCate.objects.add(index, dobj);
                            }
                            else {
                                targetCate.objects.remove(dobj);
                                targetCate.objects.add(index, dobj);
                            }
                            index++;
                            changed = true;
                        }
                    }
                    if (changed) {
                        proj.saveDataList(changedClass);
                        refresh();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void dropAccept(DropTargetEvent event) {
            }
        });
        treeDropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
    }

    public static Object getSelectObject() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView) page.findView(DataListView.ID);
        Object[] obj = view.getSelectedObjects();

        if (obj != null && obj.length > 0) {
            return obj[0];
        }

        return null;
    }
    
    public static void tryEditObject(DataObject obj) {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView) page.findView(DataListView.ID);
        if (view != null) {
            view.editObject(obj);
        }
    }
}
