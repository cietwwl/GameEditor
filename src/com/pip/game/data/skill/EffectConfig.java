package com.pip.game.data.skill;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.IllegalDataException;

import com.pip.game.data.ProjectData;
import com.pip.game.data.autocoding.AutoEffectFromPreDefined;
import com.pip.game.data.effects0.EffectRejectException;
import com.pip.game.data.effectsListTest.ListClasses;
import com.pip.game.editor.skill.ParamIndicator;
import com.pip.util.Utils;

/**
 * 技能/BUFF效果配置。
 */
public abstract class EffectConfig implements Cloneable {
    // 不要写值为0 的常量 jhkang
    public static final int CHANGE_MAGIC_AP = 1;
    public static final int CHANGE_WEAPON_ATK = 2;
    public static final int CHANGE_WEAPON_MATK = 3;
    public static final int CHANGE_THREAT = 4;
    public static final int CHANGE_ARMOR = 5;
    public static final int CHANGE_PHYSICAL_HIT = 6;
    public static final int CHANGE_PHYSICAL_CRIT = 7;
    public static final int CHANGE_PHYSICAL_DODGE = 8;
    public static final int CHANGE_MAGIC_CRIT = 9;
    public static final int CHANGE_MP_RENEW = 10;
    public static final int CHANGE_HP_RENEW = 11;
    public static final int CHANGE_SPEED = 12;
    public static final int CHANGE_MAXHP = 13;
    public static final int CHANGE_CURE_EFFECT = 14;
    public static final int APPEND_MAGIC_DAMAGE = 15;
    public static final int IGNORE_ARMOR = 16;
    public static final int ADD_MP_ON_HIT = 17;
    public static final int ADD_DEBUFF_ON_HIT = 18;
    public static final int ADD_BUFF_ON_HIT = 19;
    public static final int FIRST_THREAT_ON_HIT = 20;
    public static final int FEAR_ON_HIT = 21;
    public static final int SLOW_ON_HIT = 22;
    public static final int PARALYZE_ON_HIT = 23;
    public static final int STAY_ON_HIT = 24;
    public static final int REPEAT_ON_HIT = 25;
    public static final int DOUBLE_DAMAGE_ON_HIT = 26;
    public static final int DEC_MP_ON_HIT = 27;
    public static final int ADD_HP_ON_HIT = 28;
    public static final int TWO_HIT_ON_HIT = 29;
    public static final int RELIVE_TARGET = 30;
    public static final int CURE_TARGET = 31;
    public static final int IMMUNE_PHYICAL_ATTACK = 32;
    public static final int IMMUNE_MAGIC_ATTACK = 33;
    public static final int IMMUNE_SLOW_ATTACK = 34;
    public static final int COUNTER_ATTACK = 35;
    public static final int BOUNCE = 36;
    public static final int CHANGE_MP_USE = 37;
    public static final int SET_VARIABLE = 38;
    public static final int H_O_T = 39;
    public static final int D_O_T = 40;
    public static final int MP_SHIELD = 41;
    public static final int VAMPIRE_ON_HIT = 42;
    public static final int CHANGE_CD_TIME = 43;
    public static final int CHANGE_DISTANCE = 44;
    public static final int CHANGE_ACT_TIME = 45;
    public static final int CHANGE_RANGE = 46;
    public static final int CURE_TARGET_IGNORE_MAX = 47;
    public static final int CHANGE_MAGIC_ARMOR = 48;
    public static final int IGNORE_MAGIC_ARMOR = 49;
    public static final int REDUCE_PHYSICAL_DAMAGE = 50;
    public static final int REDUCE_MAGIC_DAMAGE = 51;
    public static final int CHANGE_MAGIC_HIT = 52;
    public static final int CHANGE_MAGIC_DODGE = 53;
    public static final int CANNOT_MOVE = 54;
    public static final int MPHOT = 55;
    public static final int MPDOT = 56;
    public static final int DISPEL_BUFF = 57;
    public static final int DISPEL_ALL_BUFF = 58;
    public static final int DISPEL_DEBUFF = 59;
    public static final int DISPEL_ALL_DEBUFF = 60;
    public static final int CHANGE_STA = 61;
    public static final int CHANGE_AGI = 62;
    public static final int CHANGE_STR = 63;
    public static final int CHANGE_INT = 64;
    public static final int HP_ACTIVE_BUFF = 65;
    public static final int CRIT_ACTIVE_BUFF = 66;
    public static final int CRITED_ACTIVE_BUFF = 67;
    public static final int DUMB_ON_HIT = 68;
    public static final int IMMUNE_FEAR = 69;
    public static final int IMMUNE_DUMB = 70;
    public static final int IMMUNE_PARALYZE = 71;
    public static final int IMMUNE_STAY = 72;
    public static final int CHANGE_BASIC_MAGIC_AP = 73;
    public static final int CHANGE_BASIC_HP = 74;
    public static final int CHANGE_BASIC_MP = 75;
    public static final int LIMIT_EFFECT_TIMES = 76;
    public static final int CHANGE_MAGIC_HEAL = 77;
    public static final int LIMIT_SKILL = 78;
    public static final int CHANGE_BATTLE_PHYICAL_AP = 79;
    public static final int CHANGE_BATTLE_MAGIC_AP = 80;
    public static final int CHANGE_BATTLE_WEAPON_ATK = 81;
    public static final int CHANGE_BATTLE_WEAPON_MATK = 82;
    public static final int CHANGE_BATTLE_PHYSICAL_HIT = 83;
    public static final int CHANGE_BATTLE_PHYSICAL_CRIT = 84;
    public static final int CHANGE_BATTLE_PHYSICAL_DODGE = 85;
    public static final int CHANGE_BATTLE_MAGIC_CRIT = 86;
    public static final int CHANGE_BATTLE_MAGIC_HIT = 87;
    public static final int CHANGE_BATTLE_MAGIC_DODGE = 88;
    public static final int CHANGE_BATTLE_PHYSICAL_CRITED = 89;
    public static final int CHANGE_BATTLE_MAGIC_CRITED = 90;
    public static final int CHANGE_BATTLE_ARMOR = 91;
    public static final int CHANGE_BATTLE_MAGIC_ARMOR = 92;
    public static final int CHANGE_EXP_RATE = 93;
    public static final int CHANGE_MONEY_RATE = 94;
    public static final int CHANGE_PHYSICAL_HIT_RATE = 95;
    public static final int CHANGE_PHYSICAL_CRIT_RATE = 96;
    public static final int CHANGE_PHYSICAL_DODGE_RATE = 97;
    public static final int CHANGE_MAGIC_CRIT_RATE = 98;
    public static final int CHANGE_MAGIC_HIT_RATE = 99;
    public static final int CHANGE_MAGIC_DODGE_RATE = 100;
    public static final int CHANGE_HORSE_EXP_RATE = 101;
    public static final int INTERRUPT = 102;
    public static final int ADD_DEBUFF_ON_HITED = 103;
    public static final int ADD_BUFF_ON_HITED = 104;
    public static final int CHANGE_PARAM = 105;
    public static final int CHANGE_THREAT_TOTAL = 106;
    public static final int FIRST_THREAT_TEMP = 107;
    public static final int TRANSPORT_TO_ME = 108;
    public static final int TRANSPORT_TO_POS = 109;
    public static final int REMOVE_ON_BATTLE_END = 110;
    public static final int SLOW_ON_HITED = 111;
    public static final int IMMUNE_BREAKATTACK = 112;
    public static final int CHANGE_PHYICAL_AP = 113;
    public static final int CHANGE_STATE = 114;
    public static final int USE_SKILL = 115;
    public static final int IMMUNE_SKILL = 116;
    public static final int APPEND_DAMAGE_PHYSICAL = 117;
    public static final int APPEND_DAMAGE_MAGIC = 118;

