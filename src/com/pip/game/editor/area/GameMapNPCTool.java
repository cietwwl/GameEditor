package com.pip.game.editor.area;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;

import com.pip.mango.jni.GLGraphics;
import scryer.ogre.mesh.MeshConfig;

import com.pip.data.EntitySpriteInfo;
import com.pip.data.SpriteAnimation;
import com.pip.data.SpriteInfo;
import com.pip.game.data.Faction;
import com.pip.game.data.GameMesh;
import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapNPC;
import com.pip.image.workshop.Settings;
import com.pip.mapeditor.MapEditor;
import com.pip.mapeditor.MapViewer;
import com.pip.mapeditor.data.GameMap;
import com.pip.mapeditor.tool.IMapEditTool;
import com.pipimage.image.PipAnimate;
import com.pipimage.image.PipAnimateSet;

/**
 * ������ϷNPC���ߡ�������߸�����NPCģ���б���ѡ�е�NPCģ�壬�ڵ�ͼ�д���NPC��
 * @author lighthu
 */
public class GameMapNPCTool implements IMapEditTool {
    // ���༭��
    private GameAreaEditor editor;
    // ���ŵı༭��
    protected GameMapViewer viewer;
    // NPCģ��
    protected NPCTemplate template;
    // NPC����
//    private PipAnimateSet npcAnimate;
    // ���һ�μ�⵽�����λ��
    private int lastX, lastY;
    
    private SpriteInfo spriteInfo;
    private boolean valid;
    /**
     * ȱʡ���췽��
     * @param viewer �༭��
     * @param tv ��ͼ�鿴��
     */
    public GameMapNPCTool(GameAreaEditor editor, GameMapViewer viewer, NPCTemplate template) {
        this.editor = editor;
        this.viewer = viewer;
        this.template = template;
        this.valid = true;
//        npcAnimate = new PipAnimateSet();
//        File source = template.image.getAnimateFile(viewer.getMapFormat().aniFormat.id);
//        if (source == null) {
//            source = template.image.getAnimateFile(0);
//        }
//        try {
//            npcAnimate.load(source);
//        } catch (Exception e) {
//        }
    }

    // �ж�һ�������Ƿ���Է��õ�ǰѡ�е�NPC�����뱣֤NPC��һ��������Ļ�ڡ�
    protected boolean isValidPos(Point pt) {
        GameMap map = viewer.getMap();
        if(spriteInfo != null){
            SpriteAnimation animate = spriteInfo.getAnimation(0);
            Rectangle bounds = null;
            if(spriteInfo instanceof EntitySpriteInfo){
                EntitySpriteInfo entitySpriteInfo = (EntitySpriteInfo)spriteInfo;
                bounds = spriteInfo.getBounds(animate, entitySpriteInfo.getMeshConfig().getScalar() * Settings.meshScalar);
            } else if(spriteInfo instanceof PipAnimateSet){
                PipAnimateSet npcAnimate = (PipAnimateSet)spriteInfo;
                PipAnimate animation = npcAnimate.getAnimate(0);
                bounds = animation.getBounds();
            }
//            PipAnimate animate = npcAnimate.getAnimate(0);
//            Rectangle bounds = animate.getBounds();
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
        return false;
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
            // ����һ����NPC
            GameMapInfo mapInfo = viewer.getMapInfo();
            GameMapNPC npc = null;
            if(ProjectData.getActiveProject().config.gameMapNpcClass != null && ProjectData.getActiveProject().config.gameMapNpcClass.trim().length() > 0){
                try {
                    String className = ProjectData.getActiveProject().config.gameMapNpcClass.trim();
                    ProjectConfig config = ProjectData.getActiveProject().config;
                    Class clzz = config.getProjectClassLoader().loadClass(className);
                    npc = (GameMapNPC) clzz.newInstance();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                npc = new GameMapNPC();
            }
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
            npc.x = (int)(x / scale);
            npc.y = (int)(y / scale);
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
        
        spriteInfo = viewer.getSpriteInfo(template.image);
        if(spriteInfo == null){
            return;
        }
        // ����NPC
        PipAnimate animate = ((PipAnimateSet)spriteInfo).getAnimate(0);
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
    
//    public SpriteInfo getSprite(){
//        if(!valid){
//            return null;
//        }
//        File source = template.image.getAnimateFile(viewer.getMapFormat().aniFormat.id);
//        if (source == null) {
//            source = template.image.getAnimateFile(0);
//        }
//        try {
//            boolean inited = false;
//            if(SpriteInfo.hasSprite(source)){
//                inited = true;
//            }
//            spriteInfo = SpriteInfo.loadSprite(source);
//            if(spriteInfo != null && !inited){
//                if(spriteInfo instanceof EntitySpriteInfo){
//                    EntitySpriteInfo info = (EntitySpriteInfo)spriteInfo;
//                    GameMesh gameMesh = (GameMesh)template.image;
//                    info.getPlayer().setAnimation(gameMesh.getAnimateName(), -1);
//                    inited = true;
//                }
//            }
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//            valid = false;
//        }
//        return spriteInfo;
//    }
    
    /**
     * ���Ƶ�ǰ����
     * @param gc
     */
    public void draw(GLGraphics gc) {
        GameMap map = viewer.getMap();
        
        spriteInfo = viewer.getSpriteInfo(template.image);
        if(spriteInfo == null){
            return;
        }
        Point pt = new Point(lastX, lastY);
        viewer.map2screen(pt);
        
        if(spriteInfo instanceof PipAnimateSet){
            PipAnimateSet npcAnimate = (PipAnimateSet)spriteInfo;
            // ����NPC
            PipAnimate animate = npcAnimate.getAnimate(0);
            if(animate != null){            
                animate.drawAnimateFrame(gc, viewer.getCurrentTime(), pt.x, pt.y, viewer.getRatio(), MapEditor.imageCache);
            }
        }else if(spriteInfo instanceof EntitySpriteInfo){
            viewer.drawSprite(gc, template.image, pt.x, pt.y,45);
//            EntitySpriteInfo info = (EntitySpriteInfo)spriteInfo;
//            GameMesh gameMesh = (GameMesh)template.image;
//            float scalar = (float)(Settings.meshScalar * viewer.getMapFormat().scale * gameMesh.scalar) * (float)viewer.getRatio();
//            info.getPlayer().setScale(scalar);
//            info.getPlayer().setDirection(45);
//            info.getPlayer().draw(gc.getHandle(), pt.x, pt.y);
//            info.getPlayer().cycle();
            
//            mesh.setPosition2D(pt.x, pt.y);
            
//            mesh.setScale2D((float)(Settings.meshScalar * viewer.getMapFormat().scale * gameMesh.scalar));
//            mesh.setViewScale2D((float)viewer.getRatio());
//            mesh.rotateEntity(Settings.mapMeshYaw);
//            mesh.drawInMap(gc);
//            mesh.updateInMapViewer(viewer.getCurrentTime());
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
