package com.pip.game.editor.forbid;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import com.pip.game.data.DataObject;
import com.pip.game.data.DataObjectCategory;
import com.pip.game.data.ProjectData;
import com.pip.game.data.equipment.Equipment;
import com.pip.game.data.forbid.ForbidSkill;
import com.pip.game.data.item.Item;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.editor.DefaultDataObjectEditor;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.EditorPlugin;
import com.pip.game.editor.item.DefaultItemEditor;
import com.pip.game.editor.item.ItemTreeViewer;
import com.pip.game.editor.property.CollectItemCellEditor;
import com.pip.game.editor.util.ItemChooser;

public class ForbidSkillEditor extends DefaultDataObjectEditor{
    /**
     * 物品禁用组编辑器
     * 
     * 
     */
       
        private Button buttonOK;
        private Button buttonCancel;
        private ItemChooser itemChooser;
        private GameMapInfo mapInfo;
        private TreeViewer treeViewer;
        private Tree tree;
        private boolean includeItem = true;
        private boolean multiSel = false;
        private List<SkillConfig> selectedItems = new ArrayList<SkillConfig>();
        private ListViewer listview;
        private Text text;
        private String searchCondition;
        private CollectItemCellEditor collectItemCellEditor;
        private boolean updating = false;;
        public static final String ID = "com.pip.game.editor.forbid.ForbidSkillEditor";

        class ListContentProvider implements IStructuredContentProvider {

            public Object[] getElements(Object inputElement) {
                List el = (List) inputElement;
                return el.toArray();
            }

