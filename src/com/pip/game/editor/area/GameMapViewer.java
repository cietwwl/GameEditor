package com.pip.game.editor.area;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.pip.game.data.MapFormat;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapExit;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.data.map.GameMapPlayer;
import com.pip.game.data.map.GameRelivePoint;
import com.pip.game.data.map.MultiTargetMapExit;
import com.pip.game.data.vehicle.XyGameMapVehicle;
import com.pip.game.editor.ParticleEffectManager;
import com.pip.mango.jni.GLGraphics;
import com.pip.mango.ps.ParticlePlayer;
import com.pip.mapeditor.MapEditor;
import com.pip.mapeditor.MapViewer;
import com.pip.mapeditor.data.GameMap;
import com.pip.mapeditor.data.MapNPC;
import com.pip.mapeditor.data.MapNPCLayer;
import com.pipimage.image.PipAnimate;
import com.pipimage.image.PipAnimateSet;
import com.swtdesigner.SWTResourceManager;

/**
 * 游戏地图编辑器。它从标准地图编辑器MapViewer对象中继承了地图的绘制功能，并额外实现和游戏元素相关的
 * 内容的绘制和编辑。
 * @author lighthu
 */
public class GameMapViewer extends MapViewer {
    protected GameMapInfo mapInfo;
    protected HashMap<File, PipAnimateSet> npcImageCache = new HashMap<File, PipAnimateSet>();
    protected boolean showMapNPC = true;
    protected MapFormat mapFormat;
    protected long mirrorSet = 0xFF;
    protected int[] arrLayerIndex;
    
    protected static class NPCParticle {
        public GameMapNPC npc;
        public int typeIndex;
        public String templateName;
        public ParticlePlayer player;
    }
    protected List<NPCParticle> particles = new ArrayList<NPCParticle>();
    
    public GameMapViewer(Composite parent, int style) {
        super(parent, style);
    }
    
    public void setMirrorSet(long value) {
        mirrorSet = value;
        redraw();
    }
    
    public long getMirrorSet() {
        return mirrorSet;
    }
    
    /**
     * 是否需要显示所有同类型NPC的追击范围和视野范围
     */
    private boolean showAllNpcRange;
    
    /**
     * 设置编辑对象。
     */
    public void setInput(GameMap map, GameMapInfo mapInfo, MapFormat mapFormat) {
        this.mapInfo = mapInfo;
        this.mapFormat = mapFormat;
        setInput(map);
        initArrLayerIndex();
    }
    
    public GameMapInfo getMapInfo() {
        return mapInfo;
    }
    
    public void setShowMapNPC(boolean value) {
        showMapNPC = value;
        redraw();
    }
    
    public void setShowNpcRange(boolean needShow){
        showAllNpcRange = needShow;
    }
    
    public boolean getShowNpcRange(){
        return showAllNpcRange;
    }
    
    public MapFormat getMapFormat() {
        return mapFormat;
    }
    
    /**
     * 检查在当前相位设置下是否能看见某个地图对象。
     * @param obj
     * @return
     */
    public boolean isObjectVisible(GameMapObject obj) {
        if (obj instanceof GameMapNPC) {
            return (mirrorSet & ((GameMapNPC)obj).mirrorSet) != 0;
        } else if (obj instanceof GameMapExit) {
            return (mirrorSet & ((GameMapExit)obj).mirrorSet) != 0;
        } else if (obj instanceof MultiTargetMapExit){
            return (mirrorSet & ((MultiTargetMapExit)obj).mirrorSet) != 0;
        } else {
            return true;
        }
    }
    
    /**
     * 获得NPC视野范围
     * @param npc 当前选中NPC
     * @return
     */
    public Rectangle getEyeShot(GameMapNPC npc) {
    	PipAnimateSet animateSet = getCachedNPCImage(npc);
        Rectangle bounds = animateSet.getAnimate(ProjectData.getActiveProject().getDefaultNPCAnimateIndex(animateSet)).getBounds();
        int eyeShotRange = npc.template.eyeshot;
        Rectangle eyeShot = new Rectangle(bounds.x + bounds.width / 2 - eyeShotRange,
                                          bounds.y + bounds.height / 2 - eyeShotRange,
                                          eyeShotRange * 2, eyeShotRange * 2);
        eyeShot.x += npc.x;
        eyeShot.y += npc.y;
        return eyeShot;
    }
    
