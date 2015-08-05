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
 * 本类用来处理技能描述/BUFF描述的转换。描述文本中可以用${}格式的变量来访问技能/BUFF属性。例如，${a3}表示技能/BUFF的第3个参数。
 * 如果技能/BUFF的效果引用了另外的BUFF，要访问引用的BUFF的属性，可以用组合变量，例如：${a3.a7}表示第3个参数引用的BUFF的第7个参数。
 * 如果技能是被动技能，可以用ab来引用这个被动技能对应的buff。
 * 每个变量后面可以有一个字符来表示显示格式：%,t或T。%表示百分比,t表示毫秒,T表示秒。
 * 
 * 2011/6/8 增加属性计算的功能，可以用变量和人物属性进行计算得到数值加入描述中，例如：${a2+a3}或${minpattack+a3*str}。
 * 
 * @author lighthu
 */
public class DescriptionPattern {
    // 本描述来自的buff
    protected BuffConfig buff;
    // 本描述来自的技能（和buff成员互斥，只能有一个）
    protected SkillConfig skill;
    // 技能或buff的属性集合（包括基本属性）
    public EffectConfigSet attrs;
    // 描述文本
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
     * 分析一个描述文本中是否有变量。
     * @param pattern 描述文本
     * @return 返回描述文本的分段结果，如果有1个变量在中间，那么结果中应该有3段。
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
     * 分析一个变量中是否有四则运算。
     * @param var 变量文字
     * @return 返回分段结果，变量和运算符交替
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
     * 生成某一个等级的技能/BUFF的描述文本。
     * 注：本方法不支持属性组合计算，对于这种情况用<计算>代替计算结果。
     * @param level 技能/BUFF等级
     * @return 把变量计算成实际的值后的文本。
     */
    public String generate(int level) {
        StringBuffer buf = new StringBuffer();
        String[] secs = splitPattern(pattern);
        for (int i = 0; i < secs.length; i++) {
            if ((i & 1) == 0) {
                buf.append(secs[i]);
            } else if (splitExpr(secs[i]).length > 1) {
                // 核查每个部分是否都正确
                try {
                    varToCode(secs[i]);
                    buf.append("<计算>");
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
     * 把一个变量转换为实际值。
     * 注：本方法不支持属性组合计算，对于这种情况用<计算>代替计算结果。
     * @param token 变量字符串
     * @param type 数据类型（0-普通，1-百分比，2-毫秒，3-秒）
     * @param level 技能/BUFF等级
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
            // 百分比
            float f;
            if (value instanceof Float) {
                f = Math.abs(((Float)value).floatValue());
            } else {
                f = Math.abs(((Integer)value).floatValue());
            }
            return formatPercent(f);
        } else if (type == 2) {
            // 毫秒
            int sec = Math.abs(((Integer)value).intValue());
            return formatMillSecond(sec);
        } else if (type == 3) {
            // 秒
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
     * 生成用于计算变量值的java代码。
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
     * 变量转换为Java代码（Buff版本）。
     */
    protected String buffVarToCode(String varName) {
        StringBuilder sb = new StringBuilder();
        String[] secs = Utils.splitString(varName, '.');
        
        // 只有一节的需要特殊处理，${a1}表示剩余时间，${an}如果指向hot的时间字段，则表示剩余秒数，其他的直接指向局部变量
        if (secs.length == 1) {
            if (!secs[0].startsWith("a")) {
                return specialVarToCode(secs[0], true);
            }
            int index = Integer.parseInt(secs[0].substring(1)) - 1;
            EffectParamRef pr = attrs.getParamAt(index);
            return BuffConfig.getFieldName(pr.effect, pr.index, pr.effectID, false);
        }
        
        // 如果有多节，找到最后一节的定义
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
     * 变量转换为Java代码（Skill版本）。
     */
    protected String skillVarToCode(String varName) {
        String[] secs = Utils.splitString(varName, '.');
        
        // 只有一节的需要特殊处理，${a1}表示剩余时间，${an}如果指向hot的时间字段，则表示剩余秒数，其他的直接指向局部变量
        if (secs.length == 1) {
            if (!secs[0].startsWith("a")) {
                return specialVarToCode(secs[0], false);
            }
            int index = Integer.parseInt(secs[0].substring(1)) - 1;
            EffectParamRef pr = attrs.getParamAt(index);
            if (pr.effect.getType() == -1) {
                // 基本属性
                String name = pr.getParamName();
                if (name.equals("学习级别")) {
                    return "getRequireLevel()";
                } else if (name.equals("消耗MP")) {
                    return "getMP(owner)";
                } else if (name.equals("施法时间(毫秒)")) {
                    return "getActTime(owner)";
                } else if (name.equals("CD(毫秒)")) {
                    return "getCDTime(owner)";
                } else if (name.equals("有效半径(码)")) {
                    return "getRange(owner) / 8.0f";
                } else if (name.equals("有效距离(码)")) {
                    return "getDistance(owner) / 8.0f";
                } else {
                    throw new IllegalArgumentException();
                }
            } else {
                // 效果属性
                return BuffConfig.getFieldName(pr.effect, pr.index, pr.effectID, false);
            }
        }
        
        // 如果有多节，找到最后一节的定义
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
                    System.out.println("出错技能为:" + skill.id + ":"+ skill.title);
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
     * 特殊变量转换为代码。
     */
    protected String specialVarToCode(String name, boolean isBuff) {
        // 处理纯数字
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
            return sec + "秒";
        } else if (sec < 3600) {
            return (sec / 60) + "分" + (sec % 60) + "秒";
        } else {
            return (sec / 3600) + "小时" + ((sec % 3600) / 60) + "分";
        }
    }
    
    public static String formatMillSecond(int ms) {
        int sec = ms / 1000;;
        float sec2 = ms / 1000.0f;
        if (sec < 60) {
            return formatFloat(sec2) + "秒";
        } else if (sec < 3600) {
            return (sec / 60) + "分" + (sec % 60) + "秒";
        } else {
            return (sec / 3600) + "小时" + ((sec % 3600) / 60) + "分";
        }
    }
}
