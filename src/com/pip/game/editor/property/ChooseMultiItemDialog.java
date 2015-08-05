package com.pip.game.editor.property;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import com.pip.game.data.DataObject;
import com.pip.game.data.DataObjectCategory;
import com.pip.game.data.GiftGroup;
import com.pip.game.data.ProjectData;
import com.pip.game.data.equipment.Equipment;
import com.pip.game.data.item.Item;
import com.pip.game.data.item.ItemDefData;
import com.pip.game.data.item.Monster;
import com.pip.game.editor.EditorPlugin;
import com.pip.game.editor.item.ItemTreeViewer;

public class ChooseMultiItemDialog extends Dialog {
    private Table tblSelItems;
    private TableViewer tblViewerSelItems;
    private Text text;
    private String searchCondition;
    private boolean needEditNum; //是否支持编辑数量
    
    public boolean isNeedEditNum() {
        return needEditNum;
    }

    public void setNeedEditNum(boolean needEditNum) {
        this.needEditNum = needEditNum;
    }

    class TreeLabelProvider extends LabelProvider {
        public String getText(Object element) {
            if (element instanceof ProjectData) {
                return "项目";
            }
            return super.getText(element);
        }
        public Image getImage(Object element) {
            if (element instanceof DataObjectCategory) {
                return EditorPlugin.getDefault().getImageRegistry().get("itemtype");
            } else if (element instanceof Item) {
                return EditorPlugin.getDefault().getImageRegistry().get("item");
            }
            return null;
        }
    }
    class TreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
        public void dispose() {
        }
        public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
        }
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof ProjectData) {
                // 根节点是ProjectData，第一层子节点是所有的物品类型和装备类型
                List<DataObjectCategory> retList = new ArrayList<DataObjectCategory>();
                if (includeItem) {
                    List<DataObjectCategory> cateList = ((ProjectData)parentElement).getCategoryListByType(Item.class);
                    for (DataObjectCategory cate : cateList) {
                        if (getChildren(cate).length > 0) {
                            retList.add(cate);
                        }
                    }
                }
                List<DataObjectCategory> cateList = ((ProjectData)parentElement).getCategoryListByType(Equipment.class);
                for (DataObjectCategory cate : cateList) {
                    if (getChildren(cate).length > 0) {
                        retList.add(cate);
                    }
                }
                return retList.toArray();
            } else if (parentElement instanceof DataObjectCategory) {
                List retList = new ArrayList();
                for (DataObjectCategory cate : ((DataObjectCategory) parentElement).cates) {
                    Object[] objs = getChildren(cate);
                    for (Object dobj2 : objs) {
                        if (matchCondition((Item) dobj2)) {
                            retList.add((Item) dobj2);
                        }
                    }
                }
                for (DataObject dobj : ((DataObjectCategory)parentElement).objects) {
                    if (matchCondition((Item)dobj)) {
                        retList.add((Item)dobj);
                    }
                }
                return retList.toArray();
            } 
            return new Object[0];
        }
        public Object getParent(Object element) {
            if (element instanceof ProjectData) {
                return null;
            } else if (element instanceof DataObjectCategory) {
                return ProjectData.getActiveProject();
            } else if (element instanceof Equipment) {
                return ((Equipment)element).owner.findCategory(Equipment.class, ((Equipment)element).getCategoryName());
            } else if (element instanceof Item) {
                return ((Item)element).owner.findCategory(Item.class, ((Item)element).getCategoryName());
            }
            return null;
        }
        public boolean hasChildren(Object element) {
            return (element instanceof ProjectData || element instanceof DataObjectCategory);
        }
    }
    
    /*
     * 已经选中的物品表格，标签提供者
     */
    class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            if(element instanceof ItemDefData){
                switch(columnIndex){
                    case 0:
                        return String.valueOf(((ItemDefData) element).itemId);
                    case 1:
                        Item item = ProjectData.getActiveProject().findItem(((ItemDefData) element).itemId);
                        if(item==null){
                            item = ProjectData.getActiveProject().findEquipment(((ItemDefData) element).itemId);
                        }
                        if(item!=null){
                            return String.valueOf(item.getTitle());
                        }
                    case 2:
                        return  String.valueOf(((ItemDefData) element).itemCount);
                }
            }
            return null;
        }
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }
    
    /*
     * 已经选中的物品表格，内容提供者
     */
    class TableContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            if(inputElement instanceof List){
                return ((List) inputElement).toArray();
            }
            return new ItemDefData[0];
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    class TableCellModifier implements ICellModifier {

        public boolean canModify(Object element, String property) {
            int columnIndex = Integer.parseInt(property.substring(1));
            switch(columnIndex){
                case 2:
                    return true;
            }
            return false;
        }

        public Object getValue(Object element, String property) {
            int columnIndex = Integer.parseInt(property.substring(1));
            if(element instanceof ItemDefData){
                switch(columnIndex){
                    case 2:
                        ItemDefData item = (ItemDefData)element;
                        return  String.valueOf(item.itemCount);
                }
            }
            return null;
        }

        public void modify(Object element, String property, Object value) {
            TableItem ti = (TableItem) element;
            int columnIndex = Integer.parseInt(property.substring(1));
            Object data = ti.getData();
            if (data instanceof ItemDefData) {
                switch (columnIndex) {
                    case 2:
                        try {
                            int count = Integer.parseInt(value.toString());
                            if (count != ((ItemDefData) data).itemCount) {
                                ((ItemDefData) data).itemCount = count;
                                tblViewerSelItems.refresh();
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }
    }
    
    private TreeViewer treeViewer;
    private Tree tree;
    private int selectedItem = -1;
    private boolean includeItem = true;
    private boolean multiSel = false;
    private List<ItemDefData> selectedItems = new ArrayList<ItemDefData>();
    
    public void setSelectedItems(List<ItemDefData> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }
    
    public List<ItemDefData> getSelectedItems() {
        return selectedItems;
    }
    
    private boolean matchCondition(Item item) {
        if (searchCondition == null || searchCondition.length() == 0) {
            return true;
        }
        if (item.title.indexOf(searchCondition) >= 0 || String.valueOf(item.id).indexOf(searchCondition) >= 0) {
            return true;
        }
        return false;
    }

    /**
     * Create the dialog
     * @param parentShell
     */
    public ChooseMultiItemDialog(Shell parentShell) {
        super(parentShell);
    }
    
    public void setIncludeItem(boolean value) {
        includeItem = value;
    }
    
    public void setMultiSel(boolean value) {
        multiSel = value;
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        container.setLayout(gridLayout);

        final Label label = new Label(container, SWT.NONE);
        label.setText("查找：");

        text = new Text(container, SWT.BORDER);
        text.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                searchCondition = text.getText();
                StructuredSelection sel = (StructuredSelection)treeViewer.getSelection();
                Object selObj = sel.isEmpty() ? null : sel.getFirstElement();
                treeViewer.refresh();
//                treeViewer.expandAll();
                if (selObj != null) {
                    sel = new StructuredSelection(selObj);
                    treeViewer.setSelection(sel);
                }
            }
        });
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        treeViewer = new ItemTreeViewer(container, SWT.BORDER | (multiSel ? SWT.MULTI : 0));
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)event.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                Object selObj = sel.getFirstElement();
                if (selObj instanceof Item) {
                    onAppendList();
                } else {
                    if (treeViewer.getExpandedState(selObj)) {
                        treeViewer.collapseToLevel(selObj, 1);
                    } else {
                        treeViewer.expandToLevel(selObj, 1);
                    }
                }
            }
        });
        treeViewer.setLabelProvider(new TreeLabelProvider());
        treeViewer.setContentProvider(new TreeContentProvider());
        tree = treeViewer.getTree();
        final GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
        gd_tree.heightHint = 274;
        tree.setLayoutData(gd_tree);
        treeViewer.setInput(ProjectData.getActiveProject());
