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
 * ��ͼ�е���Ϸ����
 * @author lighthu
 */
public class GameMapObject {
    /** ������ͼ */
    public GameMapInfo owner;
    /** ȫ�ֳ���ID */
    public int id;
    /** Xλ�ã����أ� */
    public int x;
    /** Yλ�ã����أ� */
    public int y;
    
    /** �ö������ڵĲ㣬 0: ��������㣬1:�������� **/
    public int layer;

    /**
     * ȡ�ö����ȫ��ID��
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
//            dos.writeByte(areaInfo.maps.size()); // �ؿ����м���MAP
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
     * ����Ŀ�и��ݶ���ID����һ������
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
                // ������ģʽ��ֱ�Ӵ�GameArea�����ȡGameAreaInfo
                GameArea ga = (GameArea)project.findObject(GameArea.class, areaID);
                if(ga != null){
                    areaInfo = ga.getAreaInfo();
                }
            } else {
                // �༭�����л�������ٶ�
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
     * ����ID����һ�����󲢵õ������ַ�����ʾ��
     */
    public static String toString(ProjectData proj, int id) {
        if (id == -1) {
            return "��";
        }
        GameMapObject obj = findByID(proj, id);
        if (obj == null) {
            return "δ�ҵ�";
        } else {
            return obj.toString();
        }
    }
}
