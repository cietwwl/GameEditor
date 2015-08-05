package com.pip.game.editor.area;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;


import com.pip.data.EntitySpriteInfo;
import com.pip.data.SpriteAnimation;
import com.pip.data.SpriteInfo;
import com.pip.game.data.GameMesh;
import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapExit;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.data.map.GameMapPlayer;
import com.pip.game.data.map.GameRelivePoint;
import com.pip.game.data.map.MultiTargetMapExit;
import com.pip.game.data.vehicle.XyGameMapVehicle;
import com.pip.mango.jni.GLGraphics;
import com.pip.mapeditor.MapEditor;
import com.pip.mapeditor.MapViewer;
import com.pip.mapeditor.data.GameMap;
import com.pip.mapeditor.tool.IMapEditTool;
import com.pipimage.image.PipAnimate;
import com.pipimage.image.PipAnimateSet;
import com.swtdesigner.SWTResourceManager;

/**
 * 拾取工具。这个工具让用户可以选择当前显示的地图中的游戏地图元素，包括NPC和出口。还可以通过鼠标进行拖动。
 * @author lighthu
 */
public class GamePickupTool implements IMapEditTool {
    // 父编辑器
    private GameAreaEditor editor;
    // 附着的编辑器
    protected GameMapViewer viewer;
    // 当前选中的游戏对象，null表示没有
    protected GameMapObject selectedObject;
    // 是否正在拖动
    protected boolean isDragging;
    // 是否按下shift拖动
    protected boolean shiftDragging;
    // 拖动的起始点，以及拖动开始时被拖动NPC的位置
    protected Point dragStartPoint, dragStartPos;
    // 最近一次检测到的鼠标位置
    protected int lastX, lastY;
    // 正在移动NPC的按键
    protected int movingKeyCode;
    // 重复按键次数
    private int repeatKeyCount;

    /**
     * 缺省构造方法
     * @param viewer 编辑器
     * @param tv 贴图查看器
     */
    public GamePickupTool(GameAreaEditor editor, GameMapViewer viewer) {
        this.editor = editor;
        this.viewer = viewer;
    }
    
    /**
     * 清除选择。
     */
    public void clearSelection() {
        selectedObject = null;
        editor.setEditingObject(null);
    }
    
