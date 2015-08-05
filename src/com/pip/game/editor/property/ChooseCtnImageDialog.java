package com.pip.game.editor.property;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.pip.image.workshop.editor.AnimateSelector;
import com.pip.image.workshop.editor.ImageViewerListener;
import com.pipimage.image.PipAnimateSet;

public class ChooseCtnImageDialog extends Dialog implements ImageViewerListener {
    private int selectedAnimateIndex = -1;
    private AnimateSelector[] previewers;
    private PipAnimateSet[] animageSet;
    
    public int getSelectedIconIndex() {
        return selectedAnimateIndex;
    }

    public void setSelectedTemplate(int t) {
        this.selectedAnimateIndex = t;
    }
    
    /**
     * Create the dialog
     * @param parentShell
     */
    public ChooseCtnImageDialog(Shell parentShell, PipAnimateSet[] pimg) {
        super(parentShell);
        this.animageSet = pimg;
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.horizontalSpacing = 0;
        container.setLayout(gridLayout);

        previewers = new AnimateSelector[animageSet.length];
        for (int i = 0; i < previewers.length; i++) {
            previewers[i] = new AnimateSelector(container, SWT.NONE);
            previewers[i].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            previewers[i].setInput(animageSet[i]);
            if (selectedAnimateIndex >= i * 1000 && selectedAnimateIndex < i * 1000 + 1000) {
                previewers[i].setSelectedIndex(selectedAnimateIndex);
            }
            previewers[i].zoomin();
            previewers[i].setImageViewerListener(this);
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
        return new Point(720, 644);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("动画选择");
    }
    
    protected void buttonPressed(int buttonId) {
        super.buttonPressed(buttonId);
    }
    
    public void areaSelected(Object source) {
    }
    
    public void frameSelectionChanged(Object source, int newFrame) {
        int focusFile = -1;
        for (int i = 0; i < previewers.length; i++) {
            if (source == previewers[i]) {
                focusFile = i;
            } else {
                previewers[i].setSelectedIndex(-1);
            }
        }
        if (focusFile == -1) {
            selectedAnimateIndex = -1;
        } else {
            selectedAnimateIndex = focusFile * 1000 + newFrame;
        }
    }
    
    public void frameDoubleClicked(Object source, int frame) {
    }
    
    public void contentChanged(Object source) {
    }
}
