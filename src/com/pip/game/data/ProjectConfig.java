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
    //ԭConstants.java�еĳ���
    public static final String[] COMBO_YES_NO = {"��","��"};

    // ֧�ֱ�ǵĶ�����
    public Class[] supportDataClasses;
    
    /**
     * ����չ��Ŀ�б��汻��չ�ĺ�����
     * ���ڽ��List<DataObject> list = ((ProjectData)inputElement).getDataListByType(SkillConfig.class); //�Լ���ط�������չ
     * 
     * ��չ�����Ӧ��������������ͬ����������
     * ��Ϊ�ڹ���ʵ��ʱsupportDataClasses�е�����÷��������¼���
     * 
     * 1. obj instanceof SkillConfig
     * 2. SkillConfig q = (SkillConfig)list.get(i);
     * 3. private boolean matchCondition(SkillConfig q) //��Ϊ��������
     * 
     */
    public Class[] supportDataSuperClasses;
    
    /** ���ݺ�������Բ��ҵ���չ�� */
    public HashMap<Class, Class> supportDataSuperClassMap = new HashMap<Class, Class>();
    
    // ��ͬ���Ͷ����Ӧ��XML����ǩ
    public String[] dataRootTags ;

    // ��ͬ���Ͷ����Ӧ��XML��ǩ
    public String[] dataTags;

    // ��ͬ���Ͷ����Ӧ��XML�ļ����·��
    public String[] dataFiles ;

    // �ֵ������
    public Class[] dictDataClasses;
//    = { NPCType.class, Faction.class, Rank.class };
    // �ֵ�������Ӧ��XML��ǩ
    public String[] dictDataTags;
//    =  { "npctype", "faction", "rank" };
    // �ֵ�����Ӧ��XML�ļ����·��
    public String[] dictDataFiles ;
