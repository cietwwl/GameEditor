package com.pip.game.data.skill;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.effects0.EffectRejectException;
import com.pip.game.data.extprop.ExtPropEntries;
import com.pip.game.data.extprop.ExtPropEntry;
import com.pip.game.data.i18n.I18NContext;
import com.pip.game.editor.skill.DescriptionPattern;
import com.pip.util.Utils;

/**
 * 技能配置数据。
 * @author lighthu
 */
public class SkillConfig extends DataObject implements ISkillConfig{
    
    public static final int PRE_SKILL = -1;
    /**
     * 技能类型：主动攻击
     */
    public static final int TYPE_ATTACK = 1;
    /**
     * 技能类型：主动辅助
     */
    public static final int TYPE_AID = 2;
    /**
     * 技能类型：被动
     */
    public static final int TYPE_PASSIVE = 4;
    /**
     * 技能类型：光环
     */
    public static final int TYPE_BUFF = 8;
    /**
     * 技能类型：复活
     */
    public static final int TYPE_RELIVE = 16;

    public static final int DAMAGE_PHYSICAL = 0;  // 物理伤害
    public static final int DAMAGE_MAGIC = 1;    // 法术伤害
    public static final int DAMAGE_DECMP = 2;   // 抽蓝
    public static final int DAMAGE_DEBUFF = 3;  // 加物理DEBUFF
    public static final int DAMAGE_HEAL = 4;   // 治疗
    public static final int DAMAGE_ADDMP = 5;   // 回蓝
    public static final int DAMAGE_BUFF = 6;  // 加BUFF
    public static final int DAMAGE_MAGIC_DEBUFF = 7;//加法术DEBUFF
    
    public static final int TARGET_SINGLE = 0;  // 单个目标
    public static final int TARGET_SELF = 1;    // 自己
    public static final int TARGET_AREA = 2;    // 指定目标点周围的所有目标
    public static final int TARGET_AROUND = 3;  // 自己周围的所有目标
    
    public ProjectData owner;

    /**
     * 技能类型
     */
    public int type = TYPE_ATTACK;
    /**
     * 目标类型
     */
    public int targetType = TARGET_SINGLE;
    
    /**
     * 影响的范围(以目标为中心的一个范围内的N个单位；生效单位的优先级以距离目标越近越优先。)
     */
    public int affectScope = 1;
    /**
     * 最大级别
     */
    public int maxLevel = 1;
    /**
     * 需要武器，int[0]表示不要求武器
     */
    public int[] requireWeapon = new int[0];
    /**
     * 所属职业
     */
    public int clazz = 4;
    /**
     * 是否自动学习第一级。
     */
    public boolean autoLearn;
    /**
     * 学习级别
     */
    public int[] requireLevel = new int[1];
    /**
     * 耗蓝
     */
    public float[] mp = new float[1];
    /**
     * 施法时间(毫秒)
     */
    public int[] actTime = new int[1];
    
    /**
     * CD组列表 (用半角逗号分隔的字符串)
     */
    public String cdGroup;
    /**
     * CD时间(毫秒)
     */
    public int[] cdTime = new int[1];
    /**
     * 技能图标
     */
    public int iconID = -1;
    /**
     * 有效距离(码)
     */
    public float[] distance = new float[1];
    /**
     * 群体法术有效范围(码)
     */
    public float[] range = new float[1];
    /**
     * 伤害类型
     */
    public int damageType;
    
    /**
     * 辅助动作
     */
    public int assistAction = 0;
    /**
     * 准备动画
     */
    public int prepareAnimation = -1;
    /**
     * 施放动画
     */
    public int castAnimation = -1;
    /**
     * 命中动画
     */
    public int hitAnimation = -1;
    
    /**
     * 轨迹动画
     */
    public int locusAnimation = -1;
    /**
     * 是否马上能用
     */
    public boolean rideUse = true;
    /**
     * 是否可放技能栏
     */
    public boolean visible = true;
    
    /**
     * 被动和光环技能特有：对应BUFF ID
     */
    public int passiveBuff;
    
    /**
     * 限制武器伤害类型
     */
    public int weaponHurtValue;
    
    /**
     * 主动技能特有：战斗效果集合
     */
    public EffectConfigSet effects = new EffectConfigSet();
    
    public ExtPropEntries extPropEntries = new ExtPropEntries();
    /**
     * 实现Class名
     */
    public String implClass;
    
    /**
     * 技能的当前级别。。用于前置技能
     */
    public int currLevel = -1;
    
