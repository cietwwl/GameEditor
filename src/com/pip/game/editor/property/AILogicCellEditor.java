package com.pip.game.editor.property;

import org.eclipse.swt.widgets.Composite;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.editor.quest.ExpressionDialog;

/**
 * Ìõ¼þ±à¼­Æ÷
 */
public class AILogicCellEditor extends CellEditorAdapter {
    protected QuestInfo questInfo;
    String title;
    protected int contextMask;

    public AILogicCellEditor(Composite parent, String title, QuestInfo questInfo, int contextMask) {
        super(parent);
        
        this.questInfo = questInfo;
        this.title = title;
        this.contextMask = contextMask;
    }

    /**
     * The <code>TextCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method returns
     * the text string.
     *
     * @return the text string
     */
    protected Object doGetValue() {
        return questInfo.getOneLineString();
    }

    /**
     * The <code>TextCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method accepts
     * a text string (type <code>String</code>).
     *
     * @param value a text string (type <code>String</code>)
     */
    protected void doSetValue(Object value) {
        text.setText(questInfo.getOneLineString());
    }
    
    protected void editText() {
        QuestInfo newInfo = new QuestInfo(questInfo.owner);
        try {
            newInfo.loadFromXML(questInfo.saveToXML());
        } catch (Exception e) {
        }
        String newExpr = ExpressionDialog.open(text.getShell(), newInfo, contextMask);
        if (newExpr != null) {
            text.setText(newExpr);
            try {
                questInfo.loadFromXML(newInfo.saveToXML());
            } catch (Exception e) {
            }
            fireApplyEditorValue();
            deactivate();
        }
    }
}
