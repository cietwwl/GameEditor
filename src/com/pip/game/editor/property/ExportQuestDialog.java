package com.pip.game.editor.property;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
/**
 * ����������������
 * @author ybai
 *
 */
public class ExportQuestDialog extends Dialog {
    private Text questNameKeyText; //�������ֹؼ���
    private Text EquipNameKeyText; //װ�����ֹؼ���

    private String questNameKey;
    private String EquipNameKey;
    
    /**
     * Create the dialog
     * @param parentShell
     */
    public ExportQuestDialog(Shell parentShell) {
        super(parentShell);
    }
    
    public String getQuestNameKey() {
        return questNameKey;
    }
    
    public String getEquipNameKey() {
        return EquipNameKey;
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

        final Label label2 = new Label(container, SWT.NONE);
        label2.setText("��Ʒ(װ��)���ؼ��֣�");
        EquipNameKeyText = new Text(container, SWT.BORDER);
        EquipNameKeyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        final Label label = new Label(container, SWT.NONE);
        label.setText("�������ؼ��֣�");
        questNameKeyText = new Text(container, SWT.BORDER);
        questNameKeyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        return container;
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

    /**
     * Return the initial size of the dialog
     */
    @Override
    protected Point getInitialSize() {
        return new Point(520, 644);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("��������������");
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            questNameKey = questNameKeyText.getText();
            EquipNameKey = EquipNameKeyText.getText();
        }
        super.buttonPressed(buttonId);
    }
}

