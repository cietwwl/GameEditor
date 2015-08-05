package com.pip.game.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import com.pip.game.data.equipment.AttributeCalculator;
import com.pip.game.data.equipment.BodyPart;
import com.pip.game.data.equipment.EquipmentType;
import com.pip.game.data.item.ItemEffectConfig;
import com.pip.game.data.item.ItemTypeConfig;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.quest.TemplateManager;
import com.pip.game.editor.skill.DescriptionPatternConfig;
import com.pip.mapeditor.data.MapFile;
import com.pip.util.Utils;
import com.pipimage.image.PipAnimateSet;
import com.pipimage.image.PipImage;

public class ProjectConfig {    
    //原Constants.java中的常量
    public static final String[] COMBO_YES_NO = {"否","是"};

    // 支持标记的对象类
    public Class[] supportDataClasses;
    
    /**
     * 在扩展项目中保存被扩展的核心类
     * 用于解决List<DataObject> list = ((ProjectData)inputElement).getDataListByType(SkillConfig.class); //以及相关方法的扩展
     * 
     * 扩展后的类应该与核心类具有相同的数据类型
     * 因为在功能实现时supportDataClasses中的类的用法还有以下几种
     * 
     * 1. obj instanceof SkillConfig
     * 2. SkillConfig q = (SkillConfig)list.get(i);
     * 3. private boolean matchCondition(SkillConfig q) //作为参数传递
     * 
     */
    public Class[] supportDataSuperClasses;
    
    /** 根据核心类可以查找到扩展类 */
    public HashMap<Class, Class> supportDataSuperClassMap = new HashMap<Class, Class>();
    
    // 不同类型对象对应的XML根标签
    public String[] dataRootTags ;

    // 不同类型对象对应的XML标签
    public String[] dataTags;

    // 不同类型对象对应的XML文件相对路径
    public String[] dataFiles ;

    // 字典对象类
    public Class[] dictDataClasses;
//    = { NPCType.class, Faction.class, Rank.class };
    // 字典对象类对应的XML标签
    public String[] dictDataTags;
//    =  { "npctype", "faction", "rank" };
    // 字典对象对应的XML文件相对路径
    public String[] dictDataFiles ;
//    = { "npctypes.xml", "factions.xml", "ranks.xml" };

    // 支持编辑的对象类
    public Class[] editableClasses;
    // 存储各数据类型对应的编辑器ID
    public HashMap<Class, String> dataTypeEditors = new HashMap<Class, String>();
    
    // 存储各数据类型对应的标题名称
    public HashMap<Class, String> dataTypeNames = new HashMap<Class, String>();
    
    // 存储各数据类型对应的创建Wizard类
    public final HashMap<Class, Class> dataTypeCreateWizards = new HashMap<Class, Class>();
    
    //存储各数据类型对应的扩展editor类
    private HashMap<Class, Class> dataTypeExtendEditor = new HashMap<Class, Class>();
    
    //存储各数据类型对应的计算器
    private HashMap<Class, IDataCalculator> dataTypeCalc = new HashMap<Class, IDataCalculator>();
    
    /**
     * 怪物掉落经验，从0级开始
     */
    public int[] LEVEL_EXP;
    
    /**
     * 怪物掉落金钱，从0级开始
     */
    public int[] LEVEL_MONEY;
    
    /** 品质选项 */
    public String[] COMBO_QUALITY;
    
    /** 品质对应的颜色 */
    public int[] QUALITY_COLOR;
    
    /** 时效类型选项 */
    public String[] COMBO_TIME_TYPE;
    
    /** 全部装备类型选项 */
    public String[] COMBO_PLACE;
    public int[] EQU_PLACE_ICON;
    
    public float[] PLACE_WEIGHTS;
    public int[][] PLACE_IMAGE_STEP;

    /** 全部装备类型名称 */
    public String[] PLACE_NAMES;
    
    /** 绑定选项 */
    public String[] COMBO_BIND;
    
    /** 职业选项 */
    public String[] PLAYER_CLAZZ;
    public String[] PLAYER_CLAZZ_RAW;
    
