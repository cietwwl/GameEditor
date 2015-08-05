package com.pip.game.data;

import java.io.File;
import java.util.Arrays;

import org.jdom.Element;

import com.pip.game.data.i18n.I18NContext;
import com.pipimage.image.PipAnimateSet;
import com.pipimage.image.PipImage;

/**
 * ��Ϸ��������
 * @author lighthu
 */
public class Animation extends DataObject {
    /**
     * ������Ŀ��
     */
    public ProjectData owner;
    /**
     * ���汾�����ļ�����
     */
    public String[] animateFiles;
    /**
     * ���汾���������ļ�����
     */
    public String[] attackAnimateFiles;
    /**
     * ͷ��Xλ�ã���׼�棩��
     */
    public short headAreaX;
    /**
     * ͷ��Yλ�ã���׼�棩��
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
     * ȡ��ĳ�������汾���ļ��洢Ŀ¼��
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
     * ȡ��ĳ�������汾���ļ��洢Ŀ¼����ԣ���
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
     * ȡ��ĳ���汾����ͨ�����ļ���
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
     * ȡ��ĳ���汾��ս�������ļ���
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
     * ����ĳ���汾����ͨ�����ļ���
     */
    public void setAnimateFile(int format, File file) {
        animateFiles[format] = file.getName();
    }
    
    /**
     * ����ĳ���汾�Ĺ��������ļ���
     */
    public void setAttackAnimateFile(int format, File file) {
        attackAnimateFiles[format] = file.getName();
    }

    /**
     * �������������Խ��й��ʻ������������Ҫ���ʻ����ַ���������ȡ������context�в��ҷ�������
     * @param context
     * @return �����ĳ�����Ա��滻������true�����򷵻�false��
     */
    public boolean i18n(I18NContext context) {
        return false;
    }
    
    /**
     * ��ȡ�����ļ���С
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
     * ��ȡ�����ڴ��С
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
