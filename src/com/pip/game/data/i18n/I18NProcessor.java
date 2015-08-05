package com.pip.game.data.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import com.pip.game.data.ProjectData;
import com.pip.game.editor.quest.GameAreaCache;
import com.pip.gtl.etf.ETFFile;
import com.pipimage.utils.Utils;

/**
 * ʵ��ִ��I18N�������ࡣ�ṩ�����������Ŀ�����ļ���JavaԴ���롣
 * @author lighthu
 */
public class I18NProcessor {
    public static final int MODE_PROJECT = 1;
    public static final int MODE_JAVA = 2;
    public static final int MODE_SCRIPT = 3;
    
    private int opMode;
    private ProjectData project;
    private File rootDir;
    private LocaleConfig targetLocale;
    
    /**
     * ��ʼ��һ��I18NProcessor���Ա��ػ�һ����Ŀ��
     * @param proj ��Ŀ
     * @param locale Ŀ������
     */
    public I18NProcessor(ProjectData proj, LocaleConfig locale) {
        opMode = MODE_PROJECT;
        project = proj;
        targetLocale = locale;
    }
    
    /**
     * ��ʼ��һ��I18NProcessor���Ա��ػ�һ��Ŀ¼�µ�����Java���롣
     * @param root ��ĿsrcĿ¼
     * @param locale Ŀ������
     */
    public I18NProcessor(File root, LocaleConfig locale) {
        opMode = MODE_JAVA;
        rootDir = root;
        targetLocale = locale;
    }
    
    /**
     * ��ʼ��һ��I18NProcessor���Ա��ػ�һ��Ŀ¼�µ�����Java�����ű���
     * @param root ��ĿsrcĿ¼
     * @param locale Ŀ������
     */
    public I18NProcessor(File root, LocaleConfig locale, int mode) {
        opMode = mode;
        rootDir = root;
        targetLocale = locale;
    }
    
    /**
     * ִ�б��ػ�����
     */
    public void process(boolean change, boolean reportError) throws Exception {
        I18NError.clear();
        try {
            if (opMode == MODE_PROJECT) {
                processProject(change);
            } else if (opMode == MODE_JAVA) {
                processJava(change);
            } else if (opMode == MODE_SCRIPT) {
                processScript(change);
            }
        } catch (Exception e) {
            if (reportError) {
                throw e;
            } else {
                I18NError.error(null, e.toString(), e);
            }
        }
    }
    
    /*
     * ���ػ�һ����Ŀ��
     */
    private void processProject(boolean change) throws Exception {
        // ��һ�����ѱ���Ŀ�е������ļ����Ƶ����Ŀ¼
        targetLocale.outputDir.mkdirs();
        Set<String> excludes = new HashSet<String>();
        excludes.add("CVS");
        excludes.add(".svn");
        excludes.add("Branches");
        excludes.add("client_pkg");
        excludes.add("scripts");
        copyFiles(project.baseDir, targetLocale.outputDir, excludes, true);
        
        // �ڶ����������˰汾�����ļ�����Ҫ�ڱ��ػ�֮ǰ�����ģ���Ŀ��Ŀ¼��
        /*�ڹ��ʻ�ǰ��Ҫ��revision_resources������copy����ӦdataĿ¼��*/
        excludes.remove("scripts");
        copyFiles(targetLocale.revisionResourceDir, targetLocale.outputDir, excludes, false);

        // ��������������Ϣ�ļ�����Ŀ��Ŀ¼ִ�б��ػ�
        MessageFile mf = new MessageFile(targetLocale.dataMessageFile, "zh_CN", targetLocale.id);
        ProjectData newProj = new ProjectData();
        newProj.load(targetLocale.outputDir, ProjectData.getActiveProject().config.getProjectClassLoader());
        String oldEncoding = System.getProperty("pip_xml_encoding");
        System.setProperty("pip_xml_encoding", targetLocale.encoding);
        String[][] newStrs;
        if (change) {
            newStrs = I18NUtils.doI18N(newProj, mf);
        } else {
            newStrs = I18NUtils.findI18NRelatedStrings(newProj, mf);
        }
        if (newStrs.length > 0) {
            for (String[] s : newStrs) {
                mf.addString(s[0], s[1]);
            }
            mf.save();
        }
        
        // У������NPC���ú�λ������
        ProjectData oldProj = ProjectData.getActiveProject();
        ProjectData.setActiveProject(newProj);
        GameAreaCache.clearAreaInfo();
        newProj.validateMixedText(false);
        GameAreaCache.clearAreaInfo();
        ProjectData.setActiveProject(oldProj);
        if (oldEncoding != null) {
            System.setProperty("pip_xml_encoding", oldEncoding);
        }
        
        // �Խű�����ִ�д���
        if (false) {
            mf = new MessageFile(targetLocale.sourceCodeMessageFile, "zh_CN", targetLocale.id);
            if (change) {
                newStrs = I18NUtils.doI18NScript(newProj.baseDir, mf);
            } else {
                newStrs = I18NUtils.findI18NRelatedScriptStrings(newProj.baseDir, mf);
            }
            if (newStrs.length > 0) {
                for (String[] s : newStrs) {
                    mf.addString(s[0], s[1]);
                }
                mf.save();
            }
        }
        
        // ���Ĳ��������˰汾�����ļ���Ŀ��Ŀ¼��
        copyFiles(targetLocale.specialResourceDir, targetLocale.outputDir, excludes, false);
        
        // ���岽����������client.pkg
        // Light: �¸�ʽ�Ĺؿ��У��Ѿ�û�к͹��ʻ��йص������ˣ����Բ���Ҫ�������ɹؿ��ļ���
        //newProj.makeClientPackages();
    }
    
