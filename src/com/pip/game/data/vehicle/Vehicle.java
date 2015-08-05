package com.pip.game.data.vehicle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Text;
import org.jdom.Element;

import com.pip.game.data.Animation;
import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.i18n.I18NContext;
import com.pip.game.data.skill.SkillConfig;

public class Vehicle extends DataObject {
    public ProjectData owner;
    /**骑乘类型站立*/
    public static int RIDE_TYPE_STAND = 0;
    /**骑乘类型跨坐*/
    public static int RIDE_TYPE_SIT = 1;

    public static int PERSON_NUM1 = 1;
    
    public static int PERSON_NUM2 = 2;
    
    /**
     * 移动路径的设置类
     */
    public static class MovePath extends DataObject {
        public int timePeriodID;
        public int percent;
        public int moveSpeed;

        public MovePath() {

        }

        @Override
        public boolean changed(DataObject obj) {
            // TODO Auto-generated method stub
            return changed(this, obj);
        }

        @Override
        public boolean depends(DataObject obj) {
            return false;
        }

        @Override
        public DataObject duplicate() {
            // TODO Auto-generated method stub
            MovePath ret = new MovePath();
            ret.update(this);
            return ret;
        }

        @Override
        public void load(Element elem) {
            // TODO Auto-generated method stub
            timePeriodID = Integer.parseInt(elem
                    .getAttributeValue("timePeriod"));
            percent = Integer.parseInt(elem.getAttributeValue("percent"));
            moveSpeed = Integer.parseInt(elem.getAttributeValue("moveSpeed"));

        }

        @Override
        public Element save() {
            // TODO Auto-generated method stub
            Element ret = new Element("patrolpath");
            ret.addAttribute("timePeriod", String.valueOf(timePeriodID));
            ret.addAttribute("percent", String.valueOf(percent));
            ret.addAttribute("moveSpeed", String.valueOf(moveSpeed));
            return ret;
        }

