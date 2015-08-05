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

import com.pip.game.data.autocoding.AutoEffectFromPreDefined;
import com.pip.game.data.effects0.EffectRejectException;
import com.pip.game.data.effectsListTest.ListClasses;
import com.pip.game.editor.skill.ParamIndicator;
import com.pip.util.Utils;

/**
 * 技能/BUFF效果管理器。
 */
public class EffectConfigManager implements Cloneable {
    public HashMap<Integer, Class> TYPE_CLASSES;
    protected HashMap<Class, EffectSetup> CLASS_CONFIGS;
    
    public EffectConfigManager(ClassLoader classLoader, String dataDir) throws Exception {
        List<Field> fields = AutoEffectFromPreDefined.getFiledNames();
        TYPE_CLASSES = new HashMap<Integer, Class>();
//        for (Field field : fields) {
//            String clzName = "com.pip.game.data.effects0.Effect_" + field.getName();
//            Class clz = Class.forName(clzName);
//
//            int type = field.getInt(clz);
//            TYPE_CLASSES.put(type, clz);
//        }
        // customer effects
        CLASS_CONFIGS = new HashMap<Class, EffectSetup>();
        
        File ef = new File(dataDir + File.separator + "Skill/effects.xml");
        if (ef.exists() == false) {
            return;
        }
        Document doc = Utils.loadDOM(ef);
        List<Element> effs = doc.getRootElement().getChildren();
        StringBuilder sb = new StringBuilder();
        for (Element elem : effs) {
            String className = elem.getAttributeValue("class");
            Class clz = classLoader.loadClass(className);
            Integer type = new Integer(elem.getAttributeValue("id"));
            if (TYPE_CLASSES.containsKey(type)) {
                sb.append("Type:" + type + " className:" + className + "\n");
            }
//            int idDefInClass = getTypeId(clz);
//            if (type.intValue() != idDefInClass) {
//                throw new Exception("xml中的类型ID与类中的ID不一致:" + className);
//            }
            TYPE_CLASSES.put(type, clz);
            
            EffectSetup es = new EffectSetup();
            es.id = type;
            es.typeNames = elem.getAttributeValue("typeNames").replace(" ", "").split(",");
            es.typeParames = elem.getAttributeValue("typeParames").replace(" ", "").split(",");
            es.supportBuff = getOrRelation(elem.getAttributeValue("buffType"));
            es.supportedSkillType = getOrRelation(elem.getAttributeValue("skillType"));
            
            CLASS_CONFIGS.put(clz, es);
                        
        }
        //永久buff
//            TYPE_CLASSES.put(-1, StaticGeneralConfig.class);
        EffectSetup es = new EffectSetup();
        es.id = -1;
        es.typeNames = new String[0];
        es.typeParames = new String[0];
        es.supportBuff = BuffConfig.BUFF_TYPE_STATIC;
        es.supportedSkillType = 0;
        CLASS_CONFIGS.put(StaticGeneralConfig.class, es);
        
        //临时buff
//            TYPE_CLASSES.put(-2, DynamicGeneralConfig.class);
        es = new EffectSetup();
        es.id = -2;
        es.typeNames = new String[]{"","","",""};
        es.typeParames = new String[]{ "duration", "round_times","battle_times","times"};
        es.supportBuff = BuffConfig.BUFF_TYPE_DYNAMIC;
        es.supportedSkillType = 0;
        CLASS_CONFIGS.put(DynamicGeneralConfig.class, es);
        
        //装备buff            
//            TYPE_CLASSES.put(-3, EquipGeneralConfig.class);
        es = new EffectSetup();
        es.id = -3;
        es.typeNames = new String[]{"",""};
        es.typeParames = new String[]{"buff_effect_value" };
        es.supportBuff = BuffConfig.BUFF_TYPE_EQUIP;
        es.supportedSkillType = 0;
        CLASS_CONFIGS.put(EquipGeneralConfig.class, es);
        
        if (sb.length() > 0) {
            throw new Exception("重复的效果类型:" + sb.toString());
        }
    }
    
    protected static int getOrRelation(String str) {
        String[] ors = str.replace(" ", "").split("\\|");
        int ret = 0;
        for(int i=0; i<ors.length; i++) {
            ret |= Integer.parseInt(ors[i]);
        }
        
        return ret;
        
    }

    public String[] getTypeNames(Class clz) throws Exception {
//        Method getTypeNamesMethod = clz.getMethod("getTypeNames");
//        Object obj = getTypeNamesMethod.invoke(clz);
//        return (String[]) obj;
        
        return CLASS_CONFIGS.get(clz).typeNames;
    }

    public String[] getTypeParames(Class clz) throws Exception {
//        Method getTypeParamesMethod = clz.getMethod("getTypeParames");
//        Object obj2 = getTypeParamesMethod.invoke(clz);
//        return (String[]) obj2;
        
        return CLASS_CONFIGS.get(clz).typeParames;
    }

    public int getTypeId(Class clz) {
//        Method getTypeParamesMethod = clz.getMethod("getId");
//        Object obj2 = getTypeParamesMethod.invoke(clz);
//        return ((Integer) obj2).intValue();
        return CLASS_CONFIGS.get(clz).id;
    }

    public int getBuffSupport(Class clz) {
//        Field buffSupportField = clz.getField("supportBuff");
//        return buffSupportField.getInt(clz);
        try {
            int ii = CLASS_CONFIGS.get(clz).supportBuff;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return CLASS_CONFIGS.get(clz).supportBuff;
    }

    public int getSkillSupport(Class clz) {
//        try {
//            Field buffSupportField = clz.getField("supportedSkillType");
//            return buffSupportField.getInt(clz);            
//        }
//        catch (NoSuchFieldException e) {
//            System.out.println(clz);
//        }
        return CLASS_CONFIGS.get(clz).supportedSkillType;
    }

    /**
     * 创建指定类型的效果对象
     * 
     * @param type
     * @param levelCount
     * @return
     * @throws Exception
     */
    public EffectConfig create(int type, int levelCount) throws Exception {
        Constructor c = TYPE_CLASSES.get(type).getConstructors()[0];
        EffectConfig ret = (EffectConfig) c.newInstance(type);
        ret.manager = this;
        ret.setLevelCount(levelCount);
        return ret;
    }

    /**
     * 从XML中载入效果。
     * 
     * @param elem
     * @return
     */
    public EffectConfig load(Element elem, int maxLevel) throws Exception {
        int type = Integer.parseInt(elem.getAttributeValue("type"));
        EffectConfig ret = create(type, maxLevel);
        ret.load(elem, maxLevel);
        return ret;
    }
}
