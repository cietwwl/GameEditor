package com.pip.game.data.vehicle;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;

import com.pip.game.data.Faction;
import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.data.map.Period;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.area.GameAreaEditor;
import com.pip.game.editor.area.GameMapViewer;
import com.pip.mapeditor.MapViewer;
import com.pip.mapeditor.data.GameMap;
import com.pipimage.image.PipAnimate;
import com.pipimage.image.PipAnimateSet;

public class XyGameMapVehicle extends GameMapObject {

    /** NPC模板 */
    public Vehicle template;
    /** NPC名字 */
    public String name;
    /** NPC 阵营 */
    public Faction faction;
    /** 初始是否出现 */
    public boolean visible;
    /** 是否允许被攻击 */
    public boolean canAttack = true;
    /** 刷新时间(秒)，-1表示触发刷新 */
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
    /** 载具是否可以生成副本*/
    public boolean copy;
    /** 是否可以多次使用*/
    public boolean reuse;
    /**是否在地面无阻挡*/
    public boolean throughFloor;
    /**使用后是否消失*/
    public boolean disappear;
    /**名称是否可见 */
    public boolean canSeeTitle;
        
    public XyGameMapVehicle() {
        
    }
    /**
     * 主要用来构建monster
     * @param template
     */
    public XyGameMapVehicle(Vehicle template){
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
    
    public byte[] toClientBytes(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try{
            dos.writeInt(id);
            dos.writeInt(x);
            dos.writeInt(y);
            dos.writeInt(template.image.id);
            dos.writeUTF(name);
            dos.writeBoolean(visible);
            dos.writeBoolean(isFunctional);
            dos.writeUTF(functionScript);
            dos.writeByte(patrolPath.size());
            for(int[] point : patrolPath){
                dos.writeInt(point[0]);
                dos.writeInt(point[1]);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return bos.toByteArray();
    }
    
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
        if (obj == null || !(obj instanceof XyGameMapVehicle)) {
            return "未找到";
        } else {
            return ((XyGameMapVehicle)obj).name;
        }
    }
}
