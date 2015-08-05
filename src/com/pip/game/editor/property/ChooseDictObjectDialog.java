package com.pip.game.editor.property;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

/**
 * 通用字典对象选择对话框。
 * @author lighthu
 */
public class ChooseDictObjectDialog extends Dialog {
    class ListContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            List<DataObject> list = ((ProjectData)inputElement).getDictDataListByType(clazz);
            List<Object> retList = new ArrayList<Object>();
            retList.add("<取消选择>");
            for (int i = 0; i < list.size(); i++) {
                DataObject q = (DataObject)list.get(i);
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
    
    private ListViewer listViewer;
    private org.eclipse.swt.widgets.List list;
    private Text text;
    private String searchCondition;
    private int selectedObjectID = -1;
    private Class clazz;
    private String title;
    
    public int getSelectedObjectID() {
        return selectedObjectID;
    }

    public void setSelectedObjectID(int t) {
        this.selectedObjectID = t;
    }
    
    private boolean matchCondition(DataObject q) {
        if (searchCondition == null || searchCondition.length() == 0) {
            return true;
        }
        if (q.title.indexOf(searchCondition) >= 0 || String.valueOf(q.id).indexOf(searchCondition) >= 0) {
            return true;
        }
        return false;
    }

    /**
     * Create the dialog
     * @param parentShell
     */
    public ChooseDictObjectDialog(Shell parentShell, Class clazz, String title) {
        super(parentShell);
        this.clazz = clazz;
        this.title = title;
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
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
                buttonPressed(IDialogConstants.OK_ID);
            }
        });
        listViewer.setInput(ProjectData.getActiveProject());
        
        if (selectedObjectID != -1) {
            DataObject q = ProjectData.getActiveProject().findDictObject(clazz, selectedObjectID);
            if (q != null) {
                searchCondition = q.title;
                text.setText(searchCondition);
                text.selectAll();
                StructuredSelection sel = new StructuredSelection(q);
                listViewer.setSelection(sel);
            }
        }

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
        newShell.setText(title);
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            StructuredSelection sel = (StructuredSelection)listViewer.getSelection();
            if (sel.isEmpty() || !(sel.getFirstElement() instanceof DataObject)) {
                selectedObjectID = -1;
            } else {
                selectedObjectID = ((DataObject)sel.getFirstElement()).id;
            }
        }
        super.buttonPressed(buttonId);
    }
}
