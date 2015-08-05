package com.pip.game.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.jdom.Element;

import com.pip.game.data.i18n.I18NContext;
import com.pip.game.data.i18n.I18NUtils;
import com.pip.game.data.map.GameMapExit;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.data.map.MultiTargetMapExit;
import com.pip.game.data.pkg.PackageFile;
import com.pip.game.data.pkg.PackageFileItem;
import com.pip.game.data.vehicle.XyGameMapVehicle;
import com.pip.mapeditor.data.MapFile;
import com.pip.util.Utils;
import com.pipimage.image.CompressTextureOption;
import com.pipimage.image.JPEGMergeOption;

/**
 * 一个区域（关卡）的描述信息。一个关卡占用一个目录，里面应该有2个文件：地图文件game.map和描述文件info.xml。
 */
public class GameArea extends DataObject {
    /*
     * 一个地图的引用。
     */
    public static class MapRef {
        // map文件在pipLib目录下的相对路径
        public String mapFilePath;
        // 颜色模式：-2表示使用原始色彩，-1表示用标准方式压缩到256色，>=0表示用指定的调色板缩色，-3表示使用JPEG压缩，-4表示使用压缩纹理
        public int colorMode;
        // JPEG压缩参数
        public JPEGMergeOption jpegOption;
        // 压缩纹理参数
        public CompressTextureOption compressTextureOption;
        
        public MapRef dup() {
            MapRef ret = new MapRef();
            ret.mapFilePath = mapFilePath;
            ret.colorMode = colorMode;
            ret.jpegOption = jpegOption == null ? null : jpegOption.dup();
            ret.compressTextureOption = compressTextureOption == null ? null : compressTextureOption.dup();
            return ret; 
        }
        
        public String toString() {
            if (colorMode == -4) {
                return mapFilePath + "(" + compressTextureOption.getFormatName() + ":" + compressTextureOption.sizeWidth + "x" + 
                        compressTextureOption.sizeHeight + ",border=" + compressTextureOption.borderWidth + ")";
            } else if (colorMode == -3) {
                return mapFilePath + "(JPEG:" + jpegOption.quality + ",alpha=" + jpegOption.alphaBits + ",border=" + jpegOption.borderWidth + ")"; 
            } else if (colorMode == -2) {
                return mapFilePath;
            } else if (colorMode == -1) {
                return mapFilePath + "(自动压缩到256色)";
            } else {
                return mapFilePath + "(使用地图调色板" + (colorMode + 1) + ")";
            }
        }
    }
    
    /**
     * 所属项目。
     */
    public ProjectData owner;
    /**
     * 关卡对应的目录。
     */
    public java.io.File source;
    /**
     * 引用地图文件,game.map不在关卡目录里了。一个关卡对应多个地图，对应于game.conf.xml里配置的地图格式。
     */
    public MapRef[] maps;
    /**
     * 地图文件参照关卡ID，0表示没有参照关卡。如果设置了参照关卡，则生成本关卡的pkg文件的时候，不会包含任何地图NPC的动画和图片，而是完全用参照关卡
     * 里的动画和图片。这要求参照关卡和本关卡的map文件中，引用的动画完全一样。
     */
    public int refAreaID = 0;
    /**
     * 打包时是否把引用的动画文件整体打包，而不是只打包引用到的动画序列。
     */
    public boolean packingFullAnimate = false;
    /**
     * 载入的地图文件（仅用于服务器模式）
     */
    private MapFile mapFile;
    /**
     * 载入的关卡信息文件（仅用于服务器模式）
     */
    public GameAreaInfo areaInfo; 

    public GameArea(ProjectData owner) {
        this.owner = owner;
        maps = new MapRef[owner.config.mapFormats.size()];
    }

    public int getID() {
        return id;
    }
    
    public String toString() {
        return id + ": " + title;
    }

    public boolean equals(Object o) {
        return this == o;
    }
    
