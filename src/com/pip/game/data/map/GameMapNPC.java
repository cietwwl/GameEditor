package com.pip.game.data.map;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.pip.game.data.Faction;
import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectData;

/**
 * ��ͼ�е�һ��NPC�������һ��NPC����NPCģ�崴����һ��ʵ����ÿ��ʵ������Ψһ��ID��
 * @author lighthu
 */
public class GameMapNPC extends GameMapObject {
    /** NPCģ�� */
    public NPCTemplate template;
    /** NPC���� */
    public String name;
    /** NPC ��Ӫ */
    public Faction faction;
    /** ��ʼ�Ƿ���� */
    public boolean visible;
    /** �Ƿ��������� */
    public boolean canAttack = true;
    /** ˢ��ʱ��(��)��-1��ʾ��������ˢ�� */
    public int refreshInterval;
    /** �Ƿ���ö�̬ˢ��ʱ�� */
    public boolean dynamicRefresh = true;
    /** ������޾��� */
    public int linkDistance;
    /** �Ƿ������������͹������� */
    public boolean isGuard;
    /** �Ƿ�̬NPC����̬NPCһ���볡���ͻ�ˢ���û� */
    public boolean isStatic;
    /** ����������ڣ�0��ʾ���� */
    public int liveTime;
    /** Ѳ��·�������ö������ɵ�һ����ն�������� */
    public List<int[]> patrolPath = new ArrayList<int[]>();
    /** �Ƿ�����ͨ�������Ϊfalse��������赲����ж� */
    public boolean canPass = true;
    /** �Ƿ���NPC */
    public boolean isFunctional = false;
    /** ����ǹ���NPC��˵���������ƣ����硰���������С����°汾����֧�ַֺŷָ��Ķ������ */
    public String functionName = "";
    /** ����ǹ���NPC��˵���������ܵĽű����°汾֧�ֶ�����س��ָ��� */
    public String functionScript = "";
    /** ȱʡ�԰� */
    public String defaultChat = "";
    /** ������ˢ�µ�NPC��-1��ʾ��ˢ�� */
    public int dieRefresh = -1;
    /** �������Ƿ�㲥 */
    public boolean broadcastDie = false;
    /** Ѱ·���� */
    public String searchName = "";
    /** ���ư汾 */
    public String revision = "";
    
    public List<Period> periods = new ArrayList<Period>();
        
    /** NpcѲ��·��id */
    public int patrolPathId1 = -1;
    public int patrolPathId2 = -1;
    public int patrolPathId3 = -1;
        
    /** ����ǹ����ʾ�����Ĺ�����Id */
    public int monsterGrpId;
        
    /** ����ͬʱ����ս���Ĵ��� */
    public int combatCount = 1;
    
    /** Npc��ײ����(��) */
    public int conlliseDistance;
    
    /** ������λ */
    public long mirrorSet = 1L;
    
    /** 
     * Ĩ���赲�ķ�Χ���Ը���Ϊ��λ�����Ӵ�СΪMapFile.cellSize�����ĸ�Ԫ�طֱ���x��y��w��h�������NPCλ�ã���
     * null��ʾû��Ĩ���赲���ܡ� 
     */
    public int[] antiBlockArea;
    /**
     * �������ƣ������,�ָ���
     */
    public String channel = "";
    
    /**
     * ���ӵ�����Ч�����Ȼ�����
     */
    public String particle1 = "";
    /**
     * ���ӵ�����Ч�����󻭣���
     */
    public String particle2 = "";
    
    public int headImage = -1;//ͷ������ͼ��
    
    public GameMapNPC() {
        
    }
    /**
     * ��Ҫ��������monster
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
     * �õ�NPC��ȫ���ƣ������������ƺ�NPC���ơ�
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
     * ����ID����һ����������֡�
     */
    public static String toStringShort(ProjectData proj, int id) {
        if (id == -1) {
            return "��";
        }
        GameMapObject obj = findByID(proj, id);
        if (obj == null || !(obj instanceof GameMapNPC)) {
            return "δ�ҵ�";
        } else {
            return ((GameMapNPC)obj).name;
        }
    }
    
    public String toFindStr(){
        
        return this.x+","+this.y;
    }
}
