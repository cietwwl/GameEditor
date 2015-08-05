package com.pip.game.editor.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Condition;

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
import com.pip.game.data.GameArea;
import com.pip.game.data.GameAreaInfo;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.EditorPlugin;
import com.pip.game.editor.quest.GameAreaCache;
import com.pip.image.workshop.WorkshopPlugin;

public class ChooseSceneDialog extends Dialog {
    private Text text;
    private String searchCondition;

    class TreeLabelProvider extends LabelProvider {
        public String getText(Object element) {
            if (element instanceof ProjectData) {
                return "��Ŀ";
            }
            if (element instanceof GameArea) {
                return element.toString();
            }
            if (element instanceof GameMapInfo) {
                return ((GameMapInfo)element).id + ": " + ((GameMapInfo)element).name;
            }
            return super.getText(element);
        }
        public Image getImage(Object element) {
            if (element instanceof GameArea) {
                return WorkshopPlugin.getDefault().getImageRegistry().get("animate");
            } else if (element instanceof GameMapInfo) {
                return WorkshopPlugin.getDefault().getImageRegistry().get("map");
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
                // ���ڵ���ProjectData����һ���ӽڵ������е���Ϸ�ؿ�
                List<DataObject> list = ((ProjectData)parentElement).getDataListByType(GameArea.class);
                List<DataObject> retList = new ArrayList<DataObject>();
                for (int i = 0; i < list.size(); i++) {
                    if (getChildren(list.get(i)).length > 0) {
                        retList.add(list.get(i));
                    }
                }
                return retList.toArray();
            } else if (parentElement instanceof GameArea) {
                // �ؿ�����һ��ڵ�����Ϸ����
                GameAreaInfo areaInfo = GameAreaCache.getAreaInfo(((GameArea)parentElement).id);
                if (areaInfo == null) {
                    return new Object[0];
                } else {
                    List<GameMapInfo> retList = new ArrayList<GameMapInfo>();
                    for (GameMapInfo mi : areaInfo.maps) {
                        if (matchCondition(mi)) {
                            retList.add(mi);
                        }
                    }
                    return retList.toArray();
                }
            }
            return new Object[0];
        }
        public Object getParent(Object element) {
            if (element instanceof ProjectData) {
                return null;
            } else if (element instanceof GameArea) {
                return ((GameArea)element).owner;
            } else if (element instanceof GameMapInfo) {
                return ((GameMapInfo)element).owner;
            }
            return null;
        }
        public boolean hasChildren(Object element) {
            return (element instanceof ProjectData || element instanceof GameArea || element instanceof GameMapInfo);
        }
    }
    private TreeViewer treeViewer;
    private Tree tree;
    private int selectedMap = -1;
    
    public int getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(int sel) {
        this.selectedMap = sel;
    }
    
    private boolean matchCondition(GameMapInfo map) {
        if (searchCondition == null || searchCondition.length() == 0) {
            return true;
        }
        if (map.name.indexOf(searchCondition) >= 0 || String.valueOf(map.getGlobalID()).indexOf(searchCondition) >= 0) {
            return true;
        }
        return false;
    }

    /**
     * Create the dialog
     * @param parentShell
     */
    public ChooseSceneDialog(Shell parentShell) {
        super(parentShell);
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
        label.setText("���ң�");

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

        treeViewer = new TreeViewer(container, SWT.BORDER);
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)event.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                Object selObj = sel.getFirstElement();
                if (selObj instanceof GameMapInfo) {
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
        treeViewer.expandAll();
        
        if (selectedMap != -1) {
            try {
                // ���������ͼ��tree�е�λ��
                GameMapInfo map = GameMapInfo.findByID(ProjectData.getActiveProject(), selectedMap);
                if (map != null) {
                    searchCondition = map.name;
                    text.setText(searchCondition);
                    text.selectAll();
                    StructuredSelection sel = new StructuredSelection(map);
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
        createButton(parent, IDialogConstants.OK_ID, "ȷ��", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "ȡ��", false);
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
        newShell.setText("ѡ�񳡾�");
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            StructuredSelection sel = (StructuredSelection)treeViewer.getSelection();
            if (sel.isEmpty() || !(sel.getFirstElement() instanceof GameMapInfo)) {
                selectedMap = -1;
            } else {
                selectedMap = ((GameMapInfo)sel.getFirstElement()).getGlobalID();
            }
        }
        super.buttonPressed(buttonId);
    }
    
    public TreeViewer getTreeViewer(){
        return treeViewer;
    }
}
