package com.pip.game.editor.area;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.DataObject;
import com.pip.game.data.Faction;
import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.data.map.GamePatrolPath;
import com.pip.game.data.map.Period;
import com.pip.game.editor.property.AntiBlockPropertyDescriptor;
import com.pip.game.editor.property.MirrorSetPropertyDescriptor;
import com.pip.game.editor.property.NPCFunctionPropertyDescriptor;
import com.pip.game.editor.property.NPCPropertyDescriptor;
import com.pip.game.editor.property.NPCTemplatePropertyDescriptor;
import com.pip.game.editor.property.ParticleEffectPropertyDescriptor;
import com.pip.game.editor.property.PatrolPathPropertyDescriptor;

/**
 * ������������ҳ��
 * @author lighthu
 */
public class GameMapNPCPropertySource implements IPropertySource {
    protected GameAreaEditor owner;
    private GameMapNPC mapNPC;
    
    public GameMapNPCPropertySource(GameAreaEditor owner, GameMapNPC mapNPC) {
        this.owner = owner;
        this.mapNPC = mapNPC;
    }
    
    public Object getEditableValue() {
        return this;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] ret = new IPropertyDescriptor[33];
        ret[0] = new PropertyDescriptor("type", "����");
        ret[1] = new PropertyDescriptor("id", "ID");
        ret[2] = new NPCTemplatePropertyDescriptor("template", "ģ��");
        ret[3] = new TextPropertyDescriptor("name", "����");
        List<DataObject> factions = ProjectData.getActiveProject().getDictDataListByType(Faction.class);
        String[] labels = new String[factions.size()];
        for (int i = 0; i < factions.size(); i++) {
            labels[i] = factions.get(i).toString();
        }
        ret[4] = new ComboBoxPropertyDescriptor("faction", "��Ӫ", labels);
        ret[5] = new ComboBoxPropertyDescriptor("visible", "�Ƿ�ɼ�", new String[] { "��", "��" });
        ret[6] = new ComboBoxPropertyDescriptor("canAttack", "�Ƿ�ɱ�����", new String[] { "��", "��" });
        ret[7] = new TextPropertyDescriptor("refreshInterval", "ˢ�¼��(��)");
        ret[8] = new ComboBoxPropertyDescriptor("dynamicRefresh", "��̬ˢ��", new String[] { "��", "��" });
        ret[9] = new TextPropertyDescriptor("linkDistance", "���Ⱦ���(��)");
        ret[10] = new ComboBoxPropertyDescriptor("isGuard", "�Ƿ�����", new String[] { "��", "��" });
        ret[11] = new ComboBoxPropertyDescriptor("isStatic", "�Ƿ�̬", new String[] { "��", "��" });
        ret[12] = new ComboBoxPropertyDescriptor("canpass", "��ͨ��", new String[] { "��", "��" });
        ret[13] = new TextPropertyDescriptor("liveTime", "���ʱ��(��)");
        ret[14] = new NPCPropertyDescriptor("dieRefresh", "������ˢ��");
        ret[15] = new ComboBoxPropertyDescriptor("functional", "�Ƿ���NPC", new String[] { "��", "��" });
        ret[16] = new NPCFunctionPropertyDescriptor("functions", "����");
        ret[17] = new TextPropertyDescriptor("defaultchat", "ȱʡ�԰�");
        ret[18] = new ComboBoxPropertyDescriptor("broadcastDie", "������㲥", new String[] { "��", "��" });
        ret[19] = new TextPropertyDescriptor("searchName", "Ѱ·����");
        ret[20] = new TextPropertyDescriptor("period","ˢ��ʱ���");
        ret[21] = new TextPropertyDescriptor("revision", "���ư汾");
        
        ret[22] = new PatrolPathPropertyDescriptor("patrolpath1", "Ѳ��·��1", mapNPC);
        ret[23] = new PatrolPathPropertyDescriptor("patrolpath2", "Ѳ��·��2", mapNPC);
        ret[24] = new PatrolPathPropertyDescriptor("patrolpath3", "Ѳ��·��3", mapNPC);
        
