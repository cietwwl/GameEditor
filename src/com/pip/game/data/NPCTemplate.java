package com.pip.game.data;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.DataConversionException;
import org.jdom.Element;

import com.pip.game.data.AI.AIData;
import com.pip.game.data.AI.AIRule;
import com.pip.game.data.AI.AIRuleConfig;
import com.pip.game.data.i18n.I18NContext;
import com.pip.game.data.item.DropNode;
import com.pip.game.data.quest.QuestTrigger;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.ExpressionList;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.data.quest.pqe.PQEUtils.SystemVar;

/**
 * 一个NPC的描述信息。NPC可能是一个友方目标，也可能是一只怪物。
 */
public class NPCTemplate extends DataObject {
    /** 所属项目。*/
    public ProjectData owner;
    /** NPC精灵。*/
    public Sprite image;
    /** NPC类型。*/
    public NPCType type;
    /** NPC职业 */
    public int clazz;
    /** 难度 */
    public int difficulty;
    /** 级别 */
    public int level;
    /** AI规则 */
    public int aiDataID = 1;
    /** NPC关联的任务ID列表，逗号分隔 */
    public String questIDs = "";
    /** 掉落组 */
    public List<DropNode> dropGroups = new ArrayList<DropNode>();
    /**
     * 是否改变地图的通过性
     */
    public boolean changePath = false;

    // 下面几个变量只是在这里定义，但实现由产品实现
    /** 生命 */
    public int hp;
    /** 法力 */
    public int mp;
    /** 怪物掉落经验值 */
    public int exp;
    /** 怪物掉落金钱 */
    public int money;
    /** 视野 */
    public int eyeshot;
    /** 追击范围 */
    public int chaseDistance;
    /** 怪物巡逻范围 */
    public int patrol;
    /** 追击速度 */
    public int speed;
    /** 巡逻速度 */
    public int walkSpeed;
    
    public String aiImplClass;
    //材质索引
    public int materialNameIndex;
    
    public String materialName;
    /**
     * 是否随机刷新，只针对采集npc有效
     */
    public boolean isRandomRefresh =false;
    
    public NPCTemplate(ProjectData owner) {
        this.owner = owner;
    }

    public int getID() {
        return id;
    }

    public String toString() {
        return id + ": " + title;
    }

    public boolean equals(Object o) {
        return this == o;
    }

    public void update(DataObject obj) {
        NPCTemplate oo = (NPCTemplate)obj;
        id = oo.id;
        image = oo.image;
        title = oo.title;
        description = oo.description;
        setCategoryName(oo.getCategoryName());
        
        type = oo.type;
        clazz = oo.clazz;
        level = oo.level;
        difficulty = oo.difficulty;
        materialNameIndex = oo.materialNameIndex;
        materialName = oo.materialName;
        aiDataID = oo.aiDataID;
        questIDs = oo.questIDs;
        isRandomRefresh = oo.isRandomRefresh;
        
        dropGroups.clear();
        for(DropNode dropNode : oo.dropGroups){
            dropGroups.add(dropNode);
        }
        
        if (owner != oo.owner) {
            if (image != null) {
                image = (Animation)owner.findObject(Animation.class, image.id);
            }
            if (type != null) {
                type = (NPCType)owner.findDictObject(NPCType.class, type.id);
            }
        }
        changePath = oo.changePath;
       
    }
    
    public DataObject duplicate() {
        NPCTemplate ret = new NPCTemplate(owner);
        ret.update(this);
        return ret;
    }

    @Override
    public boolean changed(DataObject obj) {
        return changed(this, obj);
    }
    
    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
        description = elem.getAttributeValue("description");
        if (description == null) {
            description = "";
        }
        setCategoryName(elem.getAttributeValue("category"));
        if (getWholeCategoryName() == null) {
            setCategoryName("");
        }
        
