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
 * ������ͼ����㹤�ߡ�
 * @author lighthu
 */
public class GameRelivePointTool implements IMapEditTool {
    // ���༭��
    private GameAreaEditor editor;
    // ���ŵı༭��
    private GameMapViewer viewer;
    // ���һ�μ�⵽�����λ��
    private int lastX, lastY;
    
    // �����ͼƬ
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
     * ȱʡ���췽��
     * @param viewer �༭��
     * @param tv ��ͼ�鿴��
     */
    public GameRelivePointTool(GameAreaEditor editor, GameMapViewer viewer) {
        this.editor = editor;
        this.viewer = viewer;
    }

    // �ж�һ�������Ƿ���Է��õ�ǰѡ�е�NPC�����뱣֤NPC��һ��������Ļ�ڡ�
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
            // ����һ���¸����
            GameMapInfo mapInfo = viewer.getMapInfo();
            GameRelivePoint relivePoint = new GameRelivePoint();
            relivePoint.owner = mapInfo;
            relivePoint.id = 0;
            while (true) {
                // ȷ��ID���ظ�
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
        Rectangle rect = new Rectangle(0, 0, 0, 0);
        rect.x = lastX ;
        rect.y = lastY;
        viewer.map2screen(rect);
        drawRelivePointImage(gc, rect.x, rect.y);
        
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
        Rectangle rect = new Rectangle(0, 0, 0, 0);
        rect.x = lastX ;
        rect.y = lastY;
        viewer.map2screen(rect);
        drawRelivePointImage(gc, rect.x, rect.y);
        
        // ��������
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
    }
}
