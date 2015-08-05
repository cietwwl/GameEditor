package com.pip.game.editor.property;
//选择禁用的物品
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import com.pip.game.data.DataObject;
import com.pip.game.data.DataObjectCategory;
import com.pip.game.data.ProjectData;
import com.pip.game.data.forbid.ForbidItem;
import com.pip.game.data.forbid.ForbidSkill;
import com.pip.game.data.item.DropGroup;
import com.pip.game.data.item.DropItem;
import com.pip.game.data.item.Item;
import com.pip.game.data.item.SubDropGroup;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.EditorPlugin;
import com.pip.game.editor.item.ItemTreeViewer;

public class CollectItemDialog extends Dialog {
    private Text text;
    private String searchCondition;

    class ListContentProvider implements IStructuredContentProvider{

        public Object[] getElements(Object inputElement) {
            List el = (List)inputElement;
            return el.toArray();
        }

        public void dispose() {}

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
    }
    
    
    class ListLabelProvider implements ILabelProvider{

        public Image getImage(Object element) {
            return null;
        }

        public String getText(Object element) {
            return element.toString();
        }

        public void addListener(ILabelProviderListener listener) {}

        public void dispose() {}

        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {}
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
            } else if (element instanceof ForbidSkill) {
                return EditorPlugin.getDefault().getImageRegistry().get("Item");
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
                    List<DataObjectCategory> cateList = ((ProjectData)parentElement).getCategoryListByType(ForbidItem.class);
                    for (DataObjectCategory cate : cateList) {
                        if (getChildren(cate).length > 0) {
                            retList.add(cate);
                            
                        }
                    }
                }
//                List<DataObjectCategory> cateList = ((ProjectData)parentElement).getCategoryListByType(Equipment.class);
//                for (DataObjectCategory cate : cateList) {
//                    if (getChildren(cate).length > 0) {
//                        retList.add(cate);
//                    }
//                }
                return retList.toArray();
            } 
            else if (parentElement instanceof DataObjectCategory) {
                List<ForbidItem>  retList= new ArrayList<ForbidItem>();
                for (DataObject dobj : ((DataObjectCategory)parentElement).objects) {
                    if (matchCondition((ForbidItem)dobj)) {
                        retList.add((ForbidItem)dobj);
                    }                    
                }
                                
                for(DataObjectCategory cate : ((DataObjectCategory)parentElement).cates) {
                    Object[] objs = getChildren(cate);
                    for(Object dobj2 : objs) {
                        if (matchCondition((ForbidItem)dobj2)) {
                            retList.add((ForbidItem)dobj2);
                        }
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
//            } else if (element instanceof Equipment) {
//                return ((Equipment)element).owner.findCategory(Equipment.class, ((Equipment)element).getCategoryName());
            } else if (element instanceof ForbidItem) {
                return ((ForbidItem)element).owner.findCategory(Item.class, ((ForbidItem)element).getCategoryName());
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
    private List<ForbidItem> selectedItems = new ArrayList<ForbidItem>();
    private List<String> itemName=new  ArrayList<String>();
    private ListViewer listview;
    
    public List<ForbidItem> getSelectedItems() {
        return selectedItems;
    }
    
    public void setSelItems(int[] itemIds) {
        if(itemIds != null) {
            for(int i=0; i<itemIds.length; i++) {
                ForbidItem item = ProjectData.getActiveProject().findForbidItem(itemIds[i]);
                if(item != null) {
                    selectedItems.add(item);
                }
//                else {
//                    item = ProjectData.getActiveProject().findEquipment(itemIds[i]);
//                    if(item != null) {
//                        selectedItems.add(item);
//                    }
//                }
            }
        }
   }
    
    private boolean matchCondition(ForbidItem item) {
        if (searchCondition == null || searchCondition.length() == 0) {
            return true;
        }
        if (item.title.indexOf(searchCondition) >= 0 || String.valueOf(item.id).indexOf(searchCondition) >= 0) {
           System.out.println(item.id);
            return true;
        }
        return false;
    }

    /**
     * Create the dialog
     * @param parentShell
     */
    public CollectItemDialog(Shell parentShell) {
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

        Composite listContainer = new Composite(parentContainer, SWT.BORDER);
        listContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        listview = new ListViewer(listContainer, SWT.FILL | SWT.BORDER | SWT.V_SCROLL);
        listview.setContentProvider(new ListContentProvider());
        listview.setLabelProvider(new ListLabelProvider());
        listview.setInput(this.selectedItems);
        final GridData gd_npcTemplateList = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_npcTemplateList.exclude = true;
        listview.getList().setBounds(0, 0, 300, 600);
        listview.getList().setLayoutData(gd_npcTemplateList);
         
        listview.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)event.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                Object selObj = sel.getFirstElement();
                if (selObj instanceof ForbidItem) {
                    selectedItems.remove(selObj);
                    listview.refresh();
                }
            }
                
        }
        );
        
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
                if (selObj instanceof ForbidItem) {
                    
                    if (sel.isEmpty() || !(sel.getFirstElement() instanceof ForbidItem)) {
                        MessageDialog.openInformation(getShell(), "提示：", "请选择一个有效物品！");
                        return;
                    } else {
                        Object[] sels = sel.toArray();
                        for (Object obj : sels) {
                            if (obj instanceof ForbidItem) {
                                if(selectedItems.contains(obj)) {
                                    MessageDialog.openInformation(getShell(), "提示：", "该物品已经添加！");
                                    return;
                                } else {                                    
                                    selectedItems.add((ForbidItem)obj);                                    
                                }
                            }
                        }                        
                        listview.refresh();
                    }
                } else{
                    if (treeViewer.getExpandedState(selObj)) {
                        treeViewer.collapseToLevel(selObj, 1);
                    } else {
                        treeViewer.expandToLevel(selObj, 1);
                    }
                }
            }
        });
//                 //   Object[] se=sel.toArray();
//                    String str=sel.toString();
//                   
////                    System.out.println(str);
//                    if(itemName.contains(str)){
//                        MessageDialog.openInformation(getShell(), "提示：", "该物品已经添加！");
//                      return;
//                    }else{
//                        itemName.add(str);
//                    }
//                    listview.refresh();
////                    ForbidItem item = null;
////                    System.out.println("kjk"+item.id);
////                   
////                    selectedItems.add((ForbidItem)ProjectData.getActiveProject().findForbidName(item.id));
//                }
//            }
//        });
        treeViewer.setLabelProvider(new TreeLabelProvider());
        treeViewer.setContentProvider(new TreeContentProvider());
        tree = treeViewer.getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        treeViewer.setInput(ProjectData.getActiveProject());
//        treeViewer.expandAll();
        
//        if (selectedItem != -1) {
//            try {
//                // 查找这个物品在tree中的位置
//                Item item = ProjectData.getActiveProject().findItemOrEquipment(selectedItem);
//                if (item != null) {
//                    searchCondition = item.title;
//                    text.setText(searchCondition);
//                    text.selectAll();
//                    StructuredSelection sel = new StructuredSelection(item);
//                    treeViewer.setSelection(sel);
//                }
//            } catch (Exception e) {
//            }
//        }

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
        newShell.setText("选择禁用的物品组");
    }    
}
