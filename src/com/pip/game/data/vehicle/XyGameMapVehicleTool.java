package com.pip.game.data.vehicle;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;

import com.pip.game.data.Faction;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.editor.area.GameAreaEditor;
import com.pip.game.editor.area.GameMapViewer;
import com.pip.mango.jni.GLGraphics;
import com.pip.mapeditor.MapEditor;
import com.pip.mapeditor.MapViewer;
import com.pip.mapeditor.data.GameMap;
import com.pip.mapeditor.tool.IMapEditTool;
import com.pipimage.image.PipAnimate;
import com.pipimage.image.PipAnimateSet;

public class XyGameMapVehicleTool implements IMapEditTool {
    // ���༭��
    private GameAreaEditor editor;
    // ���ŵı༭��
    private GameMapViewer viewer;
    // NPCģ��
    private Vehicle template;
    // NPC����
    private PipAnimateSet npcAnimate;
    // ���һ�μ�⵽�����λ��
    private int lastX, lastY;

    /**
     * ȱʡ���췽��
     * @param viewer �༭��
     * @param tv ��ͼ�鿴��
     */
    public XyGameMapVehicleTool(GameAreaEditor editor, GameMapViewer viewer, Vehicle template) {
        this.editor = editor;
        this.viewer = viewer;
        this.template = template;
        npcAnimate = new PipAnimateSet();
        try {
            npcAnimate.load(template.image.getAnimateFile(viewer.getMapFormat().aniFormat.id));
        } catch (Exception e) {
        }
    }

    // �ж�һ�������Ƿ���Է��õ�ǰѡ�е�NPC�����뱣֤NPC��һ��������Ļ�ڡ�
    private boolean isValidPos(Point pt) {
        GameMap map = viewer.getMap();
        PipAnimate animate = npcAnimate.getAnimate(0);
        Rectangle bounds = animate.getBounds();
        bounds.x += pt.x;
        bounds.y += pt.y;
        if (bounds.x + bounds.width - 3 < 0) {
            return false;
        } else if (bounds.x - map.width + 3 > 0) {
            return false;
        }
        if (bounds.y + bounds.height - 3 < 0) {
            return false;
        } else if (bounds.y - map.height + 3 > 0) {
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
        if (isValidPos(pt)) {
            // ����һ����NPC
            GameMapInfo mapInfo = viewer.getMapInfo();
            XyGameMapVehicle npc = new XyGameMapVehicle();
            npc.owner = mapInfo;
            npc.id = 0;
            while (true) {
                // ȷ��ID���ظ�
                boolean dup = false;
                for (int i = mapInfo.objects.size() - 1; i >= 0; i--) {
                    if (mapInfo.objects.get(i).id == npc.id) {
                        dup = true;
                        break;
                    }
                }
                if (dup) {
                    npc.id++;
                } else {
                    break;
                }
            }
            npc.x = x;
            npc.y = y;
            npc.template = template;
            npc.name = template.title;
            npc.faction = (Faction)ProjectData.getActiveProject().getDictDataListByType(Faction.class).get(0);
            npc.visible = true;
            npc.refreshInterval = 300;
            mapInfo.objects.add(npc);
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
        PipAnimate animate = npcAnimate.getAnimate(0);
        Point pt = new Point(lastX, lastY);
        viewer.map2screen(pt);
        if(animate != null){            
            animate.drawAnimateFrame(gc, viewer.getCurrentTime(), pt.x, pt.y, viewer.getRatio(), null);
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
        
        // ����NPC
        PipAnimate animate = npcAnimate.getAnimate(0);
        Point pt = new Point(lastX, lastY);
        viewer.map2screen(pt);
        if(animate != null){            
            animate.drawAnimateFrame(gc, viewer.getCurrentTime(), pt.x, pt.y, viewer.getRatio(), MapEditor.imageCache);
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
