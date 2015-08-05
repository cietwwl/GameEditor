package com.pip.game.editor.area;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;


import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.data.map.GamePatrolPath;
import com.pip.mango.jni.GLGraphics;
import com.pip.mapeditor.MapViewer;
import com.pip.mapeditor.data.GameMap;
import com.pip.mapeditor.tool.IMapEditTool;
import com.swtdesigner.SWTResourceManager;

/**
 * 巡逻路径编辑工具。
 * @author lighthu
 */
public class GamePatrolPathTool2 implements IMapEditTool {
    // 父编辑器
    private GameAreaEditor editor;
    // 附着的编辑器
    private GameMapViewer viewer;
    // 是否正在拖动路点
    private boolean isDragging;
//    // 正在拖动的路点在当前选中游戏对象的运动路径中的索引
    private int draggingPointIndex;
    // 拖动的起始点，以及拖动开始时被拖动路点的位置
    private int[] dragStartPoint;
    // 最近一次检测到的鼠标位置
    private int lastX, lastY;
    
    private GamePatrolPath selectedObject;

    private boolean isPressCtrl;
    
    /**
     * 缺省构造方法
     * @param viewer 编辑器
     * @param tv 贴图查看器
     */
    public GamePatrolPathTool2(GameAreaEditor editor, GameMapViewer viewer) {
        this.editor = editor;
        this.viewer = viewer;
    }

    /**
     * 鼠标按下事件。
     * @param x 鼠标位置在地图中的相对位置（不是屏幕坐标）
     * @param y 鼠标位置在地图中的相对位置（不是屏幕坐标）
     * @param mask 按键状态掩码
     */
    public void mouseDown(int x, int y, int mask) {
        isDragging = false;
        lastX = x;
        lastY = y;
        
        if(isPressCtrl) {
            addPoint(x, y);
        }
        
        // 如果没有点中路点，则判断是否点中了某个巡逻路点
        // 如果找到了点，selectedObject肯定也找到了
        selectedObject = detectObject(x, y);
        draggingPointIndex = detectPoint(x, y);
        dragStartPoint = null;
        double scale = viewer.getMapFormat().scale;
        if(draggingPointIndex != -1) {
            int[] pos = selectedObject.path.get(draggingPointIndex);
            dragStartPoint = new int[] { (int)(pos[0] * scale), (int)(pos[1] * scale) };
        } else if(selectedObject != null) {
            dragStartPoint = new int[]{x, y};
        } else {            
            selectedObject = new GamePatrolPath();
            GameMapInfo mapInfo = viewer.getMapInfo();
            selectedObject.owner = mapInfo;
            selectedObject.id = 0;
            while (true) {
                // 确保ID不重复
                boolean dup = false;
                for (int i = mapInfo.objects.size() - 1; i >= 0; i--) {
                    if (mapInfo.objects.get(i).id == selectedObject.id) {
                        dup = true;
                        break;
                    }
                }
                if (dup) {
                    selectedObject.id++;
                } else {
                    break;
                }
            }
            
            selectedObject.path.add(new int[]{(int)(x / scale), (int)(y / scale)});
            
            dragStartPoint = new int[]{(int)(x / scale), (int)(y / scale)};
            selectedObject.path.add(new int[]{(int)(x / scale), (int)(y / scale)});
            
            viewer.getMapInfo().objects.add(selectedObject);
            draggingPointIndex = 1;
        }
        
        isDragging = true;
        
        viewer.redraw();
    }
    
