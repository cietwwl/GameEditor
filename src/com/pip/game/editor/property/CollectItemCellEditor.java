package com.pip.game.editor.property;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;

import com.pip.game.data.ProjectData;
import com.pip.game.data.forbid.ForbidItem;
import com.pip.game.data.map.GameMapInfo;

/**
 * 选择禁用的物品编辑器
 */
public class CollectItemCellEditor extends CellEditorAdapter {
    protected int mapID;
    
    /**
     * 编辑器使用的功能
     */
    private byte type;
    public CollectItemCellEditor(Composite parent, int mapId, byte type) {
        super(parent);
        
        mapID = mapId;
        this.type = type;
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
        if(type == CollectItemPropertyDescriptor.FORBID){
            return mi.forbitItems;
        }else if(type == CollectItemPropertyDescriptor.REMOVE){
            return mi.removeItems;
        }
        return null;
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
            if(type == CollectItemPropertyDescriptor.FORBID){
                mi.forbitItems = (int[])value;
                if(mi.forbitItems.length > 0) {
                    StringBuffer sb = new StringBuffer();
                    for(int i=0; i<mi.forbitItems.length; i++) {
                        ForbidItem item = ProjectData.getActiveProject().findForbidItem(mi.forbitItems[i]);
                        if(item != null) {
                            sb.append(item.getTitle());
                            sb.append(",");
                        } 
    //                    else {
    //                        item = ProjectData.getActiveProject().findEquipment(mi.forbitItems[i]);
    //                        if(item != null) {
    //                            sb.append(item.getTitle());
    //                            sb.append(",");
    //                        }
    //                    }
                    }
                    text.setText(sb.toString());
                } else {
                    text.setText("");
                }
            }else if(type == CollectItemPropertyDescriptor.REMOVE){
                mi.removeItems = (int[])value;
                if(mi.removeItems.length > 0) {
                    StringBuffer sb = new StringBuffer();
                    for(int i=0; i<mi.removeItems.length; i++) {
                        ForbidItem item = ProjectData.getActiveProject().findForbidItem(mi.removeItems[i]);
                        if(item != null) {
                            sb.append(item.getTitle());
                            sb.append(",");
                        } 
    //                    else {
    //                        item = ProjectData.getActiveProject().findEquipment(mi.removeItems[i]);
    //                        if(item != null) {
    //                            sb.append(item.getTitle());
    //                            sb.append(",");
    //                        }
    //                    }
                    }
                    text.setText(sb.toString());
                } else {
                    text.setText("");
                }
            }
        }

    }
    protected void editText() {
        CollectItemDialog dlg = new CollectItemDialog(text.getShell());
        
        GameMapInfo mi = GameMapInfo.findByID(ProjectData.getActiveProject(), mapID);
        if(type == CollectItemPropertyDescriptor.FORBID){
            dlg.setSelItems(mi.forbitItems);
        }else if(type == CollectItemPropertyDescriptor.REMOVE){
            dlg.setSelItems(mi.removeItems);
        }
        
        if (dlg.open() == Dialog.OK) {
//            mapID = dlg.getSelectedNPC();
//            text.setText(GameMapObject.toString(ProjectData.getActiveProject(), mapID));            
            
            List<ForbidItem> list = dlg.getSelectedItems();
            int count = list.size();
            if(type == CollectItemPropertyDescriptor.FORBID){
                mi.forbitItems = new int[count];
                if(count > 0) {
                    StringBuffer sb = new StringBuffer();
                    for(int i=0; i<count; i++) {
                        ForbidItem item = ProjectData.getActiveProject().findForbidItem(list.get(i).id);
                        if(item != null) {
                            mi.forbitItems[i] = list.get(i).id;
                            sb.append(item.getTitle());
                            sb.append(",");
                        }
                    }
                    text.setText(sb.toString());
                } else {
                    text.setText("");
                }
            }else if(type == CollectItemPropertyDescriptor.REMOVE){
                mi.removeItems = new int[count];
                if(count > 0) {
                    StringBuffer sb = new StringBuffer();
                    for(int i=0; i<count; i++) {
                        ForbidItem item = ProjectData.getActiveProject().findForbidItem(list.get(i).id);
                        if(item != null) {
                            mi.removeItems[i] = list.get(i).id;
                            sb.append(item.getTitle());
                            sb.append(",");
                        }
                    }
                    text.setText(sb.toString());
                } else {
                    text.setText("");
                }
            }
            
            fireApplyEditorValue();
            deactivate();
        }
    }
}
