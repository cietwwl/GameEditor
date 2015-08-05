package com.pip.game.editor.area;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Menu;

import com.pip.game.data.map.*;
import com.pip.game.editor.EditorPlugin;
import com.pip.mango.jni.GLGraphics;
import com.pip.mango.jni.GLUtils;
import com.pip.mapeditor.*;
import com.pip.mapeditor.data.*;
import com.pip.mapeditor.tool.IMapEditTool;
import com.pipimage.image.*;
import com.swtdesigner.SWTResourceManager;

/**
 * ���������ڵ����㷨��
 * @author lighthu
 */
public class TestEyesightTool implements IMapEditTool {
    // ���༭��
    private GameAreaEditor editor;
    // ���ŵı༭��
    private GameMapViewer viewer;
    // �Ƿ������϶�
    private boolean dragging;
    // �Ƿ������϶����
    private boolean draggingStartPos;
    // ·���������յ�
    private Point startPos, endPos;
    // ��ǰ�ҳ���·���ĵ�
    private List<int[]> path;

    /**
     * ȱʡ���췽��
     * @param viewer �༭��
     * @param tv ��ͼ�鿴��
     */
    public TestEyesightTool(GameAreaEditor editor, GameMapViewer viewer) {
        this.editor = editor;
        this.viewer = viewer;
    }
    
    /*
     * ���㵱ǰѡ�е������յ�֮���·�������浽��Ա����path�С�
     */
    protected void computePath() {
        if (startPos == null || endPos == null) {
            path = null;
            return;
        }
        path = viewer.getMapInfo().getEyesightPath(startPos.x, startPos.y, endPos.x, endPos.y);
//        long time1 = System.currentTimeMillis();
//        for (int i = 0; i < 100000; i++) {
//            viewer.getMapInfo().canSee(startPos.x, startPos.y, endPos.x, endPos.y, viewer.getMap());
//        }
//        System.out.println("Find Path Used: " + (System.currentTimeMillis() - time1) + "ms, 100000 times");
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
            for (int i = 0; i < path.size(); i++) {
                int[] point = path.get(i);
                Rectangle rect = new Rectangle(point[0] * 8, point[1] * 8, 8 , 8);
                viewer.map2screen(rect);
                if ((viewer.getMap().tileInfo[point[1]][point[0]] & 1) != 0) {
                    gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
                } else {
                    gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                }
                gc.fillRectangle(rect);
            }
        }
        
        // ����յ�������ڣ������յ�����λ�û����
        Image img = EditorPlugin.getDefault().getImageRegistry().get("flag");
        if (startPos != null) {
            Point pt = new Point(startPos.x, startPos.y);
            viewer.map2screen(pt);
            gc.drawImage(img, pt.x - 10, pt.y - 33);
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
            for (int i = 0; i < path.size(); i++) {
                int[] point = path.get(i);
                Rectangle rect = new Rectangle(point[0] * 8, point[1] * 8, 8 , 8);
                viewer.map2screen(rect);
                if ((viewer.getMap().tileInfo[point[1]][point[0]] & 1) != 0) {
                    gc.setColor(SWTResourceManager.getColor(SWT.COLOR_RED));
                } else {
                    gc.setColor(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                }
                gc.fillRect(rect);
            }
        }
        
        // ����յ�������ڣ������յ�����λ�û����
        Image img = EditorPlugin.getDefault().getImageRegistry().get("flag");
        if (startPos != null) {
            Point pt = new Point(startPos.x, startPos.y);
            viewer.map2screen(pt);
            gc.drawTexture(GLUtils.loadImage(img), 0, 0, pt.x - 10, pt.y - 33);
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

