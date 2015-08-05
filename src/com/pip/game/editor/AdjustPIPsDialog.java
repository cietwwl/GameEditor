package com.pip.game.editor;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
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
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.pip.image.workshop.editor.ImageViewer;
import com.pip.util.EFSUtil;
import com.pipimage.image.PipImage;

public class AdjustPIPsDialog extends Dialog {
    private static String[] MODES = new String[] {
                "��ͨ256ɫģʽ����֡�洢���������ɫ", 
                "��չ256ɫģʽ����֡�洢�������ɫ", 
                "�ϲ�256ɫģʽ���ϲ��洢���������ɫ",
                "��ͨ65536ɫģʽ����֡�洢���������ɫ", 
                "��չ65536ɫģʽ����֡�洢�������ɫ",
                "���ɫģʽ"
    };
    
    class TableSorter extends ViewerSorter {
        public int compare(Viewer viewer, Object e1, Object e2) {
            TableLabelProvider lp = (TableLabelProvider)tableViewer.getLabelProvider();
            int columnIndex = 0;
            if (table.getSortColumn() == targetColumn) {
                columnIndex = 1;
            } else if (table.getSortColumn() == transparentColumn) {
                columnIndex = 2;
            } else if (table.getSortColumn() == colorColumn) {
                columnIndex = 3;
            } else if (table.getSortColumn() == sizeColumn) {
                columnIndex = 4;
            }
            String s1 = lp.getColumnText(e1, columnIndex);
            String s2 = lp.getColumnText(e2, columnIndex);
            if (columnIndex == 1 || columnIndex == 2) {
                return s1.compareTo(s2);
            } else {
                return new Integer(s1).compareTo(new Integer(s2));
            }
        }
    }
    
