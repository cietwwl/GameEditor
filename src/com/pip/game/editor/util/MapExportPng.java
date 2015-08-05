package com.pip.game.editor.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

import com.pip.game.data.DataObject;
import com.pip.game.data.GameArea;
import com.pip.game.data.ProjectData;
import com.pip.mapeditor.data.GameMap;
import com.pip.mapeditor.data.MapExport;
import com.pip.mapeditor.data.MapFile;
import com.pipimage.png.PngEncoder;

public class MapExportPng {
    //不知道为啥，很耗内存，需要运行时加参数-Xms256m -Xmx512m
    public static void exportMapPng() {
        ProjectData.getActiveProject().serverMode = true;
        File baseDir = ProjectData.getActiveProject().baseDir;
        File targetDir = new File(baseDir, "./map_png");
        if(targetDir.exists() == false) {
            targetDir.mkdir();
        }
            
        clearTargetDir(targetDir);
        int count = 0;
        for (DataObject obj : ProjectData.getActiveProject().getDataListByType(GameArea.class)) {
            GameArea ga = (GameArea)obj;
            
            MapFile mapFile = new MapFile();
            File mapf = ga.getFile(1);

            try {
                mapFile.load(mapf);
            }
            catch (Throwable e1) {
                System.out.println("MapExportPng.exportMapPng()"+e1);
                continue;
            }
            ArrayList<GameMap> maps = mapFile.getMaps();
            int i = 0;
            for(GameMap map : maps) {                    
                MapExport mapView = new MapExport(map);    
                
                // 创建内存图片
                Image img = new Image(null, map.width, map.height);
                GC gc = new GC(img);
                mapView.drawMapOnBuffer(gc);
                gc.dispose();
                
                // 保存文件
                FileOutputStream fos = null;
                try {
                    PngEncoder enc = new PngEncoder(img);
                    File targetFile = new File(targetDir, ga.id+"_"+i+".png");
                    
                    fos = new FileOutputStream(targetFile);
                    enc.encode32(fos, false);
                    fos.close();
                    System.out.println("MapExportPng.exportMapPng() "+targetFile);
                    count ++;
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(fos != null) {
                            fos.close(); 
                        }                           
                    } catch(Exception e) {
                        
                    }
                }
                img.dispose();
                
                i ++;
            }

        }
        
        System.out.println("共导出:" + count + "个png");
        ProjectData.getActiveProject().serverMode = false;
    }

    /**
     * @param targetDir
     */
    private static void clearTargetDir(File targetDir) {
        File[] oldFiles = targetDir.listFiles();
        for (File f : oldFiles) {
            if (f.isFile() && !f.getName().equals(".cvsignore")) {
                f.delete();
            }
        }
    }
}
