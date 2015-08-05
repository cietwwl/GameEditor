package com.pip.game.data;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;

import com.pip.util.Utils;

/**
 * ����֧��client_pkg.xml���ã������������ɸ��汾�ͻ��˰�װ����Ҫ���ļ��б�
 * @author lighthu
 */
public class ClientData {
    /**
     * һ���汾�Ŀͻ��˰�װ����Դ�б��塣
     * @author lighthu
     */
    public static class PackageDefine {
        /** Ŀ��Ŀ¼·���������dataĿ¼ */
        public String target;
        /** ��Ӧ�ͻ���UI���� */
        public String uimodel;
        /** ʵ��UI���� */
        public String scriptModel;
        /** ��������ļ����ͣ�����AndroidSmall���ͣ�������Android���͵��ļ��б��������ļ���*/
        public String downloadModel;
        /** 
         * �����Ͱ�������Դ�ļ��б��������ָ�ʽ��
         * �ű��ļ�������·��������gz��չ��������lib_builtin.etf
         * �ؿ��ļ�������·���������ֹؿ����ƣ�����3.pkg
         * �����ļ����������data��·��
         */
        public String[] files;
        /** �����ĸ��ļ��Ƿ��ǿͻ��˱����ļ��� */
        public boolean[] need;
        /** �����ĸ��ļ��Ƿ���Ҫ���� */
        public boolean[] dontUpdate;
        /** �������Դ�ļ���ȫ·�� */
        public File[] srcFile;
        /** �������Դ�ļ��Ŀ���Ŀ��·�� */
        public File[] targetFile;
        /** �������Դ�ļ���Ӧ�Ŀͻ����ļ��� */
        public String[] usedFileName;
        
        /** �������Դ�ļ��Ƿ��Ǳ����ļ��ı�־��key�ǿͻ����ļ�����value�Ǳ����־ */
        public Set<String> clientNeedFiles;
        public String[] clientNeedFilesArr;
        /** ���治��Ҫ�ͻ��˸��µ��ļ� */
        public Set<String> dontUpdateFiles;
        /** ����ͻ����ļ�������ʵ���ļ�����ӳ��� */
        public Map<String, String> fileNameMapping;
        /** �����ļ��Ƿ���Ҫ������ͻ��� */
        public boolean[] notToClient;
    }

    // ������Ŀ
    public ProjectData owner;
    // ��֧��null��ʾpip�汾��
    protected String branch;
    // scripts����Ŀ¼��Ĭ����scripts��Ҳ��ͨ��client_pkg.xmlָ��
    public String scriptsDir;
    // �Ƿ��etf�ļ���¼�������ƣ�����ͻ���ʹ��scryer���棬����Ϊtrue
    protected boolean useFullNameScriptFile = false;

    // client_pkg.xml�����õ����а����塣
    public PackageDefine[] packageDefs;
    // PackageDefine���ٲ��ұ�key��UI����
    protected Hashtable<String, PackageDefine> packageDefTable;

    // �ͻ�����client.data�ļ�������������Դ���б�Ͱ汾�ţ���ʽΪ��
    // 4�ֽ��ļ���
    // ���ļ���ѭ��
    //     �ļ�����UTF-8�ַ�����
    //     4�ֽ��ļ��汾��
    //     4�ֽ��ļ�����
    public static final String CLIENT_DATA_FILE = "client.data";
    
    /**
     * �ͻ�����Դ�汾��
     * ���ڿͻ�����Դͬ��ʱ��ѿͻ���������Դ���ֺͶ�Ӧ�ļ��汾�Ŵ���������������������ϴ���Ӱ���½�ٶȣ�
     * ������������Ժ󣬿ͻ�����Դͬ��֮ǰ�����ȱȽϸð汾�ţ������ͬ����Ҫ��Դ����
     * �����Ա����ڷ����������ļ��У���ÿ����client_pkgʱ�Զ���1
     * Ĭ��Ϊ0
     */
    public int clientResVersion;
    