        @Override
        public void update(DataObject obj) {
            // TODO Auto-generated method stub
            MovePath oo = (MovePath) obj;
            timePeriodID = oo.timePeriodID;
            percent = oo.percent;
            moveSpeed = oo.moveSpeed;

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

    /**
     * 基本属性
     */
    public String description = "";
    public int level;
    public int type;
    public Animation image;
    /**
     * 扩展属性
     */
    /**生命*/
    public int hp = 20000;
    /**法力*/
    public int mp = 10000;
    /**乘坐人数*/
    public int personNum;
    /**巡逻类型*/
    public int patrolType;
    /**巡逻速度*/
    public int patrolSpeed;
    /**骑乘类型*/
    public int rideType;
    /**素体动画编号*/
    public int epIndex;
    /**物攻*/
    public int phyAttack;
    /**法攻*/
    public int magicAttack;
    /**物减*/
    public int phyReduceRate = 10;
    /**法减*/
    public int magicReduceRate = 10;
    /**物命*/
    public int phyHitRate = 95;
    /**法命*/
    public int magicHitRate = 95;
    /**物暴*/
    public int phyCritRate = 5;
    /**法暴*/
    public int magicCritRate = 5;
    /**物免*/
    public int phyDeCritRate;
    /**法免*/
    public int magicDeCritRate;
    /**物闪*/
    public int phyDodgeRate = 5;
    /**法闪*/
    public int magicDodgeRate = 5;
    /**5秒回血*/
    public int renewHP = 50;
    /**5秒回蓝*/
    public int renewMP = 25;
    /**物暴伤害*/
    public int phyCritDamageRate = 150;
    /**法暴伤害*/
    public int magicCritDamageRate = 150;
    /**
     * 所在地图层 0地面层1天空层
     */
    public int flat ;
    /**
     * 是否可下载具 0否1是
     */
    public int down;
    

    /**
     * 技能组
     */
    public List<SkillConfig> mainSkill_1 = new ArrayList<SkillConfig>();
    public List<SkillConfig> mainSkill_2 = new ArrayList<SkillConfig>();
    public List<SkillConfig> mainSkill_3 = new ArrayList<SkillConfig>();
    public List<SkillConfig> assistSkill_1 = new ArrayList<SkillConfig>();
    public List<SkillConfig> assistSkill_2 = new ArrayList<SkillConfig>();
    public List<SkillConfig> assistSkill_3 = new ArrayList<SkillConfig>();

    public String mskill1 = "-1";
    public String mskill2 = "-1";
    public String mskill3 = "-1";
    public String askill1 = "-1";
    public String askill2 = "-1";
    public String askill3 = "-1";

    /**
     * 巡逻路径
     */

    public List<MovePath> movePath = new ArrayList<MovePath>();

    public Vehicle(ProjectData owner) {
        this.owner = owner;
    }

    @Override
    public boolean changed(DataObject obj) {
        return changed(this, obj);
    }

    @Override
    public boolean depends(DataObject obj) {
        // TODO Auto-generated method stub
        return obj == image;
    }

    @Override
    public DataObject duplicate() {
        // TODO Auto-generated method stub
        Vehicle copy = new Vehicle(owner);
        copy.update(this);
        return copy;
    }

    @Override
    public void load(Element elem) {
        // TODO Auto-generated method stub
        id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
        description = elem.getAttributeValue("description");
        setCategoryName(elem.getAttributeValue("category"));
        if (getWholeCategoryName() == null) {
            setCategoryName("");
        }
        type = Integer.parseInt(elem.getAttributeValue("type"));
        level = Integer.parseInt(elem.getAttributeValue("level"));
        image = (Animation) owner.findObject(Animation.class, Integer
                .parseInt(elem.getAttributeValue("image")));

        Element extprop = elem.getChild("extprop");
        hp = Integer.parseInt(extprop.getAttributeValue("HP"));
        mp = Integer.parseInt(extprop.getAttributeValue("MP"));

        try{
            rideType = Integer.parseInt(extprop.getAttributeValue("rideType"));
            epIndex = Integer.parseInt(extprop.getAttributeValue("epIndex"));
            phyAttack = Integer.parseInt(extprop.getAttributeValue("phyAttack"));
            
            
            magicAttack = Integer.parseInt(extprop.getAttributeValue("magicAttack"));
            phyReduceRate = Integer.parseInt(extprop.getAttributeValue("phyReduceRate"));
            magicReduceRate = Integer.parseInt(extprop.getAttributeValue("magicReduceRate"));
            phyHitRate = Integer.parseInt(extprop.getAttributeValue("phyHitRate"));
            magicHitRate = Integer.parseInt(extprop.getAttributeValue("magicHitRate"));
            phyDeCritRate = Integer.parseInt(extprop.getAttributeValue("phyDeCritRate"));
            magicDeCritRate = Integer.parseInt(extprop.getAttributeValue("magicDeCritRate"));
            phyDodgeRate = Integer.parseInt(extprop.getAttributeValue("phyDodgeRate"));
            magicDodgeRate = Integer.parseInt(extprop.getAttributeValue("magicDodgeRate"));
            renewHP = Integer.parseInt(extprop.getAttributeValue("renewHP"));
            renewMP = Integer.parseInt(extprop.getAttributeValue("renewMP"));
            phyCritDamageRate = Integer.parseInt(extprop.getAttributeValue("phyCritDamageRate"));
            magicCritDamageRate = Integer.parseInt(extprop.getAttributeValue("magicCritDamageRate"));
            flat = Integer.parseInt(extprop.getAttributeValue("flat"));
            down = Integer.parseInt(extprop.getAttributeValue("down"));
        }catch (Exception e){
            System.out.println();
        }
        patrolSpeed = Integer
                .parseInt(extprop.getAttributeValue("patrolSpeed"));
        personNum = Integer.parseInt(extprop.getAttributeValue("personNum"));

        Element skills = elem.getChild("skills");
        mainSkill_1 = string2skills(skills.getAttributeValue("mskill1"));
        mainSkill_2 = string2skills(skills.getAttributeValue("mskill2"));
        mainSkill_3 = string2skills(skills.getAttributeValue("mskill3"));
        if (type == 1) {
            assistSkill_1 = string2skills(skills.getAttributeValue("askill1"));
            assistSkill_2 = string2skills(skills.getAttributeValue("askill2"));
            assistSkill_3 = string2skills(skills.getAttributeValue("askill3"));
        }
        Element moves = elem.getChild("moves");
        patrolType = Integer.parseInt(moves.getAttributeValue("patrolType"));
        if (patrolType == 2) {
            try{
            patrolSpeed = Integer.parseInt(moves.getAttributeValue("patrolSpeed"));
            }catch(Exception e){
                
            }
            List<Element> children = elem.getChildren("movepath");
            for (Element child : children) {
                MovePath node = new MovePath();
                node.load(child);
                movePath.add(node);
            }
        }
        if (patrolType == 1) {
            patrolSpeed = Integer.parseInt(moves.getAttributeValue("patrolSpeed"));
        }
        if (patrolType == 0) {
            patrolSpeed = 0;
        }
    }

    @Override
    public Element save() {
        // TODO Auto-generated method stub
        Element ret = new Element("vehicle");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        ret.addAttribute("description", description);
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }
        ret.addAttribute("type", String.valueOf(type));
        ret.addAttribute("level", String.valueOf(level));
        if (image == null) {
            ret.addAttribute("image", "-1");
        } else {
            ret.addAttribute("image", String.valueOf(image.id));
        }

        Element extprop = new Element("extprop");
        ret.addContent(extprop);
        extprop.addAttribute("HP", String.valueOf(hp));
        extprop.addAttribute("MP", String.valueOf(mp));
        extprop.addAttribute("patrolSpeed", String.valueOf(patrolSpeed));
        extprop.addAttribute("rideType",String.valueOf(rideType));
        extprop.addAttribute("personNum", String.valueOf(personNum));
        extprop.addAttribute("epIndex",String.valueOf(epIndex));
        extprop.addAttribute("phyAttack",String.valueOf(phyAttack));
        extprop.addAttribute("magicAttack",String.valueOf(magicAttack));
        extprop.addAttribute("phyReduceRate",String.valueOf(phyReduceRate));
        extprop.addAttribute("magicReduceRate",String.valueOf(magicReduceRate));
        extprop.addAttribute("phyHitRate",String.valueOf(phyHitRate));
        extprop.addAttribute("magicHitRate",String.valueOf(magicHitRate));
        extprop.addAttribute("phyCritRate",String.valueOf(phyCritRate));
        extprop.addAttribute("magicCritRate",String.valueOf(magicCritRate));
        extprop.addAttribute("phyDeCritRate",String.valueOf(phyDeCritRate));
        extprop.addAttribute("magicDeCritRate",String.valueOf(magicDeCritRate));
        extprop.addAttribute("phyDodgeRate",String.valueOf(phyDodgeRate));
        extprop.addAttribute("magicDodgeRate",String.valueOf(magicDodgeRate));
        extprop.addAttribute("renewHP",String.valueOf(renewHP));
        extprop.addAttribute("renewMP",String.valueOf(renewMP));
        extprop.addAttribute("phyCritDamageRate",String.valueOf(phyCritDamageRate));
        extprop.addAttribute("magicCritDamageRate",String.valueOf(magicCritDamageRate));
        extprop.addAttribute("flat",String.valueOf(flat));
        extprop.addAttribute("down",String.valueOf(down));

        Element skills = new Element("skills");
        ret.addContent(skills);
        mskill1 = skills2string(mainSkill_1);
        mskill2 = skills2string(mainSkill_2);
        mskill3 = skills2string(mainSkill_3);
        skills.addAttribute("mskill1", mskill1);
        skills.addAttribute("mskill2", mskill2);
        skills.addAttribute("mskill3", mskill3);
        if (type == 1) {
            askill1 = skills2string(assistSkill_1);
            askill2 = skills2string(assistSkill_2);
            askill3 = skills2string(assistSkill_3);
            skills.addAttribute("askill1", askill1);
            skills.addAttribute("askill2", askill2);
            skills.addAttribute("askill3", askill3);
        }
        Element moves = new Element("moves");
        ret.addContent(moves);
        moves.addAttribute("patrolType", String.valueOf(patrolType));
        if (patrolType == 2) {
            moves.addAttribute("patrolSpeed",String.valueOf(patrolSpeed));
            if (movePath != null && movePath.size() > 0) {
                for (MovePath mp : movePath) {
                    ret.getMixedContent().add(mp.save());
                }
            } 
        }else if(patrolType == 1){
            moves.addAttribute("patrolSpeed",String.valueOf(patrolSpeed));
        }

        return ret;
    }

