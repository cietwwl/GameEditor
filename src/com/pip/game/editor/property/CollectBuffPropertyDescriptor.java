package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author wpjiang
 *  清除玩家身上的buffer
 */
public class CollectBuffPropertyDescriptor extends PropertyDescriptor {
    
    private int mapId;
    
    private byte type;
    /**
     * 物品清除功能
     */
    public final static byte  REMOVE = 2;
    
    public CollectBuffPropertyDescriptor(Object id, String displayName, int mapId, byte type) {
        super(id, displayName);
        this.mapId = mapId;
        this.type = type;
    }
    
    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new CollectBuffCellEditor(parent, mapId);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }
    
    public static class CollectBuffLabelProvider extends LabelProvider {
        public String getText(Object element) {
            int[] ids = (int[])element;
            
            if(ids == null || ids.length == 0) {
                return "";
            } else {
                StringBuffer sb = new StringBuffer();
                for(int i=0; i<ids.length; i++) {
                    BuffConfig buff = ProjectData.getActiveProject().findBuff(ids[i]);
                    if(buff != null) {
                        sb.append(buff.getTitle());
                        sb.append(",");
                    }
                }
                return sb.toString();
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.PropertyDescriptor#getLabelProvider()
     * 提供闭标签
     */
    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new CollectBuffLabelProvider();
    }
}