    public void update(DataObject obj) {
        GameArea oo = (GameArea)obj;
        id = oo.id;
        source = oo.source;
        title = oo.title;
        description = oo.description;
        setCategoryName(oo.getCategoryName());
        mapFile = null;
        areaInfo = null;
        for (int i = 0; i < maps.length; i++) {
            if (oo.maps[i] == null) {
                maps[i] = null;
            } else {
                maps[i] = oo.maps[i].dup();
            }
        }
        refAreaID = oo.refAreaID;
        packingFullAnimate = oo.packingFullAnimate;
    }
    
    public DataObject duplicate() {
        GameArea ret = new GameArea(owner);
        ret.update(this);
        return ret;
    }

    @Override
    public boolean changed(DataObject obj) {
        GameArea oo = (GameArea)obj;
        try {
            byte[] b1 = getMapFile().toByteArray();
            byte[] b2 = oo.getMapFile().toByteArray();
            if (!Arrays.equals(b1, b2)) {
                return true;
            }
            b1 = getAreaInfo().toByteArray();
            b2 = oo.getAreaInfo().toByteArray();
            if (!Arrays.equals(b1, b2)) {
                return true;
            }
            if (refAreaID != oo.refAreaID) {
                return true;
            }
            if (packingFullAnimate != oo.packingFullAnimate) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }
    
    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        source = new java.io.File(owner.baseDir, "Areas/" + elem.getAttributeValue("source"));
        title = elem.getAttributeValue("title");
        description = elem.getAttributeValue("description");
        if(description == null){
            description = "";
        }
        setCategoryName(elem.getAttributeValue("category"));
        if (getWholeCategoryName() == null) {
            setCategoryName("");
        }
        String refAreaIDStr = elem.getAttributeValue("refarea");
        if (refAreaIDStr != null) {
            refAreaID = Integer.parseInt(refAreaIDStr);
        }
        packingFullAnimate = "true".equals(elem.getAttributeValue("packingfullanimate"));
        maps[0] = new MapRef();
        loadMapRef(maps[0], elem.getAttributeValue("fileRef"));
        for (int i = 1; i < maps.length; i++) {
            if (elem.getAttribute("fileRef" + i) != null) {
                maps[i] = new MapRef();
                loadMapRef(maps[i], elem.getAttributeValue("fileRef" + i));
            } else {
                maps[i] = null;
            }
        }
    }
    
    private void loadMapRef(MapRef mr, String str) {
        int pos = str.indexOf('#');
        if (pos == -1) {
            mr.mapFilePath = str;
            mr.colorMode = -2;
        } else {
            mr.mapFilePath = str.substring(0, pos);
            String[] ext = str.substring(pos + 1).split("#");
            mr.colorMode = Integer.parseInt(ext[0]);
            if (mr.colorMode == -3) {
                float quality = Float.parseFloat(ext[1]);
                int alphaBits = Integer.parseInt(ext[2]);
                int borderWidth = Integer.parseInt(ext[3]);
                mr.jpegOption = new JPEGMergeOption(quality, alphaBits, borderWidth);
            } else if (mr.colorMode == -4) {
                String format = ext[1];
                int texWidth = Integer.parseInt(ext[2]);
                int texHeight = Integer.parseInt(ext[3]);
                int borderWidth = Integer.parseInt(ext[4]);
                mr.compressTextureOption = new CompressTextureOption(format, texWidth, texHeight);
                mr.compressTextureOption.borderWidth = borderWidth;
            }
        }
    }
    