//        treeViewer.expandAll();
        
        if (selectedItem != -1) {
            try {
                // 查找这个物品在tree中的位置
                Item item = ProjectData.getActiveProject().findItemOrEquipment(selectedItem);
                if (item != null) {
                    searchCondition = item.title;
                    text.setText(searchCondition);
                    text.selectAll();
                    StructuredSelection sel = new StructuredSelection(item);
                    treeViewer.setSelection(sel);
                }
            } catch (Exception e) {
            }
        }

        if(needEditNum){
            final Button btnAppendTable = new Button(container, SWT.NONE);
            btnAppendTable.setLayoutData(new GridData());
            btnAppendTable.setText("添加到列表");
            btnAppendTable.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent paramSelectionEvent) {
                    onAppendList();
                }
                public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {
                }
            });

            final Button btnClearSelected = new Button(container, SWT.NONE);
            final GridData gd_btnClearSelected = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
            btnClearSelected.setLayoutData(gd_btnClearSelected);
            btnClearSelected.setText("清空已选列表");
            btnClearSelected.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent paramSelectionEvent) {
                    selectedItems.clear();
                    tblViewerSelItems.refresh();
                }
                public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {
                }
            });
            
            final Label label_1 = new Label(container, SWT.NONE);
            label_1.setText("已选物品列表:");
            new Label(container, SWT.NONE);

            tblViewerSelItems = new TableViewer(container,  SWT.FULL_SELECTION | SWT.BORDER);
            tblSelItems = tblViewerSelItems.getTable();
            tblSelItems.setLinesVisible(true);
            tblSelItems.setHeaderVisible(true);
            final GridData gd_tblSelctedItems = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
            tblSelItems.setLayoutData(gd_tblSelctedItems);

            String[] propertyNames = new String[]{"物品ID","物品名称","数量"};
            int[] columnWidths = new int[]{160,160,200};
            CellEditor[] cellEditors  = new CellEditor[]{new TextCellEditor(tblSelItems),new TextCellEditor(tblSelItems), new TextCellEditor(tblSelItems)};
            
            for(int i = 0 ; i< propertyNames.length;i++){
                TableColumn col = new TableColumn(tblSelItems, SWT.NONE);
                col.setText(propertyNames[i]);
                col.setWidth(columnWidths[i]);
            }
            tblViewerSelItems.setColumnProperties(new String[] {
                    "c0", "c1", "c2"
            });
            tblViewerSelItems.setContentProvider(new TableContentProvider());
            tblViewerSelItems.setLabelProvider(new TableLabelProvider());
            tblViewerSelItems.setCellModifier(new TableCellModifier());
            tblViewerSelItems.setCellEditors(cellEditors);
            tblViewerSelItems.setInput(selectedItems);
        }
        return container;
    }

    private void onAppendList() {
        StructuredSelection sel = (StructuredSelection) treeViewer.getSelection();
        if (sel.isEmpty() || !(sel.getFirstElement() instanceof Item)) {
            selectedItem = -1;
        }
        else {
            selectedItem = ((Item) sel.getFirstElement()).id;
            Object[] sels = sel.toArray();
            for (Object obj : sels) {
                if (obj instanceof Item) {
                    ItemDefData itemnum = new ItemDefData(((Item) obj).id, 0);
                    selectedItems.add(itemnum);
                    tblViewerSelItems.refresh();
                }
            }
        }
    }
    /**
     * Create contents of the button bar
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "确定", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
    }

    /**
     * Return the initial size of the dialog
     */
    @Override
    protected Point getInitialSize() {
        return new Point(520, 644);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("选择物品");
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if(needEditNum){
                
            }else{
                
            }
        }
        super.buttonPressed(buttonId);
    }
}

