package com.pip.game.editor.area;

import java.util.List;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.DataObject;
import com.pip.game.data.Faction;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.MultiTargetMapExit;
import com.pip.game.editor.property.MultiTargetMapExitPropertyDescriptor;
import com.pip.game.editor.property.MirrorSetPropertyDescriptor;

/**
 * 场景出口属性页。
 * @author lighthu
 */
public class MultiTargetMapExitPropertySource implements IPropertySource {
    private GameAreaEditor owner;
    protected MultiTargetMapExit mapExit;
    
    public MultiTargetMapExitPropertySource(GameAreaEditor owner, MultiTargetMapExit mapExit) {
        this.owner = owner;
        this.mapExit = mapExit;
    }
    
    public Object getEditableValue() {
        return this;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] ret = new IPropertyDescriptor[7];
        ret[0] = new PropertyDescriptor("type", "类型");
        ret[1] = new PropertyDescriptor("id", "ID");
        ret[2] = new TextPropertyDescriptor("name", "传送门名称");
        List<DataObject> factions = ProjectData.getActiveProject().getDictDataListByType(Faction.class);
        String[] labels = new String[factions.size()];
        for (int i = 0; i < factions.size(); i++) {
            labels[i] = factions.get(i).toString();
        }
        ret[3] = new ComboBoxPropertyDescriptor("faction", "阵营", labels);
        ret[4] = new ComboBoxPropertyDescriptor("whichFloor", "所在地图层", new String[] { "地面", "天空" });
        ret[5] = new MirrorSetPropertyDescriptor("mirrorSet", "所在相位", mapExit.owner);
        ret[6] = new MultiTargetMapExitPropertyDescriptor("exits", "传出口",owner.mapView.getMapInfo(),mapExit);
        
        return ret;
    }

    public Object getPropertyValue(Object id) {
        if ("type".equals(id)) {
            return "多目标传送点";
        } else if ("id".equals(id)) {
            return mapExit.getGlobalID() + "(0x" + Integer.toHexString(mapExit.getGlobalID()) + ")";
        } else if("whichFloor".equals(id)) {
            return mapExit.layer;
        } else if ("mirrorSet".equals(id)) {
            return new Long(mapExit.mirrorSet);
        } else if("name".equals(id)){
            return mapExit.name;
        } else if ("exits".equals(id)){
            return mapExit.toString();
        } else if ("faction".equals(id)){
            if(mapExit.faction == null){
                return -1;
            }
            return ProjectData.getActiveProject().getDictObjectIndex(mapExit.faction);
        }else {
            throw new IllegalArgumentException();
        }
    }

    public boolean isPropertySet(Object id) {
        return false;
    }

    public void resetPropertyValue(Object id) {}

    public void setPropertyValue(Object id, Object value) {
        if("whichFloor".equals(id)) {
            mapExit.layer = ((Integer)value).intValue();
            owner.setDirty(true);
        } else if ("mirrorSet".equals(id)) {
            long newValue = ((Long)value).longValue();
            if (newValue != mapExit.mirrorSet) {
                mapExit.mirrorSet = newValue;
                owner.setDirty(true);
            }
        } else if("name".equals(id)){
            String newValue = (String)value;
            if (!newValue.equals(mapExit.name)) {
                mapExit.name = newValue;
                owner.setDirty(true);
            } 
        } else if("exits".equals(id)){
//            String newValue = (String)value;
//            if(!newValue.equals(mapExit.toString())){
//               
//            }
            owner.setDirty(true);
        } else if("faction".equals(id)){
            int index = ((Integer)value).intValue();
            Faction newValue;
            if (index != -1) {
                newValue = (Faction)ProjectData.getActiveProject().getDictDataListByType(Faction.class).get(index);
            } else {
                newValue = null;
            }
            if (newValue != mapExit.faction) {
                mapExit.faction = newValue;
                owner.setDirty(true);
            }
        }
    }
}