    /** 人物基本属性 **/
    public String[] PLAYER_ATTR;
    
    public String[] COMBO_WEAPON_TYPE;
    public String[] WEAPON_DIR;
    public String[] WEAPON_KEY;
    public int[] WEAPON_ICON;
    public float[][] WEAPON_RANGE;
    
    /**
     * 储存装备部位以及对应的key值
     */
    public HashMap<String,BodyPart> bodyPartMap = new LinkedHashMap<String, BodyPart>();
    
    /**
     * 储存角色种族（人，妖，仙，怪）以及对应的key值
     */
    public HashMap<String,String> jobMap = new HashMap<String, String>();
    public HashMap<String, String> jobCodeMap = new HashMap<String, String>();
        
    /** 地面人物层的出口动画 **/
    public PipAnimateSet exitAni;
    
    /** 大版地面人物层的出口动画 **/
    public PipAnimateSet exitlAni;
    
    /** 技能动画组 **/
    public String[][] skillAnimateGrp;
    
    /** 粒子效果列表 */
    public String[] particleFiles;
    
    /** 粒子效果分类列表 */
    public Map<String, List<String>> particleFilesGroup = new HashMap<String, List<String>>();
    
    /** 图标系列。每个系列有一个名字标识，包含一组图标文件。第一个图标文件的图标ID为0-999，第二个为1000-1999，依此类推。 */
    public Map<String, PipImage[]> iconSeries = new HashMap<String, PipImage[]>();
    /** 动画系列。每个系列有一个名字标识，包含一组图标文件。第一个图标文件的图标ID为0-999，第二个为1000-1999，依此类推。 */
    public Map<String, PipAnimateSet[]> ctsSeries = new HashMap<String, PipAnimateSet[]>();
    
    /** 库模式下图片资源的位置 **/
    public String pipLibDir;
    
    /** 本项目支持的地图文件格式 */
    public Map<Integer, MapFormat> mapFormats = new HashMap<Integer, MapFormat>();
    /** 本项目支持的动画文件格式 */
    public Map<Integer, AnimationFormat> animationFormats = new HashMap<Integer, AnimationFormat>();
    /** 本项目支持的机型 */
    public Map<String, ClientModel> clientModels = new HashMap<String, ClientModel>();
    /** 缺省机型 */
    public ClientModel defaultModel;

    /** 扩展项目的classloader，用于载入扩展项目中有而核心项目中没有的类 **/
    private ClassLoader projectClassLoader;
    /** 此项目用的PQEUtils */
    public PQEUtils pqeUtils;
    /** 此项目用的TemplateManager */
    public TemplateManager templateManager;
    /** 此项目用的属性配置 */
    public AttributeCalculator attrCalc;
    /** 物品使用效果配置 */
    public ItemEffectConfig[] itemEffects;
    /** 物品使用效果配置查找表 */
    protected Map<Integer, ItemEffectConfig> itemEffectSearchTable;
    /** 物品类型配置 */
    public ItemTypeConfig[] itemTypes;
    /** 物品类型配置查找表 */
    protected Map<Integer, ItemTypeConfig> itemTypeSearchTable;
    /** 技能BUFF描述中用到的系统变量 */
    public DescriptionPatternConfig descPatternConfig;
    
    //项目里扩展BaseQuest和任务自动生成
    public String BaseQuestClass;
    public String AutoGenQuestClass;
    
    public String BaseAIClass;
    public String AutoGenAIClass;
    
    public String gameMapInfoClass;
    
    public String gameMapNpcClass;
    
    public String gameMapExitClass;
    
    public String richTextEditorClass;
    
