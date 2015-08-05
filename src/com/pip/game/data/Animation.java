package com.pip.game.data;

import java.io.File;
import java.util.Arrays;

import org.jdom.Element;

import com.pip.game.data.i18n.I18NContext;
import com.pipimage.image.PipAnimateSet;
import com.pipimage.image.PipImage;

/**
 * 游戏动画对象。
 * @author lighthu
 */
public class Animation extends DataObject {
    /**
     * 所属项目。
     */
    public ProjectData owner;
    /**
     * 各版本动画文件名。
     */
    public String[] animateFiles;
    /**
     * 各版本攻击动画文件名。
     */
    public String[] attackAnimateFiles;
    /**
     * 头像X位置（标准版）。
     */
    public short headAreaX;
    /**
     * 头像Y位置（标准版）。
     */
    public short headAreaY;
    
    public Animation(ProjectData owner) {
        this.owner = owner;
        int size = owner.config.animationFormats.size();
        animateFiles = new String[size];
        attackAnimateFiles = new String[size];
    }

    public boolean equals(Object o){
        return this == o;
    }
    
    public String toString() {
        return id + ": " + title;
    }

    public void update(DataObject obj) {
        Animation oo = (Animation)obj;
        id = oo.id;
        title = oo.title;
        description = oo.description;
        setCategoryName(oo.getCategoryName());
        System.arraycopy(oo.animateFiles, 0, animateFiles, 0, animateFiles.length);
        System.arraycopy(oo.attackAnimateFiles, 0, attackAnimateFiles, 0, attackAnimateFiles.length);
        headAreaX = oo.headAreaX;
        headAreaY = oo.headAreaY;
    }
    
    public DataObject duplicate() {
        Animation ret = new Animation(owner);
        ret.update(this);
        return ret;
    }

    @Override
    public boolean changed(DataObject obj) {
        Animation oo = (Animation)obj;
        return !(Arrays.equals(animateFiles, oo.animateFiles) && Arrays.equals(attackAnimateFiles, oo.attackAnimateFiles) &&
                headAreaX == oo.headAreaX && headAreaY == oo.headAreaY);
    }
    
    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
        description = elem.getAttributeValue("description");
        if(description == null){
            description = "";
        }
        setCategoryName(elem.getAttributeValue("category"));
        if (getWholeCategoryName() == null) {
            setCategoryName("");
        }
        for (int i = 0; i < animateFiles.length; i++) {
            String suffix = i == 0 ? "" : String.valueOf(i);
            animateFiles[i] = elem.getAttributeValue("source" + suffix);
            attackAnimateFiles[i] = elem.getAttributeValue("attacksource" + suffix);
        }
        headAreaX = Short.parseShort(elem.getAttributeValue("headAreaX"));
        headAreaY = Short.parseShort(elem.getAttributeValue("headAreaY"));
    }
    
    public Element save() {
        Element ret = new Element("animation");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        ret.addAttribute("description", description);
        if (getWholeCategoryName() != null) {            
            ret.addAttribute("category", getWholeCategoryName());
        }
        
        for (int i = 0; i < animateFiles.length; i++) {
            String suffix = i == 0 ? "" : String.valueOf(i);
            if (animateFiles[i] != null) {
                ret.addAttribute("source" + suffix, animateFiles[i]);
            }
            if (attackAnimateFiles[i] != null) {
                ret.addAttribute("attacksource" + suffix, attackAnimateFiles[i]);
            }
        }
        ret.addAttribute("headAreaX", String.valueOf(headAreaX));
        ret.addAttribute("headAreaY", String.valueOf(headAreaY));
        return ret;
    }
    
    public boolean depends(DataObject obj) {
        return false;
    }
    
    /**
     * 取得某个动画版本的文件存储目录。
     * @param format
     * @return
     */
    public File getAnimateDir(int format) {
        AnimationFormat af = owner.config.animationFormats.get(format);
        if (af.dirName.length() == 0) {
            return new File(owner.baseDir, "Animations");
        } else {
            return new File(owner.baseDir, "Animations/" + af.dirName);
        }
    }
    
    /**
     * 取得某个动画版本的文件存储目录（相对）。
     * @param format
     * @return
     */
    public String getAnimatePath(int format) {
        AnimationFormat af = owner.config.animationFormats.get(format);
        if (af.dirName.length() == 0) {
            return "Animations/";
        } else {
            return "Animations/" + af.dirName + "/";
        }
    }
    
    /**
     * 取得某个版本的普通动画文件。
     * @param format
     * @return
     */
    public File getAnimateFile(int format) {
        if (animateFiles[format] == null) {
            return null;
        } else {
            return new File(getAnimateDir(format), animateFiles[format]);
        }
    }
    
    /**
     * 取得某个版本的战斗动画文件。
     * @param format
     * @return
     */
    public File getAttackAnimateFile(int format) {
        if (attackAnimateFiles[format] == null) {
            return null;
        } else {
            return new File(getAnimateDir(format), attackAnimateFiles[format]);
        }
    }
    
    /**
     * 设置某个版本的普通动画文件。
     */
    public void setAnimateFile(int format, File file) {
        animateFiles[format] = file.getName();
    }
    
    /**
     * 设置某个版本的攻击动画文件。
     */
    public void setAttackAnimateFile(int format, File file) {
        attackAnimateFiles[format] = file.getName();
    }

    /**
     * 对这个对象的属性进行国际化处理，如果有需要国际化的字符串，则提取出来到context中查找翻译结果。
     * @param context
     * @return 如果有某个属性被替换，返回true，否则返回false。
     */
    public boolean i18n(I18NContext context) {
        return false;
    }
    
    /**
     * 获取动画文件大小
     * @param format
     * @return
     */
    public int getAnimateFileSize(int format){
        try{
            long fileSize = 0;
            
            File ctnFile = new File(getAnimateDir(format), animateFiles[format].substring(0, animateFiles[format].length() - 1) + "n");
            fileSize += ctnFile.length();

            PipAnimateSet animate = new PipAnimateSet();
            animate.load(getAnimateFile(format));

            for(int i = 0; i < animate.getFileCount(); i++){
                File pipFile = new File(ctnFile.getParentFile(), animate.getSourceFile(i).getName());
                fileSize += pipFile.length();
            }
            
            return (int)fileSize;
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * 获取动画内存大小
     * @param format
     * @return
     */
    public int getAnimateMemorySize(int format){
        try{
            PipAnimateSet animate = new PipAnimateSet();
            animate.load(getAnimateFile(format));
            
            int memorySize = 0;
            for(int i = 0; i < animate.getFileCount(); i++){
                PipImage pip = animate.getSourceImage(i);
                int mem = pip.getEstimateMemory();
                memorySize += mem * 4 + pip.getImagePalettes().size();
            }
            
            return memorySize;
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }
}