    /**
     * 获得NPC追击范围
     * @param npc 当前选中NPC
     * @return
     */
    public Rectangle getChaseDistance(GameMapNPC npc) {
    	PipAnimateSet animateSet = getCachedNPCImage(npc);
        Rectangle bounds = animateSet.getAnimate(ProjectData.getActiveProject().getDefaultNPCAnimateIndex(animateSet)).getBounds();
        int chaseRange = npc.template.chaseDistance;
        Rectangle chaseDis = new Rectangle(bounds.x + bounds.width / 2 - chaseRange,
                                          bounds.y + bounds.height / 2 - chaseRange,
                                          chaseRange * 2, chaseRange * 2);
        chaseDis.x += npc.x;
        chaseDis.y += npc.y;
        return chaseDis;
    }
    
    // 查找已经创建的NPC粒子效果
    protected NPCParticle findNPCParticle(GameMapNPC npc, int typeIndex, String templateName) {
        for (NPCParticle p : particles) {
            if (p.npc == npc && p.typeIndex == typeIndex && p.templateName.equals(templateName)) {
                return p;
            }
        }
        return null;
    }
    
    /**
     * 绘制组件主要编辑内容。
     */
    protected void paintInput(GLGraphics gc) {
        if (input == null) {
            return;
        }

        // 创建所有NPC的附加粒子效果
        List<NPCParticle> newList = new ArrayList<NPCParticle>();
        for (GameMapObject gmo : mapInfo.objects) {
            if (gmo instanceof GameMapNPC) {
                GameMapNPC npc = (GameMapNPC)gmo;
                if (npc.particle1.length() > 0) {
                    NPCParticle p = findNPCParticle(npc, 0, npc.particle1);
                    if (p == null) {
                        // 创建新的
                        p = new NPCParticle();
                        p.npc = npc;
                        p.typeIndex = 0;
                        p.templateName = npc.particle1;
                        p.player = ParticleEffectManager.createPlayer(p.templateName);
                        p.player.setLoop(true);
                        newList.add(p);
                    } else {
                        // 把旧的效果移动到新列表中
                        particles.remove(p);
                        newList.add(p);
                    }
                }
                if (npc.particle2.length() > 0) {
                    NPCParticle p = findNPCParticle(npc, 1, npc.particle2);
                    if (p == null) {
                        // 创建新的
                        p = new NPCParticle();
                        p.npc = npc;
                        p.typeIndex = 1;
                        p.templateName = npc.particle2;
                        p.player = ParticleEffectManager.createPlayer(p.templateName);
                        p.player.setLoop(true);
                        newList.add(p);
                    } else {
                        // 把旧的效果移动到新列表中
                        particles.remove(p);
                        newList.add(p);
                    }
                }
            }
        }
        for (NPCParticle p : particles) {
            p.player.stop();
        }
        particles = newList;
        
        // 调用父类的绘制方法绘制
        super.paintInput(gc);
    }
    
