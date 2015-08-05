package com.pip.game.editor.skill;

import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectData;
import com.pip.game.data.autocoding.AnalyzeSkill;
import com.pip.game.data.equipment.AttributeCalculator;
import com.pip.game.data.equipment.Equipment;
import com.pip.game.data.skill.BuffConfig;
import com.pip.game.data.skill.EffectConfig;
import com.pip.game.data.skill.SkillConfig;

/**
 * ����ƽ��������ߡ�
 * @author lighthu
 */
public class SkillAnalyzer {
    private SkillConfig skill;
    
    public SkillAnalyzer(SkillConfig skill) {
        this.skill = skill;
    }
    
    /**
     * ȡ�ü��ܷ���������������֡�
     */
    public String[] getParamNames() {
        if (skill.clazz == 4) {
            return null;
        }
        if (skill.type == SkillConfig.TYPE_ATTACK) {
            return new String[] { "����", "��Ҽ���", "�˺�", "���ô���", "Ч��", "���˺�", "ɱ����Ҫ����", "��ɱ����" };
        } else if (skill.type == SkillConfig.TYPE_AID) {
            return new String[] { "����", "��Ҽ���", "����", "���ô���", "Ч��", "������", "��������Ѫ����" };
        } else {
            return null;
        }
    }
    
    /**
     * ȡ��ĳ�����ܼ����ָ��������
     * @param level
     * @return
     */
    public float getParamValue(int skillLevel, int paramIndex) {
        int reqLevel = skill.requireLevel[skillLevel];
        float smp = skill.mp[skillLevel];
        if (smp < 0) {
            smp = (-smp) * reqLevel;
        }
        if (skill.type == SkillConfig.TYPE_ATTACK) {
            switch (paramIndex) {
            case 0:
                 return skillLevel + 1;
            case 1:
                return reqLevel;
            case 2: // �˺�
                return getSkillDamage(skillLevel);
            case 3: // ���ô���
                return getStandardMP(reqLevel, skill.clazz) / smp;
            case 4: // Ч��
                return getSkillDamage(skillLevel) / smp;
            case 5: // ���˺�
                return getStandardMP(reqLevel, skill.clazz) * getSkillDamage(skillLevel) / smp;
            case 6: // ɱ����Ҫ����
                return getStandardHP(reqLevel, 0) / getSkillDamage(skillLevel);
            case 7: // ��ɱ����
                return getStandardMP(reqLevel, skill.clazz) * getSkillDamage(skillLevel) / smp / getMonsterHP(reqLevel);
            }
        } else if (skill.type == SkillConfig.TYPE_AID) {
            switch (paramIndex) {
            case 0:
                 return skillLevel + 1;
            case 1:
                return reqLevel;
            case 2: // ����
                return getSkillHeal(skillLevel);
            case 3: // ���ô���
                return getStandardMP(reqLevel, skill.clazz) / smp;
            case 4: // Ч��
                return getSkillHeal(skillLevel) / smp;
            case 5: // ������
                return getStandardMP(reqLevel, skill.clazz) * getSkillHeal(skillLevel) / smp;
            case 6: // ��������Ѫ����
                return getStandardMP(reqLevel, skill.clazz) * getSkillHeal(skillLevel) / smp / getStandardHP(reqLevel, 0);
            }
        }
        return 0.0f;
    }
    
    private float getSkillDamage(int skillLevel) {
        float ret = 0;
        switch (skill.damageType) {
        case SkillConfig.DAMAGE_PHYSICAL:
            ret = getStandardAttack(skill.requireLevel[skillLevel], skill.clazz);
            break;
        case SkillConfig.DAMAGE_MAGIC:
            ret = getStandardMagicAttack(skill.requireLevel[skillLevel], skill.clazz);
            break;
        }
        for (EffectConfig eff : skill.effects.getAllEffects()) {
            ret = AnalyzeSkill.process(eff, ret, skillLevel);
        }
        for (EffectConfig eff : skill.effects.getAllEffects()) {
            switch (eff.getType()) {
            case EffectConfig.TWO_HIT_ON_HIT:
                ret *= 3;
                break;
            }
        }
        
        // �����BUFF������BUFF�˺�
        for (EffectConfig eff : skill.effects.getAllEffects()) {
            switch (eff.getType()) {
            case EffectConfig.ADD_DEBUFF_ON_HIT: {
                float rate = ((float[])eff.getParam(0))[skillLevel];
                int buffid = ((int[])eff.getParam(2))[skillLevel];
                int bufflvl = ((int[])eff.getParam(3))[skillLevel];
                BuffConfig bc = (BuffConfig)ProjectData.getActiveProject().findObject(BuffConfig.class, buffid);
                ret += getBuffDamage(bc, bufflvl - 1) * rate / 100.0f;
                break;
            }
            }
        }
        return ret;
    }
    
