package com.pip.game.data.item;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.extprop.ExtPropEntries;
import com.pip.game.data.i18n.I18NContext;
import com.pip.game.data.i18n.I18NUtils;
import com.pip.game.data.quest.pqe.ExpressionList;
import com.pip.util.Utils;

/**
 * ��Ʒ��������Ϣ
 * 
 * @author Joy Yan
 * 
 */
public class Item extends DataObject {
    /**
     * ��Ʒ������Ч
     */
    public static final int ATTRIBUTE_NONE = -1;
    /**
     * ����ȡֵΪ��
     */
    public static final int ATTRIBUTE_VALUE_NO = 0;
    /**
     * ����ȡֵΪ��
     */
    public static final int ATTRIBUTE_VALUE_YES = 1;
    
    /**
     * ��Ʒ����ʹ�� 
     */
    public static final int AVAILABLE_NO = 0;
    /**
     * ��Ʒֻ��ս���п�ʹ�� 
     */
    public static final int AVAILABLE_BATTLE = 1;
    /**
     * ��Ʒֻ�з�ս���п�ʹ�� 
     */
    public static final int AVAILABLE_UN_BATTLE = 2;    
    /**
     * ��Ʒ�κ�ʱ�򶼿�ʹ�� 
     */
    public static final int AVAILABLE_EVER = 3;//changed by JS

    /**
     * �Զ�ʹ�� 
     * Ĭ���Զ����õ���Ʒ�󣬽�����ϵͳĬ���Ƿ�ʹ�õ�ѡ����ʾ����ҿ���ѡ��ʹ�û���ʹ��;
     */
    public static final int AUTOUSE_DEFAULT = 0;
    /**
     * �Զ�ʹ��
     * �����Զ�������Դ���Ʒ����������ʾ�Ƿ�ʹ�õ�������ѡ�����֣�
     */
    public static final int AUTOUSE_DEFINE = 1;
    /**
     * �Զ�ʹ��
     * ��̨�Զ��������κ���ʾ���õ���Ʒ��ϵͳ�Զ�����ʹ��
     */
    public static final int AUTOUSE_BACKGROUND = 2;
    
    /**
     * ʹ�÷�Χ 
     * 0. ��Ŀ�� 
     */
    public static final int AREA_UN_DEFINE = 0;
    /**
     * ʹ�÷�Χ 
     * 1. �Լ� 
     */
    public static final int AREA_SELF = 1;
    /**
     * ʹ�÷�Χ 
     * 2. ��������
     */
    public static final int AREA_TEAM = 2;
    /**
     * ʹ�÷�Χ 
     * 3. �з�
     */
    public static final int AREA_ENEMY = 3;
    /**
     * ʹ�÷�Χ 
     * 4. ȫ��
     */
    public static final int AREA_ALL = 4;
    
    
    /**
     * ʱ������
     *  0:û��ʱЧ����
     */
    public static final int TIME_TYPE_UNDEFINE = 0;
    /**
     * ʱ������
     *  1:����ʱЧ
     */
    public static final int TIME_TYPE_ABSOLUTELY = 1;
    /**
     * ʱ������
     *  2:���ʱЧ
     */
    public static final int TIME_TYPE_RELATIVELY = 2;
    
    /**
     * ��ƷƷ��
     * 0.  ��ͨ���ף�
     */
    public static final int QUALITY_WHITE = 0;
    /**
     * ��ƷƷ��
     * 1.  �������̣�
     */
    public static final int QUALITY_GREEN = 1;
    /**
     * ��ƷƷ��
     * 2.  ����������
     */
    public static final int QUALITY_BLUE = 2;
    /**
     * ��ƷƷ��
     * 3.  ʷʫ���ϣ�
     */
    public static final int QUALITY_PURPLE = 3;
    /**
     * ��ƷƷ��
     * 4.  ��˵���ȣ�
     */
    public static final int QUALITY_ORANGE = 4;
    /**
     * ��ƷƷ��
     * 5.  �������ƣ�
     */
    public static final int QUALITY_YELLOW = 5;
    
    /**
     * �Ƿ��
     * 0.  ����
     */
    public static final int BIND_NO = 0;
    /**
     * �Ƿ��
     * 1.  װ����
     */
    public static final int BIND_EQUIPMENT = 1;
    /**
     * �Ƿ��
     * 2.  ʰȡ��
     */
    public static final int BIND_PICK_UP = 2;

