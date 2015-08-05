package com.pip.game.editor.area;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Menu;

import com.pip.game.data.Faction;
import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapExit;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.map.GameRelivePoint;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.EditorPlugin;
import com.pip.mango.jni.GLGraphics;
import com.pip.mango.jni.GLUtils;
import com.pip.mapeditor.*;
import com.pip.mapeditor.data.*;
import com.pip.mapeditor.tool.IMapEditTool;
import com.pipimage.image.*;
import com.swtdesigner.SWTResourceManager;

/**
 * 创建地图复活点工具。
 * @author lighthu
 */
public class GameRelivePointTool implements IMapEditTool {
    // 父编辑器
    private GameAreaEditor editor;
    // 附着的编辑器
    private GameMapViewer viewer;
    // 最近一次检测到的鼠标位置
    private int lastX, lastY;
    
    // 复活点图片
    public static PipImage relivePointImage;
    static {
        try {
            relivePointImage = new PipImage();
            relivePointImage.load(ProjectData.getActiveProject().config.getProjectClassLoader().getResourceAsStream("/com/pip/game/editor/area/relivepoint.pip"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 缺省构造方法
     * @param viewer 编辑器
     * @param tv 贴图查看器
     */
    public GameRelivePointTool(GameAreaEditor editor, GameMapViewer viewer) {
        this.editor = editor;
        this.viewer = viewer;
    }

    // 判断一个坐标是否可以放置当前选中的NPC。必须保证NPC有一部分在屏幕内。
    private boolean isValidPos(Point pt) {
        GameMap map = viewer.getMap();
        if (pt.x < 10) {
            return false;
        }
        if (pt.x > map.width - 10) {
            return false;
        }
        if (pt.y < 10) {
            return false;
        }
        if (pt.y > map.height - 10) {
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
            // 创建一个新复活点
            GameMapInfo mapInfo = viewer.getMapInfo();
            GameRelivePoint relivePoint = new GameRelivePoint();
            relivePoint.owner = mapInfo;
            relivePoint.id = 0;
            while (true) {
                // 确保ID不重复
                boolean dup = false;
                for (int i = mapInfo.objects.size() - 1; i >= 0; i--) {
                    if (mapInfo.objects.get(i).id == relivePoint.id) {
                        dup = true;
                        break;
                    }
                }
                if (dup) {
                    relivePoint.id++;
                } else {
                    break;
                }
            }
            relivePoint.x = (int)(x / scale);
            relivePoint.y = (int)(y / scale);
            mapInfo.objects.add(relivePoint);
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
        Rectangle rect = new Rectangle(0, 0, 0, 0);
        rect.x = lastX ;
        rect.y = lastY;
        viewer.map2screen(rect);
        drawRelivePointImage(gc, rect.x, rect.y);
        
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
        Rectangle rect = new Rectangle(0, 0, 0, 0);
        rect.x = lastX ;
        rect.y = lastY;
        viewer.map2screen(rect);
        drawRelivePointImage(gc, rect.x, rect.y);
        
        // 绘制座标
        String coordStr = lastX + "," + lastY + "," + (lastX / map.parent.getCellSize()) + "," + (lastY / map.parent.getCellSize());
        Point size = viewer.getSize();
        Point ts = gc.textExtent(coordStr);
        gc.setColor(MapViewer.invert(viewer.getBackground()));
        gc.drawRect(size.x - ts.x - 9, size.y - ts.y - 8, ts.x + 7, ts.y + 6);
        gc.drawText(coordStr, size.x - ts.x - 5, size.y - ts.y - 5);
    }
    
    public static void drawRelivePointImage(GC gc, int x, int y) {
        int fw = relivePointImage.getImageData(0).width;
        int fh = relivePointImage.getImageData(0).height;
        relivePointImage.getImageDraw(0).draw(gc, x - fw / 2, y - fh + 20, 0);
    }
    
    public static void drawRelivePointImage(GLGraphics gc, int x, int y) {
        int fw = relivePointImage.getImageData(0).width;
        int fh = relivePointImage.getImageData(0).height;
        Image img = MapEditor.imageCache.get(relivePointImage, 0, 0);
        if (img == null) {
            img = relivePointImage.getImageDraw(0).createSWTImage(null, 0);
            MapEditor.imageCache.add(relivePointImage, 0, 0, img);
        }
        gc.drawTexture(GLUtils.loadImage(img), 0, 0, x - fw / 2, y - fh + 20);
    }
    
    public static Rectangle getImageBounds() {
        int fw = relivePointImage.getImageData(0).width;
        int fh = relivePointImage.getImageData(0).height;
        return new Rectangle(-fw / 2, -fh + 20, fw, fh);
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
    }
}