    /**
     * ����client_pkg.xml������ClientData��
     * @param owner
     * @param branch
     * @throws Exception
     */
    public ClientData(ProjectData owner, String branch) throws Exception {
        this.owner = owner;
        this.branch = branch;
        loadDefine();
        loadClientResVersion();
    }
    
    public String getScriptsDir() {
        return scriptsDir;
    }
    
    public boolean getUseFullNameScriptFile(){
        return useFullNameScriptFile;
    }
    
    /*
     * ����client_pkg.xml�ļ���
     */
    protected void loadDefine() throws Exception {
        Document doc1;
        if (branch == null) {
            doc1 = Utils.loadDOM(new File(owner.baseDir, "client_pkg.xml"));
        } else {
            doc1 = Utils.loadDOM(new File(owner.baseDir, "client_pkg_" + branch + ".xml"));
        }
        scriptsDir = doc1.getRootElement().getAttributeValue("scripts_dir");
        if (scriptsDir == null) {
            scriptsDir = "scripts";
        }
        useFullNameScriptFile = "true".equals(doc1.getRootElement().getAttributeValue("use_full_name_script_file"));
        
        // �������е�fileset����
        HashMap<String, Element> fileSetElements = new HashMap<String, Element>();
        for (Object el : doc1.getRootElement().getChildren("fileset")) {
            Element elem = (Element)el;
            fileSetElements.put(elem.getAttributeValue("id"), elem);
        }
        
        // ����ͳ���ļ�����ȡ�����ļ��б�
        HashMap<String, String[]> wildCharsCache = new HashMap<String, String[]>();

        // �������е�package����
        List list = doc1.getRootElement().getChildren("package");
        packageDefs = new PackageDefine[list.size()];
        packageDefTable = new Hashtable<String, PackageDefine>();
        for (int i = 0; i < list.size(); i++) {
            packageDefs[i] = new PackageDefine();
            Element elem = (Element) list.get(i);
            packageDefs[i].target = elem.getAttributeValue("target");
            packageDefs[i].uimodel = elem.getAttributeValue("uimodel");
            packageDefs[i].scriptModel = elem.getAttributeValue("scriptmodel");
            if (packageDefs[i].scriptModel == null) {
                packageDefs[i].scriptModel = packageDefs[i].uimodel;
            }
            packageDefs[i].downloadModel = elem.getAttributeValue("downloadmodel");
            
            // �������е�file��Ԫ�غ�fileset��Ԫ�أ�����fileset��Ԫ�ؽ���ΪfileԪ��
            List list2 = elem.getChildren();
            List<Element> fileElemList = new ArrayList<Element>();
            for (int j = 0; j < list2.size(); j++) {
                if (!(list2.get(j) instanceof Element)) {
                    continue;
                }
                Element elem2 = (Element)list2.get(j);
                if (elem2.getName().equals("file")) {
                    fileElemList.add(elem2);
                } else if (elem2.getName().equals("fileset")) {
                    // fileset����Ϊһ��file
                    String fileSetID = elem2.getAttributeValue("id");
                    Element fileSetElem = fileSetElements.get(fileSetID);
                    if (fileSetElem != null) {
                        List filesList = fileSetElem.getChildren("file");
                        for (int k = 0; k < filesList.size(); k++) {
                            fileElemList.add((Element)filesList.get(k));
                        }
                    }
                }
            }
            
            // ·�����һ��֧��ͨ���*��?
            HashMap<String, String> nameMap = new HashMap<String, String>();
            List<String> pathList = new ArrayList<String>();
            List<Boolean> needList = new ArrayList<Boolean>();
            List<Boolean> dontUpdateList = new ArrayList<Boolean>();
            List<String> usedNameList = new ArrayList<String>();
            for (int j = 0; j < fileElemList.size(); j++) {
                Element elem2 = (Element) fileElemList.get(j);
                
                String path = elem2.getAttributeValue("path");
                path = path.replace('\\', '/');
                // �����ų������ļ�
                String excludePath = elem2.getAttributeValue("exclude");
                if (excludePath != null) {
                    excludePath = excludePath.replace('\\', '/');                    
                }
                boolean need = "true".equals(elem2.getAttributeValue("need"));
                boolean dontUpdate = "true".equals(elem2.getAttributeValue("dont_update"));
                String targetPath = elem2.getAttributeValue("target_path");
                if (targetPath == null) {
                    targetPath = "";
                }
                
                // ������,�ָ��Ķ��ƥ��ģʽ
                String[] patterns = path.split(",");
                for (String pattern : patterns) {
                    String[] pathes = wildCharsCache.get(pattern);
                    if (pathes == null) {
                        pathes = translateWildChars(pattern); 
                        wildCharsCache.put(pattern, pathes);
                    }
                    for (int k = 0; k < pathes.length; k++) {
                        // �������ļ��Ƿ���exclude������
                        boolean excluded = false;
                        if (excludePath != null) {
                            String[] excludePathes = wildCharsCache.get(excludePath);
                            if (excludePathes == null) {
                                excludePathes = translateWildChars(excludePath);
                                wildCharsCache.put(excludePath, excludePathes);
                            }
                            for (String exldPath : excludePathes) {
                                if (exldPath.equals(pathes[k])) {
                                    excluded = true;
                                    break;
                                }
                            }
                        }
                        if (excluded) {
                            continue;
                        }

                        // �������ļ��Ƿ��Ѿ����б����ˣ�����ǣ���֮ǰ���ļ���¼ɾ��
                        String name = getClientName(pattern, pathes[k], targetPath, packageDefs[i].scriptModel);
                        if (nameMap.containsKey(name)) {
                            String oldPath = nameMap.remove(name);
                            int oldIndex = pathList.indexOf(oldPath);
                            pathList.remove(oldIndex);
                            needList.remove(oldIndex);
                            dontUpdateList.remove(oldIndex);
                            usedNameList.remove(oldIndex);
                        }
                        nameMap.put(name, pathes[k]);
                        
                        // ���ļ������б�
                        pathList.add(pathes[k]);
                        needList.add(need);
                        dontUpdateList.add(dontUpdate);
                        usedNameList.add(name);
                    } // for (int k = 0; k < pathes.length; k++)
                } // for (String pattern : patterns)
            }
            
            packageDefs[i].files = new String[pathList.size()];
            pathList.toArray(packageDefs[i].files);
            packageDefs[i].need = new boolean[needList.size()];
            packageDefs[i].dontUpdate = new boolean[needList.size()];
            for (int j = 0; j < needList.size(); j++) {
                packageDefs[i].need[j] = needList.get(j);
                packageDefs[i].dontUpdate[j] = dontUpdateList.get(j);
            }
            packageDefs[i].usedFileName = new String[usedNameList.size()];
            usedNameList.toArray(packageDefs[i].usedFileName);
            packageDefs[i].clientNeedFiles = new HashSet<String>();
            packageDefs[i].dontUpdateFiles = new HashSet<String>();
            packageDefs[i].srcFile = new File[packageDefs[i].files.length];
            packageDefs[i].targetFile = new File[packageDefs[i].files.length];
            packageDefs[i].fileNameMapping = new HashMap<String, String>();
            
            // ���������ļ��Ͳ���Ҫ�����ļ��Ŀ��ٲ��ұ�
            for (int j = 0; j < packageDefs[i].files.length; j++) {
                if (packageDefs[i].need[j]) {
                    packageDefs[i].clientNeedFiles.add(packageDefs[i].usedFileName[j]);
                }
                if (packageDefs[i].dontUpdate[j]) {
                    packageDefs[i].dontUpdateFiles.add(packageDefs[i].usedFileName[j]);
                }
                packageDefs[i].fileNameMapping.put(packageDefs[i].usedFileName[j], packageDefs[i].files[j]);
            }
            packageDefs[i].clientNeedFilesArr = new String[packageDefs[i].clientNeedFiles.size()];
            packageDefs[i].clientNeedFiles.toArray(packageDefs[i].clientNeedFilesArr);
            
            packageDefTable.put(packageDefs[i].uimodel, packageDefs[i]);
        }
    }
    