    /**
     * ������Ŀ��
     */
    public ProjectData owner;
    /**
     * ��Ʒ����ࡣ
     */
    public int mainType = 0;
    /**
     * ��Ʒ����
     */
    public int type = 0;
    /**
     * ��Ʒ�ѵ��������������0
     */
    public int addition = 1;
    /**
     * �Ƿ�ʵ�����ͣ������ʵ���Ļ����ѵ���������Ϊ1��
     */
    public boolean instance;
    /**
     * �Ƿ���������Ʒ
     */
    public boolean taskFlag;
    /**
     * ��Ʒ�Ƿ��ʹ�� 
     * 0. �񣺲���ֱ��ʹ�õ���Ʒ������Ʒ�����н�����ʾʹ�ù����� 
     * 1. ս���п��� 
     * 2. ��ս���п��� 
     * 3. �κ�ʱ�����
     */
    public int available;
    /**
     * ʹ������ְҵ��
     * 0 - ������
     * 1-n ��ProjectConfig.PLAYER_CLAZZ˳���ְҵID
     */
    public int useClazz = -1;
    /**
     * ʹ��ȷ���ַ������մ���ʾ����Ҫȷ�ϡ�
     */
    public String useConfirm = "";
    
    public String additionalPrompt = "";
    /**
     * ��ƷƷ��
     * 0.  ��ͨ���ף�
     * 1.  �������̣�
     * 2.  ����������
     * 3.  ʷʫ���ϣ�
     * 4.  ��˵���ȣ�
     * 5.  �������ƣ�
     */
    public int quality;
    /**
     * �Ƿ����ģ�
     */
    public boolean waste = true;
    /**
     * �Զ�ʹ�� 
     * -1. �������Զ�ʹ�� 
     *  0. Ĭ���Զ����õ���Ʒ�󣬽�����ϵͳĬ���Ƿ�ʹ�õ�ѡ����ʾ����ҿ���ѡ��ʹ�û���ʹ�ã� 
     *  1. �����Զ�������Դ���Ʒ����������ʾ�Ƿ�ʹ�õ�������ѡ�����֣�
     *  2. ��̨�Զ��������κ���ʾ���õ���Ʒ��ϵͳ�Զ�����ʹ��
     */
    public int autoUse;
    /**
     * ��Ʒ����
     */
    public int level = 1;
    /**
     * ʹ�÷�Χ 
     * 0. ��Ŀ�� 
     * 1. �Լ� 
     * 2. ��������
     * 3. �з�
     * 4. ȫ��
     */
    public int area;
    /**
     * ʹ�ø���Ʒ����ҵȼ�����
     *  -1��û������
     *  >0: ʵ�ʵȼ�
     */
    public int playerLevel = 1;
    /**
     * ʹ�ø���Ʒ����ҵȼ�����
     */
    public int playerMaxLevel = 100;
    /**
     * ��ȴ��
     */
    public int[] coldDownGroup = new int[0];
    /**
     * ��ȴʱ�䣨���룩
     */
    public int coldDownTime;
    /**
     * �غ�����ȴ��
     */
    public int[] roundColdDownGroup = new int[0];
    /**
     * �غ�����ȴ�غ�����
     */
    public int coldDownRound;
    /**
     * �غ�����ȴ�Ƿ�֧�ֿ�ս����
     */
    public boolean coldDownRoundCrossBattle = false;
    /**
     * ʱ������
     *  0:û��ʱЧ����
     *  1:����ʱЧ
     *  2:���ʱЧ
     */
    public int timeType;
    /**
     * ʹ��ʱЧ��
     * ��ʵʱЧ��Ϊ����ʱЧʱ����ŵ���ָ������ʱ�����Java��׼�õ���ȷ��������
     * ��ʵʱЧ��Ϊ���ʱЧʱ����¼һ��ʱ�䣨��λ���룩��
     */
    public int time;
    /**
     * ʩ��ʱ�䣬��λ�����룩��
     * ��Ʒʹ�ú�೤ʱ����������Ч�������ʱ��ͨ������������ʾ���ȣ�
     * ��;���԰���ȡ��ʹ�ã���Ʒ����ʧ��Ч�������֡�
     */
    public int schedule;
    /**
     * �۸�
     */
    public int price;
    /**
     * �ܷ񱻳���
     *  0����
     *  1����
     */
    public boolean sale = true;
    /**
     * �Ƿ��
     * 0.  ����
     * 1.  װ����
     * 2.  ʰȡ��
     */
    public int bind;
    /**
     * ͼ������
     */
    public int iconIndex;
    /**
     * ʹ�þ���
     */
    public int distance;
    /**
     * �Ƿ�������
     */
    public boolean canDelete = true;
    /**
     * �Ƿ����������ֿ�
     */
    public boolean movable = true;
    /**
     * ʹ�ô���
     */
    public int useCount;
    /**
     * ���ӵ������,0������
     */
    public int maxOwnCount;
    /**
     * ��������
     */
    public ExpressionList additionalCondition = ExpressionList.fromString("");
    /**
     * ��Ʒ��չ���ԡ�
     */
    public ExtPropEntries extPropEntries = new ExtPropEntries();
    /**
     * ʹ��Ч���б�
     */
    public List<ItemEffect> effects = new ArrayList<ItemEffect>();
        
