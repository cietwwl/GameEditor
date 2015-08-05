package com.pip.game.editor.area;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;

import com.pip.mango.jni.GLGraphics;
import scryer.ogre.mesh.MeshConfig;

import com.pip.data.EntitySpriteInfo;
import com.pip.data.SpriteAnimation;
import com.pip.data.SpriteInfo;
import com.pip.game.data.GameMesh;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.map.GameMapObject;
import com.pip.mapeditor.MapEditor;
import com.pip.mapeditor.MapViewer;
import com.pip.mapeditor.data.GameMap;
import com.pip.mapeditor.tool.IMapEditTool;
import com.pipimage.image.PipAnimate;
import com.pipimage.image.PipAnimateSet;
import com.swtdesigner.SWTResourceManager;

/**
 * Ѳ��·���༭���ߡ�
 * @author lighthu
 */
public class GamePatrolPathTool implements IMapEditTool {
    // ���༭��
    private GameAreaEditor editor;
    // ���ŵı༭��
    private GameMapViewer viewer;
    // ��ǰѡ�е���Ϸ����null��ʾû��
    private GameMapNPC selectedObject;
    // �Ƿ������϶�·��
    private boolean isDragging;
    // �����϶���·���ڵ�ǰѡ����Ϸ������˶�·���е�������-1��ʾ�϶����壨������·�㣩��-2��ʾ�϶�NPC
    private int draggingPointIndex;
    // �϶�����ʼ�㣬�Լ��϶���ʼʱ���϶�·���λ��
    private Point dragStartPoint, dragStartPos;
    // ���һ�μ�⵽�����λ��
    private int lastX, lastY;

    /**
     * ȱʡ���췽��
     * @param viewer �༭��
     * @param tv ��ͼ�鿴��
     */
    public GamePatrolPathTool(GameAreaEditor editor, GameMapViewer viewer) {
        this.editor = editor;
        this.viewer = viewer;
    }

    /**
     * ��갴���¼���
     * @param x ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
     * @param y ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
     * @param mask ����״̬����
     */
    public void mouseDown(int x, int y, int mask) {
        isDragging = false;
        lastX = x;
        lastY = y;
        
        // ���ж��Ƿ������ĳ��·��
        if (selectedObject != null) {
            int pointIndex = detectPoint(x, y);
            if (pointIndex != -3) {
                isDragging = true;
                draggingPointIndex = pointIndex;
                dragStartPoint = new Point(x, y);
                if (pointIndex == -1 || pointIndex == -2) {
                    dragStartPos = new Point(selectedObject.x, selectedObject.y);
                } else {
                    int[] roadPoint = selectedObject.patrolPath.get(pointIndex);
                    dragStartPos = new Point(roadPoint[0] + selectedObject.x, roadPoint[1] + selectedObject.y);
                }
                if (pointIndex == -2) {
                    // �Ӷ����б�����ʱɾ�����ſ����ʱ�ټ���
                    for (int i = viewer.getMapInfo().objects.size() - 1; i >= 0; i--) {
                        if (viewer.getMapInfo().objects.get(i) == selectedObject) {
                            viewer.getMapInfo().objects.remove(i);
                            break;
                        }
                    }
                }
                viewer.redraw();
                return;
            }
        }
        
        // ���û�е���·�㣬���ж��Ƿ������ĳ��NPC
        selectedObject = detectObject(x, y);
        viewer.redraw();
    }
    
