package com.pip.game.editor.area;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.map.GameMapObject;
import com.pip.mapeditor.MapViewer;
import com.pip.mapeditor.data.GameMap;
import com.pip.mapeditor.tool.TileConfigTool;
import com.swtdesigner.SWTResourceManager;

/**
 * 游戏地图阻挡设置工具，和Imageworkshop中的TileConfigTool继承，并实现了抹除阻挡的功能。
 */
public class GameTileConfigTool extends TileConfigTool {
    /**
     * 缺省构造方法
     * @param viewer 编辑器
     * @param mask 
     * @param tv 贴图查看器
     */
    public GameTileConfigTool(MapViewer viewer, byte mask, boolean reverse, String hint) {
        super(viewer, mask, reverse, hint);
    }

    /**
     * 绘制当前工具
     * @param gc
     */
    public void draw(GC gc) {
    	super.draw(gc);
    	
    	// 被NPC抹除的阻挡区域绘制为蓝色
    	drawAntiBlock(gc);
    }
    
    private void drawAntiBlock(GC gc) {
        GameMap map = viewer.getMap();
    	GameMapInfo mapInfo = ((GameMapViewer)viewer).getMapInfo();
    	gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
        gc.setLineStyle(SWT.LINE_SOLID);
        int cellSize = map.parent.getCellSize();
    	for (GameMapObject gmo : mapInfo.objects) {
    	    if (!(gmo instanceof GameMapNPC)) {
    	        continue;
    	    }
    	    GameMapNPC npc = (GameMapNPC)gmo;
    	    if (npc.antiBlockArea != null) {
    	        for (int x = npc.antiBlockArea[0]; x < npc.antiBlockArea[0] + npc.antiBlockArea[2]; x++) {
    	            for (int y = npc.antiBlockArea[1]; y < npc.antiBlockArea[1] + npc.antiBlockArea[3]; y++) {
    	                int xx = npc.x / cellSize + x;
    	                int yy = npc.y / cellSize + y;
    	                Rectangle rect = new Rectangle(xx * cellSize, yy * cellSize, cellSize, cellSize);
                        viewer.map2screen(rect);
                        gc.drawRectangle(rect);
    	            }
    	        }
    	    }
    	}
	}
}
