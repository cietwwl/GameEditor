package com.pip.game.data.map;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.pip.game.data.Faction;
import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectData;

/**
 * 地图中的一个NPC。这里的一个NPC是用NPC模板创建的一个实例，每个实例都有唯一的ID。
 * @author lighthu
 */
public class GameMapNPC extends GameMapObject {
    /** NPC模板 */
    public NPCTemplate template;
    /** NPC名字 */
    public String name;
    /** NPC 阵营 */
    public Faction faction;
    /** 初始是否出现 */
    public boolean visible;
    /** 是否允许被攻击 */
    public boolean canAttack = true;
    /** 刷新时间(秒)，-1表示触发永不刷新 */
    public int refreshInterval;
    /** 是否采用动态刷新时间 */
    public boolean dynamicRefresh = true;
    /** 关联仇恨距离 */
    public int linkDistance;
    /** 是否卫兵，卫兵和怪物中立 */
    public boolean isGuard;
    /** 是否静态NPC，静态NPC一进入场景就会刷给用户 */
    public boolean isStatic;
    /** 最大生命周期，0表示永久 */
    public int liveTime;
    /** 巡逻路径，是用多个点组成的一个封闭多边形区域 */
    public List<int[]> patrolPath = new ArrayList<int[]>();
    /** 是否允许通过，如果为false，则可以阻挡玩家行动 */
    public boolean canPass = true;
    /** 是否功能NPC */
    public boolean isFunctional = false;
    /** 如果是功能NPC，说明功能名称，例如“进入拍卖行”。新版本可以支持分号分隔的多个功能 */
    public String functionName = "";
    /** 如果是功能NPC，说明启动功能的脚本。新版本支持多个，回车分隔。 */
    public String functionScript = "";
    /** 缺省对白 */
    public String defaultChat = "";
    /** 死亡后刷新的NPC，-1表示不刷新 */
    public int dieRefresh = -1;
    /** 死亡后是否广播 */
    public boolean broadcastDie = false;
    /** 寻路名称 */
    public String searchName = "";
    /** 限制版本 */
    public String revision = "";
    
    public List<Period> periods = new ArrayList<Period>();
        
    /** Npc巡逻路径id */
    public int patrolPathId1 = -1;
    public int patrolPathId2 = -1;
    public int patrolPathId3 = -1;
        
    /** 如果是怪物，表示触发的怪物组Id */
    public int monsterGrpId;
        
    /** 可以同时进入战斗的次数 */
    public int combatCount = 1;
    
    /** Npc碰撞距离(码) */
    public int conlliseDistance;
    
    /** 所在相位 */
    public long mirrorSet = 1L;
    
    /** 
     * 抹除阻挡的范围（以格子为单位，格子大小为MapFile.cellSize）。四个元素分别是x，y，w，h（相对于NPC位置）。
     * null表示没有抹除阻挡功能。 
     */
    public int[] antiBlockArea;
    /**
     * 渠道名称（多个以,分隔）
     */
    public String channel = "";
    
    /**
     * 附加的粒子效果（先画）。
     */
    public String particle1 = "";
    /**
     * 附加的粒子效果（后画）。
     */
    public String particle2 = "";
    
    public int headImage = -1;//头顶功能图标
    
    public GameMapNPC() {
        
    }
    /**
     * 主要用来构建monster
     * @param template
     */
    public GameMapNPC(NPCTemplate template){
        id = template.id;
        this.template = template;
        this.name = template.title;
        this.visible = false;
        this.canAttack = false;
        this.refreshInterval = 0;
        this.dynamicRefresh = false;
        this.isFunctional = false;
        this.broadcastDie = true;
    }
    
//    public byte[] toClientBytes(){
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        DataOutputStream dos = new DataOutputStream(bos);
//        try{
//            dos.writeInt(id);
//            dos.writeInt(x);
//            dos.writeInt(y);
//            dos.writeInt(template.image.id);
//            dos.writeUTF(name);
//            dos.writeBoolean(visible);
//            dos.writeBoolean(isFunctional);
//            dos.writeUTF(functionScript);
//            dos.writeByte(patrolPath.size());
//            for(int[] point : patrolPath){
//                dos.writeInt(point[0]);
//                dos.writeInt(point[1]);
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return bos.toByteArray();
//    }
    
    /**
     * 得到NPC的全名称，包括场景名称和NPC名称。
     */
    public String toString() {
        String realName = name;
        int pos = realName.indexOf('|');
        if (pos != -1) {
            realName = realName.substring(0, pos);
        }
        return owner.name + " -> " + realName;
    }

    /**
     * 根据ID查找一个对象的名字。
     */
    public static String toStringShort(ProjectData proj, int id) {
        if (id == -1) {
            return "无";
        }
        GameMapObject obj = findByID(proj, id);
        if (obj == null || !(obj instanceof GameMapNPC)) {
            return "未找到";
        } else {
            return ((GameMapNPC)obj).name;
        }
    }
    
    public String toFindStr(){
        
        return this.x+","+this.y;
    }
}