    /**
     * ����һ���ļ���Ӧ�Ŀͻ����ļ�������·������
     * @param originalPath ���õ�·�������ܴ���չ����
     * @param filePath ʵ���ļ�·��
     * @param targetPath ���õ�Ŀ��·��������Ϊ�մ�����ʾ��Ŀ¼��
     * @param uimodel �ͻ���uimodel
     * @return
     */
    private String getClientName(String originalPath, String filePath, String targetPath, String uimodel) {
        // ��ȡԭʼ·���в�����ͨ�����Ŀ¼����
        String[] secs = originalPath.split("/");
        String fixPath = "";
        for (int i = 0; i < secs.length - 1; i++) {
            if (secs[i].contains("*") || secs[i].contains("?")) {
                break;
            }
            fixPath += secs[i];
            fixPath += "/";
        }
        
        // �ļ�·����ȥ��������ͨ����Ĳ��֣���Ϊƥ��·��
        String subPath = filePath.substring(fixPath.length());
        if (useFullNameScriptFile && subPath.endsWith(".etf")) {
            subPath = subPath.substring(0, subPath.length() - 4) + "_" + uimodel + ".etf.gz";
        }
        
        // ����Ŀ��·���������ڿͻ��˵�ʵ��·��
        if (targetPath.length() > 0) {
            return targetPath + "/" + subPath;
        } else {
            return subPath;
        }
    }
    
