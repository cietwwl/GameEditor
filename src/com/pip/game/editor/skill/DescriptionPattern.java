package com.pip.game.editor.skill;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.pip.game.data.ProjectData;
import com.pip.game.data.skill.BuffConfig;
import com.pip.game.data.skill.DynamicGeneralConfig;
import com.pip.game.data.skill.EffectConfig;
import com.pip.game.data.skill.EffectConfigManager;
import com.pip.game.data.skill.EffectConfigSet;
import com.pip.game.data.skill.EffectParamRef;
import com.pip.game.data.skill.EquipGeneralConfig;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.StaticGeneralConfig;
import com.pip.game.editor.EditorApplication;
import com.pip.util.Utils;

/**
 * ������������������/BUFF������ת���������ı��п�����${}��ʽ�ı��������ʼ���/BUFF���ԡ����磬${a3}��ʾ����/BUFF�ĵ�3��������
 * �������/BUFF��Ч�������������BUFF��Ҫ�������õ�BUFF�����ԣ���������ϱ��������磺${a3.a7}��ʾ��3���������õ�BUFF�ĵ�7��������
 * ��������Ǳ������ܣ�������ab����������������ܶ�Ӧ��buff��
 * ÿ���������������һ���ַ�����ʾ��ʾ��ʽ��%,t��T��%��ʾ�ٷֱ�,t��ʾ����,T��ʾ�롣
 * 
 * 2011/6/8 �������Լ���Ĺ��ܣ������ñ������������Խ��м���õ���ֵ���������У����磺${a2+a3}��${minpattack+a3*str}��
 * 
 * @author lighthu
 */
public class DescriptionPattern {
    // ���������Ե�buff
    protected BuffConfig buff;
    // ���������Եļ��ܣ���buff��Ա���⣬ֻ����һ����
    protected SkillConfig skill;
    // ���ܻ�buff�����Լ��ϣ������������ԣ�
    public EffectConfigSet attrs;
    // �����ı�
    protected String pattern;
    
    protected static String opchars = "+-*/()";
    
    public DescriptionPattern(BuffConfig buff) {
        this.buff = buff;
        pattern = buff.description;
        attrs = new EffectConfigSet();
        attrs.setLevelCount(buff.effects.getLevelCount());
        attrs.addGeneralEffect(buff.getGeneralConfig());
        EffectConfigManager mgr = ProjectData.getActiveProject().effectConfigManager;
        for (EffectConfig eff : buff.effects.getAllEffects()) {
            if(eff.getType() != mgr.getTypeId(DynamicGeneralConfig.class) && 
                    eff.getType() != mgr.getTypeId(StaticGeneralConfig.class) &&
                    eff.getType() != mgr.getTypeId(EquipGeneralConfig.class)) {
                attrs.addEffect(eff);
            }
        }
    }
    
    public DescriptionPattern(SkillConfig skill) {
        this.skill = skill;
        pattern = skill.description;
        attrs = new EffectConfigSet();
        attrs.setLevelCount(skill.effects.getLevelCount());
        attrs.addGeneralEffect(skill.getGeneralConfig());
        EffectConfigManager mgr = ProjectData.getActiveProject().effectConfigManager;
        for (EffectConfig eff : skill.effects.getAllEffects()) {
            if(eff.getType() != mgr.getTypeId(DynamicGeneralConfig.class) && 
                    eff.getType() != mgr.getTypeId(StaticGeneralConfig.class) &&
                    eff.getType() != mgr.getTypeId(EquipGeneralConfig.class)) {
                attrs.addEffect(eff);
            }
        }
    }
    