    // 绘制NPC层
    protected void drawNPCLayer(GC gc, MapNPCLayer layer, int offx, int offy, Rectangle visibleRange, List<Rectangle> dirtyList, boolean includeAnimate) {
        int layerIndex = -1;
        for(int i = 0;i<map.layers.size();i++){
            if(layer == map.layers.get(i)){
                layerIndex = arrLayerIndex[i];
                break;
            }
        }
        if(layerIndex ==-1){
            return;
        }
        List<GameMapObject> tmpList = new ArrayList<GameMapObject>();
        for(GameMapObject gmObj:mapInfo.objects){
            if(gmObj.layer == layerIndex){
                tmpList.add(gmObj);
            }
        }
        
        // 人物层，需要加入绘制NPC和出口的方法
        PipAnimateSet animates = map.parent.getAnimates();
        Object[] arr1 = layer.getNpcs().toArray();
        //Object[] arr2 = mapInfo.objects.toArray();
        Object[] arr2 = tmpList.toArray();
        Object[] arr;
        
        if (tempShowNPC != null && layer == map.groundLayer) {
            arr = new Object[arr1.length + arr2.length + 1];
            System.arraycopy(arr1, 0, arr, 0, arr1.length);
            System.arraycopy(arr2, 0, arr, arr1.length, arr2.length);
            arr[arr.length - 1] = tempShowNPC;
        } else {
            arr = new Object[arr1.length + arr2.length];
            System.arraycopy(arr1, 0, arr, 0, arr1.length);
            System.arraycopy(arr2, 0, arr, arr1.length, arr2.length);
        }
        Arrays.sort(arr, new YOrderComparator(mapFormat.scale));
        
        for (Object obj : arr) {
            if (obj instanceof GameMapObject && !isObjectVisible((GameMapObject)obj)) {
                continue;
            }
            if (obj instanceof MapNPC) {
                if (showMapNPC) {
                    MapNPC npc = (MapNPC)obj;
                    super.drawRawNPC(gc, offx, offy, visibleRange, npc, dirtyList, includeAnimate);
                }
            } else if (obj instanceof GameMapNPC) {
                if (!includeAnimate) {
                    continue;
                }
                GameMapNPC npc = (GameMapNPC)obj;
                if (layer == map.groundLayer && npc.layer != 0) {
                    continue;
                }
                if (layer == map.skyLayer && npc.layer != 1) {
                    continue;
                }
                try {
                    File source = npc.template.image.getAnimateFile(mapFormat.aniFormat.id);
                    if (source == null) {
                        source = npc.template.image.getAnimateFile(0);
                    }
                    PipAnimateSet pas = npcImageCache.get(source);
                    if (pas ==null) {
                    	pas =  new PipAnimateSet();
                        pas.load(source);
                        npcImageCache.put(source, pas);
                    }
                    PipAnimate animate =pas.getAnimate(ProjectData.getActiveProject().getDefaultNPCAnimateIndex(pas));
                    
                    // 检查是否在屏幕范围内
                    Rectangle rect = animate.getBounds();
                    rect.x += npc.x * mapFormat.scale;
                    rect.y += npc.y * mapFormat.scale;
                    if (!visibleRange.intersects(rect)) {
                        continue;
                    }
                    if (dirtyList != null) {
                        dirtyList.add(rect);
                    }
                    
                    int rx = (int)(npc.x * ratio * mapFormat.scale) + offx;
                    int ry = (int)(npc.y * ratio * mapFormat.scale) + offy;
                    animate.drawAnimateFrame(gc, getCurrentTime(), rx, ry, getRatio(), MapEditor.imageCache);
                    
                    Rectangle bounds = animate.getBounds();
                    int texty = (int)(ry + bounds.y * ratio);
                    int textx = (int)(rx + (bounds.x + bounds.width / 2) * ratio);
                    Point ts = gc.textExtent(npc.name);
                    gc.setForeground(SWTResourceManager.getColor(0, 0, 0));
//                    gc.drawText(npc.name, textx - ts.x / 2 - 1, texty - 2 - ts.y, true);
//                    gc.drawText(npc.name, textx - ts.x / 2 + 1, texty - 2 - ts.y, true);
//                    gc.drawText(npc.name, textx - ts.x / 2, texty - 2 - ts.y - 1, true);
//                    gc.drawText(npc.name, textx - ts.x / 2, texty - 2 - ts.y + 1, true);
                    gc.drawText(npc.name, textx - ts.x / 2 + 1, texty - 2 - ts.y + 1, true);
                    gc.setForeground(SWTResourceManager.getColor(0xFF, 0xFF, 0xFF));
                    gc.drawText(npc.name, textx - ts.x / 2, texty - 2 - ts.y, true);
                    
                } catch (Exception e) {
                }
            } 
            else if (obj instanceof XyGameMapVehicle) {
                if (!includeAnimate) {
                    continue;
                }
                XyGameMapVehicle npc = (XyGameMapVehicle)obj;
                if (layer == map.groundLayer && npc.layer != 0) {
                    continue;
                }
                if (layer == map.skyLayer && npc.layer != 1) {
                    continue;
                }
                try {
                    File source = npc.template.image.getAnimateFile(mapFormat.aniFormat.id);
                    if (source == null) {
                        source = npc.template.image.getAnimateFile(0);
                    } 
                    PipAnimateSet pas = npcImageCache.get(source);
                    if (pas ==null) {
                    	pas = new PipAnimateSet();
                        pas.load(source);
                        npcImageCache.put(source, pas);
                    }
                    PipAnimate animate = pas.getAnimate(ProjectData.getActiveProject().getDefaultNPCAnimateIndex(pas));
                    
                    // 检查是否在屏幕范围内
                    Rectangle rect = animate.getBounds();
                    rect.x += npc.x * mapFormat.scale;
                    rect.y += npc.y * mapFormat.scale;
                    if (!visibleRange.intersects(rect)) {
                        continue;
                    }
                    if (dirtyList != null) {
                        dirtyList.add(rect);
                    }
                    
                    int rx = (int)(npc.x * ratio * mapFormat.scale) + offx;
                    int ry = (int)(npc.y * ratio * mapFormat.scale) + offy;
                    animate.drawAnimateFrame(gc, currentTime, rx, ry, ratio, MapEditor.imageCache);
                    
                    Rectangle bounds = animate.getBounds();
                    int texty = (int)(ry + bounds.y * ratio);
                    int textx = (int)(rx + (bounds.x + bounds.width / 2) * ratio);
                    Point ts = gc.textExtent(npc.name);
                    gc.setForeground(SWTResourceManager.getColor(0, 0, 0));
//                    gc.drawText(npc.name, textx - ts.x / 2 - 1, texty - 2 - ts.y, true);
//                    gc.drawText(npc.name, textx - ts.x / 2 + 1, texty - 2 - ts.y, true);
//                    gc.drawText(npc.name, textx - ts.x / 2, texty - 2 - ts.y - 1, true);
//                    gc.drawText(npc.name, textx - ts.x / 2, texty - 2 - ts.y + 1, true);
                    gc.drawText(npc.name, textx - ts.x / 2 + 1, texty - 2 - ts.y + 1, true);
                    gc.setForeground(SWTResourceManager.getColor(0xFF, 0xFF, 0xFF));
                    gc.drawText(npc.name, textx - ts.x / 2, texty - 2 - ts.y, true);
                } catch (Exception e) {
                }
            } 
            
            else if (obj instanceof GameMapExit) {
                if (!includeAnimate) {
                    continue;
                }
                GameMapExit exit = (GameMapExit)obj;
                if (layer == map.groundLayer && exit.layer != 0) {
                    continue;
                }
                if (layer == map.skyLayer && exit.layer != 1) {
                    continue;
                }
                Rectangle rect = getExitIcon(exit.layer).getBounds();//.getImageDraw(frame).getBounds(0); 
                Rectangle oldRect = new Rectangle(rect.x, rect.y, rect.width, rect.height);
                rect.x = (int)((exit.x * mapFormat.scale));
                rect.y = (int)((exit.y * mapFormat.scale));
                if (dirtyList != null) {
                    dirtyList.add(rect);
                }
                map2screen(rect);
                
                Point ts = gc.textExtent(exit.name);
                int texty = (int)(rect.y + oldRect.y);
                int textx = (int)((rect.x) );
                
                gc.setForeground(SWTResourceManager.getColor(0, 0, 0));
//                gc.drawText(exit.name, textx - ts.x / 2 - 1, texty - 2 - ts.y, true);
//                gc.drawText(exit.name, textx - ts.x / 2 + 1, texty - 2 - ts.y, true);
//                gc.drawText(exit.name, textx - ts.x / 2, texty - 2 - ts.y - 1, true);
//                gc.drawText(exit.name, textx - ts.x / 2, texty - 2 - ts.y + 1, true);
                gc.drawText(exit.name, textx - ts.x / 2 + 1, texty - 2 - ts.y + 1, true);
                gc.setForeground(SWTResourceManager.getColor(0xFF, 0xFF, 0x00));
                gc.drawText(exit.name, textx - ts.x / 2, texty - 2 - ts.y, true);
                
                getExitIcon(0).drawFrame(gc, getCurrentTime() % getExitIcon(0).getFrameCount(), rect.x, rect.y, ratio, MapEditor.imageCache);
                
//                Image img = getExitIcon(exit.layer).getImageDraw(frame).createSWTImage(gc.getDevice(), 0);
//                gc.drawImage(img, 0, 0, imgSize.width, imgSize.height, rx, ry, rw, rh);
//                img.dispose();
            }
            else if (obj instanceof GameRelivePoint) {
                if (!includeAnimate) {
                    continue;
                }
                GameRelivePoint rp = (GameRelivePoint)obj;
                Rectangle rect = GameRelivePointTool.getImageBounds();  
                rect.x = (int)((rp.x * mapFormat.scale));
                rect.y = (int)((rp.y * mapFormat.scale));
                if (dirtyList != null) {
                    dirtyList.add(rect);
                }
                map2screen(rect);
                GameRelivePointTool.drawRelivePointImage(gc, rect.x, rect.y);
            }
            else if (obj instanceof MultiTargetMapExit) {
                if (!includeAnimate) {
                    continue;
                }
                MultiTargetMapExit mexit = (MultiTargetMapExit)obj;
                if (layer == map.groundLayer && mexit.layer != 0) {
                    continue;
                }
                if (layer == map.skyLayer && mexit.layer != 1) {
                    continue;
                }
                Rectangle rect = getExitIcon(mexit.layer).getBounds();//.getImageDraw(frame).getBounds(0); 
                Rectangle oldRect = new Rectangle(rect.x, rect.y, rect.width, rect.height);
                rect.x = (int)((mexit.x * mapFormat.scale));
                rect.y = (int)((mexit.y * mapFormat.scale));
                if (dirtyList != null) {
                    dirtyList.add(rect);
                }
                map2screen(rect);
                
                Point ts = gc.textExtent(mexit.name);
                int texty = (int)(rect.y + oldRect.y);
                int textx = (int)((rect.x) );
                
                gc.setForeground(SWTResourceManager.getColor(0, 0, 0));
                //gc.drawText("M", rect.x+rect.width-20, rect.y+rect.height-10, true);
//                gc.drawText(exit.name, textx - ts.x / 2 - 1, texty - 2 - ts.y, true);
//                gc.drawText(exit.name, textx - ts.x / 2 + 1, texty - 2 - ts.y, true);
//                gc.drawText(exit.name, textx - ts.x / 2, texty - 2 - ts.y - 1, true);
//                gc.drawText(exit.name, textx - ts.x / 2, texty - 2 - ts.y + 1, true);
                gc.drawText(mexit.name, textx - ts.x / 2 + 1, texty - 2 - ts.y + 1, true);
                gc.setForeground(SWTResourceManager.getColor(0xFF, 0xFF, 0x00));
                gc.drawText(mexit.name, textx - ts.x / 2, texty - 2 - ts.y, true);
                
                getExitIcon(0).drawFrame(gc, getCurrentTime() % getExitIcon(0).getFrameCount(), rect.x, rect.y, ratio, MapEditor.imageCache);
                gc.setForeground(SWTResourceManager.getColor(0xFF, 0, 0));
                gc.drawText("MultiExit", rect.x, rect.y, true);
//                Image img = getExitIcon(exit.layer).getImageDraw(frame).createSWTImage(gc.getDevice(), 0);
//                gc.drawImage(img, 0, 0, imgSize.width, imgSize.height, rx, ry, rw, rh);
//                img.dispose();
            }
        }
        if (layer == map.groundLayer) {
            player.draw(gc, offx, offy, visibleRange, getCurrentTime(), ratio);
        }
    }
    
