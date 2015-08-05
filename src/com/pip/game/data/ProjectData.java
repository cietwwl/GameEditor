package com.pip.game.data;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Document;
import org.jdom.Element;

import com.pip.game.data.Shop.ShopItem;
import com.pip.game.data.AI.AIData;
import com.pip.game.data.equipment.Equipment;
import com.pip.game.data.forbid.ForbidItem;
import com.pip.game.data.forbid.ForbidSkill;
import com.pip.game.data.item.DropGroup;
import com.pip.game.data.item.DropItem;
import com.pip.game.data.item.Item;
import com.pip.game.data.item.ItemEffect;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.pkg.PackageFile;
import com.pip.game.data.pkg.PackageUtils;
import com.pip.game.data.quest.Quest;
import com.pip.game.data.skill.BuffConfig;
import com.pip.game.data.skill.DynamicGeneralConfig;
import com.pip.game.data.skill.EffectConfig;
import com.pip.game.data.skill.EffectConfigManager;
import com.pip.game.data.skill.EffectConfigSet;
import com.pip.game.data.skill.EquipGeneralConfig;
import com.pip.game.data.skill.ISkillConfig;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.StaticGeneralConfig;
import com.pip.game.editor.AdjustPIPsDialog;
import com.pip.game.editor.GenericChooseItemsDialog;
import com.pip.game.editor.MergeImageConfirmDialog;
import com.pip.game.editor.util.Settings;
import com.pip.mapeditor.data.MapFile;
import com.pip.util.Utils;
import com.pipimage.image.PipAnimateSet;
import com.pipimage.image.PipImage;

/**
 * ��Ŀ���ݼ��ϡ��������ݶ��󶼼̳�DataObject�ӿڡ�ͬһ���͵����ݶ��󱣴���һ��XML�ļ��С��������ݶ����
 * XML�ļ����Լ����ݶ������õ������ļ�����������һ����ĿĿ¼����Ŀ¼�С�
 * @author lighthu
 */
public class ProjectData {
    // ��ǰ��Ŀ·��
    public java.io.File baseDir;
    // ���涯������
    private IGameDataListener dataListener;
    
    // ���б༭��֧�ֵĶ����б���supportDataClasses˳���Ӧ��
    public List<DataObject>[] dataLists;
    // ��������б�
    public List<DataObjectCategory>[] dataCateLists;
    // �����ֵ�����б���dictDataClasses��˳���Ӧ
    public List<DataObject>[] dictDataLists;
    
    // �Ƿ������ģʽ���ڷ�����ģʽ�£����з��ʵ����ݱ�����������
    public boolean serverMode = false;
    // �ڷ�����ģʽ�£��Ƿ񴴽�Ѱ·���ߣ�����Ѱ·���߿��ܻ�ķѴ���ʱ�䣩
    public boolean createPathFinder = true;
    // ��֧�汾������֧��CMCC��CHINATEL�汾��null��ʾPIP�汾�������ڷ�����ģʽ��
    public String branch = null;
    // �ļ����棬�����ڷ�����ģʽ
    protected Hashtable<String, byte[]> resourceCache = new Hashtable<String, byte[]>();
    // �����ļ��İ汾�ţ������ڷ�����ģʽ��
    private Hashtable<String, Integer> resourceVersion = new Hashtable<String, Integer>();
    // �����ļ�����ʵ���ļ��Ķ�Ӧ��ϵ�������ڷ�����ģʽ��
    protected Hashtable<String, String> downloadFileMapping = new Hashtable<String, String>();
    // �Զ�Ѱ·���ߣ������ڷ�����ģʽ��
    public AutoPathFinder pathFinder;
    // ��ͬ��֧�Ŀͻ������������ļ���key��branch���ƣ�""��ʾȱʡ��֧��
    protected Hashtable<String, ClientData> branchClientData;
    // ��ͬ�Ŀͻ��������Ŷ�Ӧ�ķ�֧�汾
    protected Hashtable<String, String> channelToBranch;
    
    /** CMCC���ߴ������� */
    public CmccConfig cmccConfig;
    /** ��Ϸ�������� */
    public ProjectConfig config;
    /** ����Ч������ */
    public EffectConfigManager effectConfigManager;
    
    /* ��ǰ��Ŀsingleton�����������EditorApplication�л�ȡProjectData */
    private static ProjectData activeProject;
    
    /**
     * ��ȡ��ǰ�����project�����á�
     * @return
     */
    public static ProjectData getActiveProject(){
        return activeProject;
    }
    
    /**
     * ���ü����project��
     * @param proj
     */
    public static void setActiveProject(ProjectData proj) {
        activeProject = proj;
    }
    
    public ProjectData() {
    }
    
    /**
     * �������ݸı䶯�������ߡ�
     * @param l
     */
    public void setDataListener(IGameDataListener l) {
        dataListener = l;
    }
    
    /**
     * ȡ��һ���������������ͱ��е�λ�á�
     */
    public int getIndexByType(Class cls) {
        for (int i = 0; i < config.supportDataClasses.length; i++) {
            if (config.supportDataClasses[i] == cls || config.supportDataSuperClasses[i] == cls) {
                return i;
            }
            if(cls.isAssignableFrom(config.supportDataClasses[i])){
                return i;
            }
        }
        return -1;
    }
    
