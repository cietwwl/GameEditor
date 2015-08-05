package com.pip.game.editor.area;

import java.util.Comparator;

import com.pip.game.data.map.*;
import com.pip.mapeditor.data.MapNPC;

/**
 * Y�����˳��Ƚ�����֧�ֵ�ͼNPC��NPC�ͳ���3�ֶ���
 * @author lighthu
 */
public class YOrderComparator implements Comparator<Object> {
    private double scale;
    
    public YOrderComparator(double scale) {
        this.scale = scale;
    }
    
    public int compare(Object o1, Object o2) {
        int y1 = getY(o1);
        int y2 = getY(o2);
        if (y1 < y2) {
            return -1;
        } else if (y1 == y2) {
            return 0;
        } else {
            return 1;
        }
    }
    
    private int getY(Object o) {
        if (o instanceof MapNPC) {
            return ((MapNPC)o).y;
        } else if (o instanceof GameMapObject) {
            return (int)(((GameMapObject)o).y * scale);
        } else {
            return 0;
        }
    }

    public boolean equals(Object obj) {
        return this == obj;
    }
}
