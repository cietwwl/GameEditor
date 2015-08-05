package com.pip.game.data.map;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

import com.pip.game.data.Faction;
import com.pip.game.data.GameArea;
import com.pip.game.data.GameAreaInfo;
import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.data.quest.pqe.ExpressionList;
import com.pip.game.data.vehicle.Vehicle;
import com.pip.game.data.vehicle.XyGameMapVehicle;
import com.pip.game.editor.quest.GameAreaCache;
import com.pipimage.utils.Utils;

/**
 * 一个关卡中一个地图的附加信息，包括名称、NPC和出口等信息。这些信息被保存在关卡目录中的info.xml文件里。
 * 
 * @author lighthu
 */
public class GameMapInfo {
    /** 所属关卡 */
    public GameArea owner;
    /** 关卡内地图ID */
    public int id;
    /** 地图名称 */
    public String name;

    /** 是否中立（不允许攻击对方阵营玩家） */
    public boolean neutral = false;
    /** 是否允许己方PK */
    public boolean allowDuel = true;
    /** 是否启动被杀保护: 0 - 不保护、1 - 1次保护、2 - 2次保护 */
    public int protect = 0;
    /** 是否允许跟随 */
    public boolean allowFollow = true;
    /** 是否隔离阵营 */
    public boolean splitFaction = false;
    /** 声音文件ID */
    public int backgroundMusic;
    /** 地图中的对象 */
    public List<GameMapObject> objects = new ArrayList<GameMapObject>();
    /** 此地图的路径查找工具（仅服务器模式有效） */
    protected PathFinder pathFinder;
    /** 阵营 */
    public Faction faction;

    /** 是否允许交易 */
    public boolean allowExchange;

    /** 是否允许组队 */
    public boolean allowTeam;

    /** 是否出现组队邀请提示确认 */
    public boolean hasNoteTeam;

    /** 是否允许入会邀请 */
    public boolean allowJoinPartyInvite;

    /** 设定场景为副本 */
    public boolean isCopy;

    /** 是否允许飞行 */
    public boolean allowFly;

    /** 是否允许PVP */
    public boolean allowPVP;

    /** 收到服务器邀请 */
    public boolean canServerInvite;

    /** 禁止使用一个或者多个道具 */
    public int[] forbitItems;

    /** 禁止使用一个或者多个技能 */
    public int[] forbitSkills;

    /** 复活点限制条件 */
    public String[] relivePointCondition;

    /**
     * 进地图清除物品
     */
    public int[] removeItems;
    /**
     * 进地图清除buff
     */
    public int[] removeBuffs;
    /**
     * 场景AI
     */
    public int AIData;
    /**
     * 场景限制的最大人数，如果超过这个人数，场景将会新建副本容纳更多的人，如果不限制人数值为0
     */
    public int maxPlayer;
    /**
     * 地图阻挡信息，此信息和GameMap类里的tileInfo完全相同。但优先级高于map文件中的设定。只要在游戏编辑器中设置了通过性，则
     * 以游戏编辑器中的设置为准。
     */
    public byte[][] tileInfo;
    /**
     * 所有64个相位的名称配置。前8个相位是预留的初始相位，每个玩家第一次进入这个场景会自动获得前8个相位。
     */
    public String[] mirrorNames;

    /**
     * 是否适中在战斗状态
     */
    public boolean alwaysInBattle;
    /**
     * 杀人是否获得经验
     */
    public boolean getExpwhenKillPlayer;

    public GameMapInfo(GameArea owner) {
        this.owner = owner;
        // this.AI = new QuestInfo(new Quest(ProjectData.getActiveProject()));
        this.AIData = -1;
        this.mirrorNames = new String[64];
        for (int i = 0; i < 64; i++) {
            if (i < 8) {
                this.mirrorNames[i] = i + "号初始相位";
            }
            else {
                this.mirrorNames[i] = "未用相位";
            }
        }
    }

    public int getGlobalID() {
        return (owner.id << 4) | id;
    }