    @Override
    public void update(DataObject obj) {
        // TODO Auto-generated method stub
        Vehicle oo = (Vehicle) obj;
        id = oo.id;
        title = oo.title;
        level = oo.level;
        description = oo.description;
        type = oo.type;
        image = oo.image;
        hp = oo.hp;
        mp = oo.mp;
        rideType = oo.rideType;
        patrolSpeed = oo.patrolSpeed;
        personNum = oo.personNum;
        epIndex = oo.epIndex;
        phyAttack = oo.phyAttack;
        magicAttack = oo.magicAttack;
        phyReduceRate = oo.phyReduceRate;
        magicReduceRate =oo.magicReduceRate;
        phyHitRate = oo.phyHitRate;
        magicHitRate =oo.magicHitRate;
        phyCritRate = oo.phyCritRate;
        magicCritRate = oo.magicCritRate;
        phyDeCritRate = oo.phyDeCritRate;
        magicDeCritRate =oo.magicDeCritRate;
        phyDodgeRate = oo.phyDodgeRate;
        magicDodgeRate =oo.magicDodgeRate;
        renewHP = oo.renewHP;
        renewMP = oo.renewMP;
        phyCritDamageRate = oo.phyCritDamageRate;
        magicCritDamageRate = oo.magicCritDamageRate;
        flat = oo.flat;
        down = oo.down;

        mainSkill_1 = oo.mainSkill_1;
        mainSkill_2 = oo.mainSkill_2;
        mainSkill_3 = oo.mainSkill_3;
        assistSkill_1 = oo.assistSkill_1;
        assistSkill_2 = oo.assistSkill_2;
        assistSkill_3 = oo.assistSkill_3;
        patrolType = oo.patrolType;
        patrolSpeed = oo.patrolSpeed;
        movePath.clear();
        for (MovePath mp : oo.movePath) {
            movePath.add(mp);
        }

    }