    /**
     * ��ע��
     */
    public String remark = "";
    
    public Item(ProjectData owner) {
        this.owner = owner;
        DataCalc = owner.config.getProjectCalc(getClass());
    }

    public boolean depends(DataObject obj) {
        return false;
    }

    public DataObject duplicate() {
        Item itemCopy = new Item(owner);
        itemCopy.update(this);
        return itemCopy;
    }

    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
        description = elem.getAttributeValue("desc");
        remark = elem.getAttributeValue("remark");
        if(remark == null) {
            remark = "";
        }
        setCategoryName(elem.getAttributeValue("category"));
        if (getWholeCategoryName() == null) {
            setCategoryName("");
        }
        
        type = Byte.parseByte(elem.getAttributeValue("type"));
        mainType = owner.config.findItemType(type).category;
        addition = Integer.parseInt(elem.getAttributeValue("addition"));
        taskFlag = Boolean.parseBoolean(elem.getAttributeValue("taskflag"));
        bind = Integer.parseInt(elem.getAttributeValue("bind"));
        quality = Integer.parseInt(elem.getAttributeValue("quality"));

        try {
            iconIndex = Integer.parseInt(elem.getAttributeValue("iconIndex"));
        } catch (NumberFormatException e1) {
            iconIndex = 0;
        }
        sale = Boolean.parseBoolean(elem.getAttributeValue("sale"));
        if (sale) {            
            price = Integer.parseInt(elem.getAttributeValue("price"));
        }
        
        instance = Boolean.parseBoolean(elem.getAttributeValue("instance"));
        
        String str = elem.getAttributeValue("additionalCondition");
        if (str != null) {
            additionalCondition = ExpressionList.fromString(str);
        }
        //additionalCondition = elem.getAttributeValue("additionalCondition");

        try {
            level = Integer.parseInt(elem.getAttributeValue("level"));
        } catch (Exception e) {
            level = 1;
        }
        try {
            timeType = Integer.parseInt(elem.getAttributeValue("timetype"));
        } catch (Exception e) {
            timeType = TIME_TYPE_UNDEFINE;
        }
        if (timeType != TIME_TYPE_UNDEFINE) {
            try {
                time = Integer.parseInt(elem.getAttributeValue("time"));
            } catch (Exception e) {
                time = 0;
            }
        }
        try{
            maxOwnCount = Integer.parseInt(elem.getAttributeValue("maxOwnCount"));
        }catch(Exception e){
            
        }

