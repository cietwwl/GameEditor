package com.pip.game.editor.area;

import java.util.List;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.DataObject;
import com.pip.game.data.Faction;
import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.editor.property.AIPropertyDescriptor;
import com.pip.game.editor.property.BuffPropertyDescriptor;
import com.pip.game.editor.property.ChooseAIDialog;
import com.pip.game.editor.property.CollectBuffPropertyDescriptor;
import com.pip.game.editor.property.CollectItemPropertyDescriptor;
import com.pip.game.editor.property.CollectSkillPropertyDescriptor;
import com.pip.game.editor.property.AILogicPropertyDescriptor;
import com.pip.game.editor.property.ConditionalLocationPropertyDescriptor;
import com.pip.game.editor.property.SoundPropertyDescriptor;

/**
 * 地图属性页。
 * @author lighthu
 */
public class GameMapPropertySource implements IPropertySource {
    private GameAreaEditor owner;
    private GameMapInfo mapInfo;
    
    public GameMapPropertySource(GameAreaEditor owner, GameMapInfo mapInfo) {
        this.owner = owner;
        this.mapInfo = mapInfo;
    }
    
    public Object getEditableValue() {
        return this;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] ret = new IPropertyDescriptor[25 + 65];
        ret[0] = new PropertyDescriptor("type", "类型");
        ret[1] = new PropertyDescriptor("id", "ID");
        ret[2] = new TextPropertyDescriptor("name", "场景名称");
        
        ret[3] = new ComboBoxPropertyDescriptor("neutral", "是否中立", new String[] { "是", "否" });
        ret[4] = new ComboBoxPropertyDescriptor("allowDuel", "允许决斗", new String[] { "是", "否" });
        ret[5] = new TextPropertyDescriptor("protect", "屠杀次数保护（0不保护）");
        ret[6] = new ComboBoxPropertyDescriptor("allowFollow", "允许跟随", new String[] { "是", "否" });
        ret[7] = new ComboBoxPropertyDescriptor("splitFaction", "隔离阵营", new String[] { "是", "否" });
        ret[8] = new SoundPropertyDescriptor("backgroundMusic", "背景音乐");
        List<DataObject> factions = ProjectData.getActiveProject().getDictDataListByType(Faction.class);
        String[] labels = new String[factions.size()];
        for (int i = 0; i < factions.size(); i++) {
            labels[i] = factions.get(i).toString();
        }
        ret[9] = new ComboBoxPropertyDescriptor("faction", "阵营", labels);
        ret[10] = new TextPropertyDescriptor("maxPlayer", "最大玩家数");
                
        ret[11] = new ComboBoxPropertyDescriptor("allowExchange", "允许交易", new String[] { "是", "否" });
        ret[12] = new ComboBoxPropertyDescriptor("allowTeam", "允许组队", new String[] { "是", "否" });
        ret[13] = new ComboBoxPropertyDescriptor("hasNoteTeam", "组队邀请提示", new String[] { "是", "否" });
        ret[14] = new ComboBoxPropertyDescriptor("allowJoinPartyInvite", "允许入会邀请", new String[] { "是", "否" });
        ret[15] = new ComboBoxPropertyDescriptor("isCopy", "是副本", new String[] { "是", "否" });
        ret[16] = new ComboBoxPropertyDescriptor("allowFly", "允许飞行", new String[] { "是", "否" });
        ret[17] = new ComboBoxPropertyDescriptor("allowPVP", "允许PVP", new String[] { "是", "否" });
        ret[18] = new ComboBoxPropertyDescriptor("canServerInvite", "收到服务器邀请", new String[] { "是", "否" });
        
