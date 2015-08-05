package com.pip.game.editor.property;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
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
import com.pip.game.data.ProjectData;
import com.pip.game.data.equipment.Equipment;
import com.pip.game.data.item.Item;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.EditorPlugin;
import com.pip.game.editor.item.ItemTreeViewer;
import com.pip.image.workshop.WorkshopPlugin;
import com.swtdesigner.ResourceManager;

/**
 * 
 * 共出口限制使用，可添加物品，设置数量，设置是否扣除
 * @author ybai
 *
 */
public class CollectItemDialogEx extends Dialog {
    private Text text;
    private String searchCondition;

    private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
        public Image getColumnImage(Object element, int columnIndex) {
            return null; 
        }
                
        public String getColumnText(Object element, int columnIndex) {
            if(element instanceof ItemRow) {
                ItemRow m = (ItemRow) element;
                switch (columnIndex) {
                    case 0:
                        return m.item.toString();
                    case 1:
                        return String.valueOf(m.count);
                    case 2:
                        return m.isRemove ? "[是]" : "[否]";
                }
            }

            return "";
        }
    }
    
    private class ContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            if(inputElement instanceof List) {
                
                return ((List)inputElement).toArray();
            }
            return new Object[]{};
            
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    class MyCellModifier implements ICellModifier{  
        private TableViewer tv;
        private Table table;
         
        public MyCellModifier(TableViewer tv) {
            this.tv = tv;
        }  
        public boolean canModify(Object element, String property) {
            if(property.equals("c3") || property.equals("c2"))
                return true;
            return false;
        }  
  
        public Object getValue(Object element, String property) {  
            if(element instanceof ItemRow) {
                ItemRow itemRow = (ItemRow)element;
                
                if("c1".equals(property)) {
                    return itemRow.item;
                } else if("c2".equals(property)) {
                    return String.valueOf(itemRow.count);
                } else if("c3".equals(property)) {
                    return Boolean.valueOf(itemRow.isRemove);
                } 
            }

            return null;
        }
  
        public void modify(Object element, String property, Object value) {
            TableItem item = (TableItem) element;
            Object data = item.getData();
            
            if(data instanceof ItemRow) {
                ItemRow itemRow = ((ItemRow)data);
                
                if("c2".equals(property)) {
                    itemRow.count = Integer.parseInt((String)value);
                } else if("c3".equals(property)) {
                    itemRow.isRemove = ((Boolean)value).booleanValue();
                }                
            }

            //tv.update(item.getData(), null);            
            tv.refresh(data);
            
        }  
         
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
                List<Item> retList = new ArrayList<Item>();
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
    private TreeViewer treeViewer;
    private Tree tree;
    private boolean includeItem = true;
    private boolean multiSel = false;
    private List<ItemRow> selectedItems = new ArrayList<ItemRow>();
    private TableViewer tableViewer;
    private Table table;
    
    private class ItemRow {
        Item item;
        int count;
        boolean isRemove;
        
        public ItemRow (Item item, int count, boolean isRemove) {
            this.item = item;
            this.count = count;
            this.isRemove = isRemove;
        }
    }
    
    public int[] getItemIds() {
        int count = selectedItems.size();
        int[] ret = new int[count];
        for(int i=0; i<count; i++) {
            ret[i] = selectedItems.get(i).item.id;
        }
        
        return ret;
    }
    
    public int[] getCounts() {
        int count = selectedItems.size();
        int[] ret = new int[count];
        for(int i=0; i<count; i++) {
            ret[i] = selectedItems.get(i).count;
        }
        
        return ret;
    }
    
    public boolean[] getIsRemoves() {
        int count = selectedItems.size();
        boolean[] ret = new boolean[count];
        for(int i=0; i<count; i++) {
            ret[i] = selectedItems.get(i).isRemove;
        }
        
        return ret;
    }
    
    public void setSelItems(int[] itemIds, int[] count, boolean[] isRemove) {
        if(itemIds != null) {
            for(int i=0; i<itemIds.length; i++) {
                Item item = ProjectData.getActiveProject().findItem(itemIds[i]);
                if(item != null) {
                    selectedItems.add(new ItemRow(item, count[i], isRemove[i]));
                } else {
                    item = ProjectData.getActiveProject().findEquipment(itemIds[i]);
                    if(item != null) {
                        selectedItems.add(new ItemRow(item, count[i], isRemove[i]));
                    }
                }
            }
        }
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
    public CollectItemDialogEx(Shell parentShell) {
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
        Composite parentContainer = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        parentContainer.setLayout(gridLayout);
        
        Composite container = new Composite(parentContainer, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        gridLayout = new GridLayout();
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
                treeViewer.expandAll();
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
                    
                    if (sel.isEmpty() || !(sel.getFirstElement() instanceof Item)) {
                        MessageDialog.openInformation(getShell(), "提示：", "请选择一个有效物品！");
                        return;
                    } else {
                        Object[] sels = sel.toArray();
                        for (Object obj : sels) {
                            if (obj instanceof Item) {
                                if(selectedItems.contains(obj)) {
                                    MessageDialog.openInformation(getShell(), "提示：", "该物品已经添加！");
                                    return;
                                } else {                                    
                                    selectedItems.add(new ItemRow((Item)obj, 1, false));                                    
                                }
                            }
                        }                        
                        tableViewer.refresh();
                    }
                    
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
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        treeViewer.setInput(ProjectData.getActiveProject());
//        treeViewer.expandAll();
                
        tableViewer = new TableViewer(parentContainer, SWT.FULL_SELECTION);
        tableViewer.setLabelProvider(new TableLabelProvider());
        tableViewer.setContentProvider(new ContentProvider());            
        table = tableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        table.addKeyListener(new KeyAdapter(){
            public void keyReleased(KeyEvent e) {
                if(e.keyCode == SWT.DEL){
                    //删除
                    Object selData = tableViewer.getElementAt(table.getSelectionIndex());
                    if(selData instanceof ItemRow) {
                        selectedItems.remove((ItemRow)selData);
                    }
                    
                    tableViewer.refresh();
                }
            }
        });
        
        {
            TableColumn columnMonsterId = new TableColumn(table, SWT.NONE);
            columnMonsterId.setResizable(false);
            columnMonsterId.setWidth(200);
            columnMonsterId.setText("物品");
            columnMonsterId.setResizable(true);
        }
        {
            TableColumn columnTitle = new TableColumn(table, SWT.NONE);
            columnTitle.setResizable(false);
            columnTitle.setWidth(50);
            columnTitle.setText("数量");
            columnTitle.setResizable(true);
        }
        {
            TableColumn columnRate = new TableColumn(table, SWT.NONE);
            columnRate.setWidth(100);
            columnRate.setText("是否扣除");
            columnRate.setResizable(true);
        }
        CellEditor[] eds = new CellEditor[3];
        eds[0] = new TextCellEditor(table);
        eds[1] = new TextCellEditor(table);
        eds[2] = new CheckboxCellEditor(table);
        String[] pros = {"c1","c2","c3"};
        tableViewer.setColumnProperties(pros);
        tableViewer.setCellEditors(eds);
        tableViewer.setCellModifier(new MyCellModifier(tableViewer));
        
        final Text text2 = (Text) eds[1].getControl();// 设置第2列只能输入数值
        text2.addVerifyListener(new VerifyListener() {
            public void verifyText(VerifyEvent e) {
                String inStr = e.text;
                if (inStr.length() > 0) {
                    try {
                        Integer.parseInt(inStr);
                        
                        e.doit = true; 
                    }catch(Exception ex) {
                        e.doit = false;
                    }
                } else {
                    e.doit = false;
                }
            }
        });
        
        tableViewer.setInput(this.selectedItems);
        tableViewer.refresh();
        
        return container;
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
        newShell.setText("选择禁用的物品");
    }    
}
