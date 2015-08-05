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
 * 和客户端打包有关的一些方法集合。
 * @author lighthu
 */
public class PackageUtils {
    // 是否合并库模式的地图NPC
    protected boolean mergeLib = true;
    // 合并地图NPC时的合并信息
    protected PipAnimateSet mergedAni4libMap;
    protected HashMap<Point, Integer> mapping;

    public PackageUtils() {
    }
    
    public PackageUtils(boolean mergeLib) {
        this.mergeLib = mergeLib;
    }
    
    /**
     * 把一个关卡转换为客户端下载格式。
     * @param area 关卡定义
     * @param mapFile 关卡地图文件，null表示需要载入
     * @param info 关卡信息文件，null表示需要载入
     * @param target 目标文件
     * @param ratio 坐标放大倍数
     * @param colorMode 颜色优化模式：-2表示不优化，-1表示自动优化成256色，>=0表示用地图文件中指定的调色板优化，-3表示JPEG压缩
     */
    public void makeClientPackage(GameArea area, MapFile mapFile, GameAreaInfo info, 
            PackageFile target, float ratio, int colorMode, JPEGMergeOption jpegOption,
            CompressTextureOption compTexOption) throws Exception {
        target.setName("GAMEPKG");
        target.setVersion(0);
        
        // 处理库模式地图的合并地图NPC
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
                // 20140223新增模式：压缩纹理
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
                    throw new Exception("指定的调色板不存在。");
                }
                int[] pal = mapFile.refPalettes.get(colorMode);
                for (int i = 0; i < mergedAni4libMap.getFileCount(); i++) {
                    mergedAni4libMap.getSourceImage(i).optimizeColor(pal);
                }
            }
        }
        
        // 生成关卡信息文件0.stg lib OK
        target.addFile("0.stg", makeStageInfo(area, mapFile, info));
        
        // 生成所有地图文件 lib OK
        int mapCount = mapFile.getMaps().size();
        for (int i = 0; i < mapCount; i++) {
            target.addFile(i + ".m", makeMapFile(area, mapFile.getMaps().get(i), info.maps.get(i), ratio));
            //地图碰撞信息
            GameMapInfo gmi = info.maps.get(i);
            byte[][] tileInfo = gmi.tileInfo;
            if (tileInfo == null) {
                tileInfo = mapFile.getMaps().get(i).tileInfo;
            } else {
                // 客户端显示遮挡区域数据（掩码16），以map文件中的为准
                copyVisibility(mapFile.getMaps().get(i), tileInfo);
            }
            target.addFile(i+".mc", convert2DByte(tileInfo));
            
            //建筑NPC
            if (area.owner.config.includeBuildingInPackage) {
                target.addFile(i+".building", makeBuildingNPC(gmi));
            }
        }
        
        // 精确地图贴图文件
        if (mapFile.getTileImage() != null && mapFile.getTileImage().image.getFrameCount() > 0) {
            target.addFile("tile.pip", PipImage.makeImageFile(mapFile.getTileImage().image, true));
            target.addFile("tile.ts", makeTileSetFile(mapFile.getTileImage()));
        }
        
        // 模糊地图地形文件
        for (int i = 0; i < mapFile.getLandforms().size(); i++) {
            TileSet ts = mapFile.getLandforms().get(i);
            if (mapFile.isLibMode && mergeLib == false) {
                target.addFile("l" + i + ".ldf", ts.hashCode);
            } else {
                target.addFile("l" + i + ".ldf", PipImage.makeImageFile(ts.image, true));
                target.addFile("l" + i + ".ts", makeTileSetFile(ts));
            }
        }
        
        // 地图NPC动画打包文件，打包前检查地图NPC动画的有效性
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
     * 导出世界地图资源文件
     * @param map 世界地图文件
     * @param target 目标文件
     * */
    public void makeClientPackage(MapFile map, PackageFile target) throws Exception{
        target.setName("SANGUOWM");
        target.setVersion(0);
        
        // 生成所有地图文件
        int mapCount = map.getMaps().size();
        for (int i = 0; i < mapCount; i++) {
            byte[] _data = makeMapFile(map, map.getMaps().get(i));
            target.addFile(i + ".m", _data);
        }
        
        // 精确地图贴图文件
        target.addFile("tile.pip", PipImage.makeImageFile(map.getTileImage().image, true));
        target.addFile("tile.ts", makeTileSetFile(map.getTileImage(), 0));
        
        // 地图NPC动画打包文件，打包前检查地图NPC动画的有效性
        checkNPCAnimates(map.getAnimates());
        target.addFile("npc.anp", makeAnimatePackage(map.getAnimates(), false, null, null));
    }
    
    /*
     * 检查NPC动画的有效性，检查两种情况：
     * 1. 动画有多帧，但都完全一样
     * 2. 动画只有1帧，但delay不等于1
     */
    protected void checkNPCAnimates(PipAnimateSet as) {
        for (int i = 0; i < as.getAnimateCount(); i++) {
            PipAnimate ani = as.getAnimate(i);
            
            // 检查是不是所有动画帧都一样
            if (ani.getFrameCount() > 1) {
                PipAnimateFrameRef f1 = ani.getFrame(0);
                for (int j = 1; j < ani.getFrameCount(); j++) {
                    // 如果某一帧和第一帧完全一样，那么把这帧删除
                    PipAnimateFrameRef f2 = ani.getFrame(j);
                    if (f1.getFrame() == f2.getFrame() && f1.getDx() == f2.getDx() && f1.getDy() == f2.getDy()) {
                        ani.removeFrame(j);
                        j--;
                    } else {
                        // 如果发现一个不一样的，跳出循环
                        break;
                    }
                }
            }
            
            // 检查只有1帧的动画的delay必须是1
            if (ani.getFrameCount() == 1) {
                PipAnimateFrameRef f1 = ani.getFrame(0);
                f1.setDelay(1);
            }
        }
    }

    /**
     * 生成关卡信息文件的内容。
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
     * 生成地图数据（世界地图）
     */
    public byte[] makeMapFile(MapFile mapf, GameMap map) throws Exception{
        // 生成一个压缩流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream zos = new GZIPOutputStream(bos);
        DataOutputStream dos = new DataOutputStream(zos);
        
        // 地图基本信息
        dos.writeShort(map.width);
        dos.writeShort(map.height);
        dos.writeByte(mapf.getTileWidth());
        dos.writeByte(mapf.getTileHeight());
        
        // 图层信息
        for (IMapLayer layer : map.layers) {
            if (layer instanceof AccurateMapLayer) {
                // 精确地图层
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
                // 地图NPC层
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

        // 对文件内容进行压缩
        dos.flush();
        dos.close();
        return bos.toByteArray();
        
    }
    
    /**
     * 生成一个地图文件的内容。
     */
    public byte[] makeMapFile(GameArea area, GameMap map, GameMapInfo mapInfo, float ratio) throws Exception {
        // 生成一个压缩流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream zos = new GZIPOutputStream(bos);
        DataOutputStream dos = new DataOutputStream(zos);

        // 地图基本信息
        dos.writeByte(mapInfo.id);
        dos.writeUTF(mapInfo.name);
        dos.writeShort(map.width);
        dos.writeShort(map.height);

        // 图层信息
        dos.writeByte(map.layers.size());
        for (IMapLayer layer : map.layers) {
            if (layer instanceof AccurateMapLayer) {
                // 精确地图层
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
                // 模糊地图层
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
                // 地图NPC层
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
                
                // 如果地图层不是按顺序绘制的，这里按Y方向进行排序
                if (!nlayer.getForceAddOrderDraw()) {
                    Arrays.sort(npcArr);
                }
                
                dos.writeShort(npcArr.length);
                for (MapNPC npc : npcArr) {
                    writeNPC(dos, npc, map.parent.isLibMode);
                    
                    // 客户端依据animate下标为-1判断是MultiAnimNPC
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

        // 对文件内容进行压缩
        dos.flush();
        dos.close();
        return bos.toByteArray();
    }

    private void writeNPC(DataOutputStream dos, MapNPC npc, boolean isLibMode) throws IOException {
        if (isLibMode) { // 为了客户端YOrder排序代码重用，这个放在最后面
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
     * 保存TS文件。
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
     * 保存TS文件。(世界地图)
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
     * 得到一个颜色在颜色表中的index。
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
     * 把一个动画集文件打包存储。
     */
    public byte[] makeAnimatePackage(PipAnimateSet animateSet, boolean compressCTN, JPEGMergeOption jpegOption, CompressTextureOption compTexOption) throws Exception {
        PackageFile pkg = new PackageFile();
        pkg.setName("ANIMATESPKG");
        pkg.setVersion(0);
        
        // 添加CTN文件
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
        
        // 添加所有图片文件
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
    
    // 客户端显示遮挡区域数据（掩码16），以map文件中的为准
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
        // 打包gz文件
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
