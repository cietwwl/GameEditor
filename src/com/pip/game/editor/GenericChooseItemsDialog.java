package com.pip.game.editor;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class GenericChooseItemsDialog extends Dialog {
    class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            return candidates.get(((Integer)element).intValue()).toString();
        }
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }
    class ContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            Object[] ret = new Object[candidates.size()];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new Integer(i);
            }
            return ret;
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    private Table itemTable;
    private List candidates;
    private String title;
    private boolean[] selectionFlag;
    private Object selection;
    private CheckboxTableViewer itemViewer;
    
    /**
     * Create the dialog
     * @param parentShell
     */
    public GenericChooseItemsDialog(Shell parentShell, String t, List cands) {
        super(parentShell);
        title = t;
        candidates = cands;
        selectionFlag = new boolean[candidates.size()];
        Arrays.fill(selectionFlag, true);
    }

    public boolean[] getSelectionFlag() {
        return selectionFlag;
    }
    
    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        final GridLayout gridLayout = new GridLayout();
        container.setLayout(gridLayout);

        itemViewer = CheckboxTableViewer.newCheckList(container, SWT.FULL_SELECTION | SWT.BORDER);
        itemViewer.setLabelProvider(new TableLabelProvider());
        itemViewer.setContentProvider(new ContentProvider());
        itemTable = itemViewer.getTable();
        itemTable.setLinesVisible(true);
        final GridData gd_itemTable = new GridData(SWT.FILL, SWT.FILL, true, true);
        itemTable.setLayoutData(gd_itemTable);

        final TableColumn newColumnTableColumn = new TableColumn(itemTable, SWT.NONE);
        newColumnTableColumn.setWidth(600);
        newColumnTableColumn.setText("item");

        itemViewer.setInput(candidates);
        
        for (int i = 0; i < candidates.size(); i++) {
            if (selectionFlag[i]) {
                itemViewer.setChecked(new Integer(i), true);
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
        return new Point(710, 593);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(title);
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            Object[] objs = itemViewer.getCheckedElements();
            Arrays.fill(selectionFlag, false);
            for (Object obj : objs) {
                selectionFlag[((Integer)obj).intValue()] = true;
            }
        }
        super.buttonPressed(buttonId);
    }
}
