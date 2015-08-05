package com.pip.game.editor;

import java.io.File;

import com.pip.game.data.ProjectData;
import com.pip.mango.ps.ParticleManager;
import com.pip.mango.ps.ParticlePlayer;

/**
 * 管理项目相关的粒子效果。粒子效果服务器不能使用，所以单独拿出来，而不放到ProjectData里。
 * @author light.hu
 */
public class ParticleEffectManager extends Thread {
    protected static ParticleManager manager = null;
    
    public static void init(ProjectData prj) {
        clear();
        
        File[] files = new File[prj.config.particleFiles.length];
        for (int i = 0; i < prj.config.particleFiles.length; i++) {
            files[i] = new File(prj.baseDir, prj.config.particleFiles[i]);
        }
        manager = new ParticleManager(files);
    }
    
    public static void init(ProjectData prj, String[] files) {
        clear();

        File[] files2 = new File[files.length];
        for (int i = 0; i < files.length; i++) {
            files2[i] = new File(prj.baseDir, files[i]);
        }
        manager = new ParticleManager(files2);
    }
    
    public static String[] getParticleEffectNames() {
        return manager.getParticleEffectNames();
    }
    
    public static void clear() {
        if (manager != null) {
            manager.destroy();
            manager = null;
        }
    }
    
    public static ParticlePlayer createPlayer(String templateName) {
        return manager.createPlayer(templateName);
    }
}