    // 绘制NPC层
    protected void drawNPCLayer(GLGraphics gc, MapNPCLayer layer, int offx, int offy, Rectangle visibleRange, List<Rectangle> dirtyList, boolean includeAnimate) {
    	 int layerIndex = -1;
         for(int i = 0;i<map.layers.size();i++){
             if(layer == map.layers.get(i)){
                 layerIndex = arrLayerIndex[i];
                 break;
             }
         }
         if(layerIndex ==-1){
             return;
         }
         List<GameMapObject> tmpList = new ArrayList<GameMapObject>();
         for(GameMapObject gmObj:mapInfo.objects){
             if(gmObj.layer == layerIndex){
                 tmpList.add(gmObj);
             }
         }
         
         // 人物层，需要加入绘制NPC和出口的方法
         PipAnimateSet animates = map.parent.getAnimates();
         Object[] arr1 = layer.getNpcs().toArray();
         //Object[] arr2 = mapInfo.objects.toArray();
         Object[] arr2 = tmpList.toArray();
         Object[] arr;
        
        if (tempShowNPC != null && layer == map.groundLayer) {
            arr = new Object[arr1.length + arr2.length + 1];
            System.arraycopy(arr1, 0, arr, 0, arr1.length);
            System.arraycopy(arr2, 0, arr, arr1.length, arr2.length);
            arr[arr.length - 1] = tempShowNPC;
        } else {
            arr = new Object[arr1.length + arr2.length];
            System.arraycopy(arr1, 0, arr, 0, arr1.length);
            System.arraycopy(arr2, 0, arr, arr1.length, arr2.length);
        }
        Arrays.sort(arr, new YOrderComparator(mapFormat.scale));
        
        for (Object obj : arr) {
            if (obj instanceof GameMapObject && !isObjectVisible((GameMapObject)obj)) {
                continue;
            }
            if (obj instanceof MapNPC) {
                if (showMapNPC) {
                    MapNPC npc = (MapNPC)obj;
                    super.drawRawNPC(gc, offx, offy, visibleRange, npc, dirtyList, includeAnimate);
                }
            } else if (obj instanceof GameMapNPC) {
                if (!includeAnimate) {
                    continue;
                }
                GameMapNPC npc = (GameMapNPC)obj;
                if (layer == map.groundLayer && npc.layer != 0) {
                    continue;
                }
                if (layer == map.skyLayer && npc.layer != 1) {
                    continue;
                }
                try {
                    File source = npc.template.image.getAnimateFile(mapFormat.aniFormat.id);
                    if (source == null) {
                        source = npc.template.image.getAnimateFile(0);
                    }
                    PipAnimateSet pas = npcImageCache.get(source);
                    if (pas == null) {
                        pas = new PipAnimateSet();
                        pas.load(source);
                        npcImageCache.put(source, pas);
                    }
                    PipAnimate animate = pas.getAnimate(ProjectData.getActiveProject().getDefaultNPCAnimateIndex(pas));
                    
                    // 检查是否在屏幕范围内
                    Rectangle rect = animate.getBounds();
                    rect.x += npc.x * mapFormat.scale;
                    rect.y += npc.y * mapFormat.scale;
                    if (!visibleRange.intersects(rect)) {
                        continue;
                    }
                    if (dirtyList != null) {
                        dirtyList.add(rect);
                    }
                    
                    int rx = (int)(npc.x * ratio * mapFormat.scale) + offx;
                    int ry = (int)(npc.y * ratio * mapFormat.scale) + offy;
                    if (npc.particle1.length() > 0) {
                        NPCParticle p = findNPCParticle(npc, 0, npc.particle1);
                        if (p != null) {
                            synchronized(p.player.getManager()) {
                                p.player.setPosition(rx, ry);
                                p.player.draw(gc.getHandle(), 0, 0);
                            }
                        }
                    }
                    animate.drawAnimateFrame(gc, getCurrentTime(), rx, ry, ratio, MapEditor.imageCache);
                    if (npc.particle2.length() > 0) {
                        NPCParticle p = findNPCParticle(npc, 1, npc.particle2);
                        if (p != null) {
                            synchronized(p.player.getManager()) {
                                p.player.setPosition(rx, ry);
                                p.player.draw(gc.getHandle(), 0, 0);
                            }
                        }
                    }
                    
                    Rectangle bounds = animate.getBounds();
                    int texty = (int)(ry + bounds.y * ratio);
                    int textx = (int)(rx + (bounds.x + bounds.width / 2) * ratio);
                    Point ts = gc.textExtent(npc.name);
                    gc.setColor(SWTResourceManager.getColor(0, 0, 0));
//                    gc.drawText(npc.name, textx - ts.x / 2 - 1, texty - 2 - ts.y, true);
//                    gc.drawText(npc.name, textx - ts.x / 2 + 1, texty - 2 - ts.y, true);
//                    gc.drawText(npc.name, textx - ts.x / 2, texty - 2 - ts.y - 1, true);
//                    gc.drawText(npc.name, textx - ts.x / 2, texty - 2 - ts.y + 1, true);
                    gc.drawText(npc.name, textx - ts.x / 2 + 1, texty - 2 - ts.y + 1);
                    gc.setColor(SWTResourceManager.getColor(0xFF, 0xFF, 0xFF));
                    gc.drawText(npc.name, textx - ts.x / 2, texty - 2 - ts.y);
                    
                } catch (Exception e) {
                }
            } 
            else if (obj instanceof XyGameMapVehicle) {
                if (!includeAnimate) {
                    continue;
                }
                XyGameMapVehicle npc = (XyGameMapVehicle)obj;
                if (layer == map.groundLayer && npc.layer != 0) {
                    continue;
                }
                if (layer == map.skyLayer && npc.layer != 1) {
                    continue;
                }
                try {
                    File source = npc.template.image.getAnimateFile(mapFormat.aniFormat.id);
                    if (source == null) {
                        source = npc.template.image.getAnimateFile(0);
                    }
                    PipAnimateSet pas = npcImageCache.get(source);
                    if (pas==null) {
                        pas = new PipAnimateSet();
                        pas.load(source);
                        npcImageCache.put(source, pas);
                    }
                    PipAnimate animate = pas.getAnimate(ProjectData.getActiveProject().getDefaultNPCAnimateIndex(pas));
                    
                    // 检查是否在屏幕范围内
                    Rectangle rect = animate.getBounds();
                    rect.x += npc.x * mapFormat.scale;
                    rect.y += npc.y * mapFormat.scale;
                    if (!visibleRange.intersects(rect)) {
                        continue;
                    }
                    if (dirtyList != null) {
                        dirtyList.add(rect);
                    }
                    
                    int rx = (int)(npc.x * ratio * mapFormat.scale) + offx;
                    int ry = (int)(npc.y * ratio * mapFormat.scale) + offy;
                    animate.drawAnimateFrame(gc, getCurrentTime(), rx, ry, ratio, MapEditor.imageCache);
                    
                    Rectangle bounds = animate.getBounds();
                    int texty = (int)(ry + bounds.y * ratio);
                    int textx = (int)(rx + (bounds.x + bounds.width / 2) * ratio);
                    Point ts = gc.textExtent(npc.name);
                    gc.setColor(SWTResourceManager.getColor(0, 0, 0));
//                    gc.drawText(npc.name, textx - ts.x / 2 - 1, texty - 2 - ts.y, true);
//                    gc.drawText(npc.name, textx - ts.x / 2 + 1, texty - 2 - ts.y, true);
//                    gc.drawText(npc.name, textx - ts.x / 2, texty - 2 - ts.y - 1, true);
//                    gc.drawText(npc.name, textx - ts.x / 2, texty - 2 - ts.y + 1, true);
                    gc.drawText(npc.name, textx - ts.x / 2 + 1, texty - 2 - ts.y + 1);
                    gc.setColor(SWTResourceManager.getColor(0xFF, 0xFF, 0xFF));
                    gc.drawText(npc.name, textx - ts.x / 2, texty - 2 - ts.y);
                } catch (Exception e) {
                }
            } 
            
            else if (obj instanceof GameMapExit) {
                if (!includeAnimate) {
                    continue;
                }
                GameMapExit exit = (GameMapExit)obj;
                if (layer == map.groundLayer && exit.layer != 0) {
                    continue;
                }
                if (layer == map.skyLayer && exit.layer != 1) {
                    continue;
                }
                Rectangle rect = getExitIcon(exit.layer).getBounds();//.getImageDraw(frame).getBounds(0); 
                Rectangle oldRect = new Rectangle(rect.x, rect.y, rect.width, rect.height);
                rect.x = (int)((exit.x * mapFormat.scale));
                rect.y = (int)((exit.y * mapFormat.scale));
                if (dirtyList != null) {
                    dirtyList.add(rect);
                }
                map2screen(rect);
                
                Point ts = gc.textExtent(exit.name);
                int texty = (int)(rect.y + oldRect.y);
                int textx = (int)((rect.x) );
                
                gc.setColor(SWTResourceManager.getColor(0, 0, 0));
//                gc.drawText(exit.name, textx - ts.x / 2 - 1, texty - 2 - ts.y, true);
//                gc.drawText(exit.name, textx - ts.x / 2 + 1, texty - 2 - ts.y, true);
//                gc.drawText(exit.name, textx - ts.x / 2, texty - 2 - ts.y - 1, true);
//                gc.drawText(exit.name, textx - ts.x / 2, texty - 2 - ts.y + 1, true);
                gc.drawText(exit.name, textx - ts.x / 2 + 1, texty - 2 - ts.y + 1);
                gc.setColor(SWTResourceManager.getColor(0xFF, 0xFF, 0x00));
                gc.drawText(exit.name, textx - ts.x / 2, texty - 2 - ts.y);
                
                getExitIcon(0).drawFrame(gc, getCurrentTime() % getExitIcon(0).getFrameCount(), rect.x, rect.y, ratio, MapEditor.imageCache);
                
//                Image img = getExitIcon(exit.layer).getImageDraw(frame).createSWTImage(gc.getDevice(), 0);
//                gc.drawImage(img, 0, 0, imgSize.width, imgSize.height, rx, ry, rw, rh);
//                img.dispose();
            }
            else if (obj instanceof MultiTargetMapExit) {
                if (!includeAnimate) {
                    continue;
                }
                MultiTargetMapExit mexit = (MultiTargetMapExit)obj;
                if (layer == map.groundLayer && mexit.layer != 0) {
                    continue;
                }
                if (layer == map.skyLayer && mexit.layer != 1) {
                    continue;
                }
                Rectangle rect = getExitIcon(mexit.layer).getBounds();//.getImageDraw(frame).getBounds(0); 
                Rectangle oldRect = new Rectangle(rect.x, rect.y, rect.width, rect.height);
                rect.x = (int)((mexit.x * mapFormat.scale));
                rect.y = (int)((mexit.y * mapFormat.scale));
                if (dirtyList != null) {
                    dirtyList.add(rect);
                }
                map2screen(rect);
                
                Point ts = gc.textExtent(mexit.name);
                int texty = (int)(rect.y + oldRect.y);
                int textx = (int)((rect.x) );
                
                gc.setColor(SWTResourceManager.getColor(0, 0, 0));
//                gc.drawText(exit.name, textx - ts.x / 2 - 1, texty - 2 - ts.y, true);
//                gc.drawText(exit.name, textx - ts.x / 2 + 1, texty - 2 - ts.y, true);
//                gc.drawText(exit.name, textx - ts.x / 2, texty - 2 - ts.y - 1, true);
//                gc.drawText(exit.name, textx - ts.x / 2, texty - 2 - ts.y + 1, true);
                gc.drawText(mexit.name, textx - ts.x / 2 + 1, texty - 2 - ts.y + 1);
                gc.setColor(SWTResourceManager.getColor(0xFF, 0xFF, 0x00));
                gc.drawText(mexit.name, textx - ts.x / 2, texty - 2 - ts.y);
                
                getExitIcon(0).drawFrame(gc, getCurrentTime() % getExitIcon(0).getFrameCount(), rect.x, rect.y, ratio, MapEditor.imageCache);
                
//                Image img = getExitIcon(exit.layer).getImageDraw(frame).createSWTImage(gc.getDevice(), 0);
//                gc.drawImage(img, 0, 0, imgSize.width, imgSize.height, rx, ry, rw, rh);
//                img.dispose();
            }
            else if (obj instanceof GameRelivePoint) {
                if (!includeAnimate) {
                    continue;
                }
                GameRelivePoint rp = (GameRelivePoint)obj;
                Rectangle rect = GameRelivePointTool.getImageBounds();  
                rect.x = (int)((rp.x * mapFormat.scale));
                rect.y = (int)((rp.y * mapFormat.scale));
                if (dirtyList != null) {
                    dirtyList.add(rect);
                }
                map2screen(rect);
                GameRelivePointTool.drawRelivePointImage(gc, rect.x, rect.y);
            }
        }
        if (layer == map.groundLayer) {
            player.draw(gc, offx, offy, visibleRange, getCurrentTime(), ratio);
        }
    }
    
