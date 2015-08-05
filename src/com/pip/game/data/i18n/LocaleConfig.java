package com.pip.game.data.i18n;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.pip.game.data.ProjectData;
import com.pip.util.Utils;

/**
 * һ���������԰汾�Ķ��塣
 * @author lighthu
 */
public class LocaleConfig {
    /**
     * ������Ŀ��
     */
    public ProjectData owner;
    /**
     * ����ID�����磺zh_CN
     */
    public String id;
    /**
     * ���ֱ��롣
     */
    public String encoding;
    /**
     * ��ʾ���⡣
     */
    public String title;
    /**
     * ���ݷ����ַ����ļ�·����
     */
    public File dataMessageFile;
    /**
     * Դ���뷭���ַ����ļ�·����
     */
    public File sourceCodeMessageFile;
    /**
     * �����������ļ�·���������Ŀ¼�ṹӦ�ú�data�µ�Ŀ¼�ṹ��Ӧ������special_resource��ͬ�����Ŀ¼��
     * ���ļ����ڱ��ػ�֮ǰ������Ŀ��Ŀ¼��
     */
    public File revisionResourceDir;
    /**
     * �����������ļ�·���������Ŀ¼�ṹӦ�ú�data�µ�Ŀ¼�ṹ��Ӧ����
     */
    public File specialResourceDir;
    /**
     * ���Ŀ¼��
     */
    public File outputDir;
    
    public LocaleConfig(ProjectData owner) {
        this.owner = owner;
    }
    
    public void load(Element elem) {
        id = elem.getAttributeValue("id");
        encoding = elem.getAttributeValue("encoding");
        title = elem.getAttributeValue("title");
        String path = elem.getAttributeValue("data_message_file");
        dataMessageFile = new File(owner.baseDir, path);
        path = elem.getAttributeValue("source_code_message_file");
        sourceCodeMessageFile = new File(owner.baseDir, path);
        path = elem.getAttributeValue("revision_resource_dir");
        revisionResourceDir = new File(owner.baseDir, path);
        path = elem.getAttributeValue("special_resource_dir");
        specialResourceDir = new File(owner.baseDir, path);
        path = elem.getAttributeValue("output_dir");
        outputDir = new File(owner.baseDir, path);
    }
    
    public static List<LocaleConfig> getLocales(ProjectData proj) throws Exception {
        List<LocaleConfig> ret = new ArrayList<LocaleConfig>();
        Document doc = Utils.loadDOM(new File(proj.baseDir, "i18n.xml"));
        List list = doc.getRootElement().getChildren("locale");
        for (int i = 0; i < list.size(); i++) {
            Element elem = (Element)list.get(i);
            LocaleConfig locale = new LocaleConfig(proj);
            locale.load(elem);
            ret.add(locale);
        }
        return ret;
    }
    
    public String toString() {
        return title;
    }
}
