package com.pip.game.data.pkg;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import com.pip.game.data.GameArea;
import com.pip.game.data.GameAreaInfo;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.map.GameMapObject;
import com.pip.mapeditor.data.AccurateMapLayer;
import com.pip.mapeditor.data.BlurMapLayer;
import com.pip.mapeditor.data.GameMap;
import com.pip.mapeditor.data.IMapLayer;
import com.pip.mapeditor.data.MapFile;
import com.pip.mapeditor.data.MapNPC;
import com.pip.mapeditor.data.MapNPCLayer;
import com.pip.mapeditor.data.MultiAnimNPC;
import com.pip.mapeditor.data.TileInfo;
import com.pip.mapeditor.data.TileSet;
import com.pip.mapeditor.tool.TileConfigTool;
import com.pip.util.Point;
import com.pip.util.Utils;
import com.pipimage.image.CompressTextureOption;
import com.pipimage.image.JPEGMergeOption;
import com.pipimage.image.PipAnimate;
import com.pipimage.image.PipAnimateFrameRef;
import com.pipimage.image.PipAnimateSet;
import com.pipimage.image.PipImage;

/**
 * �Ϳͻ��˴���йص�һЩ�������ϡ�
 * @author lighthu
 */
public class PackageUtils {
    // �Ƿ�ϲ���ģʽ�ĵ�ͼNPC
    protected boolean mergeLib = true;
    // �ϲ���ͼNPCʱ�ĺϲ���Ϣ
    protected PipAnimateSet mergedAni4libMap;
    protected HashMap<Point, Integer> mapping;

    public PackageUtils() {
    }
    
    public PackageUtils(boolean mergeLib) {
        this.mergeLib = mergeLib;
    }
    