    /** 地图玩家 */
    public GameMapPlayer player = new GameMapPlayer(this);
    
    /** 检查某个位置是否遮挡 */
    public boolean checkBlock(int x, int y, boolean sky) {
        x = x / map.parent.getCellSize();
        y = y / map.parent.getCellSize();
        byte block = map.tileInfo[y][x];
        System.out.println(">> block " + y + "," + x + " = " + block);
        if (sky) {
            if ((block & 4) == 0) {
                return true;
            }
        } else if ((block & 2) == 0) {
            return true;
        }
        return false;
    }
    
    public PipAnimateSet getCachedImage(GameMapObject gmo) {
        if(gmo instanceof GameMapNPC) {
            return getCachedNPCImage((GameMapNPC)gmo);
        }
        
        return null;

    }
    
    public PipAnimateSet getCachedNPCImage(GameMapNPC npc) {
        File source = npc.template.image.getAnimateFile(mapFormat.aniFormat.id);
        if (source == null) {
            source = npc.template.image.getAnimateFile(0);
        }
        return npcImageCache.get(source);
    }
    
    public PipAnimateSet getCachedVehicleImage(XyGameMapVehicle npc) {
        File source = npc.template.image.getAnimateFile(mapFormat.aniFormat.id);
        if (source == null) {
            source = npc.template.image.getAnimateFile(0);
        }
        return npcImageCache.get(source);
    }
    