    /**
     * ����һ�������ı����Ƿ��б�����
     * @param pattern �����ı�
     * @return ���������ı��ķֶν���������1���������м䣬��ô�����Ӧ����3�Ρ�
     */
    public static String[] splitPattern(String pattern) {
        List<String> ret = new ArrayList<String>();
        int start = 0;
        while (true) {
            int cur = pattern.indexOf("${", start);
            if (cur == -1) {
                ret.add(pattern.substring(start));
                break;
            }
            int next = pattern.indexOf('}', cur);
            if (next == -1) {
                ret.add(pattern.substring(start));
                break;
            }
            ret.add(pattern.substring(start, cur));
            String token = pattern.substring(cur + 2, next);
            start = next + 1;
            if (start < pattern.length()) {
                char ch = pattern.charAt(start);
                if (ch == '%') {
                    token += '%';
                    start++;
                } else if (ch == 't') {
                    token += 't';
                    start++;
                } else if (ch == 'T') {
                    token += 'T';
                    start++;
                }
            }
            ret.add(token);
        }
        String[] ret2 = new String[ret.size()];
        ret.toArray(ret2);
        return ret2;
    }
    
    /*
     * ����һ���������Ƿ����������㡣
     * @param var ��������
     * @return ���طֶν�������������������
     */
    protected String[] splitExpr(String var) {
        List<String> ret = new ArrayList<String>();
        int state = 0;
        int tokenStart = 0;
        for (int i = 0; i < var.length(); i++) {
            char ch = var.charAt(i);
            if (state == 0) {
                if (opchars.indexOf(ch) != -1) {
                    if (i > tokenStart) {
                        ret.add(var.substring(tokenStart, i));
                        tokenStart = i;
                    }
                    state = 1;
                }
            } else {
                if (opchars.indexOf(ch) == -1) {
                    if (i > tokenStart) {
                        ret.add(var.substring(tokenStart, i));
                        tokenStart = i;
                    }
                    state = 0;
                }
            }
        }
        if (tokenStart < var.length()) {
            ret.add(var.substring(tokenStart));
        }
        String[] ret2 = new String[ret.size()];
        ret.toArray(ret2);
        return ret2;
    }
    
    /**
     * ����ĳһ���ȼ��ļ���/BUFF�������ı���
     * ע����������֧��������ϼ��㣬�������������<����>�����������
     * @param level ����/BUFF�ȼ�
     * @return �ѱ��������ʵ�ʵ�ֵ����ı���
     */
    public String generate(int level) {
        StringBuffer buf = new StringBuffer();
        String[] secs = splitPattern(pattern);
        for (int i = 0; i < secs.length; i++) {
            if ((i & 1) == 0) {
                buf.append(secs[i]);
            } else if (splitExpr(secs[i]).length > 1) {
                // �˲�ÿ�������Ƿ���ȷ
                try {
                    varToCode(secs[i]);
                    buf.append("<����>");
                } catch (Exception e) {
                    buf.append("error");
                }
            } else {
                try {
                    if (secs[i].endsWith("%")) {
                        buf.append(translateVar(secs[i].substring(0, secs[i].length() - 1), 1, level));
                    } else if (secs[i].endsWith("t")) {
                        buf.append(translateVar(secs[i].substring(0, secs[i].length() - 1), 2, level));
                    } else if (secs[i].endsWith("T")) {
                        buf.append(translateVar(secs[i].substring(0, secs[i].length() - 1), 3, level));
                    } else {
                        buf.append(translateVar(secs[i], 0, level));
                    }
                } catch (Exception e) {
                    buf.append("error");
                }
            }
        }
        return buf.toString();
    }
    