    public int getCurrLevel() {
        return currLevel;
    }

    /**
     * 技能的当前级别。。用于前置技能
     */
    public List<SkillConfig> preSkillConfig = new ArrayList<SkillConfig>();
    
    public List<SkillConfig> getPreSkillConfig() {
        return preSkillConfig;
    }

    public void setPreSkillConfig(List<SkillConfig> preSkillConfig) {
        this.preSkillConfig = preSkillConfig;
    }

    /**
     * 用于编辑器的临时对象
     */
    public class GeneralConfig extends EffectConfig {
        public void setLevelCount(int max) {
        }

        public int getType() {
            return -1;
        }

        public String getTypeName() {
            return "";
        }

        public String getShortName() {
            return "";
        }

        public int getParamCount() {
            if (type == TYPE_PASSIVE || type == TYPE_BUFF) {
                return 1;
            } else if (targetType == TARGET_SINGLE) {
                return 5;
            } else if (targetType == TARGET_SELF) {
                return 4;
            } else if (targetType == TARGET_AREA) {
                return 6;
            } else if (targetType == TARGET_AROUND) {
                return 5;
            } else {
                throw new IllegalArgumentException();
            }
        }

        public String getParamName(int index) {
            if (index == 0) {
                return "学习级别";
            } else if (index == 1) {
                return "消耗MP";
            } else if (index == 2) {
                return "施法时间(毫秒)";
            } else if (index == 3) {
                return "CD(毫秒)";
            } else if (index == 4) {
                if (targetType == TARGET_AROUND) {
                    return "有效半径(码)";
                } else {
                    return "有效距离(码)";
                }
            } else if (index == 5) {
                return "有效半径(码)";
            }
            throw new IllegalArgumentException();
        }

        public Class getParamClass(int index) {
            if (index == 0) {
                return Integer.class;
            } else if (index == 1) {
                return Float.class;
            } else if (index == 2) {
                return Integer.class;
            } else if (index == 3) {
                return Integer.class;
            } else if (index == 4) {
                if (targetType == TARGET_AROUND) {
                    return Float.class;
                } else {
                    return Float.class;
                }
            } else if (index == 5) {
                return Float.class;
            }
            throw new IllegalArgumentException();
        }

        public Object getParam(int index) {
            if (index == 0) {
                return requireLevel;
            } else if (index == 1) {
                return mp;
            } else if (index == 2) {
                return actTime;
            } else if (index == 3) {
                return cdTime;
            } else if (index == 4) {
                if (targetType == TARGET_AROUND) {
                    return range;
                } else {
                    return distance;
                }
            } else if (index == 5) {
                return range;
            }
            throw new IllegalArgumentException();
        }

        @Override
        public String getJavaInterface() throws Exception {
            // TODO Auto-generated method stub
            return null;
        }
    }
    
    public SkillConfig(ProjectData owner) {
        this.owner = owner;
        effects.setLevelCount(maxLevel);
    }
    
    public int getID() {
        return id;
    }

    public boolean equals(Object o) {
        return this == o;
    }

    public String toString() {
        String name = id + ": " + title;
        if(currLevel != PRE_SKILL){
            name = name + currLevel + "级别";
        }
        return name;
    }
    
    /**
     * 设置技能类型。
     * @param type
     */
    public void setType(int type) {
        this.type = type;
        targetType = TARGET_SINGLE;
        if (type == TYPE_ATTACK) {
            damageType = DAMAGE_PHYSICAL;
            passiveBuff = -1;
        } else if (type == TYPE_AID) {
            damageType = DAMAGE_HEAL;
            passiveBuff = -1;
        } else if (type == TYPE_PASSIVE || type == TYPE_BUFF) {
            damageType = DAMAGE_HEAL;
            rideUse = false;
            visible = false;
            effects.clear();
        } else if (type == TYPE_RELIVE) {
            damageType = DAMAGE_HEAL;
            passiveBuff = -1;
        }
    }

    /**
     * 获得通用参数表。
     * 
     * @return
     */
    public EffectConfig getGeneralConfig() {
        return new GeneralConfig();
    }

    /**
     * 修改最大级别。
     */
    public void setMaxLevel(int newValue) {
        maxLevel = newValue;
        requireLevel = Utils.realloc(requireLevel, maxLevel);
        mp = Utils.realloc(mp, maxLevel);
        actTime = Utils.realloc(actTime, maxLevel);
        cdTime = Utils.realloc(cdTime, maxLevel);
        range = Utils.realloc(range, maxLevel);
        distance = Utils.realloc(distance, maxLevel);
        effects.setLevelCount(maxLevel);
    }

