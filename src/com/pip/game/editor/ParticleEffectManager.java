package com.pip.game.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import scryer.game_utils.BufferedObject;
import scryer.ogre.ps.ParticleSystemManager;
import scryer.ogre.ps.PsConfig;

import com.pip.game.data.ProjectData;
import com.pip.image.workshop.Settings;

/**
 * 管理项目相关的粒子效果。粒子效果服务器不能使用，所以单独拿出来，而不放到ProjectData里。
 * @author light.hu
 */
public class ParticleEffectManager extends Thread {
    protected static ParticleSystemManager psManager = null;
    protected static String[] particleEffectNames;
    
    protected static ParticleEffectManager currentThread = null;

    protected static void getTemplateNames(PsConfig config, List<String> outList) {
        int count = config.getTemplateCount();
        for (int i = 0; i < count; i++) {
            outList.add(config.getTemplate(i).getName());
        }
    }
    
    public static void init(ProjectData prj) {
        clear();
        psManager = new ParticleSystemManager();
        List<String> effectNameList = new ArrayList<String>();
        for (int i = 0; i < prj.config.particleFiles.length; i++) {
            File f = new File(prj.baseDir, prj.config.particleFiles[i]);
            PsConfig config = psManager.getOrCreatePsConfig(f.getAbsolutePath());
            config.prepare();
            getTemplateNames(config, effectNameList);
        }
        particleEffectNames = new String[effectNameList.size()];
        effectNameList.toArray(particleEffectNames);

        currentThread = new ParticleEffectManager();
        currentThread.start();
    }
    
    public static String[] getParticleEffectNames() {
        return particleEffectNames;
    }
    
    public static ParticleSystemManager getPsManager() {
        return psManager;
    }
    
    public static void clear() {
        if (psManager != null) {
            synchronized (psManager) {
                psManager = null;
            }
        }
        if (currentThread != null) {
            ParticleEffectManager t = currentThread;
            currentThread = null;
            try {
                t.interrupt();
            } catch (Exception e) {
            }
        }
    }
    
    public void run() {
        while (currentThread == this) {
            try {
                Thread.sleep(Settings.particleUpdateDelay);
            } catch (Exception e) {
            }
            if (psManager != null) {
                synchronized (psManager) {
                    psManager.update(Settings.particleUpdateDelay / 1000.0f);
                }
            }
        }
    }
}
