package com.pip.game.editor.skill;

import com.pip.game.data.ProjectData;
import com.pip.game.data.equipment.AttributeCalculator;
import com.pip.game.data.equipment.EquipmentAttribute;
import com.pip.game.data.skill.BuffConfig;
import com.pip.game.data.skill.EffectConfig;
import com.pip.game.data.skill.EffectParamRef;
import com.pip.game.data.skill.SkillConfig;
import com.pip.util.Utils;

/**
 * һ�����ܻ�BUFF�������޸�����
 */
public class ParamIndicator {
    public static final int TYPE_SKILL_ACTIVE = 0;
    public static final int TYPE_SKILL_PASSIVE = 1;
    public static final int TYPE_BUFF_OWNER = 2;
    public static final int TYPE_BUFF_SOURCE = 3;
    
    public int type;
    public int id;
    public int paramIndex;
    public boolean check;
    public int element;
    
    public void update(ParamIndicator obj) {
        type = obj.type;
        id = obj.id;
        paramIndex = obj.paramIndex;
        check = obj.check;
        element=obj.element;
    }
    
    public void load(String str) {
        try {
            String[] secs = Utils.splitString(str, ',');
            type = Integer.parseInt(secs[0]);
            id = Integer.parseInt(secs[1]);
            paramIndex = Integer.parseInt(secs[2]);
            check = Boolean.parseBoolean(secs[3]);
            element = Integer.parseInt(secs[4]);
        } catch (Exception e) {
        }
    }
    
    public String toString() {
        return type + "," + id + "," + paramIndex+","+check+","+element;
    }
    
    public String toString(ProjectData proj) {
        EquipmentAttribute[] attrs = proj.config.attrCalc.ATTRIBUTES;
        try {
            StringBuilder sb = new StringBuilder();
            if (type == TYPE_SKILL_ACTIVE) {
                
                SkillConfig skill = (SkillConfig)proj.findObject(SkillConfig.class, id);
                if(skill!=null){
                    EffectParamRef param = getParamRef(proj);
                    sb.append("ʩ�ż���");
                    sb.append(skill.title);
                    sb.append("ʱ:");
                    sb.append(param.getParamName());
                    if (check) {
                        sb.append(",");
                        sb.append(attrs[element].name);
                    }
                }
            } else if (type == TYPE_SKILL_PASSIVE) {
                SkillConfig skill = (SkillConfig)proj.findObject(SkillConfig.class, id);
                if(skill!=null){
                    EffectParamRef param = getParamRef(proj);
                    sb.append("�ܵ�����");
                    sb.append(skill.title);
                    sb.append("����/����ʱ:");
                    sb.append(param.getParamName());
                    if (check) {
                        sb.append(",");
                        sb.append(attrs[element].name);
                    }
                }
            } else if (type == TYPE_BUFF_OWNER) {
                BuffConfig buff = (BuffConfig)proj.findObject(BuffConfig.class, id);
                if(buff!=null){
                    EffectParamRef param = getParamRef(proj);
                    sb.append("����BUFF ");
                    sb.append(buff.title);
                    sb.append(":");
                    sb.append(param.getParamName());
                    if (check) {
                        sb.append(",");
                        sb.append(attrs[element].name);
                    }
                }
            } else if (type == TYPE_BUFF_SOURCE) {
                BuffConfig buff = (BuffConfig)proj.findObject(BuffConfig.class, id);
                if(buff!=null){
                    EffectParamRef param = getParamRef(proj);
                    sb.append("�����˼�BUFF ");
                    sb.append(buff.title);
                    sb.append(":");
                    sb.append(param.getParamName());
                    if (check) {
                        sb.append(",");
                        sb.append(attrs[element].name);
                    }
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "��";
        }
    }
    
    public EffectParamRef getParamRef(ProjectData proj) {
        try {
            if (type == TYPE_SKILL_ACTIVE || type == TYPE_SKILL_PASSIVE) {
                SkillConfig skill = (SkillConfig)proj.findObject(SkillConfig.class, id);
                return skill.effects.getParamAt(paramIndex);
            } else if (type == TYPE_BUFF_OWNER || type == TYPE_BUFF_SOURCE) {
                BuffConfig buff = (BuffConfig)proj.findObject(BuffConfig.class, id);
                EffectConfig gc = buff.getGeneralConfig();
                if (paramIndex < gc.getParamCount()) {
                    return new EffectParamRef(gc, paramIndex, -1);
                } else {
                    if(buff.effects.findEffect(gc.getType()) == null){//-2 ��DynamicGeneralConfig��type
                        return buff.effects.getParamAt(paramIndex - gc.getParamCount());
                    }else{
                        return buff.effects.getParamAt(paramIndex/* - gc.getParamCount()*/);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
