package com.pip.game.data.skill;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.autocoding.BuffGetJavaInterface;
import com.pip.game.data.extprop.ExtPropEntries;
import com.pip.game.data.i18n.I18NContext;
import com.pip.game.editor.skill.DescriptionPattern;
import com.pip.game.editor.skill.ParamIndicator;
import com.pip.util.Utils;

/**
 * 一个BUFF类的配置数据。
 * 
 * @author lighthu
 */
public class BuffConfig extends DataObject implements IBuffConfig{
    /**
     * 动态BUFF，表示临时的有时间限制或其它到期限制的BUFF。
     */
    public static final int BUFF_TYPE_DYNAMIC = 1;
    /**
     * 静态BUFF，表示被动技能加上的永不消失的BUFF。
     */
    public static final int BUFF_TYPE_STATIC = 2;
    /**
     * 物品特效BUFF，表示装备、称号等系统添加的持久性BUFF。
     */
    public static final int BUFF_TYPE_EQUIP = 4;
    
    /** 合并规则：总是不合并 */
    public static final int MERGE_NONE = 0;
    /** 合并规则：叠加，最多3层 */
    public static final int MERGE_ADD = 1;
    /** 合并规则：高级或同级覆盖 */
    public static final int MERGE_LEVEL = 2;
    /** 合并规则：同一来源的覆盖，效果叠加 */
    public static final int MERGE_SAME_SOURCE = 3;
    /** 合并规则：总是覆盖 */
    public static final int MERGE_ALWAYS = 4;

    public static final int MINORTYPE_SWORD = 1; // 剑
    public static final int MINORTYPE_KNIFE = 2; // 刀
    public static final int MINORTYPE_AXE = 3; // 斧
    public static final int MINORTYPE_SPEAR = 4; // 枪
    public static final int MINORTYPE_POLEARM = 5; // 长柄
    public static final int MINORTYPE_FAN = 6; // 扇
    public static final int MINORTYPE_BOW = 7; // 弓
    
    /**
     * 所属项目。
     */
    public ProjectData owner;
    /**
     * BUFF类型。
     */
    public int buffType = BUFF_TYPE_DYNAMIC;
    /**
     * 最高级别（有效为1-x级）
     */
    public int maxLevel = 1;
    /**
     * 图标ID，-1表示不显示
     */
    public int iconID = -1;
    /**
     * 需要武器，int[0]表示不要求武器
     */
    public int[] requireWeapon = new int[0];
    /**
     * 持续时间(毫秒)，-1表示不过期
     */
    public int[] duration = new int[1];
    
    /**
     * 持续时间(回合)
     */
    public int[] round_times = new int[1];
    
    /**
     * 持续时间(战斗场次)
     */
    public int[] battle_times = new int[1];
    /**
     * 持续时间(次数)
     */
    public int[] times = new int[1];
    /**
     * 效果价值。
     */
    public int[] value = new int[1];
    /**
     * 是否良性
     */
    public boolean good = false;
    /**
     * 是否可驱散
     */
    public boolean dispelable = true;
    /**
     * 死亡后是否保持
     */
    public boolean keepOnDie = false;
    /**
     * 是否光环
     */
    public boolean isAreaBuff = false;
    /**
     * 合并规则
     */
    public int mergeStrategy = MERGE_NONE;
    /**
     * 下线是否计时
     */
    public boolean updateEvenOffline;
    /**
     * BUFF效果
     */
    public EffectConfigSet effects = new EffectConfigSet();
    
    public ExtPropEntries extPropEntries = new ExtPropEntries();
    /**
     * 分组ID，0表示没有。
     */
    public int groupID;
    /**
     * 对应class名
     */
    public String implClass;
    
    
    /**
     * 用于编辑器的临时对象
     */
    public class GeneralConfig extends EffectConfig {
        public void setLevelCount(int max) {
        }

        public int getType() {
            return -1;
        }

//        public String getTypeName() {
//            return "";
//        }
//
//        public String getShortName() {
//            return "";
//        }

        public int getParamCount() {
            if (buffType == BUFF_TYPE_STATIC) {
                return 0;
            }
            return 1;
        }

        public String getParamName(int index) {
            if (buffType == BUFF_TYPE_EQUIP) {
                return "特效价值";
            }
            return "持续时间(毫秒)";
        }

        public Class getParamClass(int index) {
            return Integer.class;
        }

        public Object getParam(int index) {
            if (buffType == BUFF_TYPE_EQUIP) {
                return value;
            }
            return duration;
        }

        @Override
        public String getJavaInterface() throws Exception {
            // TODO Auto-generated method stub
            return null;
        }
        
    }

    public BuffConfig(ProjectData owner) {
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
        return id + ": " + title;
    }
    
    /**
     * 设置BUFF类型。
     * @param type
     */
    public void setBuffType(int type) {
        buffType = type;
        if (buffType == BUFF_TYPE_STATIC) {
            Arrays.fill(duration, -1);
            Arrays.fill(round_times, -1);
            Arrays.fill(battle_times, -1);
            Arrays.fill(times, -1);
            good = true;
            dispelable = false;
            isAreaBuff = false;
            mergeStrategy = MERGE_LEVEL;
        } else if (buffType == BUFF_TYPE_EQUIP) {
            Arrays.fill(duration, -1);
            Arrays.fill(round_times, -1);
            Arrays.fill(battle_times, -1);
            Arrays.fill(times, -1);
            good = true;
            dispelable = false;
            isAreaBuff = false;
            mergeStrategy = MERGE_LEVEL;
        } else {
            isAreaBuff = false;
        }
    }

    /**
     * 获得通用参数表。
     * 
     * @return
     */
    public EffectConfig getGeneralConfig() {
        if(buffType==BUFF_TYPE_DYNAMIC)
            return new  DynamicGeneralConfig(this);
        if(buffType==BUFF_TYPE_STATIC){
            return new StaticGeneralConfig(this);
        }
        if(buffType==BUFF_TYPE_EQUIP)
            return new EquipGeneralConfig(this);
        return null;
    }

    /**
     * 修改最大级别。
     */
    public void setMaxLevel(int newValue) {
        maxLevel = newValue;
        duration = Utils.realloc(duration, maxLevel);
        round_times = Utils.realloc(round_times, maxLevel);
        battle_times = Utils.realloc(battle_times, maxLevel);
        times = Utils.realloc(times, maxLevel);
        value = Utils.realloc(value, maxLevel);
        if (buffType == BUFF_TYPE_STATIC) {
            Arrays.fill(duration, -1);
            Arrays.fill(round_times, -1);
            Arrays.fill(battle_times, -1);
            Arrays.fill(times, -1);
        } else if (buffType == BUFF_TYPE_EQUIP) {
            Arrays.fill(duration, -1);
            Arrays.fill(round_times, -1);
            Arrays.fill(battle_times, -1);
            Arrays.fill(times, -1);
        }
        effects.setLevelCount(maxLevel);
    }

