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
 * ������ͼ���ڹ��ߡ�
 * @author lighthu
 */
public class GameMapExitTool implements IMapEditTool {
    // ���༭��
    private GameAreaEditor editor;
    // ���ŵı༭��
    protected GameMapViewer viewer;
    // ���һ�μ�⵽�����λ��
    private int lastX, lastY;

    /**
     * ȱʡ���췽��
     * @param viewer �༭��
     * @param tv ��ͼ�鿴��
     */
    public GameMapExitTool(GameAreaEditor editor, GameMapViewer viewer) {
        this.editor = editor;
        this.viewer = viewer;
    }

    // �ж�һ�������Ƿ���Է��õ�ǰѡ�е�NPC�����뱣֤NPC��һ��������Ļ�ڡ�
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
     * ��갴���¼�����ʼ�϶����ơ�
     * ��ͼ�鿴���е�ǰѡ����ͼ��
     * @param x ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
     * @param y ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
     * @param mask ����״̬����
     */
    public void mouseDown(int x, int y, int mask) {
    }

    /**
     * ���̧���¼����϶����ƽ��������϶����ĵ㶼����Ϊѡ�е�Tile��
     * @param x ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
     * @param y ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
     * @param mask ����״̬����
     */
    public void mouseUp(int x, int y, int mask) {
        Point pt = new Point(x, y);
        double scale = viewer.getMapFormat().scale;
        if (isValidPos(pt)) {
            // ����һ���³���
            GameMapInfo mapInfo = viewer.getMapInfo();
            GameMapExit exit = new GameMapExit();
            exit.owner = mapInfo;
            exit.id = 0;
            while (true) {
                // ȷ��ID���ظ�
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
     * ����ƶ��¼����϶�ˢ�ӡ�
     * @param x ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
     * @param y ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
     */
    public void mouseMove(int x, int y) {
        lastX = x;
        lastY = y;
        viewer.redraw();
    }

    /**
     * ���Ƶ�ǰ����
     * @param gc
     */
    public void draw(GC gc) {
        GameMap map = viewer.getMap();
        
        // ����NPC
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
        
        // ����NPC
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
