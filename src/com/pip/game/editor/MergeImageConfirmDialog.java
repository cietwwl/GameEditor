package com.pip.game.editor;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.pip.image.workshop.editor.ImageViewer;
import com.pipimage.image.PipImage;

public class MergeImageConfirmDialog extends Dialog {
    
    class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            if (columnIndex == 0) {
                return (String)element;
            } else if (columnIndex == 1) {
                return mapNames.get((String)element);
            } else if (columnIndex == 2) {
                return new DecimalFormat("###.##%").format(mapRates.get((String)element));
            }
            return element.toString();
        }
        
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }
    
    class ContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            return sourceNames;
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    private Table table;
    private CheckboxTableViewer checkboxTableViewer;
    private ImageViewer leftViewer;
    private ImageViewer rightViewer;
    
    private String[] sourceNames;
    private File baseDir;
    private HashMap<String, String> mapNames;
    private HashMap<String, Double> mapRates;
    private Set<String> selectedNames;
    
    /**
     * Create the dialog
     * @param parentShell
     */
    public MergeImageConfirmDialog(Shell parentShell, File baseDir, HashMap<String, String> mapNames, HashMap<String, Double> mapRates) {
        super(parentShell);
        this.baseDir = baseDir;
        sourceNames = new String[mapNames.size()];
        mapNames.keySet().toArray(sourceNames);
        this.mapNames = mapNames;
        this.mapRates = mapRates;
    }
    
    public Set<String> getSelectedNames() {
        return selectedNames;
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

        checkboxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.FULL_SELECTION | SWT.BORDER);
        checkboxTableViewer.setLabelProvider(new TableLabelProvider());
        checkboxTableViewer.setContentProvider(new ContentProvider());
        table = checkboxTableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        final GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        gd_table.heightHint = 400;
        table.setLayoutData(gd_table);

        final TableColumn fromColumn = new TableColumn(table, SWT.NONE);
        fromColumn.setWidth(184);
        fromColumn.setText("原文件");

        final TableColumn targetColumn = new TableColumn(table, SWT.NONE);
        targetColumn.setWidth(175);
        targetColumn.setText("合并到");

        final TableColumn rateColumn = new TableColumn(table, SWT.NONE);
        rateColumn.setWidth(118);
        rateColumn.setText("相似率");

        checkboxTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                StructuredSelection sel = (StructuredSelection)checkboxTableViewer.getSelection();
                if (sel.isEmpty()) {
                    leftViewer.setInput(null);
                    rightViewer.setInput(null);
                    return;
                }
                try {
                    String name = (String)sel.getFirstElement();
                    PipImage pimg = new PipImage();
                    pimg.load(new File(baseDir, name).getAbsolutePath());
                    leftViewer.setInput(pimg);
                    leftViewer.refresh();
                    String newName = mapNames.get(name);
                    pimg = new PipImage();
                    pimg.load(new File(baseDir, newName).getAbsolutePath());
                    rightViewer.setInput(pimg);
                    rightViewer.refresh();
                } catch (Exception e) {
                    MessageDialog.openError(getShell(), "错误", e.toString());
                }
            }
        });
        checkboxTableViewer.setInput(new Object());
        checkboxTableViewer.setAllChecked(true);

        final Composite leftViewContainer = new Composite(container, SWT.NONE);
        leftViewContainer.setLayout(new FillLayout());
        final GridData gd_leftViewContainer = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_leftViewContainer.heightHint = 400;
        leftViewContainer.setLayoutData(gd_leftViewContainer);

        leftViewer = new ImageViewer(leftViewContainer, SWT.NONE);
        leftViewer.setFlatMode(true);

        final Composite rightViewContainer = new Composite(container, SWT.NONE);
        rightViewContainer.setLayout(new FillLayout());
        final GridData gd_rightViewContainer = new GridData(SWT.FILL, SWT.FILL, true, true);
        rightViewContainer.setLayoutData(gd_rightViewContainer);
        
        rightViewer = new ImageViewer(rightViewContainer, SWT.NONE);
        rightViewer.setFlatMode(true);
        
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
        return new Point(724, 833);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("合并图片确认");
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            selectedNames = new HashSet<String>();
            Object[] objs = checkboxTableViewer.getCheckedElements();
            for (Object obj : objs) {
                selectedNames.add((String)obj);
            }
        }
        super.buttonPressed(buttonId);
    }
}