    /*
     * ��һ������ת��Ϊʵ��ֵ��
     * ע����������֧��������ϼ��㣬�������������<����>�����������
     * @param token �����ַ���
     * @param type �������ͣ�0-��ͨ��1-�ٷֱȣ�2-���룬3-�룩
     * @param level ����/BUFF�ȼ�
     */
    private String translateVar(String token, int type, int level) {
        String[] secs = Utils.splitString(token, '.');
        EffectConfigSet curSet = attrs;
        Object value = null;
        for (int i = 0; i < secs.length; i++) {
            String tt = secs[i];
            if (tt.equals("ab")) {
                BuffConfig nextBuff = (BuffConfig)getProjectData().findObject(BuffConfig.class, skill.passiveBuff);
                curSet = new EffectConfigSet();
                curSet.setLevelCount(nextBuff.maxLevel);
                curSet.addEffect(nextBuff.getGeneralConfig());
                EffectConfigManager mgr = ProjectData.getActiveProject().effectConfigManager;
                for (EffectConfig eff : nextBuff.effects.getAllEffects()) {
                    if(eff.getType() != mgr.getTypeId(DynamicGeneralConfig.class) && 
                            eff.getType() != mgr.getTypeId(StaticGeneralConfig.class) &&
                            eff.getType() != mgr.getTypeId(EquipGeneralConfig.class)) {
                        curSet.addEffect(eff);
                    }
                }
            } else {
                int index = Integer.parseInt(tt.substring(1)) - 1;
                EffectParamRef pr = curSet.getParamAt(index);
                if (pr.getParamClass() == BuffConfig.class) {
                    int bid = ((Integer)pr.getParamValue(level)).intValue();
                    BuffConfig nextBuff = (BuffConfig)getProjectData().findObject(BuffConfig.class, bid);
                    curSet = new EffectConfigSet();
                    curSet.setLevelCount(nextBuff.maxLevel);
                    curSet.addEffect(nextBuff.getGeneralConfig());
                    EffectConfigManager mgr = ProjectData.getActiveProject().effectConfigManager;
                    for (EffectConfig eff : nextBuff.effects.getAllEffects()) {
                        if(eff.getType() != mgr.getTypeId(DynamicGeneralConfig.class) && 
                                eff.getType() != mgr.getTypeId(StaticGeneralConfig.class) &&
                                eff.getType() != mgr.getTypeId(EquipGeneralConfig.class)) {
                            curSet.addEffect(eff);
                        }
                    }
                } else {
                    curSet = null;
                    value = pr.getParamValue(level);
                }
            }
        }
        if (type == 0) {
            if (value instanceof Integer) {
                int v = ((Integer)value).intValue();
                return String.valueOf(Math.abs(v));
            } else if (value instanceof Float) {
                float v = ((Float)value).floatValue();
                return formatFloat(Math.abs(v));
            } else {
                return String.valueOf(value);
            }
        } else if (type == 1) {
            // �ٷֱ�
            float f;
            if (value instanceof Float) {
                f = Math.abs(((Float)value).floatValue());
            } else {
                f = Math.abs(((Integer)value).floatValue());
            }
            return formatPercent(f);
        } else if (type == 2) {
            // ����
            int sec = Math.abs(((Integer)value).intValue());
            return formatMillSecond(sec);
        } else if (type == 3) {
            // ��
            int sec = Math.abs(((Integer)value).intValue());
            return formatSecond(sec);
        }
        return null;
    }
    
    protected ProjectData getProjectData(){
        if (buff != null)
            return buff.owner;
        else
            return skill.owner;
    }