        ret[25] = new ComboBoxPropertyDescriptor("whichFloor", "���ڵ�ͼ��", new String[] { "����", "���" });
        ret[26] = new TextPropertyDescriptor("conlliseDistance", "��ײ����(��)");
        
        ret[27] = new TextPropertyDescriptor("combatCount", "����ս������");
        ret[28] = new MirrorSetPropertyDescriptor("mirrorSet", "������λ", mapNPC.owner);
        ret[29] = new AntiBlockPropertyDescriptor("antiBlock", "Ĩ���赲����");
        ret[30] = new TextPropertyDescriptor("channel", "�������ƣ�,�ָ���");
        
        ret[31] = new ParticleEffectPropertyDescriptor("particle1", "����Ч��(�Ȼ�)");
        ret[32] = new ParticleEffectPropertyDescriptor("particle2", "����Ч��(��)");
        
        return ret;
    }

    public Object getPropertyValue(Object id) {
        if ("type".equals(id)) {
            return "NPC";
        } else if ("id".equals(id)) {
            return mapNPC.getGlobalID() + "(0x" + Integer.toHexString(mapNPC.getGlobalID()) + ")";
        } else if ("template".equals(id)) {
            if (mapNPC.template == null) {
                return -1;
            }
            return new Integer(mapNPC.template.id);
        } else if ("name".equals(id)) {
            return mapNPC.name;
        } else if ("faction".equals(id)) {
            if (mapNPC.faction == null) {
                return -1;
            }
            return ProjectData.getActiveProject().getDictObjectIndex(mapNPC.faction);
        } else if ("visible".equals(id)) {
            return mapNPC.visible ? 0 : 1;
        } else if ("canAttack".equals(id)) {
            return mapNPC.canAttack ? 0 : 1;
        } else if ("refreshInterval".equals(id)) {
            return String.valueOf(mapNPC.refreshInterval);
        } else if ("dynamicRefresh".equals(id)) {
            return mapNPC.dynamicRefresh ? 0 : 1;
        } else if ("linkDistance".equals(id)) {
            return String.valueOf(mapNPC.linkDistance / 8.0f);
        } else if ("isGuard".equals(id)) {
            return mapNPC.isGuard ? 0 : 1;
        } else if ("isStatic".equals(id)) {
            return mapNPC.isStatic ? 0 : 1;
        } else if ("canpass".equals(id)) {
            return mapNPC.canPass ? 0 : 1;
        } else if ("liveTime".equals(id)) {
            return String.valueOf(mapNPC.liveTime);
        } else if ("dieRefresh".equals(id)) {
            return mapNPC.dieRefresh;
        } else if ("functional".equals(id)) {
            return mapNPC.isFunctional ? 0 : 1;
        } else if ("functions".equals(id)) {
            return new String[] {  mapNPC.functionName,mapNPC.functionScript };
        } else if ("defaultchat".equals(id)) {
            return mapNPC.defaultChat;
        } else if ("broadcastDie".equals(id)) {
            return mapNPC.broadcastDie ? 0 : 1;
        } else if ("searchName".equals(id)) {
            return mapNPC.searchName;
        } else if ("period".equals(id)){
            if(mapNPC.periods.size()==0)
                return "";
            else{
                Period[] ps = new Period[mapNPC.periods.size()];
                mapNPC.periods.toArray(ps);
                return Period.getString(ps);
            }
        } else if ("revision".equals(id)) {
            return mapNPC.revision;
        } else if("patrolpath1".equals(id)) {
            GameMapObject gmo = mapNPC.owner.findObject(mapNPC.patrolPathId1);
            if(gmo != null && gmo instanceof GamePatrolPath) {
                GamePatrolPath gpp = (GamePatrolPath)gmo;
                return gpp.getGlobalID() + "(0x" + Integer.toHexString(gpp.getGlobalID()) + ")";
            } else {
                return "δָ��";
            }
        } else if("patrolpath2".equals(id)) {
            GameMapObject gmo = mapNPC.owner.findObject(mapNPC.patrolPathId2);
            if(gmo != null && gmo instanceof GamePatrolPath) {
                GamePatrolPath gpp = (GamePatrolPath)gmo;
                return gpp.getGlobalID() + "(0x" + Integer.toHexString(gpp.getGlobalID()) + ")";
            } else {
                return "δָ��";
            }
        } else if("patrolpath3".equals(id)) {
            GameMapObject gmo = mapNPC.owner.findObject(mapNPC.patrolPathId3);
            if(gmo != null && gmo instanceof GamePatrolPath) {
                GamePatrolPath gpp = (GamePatrolPath)gmo;
                return gpp.getGlobalID() + "(0x" + Integer.toHexString(gpp.getGlobalID()) + ")";
            } else {
                return "δָ��";
            }
        }else if("whichFloor".equals(id)) {
            return mapNPC.layer;
        }else if ("conlliseDistance".equals(id)) {
            return String.valueOf(mapNPC.conlliseDistance / 8.0f);
        }else if("combatCount".equals(id)) {
        	return String.valueOf(mapNPC.combatCount);
        }else if ("mirrorSet".equals(id)) {
            return new Long(mapNPC.mirrorSet);
        }else if ("antiBlock".equals(id)) {
            if (mapNPC.antiBlockArea == null) {
                return new int[4];
            }
            return mapNPC.antiBlockArea;
        }else if("channel".equals(id)){
            return String.valueOf(mapNPC.channel);
        } else if ("particle1".equals(id)) {
            return mapNPC.particle1;
        } else if ("particle2".equals(id)) {
            return mapNPC.particle2;
        }
        
        return null;
//        else {
//            throw new IllegalArgumentException();
//        }
    }

    public boolean isPropertySet(Object id) {
        return false;
    }

    public void resetPropertyValue(Object id) {}

    public void setPropertyValue(Object id, Object value) {
        if ("template".equals(id)) {
            int tid = ((Integer)value).intValue();
            NPCTemplate newValue = (NPCTemplate)ProjectData.getActiveProject().findObject(NPCTemplate.class, tid);
            if (newValue != mapNPC.template) {
                mapNPC.template = newValue;
                owner.setDirty(true);
            }                
        } else if ("name".equals(id)) {
            String newName = (String)value;
            newName = newName.trim();
            if (newName.length() > 0 && !newName.equals(mapNPC.name)) {
                mapNPC.name = newName;
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
            if (newValue != mapNPC.faction) {
                mapNPC.faction = newValue;
                owner.setDirty(true);
            }
        } else if ("visible".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapNPC.visible) {
                mapNPC.visible = newValue;
                owner.setDirty(true);
            }
        } else if ("canAttack".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapNPC.canAttack) {
                mapNPC.canAttack = newValue;
                owner.setDirty(true);
            }
        } else if ("refreshInterval".equals(id)) {
            try {
                int newValue = Integer.parseInt((String)value);
                if (newValue != mapNPC.refreshInterval) {
                    mapNPC.refreshInterval = newValue;
                    owner.setDirty(true);
                }
            } catch (Exception e) {
            }
        } else if ("dynamicRefresh".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapNPC.dynamicRefresh) {
                mapNPC.dynamicRefresh = newValue;
                owner.setDirty(true);
            }
        } else if ("linkDistance".equals(id)) {
            try {
                int newValue = (int)(Float.parseFloat((String)value) * 8.0f);
                if (newValue != mapNPC.linkDistance) {
                    mapNPC.linkDistance = newValue;
                    owner.setDirty(true);
                }
            } catch (Exception e) {
            }
        } else if ("isGuard".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapNPC.isGuard) {
                mapNPC.isGuard = newValue;
                owner.setDirty(true);
            }
        } else if ("isStatic".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapNPC.isStatic) {
                mapNPC.isStatic = newValue;
                owner.setDirty(true);
            }
        } else if ("canpass".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapNPC.canPass) {
                mapNPC.canPass = newValue;
                owner.setDirty(true);
            }
        } else if ("liveTime".equals(id)) {
            try {
                int newValue = Integer.parseInt((String)value);
                if (newValue != mapNPC.liveTime) {
                    mapNPC.liveTime = newValue;
                    owner.setDirty(true);
                }
            } catch (Exception e) {
            }
        } else if ("dieRefresh".equals(id)) {
            int newValue = ((Integer)value).intValue();
            if (newValue != mapNPC.dieRefresh) {
                mapNPC.dieRefresh = newValue;
                owner.setDirty(true);
            }
        } else if ("functional".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapNPC.isFunctional) {
                mapNPC.isFunctional = newValue;
                owner.setDirty(true);
            }
        } else if ("functions".equals(id)) {
            String[] funcs = (String[])value;
            if (!funcs[0].equals(mapNPC.functionName) || !funcs[1].equals(mapNPC.functionScript)) {
                mapNPC.functionName = funcs[0];
                mapNPC.functionScript = funcs[1];
                owner.setDirty(true);
            }
        } else if ("defaultchat".equals(id)) {
            String chat = (String)value;
            if (!chat.equals(mapNPC.defaultChat)) {
                mapNPC.defaultChat = chat;
                owner.setDirty(true);
            }
        } else if ("broadcastDie".equals(id)) {
            int index = ((Integer)value).intValue();
            boolean newValue = (index == 0);
            if (newValue != mapNPC.broadcastDie) {
                mapNPC.broadcastDie = newValue;
                owner.setDirty(true);
            }
        } else if ("searchName".equals(id)) {
            String newValue = (String)value;
            if (!newValue.equals(mapNPC.searchName)) {
                mapNPC.searchName = newValue;
                owner.setDirty(true);
            }
        } else if("period".equals(id)){
            try {
                Period[] ps = Period.parse((String)value);
                List<Period> l = new ArrayList<Period>(ps.length);
                for(Period p:ps){
                    l.add(p);
                }
                mapNPC.periods = l;
                owner.setDirty(true);
            }
            catch (Exception e) {
            }
        } else if ("revision".equals(id)) {
            String newValue = (String)value;
            if (!newValue.equals(mapNPC.revision)) {
                mapNPC.revision = newValue;
                owner.setDirty(true);
            }
        } else if("patrolpath1".equals(id)) {
            int pathId = ((Integer)value).intValue();
            mapNPC.patrolPathId1 = pathId;
            owner.setDirty(true);
        } else if("patrolpath2".equals(id)) {
            int pathId = ((Integer)value).intValue();
            mapNPC.patrolPathId2 = pathId;
            owner.setDirty(true);
        } else if("patrolpath3".equals(id)) {
            int pathId = ((Integer)value).intValue();
            mapNPC.patrolPathId3 = pathId;
            owner.setDirty(true);
        } else if("whichFloor".equals(id)) {
            mapNPC.layer = ((Integer)value).intValue();
            owner.setDirty(true);
        }else if ("conlliseDistance".equals(id)) {
            try {
                int newValue = (int)(Float.parseFloat((String)value) * 8.0f);
                if (newValue != mapNPC.conlliseDistance) {
                    mapNPC.conlliseDistance = newValue;
                    owner.setDirty(true);
                }
            } catch (Exception e) {
            }
        } else if("combatCount".equals(id)) {
        	mapNPC.combatCount = Integer.parseInt((String)value);
        	owner.setDirty(true);
        } else if ("mirrorSet".equals(id)) {
            long newValue = ((Long)value).longValue();
            if (newValue != mapNPC.mirrorSet) {
                mapNPC.mirrorSet = newValue;
                owner.setDirty(true);
            }
        } else if ("antiBlock".equals(id)) {
            int[] newValue = (int[])value;
            if (newValue[2] <= 0 || newValue[3] <= 0) {
                newValue = null;
            }
            if (!Arrays.equals(newValue, mapNPC.antiBlockArea)) {
                mapNPC.antiBlockArea = newValue.clone();
                owner.setDirty(true);
            }
        } else if("channel".equals(id)){
            String newValue = (String)value;
            if (!newValue.equals(mapNPC.channel)) {
                mapNPC.channel = newValue;
                owner.setDirty(true);
            }
        } else if ("particle1".equals(id)) {
            String newValue = (String)value;
            if (!newValue.equals(mapNPC.particle1)) {
                mapNPC.particle1 = newValue;
                owner.setDirty(true);
            }
        } else if ("particle2".equals(id)) {
            String newValue = (String)value;
            if (!newValue.equals(mapNPC.particle2)) {
                mapNPC.particle2 = newValue;
                owner.setDirty(true);
            }
        }
    }
}
