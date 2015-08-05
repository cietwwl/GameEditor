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
 * 本类支持client_pkg.xml配置，根据配置生成各版本客户端安装包需要的文件列表。
 * @author lighthu
 */
public class ClientData {
    /**
     * 一个版本的客户端安装包资源列表定义。
     * @author lighthu
     */
    public static class PackageDefine {
        /** 目标目录路径，相对于data目录 */
        public String target;
        /** 对应客户端UI机型 */
        public String uimodel;
        /** 实际UI机型 */
        public String scriptModel;
        /** 替代下载文件机型（例如AndroidSmall机型，可以用Android机型的文件列表来下载文件）*/
        public String downloadModel;
        /** 
         * 本机型包含的资源文件列表，包括几种格式：
         * 脚本文件：不用路径，不带gz扩展名，例如lib_builtin.etf
         * 关卡文件：不用路径，用数字关卡名称，例如3.pkg
         * 其他文件：用相对于data的路径
         */
        public String[] files;
        /** 包含的各文件是否是客户端必须文件。 */
        public boolean[] need;
        /** 包含的各文件是否不需要更新 */
        public boolean[] dontUpdate;
        /** 缓存各资源文件的全路径 */
        public File[] srcFile;
        /** 缓存各资源文件的拷贝目标路径 */
        public File[] targetFile;
        /** 缓存各资源文件对应的客户端文件名 */
        public String[] usedFileName;
        
        /** 缓存各资源文件是否是必须文件的标志，key是客户端文件名，value是必须标志 */
        public Set<String> clientNeedFiles;
        public String[] clientNeedFilesArr;
        /** 缓存不需要客户端更新的文件 */
        public Set<String> dontUpdateFiles;
        /** 缓存客户端文件名，到实际文件名的映射表 */
        public Map<String, String> fileNameMapping;
        /** 配置文件是否需要打包到客户端 */
        public boolean[] notToClient;
    }

    // 所属项目
    public ProjectData owner;
    // 分支，null表示pip版本。
    protected String branch;
    // scripts所在目录，默认是scripts，也可通过client_pkg.xml指定
    public String scriptsDir;
    // 是否对etf文件记录完整名称，如果客户端使用scryer引擎，设置为true
    protected boolean useFullNameScriptFile = false;

    // client_pkg.xml里配置的所有包定义。
    public PackageDefine[] packageDefs;
    // PackageDefine快速查找表，key是UI机型
    protected Hashtable<String, PackageDefine> packageDefTable;

    // 客户端用client.data文件来保存内置资源的列表和版本号，格式为：
    // 4字节文件数
    // 按文件数循环
    //     文件名（UTF-8字符串）
    //     4字节文件版本号
    //     4字节文件长度
    public static final String CLIENT_DATA_FILE = "client.data";
    
    /**
     * 客户端资源版本号
     * 由于客户端资源同步时会把客户端所有资源名字和对应文件版本号打包给服务器，这样流量较大且影响登陆速度，
     * 因此添加这个属性后，客户端资源同步之前可以先比较该版本号，如果相同则不需要资源更新
     * 该属性保存在服务器数据文件中，在每次做client_pkg时自动加1
     * 默认为0
     */
    public int clientResVersion;
    