    /**
     * 将obj的数据更新到保存对象上。
     * @param obj 编辑器当前输入内容
     */
    public void update(DataObject obj) {
        SkillConfig oo = (SkillConfig) obj;
        id = oo.id;
        title = oo.title;
        description = oo.description;
        setCategoryName(oo.getCategoryName());
        
        type = oo.type;
        targetType = oo.targetType;
        affectScope = oo.affectScope;
        maxLevel = oo.maxLevel;
        requireWeapon = Utils.realloc(oo.requireWeapon, oo.requireWeapon.length);
        clazz = oo.clazz;
        autoLearn = oo.autoLearn;
        requireLevel = Utils.realloc(oo.requireLevel, oo.requireLevel.length);
        mp = Utils.realloc(oo.mp, oo.mp.length);
        actTime = Utils.realloc(oo.actTime, oo.actTime.length);
        cdGroup = oo.cdGroup;
        cdTime = Utils.realloc(oo.cdTime, oo.cdTime.length);
        iconID = oo.iconID;
        distance = Utils.realloc(oo.distance, oo.distance.length);
        range = Utils.realloc(oo.range, oo.range.length);
        damageType = oo.damageType;
        prepareAnimation = oo.prepareAnimation;
        castAnimation = oo.castAnimation;
        hitAnimation = oo.hitAnimation;
        locusAnimation = oo.locusAnimation;
        rideUse = oo.rideUse;
        visible = oo.visible;
        if (type == TYPE_PASSIVE || type == TYPE_BUFF) {
            visible = false;
        }
        passiveBuff = oo.passiveBuff;
        effects = oo.effects.duplicate();
        effects.removeEffect(-1);
        
        preSkillConfig = oo.preSkillConfig;
        extPropEntries.copyFrom(oo.extPropEntries);
        
    }

    /**
     * 复制对象以用于编辑。
     */
    public DataObject duplicate() {
        SkillConfig ret = new SkillConfig(owner);
        ret.update(this);
        return ret;
    }

    @Override
    public boolean changed(DataObject obj) {
        SkillConfig oo = (SkillConfig)obj;
        return !implClass.equals(oo.implClass);
    }
    
    /**
     * 从XML标签中载入对象属性。
     * @param elem
     */
    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
        description = elem.getAttributeValue("description");
        setCategoryName(elem.getAttributeValue("category"));
        if (getWholeCategoryName() == null) {
            setCategoryName("");
        }
        
        type = Integer.parseInt(elem.getAttributeValue("type"));
        targetType = Integer.parseInt(elem.getAttributeValue("targettype"));
        affectScope = Integer.parseInt(elem.getAttributeValue("affectScope"));
        maxLevel = Integer.parseInt(elem.getAttributeValue("maxlevel"));
        requireWeapon = Utils.stringToIntArray(elem.getAttributeValue("require-weapon"), ';');
        clazz = Integer.parseInt(elem.getAttributeValue("clazz"));
        autoLearn = "1".equals(elem.getAttributeValue("autolearn"));
        requireLevel = Utils.stringToIntArray(elem.getAttributeValue("requirelevel"), ';');
        mp = Utils.stringToFloatArray(elem.getAttributeValue("mp"), ';');
        actTime = Utils.stringToIntArray(elem.getAttributeValue("acttime"), ';');
        cdGroup = elem.getAttributeValue("cdgroup");
        cdTime = Utils.stringToIntArray(elem.getAttributeValue("cdtime"), ';');
        iconID = Integer.parseInt(elem.getAttributeValue("iconid"));
        int[] arr = Utils.stringToIntArray(elem.getAttributeValue("distance"), ';');
        distance = new float[arr.length];
        for (int i = 0; i < arr.length; i++) {
            distance[i] = arr[i] / 8.0f;
        }
        arr = Utils.stringToIntArray(elem.getAttributeValue("range"), ';');
        range = new float[arr.length];
        for (int i = 0; i < arr.length; i++) {
            range[i] = arr[i] / 8.0f;
        }
        damageType = Integer.parseInt(elem.getAttributeValue("damagetype"));
        prepareAnimation = Integer.parseInt(elem.getAttributeValue("prepareani"));
        castAnimation = Integer.parseInt(elem.getAttributeValue("castani"));
        hitAnimation = Integer.parseInt(elem.getAttributeValue("hitani"));
        locusAnimation = Integer.parseInt(elem.getAttributeValue("locusani"));
        rideUse = "true".equals(elem.getAttributeValue("rideuse"));
        visible = "true".equals(elem.getAttributeValue("visible"));
        passiveBuff = Integer.parseInt(elem.getAttributeValue("buffid"));
        try {
            effects.load(owner, elem.getChild("effects"), maxLevel);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
        effects.setLevelCount(maxLevel);
        
        implClass = elem.getAttributeValue("class");
        
        if (type == TYPE_PASSIVE || type == TYPE_BUFF) {
            visible = false;
        }
        try{
            loadPreSkillConfig(elem);
        }catch (Exception e) {
        }
        extPropEntries.loadExtData(elem.getChild("extProps"));
    }
    