        ret[19] = new CollectItemPropertyDescriptor("forbitItem", "禁止的道具", mapInfo.getGlobalID(), CollectItemPropertyDescriptor.FORBID);
        ret[20] = new CollectSkillPropertyDescriptor("skillItem", "禁止的技能", mapInfo.getGlobalID());
        ret[21] = new CollectItemPropertyDescriptor("removeItem", "清除的道具", mapInfo.getGlobalID(), CollectItemPropertyDescriptor.REMOVE);
        ret[22] = new CollectBuffPropertyDescriptor("removeBuff", "清除的buff", mapInfo.getGlobalID(), CollectBuffPropertyDescriptor.REMOVE);
        //ret[23] = new ConditionPropertyDescriptor("mapAI", "场景AI", mapInfo.AI);
        ret[23] = new AIPropertyDescriptor("mapAI", "场景AI");
        for (int i = 0; i < 64; i++) {
            ret[24 + i] = new TextPropertyDescriptor("mirror" + i, "相位" + i);
        }
        ret[24 + 64] = new ComboBoxPropertyDescriptor("alwaysInBattle", "始终处于战斗状态", new String[] { "是", "否" });
        ret[25 + 64] = new ComboBoxPropertyDescriptor("getExpwhenKillPlayer", "杀人是否获得经验", new String[] { "是", "否" });
        return ret;
    }

    public Object getPropertyValue(Object id) {        
        if ("type".equals(id)) {
            return "场景";
        } else if ("id".equals(id)) {
            return mapInfo.getGlobalID() + "(0x" + Integer.toHexString(mapInfo.getGlobalID()) + ")";
        } 
        else if ("name".equals(id)) {
            return mapInfo.name;
        }
        else if ("neutral".equals(id)) {
            return mapInfo.neutral ? new Integer(0) : new Integer(1);
        }
        else if ("allowDuel".equals(id)) {
            return mapInfo.allowDuel ? new Integer(0) : new Integer(1);
        }
        else if ("protect".equals(id)) {
            return String.valueOf((mapInfo.protect));
        }
        else if ("allowFollow".equals(id)) {
            return mapInfo.allowFollow ? new Integer(0) : new Integer(1);
        }
        else if ("splitFaction".equals(id)) {
            return mapInfo.splitFaction ? new Integer(0) : new Integer(1);
        }
        else if ("backgroundMusic".equals(id)) {
            return new Integer(mapInfo.backgroundMusic);
        }else if ("faction".equals(id)) {
            if (mapInfo.faction == null) {
                return -1;
            }
            return ProjectData.getActiveProject().getDictObjectIndex(mapInfo.faction);
        } else if ("allowExchange".equals(id)) {
            return mapInfo.allowExchange ? new Integer(0) : new Integer(1);
        } else if ("allowTeam".equals(id)) {
            return mapInfo.allowTeam ? new Integer(0) : new Integer(1);
        } else if ("hasNoteTeam".equals(id)) {
            return mapInfo.hasNoteTeam ? new Integer(0) : new Integer(1);
        } else if ("allowJoinPartyInvite".equals(id)) {
            return mapInfo.allowJoinPartyInvite ? new Integer(0) : new Integer(1);
        } else if ("isCopy".equals(id)) {
            return mapInfo.isCopy ? new Integer(0) : new Integer(1);
        } else if ("allowFly".equals(id)) {
            return mapInfo.allowFly ? new Integer(0) : new Integer(1);
        } else if ("allowPVP".equals(id)) {
            return mapInfo.allowPVP ? new Integer(0) : new Integer(1);
        } else if ("canServerInvite".equals(id)) {
            return mapInfo.canServerInvite ? new Integer(0) : new Integer(1);
        } else if("forbitItem".equals(id)) {
            return mapInfo.forbitItems;
        } else if ("skillItem".equals(id)) {
            return mapInfo.forbitSkills;
        }
        else if("removeItem".equals(id)){
            return mapInfo.removeItems;
        }else if("removeBuff".equals(id)){
            return mapInfo.removeBuffs;
        } else if("mapAI".equals(id)) {
            return mapInfo.AIData;
        } else if("maxPlayer".equals(id)) {
            return String.valueOf(mapInfo.maxPlayer);
        } else if (((String)id).startsWith("mirror")) {
            int index = Integer.parseInt(((String)id).substring(6));
            return mapInfo.mirrorNames[index];
        }else if("alwaysInBattle".equals(id)){
            return mapInfo.alwaysInBattle ? new Integer(0) : new Integer(1);
        }else if("getExpwhenKillPlayer".equals(id)){
            return mapInfo.getExpwhenKillPlayer?new Integer(0):new Integer(1);
        }else {
            throw new IllegalArgumentException();
        }
    }

    public boolean isPropertySet(Object id) {
        return false;
    }

    public void resetPropertyValue(Object id) {}

    public void setPropertyValue(Object id, Object value) {
        if ("name".equals(id)) {
            if (!value.equals(mapInfo.name)) {
                mapInfo.name = (String)value;
                owner.setDirty(true);
            }
        } else if ("neutral".equals(id)) {
            boolean newValue = new Integer(0).equals(value);
            if (newValue != mapInfo.neutral) {
                mapInfo.neutral = newValue;
                owner.setDirty(true);
            }
        } else if ("allowDuel".equals(id)) {
            boolean newValue = new Integer(0).equals(value);
            if (newValue != mapInfo.allowDuel) {
                mapInfo.allowDuel = newValue;
                owner.setDirty(true);
            }
        } else if ("protect".equals(id)) {
        	int newValue = Integer.parseInt((String)value);
            if (newValue != mapInfo.protect) {
                mapInfo.protect = newValue;
                owner.setDirty(true);
            }
        } else if ("allowFollow".equals(id)) {
            boolean newValue = new Integer(0).equals(value);
            if (newValue != mapInfo.allowFollow) {
                mapInfo.allowFollow = newValue;
                owner.setDirty(true);
            }
        } else if ("splitFaction".equals(id)) {
            boolean newValue = new Integer(0).equals(value);
            if (newValue != mapInfo.splitFaction) {
                mapInfo.splitFaction = newValue;
                owner.setDirty(true);
            }
        } else if ("backgroundMusic".equals(id)) {
            int newValue = ((Integer)value).intValue();
            if (newValue != mapInfo.backgroundMusic) {
                mapInfo.backgroundMusic = newValue;
                owner.setDirty(true);
            }
        }else if ("faction".equals(id)) {
            int index = ((Integer)value).intValue();
            Faction newValue;
            if (index != -1) {
                newValue = (Faction)ProjectData.getActiveProject().getDictDataListByType(Faction.class).get(index);
            } else {
                newValue = null;
            }
            if (newValue != mapInfo.faction) {
                mapInfo.faction = newValue;
                owner.setDirty(true);
            }
        }  else if ("allowExchange".equals(id)) {
            boolean newValue = new Integer(0).equals(value);
            if (newValue != mapInfo.allowExchange) {
                mapInfo.allowExchange = newValue;
                owner.setDirty(true);
            }
        } else if ("allowTeam".equals(id)) {
            boolean newValue = new Integer(0).equals(value);
            if (newValue != mapInfo.allowTeam) {
                mapInfo.allowTeam = newValue;
                owner.setDirty(true);
            }
        } else if ("hasNoteTeam".equals(id)) {
            boolean newValue = new Integer(0).equals(value);
            if (newValue != mapInfo.hasNoteTeam) {
                mapInfo.hasNoteTeam = newValue;
                owner.setDirty(true);
            }
        } else if ("allowJoinPartyInvite".equals(id)) {
            boolean newValue = new Integer(0).equals(value);
            if (newValue != mapInfo.allowJoinPartyInvite) {
                mapInfo.allowJoinPartyInvite = newValue;
                owner.setDirty(true);
            }
        } else if ("isCopy".equals(id)) {
            boolean newValue = new Integer(0).equals(value);
            if (newValue != mapInfo.isCopy) {
                mapInfo.isCopy = newValue;
                owner.setDirty(true);
            }
        } else if ("allowFly".equals(id)) {
            boolean newValue = new Integer(0).equals(value);
            if (newValue != mapInfo.allowFly) {
                mapInfo.allowFly = newValue;
                owner.setDirty(true);
            }
        } else if ("allowPVP".equals(id)) {
            boolean newValue = new Integer(0).equals(value);
            if (newValue != mapInfo.allowPVP) {
                mapInfo.allowPVP = newValue;
                owner.setDirty(true);
            }
        } else if ("canServerInvite".equals(id)) {
            boolean newValue = new Integer(0).equals(value);
            if (newValue != mapInfo.canServerInvite) {
                mapInfo.canServerInvite = newValue;
                owner.setDirty(true);
            }
        } else if("forbitItem".equals(id)) {
            mapInfo.forbitItems = (int[])value;
            owner.setDirty(true);
        } else if("skillItem".equals(id)) {
            mapInfo.forbitSkills = (int[])value;
            owner.setDirty(true);
        } else if("removeItem".equals(id)){
            mapInfo.removeItems = (int[])value;
            owner.setDirty(true);
        }else if("removeBuff".equals(id)){
            mapInfo.removeBuffs = (int[])value;
            owner.setDirty(true);
        } else if("mapAI".equals(id)) {
            mapInfo.AIData = ((Integer)value).intValue();
            owner.setDirty(true);
        } else if ("maxPlayer".equals(id)) {
            try {
                int newValue = Integer.parseInt((String)value);
                if (newValue != mapInfo.maxPlayer) {
                    mapInfo.maxPlayer = newValue;
                    owner.setDirty(true);
                }
            } catch (Exception e) {
            }
        } else if (((String)id).startsWith("mirror")) {
            int index = Integer.parseInt(((String)id).substring(6));
            String newValue = (String)value;
            newValue = newValue.replace(',', ' ');
            if (!newValue.equals(mapInfo.mirrorNames[index])) {
                mapInfo.mirrorNames[index] = newValue;
                owner.setDirty(true);
            }
        }else if("alwaysInBattle".equals(id)){
            boolean newValue = new Integer(0).equals(value);
            if (newValue != mapInfo.alwaysInBattle) {
                mapInfo.alwaysInBattle = newValue;
                owner.setDirty(true);
            }
        }else if("getExpwhenKillPlayer".equals(id)){
            boolean newValue = new Integer(0).equals(value);
            if (newValue != mapInfo.getExpwhenKillPlayer) {
                mapInfo.getExpwhenKillPlayer = newValue;
                owner.setDirty(true);
            }
        }else {
            throw new IllegalArgumentException();
        }
    }
}