    private float getBuffDamage(BuffConfig bc, int level) {
        float ret = 0;
        for (EffectConfig eff : bc.effects.getAllEffects()) {
            if (eff.getType() == EffectConfig.D_O_T) {
                ret += ((int[])eff.getParam(2))[level];
            }
        }
        return ret;
    }
    
    private float getSkillHeal(int skillLevel) {
        float ret = this.getStandardHeal(skill.requireLevel[skillLevel], skill.clazz);
        if (skill.targetType == SkillConfig.TARGET_AREA || skill.targetType == SkillConfig.TARGET_AROUND) {
            ret = ret * 2 / 3;
        }
        for (EffectConfig eff : skill.effects.getAllEffects()) {
            switch (eff.getType()) {
            case EffectConfig.CURE_TARGET:
                ret = ((int[])eff.getParam(0))[skillLevel];
                break;
            }
        }
        
        // �����BUFF������BUFF����
        for (EffectConfig eff : skill.effects.getAllEffects()) {
            switch (eff.getType()) {
            case EffectConfig.ADD_DEBUFF_ON_HIT: {
                float rate = ((float[])eff.getParam(0))[skillLevel];
                int buffid = ((int[])eff.getParam(2))[skillLevel];
                int bufflvl = ((int[])eff.getParam(3))[skillLevel];
                BuffConfig bc = (BuffConfig)ProjectData.getActiveProject().findObject(BuffConfig.class, buffid);
                ret += getBuffHeal(bc, bufflvl - 1) * rate / 100.0f;
                break;
            }
            }
        }
        return ret;
    }
    
    private float getBuffHeal(BuffConfig bc, int level) {
        float ret = 0;
        for (EffectConfig eff : bc.effects.getAllEffects()) {
            if (eff.getType() == EffectConfig.H_O_T) {
                ret += ((int[])eff.getParam(2))[level];
            }
        }
        return ret;
    }
    
    /*
     * ����װ����������������ȫ��װ����
     */
    private int getEquipmentAttrCount(int level) {
        return (int)((level + 5) * 1.11 * 0.2 * 720 * 0.6 / 50);
    }
    
    /*
     * ����ĳ��������ҵı�׼�������ԣ�ȫ��װ����
     */
    private int getStandardSTR(int level, int clazz) {
        int base = 0;
        int attrCount = (level - 1) * 2;        // �ɼ����Ե�
        int equAttrCount = getEquipmentAttrCount(level);  // װ���������Ե�
        switch (clazz) {
        case 0:
            // �����佫��һ������ �ӵ�����
            return base + (attrCount + equAttrCount) / 2;
        case 1:
            // ����̿͵�20%���Լӵ�����
            return base + (attrCount + equAttrCount) * 2 / 10;
        case 2:
            // ����ıʿ��������
            return base;
        case 3:
            // ���跽ʿ��������
            return base;
        default:
            return 0;
        }
    }

    /*
     * ����ĳ��������ҵı�׼�������ԣ�ȫ��װ����
     */
    private int getStandardAGI(int level, int clazz) {
        int base = 0;
        int attrCount = (level - 1) * 2;        // �ɼ����Ե�
        int equAttrCount = getEquipmentAttrCount(level);  // װ���������Ե�
        switch (clazz) {
        case 0:
            // �����佫��������
            return base;
        case 1:
            // ����̿͵�40%���Լӵ�����
            return base + (attrCount + equAttrCount) * 4 / 10;
        case 2:
            // ����ıʿ��������
            return base;
        case 3:
            // ���跽ʿ��������
            return base;
        default:
            return 0;
        }
    }