    /**
     * 从XML标签中载入对象属性。
     * @param elem
     */
    public void loadPreSkillConfig(Element elem) throws Exception {
        preSkillConfig.clear();
        List list = elem.getChildren("preskillconfig");
        for (int i = 0; i < list.size(); i++) {
            Element elemTemp = (Element) list.get(i);
            int preSkillId = Integer.parseInt(elemTemp.getAttributeValue("preskillconfigid"));
            int level = Integer.parseInt(elemTemp.getAttributeValue("preskillconfiglevel"));
            SkillConfig skillConfig = (SkillConfig) owner.findObject(SkillConfig.class, preSkillId);
            SkillConfig preSkill = (SkillConfig) skillConfig.duplicate();
            preSkill.currLevel = level;
            preSkillConfig.add(preSkill);
        }
    }
    
    /**
     * 保存成一个XML标签。
     */
    public Element save() {
        Element ret = new Element("skill");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        ret.addAttribute("description", description);
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }
        
        ret.addAttribute("type", String.valueOf(type));
        ret.addAttribute("targettype", String.valueOf(targetType));
        ret.addAttribute("affectScope", String.valueOf(affectScope));
        ret.addAttribute("maxlevel", String.valueOf(maxLevel));
        ret.addAttribute("require-weapon", Utils.intArrayToString(requireWeapon, ';'));
        ret.addAttribute("clazz", String.valueOf(clazz));
        ret.addAttribute("autolearn", autoLearn ? "1" : "0");
        ret.addAttribute("requirelevel", Utils.intArrayToString(requireLevel, ';'));
        ret.addAttribute("mp", Utils.floatArrayToString(mp, ';'));
        ret.addAttribute("acttime", Utils.intArrayToString(actTime, ';'));
        if(cdGroup!=null)
        ret.addAttribute("cdgroup", cdGroup);
        ret.addAttribute("cdtime", Utils.intArrayToString(cdTime, ';'));
        ret.addAttribute("iconid", String.valueOf(iconID));

        int[] arr = new int[distance.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (int)(distance[i] * 8.0f);
        }
        ret.addAttribute("distance", Utils.intArrayToString(arr, ';'));

        arr = new int[range.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (int)(range[i] * 8.0f);
        }
        ret.addAttribute("range", Utils.intArrayToString(arr, ';'));

        ret.addAttribute("damagetype", String.valueOf(damageType));
        ret.addAttribute("prepareani", String.valueOf(prepareAnimation));
        ret.addAttribute("castani", String.valueOf(castAnimation));
        ret.addAttribute("hitani", String.valueOf(hitAnimation));
        ret.addAttribute("locusani", String.valueOf(locusAnimation));
        ret.addAttribute("rideuse", rideUse ? "true" : "false");
        ret.addAttribute("visible", visible ? "true" : "false");
        ret.addAttribute("buffid", String.valueOf(passiveBuff));
        ret.addContent(effects.save());
        