    public Element save() {
        Element ret = new Element("area");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("source", source.getName());
        ret.addAttribute("title", title);
        ret.addAttribute("description", description);
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }
        if (refAreaID != 0) {
            ret.addAttribute("refarea", String.valueOf(refAreaID));
        }
        if (packingFullAnimate) {
            ret.addAttribute("packingfullanimate", "true");
        }
        ret.addAttribute("fileRef", saveMapRef(maps[0]));
        for (int i = 1; i < maps.length; i++) {
            if (maps[i] != null) {
                ret.addAttribute("fileRef" + i, saveMapRef(maps[i]));
            }
        }
        return ret;
    }
    
    private String saveMapRef(MapRef mr) {
        if (mr.colorMode == -2) {
            return mr.mapFilePath;
        } else if (mr.colorMode == -3) {
            return mr.mapFilePath + "#" + mr.colorMode + "#" + mr.jpegOption.quality + "#" + mr.jpegOption.alphaBits + "#" + mr.jpegOption.borderWidth;
        } else if (mr.colorMode == -4) {
            return mr.mapFilePath + "#" + mr.colorMode + "#" + mr.compressTextureOption.format + "#" + mr.compressTextureOption.sizeWidth + "#" +
                    mr.compressTextureOption.sizeHeight + "#" + mr.compressTextureOption.borderWidth;
        } else {
            return mr.mapFilePath + "#" + mr.colorMode;
        }
    }
    
    public boolean depends(DataObject obj) {
        return false;
    }

    /**
     * 得到关卡对应的地图文件内容（仅用于服务器模式）。
     */
    public MapFile getMapFile() {
        if (!owner.serverMode) {
            throw new IllegalArgumentException();
        }
        if (mapFile == null) {
            try {
                mapFile = new MapFile();
                mapFile.load(getFile(0), false);
            } catch (Exception e) {
                System.out.println("GameArea.getMapFile() error map:"+ getFile(0));
                e.printStackTrace();
            }
        }
        return mapFile;
    }
    
    /**
     * 得到关卡对应的地图信息文件（仅用于服务器模式）。
     */
    public GameAreaInfo getAreaInfo() {
    	if (!owner.serverMode) {
    		throw new IllegalArgumentException();
    	}
    	if (areaInfo == null) {
    		try {
	    		areaInfo = new GameAreaInfo(this);
	    		areaInfo.load();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	return areaInfo;
    }
    
    public File getFile(int format) {
        MapRef ref = maps[format];
        if (ref == null) {
            return null;
        } else {
            return new File(owner.config.getPipLibDir(), ref.mapFilePath);
        }
    }
    
    public void setFile(int format, File file, int colorMode, JPEGMergeOption jpegOption, CompressTextureOption compTexOption) throws IOException {
        String path = Utils.getRelatePath(file.getCanonicalPath(), owner.config.getPipLibDir().getCanonicalPath());
        if (maps[format] == null) {
            maps[format] = new MapRef();
        }
        maps[format].mapFilePath = path;
        maps[format].colorMode = colorMode;
        maps[format].jpegOption = jpegOption;
        maps[format].compressTextureOption = compTexOption;
    }
    
    /**
     * 对这个对象的属性进行国际化处理，如果有需要国际化的字符串，则提取出来到context中查找翻译结果。
     * @param context
     * @return 如果有某个属性被替换，返回true，否则返回false。
     */
    public boolean i18n(I18NContext context) {
        try {
            GameAreaInfo areaInfo = new GameAreaInfo(this);
            boolean changed = false;
            String tmp;
            areaInfo.load();
            for (GameMapInfo gmi : areaInfo.maps) {
                tmp = context.input(gmi.name, "Scene:" + gmi.name);
                if (tmp != null){ 
                    gmi.name = tmp;
                    changed = true;
                }
                for (GameMapObject gmo : gmi.objects) {
                    if (gmo instanceof GameMapNPC) {
                        GameMapNPC npc = (GameMapNPC)gmo;
                        tmp = context.input(npc.name, "Scene:" + gmi.name);
                        if (tmp != null) {
                            npc.name = tmp;
                            if (tmp.length() == 0) {
                                npc.visible = false;
                            }
                            changed = true;
                        }
                        tmp = context.input(npc.functionName, "Scene:" + gmi.name);
                        if (tmp != null) {
                            npc.functionName = tmp;
                            changed = true;
                        }
                        tmp = context.input(npc.functionScript, "Scene:" + gmi.name);
                        if (tmp != null) {
                            npc.functionScript = tmp;
                            changed = true;
                        }
                        tmp = context.input(npc.searchName, "Scene:" + gmi.name);
                        if (tmp != null) {
                            npc.searchName = tmp;
                            changed = true;
                        }
                    } else if (gmo instanceof GameMapExit) {
                        GameMapExit exit = (GameMapExit)gmo;
                        tmp = context.input(exit.positionVarName, "Scene:" + gmi.name);
                        if (tmp != null) {
                            exit.positionVarName = tmp;
                            changed = true;
                        }
                        tmp = context.input(exit.name, "Scene:" + gmi.name);
                        if (tmp != null) {
                            exit.name = tmp;
                            changed = true;
                        }
                        tmp = context.input(exit.constraintsDes, "Scene:" + gmi.name);
                        if (tmp != null) {
                            exit.constraintsDes = tmp;
                            changed = true;
                        }
                        if (I18NUtils.processExpressionList(exit.constraints, context, 0, "Scene:" + gmi.name)) {
                            changed = true;
                        }
                    } else if (gmo instanceof MultiTargetMapExit){
                        for(GameMapExit exit : ((MultiTargetMapExit)gmo).exitList){
                            tmp = context.input(exit.positionVarName, "Scene:" + gmi.name);
                            if (tmp != null) {
                                exit.positionVarName = tmp;
                                changed = true;
                            }
                            tmp = context.input(exit.name, "Scene:" + gmi.name);
                            if (tmp != null) {
                                exit.name = tmp;
                                changed = true;
                            }
                            tmp = context.input(exit.constraintsDes, "Scene:" + gmi.name);
                            if (tmp != null) {
                                exit.constraintsDes = tmp;
                                changed = true;
                            }
                            if (I18NUtils.processExpressionList(exit.constraints, context, 0, "Scene:" + gmi.name)) {
                                changed = true;
                            }
                        }
                    }else if (gmo instanceof XyGameMapVehicle) {
                        XyGameMapVehicle vehicle = (XyGameMapVehicle)gmo;
                        tmp = context.input(vehicle.name, "Scene:" + gmi.name);
                        if (tmp != null) {
                            vehicle.name = tmp;
                            if (tmp.length() == 0) {
                                vehicle.visible = false;
                            }
                            changed = true;
                        }
                        tmp = context.input(vehicle.functionName, "Scene:" + gmi.name);
                        if (tmp != null) {
                            vehicle.functionName = tmp;
                            changed = true;
                        }
                        tmp = context.input(vehicle.searchName, "Scene:" + gmi.name);
                        if (tmp != null) {
                            vehicle.searchName = tmp;
                            changed = true;
                        }
                    }
                }
            }
            if (changed) {
                areaInfo.save();
            }

            // 处理pkg文件里的中文地图名字
            File[] files = source.listFiles();
            for (File f : files) {
                if (f.isFile() && f.getName().toLowerCase().endsWith(".pkg")) {
                    if (i18nPackageFile(f, context)) {
                        changed = true;
                    }
                }
            }
            
            return changed;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    
    // 处理地图文件中的地图名字
    protected boolean i18nPackageFile(File file, I18NContext context) throws IOException {
        PackageFile pkg = new PackageFile();
        pkg.load(file);
        boolean changed = false;
        for (PackageFileItem fileItem : pkg.getFiles()) {
            if (fileItem.name.endsWith(".m")) {
                byte[] mapFileContent = Utils.decomppressGZIP(fileItem.data);
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(mapFileContent));
                byte mapID = dis.readByte();
                String mapName = dis.readUTF();
                String tmp = context.input(mapName, file.getName());
                if (tmp != null) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(bos);
                    dos.writeUTF(mapName);
                    dos.flush();
                    int oldLen = bos.size();
                    bos.reset();
                    dos = new DataOutputStream(bos);
                    dos.writeUTF(tmp);
                    byte[] newContent = bos.toByteArray();
                    byte[] newFileBuff = new byte[mapFileContent.length - oldLen + newContent.length];
                    newFileBuff[0] = mapFileContent[0];
                    System.arraycopy(newContent, 0, newFileBuff, 1, newContent.length);
                    System.arraycopy(mapFileContent, 1 + oldLen, newFileBuff, 1 + newContent.length, mapFileContent.length - 1 - oldLen);
                    fileItem.data = Utils.compressGZIP(newFileBuff);
                    changed = true;
                }
            }
        }
        if (changed) {
            pkg.save(file);
        }
        return changed;
    }
}