    /*
     * ����ĳ��������ҵı�׼�������ԣ�ȫ��װ����
     */
    private int getStandardINT(int level, int clazz) {
        int base = 0;
        int attrCount = (level - 1) * 2;        // �ɼ����Ե�
        int equAttrCount = getEquipmentAttrCount(level);  // װ���������Ե�
        switch (clazz) {
        case 0:
            // �����佫��������
            return base;
        case 1:
            // ����̿Ͳ�������
            return base;
        case 2:
            // ����ıʿ��50%���Լӵ�����
            return base + (attrCount + equAttrCount) / 2;
        case 3:
            // ���跽ʿ��50%���Լӵ�����
            return base + (attrCount + equAttrCount) / 2;
        default:
            return 0;
        }
    }

    /*
     * ����ĳ��������ҵı�׼�������ԣ�ȫ��װ����
     */
    private int getStandardSTA(int level, int clazz) {
        int base = 0;
        int attrCount = (level - 1) * 2;        // �ɼ����Ե�
        int equAttrCount = getEquipmentAttrCount(level);  // װ���������Ե�
        switch (clazz) {
        case 0:
            // �����佫��50%���Լӵ�����
            return base + (attrCount + equAttrCount) / 2;
        case 1:
            // ����̿͵�40%���Լӵ�����
            return base + (attrCount + equAttrCount) * 4 / 10;
        case 2:
            // ����ıʿ��50%���Լӵ�����
            return base + (attrCount + equAttrCount) / 2;
        case 3:
            // ���跽ʿ��50%���Լӵ�����
            return base + (attrCount + equAttrCount) / 2;
        default:
            return 0;
        }
    }

    /*
     * ����ĳ��������ҵı�׼�������ޣ�ȫ��װ����
     */
    private int getStandardHP(int level, int clazz) {
        int sta = getStandardSTA(level, clazz);
        if (clazz == 0) {
            return sta * 16 + level * 15;
        } else if (clazz == 1) {
            return sta * 13 + level * 15;
        } else if (clazz == 2) {
            return sta * 13 + level * 15;
        } else {
            return sta * 13 + level * 15;
        }
    }

    /*
     * ����ĳ��������ҵı�׼�������ޣ�ȫ��װ����
     */
    private int getStandardMP(int level, int clazz) {
        switch (clazz) {
        case 0:
        case 1:
            return getStandardSTR(level, clazz) * 6 + level * 15;
        case 2:
        case 3:
            return getStandardINT(level, clazz) * 6 + level * 15;
        default:
            return 0;
        }
    }
    
    /*
     * ����ĳ������Ĺ���Ѫ����
     */
    private int getMonsterHP(int level) {
        return level * 24 + 30;
    }
    
    /*
     * ����ĳ���������ҵ�����������ȫ��װ����
     */
    private float getStandardAttack(int level, int clazz) {
        float patk = level * 10;
        switch (clazz) {
        case 0:
            return getStandardSTR(level, clazz) + patk;
        case 1:
            return getStandardAGI(level, clazz) + getStandardSTR(level, clazz) / 4 + patk;
        case 2:
            return getStandardSTR(level, clazz) / 2 + patk;
        case 3:
            return getStandardSTR(level, clazz) / 2 + patk;
        default:
            return 0;
        }
    }
    
    /*
     * ����ĳ���������ҵķ�����������ȫ��װ����
     */
    private float getStandardMagicAttack(int level, int clazz) {
        float matk = level * 10;
        switch (clazz) {
        case 0:
            return matk;
        case 1:
            return matk;
        case 2:
            return getStandardINT(level, clazz) + matk;
        case 3:
            return getStandardINT(level, clazz) + matk;
        default:
            return 0;
        }
    }
    
    /*
     * ����ĳ��������ҵķ������ơ�
     */
    private float getStandardHeal(int level, int clazz) {
        float matk = level * 10;
        return matk;
    }
}