    /**
     * ȡ��һ���ɱ༭���͵�λ��
     * @return
     */
    public int getEditableIndexByType(Class cls) {
        for (int i = 0; i < config.editableClasses.length; i++) {
            if (config.editableClasses[i] == cls || config.editableClasses[i].getSuperclass() == cls) {
                return i;
            }
            if(cls.isAssignableFrom(config.editableClasses[i])){
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * ȡ��һ�����͵��������ݶ���
     * @param cls ���ݶ�������
     * @return
     */
    public List<DataObject> getDataListByType(Class cls) {
        return (List<DataObject>)dataLists[getIndexByType(cls)];
    }
    
    /**
     * ȡ��һ�����͵��������ݶ�����ࡣ
     * @param cls ���ݶ�������
     */
    public List<DataObjectCategory> getCategoryListByType(Class cls) {
        return (List<DataObjectCategory>)dataCateLists[getIndexByType(cls)];
    }
    
    /**
     * ȡ��һ���ֵ����͵��������ݶ���
     * @param cls ���ݶ�������
     * @return
     */
    public List<DataObject> getDictDataListByType(Class cls) {
        for (int i = 0; i < config.dictDataClasses.length; i++) {
            if (config.dictDataClasses[i] == cls) {
                return (List<DataObject>)dictDataLists[i];
            }
        }
        return null;
    }
    
    /**
     * ����ָ��ID��ָ�����͵����ݶ���
     * @param cls ���ݶ�������
     * @param id ����ID
     * @return ���û���ҵ�������null
     */
    public DataObject findObject(Class cls, int id) {
        List<DataObject> list = getDataListByType(cls);
        
        // ����Ƿ�����ģʽ�����������Ѿ�����ģ���ʹ�ö��ֲ���
        if (this.serverMode) {            
            int start = 0, end = list.size() - 1;
            while (start <= end) {
                int mid = (start + end) / 2;
                DataObject obj = list.get(mid);
                if (obj.id == id) {
                    return obj;
                } else if (obj.id < id) {
                    start = mid + 1;
                } else {
                    end = mid - 1;
                }
            }
        } else {
            for (DataObject obj : list) {
                if (obj.id == id) {
                    return obj;
                }
            }
        }
        return null;
    }
    
    /**
     * ����ָ���������ݵ�һ�����ݷ��ࡣ
     * @param cls
     * @param name
     * @return
     */
    public DataObjectCategory findCategory(Class cls, String name) {
        int index = getIndexByType(cls);
        List<DataObjectCategory> list = dataCateLists[index];
        for (DataObjectCategory cate : list) {
            if (cate.name.equals(name)) {
                return cate;
            } else {
                DataObjectCategory cate2 = findSubCategory(cate, name);
                if(cate2 != null) {
                    return cate2;
                }
            }
        }
        return null;
    }
    
    public DataObjectCategory findSubCategory(DataObjectCategory cate, String name) {
        for (DataObjectCategory cate2 : cate.cates) {
            if (cate2.name.equals(name)) {
                return cate2;
            } else {
                DataObjectCategory cate3 = findSubCategory(cate2, name);
                if(cate3 != null) {
                    return cate3;
                }
            }
        }
        
        return null;
    }

    /**
     * ����ID������Ʒ��װ��
     * @param id
     * @return
     */
    public Item findItemOrEquipment(int id) {
        Item ret = findItem(id);
        if (ret == null) {
            ret = findEquipment(id);
        }
        return ret;
    }

    /**
     * ����id���Ҽ���
     * @param id
     * @return
     */
    public SkillConfig findSkill(int id){
        return (SkillConfig)findObject(SkillConfig.class,id);
    }
    /**
     * ����id������Ʒ
     * @param id
     * @return
     */
    public Item findItem(int id) {
        return (Item)findObject(Item.class, id);
    }
    
    /**
     * ����id����װ��
     * @param id
     * @return
     */
    public Equipment findEquipment(int id) {
        return (Equipment)findObject(Equipment.class, id);
    }
    
    /**
     * ����id���ҽ�������Ʒ
     * @param id
     * @return
     */
    public ForbidItem findForbidItem(int id) {
        return (ForbidItem)findObject(ForbidItem.class, id);
    }
    
    
    /**
     * ����id���ҽ����鼼��
     * @param id
     * @return
     */
    public ForbidSkill findForbidSkill(int id) {
        return (ForbidSkill)this.findObject(ForbidSkill.class, id);
    }
    
    /**
     * ����id����AI
     * @param id
     * @return
     */
    public AIData findAIData(int id) {
        return (AIData)findObject(AIData.class, id);
    }
    
    /**
     * ����ָ�����ݶ��������б��е�������
     * @param obj ���ݶ���
     * @return ���û���ҵ�������-1
     */
    public int getObjectIndex(DataObject obj) {
        List<DataObject> list = getDataListByType(obj.getClass());
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).id == obj.id) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * ����ָ��ID��ָ�����͵��ֵ����ݶ���
     * @param cls ���ݶ�������
     * @param id ����ID
     * @return ���û���ҵ�������null
     */
    public DataObject findDictObject(Class cls, int id) {
        List<DataObject> list = getDictDataListByType(cls);
        
        // ����Ƿ�����ģʽ�����������Ѿ�����ģ���ʹ�ö��ֲ���
        if (this.serverMode) {
            int start = 0, end = list.size() - 1;
            while (start <= end) {
                int mid = (start + end) / 2;
                DataObject obj = list.get(mid);
                if (obj.id == id) {
                    return obj;
                } else if (obj.id < id) {
                    start = mid + 1;
                } else {
                    end = mid - 1;
                }
            }
        } else {
            for (DataObject obj : list) {
                if (obj.id == id) {
                    return obj;
                }
            }
        }
        return null;
    }
    
    /**
     * ����ָ���ֵ����ݶ��������б��е�������
     * @param obj ���ݶ���
     * @return ���û���ҵ�������-1
     */
    public int getDictObjectIndex(DataObject obj) {
        List<DataObject> list = getDictDataListByType(obj.getClass());
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).id == obj.id) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * ����һ���µ����ݶ��󣬲����뵽�б��С�
     * @param cls ���ݶ�������
     * @return �´��������ݶ��� 
     */
    public DataObject newObject(Class cls, Object selectObject) throws Exception {
//        if(getIndexByType(cls) == -1) {
        if (config.supportDataSuperClassMap.containsKey(cls)) {
            cls = config.supportDataSuperClassMap.get(cls);
        }
        
        Constructor cons = cls.getConstructor(ProjectData.class);
        DataObject newObj = (DataObject)cons.newInstance(this);
        newObj.id = 1;
        while (findObject(cls, newObj.id) != null) {
            newObj.id++;
        }
        addObjectToList(cls, newObj, selectObject);
        return newObj;
    }
    
    /**
     * 
     * @param cls
     * @param newObj
     * @param selectObject �༭����ѡ�е���
     */
    public void addObjectToList(Class cls, DataObject newObj, Object selectObject) {
        // �������б�
        int index = getIndexByType(cls);
        dataLists[index].add(newObj);
              
        //DataListView�в�֧�ֱ༭�Ķ��󣬱���EquipmentPrefix
        if(getEditableIndexByType(cls) < 0) {
            for (DataObjectCategory cate : dataCateLists[index]) {
                if(newObj.cate == null) {
                    newObj.cate = cate;
                }
                cate.objects.add(newObj);
                break;
            }
        } else {
            // ��������б�
            if(selectObject != null) {
                if(selectObject instanceof DataObject) {
                    DataObject dataObject = (DataObject)selectObject;
                    if(dataObject.cate.objects.contains(newObj) == false) {
                        dataObject.cate.objects.add(newObj);  
                        newObj.cate = dataObject.cate;
                        newObj.setCategoryName(newObj.cate.name);
                    }                    
//                }
                } else if(selectObject instanceof DataObjectCategory) {
                    ((DataObjectCategory)selectObject).objects.add(newObj);
                    newObj.cate = ((DataObjectCategory)selectObject);
                    newObj.setCategoryName(newObj.cate.name);
                }
            }            
        }
    }
    
    /**
     * ����һ���µ����ݷ��ࡣ
     * @param cls ���ݶ�������
     * @return
     */
    public DataObjectCategory newCategory(Class cls, String name) throws Exception {
        int index = getIndexByType(cls);
        for (DataObjectCategory cate : dataCateLists[index]) {
            if (cate.name.equals(name)) {
                throw new Exception("�������Ʋ����ظ���");
            }
        }
        DataObjectCategory cate = new DataObjectCategory(cls);
        cate.name = name;
        dataCateLists[index].add(cate);
        return cate;
    }
    
    /**
     * �½�һ����Ʒ��������id����ӵ������б���
     * @param itemType
     * @return
     */
    public Item newItem(DataObjectCategory category, Object selectObject) {
        Item item = new Item(this);
        item.id = 1;
        while (findItemOrEquipment(item.id) != null) {
            item.id++;
        }
        if (category != null) {
            item.setCategoryName(category.name);
        }
        addObjectToList(Item.class, item, selectObject);
        return item;
    }

    public Item newItem(DataObjectCategory category, Object selectObject,int itemType){
        int newItemId = 1;
        switch(itemType){
            case 0://ħʯ
                newItemId = 1020001;
                break;
            case 1://��ĩ
                newItemId = 1030001;
                break;
            case 2://ʥ����Ƭ+ͼֽ
            case 3:
                newItemId = 1050001;
                break;
            case 4://ʱװ+�ϳ���
                newItemId = 1060001;
                break;
            case 5://������Ʒ
                newItemId = 1080001;
                break;
            case 6://���Ʒ
                newItemId = 1090001;
                break;
            case 7://��Ӫ����
                newItemId = 1100001;
                break;
            case 8://�������->����ʯ
            case 9://�������->Ů��ӡ��
            case 10://�������->��װ��Ƭ
                newItemId = 1110001;
                break;
        }
        Item item = new Item(this);
        while (findItemOrEquipment(newItemId) != null) {
            newItemId++;
        }
        item.id = newItemId;
        if (category != null) {
            item.setCategoryName(category.name);
        }
        addObjectToList(Item.class, item, selectObject);
        return item;
    }
    /**
     * �½�һ��װ����������id����ӵ������б���
     * @param equiType
     * @return
     */
    public Equipment newEquipment(DataObjectCategory category, Object selectObject) throws Exception{
        Equipment equi = (Equipment) newObject(Equipment.class, selectObject);
        // װ������Ʒ����id����Ҫ�ֶδӰ���ʼ
        equi.id = 1010001;
        while (true) {
            Item obj = findItemOrEquipment(equi.id);
            if(obj == null || obj == equi){
                break;
            }
            equi.id++;
        }
        if (category != null) {
            equi.setCategoryName(category.name);
        }
        //already added when do newObject
//        addObjectToList(Equipment.class, equi, selectObject);
        return equi;
    }
    
    /**
     * ���¶����κ�һ������ĸ��¶��ᴥ��XML�ļ��洢��
     * @param src �µ�����
     * @param dest ��Ҫ���µ�Ŀ�����
     * @throws Exception
     */
    public void updateObject(DataObject src, DataObject dest) throws Exception {
        // ȷ��û���ظ���ID
        DataObject searchResult = findObject(src.getClass(), src.id);
        if (searchResult != null && searchResult != dest) {
            throw new Exception("�ظ���ID��");
        }
        
        // ����������Բ�����XML�ļ�
        dest.update(src);
        saveDataList(dest.getClass());
    }
    
    /**
     * ��һ�����ݶ����һ�������ƶ�������һ�����ࡣ
     * @param obj
     * @param newCate
     */
    public void changeObjectCategory(DataObject obj, DataObjectCategory newCate) {        
        DataObjectCategory oldCate = obj.cate;
        oldCate.objects.remove(obj);
        obj.setCategoryName(newCate.name);
        newCate.objects.add(obj);
        obj.cate = newCate;
    }
    
    /**
     * ɾ��һ�����󣨱���������ɾ�������ݶ���Ĺ������󣩡�
     * @param obj
     */
    public void deleteObject(DataObject obj) {
        int index = getIndexByType(obj.getClass());
        if (index == -1) {
            return;
        }
        List<DataObject> list = dataLists[index];
        list.remove(obj);
        obj.cate.objects.remove(obj);
    }
    
    /**
     * �����������͵����ݶ����б�
     */
    public void saveAll() throws Exception {
        for (int i = 0; i < config.supportDataClasses.length; i++) {
            saveDataList(config.supportDataClasses[i]);
        }
    }
    
    /**
     * ȡ��ĳ�������ݶ�Ӧ�������ļ���
     * @param cls
     * @return
     */
    public File getDataFile(Class cls) {
        for (int i = 0; i < config.supportDataClasses.length; i++) {
            if (config.supportDataClasses[i] == cls || config.supportDataSuperClasses[i] == cls) {
                return new File(baseDir, config.dataFiles[i]);
            }
            if(cls.isAssignableFrom(config.supportDataClasses[i])){
                return new File(baseDir, config.dataFiles[i]);
            }
        }
        return null;
    }
    
    /**
     * ����һ�����͵��������ݶ���
     * @param cls ���ݶ�������
     * @throws Exception
     */
    public void saveDataList(Class cls) throws Exception {        
        if(getIndexByType(cls) == -1) {
            cls = config.supportDataSuperClassMap.get(cls);
        }
        if (dataListener != null) {
            dataListener.saveStart(cls);
        }
        try {
            for (int i = 0; i < config.supportDataClasses.length; i++) {
                if (config.supportDataClasses[i] == cls || config.supportDataSuperClasses[i] == cls) {
                    Element root = new Element(config.dataRootTags[i]);
                    Document doc = new Document(root);
                    saveData(root, dataCateLists[i], null);
                    try{
                        Utils.saveDOM(doc, new File(baseDir, config.dataFiles[i]));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            if (dataListener != null) {
                dataListener.saveEnd(cls);
            }
        }
    }
    
    /**
     * ����һ�����͵������ֵ����ݶ���
     * @param cls ���ݶ�������
     * @throws Exception
     */
    public void saveDictDataList(Class cls) throws Exception {        
        for (int i = 0; i < config.dictDataClasses.length; i++) {
            if (config.dictDataClasses[i] == cls) {
                Element root = new Element(config.dictDataTags[i] + "s");
                Document doc = new Document(root);
                for (int j = 0; j < dictDataLists[i].size(); j++) {
                    root.addContent(dictDataLists[i].get(j).save());
                }
                Utils.saveDOM(doc, new File(baseDir, config.dictDataFiles[i]));
            }
        }
    }
    
    public void saveData(Element root, List<DataObjectCategory> _dataCateLists, DataObjectCategory category) {
        for (DataObjectCategory cate : _dataCateLists) {
            cate.parent = category;
            for (DataObject obj : cate.objects) {
                root.addContent(obj.save());
            }
            saveData(root, cate.cates, cate);
        }
    }
    
    /**
     * �ҳ�������ָ�������������ض���
     * @param objs
     * @return
     */
    public List<DataObject> findRelateObjects(Object[] objs) {
        List<DataObject> ret = new ArrayList<DataObject>();
        for (int i = 0; i < dataLists.length; i++) {
            for (DataObject obj : dataLists[i]) {
                for (int j = 0; j < objs.length; j++) {
                    if (obj.depends((DataObject)objs[j])) {
                        ret.add(obj);
                        break;
                    }
                }
            }
        }
        return ret;
    }
    
    public void load(File dir) throws Exception {
        load(dir, getClass().getClassLoader());
    }
    
    /**
     * ������Ŀ����Ŀ֧�ֵ��������ݶ��󶼻ᱻ���롣�����˳����supportDataClasses�ķ������ԣ����һ��
     * ������������һ�����ݣ�������������Ҫ��������ĺ��档
     * @param dir
     * @param classLoader ���ڶ�̬������
     * @throws Exception
     */
    public void load(File dir, ClassLoader classLoader) throws Exception {
        baseDir = dir;
        config = new ProjectConfig(this);
        config.load(classLoader);
        effectConfigManager = new EffectConfigManager(classLoader, baseDir.getAbsolutePath()); 

        // ��ʼ�����ݴ洢��
        dataLists = new List[config.supportDataClasses.length];
        dataCateLists = new List[config.supportDataClasses.length];
        dictDataLists = new List[config.dictDataClasses.length];
        for (int i = 0; i < dataLists.length; i++) {
            dataLists[i] = new ArrayList<DataObject>();
        }
        for (int i = 0; i < dataCateLists.length; i++) {
            dataCateLists[i] = new ArrayList<DataObjectCategory>();
        }
        for (int i = 0; i < dictDataLists.length; i++) {
            dictDataLists[i] = new ArrayList<DataObject>();
        }
        
        // �����ֵ�����
        for (int i = config.dictDataClasses.length - 1; i >= 0; i--) {
            Class cls = config.dictDataClasses[i];
            Document doc = Utils.loadDOM(new File(baseDir, config.dictDataFiles[i]));
            List list = null;
            if(config.dictDataTags[i]==null || config.dictDataTags[i].equals("")){
                list = doc.getRootElement().getChildren();
            }else{
            	list = doc.getRootElement().getChildren(config.dictDataTags[i]);
            }
            
            HashMap<String, DataObjectCategory> cateMap = new HashMap<String, DataObjectCategory>();
            
            // ����ȱʡ����
            DataObjectCategory emptyCate = new DataObjectCategory(cls);
            emptyCate.name = "";
            cateMap.put("", emptyCate);
            dataCateLists[i].add(emptyCate);
            
            for (Object elem : list) {
                DataObject newObj = (DataObject)config.dictDataClasses[i].newInstance();
                newObj.load((Element)elem);
                dictDataLists[i].add(newObj);
            
            }
            
            // ����Ƿ����ظ�ID����
            checkID(dictDataLists[i]);
            
            // ����Ƿ�����ģʽ�����������Ա����
            if (this.serverMode) {
                Collections.sort(dictDataLists[i]);
            }
        }
        
        // ����ɱ༭����
        for (int i = config.supportDataClasses.length - 1; i >= 0; i--) {
            Document doc = null;
            try{
                doc = Utils.loadDOM(new File(baseDir, config.dataFiles[i]));
            }catch(Exception e){
                e.printStackTrace();
            }
            List list = doc.getRootElement().getChildren(config.dataTags[i]);
            HashMap<String, DataObjectCategory> cateMap = new HashMap<String, DataObjectCategory>();
            
            // ����ȱʡ����
            Class cls = config.supportDataClasses[i];
            DataObjectCategory emptyCate = new DataObjectCategory(config.supportDataClasses[i]);
            emptyCate.name = "";
            cateMap.put("", emptyCate);
            dataCateLists[i].add(emptyCate);
            
            for (Object elem : list) {
                try{
                Constructor[] conss = config.supportDataClasses[i].getConstructors();
                DataObject newObj = null;
                try{
                    newObj= (DataObject)conss[0].newInstance(this);
                }catch(IllegalArgumentException iae){
                    System.err.println("���췽��������ƥ��.");
                    throw iae;
                }
                newObj.load((Element)elem);
                dataLists[i].add(newObj);
                if (this.serverMode) {
                    newObj.editorIndex = dataLists[i].size() - 1;
                }                
                
                //����Ǵ���Ŀ¼�ṹ�ģ����⴦��
                if(newObj.getCategoryName().indexOf(',') >= 0) {
                    loadCate(emptyCate, cls, i, newObj, cateMap);
                } else {                                    
                    if (this.serverMode) {
                        newObj.editorIndex = dataLists[i].size() - 1;
                    }
                    
                    // ��������б���
                    DataObjectCategory cate = cateMap.get(newObj.getCategoryName());
                    if (cate == null) {
                        cate = new DataObjectCategory(cls);
                        cate.name = newObj.getCategoryName();
                        cateMap.put(cate.name, cate);
                        dataCateLists[i].add(cate);                    
                    }
                    cate.objects.add(newObj);
                    newObj.cate = cate;
                } 
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            
            // ����Ƿ����ظ�ID����
            checkID(dataLists[i]);
            
            // ����Ƿ�����ģʽ�����������Ա����
            if (this.serverMode) {
                Collections.sort(dataLists[i]);
            }
            
        }

        cmccConfig = new CmccConfig(new File(baseDir, "cmcc_config.xml"));
        
        // ������ģʽ�£��������г�����ͨ���ϵ����֧���Զ�Ѱ·����
        if (this.serverMode && this.createPathFinder) {
            pathFinder = new AutoPathFinder(this);
        }
        
        // ������ģʽ�¶���ͻ�����Դ�����ļ�
        if(this.serverMode){
            loadAllClientData();
        }
    }
    
    /**
     * ������ģʽ�£��������е�client_pkg_xxx.xml���á�����Ϊ��ͬ��֧�汾�ṩ���ط���
     */
    protected void loadAllClientData() throws Exception {
        branchClientData = new Hashtable<String, ClientData>();

        // ����ȱʡcliekt_pkg.xml
        ClientData clientData = new ClientData(this, null);
        branchClientData.put("", clientData);
        
        // ������ĿĿ¼������client_pkg_xxx.xml�������֧����
        File[] ffs = baseDir.listFiles();
        for (File f : ffs) {
            if (f.isFile() && f.getName().startsWith("client_pkg_") && f.getName().endsWith(".xml")) {
                String n = f.getName();
                String branch = n.substring("client_pkg_".length(), n.length() - 4);
                clientData = new ClientData(this, branch);
                branchClientData.put(branch, clientData);
            }
        }
        
        // ����branch.xml����ȡ�����ŵ���֧�Ĺ�ϵ��
        channelToBranch = new Hashtable<String, String>();
        File branchConfigFile = new File(baseDir, "branch.xml");
        if (branchConfigFile.exists()) {
            Document doc = Utils.loadDOM(branchConfigFile);
            List list = doc.getRootElement().getChildren("branch");
            for (int i = 0; i < list.size(); i++) {
                Element elem = (Element)list.get(i);
                String branchName = elem.getAttributeValue("name");
                List list2 = elem.getChildren("channel");
                for (int j = 0; j < list2.size(); j++) {
                    String channel = ((Element)list2.get(j)).getTextTrim();
                    channelToBranch.put(channel, branchName);
                }
            }
        }
    }
    
    /**
     * ���һ�������б�ȷ��û���ظ�ID.
     */
    protected void checkID(List<DataObject> list) throws Exception {
        Set<Integer> idset = new HashSet<Integer>();
        for (DataObject dobj : list) {
            int id = dobj.id;
            if (idset.contains(id)) {
                throw new Exception("ID�ظ�(" + id + ")��" + dobj.getClass().getName());
            } else {
                idset.add(id);
            }
        }
    }
    
    protected void loadCate(DataObjectCategory parentCate, Class cls, int i, DataObject newObj, HashMap<String, DataObjectCategory> cateMap) {
        StringBuffer categoryName = new StringBuffer();
        DataObjectCategory cate = null;
        
        String[] cates = newObj.getCategoryName().split(",");
        for(int k=0; k<cates.length; k++) {
            categoryName.append(cates[k]);
            
            cate = new DataObjectCategory(cls);
            cate.name = cates[k];
            
//            if(cateMap.containsKey(categoryName.toString()) == false) {
//                cateMap.put(categoryName.toString(), cate);
//                parentCate.cates.add(cate);
//            }            
            
            if(cateMap.get(categoryName.toString()) == null) {
                cateMap.put(categoryName.toString(), cate);  
                if(k == 0){
                    dataCateLists[i].add(cate);
                }else{
                    if((parentCate != null)) {
                        parentCate.cates.add(cate);
                    }
                }
            } else {
                cate = cateMap.get(categoryName.toString());
            }
            
            if(k != 0) {
                cate.parent = parentCate;
            }
            
            if(k < cates.length - 1) {
                categoryName.append(",");    
            }
            
            parentCate = cate;
        }
                
        if (this.serverMode) {
            newObj.editorIndex = dataLists[i].size() - 1;
        }
        
        newObj.setCategoryName(cates[cates.length - 1]);
        cate.name = cates[cates.length - 1];
        
        // ��������б���
//        cate = cateMap.get(newObj.categoryName);
//        if (cate == null) {
//            cate = new DataObjectCategory(cls);
//            cate.name = newObj.categoryName = categoryName.toString();
//            cateMap.put(cate.name, cate);
//            dataCateLists[i].add(cate); 
//        }
        cate.objects.add(newObj);
        newObj.cate = cate;
    }
    
    public void makeWorldMapPackages(File mapf){
        try {
            //���������ͼ�����ļ�
            MapFile mapFile = new MapFile();
            mapFile.load(mapf);
            //���������ͼ�����ļ�
            PackageFile wmtemp = new PackageFile();
            new PackageUtils().makeClientPackage(mapFile, wmtemp);
            wmtemp.save(new File(mapf.getParent(), "worldMap.dat"));
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public HashMap<File, byte[]> clbAndLfbCache = new HashMap<File, byte[]>();
    /**
     * �����Ƿ񻺴��ļ�
     */
    public static boolean cacheFile = true;
    /**
     * Ϊ���йؿ����ɿͻ��������ļ���
     */
    public void makeClientPackages() throws Exception {
        clbAndLfbCache.clear();
        for (DataObject obj : getDataListByType(GameArea.class)) {
            String info = "";
            try {
                GameArea ga = (GameArea)obj;
                info = ga.toString();
                
                // �����ͼ��Ϣ�ļ�
                GameAreaInfo areaInfo = new GameAreaInfo(ga);
                areaInfo.load();
                
                // �������а汾�ĵ�ͼ�ļ�
                for (int i = 0; i < ga.maps.length; i++) {
                    MapFormat format = config.mapFormats.get(i);
                    MapFile mapFile = new MapFile();
                    File mapf = ga.getFile(i);
                    if (mapf == null) {
                        continue;
                    }
                    mapFile.load(mapf);
    
                    PackageFile pkgtemp = new PackageFile();
                    pkgtemp.setName(String.valueOf(ga.id));
                    pkgtemp.setVersion(0);
                    new PackageUtils().makeClientPackage(ga, mapFile, areaInfo, pkgtemp, (float)format.scale, 
                            ga.maps[i].colorMode, ga.maps[i].jpegOption, ga.maps[i].compressTextureOption);
                    pkgtemp.save(new File(ga.source, ga.getID() + format.pkgName + ".pkg"));
                }
            } catch (Exception e) {
                clbAndLfbCache.clear();
                throw new Exception("�����ͼ��"+info,e);
            }
        }
        clbAndLfbCache.clear();
    }
        
    /**
     * Ϊ����BUFF��������Java Class�ļ���
     * @throws Exception
     */
    public void generateBuffClasses(String encoding) throws Exception {
        List<DataObject> buffs = getDataListByType(BuffConfig.class);
        File clsDir = new File(Settings.exportClassDir, Settings.buffPackage.replace('.', '/'));
        
        // ����Java�ļ�
        for (DataObject o : buffs) {
            BuffConfig bc = (BuffConfig)o;
            
            try {
                EffectConfigSet newSet = new EffectConfigSet();
                newSet.setLevelCount(bc.effects.getLevelCount());
                newSet.addGeneralEffect(bc.getGeneralConfig());
                for (EffectConfig eff : bc.effects.getAllEffects()) {
                    if(eff.getType() != effectConfigManager.getTypeId(DynamicGeneralConfig.class) && 
                            eff.getType() != effectConfigManager.getTypeId(StaticGeneralConfig.class) &&
                            eff.getType() != effectConfigManager.getTypeId(EquipGeneralConfig.class)) {
                        newSet.addEffect(eff);
                    }
                }
                bc.effects = newSet;
                
                File jf = new File(clsDir, bc.getClassName(Settings.buffClassPrefix) + ".java");
                FileOutputStream fos = new FileOutputStream(jf);
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, encoding));
                bc.generateJava(pw, Settings.buffPackage, Settings.buffClassPrefix);
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("buff " + bc.id + "���ڴ���" + e.toString());
            }
        }
        
        // ����buffs.xml
        this.saveDataList(BuffConfig.class);
    }
    
    /**
     * Ϊ����Skill��������Java Class�ļ���
     * @throws Exception
     */
    public void generateSkillClasses(String encoding) throws Exception {
        List<DataObject> skills = getDataListByType(SkillConfig.class);
        File clsDir = new File(Settings.exportClassDir, Settings.skillPackage.replace('.', '/'));
        
        // ����Java�ļ�
        for (DataObject o : skills) {
            ISkillConfig bc = (ISkillConfig)o;
            try {
                File jf = new File(clsDir, bc.getClassName(Settings.skillClassPrefix) + ".java");
                FileOutputStream fos = new FileOutputStream(jf);
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, encoding));
                bc.generateJava(pw, Settings.skillPackage, Settings.skillClassPrefix);
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("skill " + bc.getId() + "���ڴ���" + e.toString());
            }
        }
        
        // ����skills.xml
        this.saveDataList(SkillConfig.class);
    }
    
    /**
     * Ϊ����Quest��������Java Class�ļ���
     * @throws Exception
     */
    public void generateQuestClasses(String encoding) throws Exception {
        List<DataObject> quests = getDataListByType(Quest.class);
        File clsDir = new File(Settings.exportClassDir, Settings.questPackage.replace('.', '/'));
        
        // ����Java�ļ�
        for (DataObject o : quests) {
            try {
                Quest bc = (Quest)o;
                File jf = new File(clsDir, bc.getClassName(Settings.questClassPrefix) + ".java");
                FileOutputStream fos = new FileOutputStream(jf);
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, encoding));
                if(ProjectData.activeProject.config.AutoGenQuestClass == null) {
                	bc.generateJava(pw, Settings.questPackage, Settings.questClassPrefix);
                } else {
                	Class c;
                	ProjectConfig config = ProjectData.activeProject.config;
                	c = config.getProjectClassLoader().loadClass(config.AutoGenQuestClass);
                	Method m = c.getMethod("generateJava", new Class[] {Quest.class, PrintWriter.class, String.class, String.class});
                	m.invoke(c, new Object[] {bc, pw, Settings.questPackage, Settings.questClassPrefix});
                }
                
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }
    
    /**
     * Ϊ����AI��������Java Class�ļ���
     * @throws Exception
     */
    public void generateAIClasses(String encoding) throws Exception {
        
        List<DataObject> aiData = getDataListByType(AIData.class);
        //List<DataObject> npc = getDataListByType(NPCTemplate.class);
        File clsDir = new File(Settings.exportClassDir, Settings.aiPackage.replace('.', '/'));
        
        // ����Java�ļ�
        for (DataObject o : aiData) {
            try {               
                AIData bc = (AIData)o;
                File jf = new File(clsDir, bc.getClassName(Settings.aiClassPrefix) + ".java");
                FileOutputStream fos = new FileOutputStream(jf);
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, encoding));                
                bc.generateAIJava(pw, Settings.aiPackage, Settings.aiClassPrefix);
                pw.close();
            } catch (Exception e) {
                System.out.println("AI error: " + o.getId());
                e.printStackTrace();
                throw e;
            }
        }
        
        this.saveDataList(AIData.class);
    }

    /**
     * �������г������б�
     * @throws Exception
     */
    public String generateMapList() throws Exception {
        List<DataObject> areas = getDataListByType(GameArea.class);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < areas.size(); i++) {
            GameArea ga = (GameArea)areas.get(i);
            GameAreaInfo areaInfo = new GameAreaInfo(ga);
            areaInfo.load();
            for (GameMapInfo gmi : areaInfo.maps) {
                sb.append(gmi.getGlobalID());
                sb.append("\t");
                sb.append(gmi.name);
                sb.append("\r\n");
            }
        }
        return sb.toString();
    }
    
    /**
     * �������������ı��е�NPC�͵�ͼ���á�
     * @throws Exception
     */
    public void validateMixedText() throws Exception {
        validateMixedText(true);
    }
    public void validateMixedText(boolean reportException) throws Exception {
        List<DataObject> quests = getDataListByType(Quest.class);
        for (int i = 0; i < quests.size(); i++) {
            Quest quest = (Quest)quests.get(i);
            try {
                quest.validateMixedText();
            } catch (Exception e) {
                if (reportException) {
                    throw e;
                } else {
                    System.err.println(e);
                }
            }
        }
        saveDataList(Quest.class);
    }
    
    /**
     * ���ݹ�ʽ�Զ���������װ���ļ۸���;ö����ԡ�
     * @throws Exception
     */
    public void updateEquipmentPrices() throws Exception {
        // ����װ����
        List<DataObject> equiList = getDataListByType(Equipment.class);
        for (DataObject equi : equiList) {
            ((Equipment)equi).recalcPriceAndDurability();
        }
        saveDataList(Equipment.class);
        
        // �����̵��е�����
        List<DataObject> shops = getDataListByType(Shop.class);
        for (int i = 0; i < shops.size(); i++) {
            Shop shop = (Shop)shops.get(i);
            for (ShopItem si : shop.items) {
                if (si.item instanceof Equipment) {
                    for (Shop.BuyRequirement br : si.requirements) {
                        if (br.type == Shop.TYPE_MONEY && br.deduct) {
                            br.amount = si.item.price * 2;
                        }
                    }
                }
            }
        }
        saveDataList(Shop.class);
    }
    
    /**
     * ɨ����ĿĿ¼��ͳ��������Դ�ļ��İ汾�ţ�д��һ��XML�ļ���
     * @throws Exception
     */
    public void generateResourceVersionXML() throws Exception {
        // ȡȱʡcliekt_pkg.xml����
        ClientData clientData = new ClientData(this, null);
        clientData.makeClientData();
        
        // ������ĿĿ¼������client_pkg_xxx.xml������branches
        File[] ffs = baseDir.listFiles();
        for (File f : ffs) {
            if (f.isFile() && f.getName().startsWith("client_pkg_") && f.getName().endsWith(".xml")) {
                String n = f.getName();
                String branch = n.substring("client_pkg_".length(), n.length() - 4);
                clientData = new ClientData(this, branch);
                clientData.makeClientData();
            }
        }
    }
    
    /**
     * ȡ���ļ��ĵ�ǰ�汾�š�
     * �汾�ű������
     * 4�ֽ�������ǰ3���ֽڱ�ʾ�ļ���С�����һ���ֽڱ�ʾ�ļ�CRC���ֽ�����㷨����
     * ���㷨��ǰ2���ֽڱ�ʾ�ļ���С�ĵ�16λ���������ֽڱ�ʾCRC16��
     */
    public int getFileCRCVersion(File file) {
        byte[] content = null;
        try {
            content = Utils.loadFileData(file);
        } catch (Exception e) {
            return 0;
        }
        return (content.length << 16) | (crc16(content) & 0xFFFF);
    }
    
    /**
     * �ֽ���CRCֵ ��8λ��
     */
    private static byte crc8(byte[] data) {
        byte ret = 0;
        int len = data.length;
        for (int i = 0; i < len; i++) {
            ret ^= data[i];
        }
        return ret;
    }
    
    /**
     * �ֽ���CRCֵ ��16λ��
     */
    private static int crc16(byte[] data) {
        byte ret1 = 0;
        byte ret2 = 0;
        int len = data.length;
        for (int i = 0; i < len; i += 2) {
            ret1 ^= data[i];
            if (i + 1 < len) {
                ret2 ^= data[i + 1];
            }
        }
        return (ret1 << 8) | (ret2 & 0xFF);
    }
    
    /*
     * ȡ��һ���ļ���CVS�еİ汾�š�
     */
    private int getFileCVSVersion(File file) {
        long fileTm = file.lastModified();

        File entryFile = new File(file.getParentFile(), "CVS/Entries");
        try {
            String content = Utils.loadFileContent(entryFile);
            BufferedReader br = new BufferedReader(new StringReader(content));
            String line ="";
            String fname = file.getName();
            String prefix = "/" + fname + "/";
            while ((line = br.readLine()) != null) {
                if (line.startsWith(prefix)) {
                    line = line.substring(prefix.length());
                    int pos = line.indexOf('/');
                    
                    String versionStr = line.substring(0, pos);
                    line = line.substring(pos + 1);
                    
                    pos = versionStr.indexOf('.');
                    versionStr = versionStr.substring(pos + 1);
                    int version = Integer.parseInt(versionStr);
                    
                    pos = line.indexOf('/');
                    String entryTimeStr = line.substring(0, pos);
                    DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", new Locale("en_US"));
                    long entryTime = 0;
                    
                    try{
                        entryTime = df.parse(entryTimeStr).getTime() + (long)3600 * 8 * 1000;
                    }catch(Exception e){
                        e.printStackTrace();
                        entryTime = fileTm;
                    }
                    
                    if(entryTime < fileTm - 10000){
                        System.out.println("warning: uncommited file: " + file);
                        // version++;
                    }
                    
                    return version;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }
 
    /*
     * �������ļ���ת��Ϊʵ���ļ�����ʹ��ȱʡ��֧��
     * @param name �ͻ���ʹ�õ������ļ��������淶��
     * @param model �ͻ��˻���
     */
    public String translateFileName(String name, String model) {
        return translateFileName(name, model, branch);
    }
    
    /*
     * �������ļ���ת��Ϊʵ���ļ�����
     * @param name �ͻ���ʹ�õ������ļ��������淶��
     * @param model �ͻ��˻���
     * @param useBranch ʹ�÷�֧
     */
    public String translateFileName(String name, String model, String useBranch) {
        File tmpFile = new File(name);
        String filename = tmpFile.getName();
        if (branchClientData == null) {
            try {
                loadAllClientData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String key = name + "\n" + model;
        if (useBranch != null) {
            key += "\n" + useBranch;
        }
        String ret = downloadFileMapping.get(key);
        if (ret == null) {
            // �ҳ���֧��Ӧ��ClientData
            ClientData clientData = branchClientData.get(useBranch == null ? "" : useBranch);
            
            // ���������ͻ���������Դ
            if (name.endsWith(".etf")) {
                ret = clientData.getScriptsDir() + "/" + model + "/" + (name.substring(0, name.length() - 4)) + "_" + model + ".etf.gz";
            } else if ((ret = clientData.getMatchPath(model, name)) != null) {
                // nothing to do
            } else if (name.endsWith(".pkg") && Character.isDigit(name.charAt(0))) {
                int id ;
                
                int idxOf_ = name.indexOf('_', 0);
                if(idxOf_ != -1){
                    //����ͼ
                    id = Integer.parseInt(name.substring(0, idxOf_));
                }else{
                    //�����汾��ͼ
                    id = Integer.parseInt(name.substring(0, name.length() - 4));
                }
                GameArea area = (GameArea)findObject(GameArea.class, id);
                String pkgName = config.getClientMapFormat(model).pkgName;
                ret = "Areas/" + area.source.getName() + "/" + id + pkgName + ".pkg";
            } else if (name.endsWith(".ctn")) {
                if (Character.isDigit(name.charAt(0))) {
                    int id = Integer.parseInt(name.substring(0, name.length() - 4));

                    Animation nif = (Animation)findObject(Animation.class, id > 10000 ? (id - 10000) : id);
                    AnimationFormat aniFormat = config.getClientMapFormat(model).aniFormat;
                    String ctsName;
                    if (id <= 10000) {
                        ctsName = nif.animateFiles[aniFormat.id];
                    } else {
                        ctsName = nif.attackAnimateFiles[aniFormat.id];
                    }
                    String ctnName = ctsName.substring(0, ctsName.length() - 1) + "n";
                    return nif.getAnimatePath(aniFormat.id) + ctnName;
                } else {
                    ret = findFile("client_res/" + model, name, true, useBranch);
                    if (!new File(baseDir, ret).exists()) {
                        ret = findFile("client_res", name, true, useBranch);
                    }
                }
            } else if (name.endsWith(".mcfg")) {
                ret = findFile("Meshes", name, true, useBranch);
            } else if (name.endsWith(".mesh")) {
                ret = findFile("Meshes", name, true, useBranch);
            } else if (name.endsWith(".skeleton")) {
                ret = findFile("Meshes", name, true, useBranch);
            } else if (filename.startsWith("m_")) {
                if(name.endsWith(".jpg") || name.endsWith(".png")){
                    ret = findFile("Meshes", name, true, useBranch);
                }
            } else if (name.endsWith(".pip")) {
                ret = findFile("client_res/" + model, name, true, useBranch);
                if (!new File(baseDir, ret).exists()) {
                    ret = findFile("client_res", name, true, useBranch);
                }
                if (!new File(baseDir, ret).exists()) {
                    String aniDir = config.getClientMapFormat(model).aniFormat.dirName;
                    if (aniDir.length() > 0) {
                        ret = findFile("Animations/" + aniDir, name, true, useBranch);
                    } else {
                        ret = findFile("Animations", name, true, useBranch);
                    }
                }
            } else if(name.matches("\\d+_\\d+.png")){
                AnimationFormat aniFormat = config.getClientMapFormat(model).aniFormat;
                String dirName;
                //����aniFormatӦ�ò�Ϊ�գ�����default_client_modelû������
                if(aniFormat != null){
                   dirName = aniFormat.dirName;
                   ret = findFile("miniMapImage/"+dirName, name, true, useBranch);
                }else{
                   ret = findFile("miniMapImage", name, true, useBranch);
                }
            }  else if (name.endsWith(".png")) {
                ret = findFile("client_res/" + model, name, true, useBranch);
                if (!new File(baseDir, ret).exists()) {
                    ret = findFile("client_res", name, true, useBranch);
                }
                if (!new File(baseDir, ret).exists()) {
                    ret = findFile("psdata", name, true, useBranch);
                }
            } else if (name.equals("client.data")) {
                ret = findFile("client_pkg/" + model, name, true, useBranch);
                if (!new File(baseDir, ret).exists()) {
                    ret = findFile("client_pkg", name, true, useBranch);
                }
            } else if (name.endsWith(".ep") || name.endsWith(".ak")) {
                AnimationFormat aniFormat = config.getClientMapFormat(model).aniFormat;
                String dirName;
                //����aniFormatӦ�ò�Ϊ�գ�����default_client_modelû������
                if(aniFormat != null){
                   dirName = aniFormat.dirName;
                   if(dirName.equals("")){
                       ret = "packedAvatar/"+name;
                   }else{
                       ret = "packedAvatar/"+dirName+"/"+name;
                   }
                }else{
                    ret = "packedAvatar/"+name;
                }
            } else if(name.endsWith(".psdata")) {
                ret = findFile("psdata", name, true, useBranch);
            } else {
                ret = findFile("client_res/" + model, name, true, useBranch);
                if (!new File(baseDir, ret).exists()) {
                    ret = findFile("client_res", name, true, useBranch);
                }
            }
            downloadFileMapping.put(key, ret);
        }
        return ret;
    }
    
    /**
     * ����Ŀ��һ����Ŀ¼�������ļ������������ȴ���Branch�����ָ����branch����ô��������Branches/<branch_name>Ŀ¼��������Ӧ���ļ���
     * @param dir ��Ŀ¼��""��ʾ��Ŀ¼
     * @param name
     * @return
     */
    public String findFile(String dir, String name, boolean recursive) {
        return findFile(dir, name, recursive, branch);
    }
    
    /**
     * ����Ŀ��һ����Ŀ¼�������ļ������������ȴ���Branch�����ָ����branch����ô��������Branches/<branch_name>Ŀ¼��������Ӧ���ļ���
     * @param dir ��Ŀ¼��""��ʾ��Ŀ¼
     * @param name
     * @return
     */
    public String findFile(String dir, String name, boolean recursive, String useBranch) {
        if (useBranch != null && !useBranch.isEmpty()) {
            String newDir = dir.length() == 0 ? "Branches/" + branch : "Branches/" + branch + "/" + dir;
            String ret = findFileImpl(newDir, name, recursive);
            if (new File(baseDir, ret).exists()) {
                return ret;
            }
        }
        return findFileImpl(dir, name, recursive);
    }
    
    /*
     * ��һ����Ŀ¼�������ļ���
     * @param dir �����Ŀ¼���մ���ʾ��Ŀ¼
     * @param name �ļ���
     * @return �����ҵ����ļ������·��
     */
    private String findFileImpl(String dir, String name, boolean recursive) {
        File tdir = new File(baseDir, dir);
        if (!tdir.exists()) {
            return dir.length() == 0 ? name : dir + "/" + name;
        }
        File f = new File(tdir, name);
        if (f.exists()) {
            return dir.length() == 0 ? name : dir + "/" + name;
        }
        
        if (recursive) {
            // ����Ŀ¼������
            ArrayList<String> subDir = new ArrayList<String>();
            File[] files = tdir.listFiles();
            for (File ff : files) {
                if (ff.isDirectory()) {
                    subDir.add(ff.getName());
                }
            }
            while (subDir.size() > 0) {
                String subDirName = subDir.remove(0);
                if (new File(tdir, subDirName + "/" + name).exists()) {
                    return dir.length() == 0 ? subDirName + "/" + name : dir + "/" + subDirName + "/" + name;
                }
                files = new File(tdir, subDirName).listFiles();
                for (File ff : files) {
                    if (ff.isDirectory()) {
                        subDir.add(subDirName + "/" + ff.getName());
                    }
                }
            }
        }
        return dir.length() == 0 ? name : dir + "/" + name;
    }
    
    /**
     * ȡ��ĳ���ļ��ĵ�ǰ�汾�ţ�ʹ��ȱʡ��֧��
     * @param name �ͻ���ʹ�õ������ļ��������淶��
     * @param model �ͻ��˻���
     * @return ����ļ������ڣ�����0��
     */
    public int getFileVersion(String name, String model) {
        return getFileVersion(name, model, branch);
    }

    /**
     * ȡ��ĳ���ļ��ĵ�ǰ�汾�ţ�ʹ��ָ����֧��
     * @param name �ͻ���ʹ�õ������ļ��������淶��
     * @param model �ͻ��˻���
     * @param useBranch ʹ�÷�֧�汾
     * @return ����ļ������ڣ�����0��
     */
    public int getFileVersion(String name, String model, String useBranch) {
        try {
            String fullName = translateFileName(name, model, useBranch);
            Integer obj = resourceVersion.get(fullName);
            if (obj == null) {
                File f = new File(baseDir, fullName);
                int v = getFileCRCVersion(f);
                resourceVersion.put(fullName, v);
                return v;
            } else {
                return obj.intValue();
            }
        } catch (Exception e) {
            System.err.println("file not found: " + name + "/" + model);
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * �ж�һ���ļ��Ƿ��ǿͻ��˱����ļ���ʹ��ȱʡ��֧��
     * @param name �ͻ����ļ���
     * @param model �ͻ���UIModel
     * @return
     */
    public boolean isClientNeedFile(String name, String model) {
        return isClientNeedFile(name, model, branch);
    }
    
    /**
     * �ж�һ���ļ��Ƿ��ǿͻ��˱����ļ���ָ����֧��
     * @param name �ͻ����ļ���
     * @param model �ͻ���UIModel
     * @param useBranch ʹ�õķ�֧�汾
     * @return
     */
    public boolean isClientNeedFile(String name, String model, String useBranch) {
        ClientData clientData = branchClientData.get(useBranch == null ? "" : useBranch);
        return clientData.isClientNeedFile(name, model);
    }
    
    /**
     * �ж�һ���ļ��Ƿ���Ҫ�ͻ��˸��£�ʹ��ȱʡ��֧��
     * @param name �ͻ����ļ���
     * @param model �ͻ���UIModel
     * @return
     */
    public boolean needNotUpdate(String name, String model) {
        return needNotUpdate(name, model, branch);
    }

    /**
     * �ж�һ���ļ��Ƿ���Ҫ�ͻ��˸��£�ָ����֧��
     * @param name �ͻ����ļ���
     * @param model �ͻ���UIModel
     * @param useBranch ʹ�÷�֧�汾
     * @return
     */
    public boolean needNotUpdate(String name, String model, String useBranch) {
        ClientData clientData = branchClientData.get(useBranch == null ? "" : useBranch);
        return clientData.needNotUpdate(name, model);
    }
    
    /**
     * ȡ��ĳ���������пͻ��˱����ļ���ʹ��ȱʡ��֧��
     * @param model
     * @return
     */
    public String[] getClientNeedFiles(String model) {
        return getClientNeedFiles(model, branch);
    }
    
    /**
     * ȡ��ĳ���������пͻ��˱����ļ���ʹ��ָ����֧��
     * @param model
     * @param useBranch ʹ�õķ�֧�汾
     * @return
     */
    public String[] getClientNeedFiles(String model, String useBranch) {
        ClientData clientData = branchClientData.get(useBranch == null ? "" : useBranch);
        return clientData.getClientNeedFiles(model);
    }
    
    /**
     * �����ļ����������������������CTN��PIP��ETF�ļ���PKG�ļ�����ͨ��PackageUtils.makeClientPackage����á�ʹ��ȱʡ��֧��
     * @param name �ͻ���ʹ�õ������ļ��������淶��
     * @param model �ͻ��˻���
     * @return ����ļ������ڣ�����null
     */
    public byte[] downloadFile(String name, String model) {
        return downloadFile(name, model, branch);
    }
    
    /**
     * �����ļ����������������������CTN��PIP��ETF�ļ���PKG�ļ�����ͨ��PackageUtils.makeClientPackage����á�ʹ��ָ����֧��
     * @param name �ͻ���ʹ�õ������ļ��������淶��
     * @param model �ͻ��˻���
     * @param useBranch ʹ�÷�֧�汾
     * @return ����ļ������ڣ�����null
     */
    public byte[] downloadFile(String name, String model, String useBranch) {
        try {
            String path = translateFileName(name, model, useBranch);
            byte[] ret = findFile(path);
            return ret;
        } catch (Exception e) {
            System.err.println("file not found: " + name + "/" + model);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * ����һ���ļ������ݡ�
     * @param path �ļ��������Ŀ��Ŀ¼��·��
     * @return �ļ����ݣ�����ļ�δ�ҵ�������null��
     */
    public byte[] findFile(String name) {
    	if (!serverMode) {
    		throw new IllegalArgumentException();
    	}
    	byte[] ret = resourceCache.get(name);
    	if (ret != null) {
    		return ret;
    	}
    	try {
    		ret = Utils.loadFileData(new File(baseDir, name));
    	} catch (Exception e) {
    	}
    	if (ret != null && cacheFile) {
    		resourceCache.put(name, ret);
    	}
    	return ret;
    }
    
    /**
     * ���ݿͻ����������ж�Ӧ��ʹ�õķ�֧�汾��
     * @param channel �ͻ���8λ������
     * @return ���������������������֧�����汾����ô���ط�֧���ƣ����û�����ã�����ȱʡbranch��
     */
    public String getBranchOfClient(String channel) {
        String ret = channelToBranch.get(channel);
        if (ret == null) {
            return branch;
        } else {
            return ret;
        }
    }
 
    /**
     * ����ȫ��Ѱ·���ߡ�
     * @return
     */
    public AutoPathFinder getPathFinder() {
        return pathFinder;
    }
    
    /**
     * ����ĳ��Ŀ¼�¶����ļ��ĺϲ�ģʽ��
     */
    public void modifyMergeMode(Shell shell) {
        DirectoryDialog dlg = new DirectoryDialog(shell);
        dlg.setFilterPath(baseDir.getAbsolutePath());
        dlg.setText("ѡ��Ŀ¼");
        dlg.setMessage("��ѡ�񶯻��ļ�Ŀ¼��");
        String newPath = dlg.open();
        if (newPath == null) {
            return;
        }
        File[] arr = new File(newPath).listFiles();
        
        try {
            // ����Ŀ¼������PIP�ļ�
            HashMap<File, PipImage> images = new HashMap<File, PipImage>();
            for (File f : arr) {
                String fname = f.getName();
                if (fname.endsWith(".pip")) {
                    PipImage img = new PipImage();
                    img.load(f.getAbsolutePath());
                    images.put(f, img);
                }
            }
            
            // �����Ի�����е���
            AdjustPIPsDialog dlg2 = new AdjustPIPsDialog(shell, images);
            dlg2.open();
        } catch (Exception e) {
            MessageDialog.openError(shell, "����", e.toString());
        }
    }
    
    /**
     * �Ż�����AnimationĿ¼���ϲ���ͬ���ļ���
     */
    public void optimizeAnimations(Shell shell) {
        for (AnimationFormat format : config.animationFormats.values()) {
            try {
                File aniDir = new File(baseDir, "Animations/" + format.dirName);
                File[] arr = aniDir.listFiles();
                
                // �ҳ��ɺͿͻ��˹��õı�׼PIP�ļ����������ֿ�ͷ�ģ�
                HashMap<String, PipImage> standards = new HashMap<String, PipImage>();
                for (File f : arr) {
                    String fname = f.getName();
                    if (fname.endsWith(".pip")) {
                        if (!Character.isDigit(fname.charAt(0))) {
                            PipImage img = new PipImage();
                            img.load(f.getAbsolutePath());
                            standards.put(fname, img);
                        }
                    }
                }
                
                // �ҳ����п��ܱ��ϲ���PIP�ļ���������PIP�ļ����ƶ�>0.95
                HashMap<String, String> mergeNames = new HashMap<String, String>();
                HashMap<String, Double> mergeRates = new HashMap<String, Double>();
                for (File f : arr) {
                    String fname = f.getName();
                    if (fname.endsWith(".pip")) {
                        if (Character.isDigit(fname.charAt(0))) {
                            // �Ƚ�
                            PipImage img = new PipImage();
                            img.load(f.getAbsolutePath());
                            boolean foundMatch = false;
                            double bestMatchRate = 0.0;
                            for (String key : standards.keySet()) {
                                PipImage img2 = standards.get(key);
                                double matchRate = img.compare(img2);
                                if (matchRate > 0.5 && matchRate > bestMatchRate) {
                                    mergeNames.put(f.getName(), key);
                                    mergeRates.put(f.getName(), matchRate);
                                    foundMatch = true;
                                    bestMatchRate = matchRate;
                                }
                            }
                            if (!foundMatch || bestMatchRate < 0.95) {
                                standards.put(f.getName(), img);
                            }
                        }
                    }
                }
                
                // ��ʾȷ��
                if (mergeNames.isEmpty()) {
                    continue;
                }
                MergeImageConfirmDialog dlg = new MergeImageConfirmDialog(shell, aniDir, mergeNames, mergeRates);
                if (dlg.open() != Dialog.OK) {
                    continue;
                }
                Set<String> confirmedNames = dlg.getSelectedNames();
                
                // �������ж����ļ��е��ļ����ã��ѱ��ϲ����ļ����޸�Ϊ�ϲ�����ļ���
                for (File f : arr) {
                    String fname = f.getName();
                    if (fname.endsWith(".cts")) {
                        PipAnimateSet as = new PipAnimateSet();
                        as.load(f);
                        boolean changed = false;
                        for (int i = 0; i < as.getFileCount(); i++) {
                            if (confirmedNames.contains(as.getFileName(i))) {
                                as.setFileName(i, mergeNames.get(as.getFileName(i)));
                                changed = true;
                            }
                        }
                        if (changed) {
                            as.save(f, true);
                            String ctnName = f.getName().replaceAll("\\.cts$", "\\.ctn");
                            as.save(new File(aniDir, ctnName), false);
                        }
                    }
                }
                MessageDialog.openInformation(shell, "�ɹ�", "�����ɹ���");
            } catch (Exception e) {
                MessageDialog.openError(shell, "����", e.toString());
            }
        }
    }
    
    /**
     * ��������û���õ����ļ���
     */
    public void cleanGabage(Shell shell) {
        List<File> toBeDelete = new ArrayList<File>();
        
        // AnimationsĿ¼��ɾ��û�б����õ�cts, ctn��Ȼ��ɾ��û�б����õ�pip
        List<DataObject> anis = getDataListByType(Animation.class);
        for (AnimationFormat format : config.animationFormats.values()) {
            File dir = new File(baseDir, "Animations/" + format.dirName);
            HashSet<String> usedCTS = new HashSet<String>();
            HashSet<String> usedPIP = new HashSet<String>();
            for (DataObject obj : anis) {
                Animation ani = (Animation)obj;
                File f = ani.getAnimateFile(format.id);
                if (f != null) {
                    usedCTS.add(f.getName());
                    PipAnimateSet nset = new PipAnimateSet();
                    try {
                        nset.load(f);
                        String ctn = f.getAbsolutePath();
                        ctn = ctn.substring(0, ctn.length() - 1) + "n";
                        nset.save(new File(ctn), false);
                        for (int j = 0; j < nset.getFileCount(); j++) {
                            usedPIP.add(nset.getFileName(j));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                f = ani.getAttackAnimateFile(format.id);
                if (f != null) {
                    usedCTS.add(f.getName());
                    PipAnimateSet nset = new PipAnimateSet();
                    try {
                        nset.load(f);
                        String ctn = f.getAbsolutePath();
                        ctn = ctn.substring(0, ctn.length() - 1) + "n";
                        nset.save(new File(ctn), false);
                        for (int j = 0; j < nset.getFileCount(); j++) {
                            usedPIP.add(nset.getFileName(j));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            File[] files = dir.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    continue;
                }
                String n = f.getName();
                if (!Character.isDigit(n.charAt(0))) {
                    continue;
                }
                if (n.endsWith(".cts")) {
                    if (!usedCTS.contains(n)) {
                        toBeDelete.add(f);
                    }
                } else if (n.endsWith(".ctn")) {
                    if (!usedCTS.contains(n.substring(0, n.length() - 1) + "s")) {
                        toBeDelete.add(f);
                    }
                } else if (n.endsWith(".pip")) {
                    if (!usedPIP.contains(n)) {
                        toBeDelete.add(f);
                    }
                }
            }
        }
        
        // Areas��ɾ��û�б����õ�Ŀ¼
        List<DataObject> areas = getDataListByType(GameArea.class);
        HashSet<String> usedAreaDir = new HashSet<String>();
        for (DataObject obj : areas) {
            GameArea area = (GameArea)obj;
            usedAreaDir.add(area.source.getName());
        }
        File[] files = new File(baseDir, "Areas").listFiles();
        for (File f : files) {
            if (!f.isDirectory()) {
                continue;
            }
            String n = f.getName();
            if (n.toLowerCase().contains("cvs") || n.startsWith(".")) {
                continue;
            }
            if (!usedAreaDir.contains(n)) {
                toBeDelete.add(f);
            }
        }
        
        // Quests��ɾ��û�б����õ������ļ�
        List<DataObject> quests = getDataListByType(Quest.class);
        HashSet<String> usedQuestXML = new HashSet<String>();
        for (DataObject obj : quests) {
            Quest quest = (Quest)obj;
            usedQuestXML.add(quest.source.getName());
        }
        files = new File(baseDir, "Quests").listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                continue;
            }
            String n = f.getName();
            if (n.endsWith(".txt")) {
                if (!usedQuestXML.contains(n)) {
                    toBeDelete.add(f);
                }
            }
        }
        
        // ��ʾȷ��
        GenericChooseItemsDialog dlg = new GenericChooseItemsDialog(shell, "ɾ��ȷ��", toBeDelete);
        if (dlg.open() == Dialog.OK) {
            boolean[] flag = dlg.getSelectionFlag();
            for (int i = 0; i < toBeDelete.size(); i++) {
                if (flag[i]) {
                    File f = toBeDelete.get(i);
                    if (f.isFile()) {
                        f.delete();
                    } else {
                        Utils.deleteDir(f);
                    }
                }
            }
            MessageDialog.openInformation(shell, "�ɹ�", "�����ɹ���");
        }
    }
    
    public ProjectData reload(Class[] types, Map<Class, DataChangeHandler> handlers) throws Exception {
        return reload(null, types, handlers);
    }
    
    /**
     * ����������Ŀ���ݡ������ݻ�;����ݽ���һһ�ȶԣ�ֻ���޸ĺ�����ݲŻ�֪ͨ��Ӧ�Ĵ�����
     * ���д������ݱ仯���ܰ���3�����ͣ���ӡ��޸ġ�ɾ����
     * �������ֻ�ڷ�����ģʽ��Ч��
     * @param proj Ԥ�ȼ��غõ���Ŀ
     * @param types ���ص���������
     * @param handlers ��ͬ�������ݵı仯������
     */
    public ProjectData reload(ProjectData proj, Class[] types, Map<Class, DataChangeHandler> handlers) throws Exception {
        if (!this.serverMode) {
            throw new IllegalArgumentException();
        }
        
        // �����°汾����
        ProjectData newPrj;
        if (proj == null) {
            newPrj = new ProjectData();
            newPrj.serverMode = true;
            newPrj.createPathFinder = false;
            newPrj.branch = branch;
            newPrj.load(baseDir, getClass().getClassLoader());
        } else {
            newPrj = proj;
        }
        
        // ���������ֵ�������
        for (int i = 0; i < config.dictDataClasses.length; i++) {
            updateDataList(config.dictDataClasses[i], dictDataLists[i], newPrj.dictDataLists[i], handlers.get(config.dictDataClasses[i]));
        }
        
        // �������пɱ༭����
        for (int i = types.length - 1; i >= 0; i--) {
            int ind = getIndexByType(types[i]);
            Class cls = config.supportDataClasses[ind];
            updateDataList(cls, dataLists[ind], newPrj.dataLists[ind], handlers.get(cls));
        }

        return newPrj;
    }
    
    public void reloadFile() throws Exception {
        // ����ļ�����  
        resourceVersion.clear();
        resourceCache.clear();
        downloadFileMapping.clear();
        
        // ����ͻ�����Դ�����ļ�
        loadAllClientData();
        for (ClientData clientData : branchClientData.values()) {
            clientData.clientResVersion = (int)(System.currentTimeMillis() / 1000L);
        }
    }
    
    /**
     * ǿ�ƴӻ��������ĳ���ļ����´�ʹ��ʱ�������롣
     * @param path �ļ������dataĿ¼��ȫ·��������client_res/channel.data��
     */
    public void forceReloadFile(String path) {
        resourceVersion.remove(path);
        resourceCache.remove(path);
    }

    public void reloadPathFinder() {
        // �ع�Ѱ·����
        pathFinder = new AutoPathFinder(this);
    }

    /*
     * �Ƚ��¾������б�����ݣ�������ģʽ���Ѱ�ID���򣩡�
     */
    protected void updateDataList(Class dataClass, List<DataObject> oldList, List<DataObject> newList, DataChangeHandler handler) throws Exception {
        int i = 0;
        int j = 0;
        while (i < oldList.size() && j < newList.size()) {
            DataObject oldObj = oldList.get(i);
            DataObject newObj = newList.get(j);
            if (oldObj.id < newObj.id) {
                // �������˵���ɶ����ѱ�ɾ��
                if (handler != null) {
                    handler.dataObjectRemoved(oldObj);
                }
                oldList.remove(i);
            } else if (oldObj.id > newObj.id) {
                // �������˵�����½�����
                Constructor cons = dataClass.getConstructor(ProjectData.class);
                DataObject addObj = (DataObject)cons.newInstance(this);
                addObj.update(newObj);
                oldList.add(i, addObj);
                if (handler != null) {
                    handler.dataObjectAdded(addObj);
                }
                i++;
                j++;
            } else {
                // IDƥ�䣬��������Ƿ��޸�
                if (oldObj.changed(newObj)) {
                    if (handler != null) {
                        handler.dataObjectChanging(oldObj);
                    }
                    oldObj.update(newObj);
                    if (handler != null) {
                        handler.dataObjectChanged(oldObj);
                    }
                }
                i++;
                j++;
            }
        }
        
        // ��β��ɾ���ɶ�����ʣ�������Щ������Ӧ�ñ�ɾ����
        while (i < oldList.size()) {
            if (handler != null) {
                handler.dataObjectRemoved(oldList.get(i));
            }
            oldList.remove(i);
        }
        
        // ��β���¶�����ʣ��������ɶ���
        while (j < newList.size()) {
            Constructor cons = dataClass.getConstructor(ProjectData.class);
            DataObject addObj = (DataObject)cons.newInstance(this);
            addObj.update(newList.get(j));
            oldList.add(addObj);
            if (handler != null) {
                handler.dataObjectAdded(addObj);
            }
            j++;
        }
    }
    
    /**
     * ����С��ʾ��
     * @return
     */
    public List<Hint> loadHints() throws Exception {
       Document doc = Utils.loadDOM(new File(baseDir, "hints.xml"));
       List list = doc.getRootElement().getChildren("hint");
       List<Hint> ret = new ArrayList<Hint>();
       for (int i = 0; i < list.size(); i++) {
           Element elem = (Element)list.get(i);
           Hint hint = new Hint();
           hint.load(elem);
           ret.add(hint);
       }
       return ret;
    }
    

    public DataObjectCategory newCategory(DataObjectCategory selCate, Class cls, String name) throws Exception {
        int index = getIndexByType(cls);
        for (DataObjectCategory cate : selCate.cates) {
            if (cate.name.equals(name)) {
                throw new Exception("�������Ʋ����ظ���");
            }
        }
        DataObjectCategory cate = new DataObjectCategory(cls);
        cate.name = name;
        
        selCate.cates.add(cate);
        cate.parent = selCate;
//        dataCateLists[index].add(cate);
        return cate;
    }

    //���еĹ���
    public void getAllMonsterNpc(List<DataObjectCategory> monsterNpcs) {        
        List<DataObjectCategory> npcs = dataCateLists[getIndexByType(NPCTemplate.class)];
        
        for(DataObjectCategory cate: npcs) {
            DataObjectCategory doCate = new DataObjectCategory(NPCTemplate.class);
            doCate.name = cate.name;
            if(addMonsterNpcCate(cate, doCate)) {
                monsterNpcs.add(doCate);
            }
        }        
    }
      
    private boolean addMonsterNpcCate(DataObjectCategory npcCate, DataObjectCategory monsteCate) {
        boolean hasMonster = false;
        for(DataObject npc: npcCate.objects) {
            NPCTemplate _npc = (NPCTemplate)npc;
            if(_npc.type.id == 4) {
                hasMonster = true;
                monsteCate.objects.add(_npc);
                
                monsteCate.name = npcCate.name;
            }
        }
        
        for(DataObjectCategory cate: npcCate.cates) {
            DataObjectCategory doCate2 = new DataObjectCategory(NPCTemplate.class);
            if(hasMonster = true) {
                addMonsterNpcCate(cate, doCate2);
            } else {
                hasMonster = addMonsterNpcCate(cate, doCate2);
            }
            
            if(hasMonster = true) {                
                monsteCate.cates.add(doCate2);
                monsteCate.name = npcCate.name;
                monsteCate.parent = doCate2;
            }
            
        }
        
        
        return hasMonster;  
    }
    public BuffConfig findBuff(int id) {
        return (BuffConfig)this.findObject(BuffConfig.class, id);
    }
    
    /**
     * ȱʡȱʡ��֧����Դ�汾�š�
     * @return
     */
    public int getClientResVersion() {
        return getClientResVersion(branch);
    }
    
    /**
     * ȡ��ָ����֧����Դ�汾�š�
     * @return
     */
    public int getClientResVersion(String useBranch) {
        ClientData clientData = branchClientData.get(useBranch == null ? "" : useBranch);
        return clientData.clientResVersion;
    }
    
    /**
     * 
     * ���ڼ��������Ʒʹ�ú󣬻�����Щ���͵���Ʒ
     */
    public void checkUseItemDrop() {
        List<DataObject> list = getDataListByType(Item.class);
        for(DataObject obj :list ) {
            Item item = (Item)obj;
            List<ItemEffect> ieffs = item.effects;
            for(ItemEffect eff : ieffs) {
                if(eff != null && eff.effectType == 12) {
                    Map<String, String> ps = eff.param;
                    Set<String> keys = ps.keySet();
                    for(String key : keys) {
                        if(key.equals("useitem")) {
                            Item itm = findItem(Integer.parseInt(ps.get(key)));
//                          if(itm != null && itm.mainType == 12) {
//                              System.out.println("item=" + item.getId() + "," + item.getTitle());
//                          }
                        } else if(key.startsWith("dropgroup")) {
                            DropGroup dg = (DropGroup)findObject(DropGroup.class, Integer.parseInt(ps.get(key)));
                            if(dg != null) {
                                List<DropItem> dropItems = dg.getAllDropItems(null);
                                for(DropItem di : dropItems ) {
                                    Item im = findItem(di.dropID);
//                                  if(im != null && im.mainType == 12) {
                                        System.out.println("item=" + item.getId() + "," + item.getTitle());
//                                  }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static ProjectDataFactory projDataFactory = new ProjectDataFactory();
}
