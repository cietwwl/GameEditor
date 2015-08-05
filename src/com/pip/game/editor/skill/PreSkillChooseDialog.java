package com.pip.game.editor.skill;

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
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.EditorPlugin;
import com.pip.game.editor.item.ItemTreeViewer;

/**
 * @author wpjiang
 * 选择前置技能，可以包括级别
 */
public class PreSkillChooseDialog extends Dialog {
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
                    List<DataObjectCategory> cateList = ((ProjectData)parentElement).getCategoryListByType(SkillConfig.class);
                    for (DataObjectCategory cate : cateList) {
                        if (getChildren(cate).length > 0) {
                            retList.add(cate);
                            
                        }
                    }
                }
                return retList.toArray();
            } 
            else if (parentElement instanceof DataObjectCategory) {
                List<SkillConfig>  retList= new ArrayList<SkillConfig>();
                for (DataObject dobj : ((DataObjectCategory)parentElement).objects) {
                    if (matchCondition((SkillConfig)dobj)) {
                        retList.add((SkillConfig)dobj);
                    }                    
                }
                                
                for(DataObjectCategory cate : ((DataObjectCategory)parentElement).cates) {
                    Object[] objs = getChildren(cate);
                    for(Object dobj2 : objs) {
                        if (matchCondition((SkillConfig)dobj2)) {
                            retList.add((SkillConfig)dobj2);
                        }
                    }
                }
                
                return retList.toArray();
            } else if(parentElement instanceof SkillConfig && ((SkillConfig)parentElement).currLevel == SkillConfig.PRE_SKILL ){//生成下面的 级别
                List<SkillConfig>  retList= new ArrayList<SkillConfig>();
                SkillConfig skillConfig = (SkillConfig) parentElement;
                for(int i = 0; i < skillConfig.maxLevel; i++){
                    SkillConfig preSkillConfig = (SkillConfig) skillConfig.duplicate();
                    preSkillConfig.currLevel = i;
                    retList.add(preSkillConfig);
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
            } else if ((element instanceof SkillConfig && ((SkillConfig)element).currLevel == SkillConfig.PRE_SKILL)) {
                //return ((ForbidItem)element).owner.findCategory(Item.class, ((ForbidItem)element).getCategoryName());
                return((SkillConfig)element).owner.findCategory(SkillConfig.class, ((SkillConfig)element).getCategoryName());
            }
            return null;
        }
        public boolean hasChildren(Object element) {
            return (element instanceof ProjectData || element instanceof DataObjectCategory || (element instanceof SkillConfig && ((SkillConfig)element).currLevel == SkillConfig.PRE_SKILL));
        }
    }
    private TreeViewer treeViewer;
    private Tree tree;
    private boolean includeItem = true;
    private boolean multiSel = false;
    private ListViewer listview;
    
    private List<SkillConfig> selectedSkillConfig = new ArrayList<SkillConfig>();
    
    public List<SkillConfig> getSelectedSkillConfig() {
        return selectedSkillConfig;
    }
    
    /**
     * 设置所有技能的前置技能
     */
    public void setPreSkills(List<SkillConfig> skillConfig) {
        this.selectedSkillConfig = skillConfig;
    }
    
    private boolean matchCondition(SkillConfig Skillconfig) {
        if (searchCondition == null || searchCondition.length() == 0) {
            return true;
        }
        if (Skillconfig.title.indexOf(searchCondition) >= 0 || String.valueOf(Skillconfig.id).indexOf(searchCondition) >= 0) {
            System.out.println(Skillconfig.id);
            return true;
        }
        return false;
    }

    /**
     * Create the dialog
     * @param parentShell
     */
    public PreSkillChooseDialog(Shell parentShell) {
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
        final GridData gd_textPreDesc = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_textPreDesc.widthHint = 600;
        gd_textPreDesc.heightHint = 800;
        container.setLayoutData(gd_textPreDesc);
        gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        container.setLayout(gridLayout);

        Composite listContainer = new Composite(parentContainer, SWT.BORDER);
        listContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        listview = new ListViewer(listContainer, SWT.FILL | SWT.BORDER | SWT.V_SCROLL);
        listview.setContentProvider(new ListContentProvider());
        listview.setLabelProvider(new ListLabelProvider());
        listview.setInput(this.selectedSkillConfig);
        final GridData gd_npcTemplateList = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_npcTemplateList.exclude = true;
        listview.getList().setBounds(0, 0, 1000, 800);
        listview.getList().setLayoutData(gd_npcTemplateList);
         
        listview.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)event.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                Object selObj = sel.getFirstElement();
                if (selObj instanceof SkillConfig) {
                    selectedSkillConfig.remove(selObj);
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
                if (selObj instanceof SkillConfig) {
                    Object[] sels = sel.toArray();
                    for (Object obj : sels) {
                        if (obj instanceof SkillConfig) {
                            if(selectedSkillConfig.contains(obj)) {
                                MessageDialog.openInformation(getShell(), "提示：", "该物品已经添加！");
                                return;
                            }
                            //此处打开的话， 就只能添加有级别的技能
                           /* else if(((SkillConfig)obj).currLevel == SkillConfig.PRE_SKILL){
                                MessageDialog.openInformation(getShell(), "提示：", "只能添加有级别的！");
                                return;
                            }*/
                            else{                                    
                                selectedSkillConfig.add((SkillConfig)obj);                                    
                            }
                        }
                    }                        
                    listview.refresh();
                } else{
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
        newShell.setText("选择该技能的前置技能");
        newShell.setSize(1000, 800);
    }    
}