    class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            if (columnIndex == 0) {
                return ((File)element).getName();
            } else if (columnIndex == 1) {
                PipImage image = images.get((File)element);
                int mode;
                if (image.isTrueColor()) {
                    mode = 5;
                } else if (image.isMergeMode()) {
                    mode = 2;
                } else if (image.isSupportColorOp()) {
                    if (image.isSupportMoreColors()) {
                        mode = 4;
                    } else {
                        mode = 1;
                    }
                } else {
                    if (image.isSupportMoreColors()) {
                        mode = 3;
                    } else {
                        mode = 0;
                    }
                }
                return MODES[mode];
            } else if (columnIndex == 2) {
                PipImage image = images.get((File)element);
                if (image.hasHalfTransparent()) {
                    return "��";
                } else {
                    return "��";
                }
            } else if (columnIndex == 3) {
                PipImage image = images.get((File)element);
                if (image.isTrueColor()) {
                    return "0";
                } else if (image.getPaletteCount() == 0) {
                    return "0";
                } else {
                    return String.valueOf(image.getImagePalettes().get(0).getPalette().length);
                }
            } else if (columnIndex == 4) {
                return String.valueOf(((File)element).length());
            }
            return element.toString();
        }
        
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }
    
    class ContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            return files;
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    private Table table;
    private TableViewer tableViewer;
    private ImageViewer previewer;
    
    private File[] files;
    private HashMap<File, PipImage> images;
    private Button buttonMerge;
    private TableColumn targetColumn;
    private TableColumn transparentColumn;
    private TableColumn colorColumn;
    private TableColumn sizeColumn;
    
    /**
     * Create the dialog
     * @param parentShell
     */
    public AdjustPIPsDialog(Shell parentShell, HashMap<File, PipImage> images) {
        super(parentShell);
        files = new File[images.size()];
        images.keySet().toArray(files);
        this.images = images;
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

        tableViewer = new TableViewer(container, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        tableViewer.setLabelProvider(new TableLabelProvider());
        tableViewer.setContentProvider(new ContentProvider());
        table = tableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        final GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        gd_table.heightHint = 400;
        table.setLayoutData(gd_table);

        final TableColumn fromColumn = new TableColumn(table, SWT.NONE);
        fromColumn.setWidth(120);
        fromColumn.setText("�ļ���");

        targetColumn = new TableColumn(table, SWT.NONE);
        targetColumn.setWidth(300);
        targetColumn.setText("��ǰģʽ");
        targetColumn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                table.setSortColumn(targetColumn);
                tableViewer.setSorter(new TableSorter());
            }
        });

        transparentColumn = new TableColumn(table, SWT.NONE);
        transparentColumn.setWidth(60);
        transparentColumn.setText("��͸��");
        transparentColumn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                table.setSortColumn(transparentColumn);
                tableViewer.setSorter(new TableSorter());
            }
        });
        
        colorColumn = new TableColumn(table, SWT.NONE);
        colorColumn.setWidth(60);
        colorColumn.setText("��ɫ��");
        colorColumn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                table.setSortColumn(colorColumn);
                tableViewer.setSorter(new TableSorter());
            }
        });
        
        sizeColumn = new TableColumn(table, SWT.NONE);
        sizeColumn.setWidth(60);
        sizeColumn.setText("�ļ���С");
        sizeColumn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                table.setSortColumn(sizeColumn);
                tableViewer.setSorter(new TableSorter());
            }
        });

        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                StructuredSelection sel = (StructuredSelection)tableViewer.getSelection();
                if (sel.isEmpty()) {
                    previewer.setInput(null);
                    return;
                }
                File file = (File)sel.getFirstElement();
                PipImage image = images.get(file);
                previewer.setInput(image);
                previewer.refresh();
                
                boolean canMerge = true;
                boolean canUnMerge = true;
                Object[] selFiles = sel.toArray();
                for (Object o : selFiles) {
                    file = (File)o;
                    image = images.get(file);
                    
                    // ����ͼƬ��ǰģʽ�޸İ�ť
                    if (image.isTrueColor()) {
                        canMerge = false;
                        canUnMerge = false;
                    } else if (image.isMergeMode()) {
                        canMerge = false;
                    } else if (image.isSupportMoreColors()) {
                        canMerge = false;
                        canUnMerge = false;
                    } else {
                        canUnMerge = false;
                    }
                }
                if (canMerge) {
                    buttonMerge.setEnabled(true);
                    buttonMerge.setText("�ϲ�(&M)");
                } else if (canUnMerge) {
                    buttonMerge.setEnabled(true);
                    buttonMerge.setText("ȡ���ϲ�(&M)");
                } else {
                    buttonMerge.setEnabled(false);
                }
            }
        });
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)tableViewer.getSelection();
                if (sel.isEmpty()) {
                    previewer.setInput(null);
                    return;
                }
                File file = (File)sel.getFirstElement();
                IFileStore fileStore =  EFS.getLocalFileSystem().getStore(new Path((file.getAbsolutePath())));
                try {
                    IDE.openEditorOnFileStore(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), fileStore);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        tableViewer.setInput(new Object());

        buttonMerge = new Button(container, SWT.NONE);
        buttonMerge.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                StructuredSelection sel = (StructuredSelection)tableViewer.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                for (Object o : sel.toArray()) {
                    File file = (File)o;
                    PipImage image = images.get(file);
                    try {
                        if (image.isTrueColor()) {
                            continue;
                        } else if (image.isMergeMode()) {
                            image.setMergeMode(false);
                            image.save(file);
                            tableViewer.refresh(file);
                        } else if (image.isSupportMoreColors()) {
                            continue;
                        } else {
                            image.setMergeMode(true);
                            image.save(file);
                            tableViewer.refresh(file);
                        }
                    } catch (Exception e1) {
                        MessageDialog.openError(getShell(), "����", e1.toString());
                    }
                }
            }
        });
        final GridData gd_buttonMerge = new GridData(SWT.FILL, SWT.CENTER, true, false);
        buttonMerge.setLayoutData(gd_buttonMerge);
        buttonMerge.setText("�ϲ�(&M)");

        final Button buttonExport = new Button(container, SWT.NONE);
        buttonExport.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                StructuredSelection sel = (StructuredSelection)tableViewer.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                DirectoryDialog dlg = new DirectoryDialog(getShell());
                dlg.setText("ѡ��Ŀ¼");
                dlg.setMessage("��ѡ�񵼳�Ŀ¼��");
                String newPath = dlg.open();
                try {
                    for (Object o : sel.toArray()) {
                        File f = (File)o;
                        File nf = new File(newPath, f.getName());
                        EFSUtil.copyFile(f, nf);
                    }
                } catch (Exception e1) {
                    MessageDialog.openError(getShell(), "����", e1.toString());
                }
            }
        });
        final GridData gd_buttonExport = new GridData(SWT.FILL, SWT.CENTER, true, false);
        buttonExport.setLayoutData(gd_buttonExport);
        buttonExport.setText("����...");
        
        final Composite leftViewContainer = new Composite(container, SWT.NONE);
        leftViewContainer.setLayout(new FillLayout());
        final GridData gd_leftViewContainer = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        gd_leftViewContainer.heightHint = 400;
        leftViewContainer.setLayoutData(gd_leftViewContainer);

        previewer = new ImageViewer(leftViewContainer, SWT.NONE);
        previewer.setFlatMode(true);

        return container;
    }

    /**
     * Create contents of the button bar
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "�ر�", true);
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
        newShell.setText("����ͼƬģʽ");
    }
    
    protected void buttonPressed(int buttonId) {
        super.buttonPressed(buttonId);
    }
}
