package com.pip.game.data.vehicle;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.DataObject;
import com.pip.game.data.Faction;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.data.map.GamePatrolPath;
import com.pip.game.data.map.Period;
import com.pip.game.editor.area.GameAreaEditor;
import com.pip.game.editor.property.NPCFunctionPropertyDescriptor;

public class XyGameMapVehiclePropertySource implements IPropertySource {

    protected GameAreaEditor owner;
    private XyGameMapVehicle mapVehicle;
    
    public XyGameMapVehiclePropertySource(GameAreaEditor owner, XyGameMapVehicle mapVehicle) {
        this.owner = owner;
        this.mapVehicle = mapVehicle;
    }
    
    public Object getEditableValue() {
        return this;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] ret = new IPropertyDescriptor[32];
        ret[0] = new PropertyDescriptor("type", "类型");
        ret[1] = new PropertyDescriptor("id", "ID");
        ret[2] = new VehicleTemplatePropertyDescriptor("template", "模板");
        ret[3] = new TextPropertyDescriptor("name", "名称");
        List<DataObject> factions = ProjectData.getActiveProject().getDictDataListByType(Faction.class);
        String[] labels = new String[factions.size()];
        for (int i = 0; i < factions.size(); i++) {
            labels[i] = factions.get(i).toString();
        }
        ret[4] = new ComboBoxPropertyDescriptor("faction", "阵营", labels);
        ret[5] = new ComboBoxPropertyDescriptor("visible", "是否可见", new String[] { "是", "否" });
        ret[6] = new ComboBoxPropertyDescriptor("canAttack", "是否可被攻击", new String[] { "是", "否" });
        ret[7] = new TextPropertyDescriptor("refreshInterval", "刷新间隔(秒)");
        ret[8] = new ComboBoxPropertyDescriptor("dynamicRefresh", "动态刷新", new String[] { "是", "否" });
        ret[9] = new TextPropertyDescriptor("linkDistance", "呼救距离(码)");
        ret[10] = new ComboBoxPropertyDescriptor("isGuard", "是否卫兵", new String[] { "是", "否" });
        ret[11] = new ComboBoxPropertyDescriptor("isStatic", "是否静态", new String[] { "是", "否" });
        ret[12] = new ComboBoxPropertyDescriptor("canpass", "可通过", new String[] { "是", "否" });
        ret[13] = new TextPropertyDescriptor("liveTime", "存活时间(秒)");
        ret[14] = new VehicleTemplatePropertyDescriptor("dieRefresh", "死亡后刷新");
        ret[15] = new ComboBoxPropertyDescriptor("functional", "是否功能载具", new String[] { "是", "否" });
        //载具的功能待修改
        ret[16] = new NPCFunctionPropertyDescriptor("functions", "功能");
        ret[17] = new ComboBoxPropertyDescriptor("broadcastDie", "死亡后广播", new String[] { "是", "否" });
        ret[18] = new TextPropertyDescriptor("searchName", "寻路名称");
        ret[19] = new TextPropertyDescriptor("period","刷新时间段");
        ret[20] = new TextPropertyDescriptor("revision", "限制版本");
        
        ret[21] = new VehiclePatrolPathPropertyDescriptor("patrolpath1", "巡逻路径1", mapVehicle);
        ret[22] = new VehiclePatrolPathPropertyDescriptor("patrolpath2", "巡逻路径2", mapVehicle);
        ret[23] = new VehiclePatrolPathPropertyDescriptor("patrolpath3", "巡逻路径3", mapVehicle);
        
