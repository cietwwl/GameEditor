package com.pip.game.editor.property;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
  


public class StringCellEditor extends CellEditor {
    protected String selection;
    protected Combo comboBox;
    private String[] strLabels = new String[0];
    /**
     * 构造方法。
     * @param parent
     * @param titles 标题列表
     * @param values 值列表
     */
    public StringCellEditor(Composite parent) {
        super(parent, SWT.NONE);
    }
    
    public void setLabels(String[] strs){
        strLabels = new String[strs.length];
        System.arraycopy(strs, 0, strLabels, 0, strs.length);
        comboBox.setItems(strLabels);
    }

    /**
     * 创建编辑控件。
     */
    protected Control createControl(Composite parent) {
        comboBox = new Combo(parent, SWT.NONE);
        comboBox.setFont(parent.getFont());
        comboBox.setVisibleItemCount(40);
       
        if(strLabels != null){
            comboBox.setItems(strLabels);
        }

        comboBox.addKeyListener(new KeyAdapter() {
            // hook key pressed - see PR 14201
            public void keyPressed(KeyEvent e) {
                keyReleaseOccured(e);
            }
        });

        comboBox.addSelectionListener(new SelectionAdapter() {
            public void widgetDefaultSelected(SelectionEvent event) {
                applyEditorValueAndDeactivate();
            }


        });

        comboBox.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE
                        || e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                }
            }
        });

        comboBox.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                StringCellEditor.this.focusLost();
            }
        });
        return comboBox;
    }


    /**
     * 取得编辑器修改后的当前值。
     */
    protected Object doGetValue() {
        return selection;
    }

    /**
     * 焦点获得事件。
     */
    protected void doSetFocus() {
        comboBox.setFocus();
    }

    /**
     * The <code>ComboBoxCellEditor</code> implementation of this
     * <code>CellEditor</code> framework method sets the minimum width of the
     * cell. The minimum width is 10 characters if <code>comboBox</code> is
     * not <code>null</code> or <code>disposed</code> else it is 60 pixels
     * to make sure the arrow button and some text is visible. The list of
     * CCombo will be wide enough to show its longest item.
     */
    public LayoutData getLayoutData() {
        LayoutData layoutData = super.getLayoutData();
        if ((comboBox == null) || comboBox.isDisposed()) {
            layoutData.minimumWidth = 60;
        } else {
            // make the comboBox 10 characters wide
            GC gc = new GC(comboBox);
            layoutData.minimumWidth = (gc.getFontMetrics()
                    .getAverageCharWidth() * 10) + 10;
            gc.dispose();
        }
        return layoutData;
    }

    /**
     * 设置当前值。
     */
    protected void doSetValue(Object value) {
        Assert.isTrue(comboBox != null && (value instanceof String));
        selection = (String)value;
        comboBox.setText(selection);

    }

    /**
     * Updates the list of choices for the combo box for the current control.
     */


    /**
     * Applies the currently selected value and deactivates the cell editor
     */
    void applyEditorValueAndDeactivate() {
        // must set the selection before getting value
        selection = comboBox.getText();
        int sel = -1;

        Object newValue = doGetValue();
        markDirty();
        boolean isValid = isCorrect(newValue);
        setValueValid(isValid);



        fireApplyEditorValue();
        deactivate();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.CellEditor#focusLost()
     */
    protected void focusLost() {
        if (isActivated()) {
            applyEditorValueAndDeactivate();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.CellEditor#keyReleaseOccured(org.eclipse.swt.events.KeyEvent)
     */
    protected void keyReleaseOccured(KeyEvent keyEvent) {
        if (keyEvent.character == '\u001b') { // Escape character
            fireCancelEditor();
        } else if (keyEvent.character == '\t') { // tab key
            applyEditorValueAndDeactivate();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.CellEditor#activate(org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent)
     */
    public void activate(ColumnViewerEditorActivationEvent activationEvent) {
        super.activate(activationEvent);
        getControl().getDisplay().asyncExec(new Runnable() {

            public void run() {
                ((CCombo) getControl()).setListVisible(true);
            }

        });
    }
    public void load(){
        
    }
}