    /**
     * 载入client_pkg.xml，创建ClientData表。
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
     * 载入client_pkg.xml文件。
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
        
        // 查找所有的fileset定义
        HashMap<String, Element> fileSetElements = new HashMap<String, Element>();
        for (Object el : doc1.getRootElement().getChildren("fileset")) {
            Element elem = (Element)el;
            fileSetElements.put(elem.getAttributeValue("id"), elem);
        }
        
        // 缓存统配文件符获取到的文件列表
        HashMap<String, String[]> wildCharsCache = new HashMap<String, String[]>();

        // 处理所有的package定义
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
            
            // 搜索所有的file子元素和fileset子元素，并把fileset子元素解释为file元素
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
                    // fileset解释为一组file
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
            
            // 路径最后一级支持通配符*和?
            HashMap<String, String> nameMap = new HashMap<String, String>();
            List<String> pathList = new ArrayList<String>();
            List<Boolean> needList = new ArrayList<Boolean>();
            List<Boolean> dontUpdateList = new ArrayList<Boolean>();
            List<String> usedNameList = new ArrayList<String>();
            for (int j = 0; j < fileElemList.size(); j++) {
                Element elem2 = (Element) fileElemList.get(j);
                
                String path = elem2.getAttributeValue("path");
                path = path.replace('\\', '/');
                // 可以排除部分文件
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
                
                // 可能是,分隔的多个匹配模式
                String[] patterns = path.split(",");
                for (String pattern : patterns) {
                    String[] pathes = wildCharsCache.get(pattern);
                    if (pathes == null) {
                        pathes = translateWildChars(pattern); 
                        wildCharsCache.put(pattern, pathes);
                    }
                    for (int k = 0; k < pathes.length; k++) {
                        // 检查这个文件是否在exclude条件中
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

                        // 检查这个文件是否已经在列表中了，如果是，把之前的文件记录删掉
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
                        
                        // 此文件加入列表
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
            
            // 建立必须文件和不需要更新文件的快速查找表
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
     * 计算一个文件对应的客户端文件名（带路径）。
     * @param originalPath 配置的路径（可能带扩展名）
     * @param filePath 实际文件路径
     * @param targetPath 配置的目标路径（可能为空串，表示根目录）
     * @param uimodel 客户端uimodel
     * @return
     */
    private String getClientName(String originalPath, String filePath, String targetPath, String uimodel) {
        // 提取原始路径中不包含通配符的目录部分
        String[] secs = originalPath.split("/");
        String fixPath = "";
        for (int i = 0; i < secs.length - 1; i++) {
            if (secs[i].contains("*") || secs[i].contains("?")) {
                break;
            }
            fixPath += secs[i];
            fixPath += "/";
        }
        
        // 文件路径，去掉不包含通配符的部分，作为匹配路径
        String subPath = filePath.substring(fixPath.length());
        if (useFullNameScriptFile && subPath.endsWith(".etf")) {
            subPath = subPath.substring(0, subPath.length() - 4) + "_" + uimodel + ".etf.gz";
        }
        
        // 加上目标路径，就是在客户端的实际路径
        if (targetPath.length() > 0) {
            return targetPath + "/" + subPath;
        } else {
            return subPath;
        }
    }
    
    /**
     * 取得某个机型全部必须文件的表。
     */
    public String[] getClientNeedFiles(String model) {
        PackageDefine pkgDef = packageDefTable.get(model);
        if (pkgDef != null) {
            return pkgDef.clientNeedFilesArr;
        }
        return null;
    }
    
    /**
     * 根据文件名查找最适合的文件路径。
     * @param model 机型 
     * @param fileName 文件名
     * @return 服务器相对路径，如果找不到，返回null。
     */
    public String getMatchPath(String model, String fileName) {
        PackageDefine pkgDef = packageDefTable.get(model);
        if (pkgDef == null) {
            return null;
        }
        if (pkgDef.fileNameMapping.containsKey(fileName)) {
            return pkgDef.fileNameMapping.get(fileName);
        }
        
        // 如果配置了替代下载机型，则本机型目录下没有的文件，尝试到替代下载机型目录进行下载
        if (pkgDef.downloadModel != null) {
            return getMatchPath(pkgDef.downloadModel, fileName);
        }
        
        return null;
    }
    
    /**
     * 判断一个文件是否客户端必须文件。
     * @param name 文件客户端名称
     * @param model 客户端UIModel
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
        
        // 如果配置了替代下载机型，则看是否是替代下载机型的必须文件（因为这个接口只有在客户端存在的文件才会调用，所以不会导致多余
        // 的下载，而在getClientNeedFiles接口还是按照正常逻辑返回，不处理替代下载机型）
        if (pkgDef.downloadModel != null) {
            return isClientNeedFile(name, pkgDef.downloadModel);
        }
        
        return false;
    }
    
    /**
     * 判断一个文件是否不需要客户端更新。
     * @param name 文件客户端名称
     * @param model 客户端UIModel
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
     * 对所有机型，把客户端资源拷贝到client_pkg目录下，并生成client.data文件。
     * @throws Exception
     */
    public void makeClientData() throws Exception {
        for (int i = 0; i < packageDefs.length; i++) {
            makeClientData(packageDefs[i]);
            System.out.println(packageDefs[i].uimodel + " is finished " + i + "/" + packageDefs.length);
        }
        
        //所有机型做完后，开始制作clientResVersion
        makeClientResVersion();
        
        // 如果有download_pkg.xml存在，则根据这个xml的内容，生成下载包
        File[] files = owner.baseDir.listFiles();
        for (File dpkConfig : files) {
            if (dpkConfig.isFile() && dpkConfig.getName().startsWith("download_pkg") && dpkConfig.getName().endsWith(".xml")) {
                // 找到一个download_pkg_xxxx.xml，根据这个配置创建一组dpk文件
                String subName = dpkConfig.getName();
                subName = subName.substring("download_pkg".length(), subName.length() - ".xml".length());
                if (subName.startsWith("_")) {
                    subName = subName.substring(1);
                }
                
                // 目标目录是client_pkg目录
                File targetDir = new File(owner.baseDir, packageDefs[0].target).getParentFile();
                if (subName.length() > 0) {
                    targetDir = new File(targetDir, subName);
                    targetDir.mkdirs();
                }
                
                // 创建dpk文件组
                createDownloadPackage(dpkConfig, targetDir);
            }
        }
    }
    
