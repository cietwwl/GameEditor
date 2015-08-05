package com.pip.game.editor.property;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
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
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.editor.EditorPlugin;

public class ChooseSkillDialog extends Dialog {

    private Text text;
    private String searchCondition;

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
            } else if (element instanceof SkillConfig) {
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
                List<DataObjectCategory> retList = new ArrayList<DataObjectCategory>();
                if (includeSkill) {
                    List<DataObjectCategory> cateList = ((ProjectData)parentElement).getCategoryListByType(SkillConfig.class);
                    for (DataObjectCategory cate : cateList) {
                        if (getChildren(cate).length > 0) {
                            retList.add(cate);
                        }
                    }
                }
                return retList.toArray();
            } else if (parentElement instanceof DataObjectCategory) {
                List retList = new ArrayList();
                for (DataObjectCategory cate : ((DataObjectCategory)parentElement).cates) {
                    retList.add(cate);
                }
                for (DataObject dobj : ((DataObjectCategory)parentElement).objects) {
                    if (matchCondition((SkillConfig)dobj)) {
                        retList.add((SkillConfig)dobj);
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
            } else if (element instanceof SkillConfig) {
                return ((SkillConfig)element).owner.findCategory(SkillConfig.class, ((SkillConfig)element).getCategoryName());
            } 
            return null;
        }
        public boolean hasChildren(Object element) {
            return (element instanceof ProjectData || element instanceof DataObjectCategory);
        }
    }
    private TreeViewer treeViewer;
    private Tree tree;
    private int selectedSkill = -1;
    private boolean includeSkill = true;
    private boolean multiSel = false;
    private List<SkillConfig> selectedSkills;
    
    public int getSelectedSkill() {
        return selectedSkill;
    }

    public void setSelectedSkill(int selectedSkill) {
        this.selectedSkill = selectedSkill;
    }
    
    public List<SkillConfig> getSelectedSkills() {
        return selectedSkills;
    }
    
    private boolean matchCondition(SkillConfig skill) {
        if (searchCondition == null || searchCondition.length() == 0) {
            return true;
        }
        if (skill.title.indexOf(searchCondition) >= 0 || String.valueOf(skill.id).indexOf(searchCondition) >= 0) {
            return true;
        }
        return false;
    }

    /**
     * Create the dialog
     * @param parentShell
     */
    public ChooseSkillDialog(Shell parentShell) {
        super(parentShell);
    }
    
    public void setIncludeSkill(boolean value) {
        includeSkill = value;
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
                treeViewer.expandAll();
                if (selObj != null) {
                    sel = new StructuredSelection(selObj);
                    treeViewer.setSelection(sel);
                }
            }
        });
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        treeViewer = new TreeViewer(container, SWT.BORDER | (multiSel ? SWT.MULTI : 0));
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)event.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                Object selObj = sel.getFirstElement();
                if (selObj instanceof SkillConfig) {
                    buttonPressed(IDialogConstants.OK_ID);
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
        
        if (selectedSkill != -1) {
            try {
                SkillConfig skill = ProjectData.getActiveProject().findSkill(selectedSkill);
                if (skill != null) {
                    searchCondition = skill.title;
                    text.setText(searchCondition);
                    text.selectAll();
                    StructuredSelection sel = new StructuredSelection(skill);
                    treeViewer.setSelection(sel);
                }
            } catch (Exception e) {
            }
        }

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
        newShell.setText("选择技能");
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            StructuredSelection sel = (StructuredSelection)treeViewer.getSelection();
            if (sel.isEmpty() || !(sel.getFirstElement() instanceof SkillConfig)) {
//                MessageDialog.openInformation(getShell(), "提示：", "请选择一个有效技能！");
                selectedSkill = -1;//选空时清除选择的技能
            } else {
                selectedSkill = ((SkillConfig)sel.getFirstElement()).id;
                selectedSkills = new ArrayList<SkillConfig>();
                Object[] sels = sel.toArray();
                for (Object obj : sels) {
                    if (obj instanceof SkillConfig) {
                        selectedSkills.add((SkillConfig)obj);
                    }
                }
            }
        }
        super.buttonPressed(buttonId);
    }
}
