package com.pip.game.editor.area;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Menu;

import com.pip.game.data.Faction;
import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapExit;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.EditorPlugin;
import com.pip.mango.jni.GLGraphics;
import com.pip.mapeditor.*;
import com.pip.mapeditor.data.*;
import com.pip.mapeditor.tool.IMapEditTool;
import com.pipimage.image.*;
import com.swtdesigner.SWTResourceManager;

/**
 * 创建地图出口工具。
 * @author lighthu
 */
public class GameMapExitTool implements IMapEditTool {
    // 父编辑器
    private GameAreaEditor editor;
    // 附着的编辑器
    protected GameMapViewer viewer;
    // 最近一次检测到的鼠标位置
    private int lastX, lastY;

    /**
     * 缺省构造方法
     * @param viewer 编辑器
     * @param tv 贴图查看器
     */
    public GameMapExitTool(GameAreaEditor editor, GameMapViewer viewer) {
        this.editor = editor;
        this.viewer = viewer;
    }

    // 判断一个坐标是否可以放置当前选中的NPC。必须保证NPC有一部分在屏幕内。
    protected boolean isValidPos(Point pt) {
        GameMap map = viewer.getMap();
        Rectangle bounds = viewer.getExitIcon(0).getBounds();
        bounds.x += pt.x;
        bounds.y += pt.y;
        if (bounds.x - 3 < 0) {
            return false;
        } else if (bounds.x + bounds.width- map.width + 3 > 0) {
            return false;
        }
        if (bounds.y - 3 < 0) {
            return false;
        } else if (bounds.y + bounds.height - map.height + 3 > 0) {
            return false;
        }
        return true;
    }

    /**
     * 鼠标按下事件。开始拖动绘制。
     * 贴图查看器中当前选中贴图。
     * @param x 鼠标位置在地图中的相对位置（不是屏幕坐标）
     * @param y 鼠标位置在地图中的相对位置（不是屏幕坐标）
     * @param mask 按键状态掩码
     */
    public void mouseDown(int x, int y, int mask) {
    }

    /**
     * 鼠标抬起事件。拖动绘制结束，把拖动过的点都设置为选中的Tile。
     * @param x 鼠标位置在地图中的相对位置（不是屏幕坐标）
     * @param y 鼠标位置在地图中的相对位置（不是屏幕坐标）
     * @param mask 按键状态掩码
     */
    public void mouseUp(int x, int y, int mask) {
        Point pt = new Point(x, y);
        double scale = viewer.getMapFormat().scale;
        if (isValidPos(pt)) {
            // 创建一个新出口
            GameMapInfo mapInfo = viewer.getMapInfo();
            GameMapExit exit = new GameMapExit();
            exit.owner = mapInfo;
            exit.id = 0;
            while (true) {
                // 确保ID不重复
                boolean dup = false;
                for (int i = mapInfo.objects.size() - 1; i >= 0; i--) {
                    if (mapInfo.objects.get(i).id == exit.id) {
                        dup = true;
                        break;
                    }
                }
                if (dup) {
                    exit.id++;
                } else {
                    break;
                }
            }
            exit.x = (int)(x / scale);
            exit.y = (int)(y / scale);
            exit.targetMap = 0;
            exit.targetX = 0;
            exit.targetY = 0;
            mapInfo.objects.add(exit);
            viewer.fireContentChanged();
            viewer.redraw();
        }
    }

    /**
     * 鼠标移动事件。拖动刷子。
     * @param x 鼠标位置在地图中的相对位置（不是屏幕坐标）
     * @param y 鼠标位置在地图中的相对位置（不是屏幕坐标）
     */
    public void mouseMove(int x, int y) {
        lastX = x;
        lastY = y;
        viewer.redraw();
    }

    /**
     * 绘制当前工具
     * @param gc
     */
    public void draw(GC gc) {
        GameMap map = viewer.getMap();
        
        // 绘制NPC
//        Rectangle rect = viewer.getExitIcon(0).getBounds();
        Rectangle rect = new Rectangle(0, 0, 0, 0);
        rect.x = lastX ;
        rect.y = lastY;
        viewer.map2screen(rect);
        
        viewer.getExitIcon(0).drawFrame(gc, viewer.getCurrentTime() % viewer.getExitIcon(0).getFrameCount(), rect.x, rect.y, 1.0);
        
//        int frame = (viewer.getCurrentTime() % 10) / 5;
//        Image img = viewer.getExitIcon(0).getFrame(0).realize().createSWTImage(gc.getDevice(), 0);
//        gc.drawImage(img, 0, 0, ow, oh, rect.x, rect.y, rect.width, rect.height);
//        img.dispose();
        
        // 绘制座标
        String coordStr = lastX + "," + lastY + "," + (lastX / map.parent.getCellSize()) + "," + (lastY / map.parent.getCellSize());
        Point size = viewer.getSize();
        Point ts = gc.textExtent(coordStr);
        gc.setForeground(MapViewer.invert(viewer.getBackground()));
        gc.setBackground(viewer.getBackground());
        gc.drawRectangle(size.x - ts.x - 9, size.y - ts.y - 8, ts.x + 7, ts.y + 6);
        gc.drawText(coordStr, size.x - ts.x - 5, size.y - ts.y - 5);
    }
    
    /**
     * 绘制当前工具
     * @param gc
     */
    public void draw(GLGraphics gc) {
        GameMap map = viewer.getMap();
        
        // 绘制NPC
//        Rectangle rect = viewer.getExitIcon(0).getBounds();
        Rectangle rect = new Rectangle(0, 0, 0, 0);
        rect.x = lastX ;
        rect.y = lastY;
        viewer.map2screen(rect);
        
        viewer.getExitIcon(0).drawFrame(gc, viewer.getCurrentTime() % viewer.getExitIcon(0).getFrameCount(), rect.x, rect.y, 1.0, MapEditor.imageCache);
        
//        int frame = (viewer.getCurrentTime() % 10) / 5;
//        Image img = viewer.getExitIcon(0).getFrame(0).realize().createSWTImage(gc.getDevice(), 0);
//        gc.drawImage(img, 0, 0, ow, oh, rect.x, rect.y, rect.width, rect.height);
//        img.dispose();
        
        // 绘制座标
        String coordStr = lastX + "," + lastY + "," + (lastX / map.parent.getCellSize()) + "," + (lastY / map.parent.getCellSize());
        Point size = viewer.getSize();
        Point ts = gc.textExtent(coordStr);
        gc.setColor(MapViewer.invert(viewer.getBackground()));
        gc.drawRect(size.x - ts.x - 9, size.y - ts.y - 8, ts.x + 7, ts.y + 6);
        gc.drawText(coordStr, size.x - ts.x - 5, size.y - ts.y - 5);
    }

    /**
     * 键按下事件。
     */
    public void onKeyDown(int keyCode) {}

    /**
     * 键松开事件。
     */
    public void onKeyUp(int keyCode) {}

    /**
     * 得到工具右键菜单。
     */
    public Menu getMenu() {
        return null;
    }

    public void mouseDoubleClick(int x, int y) {
        // TODO Auto-generated method stub
        
    }
}
