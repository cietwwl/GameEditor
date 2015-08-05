package com.pip.game.editor.property;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import com.pip.game.editor.EditorPlugin;
import com.pip.game.editor.item.ItemTreeViewer;

/**
 * 通用数据对象选择对话框。
 * @author lighthu
 */
public class ChooseDataObjectDialog extends Dialog {
    // 过滤条件
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
            } else if (element instanceof DataObject) {
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
                // 根节点是ProjectData，第一层子节点是所有的分类，外加代表不选择的特殊项
                List<Object> retList = new ArrayList<Object>();
                List<DataObjectCategory> cateList = ((ProjectData) parentElement).getCategoryListByType(clazz);
                for (DataObjectCategory cate : cateList) {
                    if (getChildren(cate).length > 0) {
                        retList.add(cate);
                    }
                }
                retList.add(0, "<取消选择>");
                return retList.toArray();
            } else if (parentElement instanceof DataObjectCategory) {
                List<Object> retList = new ArrayList<Object>();
                for (DataObjectCategory cate : ((DataObjectCategory) parentElement).cates) {
                    if (getChildren(cate).length > 0) {
                        retList.add(cate);
                    }
                }
                for (DataObject dobj : ((DataObjectCategory) parentElement).objects) {
                    if (matchCondition(dobj)) {
                        retList.add(dobj);
                    }
                }
                return retList.toArray();
            }
            return new Object[0];
        }

        public Object getParent(Object element) {
            if (element instanceof ProjectData) {
                return null;
            } else if (element instanceof String) {
                return ProjectData.getActiveProject();
            } else if (element instanceof DataObjectCategory) {
                return ProjectData.getActiveProject();
            } else if (element instanceof DataObject) {
                return ((DataObject)element).cate;
            }
            return null;
        }

        public boolean hasChildren(Object element) {
            return (element instanceof ProjectData || element instanceof DataObjectCategory);
        }
    }

    private TreeViewer treeViewer;
    private Tree tree;
    private DataObject selDataObject;
    private Class clazz;
    private String title = "选择";

    public DataObject getSelectedDataObject() {
        return selDataObject;
    }

    public void setSelDataObject(int dataObjectId) {
        selDataObject = ProjectData.getActiveProject().findObject(clazz, dataObjectId);
    }

    private boolean matchCondition(DataObject item) {
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
     * 
     * @param parentShell
     */
    public ChooseDataObjectDialog(Shell parentShell, Class clazz, String title) {
        super(parentShell);
        this.clazz = clazz;
        this.title = title;
    }

    /**
     * Create contents of the dialog
     * 
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
                StructuredSelection sel = (StructuredSelection) treeViewer.getSelection();
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

        treeViewer = new TreeViewer(container, SWT.BORDER);
        treeViewer.setLabelProvider(new TreeLabelProvider());
        treeViewer.setContentProvider(new TreeContentProvider());
        tree = treeViewer.getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        treeViewer.setInput(ProjectData.getActiveProject());

        if (selDataObject != null) {
            searchCondition = selDataObject.title;
            text.setText(searchCondition);
            text.selectAll();
            StructuredSelection sel = new StructuredSelection(selDataObject);
            treeViewer.setSelection(sel);
            treeViewer.expandToLevel(selDataObject, TreeViewer.ALL_LEVELS);
        }
        
        return container;
    }

    /**
     * Create contents of the button bar
     * 
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
        newShell.setText(title);
    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            StructuredSelection sel = (StructuredSelection) treeViewer.getSelection();
            if (sel.isEmpty() || !(sel.getFirstElement() instanceof DataObject)) {
                selDataObject = null;
            }
            else {
                selDataObject = (DataObject) sel.getFirstElement();
            }
        }
        super.buttonPressed(buttonId);
    }
}
