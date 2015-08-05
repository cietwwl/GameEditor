package com.pip.game.editor.item;

import java.lang.reflect.Constructor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.pip.game.data.ProjectData;
import com.pip.game.data.item.Item;
import com.pip.game.data.item.ItemTypeConfig;
import com.pip.game.editor.AbstractDataObjectEditor;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.DefaultDataObjectEditor;
import com.pip.util.AutoSelectAll;

/**
 * 物品编辑器
 */
public class ItemEditor extends DefaultDataObjectEditor {

	public Text textDescription;
	public Text textRemark;
	class TypeViewerContentProvider implements IStructuredContentProvider {
	    public Object[] getElements(Object inputElement) {
	        return ProjectData.getActiveProject().config.itemTypes;
	    }
	    public void dispose() {
	    }
	    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	    }
	}
	
	private Combo typeCombo;
	private Text textTitle;
	private Text textID;
	public static final String ID = "com.pip.game.editor.item.ItemEditor"; //$NON-NLS-1$
	
	private ScrolledComposite scrollContainer;
    private ComboViewer typeViewer;
    private Composite editorContainer;
    private ItemTypeConfig currentType;
    private AbstractDataObjectEditor currentTypeEditor;

	public void createPartControl(Composite parent) {
        scrollContainer = new ScrolledComposite(parent, SWT.BORDER | 
        SWT.H_SCROLL | SWT.V_SCROLL); 
        scrollContainer.setLayout(new FillLayout());

        scrollContainer.setExpandHorizontal(true);
        scrollContainer.setExpandVertical(true);

        final Composite container = new Composite(scrollContainer, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 6;
        container.setLayout(gridLayout);

        final Label label = new Label(container, SWT.NONE);
        label.setText("ID：");

        textID = new Text(container, SWT.READ_ONLY | SWT.BORDER);
        final GridData gd_textID = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textID.setLayoutData(gd_textID);
        textID.addFocusListener(AutoSelectAll.instance);

        final Label label_1 = new Label(container, SWT.NONE);
        label_1.setText("物品名称：");

        textTitle = new Text(container, SWT.BORDER);
        final GridData gd_textTitle = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textTitle.setLayoutData(gd_textTitle);
        textTitle.addFocusListener(AutoSelectAll.instance);
        textTitle.addModifyListener(this);

        final Label label_2 = new Label(container, SWT.NONE);
        label_2.setText("物品类型：");

        typeViewer = new ComboViewer(container, SWT.READ_ONLY);
        typeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                StructuredSelection sel = (StructuredSelection)typeViewer.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                changeType((ItemTypeConfig)sel.getFirstElement());
                setDirty(true);
            }
        });
        typeViewer.setContentProvider(new TypeViewerContentProvider());
        typeCombo = typeViewer.getCombo();
        typeCombo.setVisibleItemCount(30);
        final GridData gd_typeCombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
        typeCombo.setLayoutData(gd_typeCombo);
        typeViewer.setInput(this);

        final Label label_3 = new Label(container, SWT.NONE);
        label_3.setText("描述：");

        textDescription = new Text(container, SWT.WRAP | SWT.MULTI | SWT.BORDER);
        final GridData gd_textDescription = new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1);
        gd_textDescription.heightHint = 50;
        textDescription.setLayoutData(gd_textDescription);
        textDescription.addModifyListener(this);
        textDescription.addFocusListener(AutoSelectAll.instance);
        
        final Label label_31 = new Label(container, SWT.NONE);
        label_31.setText("备注：");

        textRemark = new Text(container, SWT.WRAP | SWT.MULTI | SWT.BORDER);
        final GridData gd_textRemark = new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1);
        gd_textRemark.heightHint = 50;
        textRemark.setLayoutData(gd_textDescription);
        textRemark.addModifyListener(this);
        textRemark.addFocusListener(AutoSelectAll.instance);

        editorContainer = new Composite(container, SWT.NONE);
        editorContainer.setLayout(new FillLayout());
        final GridData gd_editorContainer = new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1);
        editorContainer.setLayoutData(gd_editorContainer);
        container.setSize(600, 1200);
        scrollContainer.setMinSize(600, 1200);
        scrollContainer.setContent(container);

        // 设置当前值
        Item item = (Item)editObject;
        textID.setText(String.valueOf(item.id));
        textTitle.setText(item.title);
        textDescription.setText(item.description);
        textRemark.setText(item.remark);
        ItemTypeConfig type = item.owner.config.findItemType(item.type);
        typeViewer.setSelection(new StructuredSelection(type));
        changeType(type);
        
        setDirty(false);
        setPartName(this.getEditorInput().getName());
        saveStateToUndoBuffer();
	}
	
	/**
	 * 修改类型，可能需要改变编辑器。
	 * @param newType
	 */
	public void changeType(ItemTypeConfig newType) {
	    Item item = (Item)editObject;
	    item.type = newType.id;
	    item.mainType = newType.category;
	    if (currentType == newType) {
	        return;
	    }
	    if (currentTypeEditor != null && currentTypeEditor.getClass().getName().equals(newType.editorClass)) {
	        return;
	    }
	    try {
	        Class cls = item.owner.config.getProjectClassLoader().loadClass(newType.editorClass);
	        Constructor c = cls.getConstructor(Composite.class, int.class, DefaultDataObjectEditor.class);
	        currentType = newType;
	        if (currentTypeEditor != null) {
	            currentTypeEditor.dispose();
	        }
	        currentTypeEditor = (AbstractDataObjectEditor)c.newInstance(editorContainer, SWT.NONE, this);
	        currentTypeEditor.load();
	        editorContainer.layout();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    setDirty(true);
	}
	
	/**
	 * editor初始化
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
	    super.init(site, input);
	}
	
	/**
	 * 保存当前修改的数据
	 */
	protected void saveData() throws Exception {
	    Item item = (Item)editObject;
	    item.title = textTitle.getText();
	    item.description = textDescription.getText();
	    item.remark = textRemark.getText();
	    currentTypeEditor.save();
	}
	
	/**
	 * 保存事件处理
	 */
	public void doSave(IProgressMonitor monitor) {
        // Do the Save operation
        try {
            saveData();
            // 保存对象属性并更新XML文件
            saveTarget.update(editObject);
            ProjectData.getActiveProject().saveDataList(Item.class);
            setDirty(false);
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            DataListView view = (DataListView)page.findView(DataListView.ID);
            view.refresh(saveTarget);
        } catch (Exception e) {
            MessageDialog.openError(getSite().getShell(), "错误", e.toString());
            monitor.setCanceled(true);
        }
    }
}
