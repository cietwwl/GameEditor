package com.pip.game.editor.property;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;

import com.pip.game.data.ProjectData;
import com.pip.game.data.forbid.ForbidSkill;
import com.pip.game.data.map.GameMapInfo;

/**
 * 选择禁用的技能编辑器
 */
public class CollectSkillCellEditor extends CellEditorAdapter {
    protected int mapID;

    public CollectSkillCellEditor(Composite parent, int mapId) {
        super(parent);
        
        mapID = mapId;
    }

    /**
     * The <code>TextCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method returns
     * the text string.
     *
     * @return the text string
     */
    protected Object doGetValue() {
//        return new Integer(mapID);
        GameMapInfo mi = GameMapInfo.findByID(ProjectData.getActiveProject(), mapID);
        return mi.forbitSkills;
    }

    /**
     * The <code>TextCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method accepts
     * a text string (type <code>String</code>).
     *
     * @param value a text string (type <code>String</code>)
     */
    protected void doSetValue(Object value) {
        if(value != null && value instanceof int[]) {
            GameMapInfo mi = GameMapInfo.findByID(ProjectData.getActiveProject(), mapID);
            mi.forbitSkills = (int[])value;
            if(mi.forbitSkills.length > 0) {
                StringBuffer sb = new StringBuffer();
                for(int i=0; i<mi.forbitSkills.length; i++) {
                    ForbidSkill skill = (ForbidSkill)ProjectData.getActiveProject().findObject(ForbidSkill.class, mi.forbitSkills[i]);
                    if(skill != null) {
                        sb.append(skill.getTitle());
                        sb.append(",");
                    }
                }
                text.setText(sb.toString());
            } else {
                text.setText("");
            }
        }

    }
    
    protected void editText() {
        CollectSkillDialog dlg = new CollectSkillDialog(text.getShell());
        
        GameMapInfo mi = GameMapInfo.findByID(ProjectData.getActiveProject(), mapID);
        dlg.setSelItems(mi.forbitSkills);
        
        if (dlg.open() == Dialog.OK) {
//            mapID = dlg.getSelectedNPC();
//            text.setText(GameMapObject.toString(ProjectData.getActiveProject(), mapID));            
            
            List<ForbidSkill> list = dlg.getSelectedItems();
            int count = list.size();
            mi.forbitSkills = new int[count];
            if(count > 0) {
                StringBuffer sb = new StringBuffer();
                for(int i=0; i<count; i++) {
                    mi.forbitSkills[i] = list.get(i).getId();
                    
                    sb.append(list.get(i).getTitle());
                    sb.append(",");
                }                
                
                text.setText(sb.toString());
            } else {
                text.setText("");
            }
            
            fireApplyEditorValue();
            deactivate();
        }
    }
}