            public void dispose() {
            }

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }
        }

        class ListLabelProvider implements ILabelProvider {

            public Image getImage(Object element) {
                return null;
            }

            public String getText(Object element) {
                return element.toString();
            }

            public void addListener(ILabelProviderListener listener) {
            }

            public void dispose() {
            }

            public boolean isLabelProperty(Object element, String property) {
                return false;
            }

            public void removeListener(ILabelProviderListener listener) {
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
                }
                else if (element instanceof SkillConfig) {
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
                        List<DataObjectCategory> cateList = ((ProjectData) parentElement).getCategoryListByType(SkillConfig.class);
                        for (DataObjectCategory cate : cateList) {
                            if (getChildren(cate).length > 0) {
                                retList.add(cate);
                            }
                        }
                    }
//                    List<DataObjectCategory> cateList = ((ProjectData) parentElement)
//                            .getCategoryListByType(Equipment.class);
//                    for (DataObjectCategory cate : cateList) {
//                        if (getChildren(cate).length > 0) {
//                            retList.add(cate);
//                        }
//                    }
                    return retList.toArray();
                }
                else if (parentElement instanceof DataObjectCategory) {
                    List<SkillConfig> retList = new ArrayList<SkillConfig>();
                    for (DataObject dobj : ((DataObjectCategory) parentElement).objects) {
                        if (matchCondition((SkillConfig) dobj)) {
                            retList.add((SkillConfig) dobj);
                        }
                    }

                    for (DataObjectCategory cate : ((DataObjectCategory) parentElement).cates) {
                        Object[] objs = getChildren(cate);
                        for (Object dobj2 : objs) {
                            if (matchCondition((SkillConfig) dobj2)) {
                                retList.add((SkillConfig) dobj2);
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
                }
                else if (element instanceof DataObjectCategory) {
                    return ProjectData.getActiveProject();
                }
//                else if (element instanceof Equipment) {
//                    return ((Equipment) element).owner.findCategory(Equipment.class, ((Equipment) element)
//                            .getCategoryName());
//                }
                else if (element instanceof SkillConfig) {
                    return ((SkillConfig) element).owner.findCategory(SkillConfig.class, ((SkillConfig) element).getCategoryName());
                }
                return null;
            }

            public boolean hasChildren(Object element) {
                return (element instanceof ProjectData || element instanceof DataObjectCategory);
            }
        }

        private boolean matchCondition(SkillConfig item) {
            if (searchCondition == null || searchCondition.length() == 0) {
                return true;
            }
            if (item.title.indexOf(searchCondition) >= 0 || String.valueOf(item.id).indexOf(searchCondition) >= 0) {
                return true;
            }
            return false;
        }

        public void createPartControl(Composite parent) {
            Composite parentContainer = (Composite) (parent);
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
            
            Vector<Integer> forbitSkills = ((ForbidSkill)editObject).forbitSkills;
            
            for(int i=0; i<forbitSkills.size(); i++) {
                SkillConfig item = (SkillConfig)ProjectData.getActiveProject().findObject(SkillConfig.class, forbitSkills.get(i));
                selectedItems.add(item);
            }        
            listview.setInput(selectedItems);        
            
            final GridData gd_npcTemplateList = new GridData(SWT.FILL, SWT.FILL, true, true);
            gd_npcTemplateList.exclude = true;
            listview.getList().setBounds(0, 0, 300, 600);
            listview.getList().setLayoutData(gd_npcTemplateList);

            listview.addDoubleClickListener(new IDoubleClickListener() {
                public void doubleClick(final DoubleClickEvent event) {
                    StructuredSelection sel = (StructuredSelection) event.getSelection();
                    if (sel.isEmpty()) {
                        return;
                    }
                    Object selObj = sel.getFirstElement();
                    if (selObj instanceof SkillConfig) {
                        selectedItems.remove(selObj);
                        listview.refresh();
                        
                      ForbidSkill itemDataDef = (ForbidSkill) editObject;
                      itemDataDef.removeForbidSkillId(((SkillConfig) selObj).id);
                      
                      System.out.println(((SkillConfig) selObj).id);
                      setDirty(true);
                    }
                }
            });
            final Label label = new Label(container, SWT.NONE);
            label.setText("查找：");

            text = new Text(container, SWT.BORDER);
            text.addModifyListener(new ModifyListener() {
                public void modifyText(final ModifyEvent e) {
                    searchCondition = text.getText();
                    StructuredSelection sel = (StructuredSelection) treeViewer.getSelection();
                    Object selObj = sel.isEmpty() ? null : sel.getFirstElement();
                    treeViewer.refresh();
                    treeViewer.expandAll();
                    if (selObj != null) {
                        sel = new StructuredSelection(selObj);
                        treeViewer.setSelection(sel);
                        System.out.println("selObj");

                        
                    }
                }
            });
            text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            treeViewer = new ItemTreeViewer(container, SWT.BORDER | (multiSel ? SWT.MULTI : 0));
            treeViewer.addDoubleClickListener(new IDoubleClickListener() {
                public void doubleClick(final DoubleClickEvent event) {
                    StructuredSelection sel = (StructuredSelection) event.getSelection();
                    if (sel.isEmpty()) {
                        return;
                    }
                    Object selObj = sel.getFirstElement();
                    if (selObj instanceof SkillConfig) {

                        if (sel.isEmpty() || !(sel.getFirstElement() instanceof SkillConfig)) {
                            // MessageDialog.openInformation(getShell(), "提示：",
                            // "请选择一个有效物品！");
                            return;
                        }
                        else {
                            Object[] sels = sel.toArray();
                            for (Object obj : sels) {
                                if (obj instanceof SkillConfig) {
                                    if (selectedItems.contains(obj)) {
                                        // MessageDialog.openInformation(getShell(),
                                        // "提示：", "该物品已经添加！");
                                        return;
                                    }
                                    else {
                                        selectedItems.add((SkillConfig) obj);
                                        ForbidSkill itemDataDef = (ForbidSkill) editObject;
                                        itemDataDef.addForbidSkillId(((SkillConfig)obj).id);
                                        setDirty(true);
                                    }
                                }
                            }
                            listview.refresh();
                        }

                    }
                    else {
                        if (treeViewer.getExpandedState(selObj)) {
                            treeViewer.collapseToLevel(selObj, 1);
                        }
                        else {
                            treeViewer.expandToLevel(selObj, 1);
                        }
                    }
                }
            });
//             buttonOK = new Button(container, SWT.NONE);
//             buttonOK.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
//             false));
//             buttonOK.setText("确定");
//             buttonOK.addSelectionListener(new SelectionAdapter() {
//                 public void widgetSelected(final SelectionEvent e) {
//                     onButtonOK();
//                 }
//             });

            treeViewer.setLabelProvider(new TreeLabelProvider());
            treeViewer.setContentProvider(new TreeContentProvider());
            tree = treeViewer.getTree();
            tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
            treeViewer.setInput(ProjectData.getActiveProject());
         
            // treeViewer.expandAll();

            // if (selectedItem != -1) {
            // try {
            // // 查找这个物品在tree中的位置
            // Item item =
            // ProjectData.getActiveProject().findItemOrEquipment(selectedItem);
            // if (item != null) {
            // searchCondition = item.title;
            // text.setText(searchCondition);
            // text.selectAll();
            // StructuredSelection sel = new StructuredSelection(item);
            // treeViewer.setSelection(sel);
            // }
            // } catch (Exception e) {
            // }
            // }

            // return container;
        }
         public void widgetSelected(SelectionEvent e) {
         try {
         if (e.getSource() == buttonOK) {
                    
         onButtonOK();
         }
         } catch (Exception e1) {
         e1.printStackTrace();
         }
                
         }
        private void onButtonOK() {
            try {
           //     saveData();
                this.setDirty(true);
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
   
}




