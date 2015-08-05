package com.pip.game.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.pip.game.data.map.GameMapInfo;
import com.pip.mapeditor.data.GameMap;
import com.pip.mapeditor.data.MapFile;
import com.pip.util.Utils;

/**
 * 一个关卡的详细描述信息，这些信息被保存在关卡目录中的info.xml文件里。
 * @author lighthu
 */
public class GameAreaInfo {
    public GameArea owner;
    public List<GameMapInfo> maps = new ArrayList<GameMapInfo>();
    
    public GameAreaInfo(GameArea owner) {
        this.owner = owner;
    }
    
    public void load() throws Exception {
        Document doc = Utils.loadDOM(new File(owner.source, "info.xml"));
        loadFromXML(doc);
    }
    
    public void save() throws Exception {
        Utils.saveDOM(saveToXML(), new File(owner.source, "info.xml"));
    }
    
    public void save(MapFile mapFile) throws Exception {
        int i = 0;
        Element root = new Element("areainfo");
        for(GameMap gameMap:mapFile.getMaps()){
            Element elem = new Element("map");
            elem.addAttribute("id", String.valueOf(i));
            elem.addAttribute("name", "未命名场景");
            root.getMixedContent().add(elem);
        }
        Document doc = new Document(root);
        Utils.saveDOM(doc, new File(owner.source, "info.xml"));
    }
    
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Utils.saveDOM(saveToXML(), bos);
        return bos.toByteArray();
    }
    
    public Document saveToXML() throws Exception {
        Element root = new Element("areainfo");
        for (GameMapInfo map : maps) {
            root.getMixedContent().add(map.save());
        }
        return new Document(root);
    }
    
    public void loadFromXML(Document doc) throws Exception {
        List list = doc.getRootElement().getChildren("map");
        maps.clear();
        for (Object obj : list) {
            Element elem = (Element)obj;
            GameMapInfo gmi = null;
            if(ProjectData.getActiveProject().config.gameMapInfoClass != null && ProjectData.getActiveProject().config.gameMapInfoClass.trim().length() > 0){
                String className = ProjectData.getActiveProject().config.gameMapInfoClass.trim();
                ProjectConfig config = ProjectData.getActiveProject().config;
                Class clzz = config.getProjectClassLoader().loadClass(className);
                Constructor cons = clzz.getConstructor(owner.getClass());
                gmi = (GameMapInfo) cons.newInstance(owner);
            }else{
                gmi = new GameMapInfo(owner);
            }
            gmi.load(elem);
            
            // 有序插入
            int insertPos = 0;
            for (int i = 0; i < maps.size(); i++) {
                if (maps.get(i).id >= gmi.id) {
                    break;
                }
                insertPos++;
            }
            if (insertPos == -1) {
                maps.add(gmi);
            } else {
                maps.add(insertPos, gmi);
            }
        }
    }
}