    /**
     * ȡ��ĳ������ȫ�������ļ��ı�
     */
    public String[] getClientNeedFiles(String model) {
        PackageDefine pkgDef = packageDefTable.get(model);
        if (pkgDef != null) {
            return pkgDef.clientNeedFilesArr;
        }
        return null;
    }
    
    /**
     * �����ļ����������ʺϵ��ļ�·����
     * @param model ���� 
     * @param fileName �ļ���
     * @return ���������·��������Ҳ���������null��
     */
    public String getMatchPath(String model, String fileName) {
        PackageDefine pkgDef = packageDefTable.get(model);
        if (pkgDef == null) {
            return null;
        }
        if (pkgDef.fileNameMapping.containsKey(fileName)) {
            return pkgDef.fileNameMapping.get(fileName);
        }
        
        // ���������������ػ��ͣ��򱾻���Ŀ¼��û�е��ļ������Ե�������ػ���Ŀ¼��������
        if (pkgDef.downloadModel != null) {
            return getMatchPath(pkgDef.downloadModel, fileName);
        }
        
        return null;
    }
    
    /**
     * �ж�һ���ļ��Ƿ�ͻ��˱����ļ���
     * @param name �ļ��ͻ�������
     * @param model �ͻ���UIModel
     * @return
     */
    public boolean isClientNeedFile(String name, String model) {
        PackageDefine pkgDef = packageDefTable.get(model);
        if (pkgDef == null) {
            return false;
        }
        if (pkgDef.clientNeedFiles.contains(name)) {
            return true;
        }
        
        // ���������������ػ��ͣ����Ƿ���������ػ��͵ı����ļ�����Ϊ����ӿ�ֻ���ڿͻ��˴��ڵ��ļ��Ż���ã����Բ��ᵼ�¶���
        // �����أ�����getClientNeedFiles�ӿڻ��ǰ��������߼����أ�������������ػ��ͣ�
        if (pkgDef.downloadModel != null) {
            return isClientNeedFile(name, pkgDef.downloadModel);
        }
        
        return false;
    }
    
