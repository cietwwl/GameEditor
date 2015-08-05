package com.pip.game.data;

import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.PathFinder;
import com.pip.mapeditor.data.GameMap;

public class ProjectDataFactory {
    
    public PathFinder createPathFinder(GameMapInfo gmi) {
        return new PathFinder(gmi);
    }
    
    public PathFinder createPathFinder(GameMapInfo map, GameMap gm) {
        return new PathFinder(map, gm);
    }
}