    /**
     * 保存数据对象。
     * @param obj 编辑器当前输入内容
     */
    public void update(DataObject obj) {
        BuffConfig oo = (BuffConfig) obj;
        id = oo.id;
        title = oo.title;
        description = oo.description;
        setCategoryName(oo.getCategoryName());
        
        buffType = oo.buffType;
        maxLevel = oo.maxLevel;
        iconID = oo.iconID;
        requireWeapon = new int[oo.requireWeapon.length];
        System.arraycopy(oo.requireWeapon, 0, requireWeapon, 0, requireWeapon.length);
        duration = new int[oo.duration.length];
        System.arraycopy(oo.duration, 0, duration, 0, duration.length);
        round_times = new int[oo.round_times.length];
        System.arraycopy(oo.round_times, 0, round_times, 0, round_times.length);
        battle_times = new int[oo.battle_times.length];
        System.arraycopy(oo.battle_times, 0, battle_times, 0, battle_times.length);
        times = new int[oo.times.length];
        System.arraycopy(oo.times, 0, times, 0, times.length);
        value = new int[oo.value.length];
        System.arraycopy(oo.value, 0, value, 0, value.length);
        good = oo.good;
        dispelable = oo.dispelable;
        keepOnDie = oo.keepOnDie;
        isAreaBuff = oo.isAreaBuff;
        mergeStrategy = oo.mergeStrategy;
        updateEvenOffline = oo.updateEvenOffline;
        effects = oo.effects.duplicate();
        effects.removeEffect(-1);
        
        extPropEntries.copyFrom(oo.extPropEntries);
        groupID = oo.groupID;
    }

    /**
     * 复制对象以用于编辑。
     */
    public DataObject duplicate() {
        BuffConfig ret = new BuffConfig(owner);
        ret.update(this);
        return ret;
    }

    @Override
    public boolean changed(DataObject obj) {
        BuffConfig oo = (BuffConfig)obj;
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
        
        buffType = Integer.parseInt(elem.getAttributeValue("type"));
        maxLevel = Integer.parseInt(elem.getAttributeValue("maxlevel"));
        iconID = Integer.parseInt(elem.getAttributeValue("iconid"));
        requireWeapon = Utils.stringToIntArray(elem.getAttributeValue("require-weapon"), ';');
        duration = Utils.stringToIntArray(elem.getAttributeValue("duration"), ';');
        round_times = Utils.stringToIntArray(elem.getAttributeValue("round_times"), ';');
        battle_times = Utils.stringToIntArray(elem.getAttributeValue("battle_times"), ';');
        times = Utils.stringToIntArray(elem.getAttributeValue("times"), ';');
        try {
            value = Utils.stringToIntArray(elem.getAttributeValue("value"), ';');
        } catch (Exception e) {
            value = new int[maxLevel];
        }
        good = "true".equals(elem.getAttributeValue("good"));
        dispelable = "true".equals(elem.getAttributeValue("dispelable"));
        keepOnDie = "true".equals(elem.getAttributeValue("keepondie"));
        isAreaBuff = "true".equals(elem.getAttributeValue("isareabuff"));
        mergeStrategy = Integer.parseInt(elem.getAttributeValue("merge-strategy"));
        updateEvenOffline = "true".equals(elem.getAttributeValue("update-offline"));

        try {
            effects.load(owner, elem.getChild("effects"), maxLevel);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
        implClass = elem.getAttributeValue("class");
        extPropEntries.loadExtData(elem.getChild("extProps"));
        if (elem.getAttribute("groupid") != null) {
            groupID = Integer.parseInt(elem.getAttributeValue("groupid"));
        }
    }

    /**
     * 保存成一个XML标签。
     */
    public Element save() {
        Element ret = new Element("buff");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        ret.addAttribute("description", description);
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }
        
        ret.addAttribute("type", String.valueOf(buffType));
        ret.addAttribute("maxlevel", String.valueOf(maxLevel));
        ret.addAttribute("iconid", String.valueOf(iconID));
        ret.addAttribute("require-weapon", Utils.intArrayToString(requireWeapon, ';'));
        ret.addAttribute("duration", Utils.intArrayToString(duration, ';'));
        ret.addAttribute("round_times", Utils.intArrayToString(round_times, ';'));
        ret.addAttribute("battle_times", Utils.intArrayToString(battle_times, ';'));
        ret.addAttribute("times", Utils.intArrayToString(times, ';'));
        ret.addAttribute("value", Utils.intArrayToString(value, ';'));
        ret.addAttribute("good", good ? "true" : "false");
        ret.addAttribute("dispelable", dispelable ? "true" : "false");
        ret.addAttribute("keepondie", keepOnDie ? "true" : "false");
        ret.addAttribute("isareabuff", isAreaBuff ? "true" : "false");
        ret.addAttribute("merge-strategy", String.valueOf(mergeStrategy));
        ret.addAttribute("update-offline", updateEvenOffline ? "true" : "false");
        ret.addContent(effects.save());
        if (implClass != null) {
            ret.addAttribute("class", implClass);
        }
        Element el = new Element("extProps");
        extPropEntries.saveToDom(el);
        ret.addContent(el);
        if (groupID != 0) {
            ret.addAttribute("groupid", String.valueOf(groupID));
        }
        return ret;
    }