    /**
     * 根据一个download_pkg配置文件创建一组dpk文件。
     * @param configFile download_pkg配置文件
     * @param targetDir dpk保存目录
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
                // 包含通配符
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
                    
                    // 添加此文件
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
                // 添加此文件
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
    
    // 生成供客户端批量下载文件的dpk文件
    private void createDownloadPackage(File targetDir, List<String> pathes, List<String> names, int fileSize) throws Exception {
        // 按文件大小分组，分别制作
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
        // 生成dpk文件
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
    
    //服务器需要载入clientResVersion
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
    
    //获得所有文件列表
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
     * 对单个机型，把客户端资源拷贝到client_pkg目录下，并生成client.data文件。
     */
    public void makeClientData(PackageDefine pdef) throws Exception {
        // 第一步，清空目标目录
        File targetDir = new File(owner.baseDir, pdef.target);
        targetDir.mkdirs();
        deleteFilesInDir(targetDir);
        
        // 第二步，拷贝所有配置的文件到目标目录，注意etf文件的名字需要根据机型修改，pkg文件的名字需要解释
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
                    //大版地图
                    gid = Integer.parseInt(fname.substring(0, idxOf_));
                }else{
                    //其他版本地图
                    gid = Integer.parseInt(fname.substring(0, fname.length() - 4));
                }
                GameArea area = (GameArea)owner.findObject(GameArea.class, gid);
                MapFormat format = owner.config.getClientMapFormat(pdef.uimodel);
                srcFile = new File(owner.baseDir, "Areas/" + area.source.getName() + "/" + area.getID() + format.pkgName + ".pkg");
                tgtFile = new File(targetDir, pdef.usedFileName[i]);
            } else if(fname.endsWith(".jpg") || fname.endsWith(".png")){//将所有jpg后缀名换成img，防止android自动将其引入相册
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
        // 第三步，生成client.data文件，放到scripts和client_pkg目录下的机型目录里。
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        makeClientResourceDataFile(dos, pdef);
        dos.close();
        Utils.saveFileData(new File(targetDir, CLIENT_DATA_FILE), bos.toByteArray());
        Utils.saveFileData(new File(owner.baseDir, scriptsDir + "/" + pdef.scriptModel + "/" + CLIENT_DATA_FILE), bos.toByteArray());
        
        // 第四步，拷贝clientResVersion文件（在scripts的上层目录下）到机型目录中。
        File clientResDir = new File(owner.baseDir, scriptsDir).getParentFile();
        File clientResVersionFile = new File(clientResDir, "clientResVersion");
        if (clientResVersionFile.exists()) {
            Utils.copyFile(clientResVersionFile, new File(targetDir, "clientResVersion"));
        }
    }

    /* 
     * 生成某个机型的client.data文件。
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
     * 找出所有符合一个或多个带通配符的路径的文件路径。多个pattern之间用逗号分隔。
     */
    public String[] translateWildChars(String path) {
        String[] pathes = path.split(",");
        Set<String> retSet = new HashSet<String>();
        for (String p : pathes) {
            // 如果此录几个不包含通配符，则不进行后续匹配了
            if (!p.contains("*") && !p.contains("?")) {
                retSet.add(p);
                continue;
            }
            
            // 找到第一个没有通配符的目录，作为搜索根目录
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
            
            // 搜索出所有文件
            List<String> fileList = new ArrayList<String>();
            findFilesInDir(searchRootDir, "", fileList);
            
            // 把通配符转换为正则表达式字符串，匹配所有的文件名
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
    
    // 带*和?的匹配模式，转换成正则表达式
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
     * 找出一个目录中的所有文件（不包括目录）。
     * @param dir 搜索目录
     * @param relatePath 搜索目录的相对路径
     * @param saveSet 保存找出的文件路径（相对于根目录）
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
     * 删除一个目录中的所有文件（不包括目录）。
     * @param dir 搜索目录
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
