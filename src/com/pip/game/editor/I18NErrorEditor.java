package com.pip.game.editor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.pip.game.data.ProjectData;
import com.pip.game.data.i18n.I18NError;

public class I18NErrorEditor extends EditorPart {
    class ContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            return errorList.toArray();
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            return ((I18NError)element).message;
        }
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }
    private Text text;
    private Table table;
    public static final String ID = "com.pip.game.editor.I18NErrorEditor"; //$NON-NLS-1$
    private TableViewer tableViewer;
    private List<I18NError> errorList;
    
    /**
     * Create contents of the editor part
     * @param parent
     */
    public void createPartControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        container.setLayout(gridLayout);

        tableViewer = new TableViewer(container, SWT.BORDER);
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                I18NError err = getSelection();
                if (err != null && err.source != null) {
                    DataListView.editObject(ProjectData.getActiveProject().findObject(err.source.getClass(), err.source.getId()));
                }
            }
        });
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                I18NError err = getSelection();
                if (err == null) {
                    text.setText("");
                } else {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    pw.println(err.message);
                    if (err.exception != null) {
                        err.exception.printStackTrace(pw);
                    }
                    pw.flush();
                    text.setText(sw.toString());
                }
            }
        });
        tableViewer.setContentProvider(new ContentProvider());
        tableViewer.setLabelProvider(new TableLabelProvider());
        table = tableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        final GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_table.widthHint = 100;
        table.setLayoutData(gd_table);
        tableViewer.setInput(this);

        final TableColumn nameColumn = new TableColumn(table, SWT.NONE);
        nameColumn.setWidth(406);
        nameColumn.setText("¥ÌŒÛ‘≠“Ú");

        new TableColumn(table, SWT.NONE);

        text = new Text(container, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER);
        final GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_text.widthHint = 100;
        text.setLayoutData(gd_text);
        
        setPartName("¥ÌŒÛ");
    }
    
    private I18NError getSelection() {
        StructuredSelection sel = (StructuredSelection)tableViewer.getSelection();
        if (sel.isEmpty()) {
            return null;
        }
        return (I18NError)sel.getFirstElement();
    }
    
    public void setFocus() {
        // Set the focus
    }

    public void doSave(IProgressMonitor monitor) {
        // Do the Save operation
    }

    @Override
    public void doSaveAs() {
        // Do the Save As operation
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        // Initialize the editor part
        setSite(site);
        setInput(input);
        errorList = new ArrayList<I18NError>();
        errorList.addAll(I18NError.errorList);
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }
}
