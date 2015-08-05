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
 * һ��NPC��������Ϣ��NPC������һ���ѷ�Ŀ�꣬Ҳ������һֻ���
 */
public class NPCTemplate extends DataObject {
    /** ������Ŀ��*/
    public ProjectData owner;
    /** NPC���顣*/
    public Sprite image;
    /** NPC���͡�*/
    public NPCType type;
    /** NPCְҵ */
    public int clazz;
    /** �Ѷ� */
    public int difficulty;
    /** ���� */
    public int level;
    /** AI���� */
    public int aiDataID = 1;
    /** NPC����������ID�б����ŷָ� */
    public String questIDs = "";
    /** ������ */
    public List<DropNode> dropGroups = new ArrayList<DropNode>();
    /**
     * �Ƿ�ı��ͼ��ͨ����
     */
    public boolean changePath = false;

    // ���漸������ֻ�������ﶨ�壬��ʵ���ɲ�Ʒʵ��
    /** ���� */
    public int hp;
    /** ���� */
    public int mp;
    /** ������侭��ֵ */
    public int exp;
    /** ��������Ǯ */
    public int money;
    /** ��Ұ */
    public int eyeshot;
    /** ׷����Χ */
    public int chaseDistance;
    /** ����Ѳ�߷�Χ */
    public int patrol;
    /** ׷���ٶ� */
    public int speed;
    /** Ѳ���ٶ� */
    public int walkSpeed;
    
    public String aiImplClass;
    //��������
    public int materialNameIndex;
    
    public String materialName;
    /**
     * �Ƿ����ˢ�£�ֻ��Բɼ�npc��Ч
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
     * ����ģ��ID����һ��ģ�岢ȡ��ģ�����ơ�
     * @param project
     * @param templateID
     * @return
     */
    public static String toString(ProjectData project, int templateID) {
        NPCTemplate t = (NPCTemplate)project.findObject(NPCTemplate.class, templateID);
        if (t == null) {
            return "��";
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
     * �������������Խ��й��ʻ������������Ҫ���ʻ����ַ���������ȡ������context�в��ҷ�������
     * @param context
     * @return �����ĳ�����Ա��滻������true�����򷵻�false��
     */
    public boolean i18n(I18NContext context) {
        return false;
    }

}