    public void load(Element elem) throws Exception {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        name = elem.getAttributeValue("name");
        neutral = "1".equals(elem.getAttributeValue("neutral"));
        allowDuel = !("0".equals(elem.getAttributeValue("allowduel")));
        try {
            protect = Integer.parseInt(elem.getAttributeValue("protect"));
        }
        catch (Exception e) {
            protect = 0;
        }
        allowFollow = !("0".equals(elem.getAttributeValue("allowfollow")));
        splitFaction = "1".equals(elem.getAttributeValue("splitfaction"));
        try {
            backgroundMusic = Integer.parseInt(elem.getAttributeValue("backgroundmusic"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        List list = elem.getChildren("exit");
        for (Object obj : list) {
            objects.add(loadExit((Element) obj));
        }
        // 加载多目标传送门
        list = elem.getChildren("multiexit");
        for (Object obj : list) {
            objects.add(loadMultiExit((Element) obj));
        }
        list = elem.getChildren("npc");
        for (Object obj : list) {
            objects.add(loadNPC((Element) obj));
        }
        list = elem.getChildren("vehicle");
        for (Object obj : list) {
            objects.add(loadVehicle((Element) obj));
        }
        list = elem.getChildren("patrolpath");
        for (Object obj : list) {
            objects.add(loadPatrolPath((Element) obj));
        }
        list = elem.getChildren("relivepoint");
        for (Object obj : list) {
            objects.add(loadRelivePoint((Element) obj));
        }
        list = elem.getChildren("questinfo");
        for (Object obj : list) {
            // AI.loadFromXML((Element)obj);
        }

        Attribute att = elem.getAttribute("faction");
        if (att == null) {
            this.faction = null;
        }
        else {
            this.faction = (Faction) owner.owner.findDictObject(Faction.class, att.getIntValue());
        }
        if (elem.getAttributeValue("maxplayer") != null) {
            maxPlayer = Integer.parseInt(elem.getAttributeValue("maxplayer"));
        }

        allowExchange = "1".equals(elem.getAttributeValue("allowExchange"));
        allowTeam = "1".equals(elem.getAttributeValue("allowTeam"));
        hasNoteTeam = "1".equals(elem.getAttributeValue("hasNoteTeam"));
        allowJoinPartyInvite = "1".equals(elem.getAttributeValue("allowJoinPartyInvite"));
        isCopy = "1".equals(elem.getAttributeValue("isCopy"));
        allowFly = "1".equals(elem.getAttributeValue("allowFly"));
        allowPVP = "1".equals(elem.getAttributeValue("allowPVP"));
        canServerInvite = "1".equals(elem.getAttributeValue("canServerInvite"));
        alwaysInBattle = "1".equals(elem.getAttributeValue("alwaysInBattle"));
        getExpwhenKillPlayer = "1".equals(elem.getAttributeValue("getExpwhenKillPlayer"));
        try {
            AIData = Integer.parseInt(elem.getAttributeValue("mapAI"));
        }
        catch (Exception e) {
            AIData = 0;
        }
        String tmpStr = elem.getAttributeValue("forbitItems");
        if (tmpStr != null) {
            String[] tmp = tmpStr.split(",");
            forbitItems = new int[tmp.length];
            for (int i = 0; i < forbitItems.length; i++) {
                forbitItems[i] = Integer.parseInt(tmp[i]);
            }
        }

        tmpStr = elem.getAttributeValue("forbitSkills");
        if (tmpStr != null) {
            String[] tmp = tmpStr.split(",");
            forbitSkills = new int[tmp.length];
            for (int i = 0; i < forbitSkills.length; i++) {
                forbitSkills[i] = Integer.parseInt(tmp[i]);
            }
        }
        try {
            tmpStr = elem.getAttributeValue("removeItems");
            if (tmpStr != null) {
                String[] tmp = tmpStr.split(",");
                removeItems = new int[tmp.length];
                for (int i = 0; i < removeItems.length; i++) {
                    removeItems[i] = Integer.parseInt(tmp[i]);
                }
            }

            tmpStr = elem.getAttributeValue("removeBuffs");
            if (tmpStr != null) {
                String[] tmp = tmpStr.split(",");
                removeBuffs = new int[tmp.length];
                for (int i = 0; i < removeBuffs.length; i++) {
                    removeBuffs[i] = Integer.parseInt(tmp[i]);
                }
            }

        }
        catch (Exception e) {
            removeItems = null;
            removeBuffs = null;
        }

        // 载入阻挡、安全区等信息（可选）
        tileInfo = null;
        Element linesEl = elem.getChild("tileBlock");
        if (linesEl != null) {
            List lineList = linesEl.getChildren("line");
            int size = lineList.size();
            tileInfo = new byte[size][];
            for (int i = 0; i < size; i++) {
                Element lineElem = (Element) lineList.get(i);
                String[] secs = lineElem.getTextTrim().split(" ");
                tileInfo[i] = new byte[secs.length];
                for (int j = 0; j < secs.length; j++) {
                    tileInfo[i][j] = Byte.parseByte(secs[j]);
                }
            }
        }

        // 载入相位命名信息
        Element mirrorEl = elem.getChild("mirrordef");
        if (mirrorEl != null) {
            String[] arr = mirrorEl.getTextTrim().split(",");
            for (int i = 0; i < mirrorNames.length && i < arr.length; i++) {
                mirrorNames[i] = arr[i];
            }
        }

        // long t = System.currentTimeMillis();

        // 如果是服务器模式，生成路径查找工具
        if (owner.owner.serverMode) {
            pathFinder = ProjectData.projDataFactory.createPathFinder(this);
            File bufFile = new File(owner.owner.baseDir, "PathFinder/" + getGlobalID() + ".pth");
            File areaInfoFile = new File(owner.source, "info.xml");
            File mapFile = owner.getFile(0);
            if (bufFile.exists() && bufFile.lastModified() > mapFile.lastModified()
                    && bufFile.lastModified() > areaInfoFile.lastModified()) {
                byte[] bytes = Utils.loadFileData(bufFile);
                try {
                    pathFinder.loadPathBuffer(bytes);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    pathFinder.buildPathBuffer();
                    Utils.saveFileData(bufFile, pathFinder.savePathBuffer());
                }
            }
            else {
                pathFinder.buildPathBuffer();
                bufFile.createNewFile();
                Utils.saveFileData(bufFile, pathFinder.savePathBuffer());
            }
        }
        // System.out.println("timeall: " + (System.currentTimeMillis() - t));
    }

    public Element save() throws Exception {
        // 保存前对所有的对象按ID进行排序
        sortObjects();

        Element elem = new Element("map");
        elem.addAttribute("id", String.valueOf(id));
        elem.addAttribute("name", name);
        elem.addAttribute("neutral", neutral ? "1" : "0");
        elem.addAttribute("allowduel", allowDuel ? "1" : "0");
        elem.addAttribute("protect", String.valueOf(protect));
        elem.addAttribute("allowfollow", allowFollow ? "1" : "0");
        elem.addAttribute("splitfaction", splitFaction ? "1" : "0");
        elem.addAttribute("backgroundmusic", String.valueOf(backgroundMusic));
        if (faction != null)
            elem.addAttribute("faction", String.valueOf(faction.id));
        if (maxPlayer != 0) {
            elem.addAttribute("maxplayer", String.valueOf(maxPlayer));
        }

        elem.addAttribute("allowExchange", allowExchange ? "1" : "0");
        elem.addAttribute("allowTeam", allowTeam ? "1" : "0");
        elem.addAttribute("hasNoteTeam", hasNoteTeam ? "1" : "0");
        elem.addAttribute("allowJoinPartyInvite", allowJoinPartyInvite ? "1" : "0");
        elem.addAttribute("isCopy", isCopy ? "1" : "0");
        elem.addAttribute("allowFly", allowFly ? "1" : "0");
        elem.addAttribute("allowPVP", allowPVP ? "1" : "0");
        elem.addAttribute("canServerInvite", canServerInvite ? "1" : "0");
        elem.addAttribute("alwaysInBattle", alwaysInBattle ? "1" : "0");
        elem.addAttribute("getExpwhenKillPlayer", getExpwhenKillPlayer ? "1" : "0");
        elem.addAttribute("mapAI", String.valueOf(AIData));

        StringBuffer sb = new StringBuffer();
        if (forbitItems != null) {
            for (int i = 0; i < forbitItems.length; i++) {
                sb.append(forbitItems[i]);
                sb.append(",");
            }
            elem.addAttribute("forbitItems", sb.toString());
        }

        sb = new StringBuffer();
        if (forbitSkills != null) {
            for (int i = 0; i < forbitSkills.length; i++) {
                sb.append(forbitSkills[i]);
                sb.append(",");
            }
            elem.addAttribute("forbitSkills", sb.toString());
        }

        sb = new StringBuffer();
        if (removeItems != null) {
            for (int i = 0; i < removeItems.length; i++) {
                sb.append(removeItems[i]);
                sb.append(",");
            }
            elem.addAttribute("removeItems", sb.toString());
        }
        sb = new StringBuffer();
        if (removeBuffs != null) {
            for (int i = 0; i < removeBuffs.length; i++) {
                sb.append(removeBuffs[i]);
                sb.append(",");
            }
            elem.addAttribute("removeBuffs", sb.toString());
        }

        for (GameMapObject obj : objects) {
            if (obj instanceof GameMapExit) {
                elem.getMixedContent().add(saveExit((GameMapExit) obj));
            }
            else if (obj instanceof GameMapNPC) {
                elem.getMixedContent().add(saveNPC((GameMapNPC) obj));
            }
            else if (obj instanceof GamePatrolPath) {
                elem.getMixedContent().add(savePatrolPath((GamePatrolPath) obj));
            }
            else if (obj instanceof XyGameMapVehicle) {
                elem.getMixedContent().add(saveVehicle((XyGameMapVehicle) obj));
            }
            else if (obj instanceof GameRelivePoint) {
                elem.getMixedContent().add(saveRelivePoint((GameRelivePoint) obj));
            }
            else if (obj instanceof MultiTargetMapExit) {
                elem.getMixedContent().add(saveMultiExit((MultiTargetMapExit) obj));
            }
        }
        // elem.getMixedContent().add(AI.saveToXML());

        // 保存tile设置信息（通过性、安全区等）
        if (tileInfo != null) {
            Element tileBlock = new Element("tileBlock");
            for (int i = 0; i < tileInfo.length; i++) {
                Element lineElem = new Element("line");
                StringBuffer buf = new StringBuffer(200);
                for (int j = 0; j < tileInfo[i].length; j++) {
                    buf.append(tileInfo[i][j] + " ");
                }
                lineElem.setText(buf.toString());
                tileBlock.getMixedContent().add(lineElem);
            }
            elem.getMixedContent().add(tileBlock);
        }

        // 保存相位命名信息
        Element mirrorEl = new Element("mirrordef");
        StringBuilder sbb = new StringBuilder();
        for (int i = 0; i < mirrorNames.length; i++) {
            if (i > 0) {
                sbb.append(",");
            }
            sbb.append(mirrorNames[i]);
        }
        mirrorEl.setText(sbb.toString());
        elem.getMixedContent().add(mirrorEl);

        return elem;
    }

    /**
     * 找到巡逻路径信息
     * 
     * @param patrolPathId
     * @return
     */
    public GamePatrolPath getGamePatrolPath(int patrolPathId) {
        if (patrolPathId >= 0) {
            for (GameMapObject obj : objects) {
                if (obj instanceof GamePatrolPath) {
                    if ((patrolPathId & 0xFFF) == ((GamePatrolPath) obj).id) {
                        return (GamePatrolPath) obj;
                    }
                }
            }
        }

        return null;
    }

    /*
     * 把地图中所有的游戏对象按ID进行排序。
     */
    protected void sortObjects() {
        int count = objects.size();
        for (int i = 0; i < count - 1; i++) {
            for (int j = i + 1; j < count; j++) {
                GameMapObject obj1 = objects.get(i);
                GameMapObject obj2 = objects.get(j);
                if (obj1.id > obj2.id) {
                    objects.set(i, obj2);
                    objects.set(j, obj1);
                }
            }
        }
    }

    protected GamePatrolPath loadPatrolPath(Element elem) {
        GamePatrolPath ret = new GamePatrolPath();
        ret.owner = this;
        String id = elem.getAttributeValue("id");
        if ("".equals(id) == false) {
            ret.id = Integer.parseInt(elem.getAttributeValue("id"));
        }

        String pathStr = elem.getAttributeValue("path");
        if ("".equals(pathStr) == false) {
            String[] path = pathStr.split(",");
            for (int i = 0; i < path.length / 2; i++) {
                int x = Integer.parseInt(path[i * 2]);
                int y = Integer.parseInt(path[i * 2 + 1]);
                ret.path.add(new int[] { x, y });
            }
        }

        return ret;
    }

    protected Element savePatrolPath(GamePatrolPath patrolPath) {
        Element elem = new Element("patrolpath");
        elem.addAttribute("id", String.valueOf(patrolPath.id));
        StringBuffer sb = new StringBuffer();
        for (int[] point : patrolPath.path) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(point[0] + "," + point[1]);
        }

        elem.addAttribute("path", sb.toString());
        return elem;
    }

    protected GameRelivePoint loadRelivePoint(Element elem) {
        GameRelivePoint ret = new GameRelivePoint();
        ret.owner = this;
        ret.id = Integer.parseInt(elem.getAttributeValue("id"));
        ret.x = Integer.parseInt(elem.getAttributeValue("x"));
        ret.y = Integer.parseInt(elem.getAttributeValue("y"));
        ret.condition = ExpressionList.fromString(elem.getAttributeValue("condition"));
        ret.jumpPosition[0] = Integer.parseInt(elem.getAttributeValue("jumpmap"));
        ret.jumpPosition[1] = Integer.parseInt(elem.getAttributeValue("jumpx"));
        ret.jumpPosition[2] = Integer.parseInt(elem.getAttributeValue("jumpy"));
        return ret;
    }

    protected Element saveRelivePoint(GameRelivePoint relivePoint) {
        Element elem = new Element("relivepoint");
        elem.addAttribute("id", String.valueOf(relivePoint.id));
        elem.addAttribute("x", String.valueOf(relivePoint.x));
        elem.addAttribute("y", String.valueOf(relivePoint.y));
        elem.addAttribute("condition", relivePoint.condition.toString());
        elem.addAttribute("jumpmap", String.valueOf(relivePoint.jumpPosition[0]));
        elem.addAttribute("jumpx", String.valueOf(relivePoint.jumpPosition[1]));
        elem.addAttribute("jumpy", String.valueOf(relivePoint.jumpPosition[2]));
        return elem;
    }

    protected GameMapExit loadExit(Element elem) {
        GameMapExit ret = new GameMapExit();
        ret.owner = this;
        ret.id = Integer.parseInt(elem.getAttributeValue("id"));
        ret.x = Integer.parseInt(elem.getAttributeValue("x"));
        ret.y = Integer.parseInt(elem.getAttributeValue("y"));
        ret.targetMap = Integer.parseInt(elem.getAttributeValue("targetmap"));
        ret.targetX = Integer.parseInt(elem.getAttributeValue("targetx"));
        ret.targetY = Integer.parseInt(elem.getAttributeValue("targety"));
        ret.showName = !("0".equals(elem.getAttributeValue("showname")));
        try {
            ret.exitType = Integer.parseInt(elem.getAttributeValue("exittype"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        ret.positionVarName = elem.getAttributeValue("posvarname");
        if (ret.positionVarName == null) {
            ret.positionVarName = "";
        }
        String str = elem.getAttributeValue("constraints");
        if (str != null) {
            ret.constraints = ExpressionList.fromString(str);
        }

        if (elem.getAttributeValue("constraintsDes") != null) {
            ret.constraintsDes = elem.getAttributeValue("constraintsDes");
        }
        if (elem.getAttributeValue("name") != null) {
            ret.name = elem.getAttributeValue("name");
        }

        if (elem.getAttributeValue("whichFloor") != null) {
            ret.layer = Integer.parseInt(elem.getAttributeValue("whichFloor"));
        }
        try {
            ret.mirrorSet = Long.parseLong(elem.getAttributeValue("mirrorset"));
        }
        catch (Exception e) {
        }

        return ret;
    }

    public MultiTargetMapExit loadMultiExit(Element elem) {
        MultiTargetMapExit ret = new MultiTargetMapExit();
        ret.owner = this;
        ret.id = Integer.parseInt(elem.getAttributeValue("id"));
        ret.x = Integer.parseInt(elem.getAttributeValue("x"));
        ret.y = Integer.parseInt(elem.getAttributeValue("y"));
        if (elem.getAttributeValue("name") != null) {
            ret.name = elem.getAttributeValue("name");
        }
        try{
            int fid = Integer.parseInt(elem.getAttributeValue("faction"));
            ret.faction = (Faction) owner.owner.findDictObject(Faction.class, fid);
        }catch(Exception e){
        }
        if (elem.getAttributeValue("whichFloor") != null) {
            ret.layer = Integer.parseInt(elem.getAttributeValue("whichFloor"));
        }
        try {
            ret.mirrorSet = Long.parseLong(elem.getAttributeValue("mirrorset"));
        }
        catch (Exception e) {
        }
        List list = elem.getChildren("exit");
        for(Object obj : list){
            Element child = (Element)obj;
            GameMapExit exit = loadExit(child);
            if(exit != null){
                ret.exitList.add(exit);
            }
        }
        return ret;
    }

    protected Element saveExit(GameMapExit exit) {
        Element elem = new Element("exit");
        elem.addAttribute("id", String.valueOf(exit.id));
        elem.addAttribute("x", String.valueOf(exit.x));
        elem.addAttribute("y", String.valueOf(exit.y));
        elem.addAttribute("targetmap", String.valueOf(exit.targetMap));
        elem.addAttribute("targetx", String.valueOf(exit.targetX));
        elem.addAttribute("targety", String.valueOf(exit.targetY));
        elem.addAttribute("showname", exit.showName ? "1" : "0");
        elem.addAttribute("exittype", String.valueOf(exit.exitType));
        elem.addAttribute("posvarname", exit.positionVarName);
        elem.addAttribute("name", exit.name);
        elem.addAttribute("constraints", exit.constraints.toString());
        elem.addAttribute("constraintsDes", exit.constraintsDes);
        elem.addAttribute("whichFloor", String.valueOf(exit.layer));
        elem.addAttribute("mirrorset", String.valueOf(exit.mirrorSet));
        return elem;
    }

    protected Element saveMultiExit(MultiTargetMapExit mexit) {
        Element elem = new Element("multiexit");
        elem.addAttribute("id", String.valueOf(mexit.id));
        elem.addAttribute("x", String.valueOf(mexit.x));
        elem.addAttribute("y", String.valueOf(mexit.y));
        elem.addAttribute("name", mexit.name);
        if(mexit.faction != null){
            elem.addAttribute("faction", String.valueOf(mexit.faction.id));
        }else{
            elem.addAttribute("faction", String.valueOf(-1));//
        }
        elem.addAttribute("whichFloor", String.valueOf(mexit.layer));
        elem.addAttribute("mirrorset", String.valueOf(mexit.mirrorSet));
        for (GameMapExit exit : mexit.exitList) {
            Element child = new Element("exit");
            child.addAttribute("id", String.valueOf(exit.id));
            child.addAttribute("x", String.valueOf(exit.x));
            child.addAttribute("y", String.valueOf(exit.y));
            child.addAttribute("targetmap", String.valueOf(exit.targetMap));
            child.addAttribute("targetx", String.valueOf(exit.targetX));
            child.addAttribute("targety", String.valueOf(exit.targetY));
            child.addAttribute("showname", exit.showName ? "1" : "0");
            child.addAttribute("exittype", String.valueOf(exit.exitType));
            child.addAttribute("posvarname", exit.positionVarName);
            child.addAttribute("name", exit.name);
            child.addAttribute("constraints", exit.constraints.toString());
            child.addAttribute("constraintsDes", exit.constraintsDes);
            child.addAttribute("whichFloor", String.valueOf(exit.layer));
            child.addAttribute("mirrorset", String.valueOf(exit.mirrorSet));
            elem.addContent(child);
        }
        return elem;
    }

    protected GameMapNPC loadNPC(Element elem) {
        int tid = Integer.parseInt(elem.getAttributeValue("template"));
        NPCTemplate npcTemplate = (NPCTemplate) owner.owner.findObject(NPCTemplate.class, tid);
        if (npcTemplate == null) {
            throw new IllegalArgumentException();
        }
        GameMapNPC ret = null;
        if (ProjectData.getActiveProject().config.gameMapNpcClass != null
                && ProjectData.getActiveProject().config.gameMapNpcClass.trim().length() > 0) {
            try {
                String className = ProjectData.getActiveProject().config.gameMapNpcClass.trim();
                ProjectConfig config = ProjectData.getActiveProject().config;
                Class clzz = config.getProjectClassLoader().loadClass(className);
                Constructor cons = clzz.getConstructor(NPCTemplate.class);
                ret = (GameMapNPC) cons.newInstance(npcTemplate);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            ret = new GameMapNPC(npcTemplate);
        }

        ret.owner = this;
        ret.id = Integer.parseInt(elem.getAttributeValue("id"));
        ret.x = Integer.parseInt(elem.getAttributeValue("x"));
        ret.y = Integer.parseInt(elem.getAttributeValue("y"));
        ret.template = npcTemplate;
        ret.name = elem.getAttributeValue("name");
        int fid = Integer.parseInt(elem.getAttributeValue("faction"));
        /*
         * 修改阵营时代码 switch(fid){ case 0: fid = 1; break; case 1: fid = 2; break;
         * case 2: fid = 3; break; case 3: fid = 4; break; case 4: fid = 5;
         * break; case 5: fid = 0; break; }//
         */
        ret.faction = (Faction) owner.owner.findDictObject(Faction.class, fid);
        ret.visible = "1".equals(elem.getAttributeValue("visible"));
        ret.canAttack = !("0".equals(elem.getAttributeValue("canattack")));
        ret.refreshInterval = Integer.parseInt(elem.getAttributeValue("refreshinterval"));
        ret.dynamicRefresh = !("0".equals(elem.getAttributeValue("dynamicrefresh")));
        try {
            ret.linkDistance = Integer.parseInt(elem.getAttributeValue("linkdistance"));
        }
        catch (Exception e) {
            ret.linkDistance = 0;
        }
        ret.isGuard = "1".equals(elem.getAttributeValue("isguard"));
        ret.isStatic = "1".equals(elem.getAttributeValue("isstatic"));
        try {
            ret.liveTime = Integer.parseInt(elem.getAttributeValue("livetime"));
        }
        catch (Exception e) {
            ret.liveTime = 0;
        }
        String[] pathPts = elem.getAttributeValue("patrolpath").split(";");
        for (String s : pathPts) {
            if (s.length() == 0) {
                continue;
            }
            String[] secs = s.split(",");
            ret.patrolPath.add(new int[] { Integer.parseInt(secs[0]), Integer.parseInt(secs[1]) });
        }
        ret.canPass = !("0".equals(elem.getAttributeValue("canpass")));
        ret.isFunctional = "1".equals(elem.getAttributeValue("functional"));
        ret.functionName = elem.getAttributeValue("funcname");
        if (ret.functionName == null) {
            ret.functionName = "";
        }
        ret.functionScript = elem.getAttributeValue("funcscript");
        if (ret.functionScript == null) {
            ret.functionScript = "";
        }
        ret.defaultChat = elem.getAttributeValue("defaultchat");
        if (ret.defaultChat == null) {
            ret.defaultChat = "";
        }
        try {
            ret.dieRefresh = Integer.parseInt(elem.getAttributeValue("dierefresh"));
        }
        catch (Exception e) {
            ret.dieRefresh = -1;
        }
        ret.broadcastDie = "1".equals(elem.getAttributeValue("broadcastdie"));
        ret.searchName = elem.getAttributeValue("searchname");
        if (ret.searchName == null) {
            ret.searchName = "";
        }
        String periods = elem.getAttributeValue("period");
        ret.periods = new ArrayList<Period>();
        if (periods != null && periods.length() != 0) {
            Period[] ps = Period.parse(periods);
            for (Period p : ps) {
                ret.periods.add(p);
            }
        }
        ret.revision = elem.getAttributeValue("revision");
        if (ret.revision == null) {
            ret.revision = "";
        }

        String combatCountStr = elem.getAttributeValue("combatCount");
        if (combatCountStr != null) {
            ret.combatCount = Integer.parseInt(combatCountStr);
        }

        String npcPatrolPath = elem.getAttributeValue("patrolPathId1");
        if (npcPatrolPath != null) {
            ret.patrolPathId1 = Integer.parseInt(npcPatrolPath);
        }
        npcPatrolPath = elem.getAttributeValue("patrolPathId2");
        if (npcPatrolPath != null) {
            ret.patrolPathId2 = Integer.parseInt(npcPatrolPath);
        }
        npcPatrolPath = elem.getAttributeValue("patrolPathId3");
        if (npcPatrolPath != null) {
            ret.patrolPathId3 = Integer.parseInt(npcPatrolPath);
        }

        npcPatrolPath = elem.getAttributeValue("whichFloor");
        if (npcPatrolPath != null) {
            ret.layer = Integer.parseInt(npcPatrolPath);
        }

        String monsterGrpIdStr = elem.getAttributeValue("monsterGrpId");
        if (monsterGrpIdStr != null) {
            ret.monsterGrpId = Integer.parseInt(monsterGrpIdStr);
        }

        try {
            ret.conlliseDistance = Integer.parseInt(elem.getAttributeValue("conlliseDistance"));
        }
        catch (Exception e) {
            ret.conlliseDistance = 0;
        }
        try {
            ret.mirrorSet = Long.parseLong(elem.getAttributeValue("mirrorset"));
        }
        catch (Exception e) {
        }
        if (elem.getAttributeValue("antiblock") != null) {
            ret.antiBlockArea = new int[4];
            String[] secs = elem.getAttributeValue("antiblock").split(",");
            for (int i = 0; i < 4; i++) {
                ret.antiBlockArea[i] = Integer.parseInt(secs[i]);
            }
        }
        try {
            ret.revision = elem.getAttributeValue("revision");
            if (ret.revision == null) {
                ret.revision = "";
            }
        }
        catch (Exception e) {

        }
        try {
            ret.channel = elem.getAttributeValue("channel");
            if (ret.channel == null) {
                ret.channel = "";
            }
        }
        catch (Exception e) {

        }
        ret.particle1 = elem.getAttributeValue("particle1");
        if (ret.particle1 == null) {
            ret.particle1 = "";
        }
        ret.particle2 = elem.getAttributeValue("particle2");
        if (ret.particle2 == null) {
            ret.particle2 = "";
        }

        try {
            ret.headImage = Integer.parseInt(elem.getAttributeValue("headImage"));
        }
        catch (Exception e) {
            ret.headImage = -1;
        }

        return ret;
    }

    protected Element saveNPC(GameMapNPC npc) throws Exception {
        Element elem = new Element("npc");
        elem.addAttribute("id", String.valueOf(npc.id));
        elem.addAttribute("x", String.valueOf(npc.x));
        elem.addAttribute("y", String.valueOf(npc.y));
        elem.addAttribute("template", String.valueOf(npc.template.id));
        elem.addAttribute("name", npc.name);
        elem.addAttribute("faction", String.valueOf(npc.faction.id));
        elem.addAttribute("visible", npc.visible ? "1" : "0");
        elem.addAttribute("canattack", npc.canAttack ? "1" : "0");
        elem.addAttribute("refreshinterval", String.valueOf(npc.refreshInterval));
        elem.addAttribute("dynamicrefresh", npc.dynamicRefresh ? "1" : "0");
        elem.addAttribute("linkdistance", String.valueOf(npc.linkDistance));
        elem.addAttribute("isguard", npc.isGuard ? "1" : "0");
        elem.addAttribute("isstatic", npc.isStatic ? "1" : "0");
        elem.addAttribute("livetime", String.valueOf(npc.liveTime));
        StringBuffer buf = new StringBuffer();
        for (int[] pt : npc.patrolPath) {
            if (buf.length() > 0) {
                buf.append(";");
            }
            buf.append(pt[0] + "," + pt[1]);
        }
        elem.addAttribute("patrolpath", buf.toString());
        elem.addAttribute("canpass", npc.canPass ? "1" : "0");
        elem.addAttribute("functional", npc.isFunctional ? "1" : "0");

        if (npc.isFunctional
                && (npc.functionName == null || "".equals(npc.functionName) || npc.functionScript == null || ""
                        .equals(npc.functionScript))) {
            throw new Exception("功能NPC的功能名称和脚本不能为空！");
        }
        elem.addAttribute("funcname", npc.functionName);
        elem.addAttribute("funcscript", npc.functionScript);
        if (npc.defaultChat != null && npc.defaultChat.length() > 0) {
            elem.addAttribute("defaultchat", npc.defaultChat);
        }
        elem.addAttribute("dierefresh", String.valueOf(npc.dieRefresh));
        elem.addAttribute("broadcastdie", npc.broadcastDie ? "1" : "0");
        if (npc.searchName.length() > 0) {
            elem.addAttribute("searchname", npc.searchName);
        }
        if (npc.periods.size() > 0) {
            StringBuilder sb = new StringBuilder(100);
            Period[] ps = new Period[npc.periods.size()];
            npc.periods.toArray(ps);
            elem.addAttribute("period", Period.getString(ps));
        }
        if (npc.revision.length() > 0) {
            elem.addAttribute("revision", npc.revision);
        }

        elem.addAttribute("patrolPathId1", String.valueOf(npc.patrolPathId1));
        elem.addAttribute("patrolPathId2", String.valueOf(npc.patrolPathId2));
        elem.addAttribute("patrolPathId3", String.valueOf(npc.patrolPathId3));

        elem.addAttribute("whichFloor", String.valueOf(npc.layer));
        elem.addAttribute("monsterGrpId", String.valueOf(npc.monsterGrpId));
        elem.addAttribute("conlliseDistance", String.valueOf(npc.conlliseDistance));
        elem.addAttribute("combatCount", String.valueOf(npc.combatCount));
        elem.addAttribute("mirrorset", String.valueOf(npc.mirrorSet));
        if (npc.antiBlockArea != null) {
            String str = npc.antiBlockArea[0] + "," + npc.antiBlockArea[1] + "," + npc.antiBlockArea[2] + ","
                    + npc.antiBlockArea[3];
            elem.addAttribute("antiblock", str);
        }

        if (npc.channel.length() > 0) {
            elem.addAttribute("channel", npc.channel);
        }
        if (npc.particle1.length() > 0) {
            elem.addAttribute("particle1", npc.particle1);
        }
        if (npc.particle2.length() > 0) {
            elem.addAttribute("particle2", npc.particle2);
        }
        elem.addAttribute("headImage", String.valueOf(npc.headImage));// 头顶功能图像

        return elem;
    }

    protected Element saveVehicle(XyGameMapVehicle npc) throws Exception {
        Element elem = new Element("vehicle");
        elem.addAttribute("id", String.valueOf(npc.id));
        elem.addAttribute("x", String.valueOf(npc.x));
        elem.addAttribute("y", String.valueOf(npc.y));
        elem.addAttribute("template", String.valueOf(npc.template.id));
        elem.addAttribute("name", npc.name);
        elem.addAttribute("faction", String.valueOf(npc.faction.id));
        elem.addAttribute("visible", npc.visible ? "1" : "0");
        elem.addAttribute("canattack", npc.canAttack ? "1" : "0");
        elem.addAttribute("refreshinterval", String.valueOf(npc.refreshInterval));
        elem.addAttribute("dynamicrefresh", npc.dynamicRefresh ? "1" : "0");
        elem.addAttribute("linkdistance", String.valueOf(npc.linkDistance));
        elem.addAttribute("isguard", npc.isGuard ? "1" : "0");
        elem.addAttribute("isstatic", npc.isStatic ? "1" : "0");
        elem.addAttribute("livetime", String.valueOf(npc.liveTime));
        StringBuffer buf = new StringBuffer();
        for (int[] pt : npc.patrolPath) {
            if (buf.length() > 0) {
                buf.append(";");
            }
            buf.append(pt[0] + "," + pt[1]);
        }
        elem.addAttribute("patrolpath", buf.toString());
        elem.addAttribute("canpass", npc.canPass ? "1" : "0");
        elem.addAttribute("functional", npc.isFunctional ? "1" : "0");

        if (npc.isFunctional
                && (npc.functionName == null || "".equals(npc.functionName) || npc.functionScript == null || ""
                        .equals(npc.functionScript))) {
            throw new Exception("功能NPC的功能名称和脚本不能为空！");
        }
        elem.addAttribute("funcname", npc.functionName);
        elem.addAttribute("funcscript", npc.functionScript);
        elem.addAttribute("dierefresh", String.valueOf(npc.dieRefresh));
        elem.addAttribute("broadcastdie", npc.broadcastDie ? "1" : "0");
        if (npc.searchName.length() > 0) {
            elem.addAttribute("searchname", npc.searchName);
        }
        if (npc.periods.size() > 0) {
            StringBuilder sb = new StringBuilder(100);
            Period[] ps = new Period[npc.periods.size()];
            npc.periods.toArray(ps);
            elem.addAttribute("period", Period.getString(ps));
        }
        if (npc.revision.length() > 0) {
            elem.addAttribute("revision", npc.revision);
        }

        elem.addAttribute("patrolPathId1", String.valueOf(npc.patrolPathId1));
        elem.addAttribute("patrolPathId2", String.valueOf(npc.patrolPathId2));
        elem.addAttribute("patrolPathId3", String.valueOf(npc.patrolPathId3));

        elem.addAttribute("whichFloor", String.valueOf(npc.layer));
        elem.addAttribute("monsterGrpId", String.valueOf(npc.monsterGrpId));
        elem.addAttribute("conlliseDistance", String.valueOf(npc.conlliseDistance));
        elem.addAttribute("combatCount", String.valueOf(npc.combatCount));
        elem.addAttribute("reuse", npc.reuse ? "1" : "0");
        elem.addAttribute("throughFloor", npc.throughFloor ? "1" : "0");
        elem.addAttribute("disappear", npc.disappear ? "1" : "0");
        elem.addAttribute("copy", npc.copy ? "1" : "0");
        elem.addAttribute("canSeeTitle", npc.canSeeTitle ? "1" : "0");

        return elem;
    }

    protected XyGameMapVehicle loadVehicle(Element elem) {
        int tid = Integer.parseInt(elem.getAttributeValue("template"));
        Vehicle npcTemplate = (Vehicle) owner.owner.findObject(Vehicle.class, tid);
        XyGameMapVehicle ret = new XyGameMapVehicle(npcTemplate);

        ret.owner = this;
        ret.id = Integer.parseInt(elem.getAttributeValue("id"));
        ret.x = Integer.parseInt(elem.getAttributeValue("x"));
        ret.y = Integer.parseInt(elem.getAttributeValue("y"));
        ret.template = npcTemplate;
        ret.name = elem.getAttributeValue("name");
        int fid = Integer.parseInt(elem.getAttributeValue("faction"));
        /*
         * 修改阵营时代码 switch(fid){ case 0: fid = 1; break; case 1: fid = 2; break;
         * case 2: fid = 3; break; case 3: fid = 4; break; case 4: fid = 5;
         * break; case 5: fid = 0; break; }//
         */
        ret.faction = (Faction) owner.owner.findDictObject(Faction.class, fid);
        ret.visible = "1".equals(elem.getAttributeValue("visible"));
        ret.canAttack = !("0".equals(elem.getAttributeValue("canattack")));
        ret.refreshInterval = Integer.parseInt(elem.getAttributeValue("refreshinterval"));
        ret.dynamicRefresh = !("0".equals(elem.getAttributeValue("dynamicrefresh")));
        try {
            ret.linkDistance = Integer.parseInt(elem.getAttributeValue("linkdistance"));
        }
        catch (Exception e) {
            ret.linkDistance = 0;
        }
        ret.isGuard = "1".equals(elem.getAttributeValue("isguard"));
        ret.isStatic = "1".equals(elem.getAttributeValue("isstatic"));
        try {
            ret.liveTime = Integer.parseInt(elem.getAttributeValue("livetime"));
        }
        catch (Exception e) {
            ret.liveTime = 0;
        }
        String[] pathPts = elem.getAttributeValue("patrolpath").split(";");
        for (String s : pathPts) {
            if (s.length() == 0) {
                continue;
            }
            String[] secs = s.split(",");
            ret.patrolPath.add(new int[] { Integer.parseInt(secs[0]), Integer.parseInt(secs[1]) });
        }
        ret.canPass = !("0".equals(elem.getAttributeValue("canpass")));
        ret.isFunctional = "1".equals(elem.getAttributeValue("functional"));
        ret.functionName = elem.getAttributeValue("funcname");
        if (ret.functionName == null) {
            ret.functionName = "";
        }
        ret.functionScript = elem.getAttributeValue("funcscript");
        if (ret.functionScript == null) {
            ret.functionScript = "";
        }
        try {
            ret.dieRefresh = Integer.parseInt(elem.getAttributeValue("dierefresh"));
        }
        catch (Exception e) {
            ret.dieRefresh = -1;
        }
        ret.broadcastDie = "1".equals(elem.getAttributeValue("broadcastdie"));
        ret.searchName = elem.getAttributeValue("searchname");
        if (ret.searchName == null) {
            ret.searchName = "";
        }
        String periods = elem.getAttributeValue("period");
        ret.periods = new ArrayList<Period>();
        if (periods != null && periods.length() != 0) {
            Period[] ps = Period.parse(periods);
            for (Period p : ps) {
                ret.periods.add(p);
            }
        }
        ret.revision = elem.getAttributeValue("revision");
        if (ret.revision == null) {
            ret.revision = "";
        }

        String combatCountStr = elem.getAttributeValue("combatCount");
        if (combatCountStr != null) {
            ret.combatCount = Integer.parseInt(combatCountStr);
        }

        String npcPatrolPath = elem.getAttributeValue("patrolPathId1");
        if (npcPatrolPath != null) {
            ret.patrolPathId1 = Integer.parseInt(npcPatrolPath);
        }
        npcPatrolPath = elem.getAttributeValue("patrolPathId2");
        if (npcPatrolPath != null) {
            ret.patrolPathId2 = Integer.parseInt(npcPatrolPath);
        }
        npcPatrolPath = elem.getAttributeValue("patrolPathId3");
        if (npcPatrolPath != null) {
            ret.patrolPathId3 = Integer.parseInt(npcPatrolPath);
        }

        npcPatrolPath = elem.getAttributeValue("whichFloor");
        if (npcPatrolPath != null) {
            ret.layer = Integer.parseInt(npcPatrolPath);
        }

        String monsterGrpIdStr = elem.getAttributeValue("monsterGrpId");
        if (monsterGrpIdStr != null) {
            ret.monsterGrpId = Integer.parseInt(monsterGrpIdStr);
        }

        try {
            ret.conlliseDistance = Integer.parseInt(elem.getAttributeValue("conlliseDistance"));
        }
        catch (Exception e) {
            ret.conlliseDistance = 0;
        }
        ret.reuse = !("0".equals(elem.getAttributeValue("reuse")));
        ret.throughFloor = !("0".equals(elem.getAttributeValue("throughFloor")));
        ret.copy = !("0".equals(elem.getAttributeValue("copy")));
        ret.disappear = !("0".equals(elem.getAttributeValue("disappear")));
        ret.canSeeTitle = !("0".equals(elem.getAttributeValue("canSeeTitle")));

        return ret;
    }

    /**
     * 根据ID查找一个对象。
     */
    public GameMapObject findObject(int id) {
        for (GameMapObject mo : objects) {
            if (mo.id == id) {
                return mo;
            }
        }
        return null;
    }

    /**
     * 在项目中根据对象ID查找一个对象。
     * 
     * @param id
     * @return
     */
    public static GameMapInfo findByID(ProjectData project, int id) {
        try {
            int areaID = (id >> 4) & 0xFFFF;
            int mapID = id & 0x0F;
            GameAreaInfo areaInfo;
            if (project.serverMode) {
                GameArea area = (GameArea) project.findObject(GameArea.class, areaID);
                areaInfo = area.getAreaInfo();
            }
            else {
                areaInfo = GameAreaCache.getAreaInfo(areaID);
            }
            if (areaInfo == null) {
                // System.out.println("GameMapInfo.findByID()");
                // System.err.println("未找到怪卡id：" + areaID + "的关卡");
                return null;
            }
            for (GameMapInfo mi : areaInfo.maps) {
                if (mi.id == mapID) {
                    return mi;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String locationToString(ProjectData project, int[] location, boolean useVirtualCoord) {
        return locationToString(project, location, useVirtualCoord, "");
    }

    /**
     * 把一个地图坐标转换为文字显示。
     * 
     * @param project
     *            项目数据
     * @param location
     *            地图坐标，元素依次是：地图ID、X（像素）、Y（像素）
     * @param useVirtualCoord
     *            是否使用虚拟坐标系（单位为码）
     * @return
     */
    public static String locationToString(ProjectData project, int[] location, boolean useVirtualCoord, String condition) {
        GameMapInfo mi = findByID(project, location[0]);
        int x = location[1];
        int y = location[2];
        if (useVirtualCoord) {
            x /= 8;
            y /= 8;
        }
        if (mi == null) {
            return "未知场景(" + x + "," + y + ")";
        }
        else {
            String tmp = ExpressionList.toNatureString(condition == null ? "" : condition);
            return mi.name + "(" + x + "," + y + ")" + ("无".equals(tmp) ? "" : "(" + tmp + ")");
        }
    }

    /**
     * 得到一个场景的名字。
     * 
     * @param project
     *            项目数据
     * @param sceneID
     *            场景ID
     * @return
     */
    public static String toString(ProjectData project, int sceneID) {
        GameMapInfo mi = findByID(project, sceneID);
        if (mi == null) {
            return "未知场景";
        }
        else {
            return mi.name;
        }
    }

    /**
     * 取得预先生成的路径查找工具。
     * 
     * @return
     */
    public PathFinder getPathFinder() {
        return pathFinder;
    }

    /**
     * 检查地图的视线遮挡信息，判断从地图上的某点能否看到另外一个点。此方法只有在服务器模式才能调用。
     * 
     * @param fromx
     *            起始点坐标（像素）
     * @param fromy
     *            起始点坐标（像素）
     * @param tox
     *            结束点坐标（像素）
     * @param toy
     *            结束点坐标（像素）
     * @return
     */
    public boolean canSee(int fromx, int fromy, int tox, int toy) {
        byte[][] testTileInfo = tileInfo;
        if (tileInfo == null) {
            testTileInfo = owner.getMapFile().getMaps().get(id).tileInfo;
        }
        try {
            fromx >>= 3;
            fromy >>= 3;
            tox >>= 3;
            toy >>= 3;
            if (fromx == tox) {
                // 对于垂直这种情况，无法计算tgt值，所以需要特殊处理
                int delta = 1;
                if (toy < fromy) {
                    delta = -1;
                }
                for (int y = fromy; true; y += delta) {
                    if ((testTileInfo[y][fromx] & 1) != 0) {
                        return false;
                    }
                    if (y == toy) {
                        break;
                    }
                }
                return true;
            }

            // 计算tgt值，然后计算直线上每个点是否阻挡
            int tgt = (toy - fromy) * 10000 / (tox - fromx);
            int delta = 1;
            if (tox < fromx) {
                delta = -1;
            }
            int lastx = fromx;
            int lasty = fromy;
            for (int x = fromx + delta; true; x += delta) {
                int y = (x - fromx) * tgt / 10000 + fromy;
                if (x == tox) {
                    y = toy;
                }
                if (lasty > y) {
                    for (int yy = y; yy <= lasty; yy++) {
                        if ((testTileInfo[yy][lastx] & 1) != 0) {
                            return false;
                        }
                    }
                }
                else {
                    for (int yy = lasty; yy <= y; yy++) {
                        if ((testTileInfo[yy][lastx] & 1) != 0) {
                            return false;
                        }
                    }
                }
                if (x == tox) {
                    break;
                }
                lastx = x;
                lasty = y;
            }
            if ((testTileInfo[toy][tox] & 1) != 0) {
                return false;
            }
            return true;
        }
        catch (Exception e) {
            return true;
        }
    }

    /**
     * 测试两点之间视线轨迹的方法。
     */
    public List<int[]> getEyesightPath(int fromx, int fromy, int tox, int toy) {
        List<int[]> ret = new ArrayList<int[]>();
        try {
            fromx >>= 3;
            fromy >>= 3;
            tox >>= 3;
            toy >>= 3;
            if (fromx == tox) {
                // 对于垂直这种情况，无法计算tgt值，所以需要特殊处理
                int delta = 1;
                if (toy < fromy) {
                    delta = -1;
                }
                for (int y = fromy; true; y += delta) {
                    ret.add(new int[] { fromx, y });
                    if (y == toy) {
                        break;
                    }
                }
                return ret;
            }

            // 计算tgt值，然后计算直线上每个点是否阻挡
            int tgt = (toy - fromy) * 10000 / (tox - fromx);
            int delta = 1;
            if (tox < fromx) {
                delta = -1;
            }
            int lastx = fromx;
            int lasty = fromy;
            for (int x = fromx + delta; true; x += delta) {
                int y = (x - fromx) * tgt / 10000 + fromy;
                if (x == tox) {
                    y = toy;
                }
                if (lasty > y) {
                    for (int yy = y; yy <= lasty; yy++) {
                        ret.add(new int[] { lastx, yy });
                    }
                }
                else {
                    for (int yy = lasty; yy <= y; yy++) {
                        ret.add(new int[] { lastx, yy });
                    }
                }
                if (x == tox) {
                    break;
                }
                lastx = x;
                lasty = y;
            }
            ret.add(new int[] { tox, toy });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
