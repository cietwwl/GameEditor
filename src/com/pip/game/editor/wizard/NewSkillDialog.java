package com.pip.game.editor.wizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.skill.SkillConfig;
import com.pip.util.AutoSelectAll;

public class NewSkillDialog extends Dialog {
    
    private Combo comboType;
    private Text textName;
    
    private int type;
    private String name;
    
    /**
     * Create the dialog
     * @param parentShell
     */
    public NewSkillDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        final GridLayout gridLayout = new GridLayout();
        container.setLayout(gridLayout);

        final Label label = new Label(container, SWT.NONE);
        label.setText("�������ƣ�");

        textName = new Text(container, SWT.BORDER);
        textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textName.setText("�¼���");
        textName.addFocusListener(AutoSelectAll.instance);

        final Label label_1 = new Label(container, SWT.NONE);
        label_1.setText("�������ͣ�");

        comboType = new Combo(container, SWT.READ_ONLY);
        comboType.setItems(new String[] {"������������", "������������", "��������", "�⻷����", "�����"});
        comboType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboType.select(0);
        
        return container;
    }
    
    public String getName(){
        return name;
    }
    
    public int getType(){
        return type;
    }
    
    /**
     * Create contents of the button bar
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "ȷ��", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "ȡ��", false);
    }
     
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("�½�����");
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            name = textName.getText().trim();
            type = comboType.getSelectionIndex();
            if (name.length() == 0) {
                MessageDialog.openError(getShell(), "����", "���Ʋ���Ϊ�ա�");
                return;
            }
        }
        super.buttonPressed(buttonId);
    }
}
