package com.pip.game.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pip.game.data.map.GameMapExit;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.data.map.PathFinder;

/**
 * �Զ�Ѱ·���ߡ�
 * @author lighthu
 */
public class AutoPathFinder {
    protected ProjectData owner;
    // ���е�ͼ�ĳ�����Ϣ
    protected Map<Integer, GameMapExit[]> mapLinks;
    
    //�̳���
    protected AutoPathFinder(){
        
    }
    
    /**
     * ��ʼ���Զ�Ѱ·���ߣ��ҳ����г���֮���ͨ���ϵ��
     * @param proj
     */
    public AutoPathFinder(ProjectData proj) {
        owner = proj;
        mapLinks = new HashMap<Integer, GameMapExit[]>();
        List<DataObject> dobjs = proj.getDataListByType(GameArea.class);
        for (DataObject dobj : dobjs) {
            GameArea ga = (GameArea)dobj;
            for (GameMapInfo gmi : ga.getAreaInfo().maps) {
                // �ҳ����г���
                List<GameMapExit> links = new ArrayList<GameMapExit>();
                for (GameMapObject gmo : gmi.objects) {
                    if (gmo instanceof GameMapExit) {
                        links.add((GameMapExit)gmo);
                    }
                }
                GameMapExit[] arr = new GameMapExit[links.size()];
                links.toArray(arr);
                mapLinks.put(gmi.getGlobalID(), arr);
            }
        }
    }
    
    /**
     * ���������ͼ��������֮���·����
     * @param smap Դ��ͼȫ��ID
     * @param sx ԴXλ�ã����أ�
     * @param sy ԴYλ�ã����أ�
     * @param tmap Ŀ���ͼȫ��ID
     * @param tx Ŀ��Xλ�ã����أ�
     * @param ty Ŀ��Yλ�ã����أ�
     * @return ���������ͬһ�������ҿ���ͨ����ؿ����顣���·��δ�ҵ�������null������ҵ�·��
     *    ����Ҫͨ�����γ�����ת���ܵ������·���ϵ����г��ڡ�
     */
    public GameMapExit[] findPath(IConditionCheck source, int smap, int sx, int sy, int tmap, int tx, int ty) {
        // ͬ����Ѱ·���⴦��
        if (smap == tmap) {
            if (checkPath(smap, sx, sy, tx, ty)) {
                return new GameMapExit[0];
            }
        }

        // �ڿ�ʼ�����У��ҳ�����ʼ���ܹ���������г��ڣ����뱸ѡ·����
        List<GameMapExit[]> findBuffer = new ArrayList<GameMapExit[]>();
        Set<GameMapExit> visited = new HashSet<GameMapExit>();
        GameMapExit[] arr = mapLinks.get(smap);
        if (arr == null) {
            return null;
        }
        GameMapInfo gmi = GameMapInfo.findByID(owner, smap);
        if (gmi == null) {
            return null;
        }
        PathFinder pf = gmi.getPathFinder();
        for (int i = 0; i < arr.length; i++) {
        	if (source.checkCondition(arr[i].constraints) != -1) {
        		continue;
        	}
            if (pf.canReach(sx, sy, i)) {
                findBuffer.add(new GameMapExit[] { arr[i] });
                visited.add(arr[i]);
            }
        }
        
        // ��ȱ������п��ܵ�·����ֱ���ҳ��ܵ���Ŀ����·�������ߺ�ѡ·����Ϊ��
        while (findBuffer.size() > 0) {
            GameMapExit[] path = findBuffer.remove(0);
            GameMapExit lastNode = path[path.length - 1];
            int em = lastNode.targetMap;
            int ex = lastNode.targetX;
            int ey = lastNode.targetY;
            
            // ����Ƿ��Ѿ�����Ŀ���ͼ���ҿ���ͨ��Ŀ���
            if (em == tmap && checkPath(tmap, ex, ey, tx, ty)) {
                return path;
            }
            
            // ������ǰһ�����ҳ�Ŀ�곡�������г��ڣ���Ҫ�����ظ�������
            arr = mapLinks.get(em);
            if (arr == null) {
                continue;
            }
            gmi = GameMapInfo.findByID(owner, em);
            if (gmi == null) {
                continue;
            }
            pf = gmi.getPathFinder();
            for (int i = 0; i < arr.length; i++) {
            	if (source.checkCondition(arr[i].constraints) == 0) {
                    continue;
                }
                if (!visited.contains(arr[i]) && pf.canReach(ex, ey, i)) {
                    GameMapExit[] newarr = new GameMapExit[path.length + 1];
                    System.arraycopy(path, 0, newarr, 0, path.length);
                    newarr[path.length] = arr[i];
                    findBuffer.add(newarr);
                    visited.add(arr[i]);
                }
            }
        }
        return null;
    }
    
    
    
    /**
     * ���һ����ͼ��������֮���Ƿ���ͨ�������Ե���ͬһ�����ڣ���
     */
    public boolean checkPath(int map, int sx, int sy, int tx, int ty) {
       GameMapExit[] exits = mapLinks.get(map);
       if (exits == null) {
           return false;
       }
       GameMapInfo gmi = GameMapInfo.findByID(owner, map);
       if (gmi == null) {
           return false;
       }
       PathFinder pf = gmi.getPathFinder();
       for (int i = 0; i < exits.length; i++) {
           if (pf.canReach(sx, sy, i) && pf.canReach(tx, ty, i)) {
               return true;
           }
       }
       return false;
    }
}
