package com.pip.game.editor.property;

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
import com.pip.game.data.forbid.ForbidSkill;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.EditorPlugin;
import com.pip.game.editor.item.ItemTreeViewer;

public class CollectSkillDialog extends Dialog {


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
                      List<DataObjectCategory> cateList = ((ProjectData)parentElement).getCategoryListByType(ForbidSkill.class);
                      for (DataObjectCategory cate : cateList) {
                          if (getChildren(cate).length > 0) {
                              retList.add(cate);
                              
                          }
                      }
                  }
//                  List<DataObjectCategory> cateList = ((ProjectData)parentElement).getCategoryListByType(Equipment.class);
//                  for (DataObjectCategory cate : cateList) {
//                      if (getChildren(cate).length > 0) {
//                          retList.add(cate);
//                      }
//                  }
                  return retList.toArray();
              } 
              else if (parentElement instanceof DataObjectCategory) {
                  List<ForbidSkill>  retList= new ArrayList<ForbidSkill>();
                  for (DataObject dobj : ((DataObjectCategory)parentElement).objects) {
                      if (matchCondition((ForbidSkill)dobj)) {
                          retList.add((ForbidSkill)dobj);
                      }                    
                  }
                                  
                  for(DataObjectCategory cate : ((DataObjectCategory)parentElement).cates) {
                      Object[] objs = getChildren(cate);
                      for(Object dobj2 : objs) {
                          if (matchCondition((ForbidSkill)dobj2)) {
                              retList.add((ForbidSkill)dobj2);
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
//              } else if (element instanceof Equipment) {
//                  return ((Equipment)element).owner.findCategory(Equipment.class, ((Equipment)element).getCategoryName());
              } else if (element instanceof ForbidSkill) {
                  return ((ForbidSkill)element).owner.findCategory(SkillConfig.class, ((ForbidSkill)element).getCategoryName());
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
      private List<ForbidSkill> selectedItems = new ArrayList<ForbidSkill>();
      private ListViewer listview;
      
      public List<ForbidSkill> getSelectedItems() {
          return selectedItems;
      }
      
      public void setSelItems(int[] itemIds) {
          if(itemIds != null) {
              for(int i=0; i<itemIds.length; i++) {
                  ForbidSkill item = ProjectData.getActiveProject().findForbidSkill(itemIds[i]);
                  if(item != null) {
                      selectedItems.add(item);
                  }
//                  else {
//                      item = ProjectData.getActiveProject().findEquipment(itemIds[i]);
//                      if(item != null) {
//                          selectedItems.add(item);
//                      }
//                  }
              }
          }
     }
      
      private boolean matchCondition(ForbidSkill item) {
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
      public CollectSkillDialog(Shell parentShell) {
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
//          listview.setInput(this.itemName);
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
                  if (selObj instanceof ForbidSkill) {
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
                  if (selObj instanceof ForbidSkill) {
                      
                      if (sel.isEmpty() || !(sel.getFirstElement() instanceof ForbidSkill)) {
                          MessageDialog.openInformation(getShell(), "提示：", "请选择一个有效技能！");
                          return;
                      } else {
                          Object[] sels = sel.toArray();
                          for (Object obj : sels) {
                              if (obj instanceof ForbidSkill) {
                                  if(selectedItems.contains(obj)) {
                                      MessageDialog.openInformation(getShell(), "提示：", "该技能已经添加！");
                                      return;
                                  } else {                                    
                                      selectedItems.add((ForbidSkill)obj);                                    
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
//                   //   Object[] se=sel.toArray();
//                      String str=sel.toString();
//                     
////                      System.out.println(str);
//                      if(itemName.contains(str)){
//                          MessageDialog.openInformation(getShell(), "提示：", "该物品已经添加！");
//                        return;
//                      }else{
//                          itemName.add(str);
//                      }
//                      listview.refresh();
////                      ForbidSkill item = null;
////                      System.out.println("kjk"+item.id);
////                     
////                      selectedItems.add((ForbidSkill)ProjectData.getActiveProject().findForbidName(item.id));
//                  }
//              }
//          });
          treeViewer.setLabelProvider(new TreeLabelProvider());
          treeViewer.setContentProvider(new TreeContentProvider());
          tree = treeViewer.getTree();
          tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
          treeViewer.setInput(ProjectData.getActiveProject());
//          treeViewer.expandAll();
          
//          if (selectedItem != -1) {
//              try {
//                  // 查找这个物品在tree中的位置
//                  Item item = ProjectData.getActiveProject().findItemOrEquipment(selectedItem);
//                  if (item != null) {
//                      searchCondition = item.title;
//                      text.setText(searchCondition);
//                      text.selectAll();
//                      StructuredSelection sel = new StructuredSelection(item);
//                      treeViewer.setSelection(sel);
//                  }
//              } catch (Exception e) {
//              }
//          }

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
          newShell.setText("选择禁用的技能组");
      }    
  }
    
//    class ListContentProvider implements IStructuredContentProvider {
//        public Object[] getElements(Object inputElement) {
//            List<DataObject> list = ((ProjectData)inputElement).getDataListByType(ForbidSkill.class);
//            List<ForbidSkill> retList = new ArrayList<ForbidSkill>();
//            for (int i = 0; i < list.size(); i++) {
//                ForbidSkill q = (ForbidSkill)list.get(i);
//                if (matchCondition(q)) {
//                    retList.add(q);
//                }
//            }
//            return retList.toArray();
//        }
//        public void dispose() {
//        }
//        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//        }
//    }
//    
//    class ListContentProvider2 implements IStructuredContentProvider{
//
//        public Object[] getElements(Object inputElement) {
//            List el = (List)inputElement;
//            return el.toArray();
//        }
//
//        public void dispose() {}
//
//        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
//    }
//    
//    
//    class ListLabelProvider implements ILabelProvider{
//
//        public Image getImage(Object element) {
//            return null;
//        }
//
//        public String getText(Object element) {
//            return element.toString();
//        }
//
//        public void addListener(ILabelProviderListener listener) {}
//
//        public void dispose() {}
//
//        public boolean isLabelProperty(Object element, String property) {
//            return false;
//        }
//
//        public void removeListener(ILabelProviderListener listener) {}
//    }
//    
//    class TreeLabelProvider extends LabelProvider {
//        public String getText(Object element) {
//            if (element instanceof ProjectData) {
//                return "项目";
//            }
//            return super.getText(element);
//        }
//        public Image getImage(Object element) {
//            if (element instanceof DataObjectCategory) {
//                return EditorPlugin.getDefault().getImageRegistry().get("itemtype");
//            } else if (element instanceof ForbidSkill) {
//                return EditorPlugin.getDefault().getImageRegistry().get("Item");
//            }
//            return null;
//        }
//    }
//    class TreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {
//        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//        }
//        public void dispose() {
//        }
//        public Object[] getElements(Object inputElement) {
//            return getChildren(inputElement);
//        }
//        public Object[] getChildren(Object parentElement) {
//            if (parentElement instanceof ProjectData) {
//                // 根节点是ProjectData，第一层子节点是所有的物品类型和装备类型
//                List<DataObjectCategory> retList = new ArrayList<DataObjectCategory>();
//                if (includeItem) {
//                    List<DataObjectCategory> cateList = ((ProjectData)parentElement).getCategoryListByType(ForbidSkill.class);
//                    for (DataObjectCategory cate : cateList) {
//                        if (getChildren(cate).length > 0) {
//                            retList.add(cate);
//                            
//                        }
//                    }
//                }
////                List<DataObjectCategory> cateList = ((ProjectData)parentElement).getCategoryListByType(Equipment.class);
////                for (DataObjectCategory cate : cateList) {
////                    if (getChildren(cate).length > 0) {
////                        retList.add(cate);
////                    }
////                }
//                return retList.toArray();
//            } 
//            else if (parentElement instanceof DataObjectCategory) {
//                List<ForbidSkill>  retList= new ArrayList<ForbidSkill>();
//                for (DataObject dobj : ((DataObjectCategory)parentElement).objects) {
//                    if (matchCondition((ForbidSkill)dobj)) {
//                        retList.add((ForbidSkill)dobj);
//                    }                    
//                }
//                                
//                for(DataObjectCategory cate : ((DataObjectCategory)parentElement).cates) {
//                    Object[] objs = getChildren(cate);
//                    for(Object dobj2 : objs) {
//                        if (matchCondition((ForbidSkill)dobj2)) {
//                            retList.add((ForbidSkill)dobj2);
//                        }
//                    }
//                }
//                
//                return retList.toArray();
//            } 
//            return new Object[0];
//        }
//        public Object getParent(Object element) {
//            if (element instanceof ProjectData) {
//                return null;
//            } else if (element instanceof DataObjectCategory) {
//                return ProjectData.getActiveProject();
////            } else if (element instanceof Equipment) {
////                return ((Equipment)element).owner.findCategory(Equipment.class, ((Equipment)element).getCategoryName());
//            } else if (element instanceof ForbidSkill) {
//                return ((ForbidSkill)element).owner.findCategory(SkillConfig.class, ((ForbidSkill)element).getCategoryName());
//            }
//            return null;
//        }
//        public boolean hasChildren(Object element) {
//            return (element instanceof ProjectData || element instanceof DataObjectCategory);
//        }
//    }
//    
//    private TreeViewer treeViewer;
//    private Tree tree;
//    private boolean includeItem = true;
//    private boolean multiSel = false;
//    private List<ForbidSkill> selectedItems = new ArrayList<ForbidSkill>();
//    private List<String> itemName=new  ArrayList<String>();
//    private ListViewer listview;
//    
//    public List<ForbidSkill> getSelectedItems() {
//        return selectedItems;
//    }
//    
//    
//    
//    
//    
//    
//    
//    
//    private ListViewer listViewer;
//    private org.eclipse.swt.widgets.List list;
//    private Text text;
//    private String searchCondition;
//    
//    private List<ForbidSkill> selectedSkills = new ArrayList<ForbidSkill>();
//    private ListViewer selListView;
//    
//    public List<ForbidSkill> getSelectedSkills() {
//        return selectedSkills;
//    }
//    
//    private boolean matchCondition(ForbidSkill q) {
//        if (searchCondition == null || searchCondition.length() == 0) {
//            return true;
//        }
//        if (q.getTitle().indexOf(searchCondition) >= 0 || String.valueOf(q.getId()).indexOf(searchCondition) >= 0) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * Create the dialog
//     * @param parentShell
//     */
//    public CollectSkillDialog(Shell parentShell) {
//        super(parentShell);
//    }
//
//    public void setSelSkills(int[] skillIds) {
//        if(skillIds != null) {
//            for(int i=0; i<skillIds.length; i++) {
//                ForbidSkill sc = (ForbidSkill)ProjectData.getActiveProject().findObject(ForbidSkill.class, skillIds[i]);
//                if(sc != null) {
//                    selectedSkills.add(sc);
//                }
//            }
//        }
//    }
//    /**
//     * Create contents of the dialog
//     * @param parent
//     */
//    protected Control createDialogArea(Composite parent) {
//        Composite parentContainer = (Composite) super.createDialogArea(parent);
//        GridLayout gridLayout = new GridLayout();
//        gridLayout.numColumns = 2;
//        parentContainer.setLayout(gridLayout);
//        
//        Composite container = new Composite(parentContainer, SWT.NONE);
//        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//        gridLayout = new GridLayout();
//        gridLayout.numColumns = 2;
//        container.setLayout(gridLayout);
//
//        Composite listContainer = new Composite(parentContainer, SWT.BORDER);
//        listContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//        selListView = new ListViewer(listContainer, SWT.FILL | SWT.BORDER | SWT.V_SCROLL);
//        selListView.setContentProvider(new ListContentProvider2());
//        selListView.setLabelProvider(new ListLabelProvider());
//        selListView.setInput(selectedSkills);
//        final GridData gd_npcTemplateList = new GridData(SWT.FILL, SWT.FILL, true, true);
//        gd_npcTemplateList.exclude = true;
//        selListView.getList().setBounds(0, 0, 380, 400);
//        selListView.getList().setLayoutData(gd_npcTemplateList);
//        
//        selListView.addDoubleClickListener(new IDoubleClickListener() {
//            public void doubleClick(final DoubleClickEvent event) {
//                StructuredSelection sel = (StructuredSelection)event.getSelection();
//                if (sel.isEmpty()) {
//                    return;
//                }
//                Object selObj = sel.getFirstElement();
//                if (selObj instanceof ForbidSkill) {
//                    selectedSkills.remove(selObj);
//                    selListView.refresh();
//                }
//            }
//        });
//        
//        selListView.getList().addKeyListener(new KeyAdapter(){
//            public void keyReleased(KeyEvent e) {
//                if(e.keyCode == SWT.DEL){
//                    Object selData = selListView.getElementAt(selListView.getList().getSelectionIndex());
//                    if(selData instanceof ForbidSkill) {
//                        selectedSkills.remove(selData);
//                        selListView.refresh();
//                    }
//                }
//            }
//        });
//        
//        final Label label = new Label(container, SWT.NONE);
//        label.setText("查找：");
//
//        text = new Text(container, SWT.BORDER);
//        text.addModifyListener(new ModifyListener() {
//            public void modifyText(final ModifyEvent e) {
//                searchCondition = text.getText();
//                StructuredSelection sel = (StructuredSelection)listViewer.getSelection();
//                Object selObj = sel.isEmpty() ? null : sel.getFirstElement();
//                listViewer.refresh();
//                if (selObj != null) {
//                    sel = new StructuredSelection(selObj);
//                    listViewer.setSelection(sel);
//                }
//            }
//        });
//        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
//
//        listViewer = new ListViewer(container, SWT.BORDER | SWT.V_SCROLL);
//        listViewer.setContentProvider(new ListContentProvider());
//        list = listViewer.getList();
//        list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
//        listViewer.addDoubleClickListener(new IDoubleClickListener() {
//            public void doubleClick(final DoubleClickEvent event) {
//                StructuredSelection sel = (StructuredSelection)event.getSelection();
//                if (sel.isEmpty()) {
//                    return;
//                }
//                Object selObj = sel.getFirstElement();
//                if (selObj instanceof ForbidSkill) {
//                    if(selectedSkills.contains(selObj)) {
//                        MessageDialog.openInformation(getShell(), "提示：", "该技能已经添加！");
//                        return;
//                    } else {
//                        selectedSkills.add((ForbidSkill)selObj);                        
//                    }
//                    selListView.refresh();
//                }
//                
//            }
//        });
//        listViewer.setInput(ProjectData.getActiveProject());
//        
//        return container;
//    }
//    
//    private void removeSkillConfig() {
//        
//    }
//
//    /**
//     * Create contents of the button bar
//     * @param parent
//     */
//    protected void createButtonsForButtonBar(Composite parent) {
//        createButton(parent, IDialogConstants.OK_ID, "确定", true);
//        createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
//    }
//
//    /**
//     * Return the initial size of the dialog
//     */
//    protected Point getInitialSize() {
//        return new Point(520, 465);
//    }
//    
//    protected void configureShell(Shell newShell) {
//        super.configureShell(newShell);
//        newShell.setText("选择禁用的技能组");
//    }

