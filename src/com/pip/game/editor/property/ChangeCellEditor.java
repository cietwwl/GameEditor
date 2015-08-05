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

import com.pip.game.data.ProjectData;
import com.pip.game.data.equipment.AttributeCalculator;
  


public class ChangeCellEditor extends CellEditor {
//    protected String[] items=new String[]{"是111111111","否111111111"};
//    protected String[] values=new String[]{"是22222","否222222222"};
    protected String selection;
    protected Combo comboBox;
    private String equipElement[];
    /**
     * 构造方法。
     * @param parent
     * @param titles 标题列表
     * @param values 值列表
     */
    public ChangeCellEditor(Composite parent) {
        super(parent, SWT.NONE);
        
       
    }
    
//    /**
//     * 设置列表框选项。
//     */
//    public void setItems(String[] items) {
//        Assert.isNotNull(items);
//        this.items = items;
//        populateComboBoxItems();
//    }

    /**
     * 创建编辑控件。
     */
    protected Control createControl(Composite parent) {
        AttributeCalculator ac = ProjectData.getActiveProject().config.attrCalc;
        equipElement=new String[ac.ATTRIBUTES.length];
        for(int i=0;i<ac.ATTRIBUTES.length;i++){
            this.equipElement[i]=ac.ATTRIBUTES[i].name;
        }
//        equipElement[ac.ATTRIBUTES.length] = "怒气";
//        equipElement[ac.ATTRIBUTES.length + 1] = "治疗量";
        comboBox = new Combo(parent, SWT.NONE);
        comboBox.setFont(parent.getFont());
//        comboBox.setEditable(false);
        comboBox.setVisibleItemCount(40);
       
        comboBox.setItems(equipElement);

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
                ChangeCellEditor.this.focusLost();
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