//    = { "npctypes.xml", "factions.xml", "ranks.xml" };

    // ֧�ֱ༭�Ķ�����
    public Class[] editableClasses;
    // �洢���������Ͷ�Ӧ�ı༭��ID
    public HashMap<Class, String> dataTypeEditors = new HashMap<Class, String>();
    
    // �洢���������Ͷ�Ӧ�ı�������
    public HashMap<Class, String> dataTypeNames = new HashMap<Class, String>();
    
    // �洢���������Ͷ�Ӧ�Ĵ���Wizard��
    public final HashMap<Class, Class> dataTypeCreateWizards = new HashMap<Class, Class>();
    
    //�洢���������Ͷ�Ӧ����չeditor��
    private HashMap<Class, Class> dataTypeExtendEditor = new HashMap<Class, Class>();
    
    //�洢���������Ͷ�Ӧ�ļ�����
    private HashMap<Class, IDataCalculator> dataTypeCalc = new HashMap<Class, IDataCalculator>();
    
    /**
     * ������侭�飬��0����ʼ
     */
    public int[] LEVEL_EXP;
    
    /**
     * ��������Ǯ����0����ʼ
     */
    public int[] LEVEL_MONEY;
    
    /** Ʒ��ѡ�� */
    public String[] COMBO_QUALITY;
    
    /** Ʒ�ʶ�Ӧ����ɫ */
    public int[] QUALITY_COLOR;
    
    /** ʱЧ����ѡ�� */
    public String[] COMBO_TIME_TYPE;
    
    /** ȫ��װ������ѡ�� */
    public String[] COMBO_PLACE;
    public int[] EQU_PLACE_ICON;
    
    public float[] PLACE_WEIGHTS;
    public int[][] PLACE_IMAGE_STEP;

    /** ȫ��װ���������� */
    public String[] PLACE_NAMES;
    
    /** ��ѡ�� */
    public String[] COMBO_BIND;
    
    /** ְҵѡ�� */
    public String[] PLAYER_CLAZZ;
    public String[] PLAYER_CLAZZ_RAW;
    
    /** ����������� **/
    public String[] PLAYER_ATTR;
    
    public String[] COMBO_WEAPON_TYPE;
    public String[] WEAPON_DIR;
    public String[] WEAPON_KEY;
    public int[] WEAPON_ICON;
    public float[][] WEAPON_RANGE;
    
    /**
     * ����װ����λ�Լ���Ӧ��keyֵ
     */
    public HashMap<String,BodyPart> bodyPartMap = new LinkedHashMap<String, BodyPart>();
    
    /**
     * �����ɫ���壨�ˣ������ɣ��֣��Լ���Ӧ��keyֵ
     */
    public HashMap<String,String> jobMap = new HashMap<String, String>();
    public HashMap<String, String> jobCodeMap = new HashMap<String, String>();
        
    /** ���������ĳ��ڶ��� **/
    public PipAnimateSet exitAni;
    
    /** �����������ĳ��ڶ��� **/
    public PipAnimateSet exitlAni;
    
    /** ���ܶ����� **/
    public String[][] skillAnimateGrp;
    
    /** ����Ч���б� */
    public String[] particleFiles;
    
    /** ����Ч�������б� */
    public Map<String, List<String>> particleFilesGroup = new HashMap<String, List<String>>();
    
    /** ͼ��ϵ�С�ÿ��ϵ����һ�����ֱ�ʶ������һ��ͼ���ļ�����һ��ͼ���ļ���ͼ��IDΪ0-999���ڶ���Ϊ1000-1999���������ơ� */
    public Map<String, PipImage[]> iconSeries = new HashMap<String, PipImage[]>();
    /** ����ϵ�С�ÿ��ϵ����һ�����ֱ�ʶ������һ��ͼ���ļ�����һ��ͼ���ļ���ͼ��IDΪ0-999���ڶ���Ϊ1000-1999���������ơ� */
    public Map<String, PipAnimateSet[]> ctsSeries = new HashMap<String, PipAnimateSet[]>();
    
    /** ��ģʽ��ͼƬ��Դ��λ�� **/
    public String pipLibDir;
    
    /** ����Ŀ֧�ֵĵ�ͼ�ļ���ʽ */
    public Map<Integer, MapFormat> mapFormats = new HashMap<Integer, MapFormat>();
    /** ����Ŀ֧�ֵĶ����ļ���ʽ */
    public Map<Integer, AnimationFormat> animationFormats = new HashMap<Integer, AnimationFormat>();
    /** ����Ŀ֧�ֵĻ��� */
    public Map<String, ClientModel> clientModels = new HashMap<String, ClientModel>();
    /** ȱʡ���� */
    public ClientModel defaultModel;

    /** ��չ��Ŀ��classloader������������չ��Ŀ���ж�������Ŀ��û�е��� **/
    private ClassLoader projectClassLoader;
    /** ����Ŀ�õ�PQEUtils */
    public PQEUtils pqeUtils;
    /** ����Ŀ�õ�TemplateManager */
    public TemplateManager templateManager;
    /** ����Ŀ�õ��������� */
    public AttributeCalculator attrCalc;
    /** ��Ʒʹ��Ч������ */
    public ItemEffectConfig[] itemEffects;
    /** ��Ʒʹ��Ч�����ò��ұ� */
    protected Map<Integer, ItemEffectConfig> itemEffectSearchTable;
    /** ��Ʒ�������� */
    public ItemTypeConfig[] itemTypes;
    /** ��Ʒ�������ò��ұ� */
    protected Map<Integer, ItemTypeConfig> itemTypeSearchTable;
    /** ����BUFF�������õ���ϵͳ���� */
    public DescriptionPatternConfig descPatternConfig;
    
    //��Ŀ����չBaseQuest�������Զ�����
    public String BaseQuestClass;
    public String AutoGenQuestClass;
    
    public String BaseAIClass;
    public String AutoGenAIClass;
    
    public String gameMapInfoClass;
    
    public String gameMapNpcClass;
    
    public String gameMapExitClass;
    
    public String richTextEditorClass;
    
    /** ������Ŀ */
    private ProjectData owner;
    // �Ƿ񱣴�ؿ�ʱ�Զ�����pkg�ļ���ȱʡΪtrue�������Ŀ��Ҫ�ֶ��Ż�pkg�ļ�����ô��Ҫ�����ѡ������Ϊfalse���Է����ǵ��Ż����pkg�ļ�
    public boolean autoGeneratePackage = true;
    // �Ƿ��ڱ���ؿ�ʱʹ�����ɫģʽ�����ɫģʽ�ܹ�Ϊ�ֹ��Ż��ṩ�����ʵ�ԭʼ��Դ�������ٶȸ���
    public boolean useTrueColourForMap = false;
    // �ļ��汾���㷨����CRC8���ȵ�CRC16��ʱ��
    public long crc8Tocrc16Time = 1324396800228L;
    // �ؿ����Ƿ����������Ϣ�������������Ŀ��
    public boolean includeBuildingInPackage = false;
    // �������й�������λ������
    public String monsterPositionDescription = null;
    // �Ƿ��Ժϲ�pip�ļ�
    public boolean tryMergePip = true;
    // DataObjectInput�Ƿ�ʹ�ó���
    public boolean useLongName;
    // �ű���ʹ�õİ汾(Ĭ��Ϊ3)
    public int scriptVersion;
    // ����IIʹ�ã�ս������ 
    public byte BATTLE_TYPE ;  
    // �Ƿ�����J2MEת������
    public boolean ignoreJ2ME = false;
    // ѡ�е�NPC������Ƿ񻭳�Բ��
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
        //ctsϵ����Դ
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
                
                //����Ч��Ӧ�û�����Ϣ
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
        
        //ԭConstants.java�еĳ���
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
        
        // ������Ʒʹ��Ч������
        loadItemEffectConfig();
        
        // ������Ʒ��������
        loadItemTypeConfig();
        
        // ���뼼��BUFF�������õ���ϵͳ��������
        loadDescPatternConfig();
        
        // 20121111�������Ƿ��Զ�����pkgѡ��
        Element apElem = doc.getRootElement().getChild("auto_generate_package");
        if (apElem != null) {
            autoGeneratePackage = !("false".equals(apElem.getTextTrim()));
        }

        // 20121114�������Ƿ񱣴����ɫpkg
        Element tcElem = doc.getRootElement().getChild("use_true_colour_map");
        if (tcElem != null) {
            useTrueColourForMap = "true".equals(tcElem.getTextTrim());
        }
        
        // 20121218������������ʹ��crc8��crc16�㷨�л���ʱ��
        Element cctElem = doc.getRootElement().getChild("crc8_to_crc16_time");
        if (cctElem != null) {
            crc8Tocrc16Time = Long.parseLong(cctElem.getTextTrim());
        }
        
        // 20121218������֧������ǣ���pkg�ļ��а���������Ϣ
        Element ibElem = doc.getRootElement().getChild("include_building_in_package");
        if (ibElem != null) {
            includeBuildingInPackage = "true".equals(ibElem.getTextTrim());
        }
        
        // 20130110������֧�ֹ��������ù���λ��������Ϣ
        Element mgpElem = doc.getRootElement().getChild("monster_position_desc");
        if(mgpElem != null) {
            monsterPositionDescription = mgpElem.getAttributeValue("value");
        }
        
        // 20130425�����������ֹpip�ϲ��߼�
        Element mgElem = doc.getRootElement().getChild("try_merge_pip");
        if(mgElem != null) {
            tryMergePip = "true".equals(mgElem.getTextTrim());
        }
        
        // 20130606�������ű�������ʹ�õİ汾�������Զ����ɵĽű�����
        Element svElem = doc.getRootElement().getChild("script_version");
        if(mgElem != null) {
            scriptVersion = Integer.parseInt(svElem.getTextTrim());
        }else{
            scriptVersion = 3;
        }
        
        // 20131017����������IIʹ�ã����ڶ���ս������
        Element battleTypeElem = doc.getRootElement().getChild("battle_type");
        if(mgElem != null) {
        	BATTLE_TYPE = Byte.parseByte(battleTypeElem.getTextTrim());
        }else{
        	BATTLE_TYPE = -1;
        }
        
        // 20140223����������������PipImage��3��ѡ��
        // allowMultiCompressTexturesInOneFile: �Ƿ�����һ��pip�а������ѹ��������Ҫ����2�Ժ�����棩�������ѹ��Ч��
        // ignoreBorderForSingleFrameImage����֡ͼƬ���ӱ�
        // autoETC1forOpaqueImage�����ͼƬȫ��͸������ETC2�Զ�ת��ΪETC1��PVRTC4_2�Զ�ת��ΪPVRTC4���Խ�ʡ�ڴ�
        Element pipImageConfigElem = doc.getRootElement().getChild("pipimage_config");
        if (pipImageConfigElem != null) {
            PipImage.allowMultiCompressTexturesInOneFile = "true".equals(pipImageConfigElem.getAttributeValue("allowMultiCompressTexturesInOneFile"));
            PipImage.ignoreBorderForSingleFrameImage = "true".equals(pipImageConfigElem.getAttributeValue("ignoreBorderForSingleFrameImage"));
            PipImage.autoETC1forOpaqueImage = "true".equals(pipImageConfigElem.getAttributeValue("autoETC1forOpaqueImage"));
            MapFile.orderAnimateWhenPacking = "true".equals(pipImageConfigElem.getAttributeValue("orderAnimateWhenPacking"));
        }
        
        // 20140401����������J2ME������߷���������Ч��
        Element ignoreJ2MEConfigElem = doc.getRootElement().getChild("ignore_j2me");
        if (ignoreJ2MEConfigElem != null) {
            ignoreJ2ME = "true".equals(ignoreJ2MEConfigElem.getTextTrim());
        }
        
        // 20140715����:NPC������Ƿ񻭳�Բ��
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
                throw new Exception("��Ʒʹ��Ч��ID�ظ���");
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
        
        //��������
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
     * ����һ���������͵ı༭��ID��
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
     * ����һ���������͵ı��⡣
     */
    public String getTypeName(Class cls) {
        String ret = dataTypeNames.get(cls);
        if(ret == null) {
            ret = dataTypeNames.get(supportDataSuperClassMap.get(cls));
        }
        return ret;
    }
    
    /**
     * �õ�һ���������Ͷ�Ӧ��Wizard����
     */
    public Runnable getCreateWizard(Class cls) throws Exception {
        Class cls2 = dataTypeCreateWizards.get(cls);
        return (Runnable)cls2.newInstance();
    }
    
    /**
     * �õ�һ���������Ͷ�Ӧ����չEditor����
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
     * ȡ��һ�����Ͷ�Ӧ�ĵ�ͼ��ʽ��
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
     * ����ʹ��Ч������ID����Ч�����á�
     * @param configID
     * @return
     */
    public ItemEffectConfig findItemEffectConfig(int effectType) {
        return itemEffectSearchTable.get(effectType);
    }
    
    /**
     * ������Ʒ����ID������Ʒ�������á�
     * @param typeID
     * @return
     */
    public ItemTypeConfig findItemType(int typeID) {
        return itemTypeSearchTable.get(typeID);
    }
}