    public PipAnimate getExitIcon(int layer) {
    	PipAnimate exitIcon = null;
        try {
            if(layer == 0) { //地面人物层
                if (mapFormat.scale >= 2.0) {
                	exitIcon = mapInfo.owner.owner.config.exitlAni.getAnimate(0);
                } else {
                	exitIcon = mapInfo.owner.owner.config.exitAni.getAnimate(0);
                }                   
            } else { //天空人物层
                if (mapFormat.scale >= 2.0) {
                    exitIcon = mapInfo.owner.owner.config.exitlAni.getAnimate(1);
                } else {
                    exitIcon = mapInfo.owner.owner.config.exitAni.getAnimate(1);
                }    
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exitIcon;
    }

    @Override
    public void widgetDisposed(DisposeEvent evt) {
        super.widgetDisposed(evt);
        for (NPCParticle p : particles) {
            p.player.stop();
        }
        particles.clear();
    }
   
    protected void initArrLayerIndex(){
        if(map==null){
            return;
        }
        int size = map.layers.size();
        arrLayerIndex = new int[size];
        int index = 2;
        for(int i = 0; i < size;i++){
            Object obj = map.layers.get(i);
            if(obj instanceof MapNPCLayer){
                if(obj == map.groundLayer){
                    arrLayerIndex[i] = 0;
                }else if(obj == map.skyLayer){
                    arrLayerIndex[i] = 1;
                }else{
                    arrLayerIndex[i] = index++;
                }
            }else{
                arrLayerIndex[i] = -1;
            }
        }
    }
}
