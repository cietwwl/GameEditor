package com.pip.game.editor.area;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;

import com.pip.game.data.ProjectData;
import com.pip.game.data.map.PathFinder;
import com.pip.game.editor.EditorPlugin;
import com.pip.mango.jni.GLGraphics;
import com.pip.mango.jni.GLUtils;
import com.pip.mapeditor.tool.IMapEditTool;
import com.swtdesigner.SWTResourceManager;

/**
 * ���Թ���Ѱ·�㷨�Ĺ��ߡ�
 * @author lighthu
 */
public class PathFinderTool implements IMapEditTool {
    // ���༭��
    private GameAreaEditor editor;
    // ���ŵı༭��
    private GameMapViewer viewer;
    // ·�����ҹ���
    private PathFinder pathFinder;
    // �Ƿ������϶�
    private boolean dragging;
    // �Ƿ������϶����
    private boolean draggingStartPos;
    // ·���������յ�
    private Point startPos, endPos;
    // ��ǰ�ҳ���·���ĵ�
    private List<Point> path;

    /**
     * ȱʡ���췽��
     * @param viewer �༭��
     * @param tv ��ͼ�鿴��
     */
    public PathFinderTool(GameAreaEditor editor, GameMapViewer viewer) {
        this.editor = editor;
        this.viewer = viewer;
        long time1 = System.currentTimeMillis();
        try {
            pathFinder = ProjectData.projDataFactory.createPathFinder(viewer.getMapInfo(), viewer.getMap());
            pathFinder.buildPathBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("Create PathFinder: " + (System.currentTimeMillis() - time1) + "ms");
    }
    
    /*
     * ���㵱ǰѡ�е������յ�֮���·�������浽��Ա����path�С�
     */
    protected void computePath() {
        if (startPos == null || endPos == null) {
            path = null;
            return;
        }
        long time1 = System.currentTimeMillis();
//        for (int i = 0; i < 10000; i++) {
            path = new ArrayList<Point>();
            int curx = startPos.x;
            int cury = startPos.y;
            path.add(new Point(curx, cury));
            int xdist = Math.abs(curx - endPos.x);
            int ydist = Math.abs(cury - endPos.y);
            while (path.size() < 100 && (xdist + ydist) > 16) {
                int[] ret = pathFinder.findPath(curx, cury, endPos.x, endPos.y);
                curx = ret[0];
                cury = ret[1];
                path.add(new Point(curx, cury));
                xdist = Math.abs(curx - endPos.x);
                ydist = Math.abs(cury - endPos.y);
            }
//        }
//        System.out.println("Find Path: " + (System.currentTimeMillis() - time1) + "ms, " + (path.size() - 1) * 10000 + " times");
    }
    
    /**
     * ��갴���¼���
     * @param x ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
     * @param y ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
     * @param mask ����״̬����
     */
    public void mouseDown(int x, int y, int mask) {
        Point pt = new Point(x, y);
        if (pt.x < 0) {
            pt.x = 0;
        }
        if (pt.x >= viewer.getMap().width) {
            pt.x = viewer.getMap().width - 1; 
        }
        if (pt.y < 0) {
            pt.y = 0;
        }
        if (pt.y >= viewer.getMap().height) {
            pt.y = viewer.getMap().height - 1;
        }
        if ((mask & SWT.SHIFT) != 0) {
            endPos = pt;
        } else {
            startPos = pt;
        }
        dragging = true;
        draggingStartPos = (mask & SWT.SHIFT) == 0; 
        computePath();
        viewer.redraw();
    }
    
    /**
     * ���̧���¼��������ǰ�����϶�NPC��״̬��������ȷ�����NPC���������ꡣ
     * @param x ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
     * @param y ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
     * @param mask ����״̬����
     */
    public void mouseUp(int x, int y, int mask) {
        dragging = false;
    }
    
    /**
     * ����ƶ��¼������϶���NPC�����ƶ���
     * @param x ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
     * @param y ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
     */
    public void mouseMove(int x, int y) {
        if (dragging) {
            Point pt = new Point(x, y);
            if (pt.x < 0) {
                pt.x = 0;
            }
            if (pt.x >= viewer.getMap().width) {
                pt.x = viewer.getMap().width - 1; 
            }
            if (pt.y < 0) {
                pt.y = 0;
            }
            if (pt.y >= viewer.getMap().height) {
                pt.y = viewer.getMap().height - 1;
            }
            if (!draggingStartPos) {
                endPos = pt;
            } else {
                startPos = pt;
            }
            computePath();
            viewer.redraw();
        }
    }
    
    /**
     * ���Ƶ�ǰ����
     * @param gc
     */
    public void draw(GC gc) {
        // ���·�����ڣ���������·������������
        if (path != null) {
            for (int i = 0; i < path.size() - 1; i++) {
                Point point1 = path.get(i);
                Point point2 = path.get(i + 1);
                
                Rectangle rect = new Rectangle(point1.x - 2, point1.y - 2, 5, 5);
                viewer.map2screen(rect);
                gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                gc.fillRectangle(rect);
                
                Point pt1 = new Point(point1.x, point1.y);
                Point pt2 = new Point(point2.x, point2.y);
                viewer.map2screen(pt1);
                viewer.map2screen(pt2);
                gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                gc.drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
            }
        }
        
        // ����յ�������ڣ������յ�����λ�û����
        Image img = EditorPlugin.getDefault().getImageRegistry().get("flag");
        if (startPos != null) {
            Point pt = new Point(startPos.x, startPos.y);
            viewer.map2screen(pt);
            gc.drawImage(img, pt.x - 10, pt.y - 33);
            
            int[] pos = pathFinder.tryOutPrison(startPos.x, startPos.y);
            if (pos != null) {
                pt = new Point(pos[0], pos[1]);
                viewer.map2screen(pt);
                gc.drawImage(img, pt.x - 10, pt.y - 33);
            }
        }
        if (endPos != null) {
            Point pt = new Point(endPos.x, endPos.y);
            viewer.map2screen(pt);
            gc.drawImage(img, pt.x - 10, pt.y - 33);
        }
    }
    
    /**
     * ���Ƶ�ǰ����
     * @param gc
     */
    public void draw(GLGraphics gc) {
        // ���·�����ڣ���������·������������
        if (path != null) {
            for (int i = 0; i < path.size() - 1; i++) {
                Point point1 = path.get(i);
                Point point2 = path.get(i + 1);
                
                Rectangle rect = new Rectangle(point1.x - 2, point1.y - 2, 5, 5);
                viewer.map2screen(rect);
                gc.setColor(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                gc.fillRect(rect);
                
                Point pt1 = new Point(point1.x, point1.y);
                Point pt2 = new Point(point2.x, point2.y);
                viewer.map2screen(pt1);
                viewer.map2screen(pt2);
                gc.setColor(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                gc.drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
            }
        }
        
        // ����յ�������ڣ������յ�����λ�û����
        Image img = EditorPlugin.getDefault().getImageRegistry().get("flag");
        if (startPos != null) {
            Point pt = new Point(startPos.x, startPos.y);
            viewer.map2screen(pt);
            gc.drawTexture(GLUtils.loadImage(img), 0, 0, pt.x - 10, pt.y - 33);
            
            int[] pos = pathFinder.tryOutPrison(startPos.x, startPos.y);
            if (pos != null) {
                pt = new Point(pos[0], pos[1]);
                viewer.map2screen(pt);
                gc.drawTexture(GLUtils.loadImage(img), 0, 0, pt.x - 10, pt.y - 33);
            }
        }
        if (endPos != null) {
            Point pt = new Point(endPos.x, endPos.y);
            viewer.map2screen(pt);
            gc.drawTexture(GLUtils.loadImage(img), 0, 0, pt.x - 10, pt.y - 33);
        }
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

