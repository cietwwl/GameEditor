package com.pip.game.editor.skill;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.DataObject;
import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectData;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.util.AnimatePreviewer;
import com.pip.image.workshop.editor.ImageViewer;
import com.pip.image.workshop.editor.ImageViewerListener;
import com.pipimage.image.PipImage;

public class ChooseSkillIconDialog extends Dialog implements ImageViewerListener {
	protected int selectedIconIndex = -1;
    protected ImageViewer[] previewers;
    protected PipImage[] pimg;
	
    public int getSelectedIconIndex() {
        return selectedIconIndex;
    }

    public void setSelectedTemplate(int t) {
        this.selectedIconIndex = t;
    }
    
    /**
     * Create the dialog
     * @param parentShell
     */
    public ChooseSkillIconDialog(Shell parentShell, PipImage[] pimg) {
        super(parentShell);
        this.pimg = pimg;
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
        
        initPreviewers(container);
        
        return container;
    }

    /**
     * 初始化图片预览控件
     */
    protected void initPreviewers(Composite container){
    	previewers = new ImageViewer[pimg.length];
        for (int i = 0; i < previewers.length; i++) {
            previewers[i] = new ImageViewer(container, SWT.NONE);
            previewers[i].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            previewers[i].setInput(pimg[i]);
            if (selectedIconIndex >= i * 1000 && selectedIconIndex < i * 1000 + 1000) {
                previewers[i].setSelectedFrame(selectedIconIndex);
            }
            previewers[i].zoomin();
            previewers[i].setImageViewerListener(this);
        }
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
        newShell.setText("选择图标");
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
                previewers[i].setSelectedFrame(-1);
            }
        }
        if (focusFile == -1) {
            selectedIconIndex = -1;
        } else {
            selectedIconIndex = focusFile * 1000 + newFrame;
        }
    }
    
    public void frameDoubleClicked(Object source, int frame) {
    }
    
    public void contentChanged(Object source) {
    }
}
