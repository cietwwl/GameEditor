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
 * 项目数据集合。所有数据对象都继承DataObject接口。同一类型的数据对象保存在一个XML文件中。所有数据对象的
 * XML文件，以及数据对象引用的其他文件，都保存在一个项目目录的子目录中。
 * @author lighthu
 */
public class ProjectData {
    // 当前项目路径
    public java.io.File baseDir;
    // 保存动作监听
    private IGameDataListener dataListener;
    
    // 所有编辑器支持的对象列表，和supportDataClasses顺序对应。
    public List<DataObject>[] dataLists;
    // 对象分类列表
    public List<DataObjectCategory>[] dataCateLists;
    // 所有字典对象列表，和dictDataClasses是顺序对应
    public List<DataObject>[] dictDataLists;
    
    // 是否服务器模式。在服务器模式下，所有访问的内容被缓存起来。
    public boolean serverMode = false;
    // 在服务器模式下，是否创建寻路工具（创建寻路工具可能会耗费大量时间）
    public boolean createPathFinder = true;
    // 分支版本，用于支持CMCC和CHINATEL版本，null表示PIP版本。仅用于服务器模式。
    public String branch = null;
    // 文件缓存，仅用于服务器模式
    protected Hashtable<String, byte[]> resourceCache = new Hashtable<String, byte[]>();
    // 所有文件的版本号（仅用于服务器模式）
    private Hashtable<String, Integer> resourceVersion = new Hashtable<String, Integer>();
    // 所有文件名和实际文件的对应关系（仅用于服务器模式）
    protected Hashtable<String, String> downloadFileMapping = new Hashtable<String, String>();
    // 自动寻路工具（仅用于服务器模式）
    public AutoPathFinder pathFinder;
    // 不同分支的客户端数据配置文件（key是branch名称，""表示缺省分支）
    protected Hashtable<String, ClientData> branchClientData;
    // 不同的客户端渠道号对应的分支版本
    protected Hashtable<String, String> channelToBranch;
    
    /** CMCC道具代码设置 */
    public CmccConfig cmccConfig;
    /** 游戏特殊配置 */
    public ProjectConfig config;
    /** 技能效果配置 */
    public EffectConfigManager effectConfigManager;
    
    /* 当前项目singleton，用于替代从EditorApplication中获取ProjectData */
    private static ProjectData activeProject;
    
    /**
     * 获取当前激活的project的引用。
     * @return
     */
    public static ProjectData getActiveProject(){
        return activeProject;
    }
    
    /**
     * 设置激活的project。
     * @param proj
     */
    public static void setActiveProject(ProjectData proj) {
        activeProject = proj;
    }
    
    public ProjectData() {
    }
    
    /**
     * 设置数据改变动作监听者。
     * @param l
     */
    public void setDataListener(IGameDataListener l) {
        dataListener = l;
    }
    
    /**
     * 取得一个数据类型在类型表中的位置。
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
     * 取得一个可编辑类型的位置
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
     * 取得一个类型的所有数据对象。
     * @param cls 数据对象类型
     * @return
     */
    public List<DataObject> getDataListByType(Class cls) {
        return (List<DataObject>)dataLists[getIndexByType(cls)];
    }
    
    /**
     * 取得一个类型的所有数据对象分类。
     * @param cls 数据对象类型
     */
    public List<DataObjectCategory> getCategoryListByType(Class cls) {
        return (List<DataObjectCategory>)dataCateLists[getIndexByType(cls)];
    }
    