    /**
     * ��һ���ؿ�ת��Ϊ�ͻ������ظ�ʽ��
     * @param area �ؿ�����
     * @param mapFile �ؿ���ͼ�ļ���null��ʾ��Ҫ����
     * @param info �ؿ���Ϣ�ļ���null��ʾ��Ҫ����
     * @param target Ŀ���ļ�
     * @param ratio ����Ŵ���
     * @param colorMode ��ɫ�Ż�ģʽ��-2��ʾ���Ż���-1��ʾ�Զ��Ż���256ɫ��>=0��ʾ�õ�ͼ�ļ���ָ���ĵ�ɫ���Ż���-3��ʾJPEGѹ��
     */
    public void makeClientPackage(GameArea area, MapFile mapFile, GameAreaInfo info, 
            PackageFile target, float ratio, int colorMode, JPEGMergeOption jpegOption,
            CompressTextureOption compTexOption) throws Exception {
        target.setName("GAMEPKG");
        target.setVersion(0);
        
        // �����ģʽ��ͼ�ĺϲ���ͼNPC
        if (mapFile.isLibMode && mergeLib) {
            int borderSize = 1;
            int maxWidth = 1024;
            int maxHeight = 800;
            boolean separateAlpha = false;
            if (colorMode == -3) {
                borderSize = jpegOption.borderWidth;
                maxHeight = 600;
            }
            PipAnimateSet.useTrueColorForMap = area.owner.config.useTrueColourForMap;
            if (colorMode == -4) {
                // 20140223����ģʽ��ѹ������
                PipAnimateSet.useTrueColorForMap = true;
                maxWidth = compTexOption.sizeWidth;
                maxHeight = compTexOption.sizeHeight;
                borderSize = compTexOption.borderWidth;
                separateAlpha = compTexOption.format.equals(CompressTextureOption.PVRTC_4BPP2) ||
                        compTexOption.format.equals(CompressTextureOption.ETC2);
            }
            if (area.packingFullAnimate) {
                MapFile.includeFullAnimate = true;
            }
            Object obj[] = mapFile.buildPackAni(borderSize, maxWidth, maxHeight, separateAlpha);
            if (area.packingFullAnimate) {
                MapFile.includeFullAnimate = false;
            }
            mergedAni4libMap = (PipAnimateSet) obj[0];
            mapping = (HashMap<Point, Integer>) obj[1];
            if (colorMode == -1) {
                for (int i = 0; i < mergedAni4libMap.getFileCount(); i++) {
                    mergedAni4libMap.getSourceImage(i).optimizeColor(256);
                }
            } else if (colorMode >= 0) {
                if (colorMode >= mapFile.refPalettes.size()) {
                    throw new Exception("ָ���ĵ�ɫ�岻���ڡ�");
                }
                int[] pal = mapFile.refPalettes.get(colorMode);
                for (int i = 0; i < mergedAni4libMap.getFileCount(); i++) {
                    mergedAni4libMap.getSourceImage(i).optimizeColor(pal);
                }
            }
        }
        
        // ���ɹؿ���Ϣ�ļ�0.stg lib OK
        target.addFile("0.stg", makeStageInfo(area, mapFile, info));
        
        // �������е�ͼ�ļ� lib OK
        int mapCount = mapFile.getMaps().size();
        for (int i = 0; i < mapCount; i++) {
            target.addFile(i + ".m", makeMapFile(area, mapFile.getMaps().get(i), info.maps.get(i), ratio));
            //��ͼ��ײ��Ϣ
            GameMapInfo gmi = info.maps.get(i);
            byte[][] tileInfo = gmi.tileInfo;
            if (tileInfo == null) {
                tileInfo = mapFile.getMaps().get(i).tileInfo;
            } else {
                // �ͻ�����ʾ�ڵ��������ݣ�����16������map�ļ��е�Ϊ׼
                copyVisibility(mapFile.getMaps().get(i), tileInfo);
            }
            target.addFile(i+".mc", convert2DByte(tileInfo));
            
            //����NPC
            if (area.owner.config.includeBuildingInPackage) {
                target.addFile(i+".building", makeBuildingNPC(gmi));
            }
        }
        
        // ��ȷ��ͼ��ͼ�ļ�
        if (mapFile.getTileImage() != null && mapFile.getTileImage().image.getFrameCount() > 0) {
            target.addFile("tile.pip", PipImage.makeImageFile(mapFile.getTileImage().image, true));
            target.addFile("tile.ts", makeTileSetFile(mapFile.getTileImage()));
        }
        
        // ģ����ͼ�����ļ�
        for (int i = 0; i < mapFile.getLandforms().size(); i++) {
            TileSet ts = mapFile.getLandforms().get(i);
            if (mapFile.isLibMode && mergeLib == false) {
                target.addFile("l" + i + ".ldf", ts.hashCode);
            } else {
                target.addFile("l" + i + ".ldf", PipImage.makeImageFile(ts.image, true));
                target.addFile("l" + i + ".ts", makeTileSetFile(ts));
            }
        }
        
        // ��ͼNPC��������ļ������ǰ����ͼNPC��������Ч��
        checkNPCAnimates(mapFile.getAnimates());
        if (mapFile.isLibMode) {
            if (mergeLib) {
                if (area.refAreaID == 0) {
                    target.addFile("npc.anp", makeAnimatePackage(mergedAni4libMap, true, colorMode == -3 ? jpegOption : null, colorMode == -4 ? compTexOption : null));
                } else {
                    target.addFile("npc.anp", String.valueOf(area.refAreaID).getBytes("ASCII"));
                }
            } else {
                ByteArrayOutputStream bas = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bas);
                for (PipAnimateSet pas : mapFile.animateList) {
                    dos.writeInt(pas.hashCode);
                }
                target.addFile("npc.anp", bas.toByteArray());
            }
        } else {
            target.addFile("npc.anp", makeAnimatePackage(mapFile.getAnimates(), true, colorMode == -3 ? jpegOption : null, colorMode == -4 ? compTexOption : null));
        }
    }
    protected byte[] makeBuildingNPC(GameMapInfo gmi) throws Exception  {
        // TODO Auto-generated method stub
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        List<GameMapNPC> ls = new ArrayList<GameMapNPC>();
        ls.clear();
        for (GameMapObject obj : gmi.objects) {
            if (obj instanceof GameMapNPC) {
                ls.add((GameMapNPC) obj);
            }
        }
        dos.writeShort(ls.size());
        if (ls.size() > 0) {
            for (GameMapNPC building : ls) {
              dos.writeInt(building.id);
              dos.writeInt(building.x);
              dos.writeInt(building.y);
              dos.writeInt(building.template.image.id);
            }
        }
        dos.flush();
        return bos.toByteArray();
    }
    protected static byte[] convert2DByte(byte[][] tileInfo) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream zos = new GZIPOutputStream(bos);
        for(int i=0; i<tileInfo.length;i++){
            zos.write(tileInfo[i]);
        }
        zos.flush();
        zos.close();
        return bos.toByteArray();
    }

    
    /**
     * ���������ͼ��Դ�ļ�
     * @param map �����ͼ�ļ�
     * @param target Ŀ���ļ�
     * */
    public void makeClientPackage(MapFile map, PackageFile target) throws Exception{
        target.setName("SANGUOWM");
        target.setVersion(0);
        
        // �������е�ͼ�ļ�
        int mapCount = map.getMaps().size();
        for (int i = 0; i < mapCount; i++) {
            byte[] _data = makeMapFile(map, map.getMaps().get(i));
            target.addFile(i + ".m", _data);
        }
        
        // ��ȷ��ͼ��ͼ�ļ�
        target.addFile("tile.pip", PipImage.makeImageFile(map.getTileImage().image, true));
        target.addFile("tile.ts", makeTileSetFile(map.getTileImage(), 0));
        
        // ��ͼNPC��������ļ������ǰ����ͼNPC��������Ч��
        checkNPCAnimates(map.getAnimates());
        target.addFile("npc.anp", makeAnimatePackage(map.getAnimates(), false, null, null));
    }
    
    /*
     * ���NPC��������Ч�ԣ�������������
     * 1. �����ж�֡��������ȫһ��
     * 2. ����ֻ��1֡����delay������1
     */
    protected void checkNPCAnimates(PipAnimateSet as) {
        for (int i = 0; i < as.getAnimateCount(); i++) {
            PipAnimate ani = as.getAnimate(i);
            
            // ����ǲ������ж���֡��һ��
            if (ani.getFrameCount() > 1) {
                PipAnimateFrameRef f1 = ani.getFrame(0);
                for (int j = 1; j < ani.getFrameCount(); j++) {
                    // ���ĳһ֡�͵�һ֡��ȫһ������ô����֡ɾ��
                    PipAnimateFrameRef f2 = ani.getFrame(j);
                    if (f1.getFrame() == f2.getFrame() && f1.getDx() == f2.getDx() && f1.getDy() == f2.getDy()) {
                        ani.removeFrame(j);
                        j--;
                    } else {
                        // �������һ����һ���ģ�����ѭ��
                        break;
                    }
                }
            }
            
            // ���ֻ��1֡�Ķ�����delay������1
            if (ani.getFrameCount() == 1) {
                PipAnimateFrameRef f1 = ani.getFrame(0);
                f1.setDelay(1);
            }
        }
    }

    /**
     * ���ɹؿ���Ϣ�ļ������ݡ�
     */
    public byte[] makeStageInfo(GameArea area, MapFile map, GameAreaInfo info) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeShort(map.getTileWidth());
        dos.writeShort(map.getTileHeight());
        dos.writeShort(map.getBlurTileWidth());
        dos.writeShort(map.getBlurTileHeight());
        dos.writeShort(area.id);
        dos.writeUTF(area.title);
        
        if (area.owner.config.includeBuildingInPackage) {
            List<GameMapNPC> ls = new ArrayList<GameMapNPC>();
            for (GameMapInfo mi : info.maps) {
                ls.clear();
                for (GameMapObject obj : mi.objects) {
                    if (obj instanceof GameMapNPC) {
                        ls.add((GameMapNPC) obj);
                    }
                }
                dos.writeShort(ls.size());
                if (ls.size() > 0) {
                    for (GameMapNPC building : ls) {
                      dos.writeInt(building.id);
                      dos.writeInt(building.x);
                      dos.writeInt(building.y);
                      dos.writeInt(building.template.image.id);
                    }
                }
            }
        }
        
        dos.flush();
        return bos.toByteArray();
    }

    /**
     * ���ɵ�ͼ���ݣ������ͼ��
     */
    public byte[] makeMapFile(MapFile mapf, GameMap map) throws Exception{
        // ����һ��ѹ����
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream zos = new GZIPOutputStream(bos);
        DataOutputStream dos = new DataOutputStream(zos);
        
        // ��ͼ������Ϣ
        dos.writeShort(map.width);
        dos.writeShort(map.height);
        dos.writeByte(mapf.getTileWidth());
        dos.writeByte(mapf.getTileHeight());
        
        // ͼ����Ϣ
        for (IMapLayer layer : map.layers) {
            if (layer instanceof AccurateMapLayer) {
                // ��ȷ��ͼ��
                AccurateMapLayer alayer = (AccurateMapLayer) layer;
                short[][] celldata = alayer.getLayerData();
                int rows = celldata.length;
                int cols = celldata[0].length;
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        dos.writeByte(celldata[i][j]);
                    }
                }
            } else if (layer instanceof MapNPCLayer) {
                // ��ͼNPC��
                MapNPCLayer nlayer = (MapNPCLayer) layer;
                List<MapNPC> npcs = nlayer.getNpcs();
                dos.writeInt(npcs.size() * 3);
                for (MapNPC npc : npcs) {
                    dos.writeShort(npc.animate);
                    dos.writeShort(npc.x);
                    dos.writeShort(npc.y);
                }
            } else {
                throw new IllegalArgumentException();
            }
        }

        // ���ļ����ݽ���ѹ��
        dos.flush();
        dos.close();
        return bos.toByteArray();
        
    }
    
    /**
     * ����һ����ͼ�ļ������ݡ�
     */
    public byte[] makeMapFile(GameArea area, GameMap map, GameMapInfo mapInfo, float ratio) throws Exception {
        // ����һ��ѹ����
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream zos = new GZIPOutputStream(bos);
        DataOutputStream dos = new DataOutputStream(zos);

        // ��ͼ������Ϣ
        dos.writeByte(mapInfo.id);
        dos.writeUTF(mapInfo.name);
        dos.writeShort(map.width);
        dos.writeShort(map.height);

        // ͼ����Ϣ
        dos.writeByte(map.layers.size());
        for (IMapLayer layer : map.layers) {
            if (layer instanceof AccurateMapLayer) {
                // ��ȷ��ͼ��
                AccurateMapLayer alayer = (AccurateMapLayer) layer;
                dos.writeByte(0);
                short[][] celldata = alayer.getLayerData();
                int rows = celldata.length;
                int cols = celldata[0].length;
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        dos.writeByte(celldata[i][j]);
                    }
                }
            } else if (layer instanceof BlurMapLayer) {
                // ģ����ͼ��
                BlurMapLayer blayer = (BlurMapLayer) layer;
                dos.writeByte(1);
                dos.writeInt(blayer.getRandomSeed());
                dos.writeByte(blayer.getBaseLandform());
                byte[][] celldata = blayer.getLayerData();
                int rows = celldata.length;
                for (int i = 0; i < rows; i++) {
                    dos.write(celldata[i]);
                }
            } else if (layer instanceof MapNPCLayer) {
                // ��ͼNPC��
                MapNPCLayer nlayer = (MapNPCLayer) layer;
                dos.writeByte(2);
                if (nlayer == map.groundLayer) {
                    dos.writeByte(1);
                } else if (nlayer == map.skyLayer) {
                    dos.writeByte(2);
                } else {
                    dos.writeByte(0);
                }
                List<MapNPC> npcs = nlayer.getNpcs();
                MapNPC[] npcArr = new MapNPC[npcs.size()];
                npcs.toArray(npcArr);
                
                // �����ͼ�㲻�ǰ�˳����Ƶģ����ﰴY�����������
                if (!nlayer.getForceAddOrderDraw()) {
                    Arrays.sort(npcArr);
                }
                
                dos.writeShort(npcArr.length);
                for (MapNPC npc : npcArr) {
                    writeNPC(dos, npc, map.parent.isLibMode);
                    
                    // �ͻ�������animate�±�Ϊ-1�ж���MultiAnimNPC
                    if (npc instanceof MultiAnimNPC) {
                        ArrayList<MapNPC> children = ((MultiAnimNPC)npc).getChildren();
                        if (children.size() >= 255) {
                            dos.writeByte(0xFF);
                            dos.writeShort(children.size());
                        } else {
                            dos.writeByte(children.size());
                        }
                        for (MapNPC cNPC: children) {
                            writeNPC(dos, cNPC, map.parent.isLibMode);
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException();
            }
        }

        // ���ļ����ݽ���ѹ��
        dos.flush();
        dos.close();
        return bos.toByteArray();
    }

    private void writeNPC(DataOutputStream dos, MapNPC npc, boolean isLibMode) throws IOException {
        if (isLibMode) { // Ϊ�˿ͻ���YOrder����������ã�������������
            if (mergeLib) {
                if (npc.animate == -1) {
                    dos.writeShort(-1);
                } else {
                    Point key = new Point(npc.animateSetRef, npc.animate);
                    dos.writeShort(mapping.get(key).intValue());
                }
                dos.writeShort(npc.x);
                dos.writeShort(npc.y);
            } else {
                dos.writeShort(npc.animate);
                dos.writeShort(npc.x);
                dos.writeShort(npc.y);
                dos.writeInt(npc.animateSetRef);
            }
        } else {
            dos.writeShort(npc.animate);
            dos.writeShort(npc.x);
            dos.writeShort(npc.y);
        }
    }

    /**
     * ����TS�ļ���
     */
    public byte[] makeTileSetFile(TileSet tileSet) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeShort(tileSet.tileInfo.size());
        for (TileInfo ti : tileSet.tileInfo) {
            dos.writeByte(ti.frameID);
            int infoByte = 0;
            infoByte = ti.transit << 6;
            infoByte |= getColorID(ti.thumbColor) << 1;
            if (ti.unpassable) {
                infoByte |= 1;
            }
            dos.writeByte(infoByte);
        }
        dos.flush();
        return bos.toByteArray();
    }
    
    /**
     * ����TS�ļ���(�����ͼ)
     */
    public byte[] makeTileSetFile(TileSet tileSet, int iii) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeShort(tileSet.tileInfo.size() * 2);
        for (TileInfo ti : tileSet.tileInfo) {
            dos.writeByte(ti.frameID);
            dos.writeByte(ti.transit);
        }
        dos.flush();
        return bos.toByteArray();
    }
    
    public static final int[] thumbColors = new int[] {
        0x000000, 0x808080, 0xC0C0C0, 0xFFFFFF,
        0xFF0000, 0xFFFF00, 0x00FF00, 0x00FFFF,
        0x0000FF, 0xFF00FF, 0xFFFF80, 0x00FF80,
        0x80FFFF, 0x8080FF, 0xFF0080, 0xFF8040
    };

    /**
     * �õ�һ����ɫ����ɫ���е�index��
     */
    public static int getColorID(int color) {
        for (int i = 0; i < thumbColors.length; i++) {
            if (color == thumbColors[i]) {
                return i;
            }
        }
        return 0;
    }

    /**
     * ��һ���������ļ�����洢��
     */
    public byte[] makeAnimatePackage(PipAnimateSet animateSet, boolean compressCTN, JPEGMergeOption jpegOption, CompressTextureOption compTexOption) throws Exception {
        PackageFile pkg = new PackageFile();
        pkg.setName("ANIMATESPKG");
        pkg.setVersion(0);
        
        // ���CTN�ļ�
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (compressCTN) {
            GZIPOutputStream zos = new GZIPOutputStream(bos);
            animateSet.save(zos, false);
            zos.flush();
            zos.close();
        } else {
            animateSet.save(bos, false);
        }
        pkg.addFile("0.ctn", bos.toByteArray());
        
        // �������ͼƬ�ļ�
        for (int i = 0; i < animateSet.getFileCount(); i++) {
            byte[] pipData;
            if (jpegOption != null) {
                pipData = PipImage.makeJPEGMergeFile(animateSet.getSourceImage(i), jpegOption);
            } else if (compTexOption != null) {
                pipData = PipImage.makeCompressTextureFile(animateSet.getSourceImage(i), compTexOption);
            } else {
                pipData = PipImage.makeImageFile(animateSet.getSourceImage(i), false);
            }
            pkg.addFile(i + ".pip", pipData);
        }
        
        bos = new ByteArrayOutputStream();
        pkg.save(bos);
        return bos.toByteArray();
    }
    
    // �ͻ�����ʾ�ڵ��������ݣ�����16������map�ļ��е�Ϊ׼
    public void copyVisibility(GameMap gameMap, byte[][] tileInfo){
        byte[][] mtInfo = gameMap.tileInfo;
        for (int y = 0; y < tileInfo.length && y < mtInfo.length; y++) {
            for (int x = 0; x < tileInfo[y].length && x < mtInfo[y].length; x++) {
                if ((mtInfo[y][x] & TileConfigTool.CONFIG_MASK_VISIBILITY) == 0) {
                    tileInfo[y][x] &= ~TileConfigTool.CONFIG_MASK_VISIBILITY;
                } else {
                    tileInfo[y][x] |= TileConfigTool.CONFIG_MASK_VISIBILITY;
                }
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        // ���gz�ļ�
        byte[] input = Utils.loadFileData(new File("d:/effect_ui.psdata"));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream zos = new GZIPOutputStream(bos);
        zos.write(input);
        zos.flush();
        zos.close();
        byte[] output = bos.toByteArray();
        Utils.saveFileData(new File("d:/effect_ui.psdata.gz"), output);
    }
}