        available = Integer.parseInt(elem.getAttributeValue("available"));
        if (available != AVAILABLE_NO) {
            try {
                useClazz = Integer.parseInt(elem.getAttributeValue("useclazz"));
            } catch (Exception e) {
                useClazz = -1;
            }
            useConfirm = elem.getAttributeValue("useconfirm");
            if (useConfirm == null) {
                useConfirm = "";
            }
            additionalPrompt = elem.getAttributeValue("additionalPrompt");
            if(additionalPrompt == null){
                additionalPrompt = "";
            }
            try{
                playerMaxLevel = Integer.parseInt(elem.getAttributeValue("playerMaxLevel"));
            }catch(Exception e){
                playerMaxLevel = 100;//ȱʡ�ȼ�����Ϊ100��
                
            }
            waste = Boolean.parseBoolean(elem.getAttributeValue("waste"));
            area = Integer.parseInt(elem.getAttributeValue("area"));
            coldDownGroup = Utils.stringToIntArray(elem.getAttributeValue("colddowngroup"), ',');
            coldDownTime = Integer.parseInt(elem.getAttributeValue("coldDownTime"));
            
            // �غ��Ƶ�CD
            try {
                roundColdDownGroup = Utils.stringToIntArray(elem.getAttributeValue("roundcolddowngroup"), ',');
                coldDownRound = Integer.parseInt(elem.getAttributeValue("colddownround"));
                coldDownRoundCrossBattle = "1".equals(elem.getAttributeValue("colddownroundcrossbattle"));
            } catch (Exception e) {
            }
            
            autoUse = Integer.parseInt(elem.getAttributeValue("autouse"));
            schedule = Integer.parseInt(elem.getAttributeValue("schedule"));
            playerLevel = Integer.parseInt(elem.getAttributeValue("playerlevel"));
            
            try {
                distance = Integer.parseInt(elem.getAttributeValue("distance"));
            } catch (NumberFormatException e1) {
                distance = 15*8; //Ĭ��ֵ15��
            }
            
            List children = elem.getChildren("effect");
            for (int i = 0; children != null && i < children.size(); i++) {
                ItemEffect e = new ItemEffect();
                e.load((Element) children.get(i));
                effects.add(e);
            }
        }
        extPropEntries.loadExtData(elem.getChild("extProps"));
        if(elem.getAttributeValue("movable") != null){
            movable = Boolean.parseBoolean(elem.getAttributeValue("movable"));
        }
        if(elem.getAttributeValue("canDelete") != null){
            canDelete = Boolean.parseBoolean(elem.getAttributeValue("canDelete"));
        }
        try{
            useCount = Integer.parseInt(elem.getAttributeValue("useCount"));
        }catch(Exception e){
            useCount = 0;
        }
    }

    public Element save() {
        Element ret = new Element("item");
        
        ret.addAttribute("additionalCondition", additionalCondition.toString());
        
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        ret.addAttribute("desc",description);
        ret.addAttribute("remark",remark);
        
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }
        
        ret.addAttribute("type",String.valueOf(type));
        ret.addAttribute("addition",String.valueOf(addition));
        ret.addAttribute("taskflag",String.valueOf(taskFlag));
        ret.addAttribute("iconIndex",String.valueOf(iconIndex));
        
        ret.addAttribute("bind",String.valueOf(bind));
        ret.addAttribute("quality",String.valueOf(quality));
        ret.addAttribute("instance",String.valueOf(instance));
        
        ret.addAttribute("sale",String.valueOf(sale));
        if (sale) {
            ret.addAttribute("price",String.valueOf(price));
        }
        ret.addAttribute("level",String.valueOf(level));
        ret.addAttribute("timetype",String.valueOf(timeType));
        if (timeType > ATTRIBUTE_NONE) {
            ret.addAttribute("time",String.valueOf(time));
        }
        
        ret.addAttribute("available",String.valueOf(available));
        if(available != AVAILABLE_NO){
            ret.addAttribute("useclazz", String.valueOf(useClazz));
            ret.addAttribute("useconfirm", useConfirm);
            ret.addAttribute("additionalPrompt",additionalPrompt);
            ret.addAttribute("playerMaxLevel",String.valueOf(playerMaxLevel));
            ret.addAttribute("waste",String.valueOf(waste));
            ret.addAttribute("area",String.valueOf(area));
            
            ret.addAttribute("colddowngroup",Utils.intArrayToString(coldDownGroup, ','));
            ret.addAttribute("coldDownTime",String.valueOf(coldDownTime));

            // �غ��Ƶ�CD
            ret.addAttribute("roundcolddowngroup", Utils.intArrayToString(roundColdDownGroup, ','));
            ret.addAttribute("colddownround", String.valueOf(coldDownRound));
            ret.addAttribute("colddownroundcrossbattle", coldDownRoundCrossBattle ? "1" : "0");
            
            ret.addAttribute("autouse",String.valueOf(autoUse));
            ret.addAttribute("schedule",String.valueOf(schedule));
            ret.addAttribute("playerlevel",String.valueOf(playerLevel));
            
            ret.addAttribute("distance",String.valueOf(distance));
            
            for (int i = 0; i < effects.size(); i++) {
                Element child = effects.get(i).save();
                if(child != null){
                    ret.getMixedContent().add(child);
                }
            }
        }
        ret.addAttribute("canDelete",String.valueOf(canDelete));
        ret.addAttribute("movable",String.valueOf(movable));
        ret.addAttribute("useCount", String.valueOf(useCount));
        if (maxOwnCount != 0) {
            ret.addAttribute("maxOwnCount",String.valueOf(maxOwnCount));
        }
        Element el = new Element("extProps");
        extPropEntries.saveToDom(el);
        ret.addContent(el);
        return ret;
        
    }

    public void update(DataObject obj) {
        Item itemCopy = (Item) obj;
        id = itemCopy.id;
        addition = itemCopy.addition;
        instance = itemCopy.instance;
        area = itemCopy.area;
        autoUse = itemCopy.autoUse;
        waste = itemCopy.waste;
        available = itemCopy.available;
        useClazz = itemCopy.useClazz;
        useConfirm = itemCopy.useConfirm;
        additionalPrompt = itemCopy.additionalPrompt;
        playerMaxLevel = itemCopy.playerMaxLevel;
        quality = itemCopy.quality;
        bind = itemCopy.bind;
        description = itemCopy.description;
        remark = itemCopy.remark;
        level = itemCopy.level;
        playerLevel = itemCopy.playerLevel;
        coldDownTime = itemCopy.coldDownTime;
        coldDownGroup = itemCopy.coldDownGroup;
        roundColdDownGroup = itemCopy.roundColdDownGroup;
        coldDownRound = itemCopy.coldDownRound;
        coldDownRoundCrossBattle = itemCopy.coldDownRoundCrossBattle;
        price = itemCopy.price;
        sale = itemCopy.sale;
        taskFlag = itemCopy.taskFlag;
        iconIndex = itemCopy.iconIndex;
        time = itemCopy.time;
        distance = itemCopy.distance;
        schedule = itemCopy.schedule;
        title = itemCopy.title;
        timeType = itemCopy.timeType;
        type = itemCopy.type;
        mainType = itemCopy.mainType;
        extPropEntries.copyFrom(itemCopy.extPropEntries);
        setCategoryName(itemCopy.getCategoryName());
        additionalCondition = itemCopy.additionalCondition;
        canDelete = itemCopy.canDelete;
        movable = itemCopy.movable;
        useCount = itemCopy.useCount;
        maxOwnCount = itemCopy.maxOwnCount;
        
        effects.clear();
        for (int i = 0; i < itemCopy.effects.size(); i++) {
            effects.add((ItemEffect)itemCopy.effects.get(i).clone());
        }
    }

    @Override
    public boolean changed(DataObject obj) {
        return changed(this, obj);
    }
    
    public String toString() {
        return id + ":" + title;
    }

    public boolean equals(Object obj){
        if(obj instanceof Item){
            return ((Item)obj).id == id;
        }
        return false;
    }

    /**
     * �������������Խ��й��ʻ������������Ҫ���ʻ����ַ���������ȡ������context�в��ҷ�������
     * @param context
     * @return �����ĳ�����Ա��滻������true�����򷵻�false��
     */
    public boolean i18n(I18NContext context) {
        boolean changed = false;
        String tmp = context.input(title, "Item");
        if (tmp != null) {
            title = tmp;
            changed = true;
        }
        tmp = context.input(useConfirm, "Item");
        if (tmp != null) {
            useConfirm = tmp;
            changed = true;
        }
        tmp = context.input(additionalPrompt, "Item");
        if (tmp != null) {
            additionalPrompt = tmp;
            changed = true;
        }
        tmp = context.input(remark, "Item");
        if (tmp != null) {
            remark = tmp;
            changed = true;
        }
        tmp = context.input(description, "Item");
        if (tmp != null) {
            description = tmp;
            changed = true;
        }
        if (I18NUtils.processExpressionList(additionalCondition, context, 0, "Item")) {
            changed = true;
        }
        return changed;
    }
}