    /**
     * 取得一个字典类型的所有数据对象。
     * @param cls 数据对象类型
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
     * 查找指定ID，指定类型的数据对象。
     * @param cls 数据对象类型
     * @param id 对象ID
     * @return 如果没有找到，返回null
     */
    public DataObject findObject(Class cls, int id) {
        List<DataObject> list = getDataListByType(cls);
        
        // 如果是服务器模式，则数据是已经排序的，可使用二分查找
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
     * 查找指定类型数据的一个数据分类。
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
     * 根据ID查找物品或装备
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
     * 根据id查找技能
     * @param id
     * @return
     */
    public SkillConfig findSkill(int id){
        return (SkillConfig)findObject(SkillConfig.class,id);
    }
    /**
     * 根据id查找物品
     * @param id
     * @return
     */
    public Item findItem(int id) {
        return (Item)findObject(Item.class, id);
    }
    
    /**
     * 根据id查找装备
     * @param id
     * @return
     */
    public Equipment findEquipment(int id) {
        return (Equipment)findObject(Equipment.class, id);
    }
    
    /**
     * 根据id查找禁用组物品
     * @param id
     * @return
     */
    public ForbidItem findForbidItem(int id) {
        return (ForbidItem)findObject(ForbidItem.class, id);
    }
    
    
    /**
     * 根据id查找禁用组技能
     * @param id
     * @return
     */
    public ForbidSkill findForbidSkill(int id) {
        return (ForbidSkill)this.findObject(ForbidSkill.class, id);
    }
    
    /**
     * 根据id查找AI
     * @param id
     * @return
     */
    public AIData findAIData(int id) {
        return (AIData)findObject(AIData.class, id);
    }
    
    /**
     * 查找指定数据对象在总列表中的索引。
     * @param obj 数据对象
     * @return 如果没有找到，返回-1
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
     * 查找指定ID，指定类型的字典数据对象。
     * @param cls 数据对象类型
     * @param id 对象ID
     * @return 如果没有找到，返回null
     */
    public DataObject findDictObject(Class cls, int id) {
        List<DataObject> list = getDictDataListByType(cls);
        
        // 如果是服务器模式，则数据是已经排序的，可使用二分查找
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
     * 查找指定字典数据对象在总列表中的索引。
     * @param obj 数据对象
     * @return 如果没有找到，返回-1
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
     * 创建一个新的数据对象，并加入到列表中。
     * @param cls 数据对象类型
     * @return 新创建的数据对象 
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
     * @param selectObject 编辑器中选中的项
     */
    public void addObjectToList(Class cls, DataObject newObj, Object selectObject) {
        // 加入总列表
        int index = getIndexByType(cls);
        dataLists[index].add(newObj);
              
        //DataListView中不支持编辑的对象，比如EquipmentPrefix
        if(getEditableIndexByType(cls) < 0) {
            for (DataObjectCategory cate : dataCateLists[index]) {
                if(newObj.cate == null) {
                    newObj.cate = cate;
                }
                cate.objects.add(newObj);
                break;
            }
        } else {
            // 加入分类列表
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
     * 创建一个新的数据分类。
     * @param cls 数据对象类型
     * @return
     */
    public DataObjectCategory newCategory(Class cls, String name) throws Exception {
        int index = getIndexByType(cls);
        for (DataObjectCategory cate : dataCateLists[index]) {
            if (cate.name.equals(name)) {
                throw new Exception("分类名称不能重复。");
            }
        }
        DataObjectCategory cate = new DataObjectCategory(cls);
        cate.name = name;
        dataCateLists[index].add(cate);
        return cate;
    }
    
    /**
     * 新建一个物品，并分配id，添加到类型列表中
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
            case 0://魔石
                newItemId = 1020001;
                break;
            case 1://粉末
                newItemId = 1030001;
                break;
            case 2://圣翼碎片+图纸
            case 3:
                newItemId = 1050001;
                break;
            case 4://时装+合成器
                newItemId = 1060001;
                break;
            case 5://任务物品
                newItemId = 1080001;
                break;
            case 6://活动物品
                newItemId = 1090001;
                break;
            case 7://运营道具
                newItemId = 1100001;
                break;
            case 8://特殊材料->铁矿石
            case 9://特殊材料->女神印章
            case 10://特殊材料->金装碎片
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
     * 新建一个装备，并分配id，添加到类型列表中
     * @param equiType
     * @return
     */
    public Equipment newEquipment(DataObjectCategory category, Object selectObject) throws Exception{
        Equipment equi = (Equipment) newObject(Equipment.class, selectObject);
        // 装备和物品公用id，需要分段从百万开始
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
     * 更新对象。任何一个对象的更新都会触发XML文件存储。
     * @param src 新的数据
     * @param dest 需要更新的目标对象
     * @throws Exception
     */
    public void updateObject(DataObject src, DataObject dest) throws Exception {
        // 确保没有重复的ID
        DataObject searchResult = findObject(src.getClass(), src.id);
        if (searchResult != null && searchResult != dest) {
            throw new Exception("重复的ID。");
        }
        
        // 保存对象属性并更新XML文件
        dest.update(src);
        saveDataList(dest.getClass());
    }
    
    /**
     * 把一个数据对象从一个分类移动到另外一个分类。
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
     * 删除一个对象（本方法不会删除此数据对象的关联对象）。
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
     * 保存所有类型的数据对象列表。
     */
    public void saveAll() throws Exception {
        for (int i = 0; i < config.supportDataClasses.length; i++) {
            saveDataList(config.supportDataClasses[i]);
        }
    }
    
    /**
     * 取得某类型数据对应的索引文件。
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
     * 保存一个类型的所有数据对象。
     * @param cls 数据对象类型
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
     * 保存一个类型的所有字典数据对象。
     * @param cls 数据对象类型
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
     * 找出依赖于指定对象的所有相关对象。
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
     * 载入项目。项目支持的所有数据对象都会被载入。载入的顺序是supportDataClasses的反序，所以，如果一类
     * 数据依赖于另一类数据，则被依赖的数据要放在数组的后面。
     * @param dir
     * @param classLoader 用于动态载入类
     * @throws Exception
     */
    public void load(File dir, ClassLoader classLoader) throws Exception {
        baseDir = dir;
        config = new ProjectConfig(this);
        config.load(classLoader);
        effectConfigManager = new EffectConfigManager(classLoader, baseDir.getAbsolutePath()); 

        // 初始化数据存储区
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
        
        // 载入字典数据
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
            
            // 创建缺省分类
            DataObjectCategory emptyCate = new DataObjectCategory(cls);
            emptyCate.name = "";
            cateMap.put("", emptyCate);
            dataCateLists[i].add(emptyCate);
            
            for (Object elem : list) {
                DataObject newObj = (DataObject)config.dictDataClasses[i].newInstance();
                newObj.load((Element)elem);
                dictDataLists[i].add(newObj);
            
            }
            
            // 检查是否有重复ID数据
            checkID(dictDataLists[i]);
            
            // 如果是服务器模式，进行排序以便查找
            if (this.serverMode) {
                Collections.sort(dictDataLists[i]);
            }
        }
        
        // 载入可编辑数据
        for (int i = config.supportDataClasses.length - 1; i >= 0; i--) {
            Document doc = null;
            try{
                doc = Utils.loadDOM(new File(baseDir, config.dataFiles[i]));
            }catch(Exception e){
                e.printStackTrace();
            }
            List list = doc.getRootElement().getChildren(config.dataTags[i]);
            HashMap<String, DataObjectCategory> cateMap = new HashMap<String, DataObjectCategory>();
            
            // 创建缺省分类
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
                    System.err.println("构造方法参数不匹配.");
                    throw iae;
                }
                newObj.load((Element)elem);
                dataLists[i].add(newObj);
                if (this.serverMode) {
                    newObj.editorIndex = dataLists[i].size() - 1;
                }                
                
                //如果是带有目录结构的，特殊处理
                if(newObj.getCategoryName().indexOf(',') >= 0) {
                    loadCate(emptyCate, cls, i, newObj, cateMap);
                } else {                                    
                    if (this.serverMode) {
                        newObj.editorIndex = dataLists[i].size() - 1;
                    }
                    
                    // 加入分类列表中
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
            
            // 检查是否有重复ID数据
            checkID(dataLists[i]);
            
            // 如果是服务器模式，进行排序以便查找
            if (this.serverMode) {
                Collections.sort(dataLists[i]);
            }
            
        }

        cmccConfig = new CmccConfig(new File(baseDir, "cmcc_config.xml"));
        
        // 服务器模式下，构建所有场景的通达关系表以支持自动寻路功能
        if (this.serverMode && this.createPathFinder) {
            pathFinder = new AutoPathFinder(this);
        }
        
        // 服务器模式下读入客户端资源配置文件
        if(this.serverMode){
            loadAllClientData();
        }
    }
    
    /**
     * 服务器模式下，载入所有的client_pkg_xxx.xml配置。用于为不同分支版本提供下载服务。
     */
    protected void loadAllClientData() throws Exception {
        branchClientData = new Hashtable<String, ClientData>();

        // 载入缺省cliekt_pkg.xml
        ClientData clientData = new ClientData(this, null);
        branchClientData.put("", clientData);
        
        // 搜索项目目录下所有client_pkg_xxx.xml，载入分支配置
        File[] ffs = baseDir.listFiles();
        for (File f : ffs) {
            if (f.isFile() && f.getName().startsWith("client_pkg_") && f.getName().endsWith(".xml")) {
                String n = f.getName();
                String branch = n.substring("client_pkg_".length(), n.length() - 4);
                clientData = new ClientData(this, branch);
                branchClientData.put(branch, clientData);
            }
        }
        
        // 载入branch.xml，读取渠道号到分支的关系表
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
     * 检查一个对象列表，确保没有重复ID.
     */
    protected void checkID(List<DataObject> list) throws Exception {
        Set<Integer> idset = new HashSet<Integer>();
        for (DataObject dobj : list) {
            int id = dobj.id;
            if (idset.contains(id)) {
                throw new Exception("ID重复(" + id + ")：" + dobj.getClass().getName());
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
        
        // 加入分类列表中
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
            //载入世界地图数据文件
            MapFile mapFile = new MapFile();
            mapFile.load(mapf);
            //生成世界地图下载文件
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
     * 设置是否缓存文件
     */
    public static boolean cacheFile = true;
    /**
     * 为所有关卡生成客户端下载文件。
     */
    public void makeClientPackages() throws Exception {
        clbAndLfbCache.clear();
        for (DataObject obj : getDataListByType(GameArea.class)) {
            String info = "";
            try {
                GameArea ga = (GameArea)obj;
                info = ga.toString();
                
                // 载入地图信息文件
                GameAreaInfo areaInfo = new GameAreaInfo(ga);
                areaInfo.load();
                
                // 生成所有版本的地图文件
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
                throw new Exception("错误地图："+info,e);
            }
        }
        clbAndLfbCache.clear();
    }
        
    /**
     * 为所有BUFF对象生成Java Class文件。
     * @throws Exception
     */
    public void generateBuffClasses(String encoding) throws Exception {
        List<DataObject> buffs = getDataListByType(BuffConfig.class);
        File clsDir = new File(Settings.exportClassDir, Settings.buffPackage.replace('.', '/'));
        
        // 生成Java文件
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
                throw new Exception("buff " + bc.id + "存在错误：" + e.toString());
            }
        }
        
        // 保存buffs.xml
        this.saveDataList(BuffConfig.class);
    }
    
    /**
     * 为所有Skill对象生成Java Class文件。
     * @throws Exception
     */
    public void generateSkillClasses(String encoding) throws Exception {
        List<DataObject> skills = getDataListByType(SkillConfig.class);
        File clsDir = new File(Settings.exportClassDir, Settings.skillPackage.replace('.', '/'));
        
        // 生成Java文件
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
                throw new Exception("skill " + bc.getId() + "存在错误：" + e.toString());
            }
        }
        
        // 保存skills.xml
        this.saveDataList(SkillConfig.class);
    }
    
    /**
     * 为所有Quest对象生成Java Class文件。
     * @throws Exception
     */
    public void generateQuestClasses(String encoding) throws Exception {
        List<DataObject> quests = getDataListByType(Quest.class);
        File clsDir = new File(Settings.exportClassDir, Settings.questPackage.replace('.', '/'));
        
        // 生成Java文件
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
     * 为所有AI对象生成Java Class文件。
     * @throws Exception
     */
    public void generateAIClasses(String encoding) throws Exception {
        
        List<DataObject> aiData = getDataListByType(AIData.class);
        //List<DataObject> npc = getDataListByType(NPCTemplate.class);
        File clsDir = new File(Settings.exportClassDir, Settings.aiPackage.replace('.', '/'));
        
        // 生成Java文件
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
     * 生成所有场景的列表
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
     * 更新所有任务文本中的NPC和地图引用。
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
     * 根据公式自动更新所有装备的价格和耐久度属性。
     * @throws Exception
     */
    public void updateEquipmentPrices() throws Exception {
        // 更新装备表
        List<DataObject> equiList = getDataListByType(Equipment.class);
        for (DataObject equi : equiList) {
            ((Equipment)equi).recalcPriceAndDurability();
        }
        saveDataList(Equipment.class);
        
        // 更新商店中的引用
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
     * 扫描项目目录，统计所有资源文件的版本号，写成一个XML文件。
     * @throws Exception
     */
    public void generateResourceVersionXML() throws Exception {
        // 取缺省cliekt_pkg.xml处理
        ClientData clientData = new ClientData(this, null);
        clientData.makeClientData();
        
        // 搜索项目目录下所有client_pkg_xxx.xml，处理branches
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
     * 取得文件的当前版本号。
     * 版本号编码规则：
     * 4字节整数，前3个字节表示文件大小，最后一个字节表示文件CRC（字节异或算法）。
     * 新算法：前2个字节表示文件大小的低16位，后两个字节表示CRC16。
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
     * 字节流CRC值 （8位）
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
     * 字节流CRC值 （16位）
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
     * 取得一个文件在CVS中的版本号。
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
     * 把下载文件名转换为实际文件名，使用缺省分支。
     * @param name 客户端使用的下载文件名（见规范）
     * @param model 客户端机型
     */
    public String translateFileName(String name, String model) {
        return translateFileName(name, model, branch);
    }
    
    /*
     * 把下载文件名转换为实际文件名。
     * @param name 客户端使用的下载文件名（见规范）
     * @param model 客户端机型
     * @param useBranch 使用分支
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
            // 找出分支对应的ClientData
            ClientData clientData = branchClientData.get(useBranch == null ? "" : useBranch);
            
            // 优先搜索客户端内置资源
            if (name.endsWith(".etf")) {
                ret = clientData.getScriptsDir() + "/" + model + "/" + (name.substring(0, name.length() - 4)) + "_" + model + ".etf.gz";
            } else if ((ret = clientData.getMatchPath(model, name)) != null) {
                // nothing to do
            } else if (name.endsWith(".pkg") && Character.isDigit(name.charAt(0))) {
                int id ;
                
                int idxOf_ = name.indexOf('_', 0);
                if(idxOf_ != -1){
                    //大版地图
                    id = Integer.parseInt(name.substring(0, idxOf_));
                }else{
                    //其他版本地图
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
                //这里aniFormat应该不为空，除非default_client_model没有设置
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
                //这里aniFormat应该不为空，除非default_client_model没有设置
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
     * 在项目的一个子目录中搜索文件。本方法优先处理Branch，如果指定了branch，那么会优先在Branches/<branch_name>目录下搜索对应的文件。
     * @param dir 子目录，""表示根目录
     * @param name
     * @return
     */
    public String findFile(String dir, String name, boolean recursive) {
        return findFile(dir, name, recursive, branch);
    }
    
    /**
     * 在项目的一个子目录中搜索文件。本方法优先处理Branch，如果指定了branch，那么会优先在Branches/<branch_name>目录下搜索对应的文件。
     * @param dir 子目录，""表示根目录
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
     * 在一个子目录中搜索文件。
     * @param dir 相对子目录，空串表示根目录
     * @param name 文件名
     * @return 返回找到的文件的相对路径
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
            // 在子目录中搜索
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
     * 取得某个文件的当前版本号，使用缺省分支。
     * @param name 客户端使用的下载文件名（见规范）
     * @param model 客户端机型
     * @return 如果文件不存在，返回0。
     */
    public int getFileVersion(String name, String model) {
        return getFileVersion(name, model, branch);
    }

    /**
     * 取得某个文件的当前版本号，使用指定分支。
     * @param name 客户端使用的下载文件名（见规范）
     * @param model 客户端机型
     * @param useBranch 使用分支版本
     * @return 如果文件不存在，返回0。
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
     * 判断一个文件是否是客户端必须文件，使用缺省分支。
     * @param name 客户端文件名
     * @param model 客户端UIModel
     * @return
     */
    public boolean isClientNeedFile(String name, String model) {
        return isClientNeedFile(name, model, branch);
    }
    
    /**
     * 判断一个文件是否是客户端必须文件，指定分支。
     * @param name 客户端文件名
     * @param model 客户端UIModel
     * @param useBranch 使用的分支版本
     * @return
     */
    public boolean isClientNeedFile(String name, String model, String useBranch) {
        ClientData clientData = branchClientData.get(useBranch == null ? "" : useBranch);
        return clientData.isClientNeedFile(name, model);
    }
    
    /**
     * 判断一个文件是否不需要客户端更新，使用缺省分支。
     * @param name 客户端文件名
     * @param model 客户端UIModel
     * @return
     */
    public boolean needNotUpdate(String name, String model) {
        return needNotUpdate(name, model, branch);
    }

    /**
     * 判断一个文件是否不需要客户端更新，指定分支。
     * @param name 客户端文件名
     * @param model 客户端UIModel
     * @param useBranch 使用分支版本
     * @return
     */
    public boolean needNotUpdate(String name, String model, String useBranch) {
        ClientData clientData = branchClientData.get(useBranch == null ? "" : useBranch);
        return clientData.needNotUpdate(name, model);
    }
    
    /**
     * 取得某个机型所有客户端必须文件，使用缺省分支。
     * @param model
     * @return
     */
    public String[] getClientNeedFiles(String model) {
        return getClientNeedFiles(model, branch);
    }
    
    /**
     * 取得某个机型所有客户端必须文件，使用指定分支。
     * @param model
     * @param useBranch 使用的分支版本
     * @return
     */
    public String[] getClientNeedFiles(String model, String useBranch) {
        ClientData clientData = branchClientData.get(useBranch == null ? "" : useBranch);
        return clientData.getClientNeedFiles(model);
    }
    
    /**
     * 下载文件。这个方法可以用来下载CTN，PIP和ETF文件。PKG文件还是通过PackageUtils.makeClientPackage来获得。使用缺省分支。
     * @param name 客户端使用的下载文件名（见规范）
     * @param model 客户端机型
     * @return 如果文件不存在，返回null
     */
    public byte[] downloadFile(String name, String model) {
        return downloadFile(name, model, branch);
    }
    
    /**
     * 下载文件。这个方法可以用来下载CTN，PIP和ETF文件。PKG文件还是通过PackageUtils.makeClientPackage来获得。使用指定分支。
     * @param name 客户端使用的下载文件名（见规范）
     * @param model 客户端机型
     * @param useBranch 使用分支版本
     * @return 如果文件不存在，返回null
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
     * 查找一个文件的内容。
     * @param path 文件相对于项目根目录的路径
     * @return 文件内容，如果文件未找到，返回null。
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
     * 根据客户端渠道号判断应该使用的分支版本。
     * @param channel 客户端8位渠道号
     * @return 如果这个渠道配置了特殊分支渠道版本，那么返回分支名称；如果没有配置，返回缺省branch。
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
     * 返回全局寻路工具。
     * @return
     */
    public AutoPathFinder getPathFinder() {
        return pathFinder;
    }
    
    /**
     * 调整某个目录下动画文件的合并模式。
     */
    public void modifyMergeMode(Shell shell) {
        DirectoryDialog dlg = new DirectoryDialog(shell);
        dlg.setFilterPath(baseDir.getAbsolutePath());
        dlg.setText("选择目录");
        dlg.setMessage("请选择动画文件目录：");
        String newPath = dlg.open();
        if (newPath == null) {
            return;
        }
        File[] arr = new File(newPath).listFiles();
        
        try {
            // 载入目录下所有PIP文件
            HashMap<File, PipImage> images = new HashMap<File, PipImage>();
            for (File f : arr) {
                String fname = f.getName();
                if (fname.endsWith(".pip")) {
                    PipImage img = new PipImage();
                    img.load(f.getAbsolutePath());
                    images.put(f, img);
                }
            }
            
            // 弹出对话框进行调整
            AdjustPIPsDialog dlg2 = new AdjustPIPsDialog(shell, images);
            dlg2.open();
        } catch (Exception e) {
            MessageDialog.openError(shell, "错误", e.toString());
        }
    }
    
    /**
     * 优化所有Animation目录，合并相同的文件。
     */
    public void optimizeAnimations(Shell shell) {
        for (AnimationFormat format : config.animationFormats.values()) {
            try {
                File aniDir = new File(baseDir, "Animations/" + format.dirName);
                File[] arr = aniDir.listFiles();
                
                // 找出可和客户端共用的标准PIP文件（不以数字开头的）
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
                
                // 找出所有可能被合并的PIP文件，和其他PIP文件相似度>0.95
                HashMap<String, String> mergeNames = new HashMap<String, String>();
                HashMap<String, Double> mergeRates = new HashMap<String, Double>();
                for (File f : arr) {
                    String fname = f.getName();
                    if (fname.endsWith(".pip")) {
                        if (Character.isDigit(fname.charAt(0))) {
                            // 比较
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
                
                // 提示确认
                if (mergeNames.isEmpty()) {
                    continue;
                }
                MergeImageConfirmDialog dlg = new MergeImageConfirmDialog(shell, aniDir, mergeNames, mergeRates);
                if (dlg.open() != Dialog.OK) {
                    continue;
                }
                Set<String> confirmedNames = dlg.getSelectedNames();
                
                // 处理所有动画文件中的文件引用，把被合并的文件名修改为合并后的文件名
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
                MessageDialog.openInformation(shell, "成功", "操作成功！");
            } catch (Exception e) {
                MessageDialog.openError(shell, "错误", e.toString());
            }
        }
    }
    
    /**
     * 清理所有没有用到的文件。
     */
    public void cleanGabage(Shell shell) {
        List<File> toBeDelete = new ArrayList<File>();
        
        // Animations目录，删除没有被引用的cts, ctn，然后删除没有被引用的pip
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
        
        // Areas，删除没有被引用的目录
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
        
        // Quests，删除没有被引用的任务文件
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
        
        // 提示确认
        GenericChooseItemsDialog dlg = new GenericChooseItemsDialog(shell, "删除确认", toBeDelete);
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
            MessageDialog.openInformation(shell, "成功", "操作成功！");
        }
    }
    
    public ProjectData reload(Class[] types, Map<Class, DataChangeHandler> handlers) throws Exception {
        return reload(null, types, handlers);
    }
    
    /**
     * 重新载入项目数据。新数据会和旧数据进行一一比对，只有修改后的数据才会通知对应的处理器
     * 进行处理。数据变化可能包含3种类型：添加、修改、删除。
     * 这个操作只在服务器模式有效。
     * @param proj 预先加载好的项目
     * @param types 重载的数据类型
     * @param handlers 不同类型数据的变化处理器
     */
    public ProjectData reload(ProjectData proj, Class[] types, Map<Class, DataChangeHandler> handlers) throws Exception {
        if (!this.serverMode) {
            throw new IllegalArgumentException();
        }
        
        // 载入新版本数据
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
        
        // 更新所有字典类数据
        for (int i = 0; i < config.dictDataClasses.length; i++) {
            updateDataList(config.dictDataClasses[i], dictDataLists[i], newPrj.dictDataLists[i], handlers.get(config.dictDataClasses[i]));
        }
        
        // 更新所有可编辑数据
        for (int i = types.length - 1; i >= 0; i--) {
            int ind = getIndexByType(types[i]);
            Class cls = config.supportDataClasses[ind];
            updateDataList(cls, dataLists[ind], newPrj.dataLists[ind], handlers.get(cls));
        }

        return newPrj;
    }
    
    public void reloadFile() throws Exception {
        // 清除文件缓存  
        resourceVersion.clear();
        resourceCache.clear();
        downloadFileMapping.clear();
        
        // 读入客户端资源配置文件
        loadAllClientData();
        for (ClientData clientData : branchClientData.values()) {
            clientData.clientResVersion = (int)(System.currentTimeMillis() / 1000L);
        }
    }
    
    /**
     * 强制从缓存中清除某个文件，下次使用时重新载入。
     * @param path 文件相对于data目录的全路径，例如client_res/channel.data。
     */
    public void forceReloadFile(String path) {
        resourceVersion.remove(path);
        resourceCache.remove(path);
    }

    public void reloadPathFinder() {
        // 重构寻路数据
        pathFinder = new AutoPathFinder(this);
    }

    /*
     * 比较新旧两个列表的数据（服务器模式，已按ID排序）。
     */
    protected void updateDataList(Class dataClass, List<DataObject> oldList, List<DataObject> newList, DataChangeHandler handler) throws Exception {
        int i = 0;
        int j = 0;
        while (i < oldList.size() && j < newList.size()) {
            DataObject oldObj = oldList.get(i);
            DataObject newObj = newList.get(j);
            if (oldObj.id < newObj.id) {
                // 这种情况说明旧对象已被删除
                if (handler != null) {
                    handler.dataObjectRemoved(oldObj);
                }
                oldList.remove(i);
            } else if (oldObj.id > newObj.id) {
                // 这种情况说明有新建对象
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
                // ID匹配，检查数据是否修改
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
        
        // 收尾，删除旧队列中剩余对象，这些对象都是应该被删除的
        while (i < oldList.size()) {
            if (handler != null) {
                handler.dataObjectRemoved(oldList.get(i));
            }
            oldList.remove(i);
        }
        
        // 收尾，新队列中剩余对象加入旧队列
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
     * 载入小提示。
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
                throw new Exception("分类名称不能重复。");
            }
        }
        DataObjectCategory cate = new DataObjectCategory(cls);
        cate.name = name;
        
        selCate.cates.add(cate);
        cate.parent = selCate;
//        dataCateLists[index].add(cate);
        return cate;
    }

    //所有的怪物
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
     * 缺省缺省分支的资源版本号。
     * @return
     */
    public int getClientResVersion() {
        return getClientResVersion(branch);
    }
    
    /**
     * 取得指定分支的资源版本号。
     * @return
     */
    public int getClientResVersion(String useBranch) {
        ClientData clientData = branchClientData.get(useBranch == null ? "" : useBranch);
        return clientData.clientResVersion;
    }
    
    /**
     * 
     * 用于检查所有物品使用后，会获得哪些类型的物品
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
