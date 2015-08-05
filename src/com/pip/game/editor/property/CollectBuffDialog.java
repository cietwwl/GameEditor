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
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.skill.BuffConfig;
import com.pip.game.data.skill.IBuffConfig;
import com.pip.game.editor.EditorApplication;

public class CollectBuffDialog extends Dialog {
    class ListContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            List<DataObject> list = ((ProjectData)inputElement).getDataListByType(BuffConfig.class);
            List<IBuffConfig> retList = new ArrayList<IBuffConfig>();
            for (int i = 0; i < list.size(); i++) {
                IBuffConfig q = (IBuffConfig)list.get(i);
                if (matchCondition(q)) {
                    retList.add(q);
                }
            }
            return retList.toArray();
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    class ListContentProvider2 implements IStructuredContentProvider{

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
    
    private ListViewer listViewer;
    private org.eclipse.swt.widgets.List list;
    private Text text;
    private String searchCondition;
    
    private List<IBuffConfig> selectedBuffs = new ArrayList<IBuffConfig>();
    private ListViewer selListView;
    
    public List<IBuffConfig> getSelectedBuffs() {
        return selectedBuffs;
    }
    
    public int[] getBuffIds() {
        int count = selectedBuffs.size();
        int[] ret = new int[count];
        for(int i=0; i<count; i++) {
            ret[i] = selectedBuffs.get(i).getId();
        }
        
        return ret;
    }
    
    private boolean matchCondition(IBuffConfig q) {
        if (searchCondition == null || searchCondition.length() == 0) {
            return true;
        }
        if (q.getTitle().indexOf(searchCondition) >= 0 || String.valueOf(q.getId()).indexOf(searchCondition) >= 0) {
            return true;
        }
        return false;
    }

    /**
     * Create the dialog
     * @param parentShell
     */
    public CollectBuffDialog(Shell parentShell) {
        super(parentShell);
    }

    public void setSelBuffs(int[] buffIds) {
        if(buffIds != null) {
            for(int i=0; i<buffIds.length; i++) {
                IBuffConfig sc = (IBuffConfig)ProjectData.getActiveProject().findObject(BuffConfig.class, buffIds[i]);
                if(sc != null) {
                    selectedBuffs.add(sc);
                }
            }
        }
    }
    /**
     * Create contents of the dialog
     * @param parent
     */
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
        selListView = new ListViewer(listContainer, SWT.FILL | SWT.BORDER | SWT.V_SCROLL);
        selListView.setContentProvider(new ListContentProvider2());
        selListView.setLabelProvider(new ListLabelProvider());
        selListView.setInput(selectedBuffs);
        final GridData gd_npcTemplateList = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_npcTemplateList.exclude = true;
        selListView.getList().setBounds(0, 0, 300, 600);
        selListView.getList().setLayoutData(gd_npcTemplateList);
        
        selListView.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)event.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                Object selObj = sel.getFirstElement();
                if (selObj instanceof IBuffConfig) {
                    selectedBuffs.remove(selObj);
                    selListView.refresh();
                }
            }
        });
        
        selListView.getList().addKeyListener(new KeyAdapter(){
            public void keyReleased(KeyEvent e) {
                if(e.keyCode == SWT.DEL){
                    Object selData = selListView.getElementAt(selListView.getList().getSelectionIndex());
                    if(selData instanceof IBuffConfig) {
                        selectedBuffs.remove(selData);
                        selListView.refresh();
                    }
                }
            }
        });
        
        final Label label = new Label(container, SWT.NONE);
        label.setText("查找：");

        text = new Text(container, SWT.BORDER);
        text.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                searchCondition = text.getText();
                StructuredSelection sel = (StructuredSelection)listViewer.getSelection();
                Object selObj = sel.isEmpty() ? null : sel.getFirstElement();
                listViewer.refresh();
                if (selObj != null) {
                    sel = new StructuredSelection(selObj);
                    listViewer.setSelection(sel);
                }
            }
        });
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        listViewer = new ListViewer(container, SWT.BORDER | SWT.V_SCROLL);
        listViewer.setContentProvider(new ListContentProvider());
        list = listViewer.getList();
        list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        listViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)event.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                Object selObj = sel.getFirstElement();
                if (selObj instanceof IBuffConfig) {
                    if(selectedBuffs.contains(selObj)) {
                        MessageDialog.openInformation(getShell(), "提示：", "该技能已经添加！");
                        return;
                    } else {
                        selectedBuffs.add((IBuffConfig)selObj);                        
                    }
                    selListView.refresh();
                }
            }
        });
        listViewer.setInput(ProjectData.getActiveProject());
        
        return container;
    }

    /**
     * Create contents of the button bar
     * @param parent
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "确定", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
    }

    /**
     * Return the initial size of the dialog
     */
    protected Point getInitialSize() {
        return new Point(520, 465);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("选择禁用的技能");
    }
}
