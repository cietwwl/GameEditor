package com.pip.game.editor.property;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.pip.util.AutoSelectAll;

public class EditAntiBlockDialog extends Dialog {
    private Text text_4;
    private Text textHeight;
    private Text textWidth;
    private Text textY;
    private Text textX;
    
    private int x, y, w, h;
    
    public void setData(int[] data) {
        if (data != null) {
            x = data[0];
            y = data[1];
            w = data[2];
            h = data[3];
        }
    }
    
    public int[] getData() {
        return new int[] { x, y, w, h };
    }
    
    /**
     * Create the dialog
     * @param parentShell
     */
    public EditAntiBlockDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentContainer = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout();
        parentContainer.setLayout(gridLayout);
        
        Composite container = new Composite(parentContainer, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        container.setLayout(gridLayout);

        final Label label = new Label(container, SWT.NONE);
        label.setText("X��");

        textX = new Text(container, SWT.BORDER);
        final GridData gd_textX = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textX.setLayoutData(gd_textX);
        textX.addFocusListener(AutoSelectAll.instance);

        final Label label_1 = new Label(container, SWT.NONE);
        label_1.setText("Y��");

        textY = new Text(container, SWT.BORDER);
        final GridData gd_textY = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textY.setLayoutData(gd_textY);
        textY.addFocusListener(AutoSelectAll.instance);

        final Label label_2 = new Label(container, SWT.NONE);
        label_2.setText("��ȣ�");

        textWidth = new Text(container, SWT.BORDER);
        final GridData gd_textWidth = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textWidth.setLayoutData(gd_textWidth);
        textWidth.addFocusListener(AutoSelectAll.instance);

        final Label label_3 = new Label(container, SWT.NONE);
        label_3.setText("�߶ȣ�");

        textHeight = new Text(container, SWT.BORDER);
        final GridData gd_textHeight = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textHeight.setLayoutData(gd_textHeight);
        textHeight.addFocusListener(AutoSelectAll.instance);

        text_4 = new Text(container, SWT.WRAP | SWT.READ_ONLY);
        text_4.setText("�������ö������赲���Ϊ��λ����ͨ��ͼ8x8���Ŵ��ͼ16x16���������NPC����λ�á���Ȼ�߶�����Ϊ<=0��ֵ��ʾȡ��Ĩ���赲��");
        text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        final GridData gd_npcTemplateList = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_npcTemplateList.exclude = true;
        
        textX.setText(String.valueOf(x));
        textY.setText(String.valueOf(y));
        textWidth.setText(String.valueOf(w));
        textHeight.setText(String.valueOf(h));
        
        return container;
    }

    /**
     * Create contents of the button bar
     * @param parent
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "ȷ��", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "ȡ��", false);
    }

    /**
     * Return the initial size of the dialog
     */
    protected Point getInitialSize() {
        return new Point(504, 332);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("�༭Ĩ���赲����");
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            try {
                x = Integer.parseInt(textX.getText());
            } catch (Exception e) {
                MessageDialog.openError(getShell(), "����", "��������ȷ����ֵ��");
                return;
            }
            try {
                y = Integer.parseInt(textY.getText());
            } catch (Exception e) {
                MessageDialog.openError(getShell(), "����", "��������ȷ����ֵ��");
                return;
            }
            try {
                w = Integer.parseInt(textWidth.getText());
            } catch (Exception e) {
                MessageDialog.openError(getShell(), "����", "��������ȷ����ֵ��");
                return;
            }
            try {
                h = Integer.parseInt(textHeight.getText());
            } catch (Exception e) {
                MessageDialog.openError(getShell(), "����", "��������ȷ����ֵ��");
                return;
            }
        }
        super.buttonPressed(buttonId);
    }
}