    // 检查两个点是否在点击误差范围内
    private boolean isMatchPoint(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) < 3 && Math.abs(y1 - y2) < 3;
    }
    
    // 检查某一个路径和路径上的点
    private int detectPoint(int x, int y) {
        double scale = viewer.getMapFormat().scale;
        for (GameMapObject obj : viewer.getMapInfo().objects) {
            if (obj instanceof GamePatrolPath) {
                for(int i=0; i< ((GamePatrolPath)obj).path.size(); i++) {
                    int[] point = ((GamePatrolPath)obj).path.get(i);
                    if(this.isMatchPoint((int)(point[0] * scale), (int)(point[1] * scale), x, y)) {
                        selectedObject = (GamePatrolPath)obj;
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * 鼠标抬起事件。如果当前处于拖动NPC的状态，则这里确定这个NPC的最终坐标。
     * @param x 鼠标位置在地图中的相对位置（不是屏幕坐标）
     * @param y 鼠标位置在地图中的相对位置（不是屏幕坐标）
     * @param mask 按键状态掩码
     */
    public void mouseUp(int x, int y, int mask) {
        lastX = x;
        lastY = y;
        if (isDragging) {
            isDragging = false;
            viewer.fireContentChanged();
            viewer.redraw();
            
            //清理只有两个点的路径，且两个路点坐标相同
            for (int i=0; i<viewer.getMapInfo().objects.size(); i++) {
                GameMapObject obj = viewer.getMapInfo().objects.get(i);
                if (obj instanceof GamePatrolPath) {
                    if(((GamePatrolPath)obj).path.size() == 2) {
                        int[] point1 = ((GamePatrolPath)obj).path.get(0);
                        int[] point2 = ((GamePatrolPath)obj).path.get(1);
                        if(point1[0] == point2[0] || point1[1] == point2[1]) {
                            viewer.getMapInfo().objects.remove(i);
                            selectedObject = null;
                            break;
                        }
                    }
                }
            }
        } else {

        }
        editor.setEditingObject(selectedObject);
    }
    
    /**
     * 鼠标移动事件。被拖动的NPC跟随移动。
     * @param x 鼠标位置在地图中的相对位置（不是屏幕坐标）
     * @param y 鼠标位置在地图中的相对位置（不是屏幕坐标）
     */
    public void mouseMove(int x, int y) {
        double scale = viewer.getMapFormat().scale;
        if(isDragging) {
            if(draggingPointIndex != -1) {
                int[] point = selectedObject.path.get(draggingPointIndex);
                point[0] += (x - lastX) / scale;
                point[1] += (y - lastY) / scale;
                
            } else if(selectedObject != null) {
                for(int[] point : selectedObject.path) {
                    point[0] += (x - lastX) / scale;
                    point[1] += (y - lastY) / scale;
                }
            }
        } else {
            selectedObject = detectObject(x, y);
            draggingPointIndex = detectPoint(x, y);
        }
        
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

        double scale = viewer.getMapFormat().scale;
        for (GameMapObject obj : viewer.getMapInfo().objects) {
            if (obj instanceof GamePatrolPath) {
                
                GamePatrolPath path = (GamePatrolPath)obj;
                
                for (int i = 0; i < path.path.size() - 1; i++) {
                    int[] point1 = path.path.get(i);
                    int[] point2 = path.path.get(i + 1);
                    
                    Rectangle rect = new Rectangle((int)(point1[0] * scale) - 2, (int)(point1[1] * scale) - 2, 5, 5);
                    viewer.map2screen(rect);
                    
                    if(selectedObject == path && draggingPointIndex == i) {
                        gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));                        
                    } else {
                        gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                    }
                    
                    gc.fillRectangle(rect);
                    
                    Point pt1 = new Point((int)(point1[0] * scale), (int)(point1[1] * scale));
                    Point pt2 = new Point((int)(point2[0] * scale), (int)(point2[1] * scale));
                    viewer.map2screen(pt1);
                    viewer.map2screen(pt2);
                    if(selectedObject != obj) {
                        gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                    } else {
                        gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
                    }
                    gc.drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
                }
                
                if(path.path.size() > 0) {
                    int[] point1 = path.path.get(path.path.size() - 1);
                    Rectangle rect = new Rectangle((int)(point1[0] * scale) - 2, (int)(point1[1] * scale) - 2, 5, 5);
                    viewer.map2screen(rect);
                    if(selectedObject == path && draggingPointIndex == path.path.size() - 1) {
                        gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));    
                    } else {
                        gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                    }                    
                    gc.fillRectangle(rect);
                }  
            }
        }        

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

        double scale = viewer.getMapFormat().scale;
        for (GameMapObject obj : viewer.getMapInfo().objects) {
            if (obj instanceof GamePatrolPath) {
                
                GamePatrolPath path = (GamePatrolPath)obj;
                
                for (int i = 0; i < path.path.size() - 1; i++) {
                    int[] point1 = path.path.get(i);
                    int[] point2 = path.path.get(i + 1);
                    
                    Rectangle rect = new Rectangle((int)(point1[0] * scale) - 2, (int)(point1[1] * scale) - 2, 5, 5);
                    viewer.map2screen(rect);
                    
                    if(selectedObject == path && draggingPointIndex == i) {
                        gc.setColor(SWTResourceManager.getColor(SWT.COLOR_RED));                        
                    } else {
                        gc.setColor(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                    }
                    
                    gc.fillRect(rect);
                    
                    Point pt1 = new Point((int)(point1[0] * scale), (int)(point1[1] * scale));
                    Point pt2 = new Point((int)(point2[0] * scale), (int)(point2[1] * scale));
                    viewer.map2screen(pt1);
                    viewer.map2screen(pt2);
                    if(selectedObject != obj) {
                        gc.setColor(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                    } else {
                        gc.setColor(SWTResourceManager.getColor(SWT.COLOR_RED));
                    }
                    gc.drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
                }
                
                if(path.path.size() > 0) {
                    int[] point1 = path.path.get(path.path.size() - 1);
                    Rectangle rect = new Rectangle((int)(point1[0] * scale) - 2, (int)(point1[1] * scale) - 2, 5, 5);
                    viewer.map2screen(rect);
                    if(selectedObject == path && draggingPointIndex == path.path.size() - 1) {
                        gc.setColor(SWTResourceManager.getColor(SWT.COLOR_RED));    
                    } else {
                        gc.setColor(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                    }                    
                    gc.fillRect(rect);
                }  
            }
        }        

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
    public void onKeyDown(int keyCode) {
        switch(keyCode) {
            case SWT.CTRL:
                isPressCtrl = true;
                break;
            case SWT.DEL:
                if(draggingPointIndex != -1) {
                    selectedObject.path.remove(draggingPointIndex);
                }
                if(selectedObject.path.size() == 1) {
                    viewer.getMapInfo().objects.remove(selectedObject);
                }
                viewer.redraw();
                break;
        }
    }

    /**
     * 键松开事件。
     */
    public void onKeyUp(int keyCode) {
        isPressCtrl = false;        
    }

    /**
     * 得到工具右键菜单。
     */
    public Menu getMenu() {
        return null;
    }
    
    //按住ctrl键，添加一个路点
    private void addPoint(int x, int y) {
        double scale = viewer.getMapFormat().scale;
        for (GameMapObject obj : viewer.getMapInfo().objects) {
            if (obj instanceof GamePatrolPath) {
                GamePatrolPath path = (GamePatrolPath)obj;
                for(int i=0; i< path.path.size() - 1; i++) {
                    int[] point1 = path.path.get(i);
                    int[] point2 = path.path.get(i + 1);
                    if(point1[0] == point2[0] && point1[1] == point2[1]) {
                        continue;
                    }
                    if(isNearest(x, y, (int)(point1[0] * scale), (int)(point1[1] * scale), 
                            (int)(point2[0] * scale), (int)(point2[1] * scale), 10)) {
                        path.path.insertElementAt(new int[]{(int)(x/scale), (int)(y/scale)}, i + 1);
                        return;
                    }
                }
            }
        } 
    }

    public GamePatrolPath detectObject(int x, int y) {
        for (GameMapObject obj : viewer.getMapInfo().objects) {
            if (obj instanceof GamePatrolPath) {
                for(int i=0; i< ((GamePatrolPath)obj).path.size() - 1; i++) {
                    int[] point1 = ((GamePatrolPath)obj).path.get(i);
                    int[] point2 = ((GamePatrolPath)obj).path.get(i + 1);
                    if(point1[0] == point2[0] && point1[1] == point2[1]) {
                        continue;
                    }
                    double scale = viewer.getMapFormat().scale;
                    int p11 = (int)(point1[0] * scale);
                    int p12 = (int)(point1[1] * scale);
                    int p21 = (int)(point2[0] * scale);
                    int p22 = (int)(point2[1] * scale);
                    if(isNearest(x, y, p11, p12, p21, p22, 8)) {
                        return ((GamePatrolPath)obj);
                    }
                }
            }
        }
        return null;
    }
    
    public static boolean isNearest(int x, int y, int startX, int startY, int endX, int endY, int nearest) {
        int[] param = makeLineParam(startX, startY, endX, endY);
        int a = param[0];
        int b = param[1];
        int c = param[2];
        double delta = Math.abs( (a*x+b*y+c) / Math.sqrt(x^2+y^2) );
        if(delta < nearest){
            return true;
        }
        
        return false;
        
    }
    
    /**
     * 获得两点确定的直线的标准式参数Ax+By+C = 0; ret[]{A,B,C}
     * @param x
     * @param y
     * @param x2
     * @param y2
     * @return
     */
    public static int[] makeLineParam(int x, int y, int x2, int y2){
        int a = y - y2;
        int b = x2 - x;
        int c = x*y2 - x2*y;
        return new int[]{a,b,c};
    }
}