    /**
     * 判断本对象是否依赖于另外一个对象。
     */
    public boolean depends(DataObject obj) {
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
     * 根据一个BUFF配置生成实现类。
     * @param out 输出流
     * @param packageName 包名
     * @param classPrefix 类名前缀
     */
    public void generateJava(PrintWriter out, String packageName, String classPrefix) {
        EffectConfig[] allEff = effects.getAllEffects();
        
        // package & import
        out.println("package " + packageName + ";");
        out.println();
        out.println("import java.util.*;");
        out.println("import cybertron.core.*;");
        out.println("import cybertron.core.buff.*;");
        out.println("import cybertron.core.skill.*;");
        out.println("import cybertron.core.battle.*;");
        out.println("import cybertron.core.battle.damage.*;");
        out.println("import cybertron.core.battle.skillType.*;");
        out.println("import org.apache.mina.common.ByteBuffer;");
        out.println("import com.pip.util.Utils;");
        out.println("import gnu.trove.TIntHashSet;");
        out.println();

        // class name & interfaces
        Set<String> ifs = getJavaInterface();
        String className = getClassName(classPrefix);

        out.print("/** ");
        out.println();
        out.print(" * " + Utils.reverseConv(title) + " <br>");
        out.println();
        out.print(" * " + Utils.reverseConv(description));
        out.println();
        out.println(" */");
        out.print("public class " + className + " extends AbstractBuff ");
        if (ifs.size() > 0) {
            out.print(" implements");
            int k = 0;
            for (String ifname : ifs) {
                if (k == 0) {
                    out.print(" " + ifname);
                } else {
                    out.print(", " + ifname);
                }
                k++;
            }
        }
        out.println(" {");

        // static data definition
        out.println("    private static final int[] WEAPON = {");
        out.print("        ");
        for (int i = 0; i < requireWeapon.length; i++) {
            if (i > 0) {
                out.print(", ");
            }
            out.print(requireWeapon[i]);
        }
        out.println();
        out.println("    };");
        generateStaticArray(out, "DURATION", duration);
        generateStaticArray(out, "ROUND_TIMES",round_times);
        generateStaticArray(out, "BATTLE_TIMES",battle_times);
        generateStaticArray(out, "TIMES",times);
        for (EffectParamRef pr : effects.getAllParams()) {
            String name = getFieldName(pr.effect, pr.index, pr.effectID, true);
            generateStaticArray(out, name, pr.effect.getParam(pr.index));
        }

        // local data definition
        if (buffType == BUFF_TYPE_STATIC) {
            out.println("    Skill skill;");
        }
        
        for(EffectConfig eff:allEff){
            eff.genCustomerFields(out);
        }
        
        // 所有效果的参数
        for (EffectParamRef pr : effects.getAllParams()) {
            String name = getFieldName(pr.effect, pr.index, pr.effectID, false);
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
            else if (param instanceof ParamIndicator[]) {
                out.println("    String " + name + ";");
            }
            else if (param instanceof int[][]) {
                out.println("    int[] " + name + ";");
            }
        }
        out.println();

        // 处理dot/hot的初始值：时间、间隔、剩余伤害、剩余治疗
        List<EffectConfig> hots = effects.findEffects(new int[] { EffectConfig.D_O_T, EffectConfig.H_O_T, EffectConfig.MPDOT, 
                EffectConfig.MPHOT });
        genConstructor(out, className, hots);
        
        // implementation of Buff

        // getDesc()
        out.println("    public String getDesc() {");
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
                        paramStrings.add("CommonUtil.formatValue(" + varRef + " * multiple)");
                    } else if (type == 1) {
                        paramStrings.add("CommonUtil.formatPercent(" + varRef + " * multiple)");
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
        
        // getSkill()
        if (buffType == BUFF_TYPE_STATIC) {
            out.println("    public Skill getSkill() {");
            out.println("        return skill;");
            out.println("    }");
            out.println();
        }

        // checkWeapon()
        out.println("    private boolean checkWeapon(Combatable unit) {");
        if (requireWeapon.length == 0) {
            out.println("        return true;");
        } else {
            out.println("        if (unit == null || unit.equipments == null || unit.equipments.getWeapon() == null) {");
            out.println("            return false;");
            out.println("        }");
            out.println("        int type = unit.equipments.getWeapon().template.equipment.minorType;");
            out.println("        for (int t : WEAPON) {");
            out.println("            if (t == type) {");
            out.println("                return true;");
            out.println("            }");
            out.println("        }");
            out.println("        return false;");
        }
        out.println("    }");
        out.println();

        // isGood()
        if (good) {
            out.println("    public boolean isGood() {");
            out.println("        return true;");
            out.println("    }");
            out.println();
        }
        // dispelable()
        if (!dispelable) {
            out.println("    public boolean dispelable() {");
            out.println("        return false;");
            out.println("    }");
            out.println();
        }
        
        // keepOnDie()
        if (keepOnDie) {
            out.println("    public boolean keepOnDie() {");
            out.println("        return true;");
            out.println("    }");
            out.println();
        }

        // isAreaBuff()
        if (isAreaBuff) {
            out.println("    public boolean isAreaBuff() {");
            out.println("        return true;");
            out.println("    }");
            out.println();
        }

        // isNeedMerge()
        if (this.mergeStrategy == MERGE_NONE) {
            out.println("    public boolean isNeedMerge() {");
            out.println("        return false;");
            out.println("    }");
            out.println();
        } else {
            // merge(Buff buff)
            out.println("    public boolean merge(Buff buff) {");
            if (this.mergeStrategy == MERGE_ADD) {
                // 如果新BUFF较高级，则用新的覆盖旧的；如果同级，则增加叠加层数并
                // 刷新时间；如果新BUFF较低级，直接丢弃。
                out.println("        if (buff instanceof " + className + ") {");
                out.println("            " + className + " other = (" + className + ")buff;");
                out.println("            if (level < other.level) {");
                out.println("                level = other.level;");
                out.println("                endTime = other.endTime;");
                out.println("                multiple = 1;");
                out.println("                remainSeconds = other.remainSeconds;");
                out.println("                tickInterval = other.tickInterval;");
                out.println("                remainCure = other.remainCure;");
                out.println("                remainDamage = other.remainDamage;");
                out.println("                remainMPCure = other.remainMPCure;");
                out.println("                remainMPDamage = other.remainMPDamage;");
                out.println("                remainAbsorb = other.remainAbsorb;");
                out.println("                effectTimes = 0;");
                out.println("                owner = other.owner;");
                out.println("                source = other.source;");
                generateFieldsCopy(out, "                ");
                out.println("            } else if (level == other.level) {");
                out.println("                endTime = other.endTime;");
                out.println("                if (multiple < 3) {");
                out.println("                    multiple++;");
                out.println("                }");
                out.println("            }");
                out.println("            return true;");
                out.println("        }");
                out.println("        return false;");
            }
            else if (this.mergeStrategy == MERGE_LEVEL) {
                // 高级或同级新BUFF覆盖旧的BUFF，低级BUFF丢弃
                out.println("        if (buff instanceof " + className + ") {");
                out.println("            " + className + " other = (" + className + ")buff;");
                out.println("            if (level <= other.level) {");
                out.println("                level = other.level;");
                out.println("                endTime = other.endTime;");
                out.println("                multiple = 1;");
                out.println("                remainSeconds = other.remainSeconds;");
                out.println("                tickInterval = other.tickInterval;");
                out.println("                remainCure = other.remainCure;");
                out.println("                remainDamage = other.remainDamage;");
                out.println("                remainMPCure = other.remainMPCure;");
                out.println("                remainMPDamage = other.remainMPDamage;");
                out.println("                remainAbsorb = other.remainAbsorb;");
                out.println("                effectTimes = 0;");
                out.println("                owner = other.owner;");
                out.println("                source = other.source;");
                generateFieldsCopy(out, "                ");
                out.println("            }");
                out.println("            return true;");
                out.println("        }");
                out.println("        return false;");
            }
            else if (this.mergeStrategy == MERGE_SAME_SOURCE) {
                // 相同来源的BUFF合并，伤害效果叠加
                out.println("        if (buff instanceof " + className + ") {");
                out.println("            " + className + " other = (" + className + ")buff;");
                out.println("            if (source.equals(other.source)) {");
                out.println("                level = other.level;");
                out.println("                endTime = other.endTime;");
                out.println("                multiple = 1;");
                out.println("                remainSeconds = other.remainSeconds;");
                out.println("                tickInterval = other.tickInterval;");
                out.println("                remainCure += other.remainCure;");
                out.println("                remainDamage += other.remainDamage;");
                out.println("                remainMPCure += other.remainMPCure;");
                out.println("                remainMPDamage += other.remainMPDamage;");
                out.println("                remainAbsorb = other.remainAbsorb;");
                generateFieldsCopy(out, "                ");
                out.println("                return true;");
                out.println("            }");
                out.println("        }");
                out.println("        return false;");
            }
            else if (this.mergeStrategy == MERGE_ALWAYS) {
                // 始终覆盖
                out.println("        if (buff instanceof " + className + ") {");
                out.println("            " + className + " other = (" + className + ")buff;");
                out.println("            level = other.level;");
                out.println("            endTime = other.endTime;");
                out.println("            multiple = 1;");
                out.println("            remainSeconds = other.remainSeconds;");
                out.println("            tickInterval = other.tickInterval;");
                out.println("            remainCure = other.remainCure;");
                out.println("            remainDamage = other.remainDamage;");
                out.println("            remainMPCure = other.remainMPCure;");
                out.println("            remainMPDamage = other.remainMPDamage;");
                out.println("            remainAbsorb = other.remainAbsorb;");
                out.println("            effectTimes = 0;");
                out.println("            owner = other.owner;");
                out.println("            source = other.source;");
                generateFieldsCopy(out, "            ");
                out.println("            return true;");
                out.println("        }");
                out.println("        return false;");
            } else {
                out.println("        return false;");
            }
            out.println("    }");
            out.println();
        }
        
        // resetParams(Combatable owner)
        if (buffType != BUFF_TYPE_DYNAMIC) {
            out.println("    public void resetParams(Combatable owner) {");
            for (EffectParamRef pr : effects.getAllParams()) {
                if (pr.getParamClass() == Integer.class || pr.getParamClass() == Float.class) { 
                    String name = getFieldName(pr.effect, pr.index, pr.effectID, false);
                    String staticName = getFieldName(pr.effect, pr.index, pr.effectID, true);
                    out.println("        " + name + " = " + staticName + "[level];");
                    out.println("        " + name + " = owner.getBuffs().getParamEnhances().enhance(ParamEnhanceSet.TYPE_BUFF_OWNER, " + id + ", \"" + name + "\", " + name + ");");
                }
            }
            out.println("    }");
            out.println();
        }

        // 实现BuffUpdatable接口
        if (ifs.contains("BuffUpdatable")) {
            // load(byte[])
            out.println("    public void load(byte[] bytes) {");
            out.println("        ByteBuffer buf = ByteBuffer.wrap(bytes);");
            out.println("        level = buf.getInt();");
            out.println("        endTime = Time.elapseTime(buf.getLong());");
            out.println("        multiple = buf.getInt();");
            for (EffectParamRef pr : effects.getAllParams()) {
                String name = getFieldName(pr.effect, pr.index, pr.effectID, false);
                String staticName = getFieldName(pr.effect, pr.index, pr.effectID, true);
                out.println("        " + name + " = " + staticName + "[level];");
            }
            out.println("        remainSeconds = buf.getInt();");
            out.println("        tickInterval = buf.getInt();");
            out.println("        remainCure = buf.getInt();");
            out.println("        remainDamage = buf.getInt();");
            out.println("        remainMPCure = buf.getInt();");
            out.println("        remainMPDamage = buf.getInt();");
            out.println("        remainAbsorb = buf.getInt();");
            out.println("        effectTimes = buf.getInt();");
            out.println("    }");
            out.println();
            
            // save()
            out.println("    public byte[] save() {");
            out.println("        ByteBuffer buf = ByteBuffer.allocate(66, false);");
            out.println("        buf.putInt(level);");
            out.println("        buf.putLong(Time.currentTimeMillis(endTime));");
            out.println("        buf.putInt(multiple);");
            out.println("        buf.putInt(remainSeconds);");
            out.println("        buf.putInt(tickInterval);");
            out.println("        buf.putInt(remainCure);");
            out.println("        buf.putInt(remainDamage);");
            out.println("        buf.putInt(remainMPCure);");
            out.println("        buf.putInt(remainMPDamage);");
            out.println("        buf.putInt(remainAbsorb);");
            out.println("        buf.putInt(effectTimes);");
            out.println("        return buf.array();");
            out.println("    }");
            out.println();
            
            // update()
            out.println("    public boolean update() {");
            if (hots.size() != 0) {
                out.println("        if (((remainSeconds - 1) % tickInterval) == 0) {");
                out.println("            int remainTicks = (remainSeconds - 1) / tickInterval + 1;");
                out.println("            int cure = remainCure / remainTicks;");
                out.println("            int dmg = remainDamage / remainTicks;");
                out.println("            int mpcure = remainMPCure / remainTicks;");
                out.println("            int mpdmg = remainMPDamage / remainTicks;");
                out.println("            remainCure -= cure;");
                out.println("            remainDamage -= dmg;");
                out.println("            remainMPCure -= mpcure;");
                out.println("            remainMPDamage -= mpdmg;");
                out.println("            if (cure > 0) {");
                out.println("                Combatable healerUnit = (Combatable)ObjectAccessor.getGameObject(source);");
                out.println("                Combatable targetUnit = (Combatable)ObjectAccessor.getGameObject(owner);");
                out.println("                if (targetUnit != null && targetUnit.isAlive()) {");
                out.println("                    cure = targetUnit.setHp(targetUnit.hp + cure, true);");
                out.println("                    if (healerUnit != null && healerUnit.isAlive()) {");
                out.println("                        ThreatList.addHealThreat(healerUnit, targetUnit, CombatContext.calcHotThreat(cure), false);");
                out.println("                    }");
                out.println("                }");
                out.println("            }");
                out.println("            if (dmg > 0) {");
                out.println("                Combatable sourceUnit = (Combatable)ObjectAccessor.getGameObject(source);");
                out.println("                Combatable targetUnit = (Combatable)ObjectAccessor.getGameObject(owner);");
                out.println("                if (targetUnit != null && targetUnit.isAlive()) {");
                out.println("                    targetUnit.setHp(targetUnit.hp - dmg, true);");
                out.println("                    if (sourceUnit != null && sourceUnit.isAlive()) {");
                out.println("                        ThreatList.addDamageThreat(sourceUnit, targetUnit, dmg, false);");
                out.println("                    }");
                out.println("                    if (targetUnit.hp <= 0) {");
                out.println("                        targetUnit.kill(sourceUnit);");
                out.println("                    }");
                out.println("                }");
                out.println("            }");
                out.println("            if (mpcure > 0) {");
                out.println("                Combatable healerUnit = (Combatable)ObjectAccessor.getGameObject(source);");
                out.println("                Combatable targetUnit = (Combatable)ObjectAccessor.getGameObject(owner);");
                out.println("                if (targetUnit != null && targetUnit.isAlive()) {");
                out.println("                    targetUnit.setMp(targetUnit.mp + mpcure, true);");
                out.println("                    if (healerUnit != null && healerUnit.isAlive()) {");
                out.println("                        ThreatList.addHealThreat(healerUnit, targetUnit, mpcure, false);");
                out.println("                    }");
                out.println("                }");
                out.println("            }");
                out.println("            if (mpdmg > 0) {");
                out.println("                Combatable sourceUnit = (Combatable)ObjectAccessor.getGameObject(source);");
                out.println("                Combatable targetUnit = (Combatable)ObjectAccessor.getGameObject(owner);");
                out.println("                if (targetUnit != null && targetUnit.isAlive()) {");
                out.println("                    targetUnit.setMp(targetUnit.mp - mpdmg, true);");
                out.println("                    if (sourceUnit != null && sourceUnit.isAlive()) {");
                out.println("                        ThreatList.addDamageThreat(sourceUnit, targetUnit, mpdmg, false);");
                out.println("                    }");
                out.println("                }");
                out.println("            }");
                out.println("        }");
                out.println("        remainSeconds--;");
                out.println("        if (remainSeconds <= 0) {");
                out.println("            return true;");
                out.println("        }");
            }
            if (duration[0] >= 0) {
                out.println("        if (endTime <= Time.currTime) {");
                out.println("            return true;");
                out.println("        }");
            }
            EffectConfig shield = effects.findEffect(EffectConfig.MP_SHIELD);
            if (shield != null) {
                out.println("        if (remainAbsorb <= 0) {");
                out.println("            return true;");
                out.println("        }");
            }
            if (effects.findEffect(EffectConfig.CANNOT_MOVE) != null) {
                effects.findEffect(EffectConfig.CANNOT_MOVE).genUpdate(out);
            };
            if (effects.findEffect(EffectConfig.REMOVE_ON_BATTLE_END) != null) {
                EffectConfig eff = effects.findEffect(EffectConfig.REMOVE_ON_BATTLE_END);
                eff.genUpdate(out);
            }
            if (effects.findEffect(EffectConfig.HP_ACTIVE_BUFF) != null) {
                EffectConfig eff = effects.findEffect(EffectConfig.HP_ACTIVE_BUFF);
                eff.genUpdate(out);
            }
            if (effects.findEffect(EffectConfig.LIMIT_EFFECT_TIMES) != null) {
                EffectConfig eff = effects.findEffect(EffectConfig.LIMIT_EFFECT_TIMES);
                eff.genUpdate(out);
            }
            out.println("        return false;");
            out.println("    }");
            out.println();
            
            
            // update2(int)
            out.println("    public boolean update2(int time) {");
            if (hots.size() != 0) {
                if (updateEvenOffline) {
                    out.println("        remainSeconds -= time / 1000;");
                }
                out.println("        if (remainSeconds <= 0) {");
                out.println("            return false;");
                out.println("        }");
            }
            if (duration[0] >= 0) {
                if (!updateEvenOffline) {
                    out.println("        endTime += time;");
                }
                out.println("        if (endTime <= Time.currTime) {");
                out.println("            return false;");
                out.println("        }");
            }
            out.println("        return true;");
            out.println("    }");
            out.println();
        }

        // 实现SkillEnhancer接口
        if (ifs.contains("SkillEnhancer")) {
            List<EffectConfig> skillEffs = effects.findEffects(new int[] { EffectConfig.CHANGE_MP_USE,
                    EffectConfig.CHANGE_CD_TIME, EffectConfig.CHANGE_DISTANCE, EffectConfig.CHANGE_ACT_TIME,
                    EffectConfig.CHANGE_RANGE });
            
            // getAffectSkillIDs()
            out.println("    private TIntHashSet changeSkills;");
            out.println("    public TIntHashSet getAffectSkillIDs() {");
            out.println("        if (changeSkills == null) {");
            out.println("            int[] idarr;");
            out.println("            changeSkills = new TIntHashSet();");
            for (EffectConfig eff : skillEffs) {
                String varName = getFieldName(eff, 0, effects.getEffectID(eff), false);
                out.println("            idarr = Utils.stringToIntArray(" + varName + ", ',');");
                out.println("            for (int sid : idarr) {");
                out.println("                changeSkills.add(sid);");
                out.println("            }");
            }
            out.println("        }");
            out.println("        return changeSkills;");
            out.println("    }");
            out.println();

            // updateCDTime(Skill skill, float cd)
            out.println("    public float updateCDTime(Skill skill, float cd) {");
            EffectConfig cdEff = effects.findEffect(EffectConfig.CHANGE_CD_TIME);
            if (cdEff != null) {
                String varName = getFieldName(cdEff, 1, effects.getEffectID(cdEff), false);
                out.println("            return cd * (1.0f + " + varName + " / 100.0f);");
            }
            else {
                out.println("            return cd;");
            }
            out.println("    }");
            out.println();

            // updateDistance(Skill skill, float distance)
            out.println("    public float updateDistance(Skill skill, float distance) {");
            EffectConfig distEff = effects.findEffect(EffectConfig.CHANGE_DISTANCE);
            if (distEff != null) {
                String varName = getFieldName(distEff, 1, effects.getEffectID(distEff), false);
                out.println("            return distance * (1.0f + " + varName + " / 100.0f);");
            }
            else {
                out.println("            return distance;");
            }
            out.println("    }");
            out.println();

            // updateActTime(Skill skill, float actTime)
            out.println("    public float updateActTime(Skill skill, float actTime) {");
            EffectConfig actTimeEff = effects.findEffect(EffectConfig.CHANGE_ACT_TIME);
            if (actTimeEff != null) {
                String varName = getFieldName(actTimeEff, 1, effects.getEffectID(actTimeEff), false);
                out.println("            return actTime * (1.0f + " + varName + " / 100.0f);");
            }
            else {
                out.println("            return actTime;");
            }
            out.println("    }");
            out.println();

            // updateRange(Skill skill, float range)
            out.println("    public float updateRange(Skill skill, float range) {");
            EffectConfig rangeEff = effects.findEffect(EffectConfig.CHANGE_RANGE);
            if (rangeEff != null) {
                String varName = getFieldName(rangeEff, 1, effects.getEffectID(rangeEff), false);
                out.println("            return range * (1.0f + " + varName + " / 100.0f);");
            }
            else {
                out.println("            return range;");
            }
            out.println("    }");
            out.println();

            // updateMP(Skill skill, float mp)
            out.println("    public float updateMP(Skill skill, float mp) {");
            EffectConfig mpEff = effects.findEffect(EffectConfig.CHANGE_MP_USE);
            if (mpEff != null) {
                String varName = getFieldName(mpEff, 1, effects.getEffectID(mpEff), false);
                out.println("            return mp * (1.0f + " + varName + " / 100.0f);");
            }
            else {
                out.println("            return mp;");
            }
            out.println("    }");
            out.println();
        }
        
        // 实现PropertyEnhancer接口
        if (ifs.contains("PropertyEnhancer")) {
            out.println("    public void enhance(PropertyCalculator pc) {");
            out.println("        if (!checkWeapon(pc.unit)) {");
            out.println("            return;");
            out.println("        }");
            for (int id = 0; id < allEff.length; id++) {
                EffectConfig eff = allEff[id];
                String p1 = getFieldName(eff, 0, eff.effectID, false);
                String p2 = getFieldName(eff, 1, eff.effectID, false);
                eff.genBuffEnhance(out, p1, p2);
            }
            out.println("    }");
            out.println();
        }

        // 实现CombatEffect接口
        if (ifs.contains("CombatEffect")) {
            // public void preHit(CombatContext context, boolean isActive)
            out.println("    public void preHit(CombatContext context, boolean isActive) {");
            out.println("        if (!checkWeapon(isActive ? context.source : context.target)) {");
            out.println("            return;");
            out.println("        }");
            for(EffectConfig eff:effects.effects){
                eff.genBreakCurChainCondition(out);
            }
            
            for (int id = 0; id < allEff.length; id++) {
                EffectConfig eff = allEff[id];
                String p1 = getFieldName(eff, 0, eff.effectID, false);
                String p2 = getFieldName(eff, 1, eff.effectID, false);
                String p3 = getFieldName(eff, 2, eff.effectID, false);
                String p4 = getFieldName(eff, 3, eff.effectID, false);
                String p5 = getFieldName(eff, 4, eff.effectID, false);
                String p6 = getFieldName(eff, 5, eff.effectID, false);
                eff.genBuffPreHit(out, p1, p2, p3, p4, p5, p6);
            }
            out.println("    }");
            out.println();

            // public void postHit(CombatContext context, boolean isActive)
            out.println("    public void postHit(CombatContext context, boolean isActive) {");
            out.println("        if (!checkWeapon(isActive ? context.source : context.target)) {");
            out.println("            return;");
            out.println("        }");
            
            for(EffectConfig eff:effects.effects){
                eff.genBreakCurChainCondition(out);
            }
            
            for (int id = 0; id < allEff.length; id++) {
                EffectConfig eff = allEff[id];
                String p1 = getFieldName(eff, 0, eff.effectID, false);
                String p2 = getFieldName(eff, 1, eff.effectID, false);
                eff.genBuffPostHit(out, p1, p2);
            }
            out.println("    }");
            out.println();

            // public void preDamage(CombatContext context, boolean isActive)
            out.println("    public void preDamage(CombatContext context, boolean isActive) {");
            out.println("        if (!checkWeapon(isActive ? context.source : context.target)) {");
            out.println("            return;");
            out.println("        }");
            for(EffectConfig eff:effects.effects){
                eff.genBreakCurChainCondition(out);
            }
            for (int id = 0; id < allEff.length; id++) {
                EffectConfig eff = allEff[id];
                String p1 = getFieldName(eff, 0, eff.effectID, false);
                String p2 = getFieldName(eff, 1, eff.effectID, false);
                String p3 = getFieldName(eff, 2, eff.effectID, false);
                eff.genBuffPreDamage(out, p1, p2, p3);
            }
            out.println("    }");
            out.println();

            // public void postDamage(CombatContext context, boolean isActive)
            out.println("    public void postDamage(CombatContext context, boolean isActive) {");
            out.println("        if (!checkWeapon(isActive ? context.source : context.target)) {");
            out.println("            return;");
            out.println("        }");
            for(EffectConfig eff:effects.effects){
                eff.genBreakCurChainCondition(out);
            }
            for (int id = 0; id < allEff.length; id++) {
                EffectConfig eff = allEff[id];
                String p1 = getFieldName(eff, 0, eff.effectID, false);
                String p2 = getFieldName(eff, 1, eff.effectID, false);
                String p3 = getFieldName(eff, 2, eff.effectID, false);
                eff.genBuffPostDamage(out, p1, p2, p3);
            }
            out.println("    }");
            out.println();

            // public void finished(CombatContext context, boolean isActive)
            out.println("    public void finished(CombatContext context, boolean isActive) {");
            out.println("        if (!checkWeapon(isActive ? context.source : context.target)) {");
            out.println("            return;");
            out.println("        }");
            for(EffectConfig eff:effects.effects){
                eff.genBreakCurChainCondition(out);
            }
            for (int id = 0; id < allEff.length; id++) {
                EffectConfig eff = allEff[id];
                String p1 = getFieldName(eff, 0, eff.effectID, false);
                String p2 = getFieldName(eff, 1, eff.effectID, false);
                String p3 = getFieldName(eff, 2, eff.effectID, false);
                String p4 = getFieldName(eff, 3, eff.effectID, false);
                String p5 = getFieldName(eff, 4, eff.effectID, false);
                String p6 = getFieldName(eff, 5, eff.effectID, false);
                eff.genBuffFinished(out, p1, p2, p3, p4, p5, p6);
            }
            out.println("    }");
            out.println();
        }
        
        // 实现改变状态
        out.println("    public void changeState(Combatable owner) {");
        for (int id = 0; id < allEff.length; id++) {
            EffectConfig eff = allEff[id];
            if (eff.getType() == EffectConfig.CHANGE_STATE) {
                String p1 = getFieldName(eff, 0, eff.effectID, false);
//              p1 = StateCellEditor.getValue(p1);
                out.println("        owner.states.addState(" + p1 +  ", DURATION[level]);");
            }
        }
        out.println("    }");
        out.println();
        
        // 实现ParamEnhancer接口
        if (ifs.contains("ParamEnhancer")) {
            // void getEnhanceParams(ParamEnhanceSet enhanceSet);
            out.println("    public void getEnhanceParams(ParamEnhanceSet enhanceSet) {");
            for (int id = 0; id < allEff.length; id++) {
                EffectConfig eff = allEff[id];
                switch (eff.getType()) {
                case EffectConfig.CHANGE_PARAM:
                    out.println("        switch (level) {");
                    for (int lvl = 0; lvl < maxLevel; lvl++) {
                        // Effect_ChangeParam: 重复10次（影响参数ParamIndicator，数额float，比例float）
                        out.println("        case " + (lvl + 1) + ":");
                        String p1 = getFieldName(eff, 0, eff.effectID, false);
                        String p2 = getFieldName(eff, 1, eff.effectID, false);
                        String p3 = getFieldName(eff, 2, eff.effectID, false);
                        ParamIndicator[] parr = (ParamIndicator[])eff.getParam(0);
                        EffectParamRef pref = parr[lvl].getParamRef(owner);
                        if (pref == null) {
                            continue;
                        }
                        out.print("            enhanceSet.add(ParamEnhanceSet.");
                        switch (parr[0].type) {
                        case ParamIndicator.TYPE_SKILL_ACTIVE:
                            out.print("TYPE_SKILL_ACTIVE");
                            break;
                        case ParamIndicator.TYPE_SKILL_PASSIVE:
                            out.print("TYPE_SKILL_PASSIVE");
                            break;
                        case ParamIndicator.TYPE_BUFF_OWNER:
                            out.print("TYPE_BUFF_OWNER");
                            break;
                        case ParamIndicator.TYPE_BUFF_SOURCE:
                            out.print("TYPE_BUFF_SOURCE");
                            break;
                        }
                        out.print(", " + parr[0].id);
                        if (pref.effect instanceof GeneralConfig) {
                            out.print(", \"buffTime\"");
                        } else {
                            out.print(", \"" + getFieldName(pref.effect, pref.index, pref.effectID, false) + "\"");
                        }
                        out.print(", " + p2);
                        out.println(", " + p3 + " / 100.0f);");
                        out.println("            break;");
                    }
                    out.println("        }");
                    break;
                }
            }
            out.println("    }");
            out.println();
            
            // void removeEnhanceParams(ParamEnhanceSet enhanceSet);
            out.println("    public void removeEnhanceParams(ParamEnhanceSet enhanceSet) {");
            for (int id = 0; id < allEff.length; id++) {
                EffectConfig eff = allEff[id];
                switch (eff.getType()) {
                case EffectConfig.CHANGE_PARAM:
                    out.println("        switch (level) {");
                    for (int lvl = 0; lvl < maxLevel; lvl++) {
                        // Effect_ChangeParam: 重复10次（影响参数ParamIndicator，数额float，比例float）
                        out.println("        case " + (lvl + 1) + ":");
                        String p1 = getFieldName(eff, 0, eff.effectID, false);
                        String p2 = getFieldName(eff, 1, eff.effectID, false);
                        String p3 = getFieldName(eff, 2, eff.effectID, false);
                        ParamIndicator[] parr = (ParamIndicator[])eff.getParam(0);
                        EffectParamRef pref = parr[lvl].getParamRef(owner);
                        if (pref == null) {
                            continue;
                        }
                        out.print("            enhanceSet.remove(ParamEnhanceSet.");
                        switch (parr[0].type) {
                        case ParamIndicator.TYPE_SKILL_ACTIVE:
                            out.print("TYPE_SKILL_ACTIVE");
                            break;
                        case ParamIndicator.TYPE_SKILL_PASSIVE:
                            out.print("TYPE_SKILL_PASSIVE");
                            break;
                        case ParamIndicator.TYPE_BUFF_OWNER:
                            out.print("TYPE_BUFF_OWNER");
                            break;
                        case ParamIndicator.TYPE_BUFF_SOURCE:
                            out.print("TYPE_BUFF_SOURCE");
                            break;
                        }
                        out.print(", " + parr[0].id);
                        if (pref.effect instanceof GeneralConfig) {
                            out.print(", \"buffTime\"");
                        } else {
                            out.print(", \"" + getFieldName(pref.effect, pref.index, pref.effectID, false) + "\"");
                        }
                        out.print(", " + p2);
                        out.println(", " + p3 + " / 100.0f);");
                        out.println("            break;");
                    }
                    out.println("        }");
                    break;
                }
            }
            out.println("    }");
            out.println();
        }

        out.println("}");
        
        this.implClass = packageName + "." + className;
    }
    
    protected void genConstructorBase(PrintWriter out, List<EffectConfig> hots) {
        if (buffType == BUFF_TYPE_STATIC) {
            out.println("        id = skill.getId();");
        } else {
            out.println("        id = " + id + ";");
        }
        out.println("        instanceID = BuffUtil.getNextID();");
        if (iconID == -1) {
            out.println("        iconID = " + iconID + ";");
        } else if (this.mergeStrategy == MERGE_ADD) {
            // 叠加3层BUFF特殊处理
            out.println("        iconID = " + iconID + " | (multiple << 24);");
        } else {
            out.println("        iconID = " + iconID + " | 0x1000000;");
        }
        if (buffType == BUFF_TYPE_STATIC || buffType == BUFF_TYPE_EQUIP) {
            out.println("        endTime = -1;");
        } else {
            if (hots.size() != 0) {
                out.println("        endTime = Time.currTime + remainSeconds * 1000;");
            } else if (duration[0] >= 0) {
            } else {
                out.println("        endTime = -1;");
            }
        }
        out.println("        name = \"" + Utils.reverseConv(title) + "\";");
        if (groupID != 0) {
            out.println("        groupID = " + groupID + ";");
        }
    }

    protected void genConstructor(PrintWriter out, String className, List<EffectConfig> hots) {
        // constructor
        if (buffType == BUFF_TYPE_STATIC) {
            out.println("    public " + className + "(Skill skl, int lvl) {");
            genConstructorBase(out, hots);
            out.println("        int dmg = 0;");
            out.println("        skill = skl;");
            out.println("        level = lvl;");
            out.println("        multiple = 1;");
            for (EffectParamRef pr : effects.getAllParams()) {
                String name = getFieldName(pr.effect, pr.index, pr.effectID, false);
                String staticName = getFieldName(pr.effect, pr.index, pr.effectID, true);
                out.println("        " + name + " = " + staticName + "[level];");
            }
        } else if (buffType == BUFF_TYPE_EQUIP) {
            out.println("    public " + className + "(int lvl) {");
            genConstructorBase(out, hots);
            out.println("        int dmg = 0;");
            out.println("        level = lvl;");
            out.println("        multiple = 1;");
            for (EffectParamRef pr : effects.getAllParams()) {
                String name = getFieldName(pr.effect, pr.index, pr.effectID, false);
                String staticName = getFieldName(pr.effect, pr.index, pr.effectID, true);
                out.println("        " + name + " = " + staticName + "[level];");
            }
        } else {
            out.println("    public " + className + "(int lvl, Combatable src, Combatable tgt, int dmg) {");
            genConstructorBase(out, hots);
            out.println("        if (src != null) {");
            out.println("            source = src.ref();");
            out.println("        }");
            out.println("        if (tgt != null) {");
            out.println("            owner = tgt.ref();");
            out.println("        }");
            out.println("        level = lvl;");
            out.println("        endTime = Time.currTime + BuffUtil.enhanceParam(src, tgt, " + id + ", \"buffTime\", DURATION[level]);");
            out.println("        round_times = ROUND_TIMES[level];");
            out.println("        battle_times = BATTLE_TIMES[level];");
            out.println("        multiple = 1;");
            for (EffectParamRef pr : effects.getAllParams()) {
                String name = getFieldName(pr.effect, pr.index, pr.effectID, false);
                String staticName = getFieldName(pr.effect, pr.index, pr.effectID, true);
                out.println("        " + name + " = " + staticName + "[level];");
                if (pr.getParamClass() == Integer.class || pr.getParamClass() == Float.class) {
                    out.println("        " + name + " = BuffUtil.enhanceParam(src, tgt, " + id + ", \"" + name + "\", " + name + ");");
                }
            }
        }
        
        if (hots.size() != 0) {
            if (buffType == BUFF_TYPE_STATIC || buffType == BUFF_TYPE_EQUIP) {
                throw new IllegalArgumentException("永久性BUFF不允许使用可能会消失的效果。");
            }
            String varNameSec = getFieldName(hots.get(0), 0, effects.getEffectID(hots.get(0)), false);
            String varNameInt = getFieldName(hots.get(0), 1, effects.getEffectID(hots.get(0)), false);
            out.println("        remainSeconds = " + varNameSec + ";");
            out.println("        tickInterval = " + varNameInt + ";");
            Iterator<EffectConfig> itor = hots.iterator();
            while (itor.hasNext()) {
                EffectConfig hot = itor.next();
                String varNameT = getFieldName(hot, 2, effects.getEffectID(hot), false);
                String varNameP = getFieldName(hot, 3, effects.getEffectID(hot), false);
                if (hot.getType() == EffectConfig.D_O_T) {
                    out.println("        remainDamage = "    + varNameT + ";");
                    out.println("        remainDamage += "   + varNameP + " * dmg / 100.0f;");
                } else if (hot.getType() == EffectConfig.H_O_T) {
                    out.println("        remainCure = "      + varNameT + ";");
                    out.println("        remainCure += "     + varNameP + " * dmg / 100.0f;");
                } else if (hot.getType() == EffectConfig.MPDOT) {
                    out.println("        remainMPDamage = "  + varNameT + ";");
                    out.println("        remainMPDamage += " + varNameP + " * dmg / 100.0f;");
                } else if (hot.getType() == EffectConfig.MPHOT) {
                    out.println("        remainMPCure = "    + varNameT + ";");
                    out.println("        remainMPCure += "   + varNameP + " * dmg / 100.0f;");
                }
            }
        }
        if (effects.findEffects(new int[] { EffectConfig.CANNOT_MOVE, EffectConfig.LIMIT_EFFECT_TIMES }).size() > 0) {
            if (buffType == BUFF_TYPE_STATIC || buffType == BUFF_TYPE_EQUIP) {
                throw new IllegalArgumentException("永久性BUFF不允许使用可能会消失的效果。");
            }
        }

        // 处理法力护盾的初始值：剩余吸收
        EffectConfig shield = effects.findEffect(EffectConfig.MP_SHIELD);
        if (shield != null) {
            if (buffType == BUFF_TYPE_STATIC || buffType == BUFF_TYPE_EQUIP) {
                throw new IllegalArgumentException("永久性BUFF不允许使用护盾效果。");
            }
        }
        
        for(EffectConfig eff:effects.getAllEffects()){
            eff.genConstructorCodes(out);
        }
        
        out.println("    }");
        out.println("");
                
    }

    /*
     * 生成参数局部变量全部拷贝的代码。
     */
    protected void generateFieldsCopy(PrintWriter out, String indent) {
        for (EffectParamRef pr : effects.getAllParams()) {
            String name = getFieldName(pr.effect, pr.index, pr.effectID, false);
            Object param = pr.effect.getParam(pr.index);
            if (param instanceof int[]) {
                out.println(indent + name + " = other.getIntAttr(\"" + name + "\");");
            } else if (param instanceof float[]) {
                out.println(indent + name + " = other.getFloatAttr(\"" + name + "\");");
            } else if (param instanceof String[]) {
                out.println(indent + name + " = other.getStringAttr(\"" + name + "\");");
            } else if (param instanceof ParamIndicator[]) {
                out.println(indent + name + " = other.getStringAttr(\"" + name + "\");");
            } else if (param instanceof int[][]) {
                out.println(indent + name + " = other.getIntsAttr(\"" + name + "\");");
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    /**
     * 取得存储某个效果参数的变量名。
     */
    public static String getFieldName(EffectConfig eff, int index, int id, boolean isStatic) {
        try {
            String name = ProjectData.getActiveProject().effectConfigManager.getTypeParames(eff.getClass())[index];
            if (id >= 0) {
                name += "_" + id;
            }
            if (isStatic) {
                return name.toUpperCase();
            } else {
                return name;
            }
        } catch (Exception e) {
            return "";
        }
    }

    /*
     * 根据BUFF配置的效果判断此BUFF类需要实现的接口。
     */
    protected Set<String> getJavaInterface() {
        HashSet<String> ret = new HashSet<String>();
        if (buffType == BUFF_TYPE_STATIC) {
            ret.add("SkillBuff");
        }
        if (duration[0] >= 0) {
            ret.add("BuffUpdatable");
        }
        for (EffectConfig eff : effects.getAllEffects()) {
            BuffGetJavaInterface.make(eff, ret);
            try {
                String javaInterface = eff.getJavaInterface();
                if("-1".equals(javaInterface) == false) {
                    ret.add(eff.getJavaInterface());                    
                }
            }
            catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 生成静态数组变量定义。
     */
    public static void generateStaticArray(PrintWriter out, String name, Object arr) {
        if (arr instanceof int[]) {
            int[] iarr = (int[]) arr;
            out.println("    public static int[] " + name + " = {");
            out.print("        0");
            for (int v : iarr) {
                out.print(", " + v);
            }
            out.println();
            out.println("    };");
        }
        else if (arr instanceof float[]) {
            float[] farr = (float[]) arr;
            out.println("    public static float[] " + name + " = {");
            out.print("        0.0f");
            for (float v : farr) {
                out.print(", " + v + "f");
            }
            out.println();
            out.println("    };");
        }
        else if (arr instanceof String[]) {
            String[] sarr = (String[]) arr;
            out.println("    public static String[] " + name + " = {");
            out.print("        \"\"");
            for (String v : sarr) {
                out.print(", \"" + Utils.reverseConv(v) + "\"");
            }
            out.println();
            out.println("    };");
        }
        else if (arr instanceof ParamIndicator[]) {
            ParamIndicator[] sarr = (ParamIndicator[]) arr;
            out.println("    public static String[] " + name + " = {");
            out.print("        \"\"");
            for (ParamIndicator v : sarr) {
                out.print(", \"" + Utils.reverseConv(v.toString()) + "\"");
            }
            out.println();
            out.println("    };");
        }
        else if (arr instanceof int[][]) {
            int[][] iarr = (int[][])arr;
            out.println("    public static int[][] " + name + " = {");
            out.print("        {}");
            for (int[] v : iarr) {
                out.print(", { ");
                for (int i = 0; i < v.length; i++) {
                    if (i > 0) {
                        out.print(", ");
                    }
                    out.print(v[i]);
                }
            }
            out.println(" }");
            out.println("    };");
        }
    }

    /**
     * 查找一个BUFF的名字。
     * @param project
     * @param buffID
     * @return
     */
    public static String toString(ProjectData project, int buffID) {
        BuffConfig q = (BuffConfig)project.findObject(BuffConfig.class, buffID);
        if (q == null) {
            return "无效效果";
        } else {
            return q.toString();
        }
    }
    
    /**
     * 查找一组Buff的名字
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
                BuffConfig sc = (BuffConfig)project.findObject(BuffConfig.class, sid);
                if (names.length() > 0) {
                    names.append(",");
                }
                if (sc == null) {
                    names.append("无效Buff");
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