    /**
     * �ж�һ���ļ��Ƿ���Ҫ�ͻ��˸��¡�
     * @param name �ļ��ͻ�������
     * @param model �ͻ���UIModel
     * @return
     */
    public boolean needNotUpdate(String name, String model) {
        PackageDefine pkgDef = packageDefTable.get(model);
        if (pkgDef != null) {
            return pkgDef.dontUpdateFiles.contains(name);
        }
        return false;
    }
    
    /**
     * �����л��ͣ��ѿͻ�����Դ������client_pkgĿ¼�£�������client.data�ļ���
     * @throws Exception
     */
    public void makeClientData() throws Exception {
        for (int i = 0; i < packageDefs.length; i++) {
            makeClientData(packageDefs[i]);
            System.out.println(packageDefs[i].uimodel + " is finished " + i + "/" + packageDefs.length);
        }
        
        //���л�������󣬿�ʼ����clientResVersion
        makeClientResVersion();
        
        // �����download_pkg.xml���ڣ���������xml�����ݣ��������ذ�
        File[] files = owner.baseDir.listFiles();
        for (File dpkConfig : files) {
            if (dpkConfig.isFile() && dpkConfig.getName().startsWith("download_pkg") && dpkConfig.getName().endsWith(".xml")) {
                // �ҵ�һ��download_pkg_xxxx.xml������������ô���һ��dpk�ļ�
                String subName = dpkConfig.getName();
                subName = subName.substring("download_pkg".length(), subName.length() - ".xml".length());
                if (subName.startsWith("_")) {
                    subName = subName.substring(1);
                }
                
                // Ŀ��Ŀ¼��client_pkgĿ¼
                File targetDir = new File(owner.baseDir, packageDefs[0].target).getParentFile();
                if (subName.length() > 0) {
                    targetDir = new File(targetDir, subName);
                    targetDir.mkdirs();
                }
                
                // ����dpk�ļ���
                createDownloadPackage(dpkConfig, targetDir);
            }
        }
    }
    