    /** 所属项目 */
    private ProjectData owner;
    // 是否保存关卡时自动生成pkg文件，缺省为true。如果项目需要手动优化pkg文件，那么需要把这个选项设置为false，以防覆盖掉优化后的pkg文件
    public boolean autoGeneratePackage = true;
    // 是否在保存关卡时使用真彩色模式，真彩色模式能够为手工优化提供更优质的原始资源，而且速度更快
    public boolean useTrueColourForMap = false;
    // 文件版本号算法，从CRC8过度到CRC16的时间
    public long crc8Tocrc16Time = 1324396800228L;
    // 关卡中是否包含建筑信息（用于明珠城项目）
    public boolean includeBuildingInPackage = false;
    // 怪物组中怪物排列位置描述
    public String monsterPositionDescription = null;
    // 是否尝试合并pip文件
    public boolean tryMergePip = true;
    // DataObjectInput是否使用长名
    public boolean useLongName;
    // 脚本所使用的版本(默认为3)
    public int scriptVersion;
    // 幻象II使用，战斗类型 
    public byte BATTLE_TYPE ;  
    // 是否无视J2ME转换处理
    public boolean ignoreJ2ME = false;
    // 选中的NPC的外框是否画成圆形
    public boolean npcBoxDrawRound = false;
    
    public ProjectConfig(ProjectData owner) {
        this.owner = owner;
    }
    
    public ProjectData getOwner() {
        return owner;
    }
    
    public ClassLoader getProjectClassLoader(){
        return projectClassLoader;
    }
    
    public void load(ClassLoader classLoader) throws Exception {
        projectClassLoader = classLoader;
        Document doc = Utils.loadDOM(new File(owner.baseDir, "game.conf.xml"));
        BaseQuestClass = doc.getRootElement().getAttributeValue("BaseQuestClass");
        AutoGenQuestClass = doc.getRootElement().getAttributeValue("AutoGenQuestClass");
        BaseAIClass = doc.getRootElement().getAttributeValue("BaseAIClass");
        AutoGenAIClass = doc.getRootElement().getAttributeValue("AutoGenAIClass");
        gameMapInfoClass = doc.getRootElement().getAttributeValue("GameMapInfoClass");
        gameMapNpcClass = doc.getRootElement().getAttributeValue("GameMapNpcClass");
        gameMapExitClass = doc.getRootElement().getAttributeValue("GameMapExitClass");
        richTextEditorClass = doc.getRootElement().getAttributeValue("RichTextEditorClass");
        
        List list = doc.getRootElement().getChildren("res");
        list = ((Element )list.get(0)).getChildren("res");
        for(int i=0; i<list.size(); i++) {
            Element elem = (Element)list.get(i);
            String name = elem.getAttributeValue("name");
            String value = elem.getAttributeValue("value");
            if("exit".equals(name)) {
                exitAni = new PipAnimateSet();
                exitAni.load(new File(owner.baseDir, value));
            } else if("exitl".equals(name)) {
                exitlAni = new PipAnimateSet();
                exitlAni.load(new File(owner.baseDir, value));
            }
        }
        list = doc.getRootElement().getChildren("res");
        list = ((Element )list.get(0)).getChildren("iconseries");
        for(int i=0; i<list.size(); i++) {
            Element elem = (Element)list.get(i);
            String name = elem.getAttributeValue("name");
            List list2 = elem.getChildren("iconfile");
            PipImage[] imgs = new PipImage[list2.size()];
            for (int j = 0; j < list2.size(); j++) {
                imgs[j] = new PipImage();
                imgs[j].load(new File(owner.baseDir, ((Element)list2.get(j)).getAttributeValue("path")).getAbsolutePath());
            }
            iconSeries.put(name, imgs);
        }
        //cts系列资源
        list = doc.getRootElement().getChildren("res");
        list = ((Element )list.get(0)).getChildren("ctsseries");
        for(int i=0; i<list.size(); i++) {
            Element elem = (Element)list.get(i);
            String name = elem.getAttributeValue("name");
            List list2 = elem.getChildren("ctsfile");
            PipAnimateSet[] imgs = new PipAnimateSet[list2.size()];
            for (int j = 0; j < list2.size(); j++) {
                imgs[j] = new PipAnimateSet();
                imgs[j].load(new File(owner.baseDir, ((Element)list2.get(j)).getAttributeValue("path")));
            }
            ctsSeries.put(name, imgs);
        }
        
        list = doc.getRootElement().getChildren("skillAnimates");
        skillAnimateGrp = new String[list.size()][];
        for(int i=0; i<list.size(); i++) {
            List skillAnis = ((Element )list.get(i)).getChildren("skillAnimate");
            skillAnimateGrp[i] = new String[skillAnis.size()];
            for(int j=0; j<skillAnis.size(); j++) {
                Element elem = (Element)skillAnis.get(j);
                skillAnimateGrp[i][j] = elem.getAttributeValue("file");  
            }
        }
        
        Element psListElem = doc.getRootElement().getChild("particle_effects");
        if (psListElem != null) {
            list = psListElem.getChildren("particle_effect");
            particleFiles = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                particleFiles[i] = ((Element)list.get(i)).getAttributeValue("file");
                
                //粒子效果应用环境信息
                String context = ((Element)list.get(i)).getAttributeValue("context");
                if(context!=null){
                	String[] groups = context.split("\\|");
                	for(String group : groups){
                		List tmpList = particleFilesGroup.get(group);
                		if(tmpList==null){
                			tmpList = new ArrayList<String>();
                        	particleFilesGroup.put(group, tmpList);
                		}
                		tmpList.add(particleFiles[i]);
                	}
                }
            }
        } else {
            particleFiles = new String[0];
        }
        
