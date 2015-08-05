package com.pip.game.editor.property;

import java.text.MessageFormat; // Not using ICU to support standalone JFace scenario

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.NPCTemplate;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.editor.EditorApplication;

/**
 * 地点编辑器。
 */
public class PatrolPathCellEditor extends CellEditorAdapter {
    protected int patrolPathId;
    protected GameMapNPC mapNPC;

    public PatrolPathCellEditor(Composite parent, GameMapNPC mapNPC) {
        super(parent);
        this.mapNPC = mapNPC;
    }

    /**
     * The <code>TextCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method returns
     * the text string.
     *
     * @return the text string
     */
    protected Object doGetValue() {
        return new Integer(patrolPathId);
    }

    /**
     * The <code>TextCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method accepts
     * a text string (type <code>String</code>).
     *
     * @param value a text string (type <code>String</code>)
     */
    protected void doSetValue(Object value) {
        Assert.isTrue(text != null && (value instanceof String));
        if("未指定".equals(value)) {
            patrolPathId = -1;
        } else {
            String tmp = (String)value;
            patrolPathId = Integer.parseInt(tmp.substring(0, tmp.indexOf('(')));
        }
        text.setText((String)value);
    }
    
    protected void editText() {
        ChoosePatrolPathDialog dlg = new ChoosePatrolPathDialog(text.getShell());
        dlg.setPatrolPathId(patrolPathId, mapNPC);
        if (dlg.open() == Dialog.OK) {
            patrolPathId = dlg.getPatrolPathId();
            text.setText(String.valueOf(patrolPathId));
            fireApplyEditorValue();
            deactivate();
        }
    }
}