    /**
     * ����һ��download_pkg�����ļ�����һ��dpk�ļ���
     * @param configFile download_pkg�����ļ�
     * @param targetDir dpk����Ŀ¼
     */
    protected void createDownloadPackage(File configFile, File targetDir) throws Exception {
        Document doc = Utils.loadDOM(configFile);
        int fileSize = Integer.parseInt(doc.getRootElement().getAttributeValue("filesize"));
        List<String> filePathes = new ArrayList<String>();
        List<String> fileNames = new ArrayList<String>();
        List fileElemList = doc.getRootElement().getChildren("file");
        for (int i = 0; i < fileElemList.size(); i++) {
            Element elem = (Element)fileElemList.get(i);
            String path = elem.getAttributeValue("path");
            String name = elem.getAttributeValue("name");
            String exclude = elem.getAttributeValue("exclude");
            String targetPath = elem.getAttributeValue("target_path");
            if (targetPath == null) {
                targetPath = "";
            }
            if (path.contains("*") || path.contains("?")) {
                // ����ͨ���
                String[] pathes = translateWildChars(path);
                Set<String> excludes = new HashSet<String>();
                if (exclude != null) {
                    String[] es = translateWildChars(exclude);
                    for (String e : es) {
                        excludes.add(e);
                    }
                }
                for (String p : pathes) {
                    if (excludes.contains(p)) {
                        continue;
                    }
                    
                    // ��Ӵ��ļ�
                    name = getClientName(path, p, targetPath, "Android");
                    if (fileNames.contains(name)) {
                        int index = fileNames.indexOf(name);
                        filePathes.remove(index);
                        fileNames.remove(index);
                    }
                    filePathes.add(p);
                    fileNames.add(name);
                }
            } else {
                // ��Ӵ��ļ�
                if (name == null) {
                    name = getClientName(path, path, targetPath, "Android");
                }
                if (fileNames.contains(name)) {
                    int index = fileNames.indexOf(name);
                    filePathes.remove(index);
                    fileNames.remove(index);
                }
                filePathes.add(path);
                fileNames.add(name);
            }
        }
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(targetDir, "debug.list")));
            for (String path : filePathes) {
                pw.println(path);
            }
            pw.close();
        } catch (Exception e) {
        }
        createDownloadPackage(targetDir, filePathes, fileNames, fileSize);
    }
    
    // ���ɹ��ͻ������������ļ���dpk�ļ�
    private void createDownloadPackage(File targetDir, List<String> pathes, List<String> names, int fileSize) throws Exception {
        // ���ļ���С���飬�ֱ�����
        int currentID = 1;
        int currentFileStart = 0;
        int currentFileSize = 0;
        for (int i = 0; i <= pathes.size(); i++) {
            if (i == pathes.size()) {
                createDownloadPackage(new File(targetDir, currentID + ".dpk"), pathes, names, currentFileStart, i);
                break;
            } else {
                File dataFile = new File(owner.baseDir, pathes.get(i));
                if (currentFileSize > 0 && currentFileSize + dataFile.length() > fileSize) {
                    createDownloadPackage(new File(targetDir, currentID + ".dpk"), pathes, names, currentFileStart, i);
                    currentID++;
                    currentFileStart = i;
                    currentFileSize = 0;
                    i--;
                } else {
                    currentFileSize += dataFile.length();
                }
            }
        }
        
        PrintWriter out = new PrintWriter(new FileWriter(new File(targetDir, "dpklist.txt")));
        for (int id  = 1; id <= currentID; id++) {
            String name = id + ".dpk";
            long size = new File(targetDir, name).length();
            out.println(name + " " + size);
        }
        out.flush();
        out.close();
    }
    
    private void createDownloadPackage(File target, List<String> pathes, List<String> names, int start, int end) throws Exception {
        // ����dpk�ļ�
        FileOutputStream fos = new FileOutputStream(target);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeUTF("DPK");
        dos.writeShort(end - start);
        for (int i = start; i < end; i++) {
            File dataFile = new File(owner.baseDir, pathes.get(i));
            dos.writeUTF(names.get(i));
            dos.writeInt(owner.getFileCRCVersion(dataFile));
            byte[] fdata = Utils.loadFileData(dataFile);
            dos.writeInt(fdata.length);
            dos.write(fdata);
        }
        dos.close();
    }
    
    //��������Ҫ����clientResVersion
    public void loadClientResVersion() throws Exception {
        File clientResDir = new File(owner.baseDir, scriptsDir).getParentFile();
        File clientResVersionFile = new File(clientResDir, "clientResVersion");
        
        if(clientResVersionFile.exists()) {
            FileInputStream fis = new FileInputStream(clientResVersionFile);
            DataInputStream dis = new DataInputStream(fis);
            clientResVersion = dis.readInt();
            dis.close();
            fis.close();
        }
    }
    
    //��������ļ��б�
    public String[] getAllFiles() throws Exception {
        List<String> list = new ArrayList<String>();
        
        for(int i = 0; i < packageDefs.length; i++){
            for(int j = 0; j < packageDefs[i].files.length; j++){
                list.add(packageDefs[i].files[j]);
            }
        }
        
        String[] tmp = new String[list.size()];
        list.toArray(tmp);
        return tmp;
    }
    
    public void makeClientResVersion() throws Exception {
        File clientResDir = new File(owner.baseDir, scriptsDir).getParentFile();
        File clientResVersionFile = new File(clientResDir, "clientResVersion");
        clientResVersion = (int)(System.currentTimeMillis() / 1000L);
        
        FileOutputStream fos = new FileOutputStream(clientResVersionFile);
        DataOutputStream dos = new DataOutputStream(fos);
        dos.writeInt(clientResVersion);
        dos.flush();
        dos.close();
        fos.close();
    }
    
    /*
     * �Ե������ͣ��ѿͻ�����Դ������client_pkgĿ¼�£�������client.data�ļ���
     */
    public void makeClientData(PackageDefine pdef) throws Exception {
        // ��һ�������Ŀ��Ŀ¼
        File targetDir = new File(owner.baseDir, pdef.target);
        targetDir.mkdirs();
        deleteFilesInDir(targetDir);
        
        // �ڶ����������������õ��ļ���Ŀ��Ŀ¼��ע��etf�ļ���������Ҫ���ݻ����޸ģ�pkg�ļ���������Ҫ����
        for (int i = 0; i < pdef.files.length; i++) {
            String fname = pdef.files[i];
            File srcFile;
            File tgtFile;
            if (fname.endsWith(".etf")) {
                String sname = fname.substring(0, fname.length() - 4);
                srcFile = new File(owner.baseDir, scriptsDir + "/" + pdef.scriptModel + "/" +
                        sname + "_" + pdef.scriptModel + ".etf.gz");
                tgtFile = new File(targetDir, srcFile.getName());
            } else if (fname.endsWith(".pkg") && Character.isDigit(fname.charAt(0))) {
                int gid ;
                
                int idxOf_ = fname.indexOf('_', 0);
                if(idxOf_ != -1){
                    //����ͼ
                    gid = Integer.parseInt(fname.substring(0, idxOf_));
                }else{
                    //�����汾��ͼ
                    gid = Integer.parseInt(fname.substring(0, fname.length() - 4));
                }
                GameArea area = (GameArea)owner.findObject(GameArea.class, gid);
                MapFormat format = owner.config.getClientMapFormat(pdef.uimodel);
                srcFile = new File(owner.baseDir, "Areas/" + area.source.getName() + "/" + area.getID() + format.pkgName + ".pkg");
                tgtFile = new File(targetDir, pdef.usedFileName[i]);
            } else if(fname.endsWith(".jpg") || fname.endsWith(".png")){//������jpg��׺������img����ֹandroid�Զ������������
                srcFile = new File(owner.baseDir, fname);
                tgtFile = new File(targetDir, pdef.usedFileName[i].replaceAll(".jpg", ".img").replace(".png", ".img"));
            }else if(fname.startsWith("Animations/")){ 
                srcFile = new File(owner.baseDir, fname);
                tgtFile = new File(targetDir, pdef.usedFileName[i]);
            } else {
                srcFile = new File(owner.baseDir, fname);
                tgtFile = new File(targetDir, pdef.usedFileName[i]);
            }
            tgtFile.getParentFile().mkdirs();
            Utils.copyFile(srcFile, tgtFile);
            pdef.srcFile[i] = srcFile;
            pdef.targetFile[i] = tgtFile;
        }
        // ������������client.data�ļ����ŵ�scripts��client_pkgĿ¼�µĻ���Ŀ¼�
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        makeClientResourceDataFile(dos, pdef);
        dos.close();
        Utils.saveFileData(new File(targetDir, CLIENT_DATA_FILE), bos.toByteArray());
        Utils.saveFileData(new File(owner.baseDir, scriptsDir + "/" + pdef.scriptModel + "/" + CLIENT_DATA_FILE), bos.toByteArray());
        
        // ���Ĳ�������clientResVersion�ļ�����scripts���ϲ�Ŀ¼�£�������Ŀ¼�С�
        File clientResDir = new File(owner.baseDir, scriptsDir).getParentFile();
        File clientResVersionFile = new File(clientResDir, "clientResVersion");
        if (clientResVersionFile.exists()) {
            Utils.copyFile(clientResVersionFile, new File(targetDir, "clientResVersion"));
        }
    }

    /* 
     * ����ĳ�����͵�client.data�ļ���
     */
    public void makeClientResourceDataFile(DataOutputStream dos, PackageDefine pdef) throws Exception {
        dos.writeInt(pdef.files.length);
        for (int i = 0; i < pdef.files.length; i++) {
            dos.writeUTF(pdef.usedFileName[i]);
            dos.writeInt(owner.getFileCRCVersion(pdef.srcFile[i]));
            dos.writeInt((int)pdef.targetFile[i].length());
        }
    }
    
    /*
     * �ҳ����з���һ��������ͨ�����·�����ļ�·�������pattern֮���ö��ŷָ���
     */
    public String[] translateWildChars(String path) {
        String[] pathes = path.split(",");
        Set<String> retSet = new HashSet<String>();
        for (String p : pathes) {
            // �����¼����������ͨ������򲻽��к���ƥ����
            if (!p.contains("*") && !p.contains("?")) {
                retSet.add(p);
                continue;
            }
            
            // �ҵ���һ��û��ͨ�����Ŀ¼����Ϊ������Ŀ¼
            String[] secs = p.split("/");
            String searchRoot = "";
            String subDirPattern = "";
            String fileNamePattern = secs[secs.length - 1];
            for (int i = 0; i < secs.length - 1; i++) {
                if (secs[i].contains("*") || secs[i].contains("?")) {
                    for (int j = i; j < secs.length - 1; j++) {
                        if (subDirPattern.length() > 0) {
                            subDirPattern += "/";
                        }
                        subDirPattern += secs[j];
                    }
                    break;
                }
                searchRoot += secs[i];
                searchRoot += "/";
            }
            File searchRootDir = new File(owner.baseDir, searchRoot);
            
            // �����������ļ�
            List<String> fileList = new ArrayList<String>();
            findFilesInDir(searchRootDir, "", fileList);
            
            // ��ͨ���ת��Ϊ������ʽ�ַ�����ƥ�����е��ļ���
            String dp = wildCharsToRegExp(subDirPattern);
            String np = wildCharsToRegExp(fileNamePattern);
            for (String rp : fileList) {
                String dir, name;
                int sp = rp.lastIndexOf('/');
                if (sp == -1) {
                    dir = "";
                    name = rp;
                } else {
                    dir = rp.substring(0, sp);
                    name = rp.substring(sp + 1);
                }
                if (dir.matches(dp) && name.matches(np)) {
                    retSet.add(searchRoot + rp);
                }
            }
        }
        String[] ret = new String[retSet.size()];
        retSet.toArray(ret);
        return ret;
    }
    
    // ��*��?��ƥ��ģʽ��ת����������ʽ
    protected String wildCharsToRegExp(String wildChars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wildChars.length(); i++) {
            char ch = wildChars.charAt(i);
            if (ch == '*') {
                sb.append(".*");
            } else if (ch == '.') {
                sb.append("\\.");
            } else if (ch == '?') {
                sb.append(".");
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    
    /**
     * �ҳ�һ��Ŀ¼�е������ļ���������Ŀ¼����
     * @param dir ����Ŀ¼
     * @param relatePath ����Ŀ¼�����·��
     * @param saveSet �����ҳ����ļ�·��������ڸ�Ŀ¼��
     */
    public static void findFilesInDir(File dir, String relatePath, List<String> saveSet) {
        File[] children = dir.listFiles();
        if (children == null) {
            return;
        }
        for (File child : children) {
            if (child.isFile()) {
                if (!child.getName().startsWith(".")) {
                    saveSet.add(relatePath.isEmpty() ? child.getName() : relatePath + "/" + child.getName());
                }
            } else if (child.isDirectory() && !child.getName().equals("CVS") && !child.getName().startsWith(".")) {
                findFilesInDir(child, relatePath.isEmpty() ? child.getName() : relatePath + "/" + child.getName(), saveSet);
            }
        }
    }
    
    /**
     * ɾ��һ��Ŀ¼�е������ļ���������Ŀ¼����
     * @param dir ����Ŀ¼
     */
    public static void deleteFilesInDir(File dir) {
        File[] children = dir.listFiles();
        if (children == null) {
            return;
        }
        for (File child : children) {
            if (child.isFile()) {
                if (!child.getName().startsWith(".")) {
                    child.delete();
                }
            } else if (child.isDirectory() && !child.getName().equals("CVS") && !child.getName().startsWith(".")) {
                deleteFilesInDir(child);
            }
        }
    }
}
