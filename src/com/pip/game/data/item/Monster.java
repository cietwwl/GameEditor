package com.pip.game.data.item;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.MonsterGroup;
import com.pip.game.data.NPCTemplate;
import com.pip.game.data.i18n.I18NContext;

public class Monster extends DataObject{
    public int monsterId;
    public int rate;
    
    public int level;
    public boolean isPlayerSide; //是否属于玩家一方的怪
    
    public NPCTemplate npcTemplate;
    
    //战斗中的站位
    //怪物以左方为例，1,2,3,4,5表示左面第一排站位，6,7,8,9,10表示左面第二排站位
    //0表示默认值，使用的是默认站位
    public int position;
    
    public Monster(){
    }
    
    public void load(Element elem){
        this.monsterId = Integer.parseInt(elem.getAttributeValue("id"));
        this.rate = Integer.parseInt(elem.getAttributeValue("rate"));
//        this.title = elem.getAttributeValue("title");
//        if(this.title == null){
//            this.title = "";
//        }
        //modified by tzhang
        //NPCTemplate npc = MonsterGroup.monsters.get(new Integer(monsterId));
        NPCTemplate npc = MonsterGroup.monsters.get(new Integer(monsterId));
        this.level = npc.level;
        this.title = npc.title;

        String npcId = elem.getAttributeValue("npcTemplate");
        if(npcId != null) {
            npcTemplate = MonsterGroup.monsters.get(Integer.parseInt(npcId));
        }
        
        String strPlayerSide = elem.getAttributeValue("isPlayerSide");
        if("1".equals(strPlayerSide)) {
            isPlayerSide = true;
        } else {
            isPlayerSide = false;
        }
        
        String strPosition = elem.getAttributeValue("position");
        if(strPosition != null) {
            try{
                position = Integer.parseInt(strPosition);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        
    }

    @Override
    public boolean changed(DataObject obj) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean depends(DataObject obj) {
        if(obj instanceof NPCTemplate) {
            if(this.monsterId == ((NPCTemplate)obj).id) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public Monster duplicate() {
        Monster copy = new Monster();
        copy.update(this);
        return copy;
    }

    public void update(DataObject obj) {
        Monster copy = (Monster)obj;
        id = copy.id;
        monsterId = copy.monsterId;
        rate = copy.rate;
//        title = copy.title;     
        level = copy.level;
        npcTemplate = copy.npcTemplate;
        isPlayerSide = copy.isPlayerSide;
        position = copy.position;
    }

    @Override
    public Element save() {
        Element e = new Element("monster");
        e.addAttribute("id",String.valueOf(monsterId));
//        e.addAttribute("title", title == null ? "" : title);
        e.addAttribute("rate",String.valueOf(rate));
        e.addAttribute("npcTemplate", String.valueOf(npcTemplate.id));
        e.addAttribute("isPlayerSide", isPlayerSide ? "1" : "0");
        e.addAttribute("position", String.valueOf(position));
        return e;
    }
    
    /**
     * 对这个对象的属性进行国际化处理，如果有需要国际化的字符串，则提取出来到context中查找翻译结果。
     * @param context
     * @return 如果有某个属性被替换，返回true，否则返回false。
     */
    public boolean i18n(I18NContext context) {
        String tmp = context.input(title, "Monster");
        if (tmp != null) {
            title = tmp;
            return true;
        } else {
            return false;
        }
    }
}