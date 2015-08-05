package com.pip.game.editor.property;



import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class CheckBoxCellEditor extends CellEditor {
    protected Composite container;
    protected Button button;
    protected boolean switchButton;
    public CheckBoxCellEditor(Composite parent) {
        super(parent, SWT.NONE);
    }

    protected Control createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout gd = new GridLayout(1, false);
        gd.horizontalSpacing = gd.verticalSpacing = 0;
        gd.marginWidth = gd.marginHeight = 0;
        container.setLayout(gd);
        
        button = new Button(container, SWT.CHECK);
        button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        button.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE) {
                    e.doit = false;
                }
            }
        });
        return container;
    }

    /**
     * The <code>TextCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method returns
     * the text string.
     *
     * 
     */
    protected Object doGetValue() {
        return button.getSelection();
    }

    /* (non-Javadoc)
     * Method declared on CellEditor.
     */
    protected void doSetFocus() {
//        if (button != null) {
//            button.setFocus();
//        }
    }

    /**
     * The <code>TextCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method accepts
     * a text string (type <code>String</code>).
     *
     * 
     */
    protected void doSetValue(Object value) {
        Assert.isTrue(button != null && (value instanceof Boolean));
        button.setSelection(Boolean.valueOf((String.valueOf(value))));
    }


  
}