        Element clzes = doc.getRootElement().getChild("supportDataClasses");
        list = clzes.getChildren("supportDataClass");
        supportDataClasses = new Class[list.size()];
        supportDataSuperClasses = new Class[list.size()];
        dataRootTags = new String[supportDataClasses.length];
        dataTags = new String[supportDataClasses.length];
        dataFiles = new String[supportDataClasses.length];
        
        for (int i=0; i<supportDataClasses.length; i++) {
            try {
            Element elem = (Element)list.get(i);
            String className = elem.getAttributeValue("className");
            
            supportDataClasses[i] = projectClassLoader.loadClass(className);//Class.forName(className);
            
            className = elem.getAttributeValue("baseClassName");
            if(className == null) {
                supportDataSuperClasses[i] = supportDataClasses[i];
            } else {
                supportDataSuperClasses[i] = projectClassLoader.loadClass(className);
            }
            supportDataSuperClassMap.put(supportDataSuperClasses[i], supportDataClasses[i]);
            
            dataTags[i] = elem.getAttributeValue("dataTag");
            dataRootTags[i] = elem.getAttributeValue("dataRootTag");
            dataFiles[i] = elem.getAttributeValue("dataFile");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
     
        
        list = doc.getRootElement().getChildren("dictDataClasses");
        list = ((Element )list.get(0)).getChildren("dictDataClass");
        dictDataClasses = new Class[list.size()];
        dictDataTags = new String[dictDataClasses.length];
        dictDataFiles = new String[dictDataClasses.length];
        for (int i=0; i<dictDataClasses.length; i++) {
            Element elem = (Element)list.get(i);
            String className = elem.getAttributeValue("className");
            dictDataClasses[i] = projectClassLoader.loadClass(className);
            dictDataTags[i] = elem.getAttributeValue("dictDataTag");
            dictDataFiles[i] = elem.getAttributeValue("dictDataFile");
        }

        list = doc.getRootElement().getChildren("editableClasses");
        list = ((Element )list.get(0)).getChildren("editableClass");
        editableClasses = new Class[list.size()];
        for (int i=0; i<editableClasses.length; i++) {
            
            Element elem = (Element)list.get(i);
            String className = elem.getAttributeValue("className");
            editableClasses[i] = projectClassLoader.loadClass(className);
            dataTypeEditors.put(editableClasses[i], elem.getAttributeValue("editorId"));
            dataTypeNames.put(editableClasses[i], elem.getAttributeValue("name"));
            
            className = elem.getAttributeValue("createWizardClass");
            dataTypeCreateWizards.put(editableClasses[i], projectClassLoader.loadClass(className));
            
            className = elem.getAttributeValue("extendEditorClass");            
            if(!owner.serverMode && className != null && className.equals("")==false) {
                dataTypeExtendEditor.put(editableClasses[i], projectClassLoader.loadClass(className));
            }
            
            className = elem.getAttributeValue("calcClass");
            if(className != null) {
                dataTypeCalc.put(editableClasses[i], (IDataCalculator)projectClassLoader.loadClass(className).newInstance());                    
            }
        }
        
        //原Constants.java中的常量
        Element elemRoot = doc.getRootElement().getChild("constants");
        Element elem;
        
        String attr = elemRoot.getAttributeValue("LEVEL_EXP");
        String[] values = attr.split(",");
        LEVEL_EXP = new int[values.length];
        for(int i=0; i<values.length; i++) {
            LEVEL_EXP[i] = Integer.parseInt(values[i].trim());
        }
        
        attr = elemRoot.getAttributeValue("LEVEL_MONEY");
        values = attr.split(",");
        LEVEL_MONEY = new int[values.length];
        for(int i=0; i<values.length; i++) {
            LEVEL_MONEY[i] = Integer.parseInt(values[i].trim());
        }
        
        attr = elemRoot.getAttributeValue("COMBO_QUALITY");
        COMBO_QUALITY = attr.split(",");
        
        attr = elemRoot.getAttributeValue("QUALITY_COLOR");
        values = attr.split(",");
        QUALITY_COLOR = new int[values.length];
        for(int i=0; i<values.length; i++) {
            QUALITY_COLOR[i] = Integer.parseInt(values[i].trim().substring(2), 16);
        }
        
        attr = elemRoot.getAttributeValue("COMBO_TIME_TYPE");
        COMBO_TIME_TYPE = attr.split(",");
        
        attr = elemRoot.getAttributeValue("COMBO_BIND");
        COMBO_BIND = attr.split(",");
        
        attr = elemRoot.getAttributeValue("PLAYER_CLAZZ");
        PLAYER_CLAZZ = attr.split(",");
        PLAYER_CLAZZ_RAW = new String[PLAYER_CLAZZ.length - 1];
        System.arraycopy(PLAYER_CLAZZ, 1, PLAYER_CLAZZ_RAW, 0, PLAYER_CLAZZ.length - 1);
        
        attr = elemRoot.getAttributeValue("PLAYER_ATTR");
        PLAYER_ATTR = attr.split(",");
        
        pipLibDir = elemRoot.getAttributeValue("pipLibDir");
        if(pipLibDir == null || "".equals(pipLibDir)) {
            pipLibDir = "./pipLib/";
        }
        
        loadEquipmenAndRacetDef(owner.baseDir);

//        list = doc.getRootElement().getChildren("res");
//        list = ((Element )list.get(0)).getChildren("res");
//        for(int i=0; i<list.size(); i++) {
//            elem = (Element)list.get(i);
//            String name = elem.getAttributeValue("name");
//            String value = elem.getAttributeValue("value");
//            if("exit".equals(name)) {
//                exitAni = new PipAnimateSet();
//                exitAni.load(new File(owner.baseDir, value));
//            } else if("exitl".equals(name)) {
//                exitlAni = new PipAnimateSet();
//                exitAni.load(new File(owner.baseDir, value));
//            }
//        }
//        
//        list = doc.getRootElement().getChildren("skillAnimates");
//        skillAnimateGrp = new String[list.size()][];
//        for(int i=0; i<list.size(); i++) {
//            List skillAnis = ((Element )list.get(i)).getChildren("skillAnimate");
//            skillAnimateGrp[i] = new String[skillAnis.size()];
//            for(int j=0; j<skillAnis.size(); j++) {
//                elem = (Element)skillAnis.get(j);
//                skillAnimateGrp[i][j] = elem.getAttributeValue("file");  
//            }
//        }    
        
        Element compElem = doc.getRootElement().getChild("compatible_settings");
        list = compElem.getChildren("animation_format");
        for (int i = 0; i < list.size(); i++) {
            elem = (Element)list.get(i);
            AnimationFormat af = new AnimationFormat();
            af.load(elem);
            animationFormats.put(af.id, af);
        }
        list = compElem.getChildren("map_format");
        for (int i = 0; i < list.size(); i++) {
            elem = (Element)list.get(i);
            MapFormat mf = new MapFormat();
            mf.load(this, elem);
            mapFormats.put(mf.id, mf);
        }
        list = compElem.getChildren("client_model");
        for (int i = 0; i < list.size(); i++) {
            elem = (Element)list.get(i);
            ClientModel cm = new ClientModel();
            cm.load(this, elem);
            clientModels.put(cm.id, cm);
        }
        defaultModel = clientModels.get(compElem.getChild("default_client_model").getAttributeValue("id"));

        pqeUtils = new PQEUtils(owner.baseDir, classLoader);
        if(!owner.serverMode) {
            templateManager = new TemplateManager(this); 
        }
        attrCalc = new AttributeCalculator(this);
        
        // 载入物品使用效果配置
        loadItemEffectConfig();
        
        // 载入物品类型配置
        loadItemTypeConfig();
        
        // 载入技能BUFF描述中用到的系统变量配置
        loadDescPatternConfig();
        
        // 20121111新增：是否自动生成pkg选项
        Element apElem = doc.getRootElement().getChild("auto_generate_package");
        if (apElem != null) {
            autoGeneratePackage = !("false".equals(apElem.getTextTrim()));
        }

        // 20121114新增：是否保存真彩色pkg
        Element tcElem = doc.getRootElement().getChild("use_true_colour_map");
        if (tcElem != null) {
            useTrueColourForMap = "true".equals(tcElem.getTextTrim());
        }
        
        // 20121218新增：可配置使用crc8和crc16算法切换的时间
        Element cctElem = doc.getRootElement().getChild("crc8_to_crc16_time");
        if (cctElem != null) {
            crc8Tocrc16Time = Long.parseLong(cctElem.getTextTrim());
        }
        
        // 20121218新增：支持明珠城，在pkg文件中包含建筑信息
        Element ibElem = doc.getRootElement().getChild("include_building_in_package");
        if (ibElem != null) {
            includeBuildingInPackage = "true".equals(ibElem.getTextTrim());
        }
        
        // 20130110新增：支持怪物组配置怪物位置描述信息
        Element mgpElem = doc.getRootElement().getChild("monster_position_desc");
        if(mgpElem != null) {
            monsterPositionDescription = mgpElem.getAttributeValue("value");
        }
        
        // 20130425新增：允许禁止pip合并逻辑
        Element mgElem = doc.getRootElement().getChild("try_merge_pip");
        if(mgElem != null) {
            tryMergePip = "true".equals(mgElem.getTextTrim());
        }
        
        // 20130606新增：脚本编译所使用的版本，用于自动生成的脚本代码
        Element svElem = doc.getRootElement().getChild("script_version");
        if(mgElem != null) {
            scriptVersion = Integer.parseInt(svElem.getTextTrim());
        }else{
            scriptVersion = 3;
        }
        
        // 20131017新增：幻象II使用，用于定义战斗类型
        Element battleTypeElem = doc.getRootElement().getChild("battle_type");
        if(mgElem != null) {
        	BATTLE_TYPE = Byte.parseByte(battleTypeElem.getTextTrim());
        }else{
        	BATTLE_TYPE = -1;
        }
        
        // 20140223新增：可用于配制PipImage的3个选项
        // allowMultiCompressTexturesInOneFile: 是否允许一个pip中包含多个压缩纹理（需要幻想2以后的引擎），以提高压缩效率
        // ignoreBorderForSingleFrameImage：单帧图片不加边
        // autoETC1forOpaqueImage：如果图片全不透明，则ETC2自动转换为ETC1，PVRTC4_2自动转换为PVRTC4，以节省内存
        Element pipImageConfigElem = doc.getRootElement().getChild("pipimage_config");
        if (pipImageConfigElem != null) {
            PipImage.allowMultiCompressTexturesInOneFile = "true".equals(pipImageConfigElem.getAttributeValue("allowMultiCompressTexturesInOneFile"));
            PipImage.ignoreBorderForSingleFrameImage = "true".equals(pipImageConfigElem.getAttributeValue("ignoreBorderForSingleFrameImage"));
            PipImage.autoETC1forOpaqueImage = "true".equals(pipImageConfigElem.getAttributeValue("autoETC1forOpaqueImage"));
            MapFile.orderAnimateWhenPacking = "true".equals(pipImageConfigElem.getAttributeValue("orderAnimateWhenPacking"));
        }
        
        // 20140401新增：无视J2ME可以提高服务器下载效率
        Element ignoreJ2MEConfigElem = doc.getRootElement().getChild("ignore_j2me");
        if (ignoreJ2MEConfigElem != null) {
            ignoreJ2ME = "true".equals(ignoreJ2MEConfigElem.getTextTrim());
        }
        
        // 20140715新增:NPC的外框是否画成圆形
        Element npcBoxDrawRoundElem = doc.getRootElement().getChild("npcbox_drawround");
        if (npcBoxDrawRoundElem != null) {
            npcBoxDrawRound = "true".equals(npcBoxDrawRoundElem.getTextTrim());
        }
    }
    
    private void loadDescPatternConfig() {
        descPatternConfig = new DescriptionPatternConfig(owner.baseDir);
    }
    
    private void loadItemTypeConfig() throws Exception {
        Document doc = Utils.loadDOM(new File(owner.baseDir, "Items/itemtypeconfig.xml"));
        Element elemRoot = doc.getRootElement();
        List list = elemRoot.getChildren("item_category");
        List<ItemTypeConfig> retList = new ArrayList<ItemTypeConfig>();
        itemTypeSearchTable = new HashMap<Integer, ItemTypeConfig>();
        for (Object o : list) {
            List list2 = ((Element)o).getChildren("item_type");
            for (Object oo : list2) {
                ItemTypeConfig conf = new ItemTypeConfig();
                conf.load((Element)oo);
                retList.add(conf);
                itemTypeSearchTable.put(conf.id, conf);
            }
        }
        itemTypes = new ItemTypeConfig[retList.size()];
        retList.toArray(itemTypes);
    }
    
    private void loadItemEffectConfig() throws Exception {
        Document doc = Utils.loadDOM(new File(owner.baseDir, "Items/itemeffectconfig.xml"));
        Element elemRoot = doc.getRootElement();
        List list = elemRoot.getChildren("item_effect");
        itemEffects = new ItemEffectConfig[list.size()];
        itemEffectSearchTable = new HashMap<Integer, ItemEffectConfig>();
        for (int i = 0; i < list.size(); i++) {
            itemEffects[i] = new ItemEffectConfig();
            itemEffects[i].load((Element)list.get(i));
            if (itemEffectSearchTable.containsKey(itemEffects[i].id)) {
                throw new Exception("物品使用效果ID重复。");
            }
            itemEffectSearchTable.put(itemEffects[i].id, itemEffects[i]);
        }
    }
    
    public void loadEquipmenAndRacetDef(File baseDir2) throws Exception{
        Document doc = Utils.loadDOM(new File(baseDir2, "Items"+File.separator+"equipmentDef.xml"));
        Element elemRoot = doc.getRootElement();
        //equipment definition
        List list = elemRoot.getChild("body").getChildren("part");
        COMBO_PLACE = new String[list.size()];
        EQU_PLACE_ICON = new int[list.size()];
        PLACE_WEIGHTS = new float[list.size()];
        PLACE_IMAGE_STEP = new int[list.size()][];
        Element elem;
        for (int i=0; i<COMBO_PLACE.length; i++) {
            elem = (Element)list.get(i);
            COMBO_PLACE[i] = elem.getAttributeValue("name");
            EQU_PLACE_ICON[i] = Integer.parseInt(elem.getAttributeValue("icon"));
            BodyPart bp = new BodyPart();
            bp.load(elem);
            bodyPartMap.put(bp.name, bp);
            PLACE_WEIGHTS[i] = bp.weigth; 
            PLACE_IMAGE_STEP[i] = bp.placeImageStep;
        }
        PLACE_NAMES = COMBO_PLACE;
        
        //武器类型
        list = elemRoot.getChild("weapon").getChildren("type");        
        COMBO_WEAPON_TYPE = new String[list.size()];        
        WEAPON_DIR = new String[list.size()];
        WEAPON_KEY = new String[list.size()];
        WEAPON_ICON = new int[list.size()];
        WEAPON_RANGE = new float[list.size()][2];
        for(int i=0; i<COMBO_WEAPON_TYPE.length; i++) {
            elem = (Element)list.get(i);
            COMBO_WEAPON_TYPE[i] = elem.getAttributeValue("name");
            WEAPON_KEY[i] = elem.getAttributeValue("key");
            WEAPON_ICON[i] = Integer.parseInt(elem.getAttributeValue("icon"));
            WEAPON_RANGE[i][0] = Float.parseFloat(elem.getAttributeValue("min"));
            WEAPON_RANGE[i][1] = Float.parseFloat(elem.getAttributeValue("max"));
                
            BodyPart bp = new BodyPart();
            bp.load(elem);
            
            bodyPartMap.put(COMBO_WEAPON_TYPE[i], bp);
        }
        
        //equipment types definition
        list = elemRoot.getChild("types").getChildren("entry");
        for(int i=0; i<list.size(); i++){
            elem = (Element)list.get(i);
            EquipmentType et = new EquipmentType();
            et.load(elem);
            EquipmentType.equipmentTypes.put(et.id, et);
        }
        //races definition
        list = elemRoot.getChild("race").getChildren("entry");
        for(int i=0; i<list.size(); i++){
            elem = (Element)list.get(i);
            String name = elem.getAttributeValue("name");
            jobMap.put(name, elem.getAttributeValue("bodyDir"));
            jobCodeMap.put(name, elem.getAttributeValue("clientChar"));
        }
    }
    
    public Class[] getEditableClasses() {
        return editableClasses;
    }
    
    /**
     * 查找一个数据类型的编辑器ID。
     * @param cls
     * @return
     */
    public String getEditorID(Class cls) {
        String ret = dataTypeEditors.get(cls);
        
        if(ret == null) {
            ret = dataTypeEditors.get(supportDataSuperClassMap.get(cls));
        }
        return ret;
    }
    
    /**
     * 查找一个数据类型的标题。
     */
    public String getTypeName(Class cls) {
        String ret = dataTypeNames.get(cls);
        if(ret == null) {
            ret = dataTypeNames.get(supportDataSuperClassMap.get(cls));
        }
        return ret;
    }
    
    /**
     * 得到一个数据类型对应的Wizard对象。
     */
    public Runnable getCreateWizard(Class cls) throws Exception {
        Class cls2 = dataTypeCreateWizards.get(cls);
        return (Runnable)cls2.newInstance();
    }
    
    /**
     * 得到一个数据类型对应的扩展Editor对象。
     */
    public Class getExEditorbyClass(Class cls) {
        return dataTypeExtendEditor.get(cls);
    }
    
    public IDataCalculator getProjectCalc(Class cls) {
        return dataTypeCalc.get(cls);
    }
    
    public File getPipLibDir() {
        return new File(owner.baseDir, pipLibDir);
    }
    
    /**
     * 取得一个机型对应的地图格式。
     * @param model
     * @return
     */
    public MapFormat getClientMapFormat(String model) {
        ClientModel m = clientModels.get(model);
        if (m != null) {
            return m.mapFormat;
        } else {
            return defaultModel.mapFormat;
        }
    }
    
    /**
     * 根据使用效果类型ID查找效果配置。
     * @param configID
     * @return
     */
    public ItemEffectConfig findItemEffectConfig(int effectType) {
        return itemEffectSearchTable.get(effectType);
    }
    
    /**
     * 根据物品类型ID查找物品类型配置。
     * @param typeID
     * @return
     */
    public ItemTypeConfig findItemType(int typeID) {
        return itemTypeSearchTable.get(typeID);
    }
}
