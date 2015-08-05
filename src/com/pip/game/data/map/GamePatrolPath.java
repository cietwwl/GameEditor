package com.pip.game.data.map;

import java.util.Vector;


public class GamePatrolPath extends GameMapObject {
    public Vector<int[]> path = new Vector<int[]>();
    private int pathId;
    
    public GamePatrolPath() {
        
    }
    
    public int getPathId() {
        return pathId;
    }
    
    
}