    public String toString() {
        return id + ":" + title;
    }

    public static String toString(ProjectData project, int id) {
        Vehicle ret = (Vehicle) project.findObject(Vehicle.class, id);
        if (ret == null) {
            return "无效载具";
        }
        return ret.toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Vehicle) {
            return ((Vehicle) obj).id == id;
        }
        return false;
    }

    private String skills2string(List<SkillConfig> skills) {
        StringBuffer stringBuffer = new StringBuffer();
        if (skills == null) {
            stringBuffer.append("");
        } else {
            for (SkillConfig skill : skills) {
                if (stringBuffer.length() == 0) {
                    if (skill == null) {
                        stringBuffer.append("");
                    } else
                        stringBuffer.append(skill.id + "," + skill.currLevel);
                } else
                    stringBuffer.append(";" + skill.id + "," + skill.currLevel);
            }
        }
        return stringBuffer.toString();
    }

    private List<SkillConfig> string2skills(String string) {
        List<SkillConfig> skills = new ArrayList<SkillConfig>();
        if (string != "") {
            String[] stringid = string.split(";");
            for (int i = 0; i < stringid.length; i++) {
                String[] stringlevel = stringid[i].split(",");
                SkillConfig skill = (SkillConfig) owner.findObject(SkillConfig.class, Integer.parseInt(stringlevel[0]));
                SkillConfig vehicleSkill = (SkillConfig) skill.duplicate();
                vehicleSkill.currLevel = Integer.parseInt(stringlevel[1]);
                skills.add(vehicleSkill);
            }
            return skills;
        } else
            return null;
    }


    /**
     * 对这个对象的属性进行国际化处理，如果有需要国际化的字符串，则提取出来到context中查找翻译结果。
     * @param context
     * @return 如果有某个属性被替换，返回true，否则返回false。
     */
    public boolean i18n(I18NContext context) {
        boolean changed = false;
        String tmp = context.input(title, "Vehicle");
        if (tmp != null) {
            title = tmp;
            changed = true;
        }
        tmp = context.input(description, "Vehicle");
        if (tmp != null) {
            description = tmp;
            changed = true;
        }
        return changed;
    }
}