    //�����ļ��б�
    public static boolean needETD = false;
    private static boolean canFileOpt(String name){
        boolean b = true;
        // ����etd
        if(name.endsWith(".etd") && !needETD){
            b = false;
//        } else if (name.endsWith(".pkg")) {
//            b = false;
        } else if (name.equals("fileversion.xml")) {
            b = false;
        } else if (name.equals("client.data")) {
            b = false;
//        } else if (name.equals("client_pkg.xml")) {
//            b=false;
        }
        return b;
    }
    
    /*
     * ���Ŀ��Ŀ¼�Լ���Ŀ¼�е������ļ�������CVSĿ¼����
     */
    private static void clearDir(File dir, Set<String> excludes) {
        List<File> cache = new ArrayList<File>();
        cache.add(dir);
        while (cache.size() > 0) {
            File d = cache.remove(0);
            File[] ffs = d.listFiles();
            if(ffs != null){
                for (File ff : ffs) {
                    if (ff.isFile()) {
                        ff.delete();
                    } else if (!excludes.contains(ff.getName())) {
                        cache.add(ff);
                    }
                }
            }
            
        }
    }
    
    /*
     * ����Ŀ¼�������ļ���Ŀ��Ŀ¼������CVSĿ¼����
     * @param src ԴĿ¼
     * @param dest Ŀ��Ŀ¼
     * @param excludes �ų�Ŀ¼
     * @param sync �Ƿ�ͬ��ģʽ����ͬ��ģʽ�£�����Ŀ��Ŀ¼�ж�����ļ�����ɾ����
     */
    public static void copyFiles(File src, File dst, Set<String> excludes, boolean sync) {
        List<String> cache = new ArrayList<String>();
        cache.add(".");
        while (cache.size() > 0) {
            String path = cache.remove(0);
            File d1 = new File(src, path);
            File d2 = new File(dst, path);
            
            // �ҳ�Ŀ��Ŀ¼�е������ļ�
            d2.mkdirs();
            String[] ffs2 = d2.list();
            Set<String> targetSet = new HashSet<String>();
            for (String f : ffs2) {
                targetSet.add(f);
            }
            
            // ����ԴĿ¼��Ŀ��Ŀ¼
            File[] ffs = d1.listFiles();
            for (File ff : ffs) {
                targetSet.remove(ff.getName());
                if (ff.isFile()) {
                    if(canFileOpt(ff.getName())){
                        try {
                            Utils.copyFile(ff, new File(d2, ff.getName()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (!excludes.contains(ff.getName())) {
                    cache.add(path + "/" + ff.getName());
                }
            }
            
            // ɾ��Ŀ��Ŀ¼�ж����ļ�
            if (sync) {
                for (String name : targetSet) {
                    if (!excludes.contains(name)) {
                        File f = new File(d2, name);
                        if (f.isFile()) {
                            f.delete();
                        } else {
                            clearDir(f, excludes);
                        }
                    }
                }
            }
        }
    }
    
    /*
     * ���ػ�һ��Ŀ¼�����е�Java�ļ���AS�ļ���
     */
    private void processJava(boolean change) throws Exception {
        MessageFile mf = new MessageFile(targetLocale.sourceCodeMessageFile, "zh_CN", targetLocale.id);

        // ����Java�ļ�
        String[][] newStrs;
        boolean needSave = false;
        if (change) {
            newStrs = I18NUtils.doI18NJava(rootDir, mf, "UTF-8", targetLocale.encoding);
        } else {
            newStrs = I18NUtils.findI18NRelatedJavaStrings(rootDir, mf, "UTF-8", targetLocale.encoding);
        }
        if (newStrs.length > 0) {
            for (String[] s : newStrs) {
                mf.addString(s[0], s[1]);
            }
            needSave = true;
        }

        // ����AS�ļ�
        if (change) {
            newStrs = I18NUtils.doI18NActionScript(rootDir, mf, "UTF-8", targetLocale.encoding);
        } else {
            newStrs = I18NUtils.findI18NRelatedActionScriptStrings(rootDir, mf, "UTF-8", targetLocale.encoding);
        }
        if (newStrs.length > 0) {
            for (String[] s : newStrs) {
                mf.addString(s[0], s[1]);
            }
            needSave = true;
        }

        // ����XML�ļ�
        if (change) {
            newStrs = I18NUtils.doI18NXML(rootDir, mf, "UTF-8", targetLocale.encoding);
        } else {
            newStrs = I18NUtils.findI18NRelatedXMLStrings(rootDir, mf, "UTF-8", targetLocale.encoding);
        }
        if (newStrs.length > 0) {
            for (String[] s : newStrs) {
                mf.addString(s[0], s[1]);
            }
            needSave = true;
        }
        if (needSave) {
            mf.save();
        }
    }
    
    private void processScript(boolean change) throws Exception {
        MessageFile mf = new MessageFile(targetLocale.sourceCodeMessageFile, "zh_CN", targetLocale.id);
        I18NUtils.doI18NScriptSpecifiedDir(rootDir, mf);
    }
    
    public static void main(String[] args) throws Exception {
        ProjectData pd = new ProjectData();
        String locale = "vi_VN";
        String base = "C:\\xworkspace\\XuanYuan1.0-Data\\data_"+locale+"\\";
//        String base = "C:\\xworkspace\\XuanYuan1.0-Data\\";
        I18NProcessor.needETD = true; 
        pd.baseDir = new File(base + "data");
        pd.serverMode = false;
        
        pd.load(new File(base + "data"));
        
        Set<String> excludes = new HashSet<String>();
        excludes.add("CVS");
        excludes.add("Branches");
        excludes.add("client_pkg");
        //excludes.add(".pkg");
        //excludes.add(".cvsignore");
        File src = new File(base +"revision_resources\\scripts");
        File dest = new File(base + "data\\scripts");
        copyFiles(src, dest, excludes, true);
        
     // ��������������Ϣ�ļ�����Ŀ��Ŀ¼ִ�б��ػ�
        File msg = new File(base +"\\messages_sourcecode.xls");
        MessageFile mf = new MessageFile(msg, "zh_CN", locale);
        System.setProperty("pip_xml_encoding", "UTF-8");
        String[][] newStrs;
        
        // �Խű�����ִ�д���
        if (true) {
            newStrs = I18NUtils.doI18NScript(pd.baseDir, mf);
        } else {
            newStrs = I18NUtils.findI18NRelatedScriptStrings(pd.baseDir, mf);
        }
//        if (newStrs.length > 0) {
//            for (String[] s : newStrs) { 
//                mf.addString(s[0], s[1]);
//            }
//            mf.save();
//        }
     // TODO Auto-generated method stub
        try {
            File sf = new File(base + "\\data\\scripts\\Android\\ui_roleinfo_iOSNewUILarge.etf.gz");
            //File sf = new File("E:\\eclipse\\workspace\\Sanguo1.0-Data\\data_vi_VN\\data\\client_pkg\\NokiaS60V3\\ui_mainmenu_NokiaS60V3.etf.gz");
            FileInputStream fis = new FileInputStream(sf);
            GZIPInputStream gis = new GZIPInputStream(fis);
            ETFFile etf = ETFFile.load(gis);
            fis.close();
            boolean changed = false;
            for (int i = 0; i < etf.stringTable.length; i++) {
                if (etf.stringTable[i] != null) {
                    System.out.println(etf.stringTable[i]);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
