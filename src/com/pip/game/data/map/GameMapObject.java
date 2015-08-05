package com.pip.game.data.map;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pip.game.data.GameArea;
import com.pip.game.data.GameAreaInfo;
import com.pip.game.data.ProjectData;
import com.pip.game.data.item.Monster;
import com.pip.game.editor.quest.GameAreaCache;

/**
 * 地图中的游戏对象。
 * @author lighthu
 */
public class GameMapObject {
    /** 所属地图 */
    public GameMapInfo owner;
    /** 全局出口ID */
    public int id;
    /** X位置（像素） */
    public int x;
    /** Y位置（像素） */
    public int y;
    
    /** 该对象所在的层， 0: 地面人物层，1:天空人物层 **/
    public int layer;

    /**
     * 取得对象的全局ID。
     * @return
     */
    public int getGlobalID() {
        return (owner.getGlobalID() << 12) | id;
    }
    
//    public static byte[] getMapNpcs(ProjectData project, int areaID) {
//        List<GameMapNPC> ls = new ArrayList<GameMapNPC>();
//        GameArea ga = (GameArea) project.findObject(GameArea.class, areaID);
//        GameAreaInfo areaInfo = ga.getAreaInfo();
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        DataOutputStream dos = new DataOutputStream(bos);
//        try {
//            dos.writeByte(areaInfo.maps.size()); // 关卡中有几个MAP
//            for (GameMapInfo mi : areaInfo.maps) {
//                ls.clear();
//                for (GameMapObject obj : mi.objects) {
//                    if (obj instanceof GameMapNPC) {
//                        ls.add((GameMapNPC) obj);
//                    }
//                }
//                dos.writeByte(mi.id);//mi.id
//                dos.writeByte(ls.size());
//                if (ls.size() > 0) {
//                    for (GameMapNPC npc : ls) {
//                        byte[] bytes = npc.toClientBytes(); 
//                        dos.writeInt(bytes.length);
//                        dos.write(bytes);
//                    }
//                }
//            }
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//        return bos.toByteArray();
//    }
    
    /**
     * 在项目中根据对象ID查找一个对象。
     * @param id
     * @return
     */
    public static GameMapObject findByID(ProjectData project, int id) {
        try {
            int areaID = (id >> 16) & 0xFFFF;
            int mapID = (id >> 12) & 0x0F;
            int npcIndex = id & 0xFFF;
            GameAreaInfo areaInfo = null;
            if (project.serverMode) {
                // 服务器模式，直接从GameArea对象获取GameAreaInfo
                GameArea ga = (GameArea)project.findObject(GameArea.class, areaID);
                if(ga != null){
                    areaInfo = ga.getAreaInfo();
                }
            } else {
                // 编辑器中有缓存提高速度
                areaInfo = GameAreaCache.getAreaInfo(areaID);
            }
            if(areaInfo == null){
                return null;
            }
            GameMapInfo mapInfo = null;
            for (GameMapInfo mi : areaInfo.maps) {
                if (mi.id == mapID) {
                    mapInfo = mi;
                    break;
                }
            }
            for (GameMapObject obj : mapInfo.objects) {
                if (obj.id == npcIndex) {
                    return obj;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 根据ID查找一个对象并得到它的字符串表示。
     */
    public static String toString(ProjectData proj, int id) {
        if (id == -1) {
            return "无";
        }
        GameMapObject obj = findByID(proj, id);
        if (obj == null) {
            return "未找到";
        } else {
            return obj.toString();
        }
    }
}
