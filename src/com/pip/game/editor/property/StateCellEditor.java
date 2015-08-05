package com.pip.game.editor.property;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * ×´Ì¬Ñ¡Ôñ
 * 
 * @author yqwang
 */
public class StateCellEditor extends CellEditor {
    static String[] stateValue;
    static String[] stateName;
    int selection;
    CCombo comboBox;
    int activationStyle = SWT.NONE;
    public static final int DROP_DOWN_ON_MOUSE_ACTIVATION = 1;
    public static final int DROP_DOWN_ON_KEY_ACTIVATION = 1 << 1;
    public static final int DROP_DOWN_ON_PROGRAMMATIC_ACTIVATION = 1 << 2;
    public static final int DROP_DOWN_ON_TRAVERSE_ACTIVATION = 1 << 3;
    
    static {
        initDate();
    }

    public StateCellEditor(Composite parent) {
        super(parent, SWT.NONE);
    }
    
    private static void initDate() {
        stateValue = new String[]{"6", "7", "8", "11"};
        stateName = new String[]{"¿Ö¾å", "Âé±Ô", "¶¨Éí", "³ÁÄ¬"};
    }
    
//    public static final String getValue(String title) {
//        for (int i = 0; i < stateName.length; i++) {
//            if (stateName[i].endsWith(title)) {
//                return stateValue[i];
//            }
//        }
//        return "0";
//    }

    protected Control createControl(Composite parent) {
        comboBox = new CCombo(parent, getStyle());
        comboBox.setFont(parent.getFont());
        comboBox.setEditable(false);
        comboBox.setVisibleItemCount(20);
        populateComboBoxItems();

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

            public void widgetSelected(SelectionEvent event) {
                selection = comboBox.getSelectionIndex();
            }
        });

        comboBox.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                }
            }
        });

        comboBox.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                StateCellEditor.this.focusLost();
            }
        });
        return comboBox;
    }

    protected Object doGetValue() {
        if (selection < 0 || selection >= stateValue.length) {
            return new Integer(0);
        }
        else {
            return Integer.parseInt(stateValue[selection]);
        }
    }

    protected void doSetFocus() {
        comboBox.setFocus();
    }

    public LayoutData getLayoutData() {
        LayoutData layoutData = super.getLayoutData();
        if ((comboBox == null) || comboBox.isDisposed()) {
            layoutData.minimumWidth = 60;
        }
        else {
            // make the comboBox 10 characters wide
            GC gc = new GC(comboBox);
            layoutData.minimumWidth = (gc.getFontMetrics().getAverageCharWidth() * 10) + 10;
            gc.dispose();
        }
        return layoutData;
    }

    protected void doSetValue(Object value) {
        selection = 0;
        for (int i = 0; i < stateName.length; i++) {
            if (stateValue[i].equals(value)) {
                selection = i;
                break;
            }
        }
        comboBox.setText(stateName[selection]);
        comboBox.select(selection);
    }

    private void populateComboBoxItems() {
        if (comboBox != null) {
            comboBox.removeAll();
            for (int i = 0; i < stateName.length; i++) {
                comboBox.add(stateName[i], i);
            }
            setValueValid(true);
            selection = 0;
        }
    }

    void applyEditorValueAndDeactivate() {
        // must set the selection before getting value
        selection = comboBox.getSelectionIndex();
        Object newValue = doGetValue();
        markDirty();
        boolean isValid = isCorrect(newValue);
        setValueValid(isValid);

        if (!isValid) {
            // Only format if the 'index' is valid
            if (stateValue.length > 0 && selection >= 0 && selection < stateValue.length) {
                // try to insert the current value into the error message.
                setErrorMessage(MessageFormat.format(getErrorMessage(), new Object[] { stateName }));
            }
            else {
                // Since we don't have a valid index, assume we're using an
                // 'edit'
                // combo so format using its text value
                setErrorMessage(MessageFormat.format(getErrorMessage(), new Object[] { comboBox.getText() }));
            }
        }

        fireApplyEditorValue();
        deactivate();
    }

    protected void focusLost() {
        if (isActivated()) {
            applyEditorValueAndDeactivate();
        }
    }

    protected void keyReleaseOccured(KeyEvent keyEvent) {
        if (keyEvent.character == '\u001b') { // Escape character
            fireCancelEditor();
        }
        else if (keyEvent.character == '\t') { // tab key
            applyEditorValueAndDeactivate();
        }
    }

    public void activate(ColumnViewerEditorActivationEvent activationEvent) {
        super.activate(activationEvent);
        if (activationStyle != SWT.NONE) {
            boolean dropDown = false;
            if ((activationEvent.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION || activationEvent.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION)
                    && (activationStyle & DROP_DOWN_ON_MOUSE_ACTIVATION) != 0) {
                dropDown = true;
            }
            else if (activationEvent.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED
                    && (activationStyle & DROP_DOWN_ON_KEY_ACTIVATION) != 0) {
                dropDown = true;
            }
            else if (activationEvent.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC
                    && (activationStyle & DROP_DOWN_ON_PROGRAMMATIC_ACTIVATION) != 0) {
                dropDown = true;
            }
            else if (activationEvent.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
                    && (activationStyle & DROP_DOWN_ON_TRAVERSE_ACTIVATION) != 0) {
                dropDown = true;
            }

            if (dropDown) {
                getControl().getDisplay().asyncExec(new Runnable() {

                    public void run() {
                        ((CCombo) getControl()).setListVisible(true);
                    }

                });

            }
        }
    }

    public void setActivationStyle(int activationStyle) {
        this.activationStyle = activationStyle;
    }
}
