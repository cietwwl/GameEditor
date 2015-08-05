package com.pip.game.editor.property;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.editor.DataListView;

/**
 * NPCÄ£°å±à¼­Æ÷¡£
 */
public class NPCTemplateCellEditor extends CellEditor {
    protected Composite container;
    protected Text text;
    protected Button button;
    protected int templateID;
    

    
    public NPCTemplateCellEditor(Composite parent) {
        super(parent, SWT.NONE);
    }

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
                NPCTemplateCellEditor.this.focusLost();
            }
        });
        text.addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent e) {
                if (e.button == 1) {
                    NPCTemplate template = (NPCTemplate)ProjectData.getActiveProject().findObject(NPCTemplate.class, templateID);
                    if (template != null) {
                        DataListView.tryEditObject(template);
                    }
                }
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
                NPCTemplateCellEditor.this.focusLost();
            }
        });
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                editText();
            }
        });
        
        return container;
    }

    /**
     * The <code>TextCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method returns
     * the text string.
     *
     * @return the text string
     */
    protected Object doGetValue() {
        return new Integer(templateID);
    }

    /* (non-Javadoc)
     * Method declared on CellEditor.
     */
    protected void doSetFocus() {
        if (button != null) {
            button.setFocus();
        }
    }

    /**
     * The <code>TextCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method accepts
     * a text string (type <code>String</code>).
     *
     * @param value a text string (type <code>String</code>)
     */
    protected void doSetValue(Object value) {
        if(value instanceof Integer){
            Assert.isTrue(text != null && (value instanceof Integer));
            templateID = ((Integer)value).intValue();
            text.setText(NPCTemplate.toString(ProjectData.getActiveProject(), templateID));
        }else if(value instanceof String){
            Assert.isTrue(text != null && (value instanceof String));
            templateID = Integer.parseInt((String)value);
            text.setText(GameMapObject.toString(ProjectData.getActiveProject(), templateID)); 
        }
    }

    /**
     * Since a text editor field is scrollable we don't
     * set a minimumSize.
     */
    public LayoutData getLayoutData() {
        return new LayoutData();
    }

    /**
     * The <code>TextCellEditor</code>  implementation of this 
     * <code>CellEditor</code> method returns <code>true</code> if 
     * the current selection is not empty.
     */
    public boolean isCopyEnabled() {
        if (text == null || text.isDisposed()) {
            return false;
        }
        return text.getSelectionCount() > 0;
    }

    /**
     * Returns <code>true</code> if this cell editor is
     * able to perform the select all action.
     * <p>
     * This default implementation always returns 
     * <code>false</code>.
     * </p>
     * <p>
     * Subclasses may override
     * </p>
     * @return <code>true</code> if select all is possible,
     *  <code>false</code> otherwise
     */
    public boolean isSelectAllEnabled() {
        if (text == null || text.isDisposed()) {
            return false;
        }
        return text.getCharCount() > 0;
    }

    /**
     * The <code>TextCellEditor</code> implementation of this
     * <code>CellEditor</code> method copies the
     * current selection to the clipboard. 
     */
    public void performCopy() {
        text.copy();
    }

    /**
     * The <code>TextCellEditor</code> implementation of this
     * <code>CellEditor</code> method selects all of the
     * current text. 
     */
    public void performSelectAll() {
        text.selectAll();
    }

    /**
     * This implementation of
     * {@link CellEditor#dependsOnExternalFocusListener()} returns false if the
     * current instance's class is TextCellEditor, and true otherwise.
     * Subclasses that hook their own focus listener should override this method
     * and return false. See also bug 58777.
     * 
     * @since 3.4
     */
    protected boolean dependsOnExternalFocusListener() {
        return getClass() != NPCTemplateCellEditor.class;
    }

    /**
     * Processes a focus lost event that occurred in this cell editor.
     * <p>
     * The default implementation of this framework method applies the current
     * value and deactivates the cell editor. Subclasses should call this method
     * at appropriate times. Subclasses may also extend or reimplement.
     * </p>
     */
    protected void focusLost() {
        if (!text.isFocusControl() && !button.isFocusControl()) {
            if (isActivated()) {
                fireApplyEditorValue();
                deactivate();
            }
        }
    }
    
    protected void editText() {
        ChooseNPCTemplateDialog dlg = new ChooseNPCTemplateDialog(text.getShell(), ChooseNPCTemplateDialog.ONE_TYPE);
        dlg.setSelectedTemplate(templateID);
        if (dlg.open() == Dialog.OK) {
            templateID = dlg.getSelectedTemplate();
            text.setText(NPCTemplate.toString(ProjectData.getActiveProject(), templateID));
            fireApplyEditorValue();
            deactivate();
        }
    }
}