    protected EffectConfigManager manager = ProjectData.getActiveProject().effectConfigManager;
    
    /**
     * 临时保存这个效果在效果集合中的引用ID。
     */
    public transient int effectID;
    
    /**
     * 设置级别数量
     */
    public abstract void setLevelCount(int max);

    /**
     * 取得效果类型ID
     */
    public int getType() {
        return manager.CLASS_CONFIGS.get(this.getClass()).id;
    }

    /**
     * 取得效果名称
     */
    public String getTypeName() {
        try {
            return manager.CLASS_CONFIGS.get(this.getClass()).typeNames[0];
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "出错了...";
    }

    /**
     * 取得效果简称
     */
    public String getShortName() {
        try {
            return manager.CLASS_CONFIGS.get(this.getClass()).typeNames[1];
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "出错了...";
    }

    /**
     * 取得参数个数
     */
    public abstract int getParamCount();

    /**
     * 取得参数的名字
     */
    public abstract String getParamName(int index);

    /**
     * 取得参数的类型。
     * 
     * @return 可能是Integer, Float或String
     */
    public abstract Class getParamClass(int index);

    /**
     * 取得某个参数各级别的参数值
     * 
     * @return 可能是int[], float[]或String[]
     */
    public abstract Object getParam(int index);

    /**
     * 克隆。
     */
    public Object clone() {
        try {
            return super.clone();
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * 从XML中载入效果。
     * 
     * @param elem
     * @return
     */
    public void load(Element elem, int maxLevel) throws Exception {
        List list = elem.getChildren("param");
        if (list.size() != getParamCount()) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < getParamCount(); i++) {
            Element paramElem = (Element) list.get(i);
            Object param = getParam(i);
            if (param instanceof int[]) {
                int[] arr = Utils.stringToIntArray(paramElem.getText(), ';');
                System.arraycopy(arr, 0, (int[]) param, 0, maxLevel);
            }
            else if (param instanceof float[]) {
                float[] arr = Utils.stringToFloatArray(paramElem.getText(), ';');
                System.arraycopy(arr, 0, (float[]) param, 0, maxLevel);
            }
            else if (param instanceof String[]) {
                String[] arr = Utils.stringToStringArray(paramElem.getText(), ';');
                System.arraycopy(arr, 0, (String[]) param, 0, maxLevel);
            }
            else if (param instanceof ParamIndicator[]) {
                String[] arr = Utils.stringToStringArray(paramElem.getText(), ';');
                ParamIndicator[] arr2 = (ParamIndicator[]) param;
                for (int j = 0; j < maxLevel; j++) {
                    arr2[j] = new ParamIndicator();
                    arr2[j].load(arr[j]);
                }
            }
            else if (param instanceof int[][]) {
                String[] arr = Utils.stringToStringArray(paramElem.getText(), ';');
                int[][] arr2 = (int[][]) param;
                for (int j = 0; j < maxLevel; j++) {
                    arr2[j] = Utils.stringToIntArray(arr[j], ',');
                }
            }
        }
    }

    /**
     * @return只存放效果的id
     */
    public Element savaId() {
        Element ret = new Element("effectStatus");
        ret.addAttribute("id", String.valueOf(getType()));
        return ret;
    }

    /**
     * 把效果保存到XML中。
     * 
     * @return
     */
    public Element save() {
        Element ret = new Element("effect");
        ret.addAttribute("type", String.valueOf(getType()));
        for (int i = 0; i < getParamCount(); i++) {
            Object param = getParam(i);
            Element paramElem = new Element("param");
            if (param instanceof int[]) {
                int[] ia = (int[]) param;
                paramElem.setText(Utils.intArrayToString(ia, ';'));
            }
            else if (param instanceof float[]) {
                float[] fa = (float[]) param;
                paramElem.setText(Utils.floatArrayToString(fa, ';'));
            }
            else if (param instanceof String[]) {
                String[] sa = (String[]) param;
                paramElem.setText(Utils.stringArrayToString(sa, ';'));
            }
            else if (param instanceof ParamIndicator[]) {
                ParamIndicator[] arr2 = (ParamIndicator[]) param;
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < arr2.length; j++) {
                    if (j > 0) {
                        sb.append(';');
                    }
                    sb.append(arr2[j].toString());
                }
                paramElem.setText(sb.toString());
            }
            else if (param instanceof int[][]) {
                int[][] arr2 = (int[][]) param;
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < arr2.length; j++) {
                    if (j > 0) {
                        sb.append(';');
                    }
                    sb.append(Utils.intArrayToString(arr2[j], ','));
                }
                paramElem.setText(sb.toString());
            }
            ret.addContent(paramElem);
        }
        return ret;
    }

    public int getSkillDamage(int curValue, int level) {
        return 0;
    }
    
    public void genBuffPreHit(PrintWriter out, String p1, String p2, String p3, String p4, String p5, String p6) {
    }
    
    public void genBuffEnhance(PrintWriter out, String p1, String p2) {
    }
    
    public void genBuffFinished(PrintWriter out, String p1, String p2, String p3, String p4, String p5, String p6) {

    }

    public void genBuffPostDamage(PrintWriter out, String p1, String p2, String p3) {

    }

    public void genBuffPostHit(PrintWriter out, String p1, String p2) {

    }

    public void genBuffPreHit(PrintWriter out, String p1, String p2) {

    }

    public void genBuffPreDamage(PrintWriter out, String p1, String p2, String p3) {

    }
    
    public void genSkillFinished(PrintWriter out, String p1, String p2, String p3, String p4, String p5, String p6) {

    }

    public void genSkillPostDamage(int damageType, PrintWriter out, String p1) {

    }

    public void genSkillPreDamage(PrintWriter out, int damageType, String p1, String p2) throws EffectRejectException {

    }
    
    public void genSkillPreHit(PrintWriter out, int damageType, String p1) {

    }
    
    public void genSkillPostHit(PrintWriter out, int damageType, String p1,String p2) {

    }

    public abstract String getJavaInterface() throws Exception;
    
    public void genSkillPreHit(PrintWriter out, int damageType, String []param) {

    }
    
    public void genSkillPostHit(PrintWriter out, int damageType, String []param) {

    }
    
    public void genSkillPreDamage(PrintWriter out, int damageType, String[] param) throws EffectRejectException {
        
    }
    
    public void genSkillPostDamage(int damageType, PrintWriter out, String []param) {
        
    }
    
    public void genSkillFinished(PrintWriter out, String []param) {
        
    }
    
    public void genBuffPreHit(PrintWriter out, String[] param) {
        
    }
    
    public void genBuffPostHit(PrintWriter out, String[] param){
        
    }
    
    public void genBuffPreDamage(PrintWriter out, String[] param) {
        
    }
    
    public void genBuffPostDamage(PrintWriter out, String param[]) {
        
    }
    
    public void genBuffFinished(PrintWriter out, String[] param) {
        
    }
    
    public void genBuffEnhance(PrintWriter out, String[] param){
        
    }
    
    /**
     * 生成需要加入游戏循环的工作接口。(update)
     */
    public void genUpdate(PrintWriter out) {

    }

    public void genCustomerFields(PrintWriter out) {

    }

    public void genConstructorCodes(PrintWriter out) {

    }

    /**
     * 诸如limitSkill这样的效果,会在执行链中做条件判断,产出return语句;<br/> 这样的语句放在其他效果执行本链之前<br/>
     * 限制技能的效果在所有链中都是同样的判断,以后可能某些效果在不同链中有不同的判断.赞不支持<br/> 判断条件可能有先后关系,也没有考虑
     * 
     * @param out
     */
    public void genBreakCurChainCondition(PrintWriter out) {

    }
}