    // 检查某一位置的对象
    protected GameMapObject detectObject(int x, int y) {
        for (GameMapObject obj : viewer.getMapInfo().objects) {
            try{
                if (viewer.isObjectVisible(obj) && getObjectBounds(obj).contains(x, y)) {
                    return obj;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return viewer.player.detect(x, y);
    }
    
    // 取得一个地图对象的外框。
    protected Rectangle getObjectBounds(GameMapObject obj) {
        double scale = viewer.getMapFormat().scale;
        if (obj instanceof GameMapExit) {
            GameMapExit exit = (GameMapExit)obj;
            Rectangle bounds = viewer.getExitIcon(exit.layer).getBounds();//.getImageDraw(0).getBounds(0);
            bounds.x += exit.x * scale;
            bounds.y += exit.y * scale;
            return bounds;
        } else if (obj instanceof GameMapNPC) {
            GameMapNPC npc = (GameMapNPC)obj;
            Rectangle bounds = viewer.getCachedNPCImage(npc).getBounds(0, ((GameMesh)npc.template.image).getMeshConfig().getScalar());
            bounds.x += npc.x * scale;
            bounds.y += npc.y * scale;
            return bounds;
        } else if(obj instanceof XyGameMapVehicle){
            XyGameMapVehicle npc = (XyGameMapVehicle)obj;
            Rectangle bounds = viewer.getCachedVehicleImage(npc).getBounds(0, ((GameMesh)npc.template.image).getMeshConfig().getScalar());
            bounds.x += npc.x * scale;
            bounds.y += npc.y * scale;
            return bounds;
        } else if (obj instanceof GameRelivePoint) {
            GameRelivePoint rp = (GameRelivePoint)obj;
            Rectangle bounds = GameRelivePointTool.getImageBounds();
            bounds.x += rp.x * scale;
            bounds.y += rp.y * scale;
            return bounds;
        } else if (obj instanceof MultiTargetMapExit){
            MultiTargetMapExit mexit = (MultiTargetMapExit)obj;
            Rectangle bounds = viewer.getExitIcon(mexit.layer).getBounds();//.getImageDraw(0).getBounds(0);
            bounds.x += mexit.x * scale;
            bounds.y += mexit.y * scale;
            return bounds;
        }
        return new Rectangle(0, 0, 0, 0);
    }
    
    /**
     * 鼠标按下事件。
     * 选择工具从最顶上的地图层开始向下扫描，如果选中了某个NPC，开始拖动这个NPC；如果选中了某个贴图，则更新
     * 贴图查看器中当前选中贴图。
     * @param x 鼠标位置在地图中的相对位置（不是屏幕坐标）
     * @param y 鼠标位置在地图中的相对位置（不是屏幕坐标）
     * @param mask 按键状态掩码
     */
    public void mouseDown(int x, int y, int mask) {
        isDragging = false;
        shiftDragging = false;
        selectedObject = detectObject(x, y);
        if (selectedObject != null) {
            // 如果按下SHIFT拖动NPC，则复制NPC
            if (selectedObject instanceof GameMapNPC && (mask & SWT.SHIFT) != 0) {
                GameMapNPC newnpc = null;
                if(ProjectData.getActiveProject().config.gameMapNpcClass != null && ProjectData.getActiveProject().config.gameMapNpcClass.trim().length() > 0){
                    try {
                        String className = ProjectData.getActiveProject().config.gameMapNpcClass.trim();
                        ProjectConfig config = ProjectData.getActiveProject().config;
                        Class clzz = config.getProjectClassLoader().loadClass(className);
                        newnpc = (GameMapNPC) clzz.newInstance();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    newnpc = new GameMapNPC();
                }
                
                GameMapNPC oldnpc = (GameMapNPC)selectedObject;
                newnpc.owner = oldnpc.owner;
                newnpc.id = 0;
                while (true) {
                    // 确保ID不重复
                    boolean dup = false;
                    for (int i = viewer.getMapInfo().objects.size() - 1; i >= 0; i--) {
                        if (viewer.getMapInfo().objects.get(i).id == newnpc.id) {
                            dup = true;
                            break;
                        }
                    }
                    if (dup) {
                        newnpc.id++;
                    } else {
                        break;
                    }
                }
                newnpc.x = oldnpc.x;
                newnpc.y = oldnpc.y;
                newnpc.template = oldnpc.template;
                newnpc.name = oldnpc.name;
                newnpc.faction = oldnpc.faction;
                newnpc.visible = oldnpc.visible;
                newnpc.canAttack = oldnpc.canAttack;
                newnpc.refreshInterval = oldnpc.refreshInterval;
                newnpc.dynamicRefresh = oldnpc.dynamicRefresh;
                newnpc.linkDistance = oldnpc.linkDistance;
                newnpc.isGuard = oldnpc.isGuard;
                newnpc.isStatic = oldnpc.isStatic;
                for (int[] pt : oldnpc.patrolPath) {
                    newnpc.patrolPath.add(new int[] { pt[0], pt[1] });;
                }
                newnpc.canPass = oldnpc.canPass;
                newnpc.isFunctional = oldnpc.isFunctional;
                newnpc.functionName = oldnpc.functionName;
                newnpc.functionScript = oldnpc.functionScript;
                selectedObject = newnpc;
                shiftDragging = true;
            }
            isDragging = true;
            dragStartPoint = new Point(x, y);
            double scale = viewer.getMapFormat().scale;
            dragStartPos = new Point((int)(selectedObject.x * scale), (int)(selectedObject.y * scale));
            
            // 从对象列表中临时删除，放开鼠标时再加入
            for (int i = viewer.getMapInfo().objects.size() - 1; i >= 0; i--) {
                if (viewer.getMapInfo().objects.get(i) == selectedObject) {
                    viewer.getMapInfo().objects.remove(i);
                    break;
                }
            }
            
            viewer.redraw();
        }
    }
    
    /**
     * 鼠标抬起事件。如果当前处于拖动NPC的状态，则这里确定这个NPC的最终坐标。
     * @param x 鼠标位置在地图中的相对位置（不是屏幕坐标）
     * @param y 鼠标位置在地图中的相对位置（不是屏幕坐标）
     * @param mask 按键状态掩码
     */
    public void mouseUp(int x, int y, int mask) {
        double scale = viewer.getMapFormat().scale;
        if (isDragging) {
            isDragging = false;
            selectedObject.x = (int)((dragStartPos.x + x - dragStartPoint.x) / scale);
            selectedObject.y = (int)((dragStartPos.y + y - dragStartPoint.y) / scale);
            normalize(selectedObject);
            if (selectedObject instanceof GameMapPlayer) {
                viewer.redraw();
                return;
            }
            if (!shiftDragging) {
                viewer.getMapInfo().objects.add(selectedObject);
                if (x != dragStartPoint.x || y != dragStartPoint.y) {
                    viewer.fireContentChanged();
                    viewer.redraw();
                } else {
                    viewer.redraw();
                }
            } else if ((mask & SWT.SHIFT) != 0) {
                if (x != dragStartPoint.x || y != dragStartPoint.y) {
                    viewer.getMapInfo().objects.add(selectedObject);
                    viewer.fireContentChanged();
                    viewer.redraw();
                } else {
                    selectedObject = null;
                    viewer.redraw();
                }
            } else {
                selectedObject = null;
                viewer.redraw();
            }
        }
        if (!(selectedObject instanceof GameMapPlayer)) {
            editor.setEditingObject(selectedObject);
        }
    }
    
    /**
     * 鼠标移动事件。被拖动的NPC跟随移动。
     * @param x 鼠标位置在地图中的相对位置（不是屏幕坐标）
     * @param y 鼠标位置在地图中的相对位置（不是屏幕坐标）
     */
    public void mouseMove(int x, int y) {
        lastX = x;
        lastY = y;
        double scale = viewer.getMapFormat().scale;
        if (isDragging) {
            selectedObject.x = (int)((dragStartPos.x + x - dragStartPoint.x) / scale);
            selectedObject.y = (int)((dragStartPos.y + y - dragStartPoint.y) / scale);
            normalize(selectedObject);
        }
        viewer.redraw();
    }
    
    // 调整NPC位置以保证NPC有一部分在屏幕内。
    private void normalize(GameMapObject obj) {
        GameMap map = viewer.getMap();
        Rectangle bounds = this.getObjectBounds(obj);
        if (bounds.x + bounds.width - 3 < 0) {
            obj.x += 3 - bounds.x - bounds.width;
        } else if (bounds.x - map.width + 3 > 0) {
            obj.x -= bounds.x - map.width + 3;
        }
        if (bounds.y + bounds.height - 3 < 0) {
            obj.y += 3 - bounds.y - bounds.height;
        } else if (bounds.y - map.height + 3 > 0) {
            obj.y -= bounds.y - map.height + 3;
        }
    }
    
    /**
     * 绘制当前工具
     * @param gc
     */
    public void draw(GC gc) {
        GameMap map = viewer.getMap();
        double scale = viewer.getMapFormat().scale;
        if (isDragging || movingKeyCode != 0) {
            // 绘制被拖动的对象
            if (selectedObject instanceof GameMapNPC) {
                GameMapNPC npc = (GameMapNPC)selectedObject;
                SpriteInfo info = viewer.getCachedNPCImage(npc);
                SpriteAnimation animate = viewer.getCachedNPCImage(npc).getAnimation(0);
                
                Point pt = new Point((int)(npc.x * scale), (int)(npc.y * scale));
                viewer.map2screen(pt);
                if(info instanceof PipAnimateSet){
                    ((PipAnimate)animate).drawAnimateFrame(gc, viewer.getCurrentTime(), pt.x, pt.y, viewer.getRatio(), null);
                }else if(info instanceof EntitySpriteInfo){
                    EntitySpriteInfo info2 = (EntitySpriteInfo)info;
                    info2.getPlayer().setPosition(pt.x, pt.y);
//                    ((MeshConfig)info).setPosition2D(pt.x, pt.y);
//                    ((MeshConfig)info).drawInMap(null);
//                    info2.getPlayer().draw(gc, x, y);
                }
                
                
                //绘制视野范围
                Rectangle eyeShot = viewer.getEyeShot(npc);
                viewer.map2screen(eyeShot);
                gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
                gc.drawRectangle(eyeShot);
                //绘追击范围
                Rectangle chaseDis = viewer.getChaseDistance(npc);
                viewer.map2screen(chaseDis);
                if(chaseDis.equals(eyeShot)){
                    chaseDis = new Rectangle((int)(eyeShot.x * scale) - 1, (int)(eyeShot.y * scale) - 1, 
                            (int)(eyeShot.width * scale) + 2, (int)(eyeShot.height * scale) + 2);
                }
                gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
                gc.drawRectangle(chaseDis);
            } else if (selectedObject instanceof GameMapExit) {
                GameMapExit exit = (GameMapExit)selectedObject;
//                Rectangle rect = viewer.getExitIcon(exit.layer).getBounds();//.getImageDraw(0).getBounds(0);
                Rectangle rect = new Rectangle(0, 0, 0, 0);
//                int ow = imgSize.width;
//                int oh = imgSize.height;
                rect.x = (int)(exit.x * scale);
                rect.y = (int)(exit.y * scale);
                viewer.map2screen(rect);
//                int frame = (viewer.getCurrentTime() % 10) / 5;
//                Image img = viewer.getExitIcon(exit.layer).getImageDraw(frame).createSWTImage(gc.getDevice(), 0);
//                gc.drawImage(img, 0, 0, ow, oh, imgSize.x, imgSize.y, imgSize.width, imgSize.height);
//                img.dispose();
                viewer.getExitIcon(exit.layer).drawFrame(gc, viewer.getCurrentTime() % viewer.getExitIcon(exit.layer).getFrameCount(), rect.x, rect.y, 1.0);
            } else if (selectedObject instanceof GameRelivePoint) {
                GameRelivePoint rp = (GameRelivePoint)selectedObject;
                Rectangle rect = new Rectangle(0, 0, 0, 0);
                rect.x = (int)(rp.x * scale);
                rect.y = (int)(rp.y * scale);
                viewer.map2screen(rect);
                GameRelivePointTool.drawRelivePointImage(gc, rect.x, rect.y);
            } else if (selectedObject instanceof MultiTargetMapExit){
                MultiTargetMapExit mexit = (MultiTargetMapExit)selectedObject;
//              Rectangle rect = viewer.getExitIcon(exit.layer).getBounds();//.getImageDraw(0).getBounds(0);
                Rectangle rect = new Rectangle(0, 0, 0, 0);
//                int ow = imgSize.width;
//                int oh = imgSize.height;
                rect.x = (int)(mexit.x * scale);
                rect.y = (int)(mexit.y * scale);
                viewer.map2screen(rect);
//                int frame = (viewer.getCurrentTime() % 10) / 5;
//                Image img = viewer.getExitIcon(exit.layer).getImageDraw(frame).createSWTImage(gc.getDevice(), 0);
//                gc.drawImage(img, 0, 0, ow, oh, imgSize.x, imgSize.y, imgSize.width, imgSize.height);
//                img.dispose();
                viewer.getExitIcon(mexit.layer).drawFrame(gc, viewer.getCurrentTime() % viewer.getExitIcon(mexit.layer).getFrameCount(), rect.x, rect.y, 1.0);
            }
            
            // 绘制NPC外框
            Rectangle bounds = getObjectBounds(selectedObject);
            viewer.map2screen(bounds);
            gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
            gc.drawRectangle(bounds);
        } else {
            // 绘制选中的NPC的外框
            if (selectedObject != null) {
                Rectangle bounds = getObjectBounds(selectedObject);
                viewer.map2screen(bounds);
                gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
                gc.drawRectangle(bounds);
                
                if(selectedObject instanceof GameMapNPC){
                    GameMapNPC npc = (GameMapNPC)selectedObject;
                    
                    //绘制视野范围
                    Rectangle eyeShot = viewer.getEyeShot(npc);
                    viewer.map2screen(eyeShot);
                    gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
                    gc.drawRectangle(eyeShot);
                    //绘追击范围
                    Rectangle chaseDis = viewer.getChaseDistance(npc);
                    viewer.map2screen(chaseDis);
                    if(chaseDis.equals(eyeShot)){
                        chaseDis = new Rectangle((int)(eyeShot.x * scale) - 1, (int)(eyeShot.y * scale) - 1, 
                                (int)(eyeShot.width * scale) + 2, (int)(eyeShot.height * scale) + 2);
                    }
                    gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
                    gc.drawRectangle(chaseDis);
                }
            }
            
            // 查找当前鼠标位置上的对象，并画框
            GameMapObject clickedObject = detectObject(lastX, lastY);
            Rectangle bounds = null;
            if (clickedObject == null) {
                // 啥都没点中，显示一个Tile大小的框
//                int cx = lastX / map.parent.getTileWidth();
//                int cy = lastY / map.parent.getTileHeight();
//                int tw = map.parent.getTileWidth();
//                int th = map.parent.getTileHeight();
//                bounds = new Rectangle(cx * tw, cy * th, tw, th);
            } else {
                // 点中NPC
                bounds = getObjectBounds(clickedObject);
                viewer.map2screen(bounds);
                gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
                gc.drawRectangle(bounds);
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
        if (isDragging || movingKeyCode != 0) {
            // 绘制被拖动的对象
            if (selectedObject instanceof GameMapNPC) {
                GameMapNPC npc = (GameMapNPC)selectedObject;
                SpriteInfo info = viewer.getCachedNPCImage(npc);
                SpriteAnimation animate = viewer.getCachedNPCImage(npc).getAnimation(0);
                
                Point pt = new Point((int)(npc.x * scale), (int)(npc.y * scale));
                viewer.map2screen(pt);
                if(info instanceof PipAnimateSet){
                    ((PipAnimate)animate).drawAnimateFrame(gc, viewer.getCurrentTime(), pt.x, pt.y, viewer.getRatio(), MapEditor.imageCache);
                }else if(info instanceof EntitySpriteInfo){
                    EntitySpriteInfo info2 = (EntitySpriteInfo)info;
//                    ((MeshConfig)info).setPosition2D(pt.x, pt.y);
//                    ((MeshConfig)info).drawInMap(gc);
                    info2.getPlayer().draw(gc.getHandle(), pt.x, pt.y);
                }
                
                
                //绘制视野范围
                Rectangle eyeShot = viewer.getEyeShot(npc);
                viewer.map2screen(eyeShot);
                gc.setColor(SWTResourceManager.getColor(SWT.COLOR_GREEN));
                gc.drawRect(eyeShot);
                //绘追击范围
                Rectangle chaseDis = viewer.getChaseDistance(npc);
                viewer.map2screen(chaseDis);
                if(chaseDis.equals(eyeShot)){
                    chaseDis = new Rectangle((int)(eyeShot.x * scale) - 1, (int)(eyeShot.y * scale) - 1, 
                            (int)(eyeShot.width * scale) + 2, (int)(eyeShot.height * scale) + 2);
                }
                gc.setColor(SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
                gc.drawRect(chaseDis);
            } else if (selectedObject instanceof GameMapExit) {
                GameMapExit exit = (GameMapExit)selectedObject;
//                Rectangle rect = viewer.getExitIcon(exit.layer).getBounds();//.getImageDraw(0).getBounds(0);
                Rectangle rect = new Rectangle(0, 0, 0, 0);
//                int ow = imgSize.width;
//                int oh = imgSize.height;
                rect.x = (int)(exit.x * scale);
                rect.y = (int)(exit.y * scale);
                viewer.map2screen(rect);
//                int frame = (viewer.getCurrentTime() % 10) / 5;
//                Image img = viewer.getExitIcon(exit.layer).getImageDraw(frame).createSWTImage(gc.getDevice(), 0);
//                gc.drawImage(img, 0, 0, ow, oh, imgSize.x, imgSize.y, imgSize.width, imgSize.height);
//                img.dispose();
                viewer.getExitIcon(exit.layer).drawFrame(gc, viewer.getCurrentTime() % viewer.getExitIcon(exit.layer).getFrameCount(), rect.x, rect.y, 1.0, MapEditor.imageCache);
            } else if (selectedObject instanceof GameRelivePoint) {
                GameRelivePoint rp = (GameRelivePoint)selectedObject;
                Rectangle rect = new Rectangle(0, 0, 0, 0);
                rect.x = (int)(rp.x * scale);
                rect.y = (int)(rp.y * scale);
                viewer.map2screen(rect);
                GameRelivePointTool.drawRelivePointImage(gc, rect.x, rect.y);
            }else if (selectedObject instanceof MultiTargetMapExit) {
                MultiTargetMapExit mexit = (MultiTargetMapExit)selectedObject;
//              Rectangle rect = viewer.getExitIcon(exit.layer).getBounds();//.getImageDraw(0).getBounds(0);
                Rectangle rect = new Rectangle(0, 0, 0, 0);
//              int ow = imgSize.width;
//              int oh = imgSize.height;
                rect.x = (int)(mexit.x * scale);
                rect.y = (int)(mexit.y * scale);
                viewer.map2screen(rect);
//              int frame = (viewer.getCurrentTime() % 10) / 5;
//              Image img = viewer.getExitIcon(exit.layer).getImageDraw(frame).createSWTImage(gc.getDevice(), 0);
//              gc.drawImage(img, 0, 0, ow, oh, imgSize.x, imgSize.y, imgSize.width, imgSize.height);
//              img.dispose();
                viewer.getExitIcon(mexit.layer).drawFrame(gc, viewer.getCurrentTime() % viewer.getExitIcon(mexit.layer).getFrameCount(), rect.x, rect.y, 1.0, MapEditor.imageCache);
            }
            
            // 绘制NPC外框
            Rectangle bounds = getObjectBounds(selectedObject);
            viewer.map2screen(bounds);
            gc.setColor(SWTResourceManager.getColor(SWT.COLOR_RED));
            gc.drawRect(bounds);
        } else {
            // 绘制选中的NPC的外框
            if (selectedObject != null) {
                Rectangle bounds = getObjectBounds(selectedObject);
                viewer.map2screen(bounds);
                gc.setColor(SWTResourceManager.getColor(SWT.COLOR_BLUE));
                gc.drawRect(bounds);
                
                if(selectedObject instanceof GameMapNPC){
                    GameMapNPC npc = (GameMapNPC)selectedObject;
                    
                    //绘制视野范围
                    Rectangle eyeShot = viewer.getEyeShot(npc);
                    viewer.map2screen(eyeShot);
                    gc.setColor(SWTResourceManager.getColor(SWT.COLOR_GREEN));
                    gc.drawRect(eyeShot);
                    //绘追击范围
                    Rectangle chaseDis = viewer.getChaseDistance(npc);
                    viewer.map2screen(chaseDis);
                    if(chaseDis.equals(eyeShot)){
                        chaseDis = new Rectangle((int)(eyeShot.x * scale) - 1, (int)(eyeShot.y * scale) - 1, 
                                (int)(eyeShot.width * scale) + 2, (int)(eyeShot.height * scale) + 2);
                    }
                    gc.setColor(SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
                    gc.drawRect(chaseDis);
                }
            }
            
            // 查找当前鼠标位置上的对象，并画框
            GameMapObject clickedObject = detectObject(lastX, lastY);
            Rectangle bounds = null;
            if (clickedObject == null) {
                // 啥都没点中，显示一个Tile大小的框
//                int cx = lastX / map.parent.getTileWidth();
//                int cy = lastY / map.parent.getTileHeight();
//                int tw = map.parent.getTileWidth();
//                int th = map.parent.getTileHeight();
//                bounds = new Rectangle(cx * tw, cy * th, tw, th);
            } else {
//                if(clickedObject instanceof GameMapNPC){
//                    GameMapNPC npc = (GameMapNPC)clickedObject;
//                    if(viewer.getCachedNPCImage(npc) instanceof MeshConfig){
//                        Rectangle bounds2 = viewer.getCachedNPCImage(npc).getBounds(0);
//                        bounds2.x += npc.x * scale;
//                        bounds2.y += npc.y * scale;
//                        int a = 0;
//                    }
//                }
                // 点中NPC
                bounds = getObjectBounds(clickedObject);
                viewer.map2screen(bounds);
                gc.setColor(SWTResourceManager.getColor(SWT.COLOR_RED));
                gc.drawRect(bounds);
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
    
    /*
     * 查找当前选中的对象在对象列表中的索引
     */
    private int getSelectedObjectIndex() {
        for (int i = viewer.getMapInfo().objects.size() - 1; i >= 0; i--) {
            if (viewer.getMapInfo().objects.get(i) == selectedObject) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * 键按下事件。响应：删除，上下左右
     */
    public void onKeyDown(int keyCode) {
        if (!isDragging && selectedObject != null) {
            if (selectedObject instanceof GameMapPlayer) {
                ((GameMapPlayer)selectedObject).onKeyDown(keyCode);
                viewer.refresh();
                return;
            }
            if (movingKeyCode != 0 && keyCode != movingKeyCode) {
                // 如果正在移动NPC，则忽略其他按键
                return;
            }
            if (keyCode == SWT.DEL) {
                // 删除键，删除选中NPC
                int index = getSelectedObjectIndex();
                viewer.getMapInfo().objects.remove(index);
                selectedObject = null;
                editor.setEditingObject(null);
                viewer.fireContentChanged();
                viewer.refresh();
            } else if (keyCode == SWT.ARROW_UP || keyCode == SWT.ARROW_DOWN || 
                    keyCode == SWT.ARROW_LEFT || keyCode == SWT.ARROW_RIGHT) {
                boolean isStart = false;
                int step;
                if (movingKeyCode == 0) {
                    isStart = true;
                    repeatKeyCount = 1;
                    step = 1;
                } else {
                    repeatKeyCount++;
                    step = repeatKeyCount / 3 + 1;
                }
                movingKeyCode = keyCode;
                switch (keyCode) {
                case SWT.ARROW_UP:
                    selectedObject.y -= step;
                    break;
                case SWT.ARROW_DOWN:
                    selectedObject.y += step;
                    break;
                case SWT.ARROW_LEFT:
                    selectedObject.x -= step;
                    break;
                case SWT.ARROW_RIGHT:
                    selectedObject.x += step;
                    break;
                }
                normalize(selectedObject);
                
                // 暂时把选中NPC从列表中删除
                if (isStart) {
                    int index = getSelectedObjectIndex();
                    viewer.getMapInfo().objects.remove(index);
                }
                viewer.refresh();
            }
        }
    }

    /**
     * 键松开事件。
     */
    public void onKeyUp(int keyCode) {
        if (movingKeyCode != 0 && keyCode == movingKeyCode) {
            movingKeyCode = 0;
            viewer.getMapInfo().objects.add(selectedObject);
            viewer.fireContentChanged();
            viewer.refresh();
        }
    }

    /**
     * 得到工具右键菜单。
     */
    public Menu getMenu() {
        return null;
    }
}
