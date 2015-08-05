package com.pip.game.editor.quest;

import java.util.HashMap;

import com.pip.game.data.GameArea;
import com.pip.game.data.GameAreaInfo;
import com.pip.game.data.ProjectData;

/**
 * ���໺������������༭����������ĵ�ͼ��Ϣ���Լӿ�����ٶȡ��������ͼ�޸�ʱ��Ӧ���´˻��档
 * @author lighthu
 */
public class GameAreaCache {
    // ��ͼ���ݻ���
    private static HashMap<Integer, GameAreaInfo> areaInfoCache = new HashMap<Integer, GameAreaInfo>();
 
    /**
     * ȡ��һ���ؿ��ĵ�ͼ��Ϣ���������ؿ��ĵ�ͼ��Ϣû���ڻ����У�������֮�����뻺�档
     * @param areaID
     * @return
     */
    public static GameAreaInfo getAreaInfo(int areaID) {
        GameAreaInfo areaInfo = areaInfoCache.get(areaID);
        if (areaInfo == null) {
            GameArea area = (GameArea)ProjectData.getActiveProject().findObject(GameArea.class, areaID);
            if (area == null) {
                return null;
            }
            areaInfo = new GameAreaInfo(area);
            try {
                areaInfo.load();
                areaInfoCache.put(areaID, areaInfo);
            } catch (Exception e) {
                System.out.println("GameAreaCache.getAreaInfo() - Load area info error:\n"+e);
                return null;
            }
        }
        return areaInfo;
    }

    /**
     * �������Ĺؿ���Ϣ����һ���ؿ����޸�ʱ��Ҫ���ô˷���������棬�´�ʹ��ʱ�����롣
     * @param areaID
     */
    public static void clearAreaInfo(int areaID) {
        areaInfoCache.remove(areaID);
    }
    
    /**
     * �����������йؿ���Ϣ��
     */
    public static void clearAreaInfo() {
        areaInfoCache.clear();
    }
}