        int typeID = Integer.parseInt(elem.getAttributeValue("type"));
        type = (NPCType)owner.findDictObject(NPCType.class, typeID);
        int imageId = Integer.parseInt(elem.getAttributeValue("image"));
        if(imageId < 0){
        }else{
            int type = imageId / Sprite.MAX_SPRITE_ID;
            int _imageId = imageId % Sprite.MAX_SPRITE_ID;
            if(type == Sprite.ANIMATION){
                image = (Animation)owner.findObject(Animation.class, _imageId);
            }else if(type == Sprite.MESH){
                image = (GameMesh)owner.findObject(GameMesh.class, _imageId);
            }
        }
        try {
            clazz = Integer.parseInt(elem.getAttributeValue("clazz"));
        } catch (Exception e) {
        }
        try {
            difficulty = Integer.parseInt(elem.getAttributeValue("difficulty"));
            materialNameIndex = Integer.parseInt(elem.getAttributeValue("materialNameIndex"));
            materialName = elem.getAttributeValue("materialName");
            if(materialName == null){
                materialName = "";
            }
        } catch (Exception e) {
        }
        level = Integer.parseInt(elem.getAttributeValue("level"));
        if (elem.getAttribute("changepath") != null) {
            changePath = elem.getAttributeValue("changepath").equals("true");
        }
        if (elem.getAttribute("aiDataID") != null) {
            aiDataID =  Integer.parseInt(elem.getAttributeValue("aiDataID"));
        }
        if (elem.getAttribute("isRandomRefresh") != null) {
            try {
                isRandomRefresh = elem.getAttribute("isRandomRefresh").getBooleanValue();
            } catch (DataConversionException e) {
//                e.printStackTrace();
            }
        }
        
        
        questIDs = elem.getAttributeValue("questId");
        if (questIDs == null) {
            questIDs = "";
        }
           
        List<Element> children = elem.getChildren("dropnode");
        for (Element child : children) {
            DropNode node = new DropNode();
            node.load(child);
            dropGroups.add(node);
        }
    }
 
    public Element save() {
        Element ret = new Element("npc");
        ret.addAttribute("id", String.valueOf(id));
        if(image != null){
            int _imageId = image.id + Sprite.MAX_SPRITE_ID * image.getType();
            ret.addAttribute("image", String.valueOf(_imageId));
        }else{
            ret.addAttribute("image", "-1");
        }
        ret.addAttribute("title", title);
        ret.addAttribute("description",description);
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }
        
        if (type == null) {
            ret.addAttribute("type", "-1");
        } else {
            ret.addAttribute("type", String.valueOf(type.id));
        }
        ret.addAttribute("questId", questIDs);
        ret.addAttribute("clazz", String.valueOf(clazz));
        ret.addAttribute("difficulty", String.valueOf(difficulty));
        ret.addAttribute("materialNameIndex", String.valueOf(materialNameIndex));
        ret.addAttribute("materialName", materialName==null?"":materialName);
        ret.addAttribute("level", String.valueOf(level));
        ret.addAttribute("changepath", String.valueOf(changePath));
        ret.addAttribute("aiDataID", String.valueOf(aiDataID));
        
        ret.addAttribute("isRandomRefresh", String.valueOf(isRandomRefresh));
        
        if (dropGroups != null && dropGroups.size() > 0) {
            for (DropNode node : dropGroups) {
                ret.getMixedContent().add(node.save());
            }
        }
        return ret;
    }

    
    public boolean depends(DataObject obj) {
        return obj == image;
    }
    
    /**
     * 根据模板ID查找一个模板并取得模板名称。
     * @param project
     * @param templateID
     * @return
     */
    public static String toString(ProjectData project, int templateID) {
        NPCTemplate t = (NPCTemplate)project.findObject(NPCTemplate.class, templateID);
        if (t == null) {
            return "无";
        } else {
            return t.toString();
        }
    }
    
    public int getMoney() {
        return money;
    }
    
    public int getExp() {
        return exp;
    }
    
    /**
     * 对这个对象的属性进行国际化处理，如果有需要国际化的字符串，则提取出来到context中查找翻译结果。
     * @param context
     * @return 如果有某个属性被替换，返回true，否则返回false。
     */
    public boolean i18n(I18NContext context) {
        return false;
    }

}
