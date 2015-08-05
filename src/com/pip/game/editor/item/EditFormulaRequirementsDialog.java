package com.pip.game.editor.item;

import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.pip.game.data.Shop;
import com.pip.game.data.item.Formula;
import com.pip.game.editor.shop.BuyRequirementEditor;

public class EditFormulaRequirementsDialog extends Dialog {
    private BuyRequirementEditor[] editors;
    private Shop.BuyRequirement[] editObjects;
    private Formula editItem;
    
    /**
     * Create the dialog
     * @param parentShell
     */
    public EditFormulaRequirementsDialog(Shell parentShell, Formula item) {
        super(parentShell);
        this.editItem = item;
        
        // �̶����10����������
        editObjects = new Shop.BuyRequirement[10];
        int index = 0;
        for (Shop.BuyRequirement req : item.requirements) {
            editObjects[index] = req.dup();
            index++;
        }
        while (index < 10) {
            editObjects[index] = new Shop.BuyRequirement();
            editObjects[index].type = Shop.TYPE_ITEM;
            index++;
        }
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout());

        editors = new BuyRequirementEditor[10];
        for (int i = 0; i < 10; i++) {
            editors[i] = new BuyRequirementEditor(container, SWT.NONE, editObjects[i]);
            editors[i].setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        }

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
        return new Point(600, 400);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("�༭��������");
    }

    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            // ɾ�����оɵ�����
            editItem.requirements.clear();
            
            // ���������
            for (int i = 0; i < 10; i++) {
                editors[i].save();
                if (editObjects[i].isValid()) {
                    editItem.requirements.add(editObjects[i].dup());
                }
            }
        }
        super.buttonPressed(buttonId);
    }
    
    public static boolean open(Shell parentShell, Formula item) {
        EditFormulaRequirementsDialog dlg = new EditFormulaRequirementsDialog(parentShell, item);
        if (dlg.open() == OK) {
            return true;
        } else {
            return false;
        }
    }
}
