package com.pip.game.editor.quest;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.ExpressionList;

public class ExpressionDialog extends Dialog {
    private QuestDesigner questDesigner;
    private String expression;
    private QuestInfo questInfo;
    private int mode = 1;
    private int contextMask;
    
    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * Create the dialog
     * @param parentShell
     */
    public ExpressionDialog(Shell parentShell, QuestInfo qinfo, int contextMask) {
        super(parentShell);
        questInfo = qinfo;
        this.contextMask = contextMask;
    }
    
    public ExpressionDialog(Shell parentShell, QuestInfo qinfo, int mode, int contextMask) {
        super(parentShell);
        questInfo = qinfo;
        this.mode = mode;
        this.contextMask = contextMask;
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        final GridLayout gridLayout = new GridLayout();
        container.setLayout(gridLayout);

        questDesigner = new QuestDesigner(container, SWT.NONE, questInfo, contextMask);
        questDesigner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        questDesigner.setup(mode, expression);
        
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
        return new Point(1004, 633);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("�����༭��");
    }

    
    protected void buttonPressed(int buttonId) {
        if(mode == 0) {
            questDesigner.saveQuest();
        }
        if (buttonId == IDialogConstants.OK_ID) {
            if(mode == 0) {
            } else {
                expression = questDesigner.saveCondition();                
            }
        }
        super.buttonPressed(buttonId);
    }
    
    //mode == 0�� ������Ӷ���
    public static String open(Shell parentShell, QuestInfo qinfo, int contextMask) {
        ExpressionDialog dlg = new ExpressionDialog(parentShell, qinfo, 0, contextMask);
        if (dlg.open() == OK) {
            if(dlg.mode == 0) {
                return qinfo.getOneLineString();               
            } else {
                return dlg.getExpression(); 
            }
        } else {
            return null;
        }
    }
    
    //mode == 1��ֻ�����������
    public static String open(Shell parentShell, String value, QuestInfo qinfo, int contextMask) {
        ExpressionDialog dlg = new ExpressionDialog(parentShell, qinfo, contextMask);
        dlg.setExpression(value);
        if (dlg.open() == OK) {
            return dlg.getExpression();
        } else {
            return null;
        }
    }
    
    public static String open(Shell parentShell, String value, QuestInfo qinfo, int mode, int contextMask) {
        ExpressionDialog dlg = new ExpressionDialog(parentShell, qinfo, mode, contextMask);
        dlg.setExpression(value);
        if (dlg.open() == OK) {
            return dlg.getExpression();
        } else {
            return null;
        }
    }
    
}
