package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.forbid.ForbidSkill;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.editor.EditorApplication;

/**
 * Ìõ¼þ±à¼­Æ÷
 */
public class AILogicPropertyDescriptor extends PropertyDescriptor {
    private QuestInfo questInfo;
    private String title;
    private int contextMask;
    
    public AILogicPropertyDescriptor(Object id, String displayName, QuestInfo questInfo, int contextMask) {
        super(id, displayName);
        this.questInfo = questInfo;
        this.title = displayName;
        this.contextMask = contextMask;
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new AILogicCellEditor(parent, title, questInfo, contextMask);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new ConditionLabelProvider();
    }

    /**
     * @author ybai
     */
    public static class ConditionLabelProvider extends LabelProvider {
        public String getText(Object element) {
            if(element instanceof String) {
                return (String)element;
            } else if(element instanceof QuestInfo) {
                return ((QuestInfo)element).getOneLineString();
            }
            return "";
        }
    }
}