    /**
     * �������ڼ������ֵ��java���롣
     * @param varName
     * @return
     */
    public String varToCode(String varName) {
        String[] secs = splitExpr(varName);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < secs.length; i++) {
            if (opchars.indexOf(secs[i].charAt(0)) == -1) {
                if (buff != null) {
                    sb.append(buffVarToCode(secs[i]));
                } else {
                    sb.append(skillVarToCode(secs[i]));
                }
            } else {
                sb.append(secs[i]);
            }
        }
        return sb.toString();
    }
    
    /*
     * ����ת��ΪJava���루Buff�汾����
     */
    protected String buffVarToCode(String varName) {
        StringBuilder sb = new StringBuilder();
        String[] secs = Utils.splitString(varName, '.');
        
        // ֻ��һ�ڵ���Ҫ���⴦��${a1}��ʾʣ��ʱ�䣬${an}���ָ��hot��ʱ���ֶΣ����ʾʣ��������������ֱ��ָ��ֲ�����
        if (secs.length == 1) {
            if (!secs[0].startsWith("a")) {
                return specialVarToCode(secs[0], true);
            }
            int index = Integer.parseInt(secs[0].substring(1)) - 1;
            EffectParamRef pr = attrs.getParamAt(index);
            return BuffConfig.getFieldName(pr.effect, pr.index, pr.effectID, false);
        }
        
        // ����ж�ڣ��ҵ����һ�ڵĶ���
        EffectConfigSet curSet = attrs;
        BuffConfig curBuff = buff;
        EffectParamRef lastLastRef = null;
        EffectParamRef lastRef = null;
        for (int i = 0; i < secs.length; i++) {
            String tt = secs[i];
            int index = Integer.parseInt(tt.substring(1)) - 1;
            EffectParamRef pr = curSet.getParamAt(index);
            if (pr.getParamClass() == BuffConfig.class) {
                int bid = ((Integer)pr.getParamValue(1)).intValue();
                lastLastRef = pr;
                curBuff = (BuffConfig)getProjectData().findObject(BuffConfig.class, bid);
                curSet = new EffectConfigSet();
                curSet.setLevelCount(curBuff.maxLevel);
                curSet.addEffect(curBuff.getGeneralConfig());
                EffectConfigManager mgr = ProjectData.getActiveProject().effectConfigManager;
                for (EffectConfig eff : curBuff.effects.getAllEffects()) {
                    if(eff.getType() != mgr.getTypeId(DynamicGeneralConfig.class) && 
                            eff.getType() != mgr.getTypeId(StaticGeneralConfig.class) &&
                            eff.getType() != mgr.getTypeId(EquipGeneralConfig.class)) {
                        curSet.addEffect(eff);
                    }
                }
            } else {
                curSet = null;
                lastRef = pr;
            }
        }
        String levelVar = BuffConfig.getFieldName(lastLastRef.effect, lastLastRef.index + 1, lastLastRef.effectID, false);
        String fieldName;
        if (lastRef.effect.getType() == -1) {
            fieldName = "DURATION";
        } else {
            fieldName = BuffConfig.getFieldName(lastRef.effect, lastRef.index, lastRef.effectID, true);
        }
        return curBuff.implClass + "." + fieldName + "[" + levelVar + "]";
    }
    
    /*
     * ����ת��ΪJava���루Skill�汾����
     */
    protected String skillVarToCode(String varName) {
        String[] secs = Utils.splitString(varName, '.');
        
        // ֻ��һ�ڵ���Ҫ���⴦��${a1}��ʾʣ��ʱ�䣬${an}���ָ��hot��ʱ���ֶΣ����ʾʣ��������������ֱ��ָ��ֲ�����
        if (secs.length == 1) {
            if (!secs[0].startsWith("a")) {
                return specialVarToCode(secs[0], false);
            }
            int index = Integer.parseInt(secs[0].substring(1)) - 1;
            EffectParamRef pr = attrs.getParamAt(index);
            if (pr.effect.getType() == -1) {
                // ��������
                String name = pr.getParamName();
                if (name.equals("ѧϰ����")) {
                    return "getRequireLevel()";
                } else if (name.equals("����MP")) {
                    return "getMP(owner)";
                } else if (name.equals("ʩ��ʱ��(����)")) {
                    return "getActTime(owner)";
                } else if (name.equals("CD(����)")) {
                    return "getCDTime(owner)";
                } else if (name.equals("��Ч�뾶(��)")) {
                    return "getRange(owner) / 8.0f";
                } else if (name.equals("��Ч����(��)")) {
                    return "getDistance(owner) / 8.0f";
                } else {
                    throw new IllegalArgumentException();
                }
            } else {
                // Ч������
                return BuffConfig.getFieldName(pr.effect, pr.index, pr.effectID, false);
            }
        }
        
        // ����ж�ڣ��ҵ����һ�ڵĶ���
        EffectConfigSet curSet = attrs;
        BuffConfig curBuff = buff;
        EffectParamRef lastLastRef = null;
        EffectParamRef lastRef = null;
        String levelVar = null;
        for (int i = 0; i < secs.length; i++) {
            String tt = secs[i];
            if (tt.equals("ab")) {
                lastLastRef = null;
                curBuff = (BuffConfig)getProjectData().findObject(BuffConfig.class, skill.passiveBuff);
                curSet = new EffectConfigSet();
                if(curBuff == null){
                    System.out.println("������Ϊ:" + skill.id + ":"+ skill.title);
                    return "";
                }else{
                    curSet.setLevelCount(curBuff.maxLevel);
                    curSet.addEffect(curBuff.getGeneralConfig());
                }
                EffectConfigManager mgr = ProjectData.getActiveProject().effectConfigManager;
                for (EffectConfig eff : curBuff.effects.getAllEffects()) {
                    if(eff.getType() != mgr.getTypeId(DynamicGeneralConfig.class) && 
                            eff.getType() != mgr.getTypeId(StaticGeneralConfig.class) &&
                            eff.getType() != mgr.getTypeId(EquipGeneralConfig.class)) {
                        curSet.addEffect(eff);
                    }
                }
                levelVar = "level";
            } else {
                int index = Integer.parseInt(tt.substring(1)) - 1;
                EffectParamRef pr = curSet.getParamAt(index);
                if (pr.getParamClass() == BuffConfig.class) {
                    if (levelVar == null) {
                        EffectParamRef lr = curSet.getParamAt(index + 1);
                        levelVar = BuffConfig.getFieldName(lr.effect, lr.index, lr.effectID, false);
                    }
                    int bid = ((Integer)pr.getParamValue(1)).intValue();
                    lastLastRef = pr;
                    curBuff = (BuffConfig)getProjectData().findObject(BuffConfig.class, bid);
                    curSet = new EffectConfigSet();
                    curSet.setLevelCount(curBuff.maxLevel);
                    curSet.addEffect(curBuff.getGeneralConfig());
                    EffectConfigManager mgr = ProjectData.getActiveProject().effectConfigManager;
                    for (EffectConfig eff : curBuff.effects.getAllEffects()) {
                        if(eff.getType() != mgr.getTypeId(DynamicGeneralConfig.class) && 
                                eff.getType() != mgr.getTypeId(StaticGeneralConfig.class) &&
                                eff.getType() != mgr.getTypeId(EquipGeneralConfig.class)) {
                            curSet.addEffect(eff);
                        }
                    }
                } else {
                    curSet = null;
                    lastRef = pr;
                }
            }
        }
        if (lastLastRef != null) {
            String fieldName;
            if (lastRef.effect.getType() == -1) {
                fieldName = "DURATION";
            } else {
                fieldName = BuffConfig.getFieldName(lastRef.effect, lastRef.index, lastRef.effectID, true);
            }
            return curBuff.implClass + "." + fieldName + "[" + levelVar + "]";
        } else {
            return curBuff.implClass + "." + BuffConfig.getFieldName(lastRef.effect, lastRef.index, lastRef.effectID, true) + "[level]";
        }
    }
    
    /*
     * �������ת��Ϊ���롣
     */
    protected String specialVarToCode(String name, boolean isBuff) {
        // ��������
        try {
            Double.parseDouble(name);
            return name;
        } catch (Exception e) {
        }
        
        DescriptionPatternConfig config = getProjectData().config.descPatternConfig;
        String ret;
        if (isBuff) {
            ret = config.varToBuffCodeMap.get(name);
        } else {
            ret = config.varToSkillCodeMap.get(name);
        }
        if (ret == null) {
            throw new RuntimeException(name + " is not supported.");
        }
        return ret;
    }

    private static final DecimalFormat percentFormat = new DecimalFormat("####.#"); 
    
    public static String formatPercent(double p) {
        return percentFormat.format(p) + "%";
    }

    public static String formatFloat(double p) {
        return percentFormat.format(p);
    }
    
    public static String formatSecond(int sec) {
        if (sec < 60) {
            return sec + "��";
        } else if (sec < 3600) {
            return (sec / 60) + "��" + (sec % 60) + "��";
        } else {
            return (sec / 3600) + "Сʱ" + ((sec % 3600) / 60) + "��";
        }
    }
    
    public static String formatMillSecond(int ms) {
        int sec = ms / 1000;;
        float sec2 = ms / 1000.0f;
        if (sec < 60) {
            return formatFloat(sec2) + "��";
        } else if (sec < 3600) {
            return (sec / 60) + "��" + (sec % 60) + "��";
        } else {
            return (sec / 3600) + "Сʱ" + ((sec % 3600) / 60) + "��";
        }
    }
}
