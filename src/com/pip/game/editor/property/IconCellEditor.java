package com.pip.game.editor.property;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.pip.game.editor.skill.ChooseSkillIconDialog;
import com.pipimage.image.PipImage;

/**
 * ͼ��ѡ��<br>
 */
public class IconCellEditor extends CellEditor {
    protected Composite container;
    protected Button button;
    
    private int iconIndex = -1;
    private Image currentIcon;
    private PipImage[] pipImage;
    
    private Text textIconIndex;

    public IconCellEditor(Composite parent,PipImage[] pipImage) {
        super(parent, SWT.NONE);
        this.pipImage = pipImage;
    }

    protected Control createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        
        GridLayout gd = new GridLayout(2, false);
        gd.horizontalSpacing = gd.verticalSpacing = 0;
        gd.marginWidth = gd.marginHeight = 0;
        container.setLayout(gd);
        
        textIconIndex = new Text(container, SWT.SINGLE);
        textIconIndex.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        textIconIndex.setEditable(false);
        textIconIndex.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE
                        || e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                }
            }
        });
        textIconIndex.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                IconCellEditor.this.focusLost();
            }
        });
        textIconIndex.setFont(parent.getFont());
        textIconIndex.setBackground(parent.getBackground());
        textIconIndex.setText("");//$NON-NLS-1$
        
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
                IconCellEditor.this.focusLost();
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
        return iconIndex;
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
        Assert.isTrue(textIconIndex != null && (value instanceof Integer));
        textIconIndex.setText(String.valueOf(value));
        iconIndex = ((Integer)value).intValue();
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
        if (textIconIndex == null || textIconIndex.isDisposed()) {
            return false;
        }
        return textIconIndex.getSelectionCount() > 0;
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
        if (textIconIndex == null || textIconIndex.isDisposed()) {
            return false;
        }
        return textIconIndex.getCharCount() > 0;
    }

    /**
     * The <code>TextCellEditor</code> implementation of this
     * <code>CellEditor</code> method copies the
     * current selection to the clipboard. 
     */
    public void performCopy() {
        textIconIndex.copy();
    }

    /**
     * The <code>TextCellEditor</code> implementation of this
     * <code>CellEditor</code> method selects all of the
     * current text. 
     */
    public void performSelectAll() {
        textIconIndex.selectAll();
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
        return getClass() != IconCellEditor.class;
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
        if (!textIconIndex.isFocusControl() && !button.isFocusControl()) {
            if (isActivated()) {
                fireApplyEditorValue();
                deactivate();
            }
        }
    }
    
    protected void editText() {
        ChooseSkillIconDialog iconChoose = new ChooseSkillIconDialog(Display.getCurrent().getActiveShell(), pipImage);
        if(iconChoose.open() == IDialogConstants.OK_ID){
            int frameIndex = iconChoose.getSelectedIconIndex();
            setIcon(frameIndex);
            fireApplyEditorValue();
            deactivate();
        }
    }
    
    public void setIcon(int index){
        iconIndex = index;
        if (currentIcon != null) {
            currentIcon.dispose();
            currentIcon = null;
        }
        textIconIndex.setText("����:"+index);
    }
}
