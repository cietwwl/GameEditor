package com.pip.game.editor.property;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.AI.AIData;

public class AICellEditor extends CellEditor{

    protected Composite container;
    protected Text text;
    protected Button button;
    protected AIData aiData;
    protected int aiId;
    protected byte type;
    
    public AICellEditor(Composite parent) {
        super(parent, SWT.NONE);
    }
    
    @Override
    protected Control createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout gd = new GridLayout(2, false);
        gd.horizontalSpacing = gd.verticalSpacing = 0;
        gd.marginWidth = gd.marginHeight = 0;
        container.setLayout(gd);
        
        text = new Text(container, SWT.SINGLE);
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        text.setEditable(false);
        text.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE
                        || e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                }
            }
        });
        text.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                AICellEditor.this.focusLost();
            }
        });
        text.setFont(parent.getFont());
        text.setBackground(parent.getBackground());
        text.setText("");//$NON-NLS-1$
        
        button = new Button(container, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        button.setText("...");
        button.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE) {
                    e.doit = false;
                }
            }
        });
        button.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                AICellEditor.this.focusLost();
            }
        });
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                editText();
            }
        });
        
        return container;
    }

    @Override
    protected Object doGetValue() {
        return new Integer(aiId);
    }

    @Override
    protected void doSetFocus() {
        if (button != null) {
            button.setFocus();
        }       
    }

    @Override
    protected void doSetValue(Object value) {
        Assert.isTrue(text != null && (value instanceof Integer));
        aiId = ((Integer)value).intValue();
        text.setText(Integer.toString(aiId));      
    }
    
    protected void editText() {
        ChooseAIDialog dlg = new ChooseAIDialog(text.getShell());
        dlg.setSelAi(1);
        
        if (dlg.open() == Dialog.OK) {
            aiData = dlg.getSelectedAIData();
            aiId = aiData.id;
            text.setText(Integer.toString(aiId));
            fireApplyEditorValue();
            deactivate();
        }
    }

}
