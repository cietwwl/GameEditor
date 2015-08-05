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
 * һ�����򣨹ؿ�����������Ϣ��һ���ؿ�ռ��һ��Ŀ¼������Ӧ����2���ļ�����ͼ�ļ�game.map�������ļ�info.xml��
 */
public class GameArea extends DataObject {
    /*
     * һ����ͼ�����á�
     */
    public static class MapRef {
        // map�ļ���pipLibĿ¼�µ����·��
        public String mapFilePath;
        // ��ɫģʽ��-2��ʾʹ��ԭʼɫ�ʣ�-1��ʾ�ñ�׼��ʽѹ����256ɫ��>=0��ʾ��ָ���ĵ�ɫ����ɫ��-3��ʾʹ��JPEGѹ����-4��ʾʹ��ѹ������
        public int colorMode;
        // JPEGѹ������
        public JPEGMergeOption jpegOption;
        // ѹ���������
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
                return mapFilePath + "(�Զ�ѹ����256ɫ)";
            } else {
                return mapFilePath + "(ʹ�õ�ͼ��ɫ��" + (colorMode + 1) + ")";
            }
        }
    }
    
    /**
     * ������Ŀ��
     */
    public ProjectData owner;
    /**
     * �ؿ���Ӧ��Ŀ¼��
     */
    public java.io.File source;
    /**
     * ���õ�ͼ�ļ�,game.map���ڹؿ�Ŀ¼���ˡ�һ���ؿ���Ӧ�����ͼ����Ӧ��game.conf.xml�����õĵ�ͼ��ʽ��
     */
    public MapRef[] maps;
    /**
     * ��ͼ�ļ����չؿ�ID��0��ʾû�в��չؿ�����������˲��չؿ��������ɱ��ؿ���pkg�ļ���ʱ�򣬲�������κε�ͼNPC�Ķ�����ͼƬ��������ȫ�ò��չؿ�
     * ��Ķ�����ͼƬ����Ҫ����չؿ��ͱ��ؿ���map�ļ��У����õĶ�����ȫһ����
     */
    public int refAreaID = 0;
    /**
     * ���ʱ�Ƿ�����õĶ����ļ���������������ֻ������õ��Ķ������С�
     */
    public boolean packingFullAnimate = false;
    /**
     * ����ĵ�ͼ�ļ��������ڷ�����ģʽ��
     */
    private MapFile mapFile;
    /**
     * ����Ĺؿ���Ϣ�ļ��������ڷ�����ģʽ��
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
     * �õ��ؿ���Ӧ�ĵ�ͼ�ļ����ݣ������ڷ�����ģʽ����
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
     * �õ��ؿ���Ӧ�ĵ�ͼ��Ϣ�ļ��������ڷ�����ģʽ����
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
     * �������������Խ��й��ʻ������������Ҫ���ʻ����ַ���������ȡ������context�в��ҷ�������
     * @param context
     * @return �����ĳ�����Ա��滻������true�����򷵻�false��
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

            // ����pkg�ļ�������ĵ�ͼ����
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

    
    // �����ͼ�ļ��еĵ�ͼ����
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