    // ����������Ƿ��ڵ����Χ��
    private boolean isMatchPoint(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) < 3 && Math.abs(y1 - y2) < 3;
    }
    
    // ���ĳһλ�õ�·��
    private int detectPoint(int x, int y) {
        // �ж��Ƿ���б���
        if (isMatchPoint(x, y, selectedObject.x, selectedObject.y)) {
            return -1;
        }
        
        // �����ж�ÿ��·���Ƿ񱻵���
        for (int i = 0; i < selectedObject.patrolPath.size(); i++) {
            int[] roadPoint = selectedObject.patrolPath.get(i);
            if (isMatchPoint(x, y, roadPoint[0] + selectedObject.x, roadPoint[1] + selectedObject.y)) {
                return i;
            }
        }
        
        // �ж��Ƿ����ѡ�е�NPC
        if (getObjectBounds(selectedObject).contains(x, y)) {
            return -2;
        }
        return -3;
    }
    
    // ���ĳһλ�õĶ���
    private GameMapNPC detectObject(int x, int y) {
        for (GameMapObject obj : viewer.getMapInfo().objects) {
            if (obj instanceof GameMapNPC && getObjectBounds(obj).contains(x, y)) {
                return (GameMapNPC)obj;
            }
        }
        return null;
    }
    
    // ȡ��һ����ͼ��������
    private Rectangle getObjectBounds(GameMapObject obj) {
        if (obj instanceof GameMapNPC) {
            GameMapNPC npc = (GameMapNPC)obj;
            Rectangle bounds = viewer.getCachedNPCImage(npc).getBounds(0, ((GameMesh)npc.template.image).getMeshConfig().getScalar());
            bounds.x += npc.x;
            bounds.y += npc.y;
            return bounds;
        }
        return new Rectangle(0, 0, 0, 0);
    }

    // ����NPCλ���Ա�֤NPC��һ��������Ļ�ڡ�
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
     * ���̧���¼��������ǰ�����϶�NPC��״̬��������ȷ�����NPC���������ꡣ
     * @param x ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
     * @param y ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
     * @param mask ����״̬����
     */
    public void mouseUp(int x, int y, int mask) {
        lastX = x;
        lastY = y;
        if (isDragging) {
            isDragging = false;
            int newx = dragStartPos.x + x - dragStartPoint.x - selectedObject.x;
            int newy = dragStartPos.y + y - dragStartPoint.y - selectedObject.y;
            
            boolean modified = false;
            if (draggingPointIndex == -2) {
                // ����϶�����NPC��������NPCλ�ú����¼���
                moveNPC(selectedObject, dragStartPos.x + x - dragStartPoint.x, dragStartPos.y + y - dragStartPoint.y);
                viewer.getMapInfo().objects.add(selectedObject);
                modified = true;
            } else if (draggingPointIndex == -1) {
                // ����϶����Ǳ��壬�����µ�λ�ò��ڱ��巶Χ�ڣ�������һ���µ�·��
                if (!isMatchPoint(newx, newy, 0, 0)) {
                    selectedObject.patrolPath.add(new int[] { newx, newy });
                    modified = true;
                }
            } else {
                // ����϶�������ͨ·�㣬���޸�·��λ�á����·�����λ���ڱ��巶Χ�ڣ���ɾ�����·��
                int[] oldPoint = selectedObject.patrolPath.get(draggingPointIndex);
                if (isMatchPoint(newx, newy, 0, 0)) {
                    selectedObject.patrolPath.remove(draggingPointIndex);
                    modified = true;
                } else if (oldPoint[0] != newx || oldPoint[1] != newy) {
                    oldPoint[0] = newx;
                    oldPoint[1] = newy;
                    modified = true;
                }
            }
            if (modified) {
                viewer.fireContentChanged();
            }
            viewer.redraw();
        }
    }
    
    /**
     * ����ƶ��¼������϶���NPC�����ƶ���
     * @param x ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
     * @param y ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
     */
    public void mouseMove(int x, int y) {
        lastX = x;
        lastY = y;
        if (isDragging && draggingPointIndex == -2) {
            moveNPC(selectedObject, dragStartPos.x + x - dragStartPoint.x, dragStartPos.y + y - dragStartPoint.y);
        }
        viewer.redraw();
    }
    
    /**
     * �ƶ�NPCλ�ã�������·��λ�ò��䡣
     */
    private void moveNPC(GameMapNPC npc, int x, int y) {
        int oldx = npc.x;
        int oldy = npc.y;
        npc.x = x;
        npc.y = y;
        normalize(npc);
        int xoff = x - oldx;
        int yoff = y - oldy;
        for (int[] point : npc.patrolPath) {
            point[0] -= xoff;
            point[1] -= yoff;
        }
    }
    
    /**
     * ���Ƶ�ǰ����
     * @param gc
     */
    public void draw(GC gc) {
        GameMap map = viewer.getMap();

        if (selectedObject != null) {
            // ��������϶�NPC���Ȼ��ƴ�ѡ��NPC
            if (isDragging && draggingPointIndex == -2) {
                PipAnimate animate = (PipAnimate)viewer.getCachedImage(selectedObject).getAnimation(0);
                Point pt = new Point(selectedObject.x, selectedObject.y);
                viewer.map2screen(pt);
                animate.drawAnimateFrame(gc, viewer.getCurrentTime(), pt.x, pt.y, viewer.getRatio(), null);
            }
            
            // ����ѡ�е�NPC�����
            Rectangle bounds = getObjectBounds(selectedObject);
            viewer.map2screen(bounds);
            gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
            gc.drawRectangle(bounds);
            
            // ����ѡ�е�NPC��Ѳ��·��
            List<int[]> path = new ArrayList<int[]>();
            path.add(new int[] { 0, 0 });
            path.addAll(selectedObject.patrolPath);
            path.add(new int[] { 0, 0 });
            int baseX = selectedObject.x;
            int baseY = selectedObject.y;
            for (int i = 0; i < path.size() - 1; i++) {
                int[] point1 = path.get(i);
                int[] point2 = path.get(i + 1);
                
                Rectangle rect = new Rectangle(point1[0] - 2 + baseX, point1[1] - 2 + baseY, 5, 5);
                viewer.map2screen(rect);
                gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                gc.fillRectangle(rect);
                
                Point pt1 = new Point(point1[0] + baseX, point1[1] + baseY);
                Point pt2 = new Point(point2[0] + baseX, point2[1] + baseY);
                viewer.map2screen(pt1);
                viewer.map2screen(pt2);
                gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                gc.drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
            }
        }
        
        
        if (isDragging) {
            // ���Ƶ�ǰ�϶�λ��
            int newx = dragStartPos.x + lastX - dragStartPoint.x;
            int newy = dragStartPos.y + lastY - dragStartPoint.y;
            Rectangle rect = new Rectangle(newx - 2, newy - 2, 5, 5);
            viewer.map2screen(rect);
            gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
            gc.fillRectangle(rect);
        } else {
            // û�����϶�״̬ʱ���������Ƶ�ǰ���λ���ϵĶ���
            
            // ���ж��Ƿ������ĳ��·��
            Point pt = null;
            if (selectedObject != null) {
                int pointIndex = detectPoint(lastX, lastY);
                if (pointIndex != -2 && pointIndex != -3) {
                    if (pointIndex == -1) {
                        pt = new Point(selectedObject.x, selectedObject.y);
                    } else {
                        int[] roadPoint = selectedObject.patrolPath.get(pointIndex);
                        pt = new Point(roadPoint[0] + selectedObject.x, roadPoint[1] + selectedObject.y);
                    }
                }
            }
            if (pt != null) {
                Rectangle rect = new Rectangle(pt.x - 2, pt.y - 2, 5, 5);
                viewer.map2screen(rect);
                gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
                gc.fillRectangle(rect);
            } else {
                // ���û�е���·�㣬���ж��Ƿ������ĳ��NPC
                GameMapObject npc = detectObject(lastX, lastY);
                if (npc != null) {
                    // ����NPC���
                    Rectangle bounds = getObjectBounds(npc);
                    viewer.map2screen(bounds);
                    gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
                    gc.drawRectangle(bounds);
                }
            }
        }

        // ��������
        String coordStr = lastX + "," + lastY + "," + (lastX / map.parent.getCellSize()) + "," + (lastY / map.parent.getCellSize());
        Point size = viewer.getSize();
        Point ts = gc.textExtent(coordStr);
        gc.setForeground(MapViewer.invert(viewer.getBackground()));
        gc.setBackground(viewer.getBackground());
        gc.drawRectangle(size.x - ts.x - 9, size.y - ts.y - 8, ts.x + 7, ts.y + 6);
        gc.drawText(coordStr, size.x - ts.x - 5, size.y - ts.y - 5);
    }
    
    /**
     * ���Ƶ�ǰ����
     * @param gc
     */
    public void draw(GLGraphics gc) {
        GameMap map = viewer.getMap();

        if (selectedObject != null) {
            // ��������϶�NPC���Ȼ��ƴ�ѡ��NPC
            if (isDragging && draggingPointIndex == -2) {
                SpriteInfo info = viewer.getCachedImage(selectedObject);
                SpriteAnimation animate = viewer.getCachedImage(selectedObject).getAnimation(0);
                Point pt = new Point(selectedObject.x, selectedObject.y);
                viewer.map2screen(pt);
                if(info instanceof PipAnimateSet){
                    ((PipAnimate)animate).drawAnimateFrame(gc, viewer.getCurrentTime(), pt.x, pt.y, viewer.getRatio(), MapEditor.imageCache);
                }else if(info instanceof EntitySpriteInfo){
                    EntitySpriteInfo info2 = (EntitySpriteInfo)info;
//                    ((MeshConfig)info).setPosition2D(pt.x, pt.y);
                    info2.getPlayer().draw(gc.getHandle(), pt.x, pt.y);
//                    ((MeshConfig)info).drawInMap(gc);
                }
            }
            
            // ����ѡ�е�NPC�����
            Rectangle bounds = getObjectBounds(selectedObject);
            viewer.map2screen(bounds);
            gc.setColor(SWTResourceManager.getColor(SWT.COLOR_BLUE));
            gc.drawRect(bounds);
            
            // ����ѡ�е�NPC��Ѳ��·��
            List<int[]> path = new ArrayList<int[]>();
            path.add(new int[] { 0, 0 });
            path.addAll(selectedObject.patrolPath);
            path.add(new int[] { 0, 0 });
            int baseX = selectedObject.x;
            int baseY = selectedObject.y;
            for (int i = 0; i < path.size() - 1; i++) {
                int[] point1 = path.get(i);
                int[] point2 = path.get(i + 1);
                
                Rectangle rect = new Rectangle(point1[0] - 2 + baseX, point1[1] - 2 + baseY, 5, 5);
                viewer.map2screen(rect);
                gc.setColor(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                gc.fillRect(rect);
                
                Point pt1 = new Point(point1[0] + baseX, point1[1] + baseY);
                Point pt2 = new Point(point2[0] + baseX, point2[1] + baseY);
                viewer.map2screen(pt1);
                viewer.map2screen(pt2);
                gc.setColor(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                gc.drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
            }
        }
        
        
        if (isDragging) {
            // ���Ƶ�ǰ�϶�λ��
            int newx = dragStartPos.x + lastX - dragStartPoint.x;
            int newy = dragStartPos.y + lastY - dragStartPoint.y;
            Rectangle rect = new Rectangle(newx - 2, newy - 2, 5, 5);
            viewer.map2screen(rect);
            gc.setColor(SWTResourceManager.getColor(SWT.COLOR_RED));
            gc.fillRect(rect);
        } else {
            // û�����϶�״̬ʱ���������Ƶ�ǰ���λ���ϵĶ���
            
            // ���ж��Ƿ������ĳ��·��
            Point pt = null;
            if (selectedObject != null) {
                int pointIndex = detectPoint(lastX, lastY);
                if (pointIndex != -2 && pointIndex != -3) {
                    if (pointIndex == -1) {
                        pt = new Point(selectedObject.x, selectedObject.y);
                    } else {
                        int[] roadPoint = selectedObject.patrolPath.get(pointIndex);
                        pt = new Point(roadPoint[0] + selectedObject.x, roadPoint[1] + selectedObject.y);
                    }
                }
            }
            if (pt != null) {
                Rectangle rect = new Rectangle(pt.x - 2, pt.y - 2, 5, 5);
                viewer.map2screen(rect);
                gc.setColor(SWTResourceManager.getColor(SWT.COLOR_RED));
                gc.fillRect(rect);
            } else {
                // ���û�е���·�㣬���ж��Ƿ������ĳ��NPC
                GameMapObject npc = detectObject(lastX, lastY);
                if (npc != null) {
                    // ����NPC���
                    Rectangle bounds = getObjectBounds(npc);
                    viewer.map2screen(bounds);
                    gc.setColor(SWTResourceManager.getColor(SWT.COLOR_RED));
                    gc.drawRect(bounds);
                }
            }
        }

        // ��������
        String coordStr = lastX + "," + lastY + "," + (lastX / map.parent.getCellSize()) + "," + (lastY / map.parent.getCellSize());
        Point size = viewer.getSize();
        Point ts = gc.textExtent(coordStr);
        gc.setColor(MapViewer.invert(viewer.getBackground()));
        gc.drawRect(size.x - ts.x - 9, size.y - ts.y - 8, ts.x + 7, ts.y + 6);
        gc.drawText(coordStr, size.x - ts.x - 5, size.y - ts.y - 5);
    }
    
    /**
     * �������¼���
     */
    public void onKeyDown(int keyCode) {}

    /**
     * ���ɿ��¼���
     */
    public void onKeyUp(int keyCode) {}

    /**
     * �õ������Ҽ��˵���
     */
    public Menu getMenu() {
        return null;
    }

    public void mouseDoubleClick(int x, int y) {
        // TODO Auto-generated method stub
        
    }
}

