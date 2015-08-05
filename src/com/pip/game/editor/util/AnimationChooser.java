package com.pip.game.editor.util;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.Animation;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.DefaultDataObjectEditor;
import com.pip.game.editor.property.ChooseAnimationDialog;

public class AnimationChooser extends Composite {
    
    private Animation selectedObject;
    private DefaultDataObjectEditor listener;
    
    private Text textText;
    
    private ModifyListener modifyListener;
    
    public AnimationChooser(Composite parent, int style) {
        super(parent, SWT.NONE);
        
        final GridLayout gridLayout = new GridLayout();
        gridLayout.horizontalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.numColumns = 3;
        gridLayout.marginHeight = 0;
        setLayout(gridLayout);
        
        textText = new Text(this, SWT.BORDER);
        textText.setEditable(false);
        final GridData gd_textText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textText.setLayoutData(gd_textText);
        textText.addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent e) {
                if (selectedObject != null) {
                    DataListView.tryEditObject(selectedObject);
                }
            }
        });
        
        final Button chooseButton = new Button(this, SWT.NONE);
        chooseButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        chooseButton.setText("...");
        chooseButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent e) {
                onChooseAnimation();
            }
        });
    }
    
    public void addModifyListener(ModifyListener l) {
        modifyListener = l;
    }
    
    public void setHandler(DefaultDataObjectEditor handle) {
        this.listener = handle;
    }
    
    public Animation getSelectedObject() {
        return selectedObject;
    }
    
    public void setSelectedObject(Animation ani) {
        selectedObject = ani;
        if (selectedObject == null) {
            textText.setText("未指定");
        } else {
            textText.setText(ani.toString());
        }
    }
    
    /**
     * 调出图标选择对话框
     */
    private void onChooseAnimation(){
        ChooseAnimationDialog dlg = new ChooseAnimationDialog(getShell());
        if (selectedObject != null) {
            dlg.setSelectedAnimation(selectedObject.id);
        }
        if (dlg.open() == Dialog.OK) {
            setSelectedObject(dlg.getSelectedAnimation());
            listener.setDirty(true);
            if (modifyListener != null) {
                modifyListener.modifyText(null);
            }
        }
    }
}
