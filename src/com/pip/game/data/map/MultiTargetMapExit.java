package com.pip.game.data.map;

import java.util.ArrayList;
import java.util.List;

import com.pip.game.data.Faction;
/**
 * ��Ŀ�괫�ͳ���<br>
 * ���Ŀ��λ��ֻ��һ������ôֱ�ӹ����������һ�����򵯳��б�<br>
 * @author think
 *
 */
public class MultiTargetMapExit extends GameMapObject {

    public String name = "";
    public Faction faction = new Faction();
    public List<GameMapExit> exitList = new ArrayList<GameMapExit>();
    
    /** ������λ */
    public long mirrorSet = 1L;
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<exitList.size();i++){
            if(i<exitList.size() - 1){
                sb.append(exitList.get(i).name).append(",");
            }else{
                sb.append(exitList.get(i).name);
            }
        }
        return sb.toString();
    }
    
    public String toMapIds(){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<exitList.size();i++){
            if(i<exitList.size() - 1){
                sb.append(exitList.get(i).targetMap).append(",");
            }else{
                sb.append(exitList.get(i).targetMap);
            }
        }
        return sb.toString();
    }
}
