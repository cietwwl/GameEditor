package com.pip.game.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.forbid.ForbidItem;

/**
 * �ռ�һϵ����Ʒ
 */
public class CollectItemPropertyDescriptor extends PropertyDescriptor {
    private int mapId;
    
    /**
     * ���ṩ�Ĺ��ܲ���
     */
    private byte type;
    
    /**
     * ��Ʒ�������
     */
    public final static byte  REMOVE = 2;
    
    
    /**
     * ��Ʒ��ֹʹ�ù���
     */
    public final static byte FORBID = 1;
    
    public CollectItemPropertyDescriptor(Object id, String displayName, int mapId, byte type) {
        super(id, displayName);
        this.mapId = mapId;
        this.type = type;
    }

    public CellEditor createPropertyEditor(Composite parent) {
        CellEditor editor = new CollectItemCellEditor(parent, mapId, type);
        if (getValidator() != null) {
            editor.setValidator(getValidator());
        }
        return editor;
    }

    public ILabelProvider getLabelProvider() {
        if (isLabelProviderSet()) {
            return super.getLabelProvider();
        }
        return new CollectItemLabelProvider();
    }

    /**
     * NPC������ʾ������NPC ID����NPC��
     * @author lighthu
     */
    public static class CollectItemLabelProvider extends LabelProvider {
        public String getText(Object element) {
            int[] itemIds = (int[])element;
            
//            int mapID = ((Integer)element).intValue();
//            GameMapInfo mi = GameMapInfo.findByID(ProjectData.getActiveProject(), mapID);
//            
            if(itemIds == null || itemIds.length == 0) {
                return "";
            } else {
                StringBuffer sb = new StringBuffer();
                for(int i=0; i<itemIds.length; i++) {
                    ForbidItem item = ProjectData.getActiveProject().findForbidItem(itemIds[i]);
                    if(item != null) {
                        sb.append(item.getTitle());
                        sb.append(",");
                    } 
                }
                return sb.toString();               
            }
        }
    }
}
