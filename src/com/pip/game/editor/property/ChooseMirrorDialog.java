package com.pip.game.editor.property;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import com.pip.game.data.Animation;

/**
 * 相位选择对话框。相位设置是一个long，对应64个相位。
 * @author lighthu
 */
public class ChooseMirrorDialog extends Dialog {
    class ListLabelProvider extends LabelProvider {
        public String getText(Object element) {
            int index = ((Integer)element).intValue();
            return mirrorNames[index];
        }
        public Image getImage(Object element) {
            return null;
        }
    }
    class ContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            Object[] ret = new Object[64];
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
    
    private List list;
    private String[] mirrorNames;
    private long mirrorSet = 0xFF;
    private ListViewer listViewer;


    public long getMirrorSet() {
        return mirrorSet;
    }

    /**
     * Create the dialog
     * 
     * @param parentShell
     */
    public ChooseMirrorDialog(Shell parentShell, String[] names, long initValue) {
        super(parentShell);
        mirrorNames = names;
        mirrorSet = initValue;
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
        parentContainer.setLayout(gridLayout);

        listViewer = new ListViewer(parentContainer, SWT.BORDER | SWT.MULTI);
        listViewer.setLabelProvider(new ListLabelProvider());
        listViewer.setContentProvider(new ContentProvider());
        list = listViewer.getList();
        list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        listViewer.setInput(new Object());
        
        ArrayList<Integer> selInds = new ArrayList<Integer>();
        for (int i = 0; i < 64; i++) {
            long mask = 1L << i;
            if ((mirrorSet & mask) != 0) {
                selInds.add(new Integer(i));
            }
        }
        StructuredSelection sel = new StructuredSelection(selInds);
        listViewer.setSelection(sel);

        return parentContainer;
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
        return new Point(527, 675);
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("选择相位");
    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            StructuredSelection sel = (StructuredSelection)listViewer.getSelection();
            Object[] arr = sel.toArray();
            long mask = 0L;
            for (int i = 0; i < arr.length; i++) {
                int index = ((Integer)arr[i]).intValue();
                mask |= 1L << index;
            }
            mirrorSet = mask;
        }
        super.buttonPressed(buttonId);
    }
}
