package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.forbid.ForbidSkill;

/**
 * 收集一系列物品
 */
public class CollectSkillPropertyDescriptor extends PropertyDescriptor {
    private int mapId;
    
    public CollectSkillPropertyDescriptor(Object id, String displayName, int mapId) {
        super(id, displayName);
        this.mapId = mapId;
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new CollectSkillCellEditor(parent, mapId);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new CollectSkillLabelProvider();
    }

    /**
     * NPC名字显示，根据NPC ID查找NPC。
     * @author lighthu
     */
    public static class CollectSkillLabelProvider extends LabelProvider {
        public String getText(Object element) {
            int[] skillIds = (int[])element;
            
            if(skillIds == null || skillIds.length == 0) {
                return "";
            } else {
                StringBuffer sb = new StringBuffer();
                for(int i=0; i<skillIds.length; i++) {
                  //  ForbidSkill skill = (ForbidSkill)ProjectData.getActiveProject().findObject(ForbidSkill.class, skillIds[i]);
                      ForbidSkill skill=ProjectData.getActiveProject().findForbidSkill(skillIds[i]);
                    if(skill != null) {
                        sb.append(skill.getTitle());
                        sb.append(",");
                    }
                }
                return sb.toString();
            }
        }
    }
}
