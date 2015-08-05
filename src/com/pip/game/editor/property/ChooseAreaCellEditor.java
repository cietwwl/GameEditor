package com.pip.game.editor.property;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellEditor.LayoutData;
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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapObject;

public class ChooseAreaCellEditor extends CellEditorAdapter {
    protected int[] location;
 
    public ChooseAreaCellEditor(Composite parent) {
        super(parent);
    }

    /**
     * The <code>TextCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method returns
     * the text string.
     *
     * @return the text string
     */
    protected Object doGetValue() {
        return new int[] { location[0], location[1], location[2] };
    }
    
    /**
     * The <code>TextCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method accepts
     * a text string (type <code>String</code>).
     *
     * @param value a text string (type <code>String</code>)
     */
    protected void doSetValue(Object value) {
        Assert.isTrue(text != null && (value instanceof int[]));
        location = (int[])value;
        text.setText(GameMapInfo.locationToString(ProjectData.getActiveProject(), location, false));
    }

    protected void editText() {
        ChooseLocationDialog dlg = new ChooseLocationDialog(text.getShell());
        dlg.setLocation(location);
        if (dlg.open() == Dialog.OK) {
            location = dlg.getLocation();
            
            text.setText(GameMapInfo.locationToString(ProjectData.getActiveProject(), location, false));
            fireApplyEditorValue();
            deactivate();
        }
    }
}
