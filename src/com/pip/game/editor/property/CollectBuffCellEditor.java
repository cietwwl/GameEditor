package com.pip.game.editor.property;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;

import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.skill.BuffConfig;
import com.pip.game.data.skill.IBuffConfig;

/**
 * @author wpjiang
 *  Çå³ýbuff±à¼­Æ÷
 */
public class CollectBuffCellEditor extends CellEditorAdapter {
    
    protected int mapId;
    
    public CollectBuffCellEditor(Composite parent, int mapId) {
        super(parent);
        // TODO Auto-generated constructor stub
        this.mapId = mapId;
    }

    @Override
    protected void editText() {
        // TODO Auto-generated method stub
        CollectBuffDialog dlg = new CollectBuffDialog(text.getShell());
        
        GameMapInfo mi = GameMapInfo.findByID(ProjectData.getActiveProject(), mapId);
        dlg.setSelBuffs(mi.removeBuffs);
        
        if (dlg.open() == Dialog.OK) {
            List<IBuffConfig> list = dlg.getSelectedBuffs();
            int count = list.size();
            mi.removeBuffs = new int[count];
            if(count > 0) {
                StringBuffer sb = new StringBuffer(); 
                for(int i=0; i<count; i++) {
                    mi.removeBuffs[i] = list.get(i).getId();
                    
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

    @Override
    protected Object doGetValue() {
        // TODO Auto-generated method stub
        GameMapInfo mi = GameMapInfo.findByID(ProjectData.getActiveProject(), mapId);
        return mi.removeBuffs;
    }

    @Override
    protected void doSetValue(Object value) {
        // TODO Auto-generated method stub
        if(value != null && value instanceof int[]) {
            GameMapInfo mi = GameMapInfo.findByID(ProjectData.getActiveProject(), mapId);
            mi.removeBuffs = (int[])value;
            if(mi.removeBuffs.length > 0) {
                StringBuffer sb = new StringBuffer();
                for(int i=0; i<mi.removeBuffs.length; i++) {
                    BuffConfig buff = ProjectData.getActiveProject().findBuff(mi.removeBuffs[i]);
                    if(buff != null) {
                        sb.append(buff.getTitle());
                        sb.append(",");
                    }
                }
                text.setText(sb.toString());
            } else {
                text.setText("");
            }
        }
    }

}