        if (implClass != null) {
            ret.addAttribute("class", implClass);
        }
        addPreSkillConfig(ret);
        Element el = new Element("extProps");
        extPropEntries.saveToDom(el);
        ret.addContent(el);
        return ret;
    }
    
    public void addPreSkillConfig(Element parent){
        for(int i = 0; i < preSkillConfig.size(); i++){
            Element propEl = new Element("preskillconfig");
            propEl.addAttribute("preskillconfigid", String.valueOf(preSkillConfig.get(i).getID()));
            propEl.addAttribute("preskillconfiglevel", String.valueOf(preSkillConfig.get(i).getCurrLevel()));
            parent.getMixedContent().add(propEl);
        }
    }
    
    
    /**
     * 判断本对象是否依赖于另外一个对象。
     */
    public boolean depends(DataObject obj) {
        if (obj instanceof BuffConfig && obj.id == this.passiveBuff) {
            return true;
        }
        return false;
    }

    /**
     * 计算自动生成的类名
     * @param classPrefix
     * @return
     */
    public String getClassName(String classPrefix) {
        String idStr = String.valueOf(id);
        while (idStr.length() < 3) {
            idStr = "0" + idStr;
        }
        return classPrefix + idStr;
    }

    /**
     * 根据一个技能配置生成实现类。
     * @param out 输出流
     * @param packageName 包名
     * @param classPrefix 类名前缀
     * @throws EffectRejectException 
     */
    public void generateJava(PrintWriter out, String packageName, String classPrefix) throws EffectRejectException {
        EffectConfig[] allEff = effects.getAllEffects();
        
        // package & import
        out.println("package " + packageName + ";");
        out.println();
//        out.println("import java.util.*;");
//        out.println("import org.slf4j.Logger;");
//        out.println("import org.slf4j.LoggerFactory;");
        out.println("import cybertron.core.*;");
        out.println("import cybertron.core.skill.*;");
        out.println("import cybertron.core.battle.*;");
        out.println("import cybertron.core.battle.damage.*;");
        out.println("import cybertron.core.battle.skillType.*;");
        out.println("import cybertron.core.buff.*;");
        out.println();

        // class name & interfaces
        String className = getClassName(classPrefix);
        out.print("public class " + className + " extends AbstractSkill");
        if (type != TYPE_BUFF && type != TYPE_PASSIVE) {
            out.print(" implements CombatEffect");
        }
        out.println(" {");

        // static data definition
        out.println("    public static final int[] WEAPON = {");
        out.print("        ");
        for (int i = 0; i < requireWeapon.length; i++) {
            if (i > 0) {
                out.print(", ");
            }
            out.print(requireWeapon[i]);
        }
        out.println();
        out.println("    };");
        BuffConfig.generateStaticArray(out, "REQUIRE_LEVEL", requireLevel);
        BuffConfig.generateStaticArray(out, "MP_USE", mp);
        BuffConfig.generateStaticArray(out, "ACT_TIME", actTime);
        BuffConfig.generateStaticArray(out, "CD_TIME", cdTime);
        int[] arr = new int[distance.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (int)(distance[i] * 8.0f);
        }
        BuffConfig.generateStaticArray(out, "DISTANCE", arr);
        arr = new int[range.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (int)(range[i] * 8.0f);
        }
        BuffConfig.generateStaticArray(out, "RANGE", arr);
        for (EffectParamRef pr : effects.getAllParams()) {
            String name = BuffConfig.getFieldName(pr.effect, pr.index, pr.effectID, true);
            BuffConfig.generateStaticArray(out, name, pr.effect.getParam(pr.index));
        }

        // local data definition
        for (EffectParamRef pr : effects.getAllParams()) {
            String name = BuffConfig.getFieldName(pr.effect, pr.index, pr.effectID, false);
            Object param = pr.effect.getParam(pr.index);
            if (param instanceof int[]) {
                out.println("    int " + name + ";");
            }
            else if (param instanceof float[]) {
                out.println("    float " + name + ";");
            }
            else if (param instanceof String[]) {
                out.println("    String " + name + ";");
            }
            else if (param instanceof int[][]) {
                out.println("    int[] " + name + ";");
            }
        }
        out.println();

        // constructor
        out.println("    public " + className + "(int lvl) {");
        out.println("        super(" + id + ", \"" + Utils.reverseConv(title) + "\", lvl);");
        out.println("        if (lvl > " + maxLevel + ") {");
        out.println("            throw new IllegalArgumentException();");
        out.println("        }");
        for (EffectParamRef pr : effects.getAllParams()) {
            String name = BuffConfig.getFieldName(pr.effect, pr.index, pr.effectID, false);
            String staticName = BuffConfig.getFieldName(pr.effect, pr.index, pr.effectID, true);
            out.println("        " + name + " = " + staticName + "[lvl];");
        }
        out.println("        distance = DISTANCE[lvl];");
        out.println("        actTime = ACT_TIME[lvl];");
        out.println("        CDGroup = new int[] {" + cdGroup + "};");
        out.println("        CDTime = CD_TIME[lvl];");
        out.println("        range = RANGE[lvl];");
        out.println("        iconId = " + iconID + ";");
        out.println("        prepareAnimation = " + prepareAnimation + ";");
        out.println("        castAnimation = " + castAnimation + ";");
//        out.println("        hitAnimation = " + hitAnimation + ";");
        out.println("        trackAnimation = " + locusAnimation + ";");
        switch (clazz) {
        case 0:
            out.println("        clazz = Combatable.CLASS_1;");
            break;
        case 1:
            out.println("        clazz = Combatable.CLASS_2;");
            break;
        case 2:
            out.println("        clazz = Combatable.CLASS_3;");
            break;
        case 3:
            out.println("        clazz = Combatable.CLASS_4;");
            break;
        case 4:
            out.println("        clazz = -1;");
            break;
        default:
            throw new IllegalArgumentException("非法职业");
        }
        out.println("        mp = MP_USE[lvl];");
        switch (type) {
        case TYPE_ATTACK:
            out.println("        type = TYPE_ATTACK;");
            break;
        case TYPE_AID:
            out.println("        type = TYPE_AID;");
            break;
        case TYPE_PASSIVE:
            out.println("        type = TYPE_PASSIVE;");
            break;
        case TYPE_BUFF:
            out.println("        type = TYPE_BUFF;");
            break;
        case TYPE_RELIVE:
            out.println("        type = TYPE_RELIVE;");
            break;
        default:
            throw new IllegalArgumentException("非法的技能类型");
        }
        if (visible) {
            out.println("        type |= TYPE_VISIBLE;");
        }
        if (rideUse) {
            out.println("        type |= TYPE_RIDE_USE;");
        }
        if (type == TYPE_PASSIVE || type == TYPE_BUFF) {
            out.println("        targetType = TARGET_AID_SELF;");
        } else {
            switch (targetType) {
            case TARGET_SINGLE:
                if (type == TYPE_ATTACK) {
                    out.println("        targetType = TARGET_SINGLE_ATTACK;");
                } else {
                    out.println("        targetType = TARGET_SINGLE_AID;");
                }
                break;
            case TARGET_SELF:
                if (type == TYPE_ATTACK) {
                    throw new IllegalArgumentException("攻击技能的目标类型不能是自己");
                } else {
                    out.println("        targetType = TARGET_AID_SELF;");
                }
                break;
            case TARGET_AREA:
                if (type == TYPE_ATTACK) {
                    out.println("        targetType = TARGET_AOE_ATTACK_TARGET;");
                } else {
                    out.println("        targetType = TARGET_AOE_AID_TARGET;");
                }
                break;
            case TARGET_AROUND:
                if (type == TYPE_ATTACK) {
                    out.println("        targetType = TARGET_AOE_ATTACK_SELF;");
                } else {
                    out.println("        targetType = TARGET_AOE_AID_SELF;");
                }
                break;
            default:
                throw new IllegalArgumentException("非法的目标类型");
            }
        }
        out.println("    }");
        out.println();
        
        // implementation of skill
        
        // getDesc(Unit owner)
        out.println("    public String getDesc(Combatable owner) {");
        String[] secs = DescriptionPattern.splitPattern(description);
        if (secs.length <= 1) {
            out.println("        return \"" + Utils.reverseConv(description) + "\";");
        } else {
            DescriptionPattern pattern = new DescriptionPattern(this);
            
            String formatString = "";
            List<String> paramStrings = new ArrayList<String>();
            for (int i = 0; i < secs.length; i++) {
                if ((i & 1) == 0) {
                    // 非变量
                    if (secs[i].length() > 0) {
                        formatString += Utils.reverseConv(secs[i]);
                    }
                } else {
                    // 变量
                    int type = 0;
                    String varName = secs[i];
                    if (varName.endsWith("%")) {
                        type = 1;
                        varName = varName.substring(0, varName.length() - 1);
                    } else if (varName.endsWith("t")) {
                        type = 2;
                        varName = varName.substring(0, varName.length() - 1);
                    } else if (varName.endsWith("T")) {
                        type = 3;
                        varName = varName.substring(0, varName.length() - 1);
                    }
                    String varRef = pattern.varToCode(varName);
                    formatString += "{" + paramStrings.size() + "}";
                    if (type == 0) {
                        paramStrings.add("CommonUtil.formatValue(" + varRef + ")");
                    } else if (type == 1) {
                        paramStrings.add("CommonUtil.formatPercent(" + varRef + ")");
                    } else if (type == 2) {
                        paramStrings.add("CommonUtil.formatMillSecond(" + varRef + ")");
                    } else if (type == 3) {
                        paramStrings.add("CommonUtil.formatSecond(" + varRef + ")");
                    }
                }
            }
            out.print("        return java.text.MessageFormat.format(\"" + formatString + "\"");
            for (String param : paramStrings) {
                out.print(", " + param);
            }
            out.println(");");
        }
        out.println("    }");
        out.println();

//        // checkWeapon()
//        out.println("    private boolean checkWeapon(Unit unit) {");
//        if (requireWeapon.length == 0) {
//            out.println("        return true;");
//        } else {
//            out.println("        if (unit == null || unit.equipments == null || unit.equipments.getWeapon() == null) {");
//            out.println("            return false;");
//            out.println("        }");
//            out.println("        int type = unit.equipments.getWeapon().template.equipment.minorType;");
//            out.println("        for (int t : WEAPON) {");
//            out.println("            if (t == type) {");
//            out.println("                return true;");
//            out.println("            }");
//            out.println("        }");
//            out.println("        return false;");
//        }
//        out.println("    }");
//        out.println();
//        
//        // getRequireWeapon()
//        if (requireWeapon.length != 0) {
//            out.println("    public int[] getRequireWeapon() {");
//            out.println("        return WEAPON;");
//            out.println("    }");
//            out.println();
//        }
        
        // isAutoLearn()
        if (autoLearn) {
            out.println("    public boolean isAutoLearn() {");
            out.println("        return true;");
            out.println("    }");
            out.println();
        }
        
        // getSkillType()
        out.println("    public SkillType getSkillType() {");
        String damageClassStr = "PhysicalSkill";
        switch (damageType) {
            case SkillConfig.DAMAGE_PHYSICAL:
                damageClassStr = "PhysicalSkill";
                break;
            case SkillConfig.DAMAGE_MAGIC:
                damageClassStr = "MagicSkill";
                break;
            case SkillConfig.DAMAGE_DECMP:
                damageClassStr = "DecmpSkill";
                break;
            case SkillConfig.DAMAGE_DEBUFF:
                damageClassStr = "DebuffSkill";
                break;
            case SkillConfig.DAMAGE_HEAL:
                damageClassStr = "HealSkill";
                break;
            case SkillConfig.DAMAGE_ADDMP:
                damageClassStr = "AddmpSkill";
                break;
            case SkillConfig.DAMAGE_BUFF:
                damageClassStr = "BuffSkill";
                break;
        }
        out.println("        return " + damageClassStr + ".instance;");
        out.println("    }");
        out.println();
        
        // getRequireLevel()
        out.println("    public int getRequireLevel() {");
        out.println("        return REQUIRE_LEVEL[level];");
        out.println("    }");
        out.println();

        // shareCD()
        //if (!shareCD) {
        if(extPropEntries.editProps.containsKey("shareCD") && !extPropEntries.editProps.get("shareCD").equals(true)){
            out.println("    public boolean shareCD() {");
            out.println("        return false;");
            out.println("    }");
            out.println();
        }
        
        // useBuff()
        if (extPropEntries.editProps.containsKey("useBuffs") && !extPropEntries.editProps.get("useBuffs").equals(true)) {
            out.println("    public boolean useBuff() {");
            out.println("        return false;");
            out.println("    }");
            out.println();
        }
        
        // createActEffect()
        out.println("    protected CombatEffect createActEffect() {");
        if (type != TYPE_BUFF && type != TYPE_PASSIVE) {
            out.println("        if (level == 0) {");
            out.println("            return null;");
            out.println("        }");
            out.println("        return this;");
        } else {
            out.println("        return null;");
        }
        out.println("    }");
        out.println();
        
        // newBuff()
        if (type == TYPE_PASSIVE) {
            out.println("    public Buff newBuff() {");
            out.println("        if (level == 0) {");
            out.println("            return null;");
            out.println("        }");
            out.println("        return BuffUtil.createSkillBuff(" + passiveBuff + ", this);");
            out.println("    }");
            out.println();
        }
        
        // getAreaBuff()
        if (type == TYPE_BUFF) {
            out.println("    public Buff getAreaBuff() {");
            out.println("        if (level == 0) {");
            out.println("            return null;");
            out.println("        }");
            out.println("        return BuffUtil.createSkillBuff(" + passiveBuff + ", this);");
            out.println("    }");
            out.println();
        }
        
        // 实现CombatEffect接口
        if (type != TYPE_PASSIVE && type != TYPE_BUFF) {
            // public void preHit(CombatContext context, boolean isActive)
            out.println("    public void preHit(CombatContext context, boolean isActive) {");
//            SkillPreHitAsignType.genDamageTypeAsignCode(damageType, out);
//            out.println("        if (!checkWeapon(context.source)) {");
//            out.println("            return;");
//            out.println("        }");
            for (int id = 0; id < allEff.length; id++) {
                EffectConfig eff = allEff[id];
                String p1 = BuffConfig.getFieldName(eff, 0, eff.effectID, false);
                eff.genSkillPreHit(out, damageType, p1);
            }
            out.println("    }");
            out.println();

            // public void postHit(CombatContext context, boolean isActive)
            out.println("    public void postHit(CombatContext context, boolean isActive) {}");
            out.println();

            // public void preDamage(CombatContext context, boolean isActive)
            out.println("    public void preDamage(CombatContext context, boolean isActive) {");
            out.println("        if (!checkWeapon(context.source)) {");
            out.println("            return;");
            out.println("        }");
            for (int id = 0; id < allEff.length; id++) {
                EffectConfig eff = allEff[id];
                String p1 = BuffConfig.getFieldName(eff, 0, eff.effectID, false);
                String p2 = BuffConfig.getFieldName(eff, 1, eff.effectID, false);
                eff.genSkillPreDamage(out, damageType, p1, p2);
                switch (eff.getType()) {}
            }
            out.println("    }");
            out.println();

            // public void postDamage(CombatContext context, boolean isActive)
            out.println("    public void postDamage(CombatContext context, boolean isActive) {");
            out.println("        if (!checkWeapon(context.source)) {");
            out.println("            return;");
            out.println("        }");
            for (int id = 0; id < allEff.length; id++) {
                EffectConfig eff = allEff[id];
                String p1 = BuffConfig.getFieldName(eff, 0, eff.effectID, false);
                eff.genSkillPostDamage(damageType, out, p1);
            }
            out.println("    }");
            out.println();

            // public void finished(CombatContext context, boolean isActive)
            out.println("    public void finished(CombatContext context, boolean isActive) {");
            out.println("        if (!checkWeapon(context.source)) {");
            out.println("            return;");
            out.println("        }");
            for (int id = 0; id < allEff.length; id++) {
                EffectConfig eff = allEff[id];
                String p1 = BuffConfig.getFieldName(eff, 0, eff.effectID, false);
                String p2 = BuffConfig.getFieldName(eff, 1, eff.effectID, false);
                String p3 = BuffConfig.getFieldName(eff, 2, eff.effectID, false);
                String p4 = BuffConfig.getFieldName(eff, 3, eff.effectID, false);
                String p5 = BuffConfig.getFieldName(eff, 4, eff.effectID, false);
                String p6 = BuffConfig.getFieldName(eff, 5, eff.effectID, false);
                eff.genSkillFinished(out, p1, p2, p3, p4, p5, p6);
            }
            out.println("    }");
            out.println();
        }

        out.println("}");
        
        this.implClass = packageName + "." + className;
    }

    /**
     * 查找一个技能的名字。
     * @param project
     * @param questID
     * @return
     */
    public static String toString(ProjectData project, int skillID) {
        SkillConfig q = (SkillConfig)project.findObject(SkillConfig.class, skillID);
        if (q == null) {
            return "无效技能";
        } else {
            return q.toString();
        }
    }
    
    /**
     * 查找一组技能的名字
     * @param project
     * @param skillIDs 技能ID,用逗号分隔
     * @return
     */
    public static String toString(ProjectData project, String skillIDs) {
        String[] secs = skillIDs.split(",");
        StringBuilder names = new StringBuilder();
        for (String sec : secs) {
            try {
                int sid = Integer.parseInt(sec);
                SkillConfig sc = (SkillConfig)project.findObject(SkillConfig.class, sid);
                if (names.length() > 0) {
                    names.append(",");
                }
                if (sc == null) {
                    names.append("无效技能");
                } else {
                    names.append(sc.title);
                }
            } catch (Exception e) {
                continue;
            }
        }
        return names.toString();
    }
    
    /**
     * 对这个对象的属性进行国际化处理，如果有需要国际化的字符串，则提取出来到context中查找翻译结果。
     * @param context
     * @return 如果有某个属性被替换，返回true，否则返回false。
     */
    public boolean i18n(I18NContext context) {
        boolean changed = false;
        String tmp = context.input(title, "Skill");
        if (tmp != null) {
            title = tmp;
            changed = true;
        }
        tmp = context.input(description, "Skill");
        if (tmp != null) {
            description = tmp;
            changed = true;
        }
        return changed;
    }
}