        ret[24] = new ComboBoxPropertyDescriptor("whichFloor", "所在地图层", new String[] { "地面", "天空" });
        ret[25] = new TextPropertyDescriptor("conlliseDistance", "碰撞距离(码)");
        
        
        ret[26] = new ComboBoxPropertyDescriptor("reuse", "是否重复使用", new String[] { "是", "否" });
        ret[27] = new TextPropertyDescriptor("combatCount", "使用的次数");
        ret[28] = new ComboBoxPropertyDescriptor("throughFloor", "是否地面无阻", new String[] { "是", "否" });
        ret[29] = new ComboBoxPropertyDescriptor("copy", "是否副本", new String[] { "是", "否" });
        ret[30] = new ComboBoxPropertyDescriptor("disappear", "是否消失", new String[] { "是", "否" });
        ret[31] = new ComboBoxPropertyDescriptor("canSeeTitle","是否可见名称",new String[] {"是","否"});
        return ret;
    }

    public Object getPropertyValue(Object id) {
        if ("type".equals(id)) {
            return "载具";
        } else if ("id".equals(id)) {
            return mapVehicle.getGlobalID() + "(0x" + Integer.toHexString(mapVehicle.getGlobalID()) + ")";
        } else if ("template".equals(id)) {
            if (mapVehicle.template == null) {
                return -1;
            }
            return new Integer(mapVehicle.template.id);
        } else if ("name".equals(id)) {
            return mapVehicle.name;
        } else if ("faction".equals(id)) {
            if (mapVehicle.faction == null) {
                return -1;
            }
            return ProjectData.getActiveProject().getDictObjectIndex(mapVehicle.faction);
        } else if ("visible".equals(id)) {
            return mapVehicle.visible ? 0 : 1;
        } else if ("canAttack".equals(id)) {
            return mapVehicle.canAttack ? 0 : 1;
        } else if ("refreshInterval".equals(id)) {
            return String.valueOf(mapVehicle.refreshInterval);
        } else if ("dynamicRefresh".equals(id)) {
            return mapVehicle.dynamicRefresh ? 0 : 1;
        } else if ("linkDistance".equals(id)) {
            return String.valueOf(mapVehicle.linkDistance / 8.0f);
        } else if ("isGuard".equals(id)) {
            return mapVehicle.isGuard ? 0 : 1;
        } else if ("isStatic".equals(id)) {
            return mapVehicle.isStatic ? 0 : 1;
        } else if ("canpass".equals(id)) {
            return mapVehicle.canPass ? 0 : 1;
        } else if ("liveTime".equals(id)) {
            return String.valueOf(mapVehicle.liveTime);
        } else if ("dieRefresh".equals(id)) {
            return mapVehicle.dieRefresh;
        } else if ("functional".equals(id)) {
            return mapVehicle.isFunctional ? 0 : 1;
        } else if ("functions".equals(id)) {
            return new String[] { mapVehicle.functionName, mapVehicle.functionScript };
        } else if ("broadcastDie".equals(id)) {
            return mapVehicle.broadcastDie ? 0 : 1;
        } else if ("searchName".equals(id)) {
            return mapVehicle.searchName;
        } else if ("period".equals(id)){
            if(mapVehicle.periods.size()==0)
                return "";
            else{
                Period[] ps = new Period[mapVehicle.periods.size()];
                mapVehicle.periods.toArray(ps);
                return Period.getString(ps);
            }
        } else if ("revision".equals(id)) {
            return mapVehicle.revision;
        } else if("patrolpath1".equals(id)) {
            GameMapObject gmo = mapVehicle.owner.findObject(mapVehicle.patrolPathId1);
            if(gmo != null && gmo instanceof GamePatrolPath) {
                GamePatrolPath gpp = (GamePatrolPath)gmo;
                return gpp.getGlobalID() + "(0x" + Integer.toHexString(gpp.getGlobalID()) + ")";
            } else {
                return "未指定";
            }
        } else if("patrolpath2".equals(id)) {
            GameMapObject gmo = mapVehicle.owner.findObject(mapVehicle.patrolPathId2);
            if(gmo != null && gmo instanceof GamePatrolPath) {
                GamePatrolPath gpp = (GamePatrolPath)gmo;
                return gpp.getGlobalID() + "(0x" + Integer.toHexString(gpp.getGlobalID()) + ")";
            } else {
                return "未指定";
            }
        } else if("patrolpath3".equals(id)) {
            GameMapObject gmo = mapVehicle.owner.findObject(mapVehicle.patrolPathId3);
            if(gmo != null && gmo instanceof GamePatrolPath) {
                GamePatrolPath gpp = (GamePatrolPath)gmo;
                return gpp.getGlobalID() + "(0x" + Integer.toHexString(gpp.getGlobalID()) + ")";
            } else {
                return "未指定";
            }
        }else if("whichFloor".equals(id)) {
            return mapVehicle.layer;
        }else if ("conlliseDistance".equals(id)) {
            return String.valueOf(mapVehicle.conlliseDistance / 8.0f);
        }else if("combatCount".equals(id)) {
            return String.valueOf(mapVehicle.combatCount);
        }else if("reuse".equals(id)){
            return mapVehicle.reuse? 0:1;
        } else if("throughFloor".equals(id)){
            return mapVehicle.throughFloor? 0:1;
        } else if("copy".equals(id)){
            return mapVehicle.copy? 0:1;
        } else if("disappear".equals(id)){
            return mapVehicle.disappear? 0:1;
        }else if("canSeeTitle".equals(id)){
            return mapVehicle.canSeeTitle?0:1;
        }
        return null;
    }

    public boolean isPropertySet(Object id) {
        return false;
    }

    public void resetPropertyValue(Object id) {}

    public void setPropertyValue(Object id, Object value) {
        if ("template".equals(id)) {
            int tid = ((Integer)value).intValue();
            Vehicle newValue = (Vehicle)ProjectData.getActiveProject().findObject(Vehicle.class, tid);
            if (newValue != mapVehicle.template) {
                mapVehicle.template = newValue;
                owner.setDirty(true);
            }                
        } else if ("name".equals(id)) {
            String newName = (String)value;
            newName = newName.trim();
            if (newName.length() > 0 && !newName.equals(mapVehicle.name)) {
                mapVehicle.name = newName;
                owner.setDirty(true);
            }
        } else if ("faction".equals(id)) {
            int index = ((Integer)value).intValue();
            Faction newValue;
            if (index != -1) {
                newValue = (Faction)ProjectData.getActiveProject().getDictDataListByType(Faction.class).get(index);
            } else {
                newValue = null;
            }
            if (newValue != mapVehicle.faction) {
                mapVehicle.faction = newValue;
                owner.setDirty(true);
            }
        } else if ("visible".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapVehicle.visible) {
                mapVehicle.visible = newValue;
                owner.setDirty(true);
            }
        } else if ("canAttack".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapVehicle.canAttack) {
                mapVehicle.canAttack = newValue;
                owner.setDirty(true);
            }
        } else if ("refreshInterval".equals(id)) {
            try {
                int newValue = Integer.parseInt((String)value);
                if (newValue != mapVehicle.refreshInterval) {
                    mapVehicle.refreshInterval = newValue;
                    owner.setDirty(true);
                }
            } catch (Exception e) {
            }
        } else if ("dynamicRefresh".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapVehicle.dynamicRefresh) {
                mapVehicle.dynamicRefresh = newValue;
                owner.setDirty(true);
            }
        } else if ("linkDistance".equals(id)) {
            try {
                int newValue = (int)(Float.parseFloat((String)value) * 8.0f);
                if (newValue != mapVehicle.linkDistance) {
                    mapVehicle.linkDistance = newValue;
                    owner.setDirty(true);
                }
            } catch (Exception e) {
            }
        } else if ("isGuard".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapVehicle.isGuard) {
                mapVehicle.isGuard = newValue;
                owner.setDirty(true);
            }
        } else if ("isStatic".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapVehicle.isStatic) {
                mapVehicle.isStatic = newValue;
                owner.setDirty(true);
            }
        } else if ("canpass".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapVehicle.canPass) {
                mapVehicle.canPass = newValue;
                owner.setDirty(true);
            }
        } else if ("liveTime".equals(id)) {
            try {
                int newValue = Integer.parseInt((String)value);
                if (newValue != mapVehicle.liveTime) {
                    mapVehicle.liveTime = newValue;
                    owner.setDirty(true);
                }
            } catch (Exception e) {
            }
        } else if ("dieRefresh".equals(id)) {
            int newValue = ((Integer)value).intValue();
            if (newValue != mapVehicle.dieRefresh) {
                mapVehicle.dieRefresh = newValue;
                owner.setDirty(true);
            }
        } else if ("functional".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapVehicle.isFunctional) {
                mapVehicle.isFunctional = newValue;
                owner.setDirty(true);
            }
        } else if ("functions".equals(id)) {
            String[] funcs = (String[])value;
            if (!funcs[0].equals(mapVehicle.functionName) || !funcs[1].equals(mapVehicle.functionScript)) {
                mapVehicle.functionName = funcs[0];
                mapVehicle.functionScript = funcs[1];
                owner.setDirty(true);
            }
        } else if ("broadcastDie".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapVehicle.broadcastDie) {
                mapVehicle.broadcastDie = newValue;
                owner.setDirty(true);
            }
        } else if ("searchName".equals(id)) {
            String newValue = (String)value;
            if (!newValue.equals(mapVehicle.searchName)) {
                mapVehicle.searchName = newValue;
                owner.setDirty(true);
            }
        } else if("period".equals(id)){
            try {
                Period[] ps = Period.parse((String)value);
                List<Period> l = new ArrayList<Period>(ps.length);
                for(Period p:ps){
                    l.add(p);
                }
                mapVehicle.periods = l;
                owner.setDirty(true);
            }
            catch (Exception e) {
            }
        } else if ("revision".equals(id)) {
            String newValue = (String)value;
            if (!newValue.equals(mapVehicle.revision)) {
                mapVehicle.revision = newValue;
                owner.setDirty(true);
            }
        } else if("patrolpath1".equals(id)) {
            int pathId = ((Integer)value).intValue();
            mapVehicle.patrolPathId1 = pathId;
            owner.setDirty(true);
        } else if("patrolpath2".equals(id)) {
            int pathId = ((Integer)value).intValue();
            mapVehicle.patrolPathId2 = pathId;
            owner.setDirty(true);
        } else if("patrolpath3".equals(id)) {
            int pathId = ((Integer)value).intValue();
            mapVehicle.patrolPathId3 = pathId;
            owner.setDirty(true);
        } else if("whichFloor".equals(id)) {
            mapVehicle.layer = ((Integer)value).intValue();
            owner.setDirty(true);
        }else if ("conlliseDistance".equals(id)) {
            try {
                int newValue = (int)(Float.parseFloat((String)value) * 8.0f);
                if (newValue != mapVehicle.conlliseDistance) {
                    mapVehicle.conlliseDistance = newValue;
                    owner.setDirty(true);
                }
            } catch (Exception e) {
            }
        } else if("combatCount".equals(id)) {
            mapVehicle.combatCount = Integer.parseInt((String)value);
            owner.setDirty(true);
        }else if ("reuse".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapVehicle.reuse) {
                mapVehicle.reuse = newValue;
                owner.setDirty(true);
            }
      } else  if ("throughFloor".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapVehicle.throughFloor) {
                mapVehicle.throughFloor = newValue;
                owner.setDirty(true);
            }
      } else  if ("copy".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapVehicle.copy) {
                mapVehicle.copy = newValue;
                owner.setDirty(true);
            }
      } else  if ("disappear".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapVehicle.disappear) {
                mapVehicle.disappear = newValue;
                owner.setDirty(true);
            }
      } else if ("canSeeTitle".equals(id)) {
          int index = ((Integer)value).intValue();
          boolean newValue = (index == 0);
          if(newValue != mapVehicle.canSeeTitle){
              mapVehicle.canSeeTitle = newValue;
              owner.setDirty(true);
          }
      }
    }

}
