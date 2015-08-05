package com.pip.game.data.i18n;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.JavaTokenizer;
import com.pip.game.data.ProjectData;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.ExpressionList;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.gtl.etf.ETFFile;
import com.pip.gtl.etf.ETFUtil;
import com.pip.util.Utils;

/**
 * �������ڴ������ݹ��ʻ�/���ػ����⡣
 * 
 * ����һ���������԰汾��Ϊ3�������ʻ������롢���ػ���
 * 1. ���ʻ�������findI18NRelatedStrings�����ҳ���Ŀ��������Ҫ���ʻ����ַ�����
 * 2. ���룺�ѵ�һ���ҳ����ַ��������������롣
 * 3. ���ػ�������doI18N��������Ŀ�е��ַ����滻Ϊ�������Եİ汾��
 * 
 * ע������3����ɺ󣬻���Ҫ���м������⶯���Ա�֤���������ԣ�
 * 1. �����д������ֵĵ�ͼ���������ִ壩����Ҫ����������滻Ϊ�������԰汾��
 * 2. �����������е�client.pkg��
 * 3. �����������е�Buff���Skill�ࡣ
 * 
 * ��Ҫ���ʻ������ݰ�����
 * GameArea
 *   GameMapInfo: name
 *   GameMapNPC: name, functionName, searchName
 * HorseType: showName
 * Equipment: title
 * Formula: title, description
 * GiftGroup: title, errorMessage, groupMessage, giftMessage, maxExceedMessage, 
 *   repeatExceedMessage, timeSpaceMessage, timeErrorMessage, needItemMessage, 
 *   needVarMessage, giveOKMessage, bagFullMessage, 
 * Item: title, description
 * Shop: title
 * Suite: title
 * TeleportSet:
 *   Teleport: name
 * NPCTemplate:
 *   Rule: message
 * Quest: preDescription, postDescription, unfinishDescription
 *   QuestTarget: condition, description
 *   QuestInfo: 
 *     QuestTrigger: condition, action
 * BuffConfig: title, description
 * SkillConfig : title, description
 * Title: title, description
 * hints.xml: ������ʾ��Ϣ�ı�
 * Rank: title
 * scriptsĿ¼�µ�����etf.gz�ű��ļ�
 * questions.xml: �����ļ�
 *
 * @author lighthu
 */
public class I18NUtils {
    /**
     * ����������Ŀ����Ҫ���ʻ����ַ�����
     * @param proj ��Ŀ
     * @param mfile ���еķ����ļ�
     * @return ���δ����·��ֵ���Ҫ���ʻ����ַ���
     */
    public static String[][] findI18NRelatedStrings(ProjectData proj, MessageFile mfile) {
        I18NContext context = new I18NContext(true, false, mfile);
        processProject(proj, context);
        context.report();
        return context.getMissingStrings();
    }
    
    /**
     * ����Ŀ�����й��ʻ�����ļ����б��ػ���
     * @param proj ��Ŀ
     * @param mfile ���еķ����ļ�
     * @return ���δ�����û�б��ػ����ݵ��ַ���
     */
    public static String[][] doI18N(ProjectData proj, MessageFile mfile) {
        I18NContext context = new I18NContext(false, false, mfile);
        processProject(proj, context);
        context.report();
        return context.getMissingStrings();
    }
    
    /*
     * ����һ����Ŀ�е������ļ������ָ����I18Nģʽ�ǹ��ʻ����򲻸ı��ļ�������ı��ļ���
     */
    private static void processProject(ProjectData proj, I18NContext context) {
        // ���������ֵ�������
        for (int i = 0; i < proj.config.dictDataClasses.length; i++) {
            List<DataObject> list = proj.getDictDataListByType(proj.config.dictDataClasses[i]);
            boolean changed = false;
            System.out.println("process " + proj.config.dictDataClasses[i].getName() + "...");
            for (DataObject dobj : list) {
                try {
                    if (dobj.i18n(context)) {
                        changed = true;
                    }
                } catch (Throwable e) {
                    I18NError.error(null, "Error: " + proj.config.dictDataClasses[i].getName() + ", id=" + dobj.id, e);
                }
            }
            if (changed) {
                try {
                    proj.saveDictDataList(proj.config.dictDataClasses[i]);
                } catch (Exception e) {
                    I18NError.error(null, "Save error: " + proj.config.dictDataClasses[i].getName(), e);
                }
            }
        }
        
        // ����������������
        for (int i = 0; i < proj.config.supportDataClasses.length; i++) {
            List<DataObject> list = proj.getDataListByType(proj.config.supportDataClasses[i]);
            boolean changed = false;
            System.out.println("process " + proj.config.supportDataClasses[i].getName() + "...");
            for (DataObject dobj : list) {
                context.setCurrentTarget(dobj);
                try {
                    if (dobj.i18n(context)) {
                        changed = true;
                    }
                } catch (Throwable e) {
                    I18NError.error(dobj, "Error: " + proj.config.supportDataClasses[i].getName() + ", id=" + dobj.id, e);
                }
            }
            if (changed) {
                try {
                    proj.saveDataList(proj.config.supportDataClasses[i]);
                } catch (Exception e) {
                    I18NError.error(null, "Save error: " + proj.config.supportDataClasses[i].getName(), e);
                }
            }
        }
        context.setCurrentTarget(null);
        
        // ��������С��ʾ�����⴦����ΪС��ʾ���ǿɱ༭��DataObject����
        // hints.xml: ������ʾ��Ϣ�ı�
        try {
            System.out.println("process hints.xml");
            boolean changed = false;
            Document doc = Utils.loadDOM(new File(proj.baseDir, "hints.xml"));
            Element root = doc.getRootElement();
            List list = root.getChildren("hint");
            for (int i = 0; i < list.size(); i++) {
                Element elem = (Element)list.get(i);
                String tmp = context.input(elem.getText(), "Hint");
                if (tmp != null) {
                    elem.setText(tmp);
                    changed = true;
                }
            }
            if (changed) {
                Utils.saveDOM(doc, new File(proj.baseDir, "hints.xml"));
            }
        } catch (Exception e) {
            I18NError.error(null, "Error in hints.xml", e);
        }
        
        // ������Ŀ�����е�excel�ļ�
        if (new File(proj.baseDir, "i18n_xls.txt").exists()) {
            // �ҳ�������Ҫ���ʻ���excel�ļ�
            Set<String> excludeExcels = new HashSet<String>();
            try {
                BufferedReader br = new BufferedReader(new StringReader(Utils.loadFileContent(new File(proj.baseDir, "i18n_xls.txt"))));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().startsWith("exclude:")) {
                        excludeExcels.add(line.trim().substring("exclude:".length()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Set<String> xlsFiles = new HashSet<String>();
            Utils.findFilesInDir(proj.baseDir, ".xls", xlsFiles);
            for (String path : xlsFiles) {
                if (excludeExcels.contains(new File(path).getName())) {
                    continue;
                }
                try {
                    processExcelFile(path, context);
                } catch (Exception e) {
                    I18NError.error(null, "Error in " + path, e);
                }
            }
        }
    }
    
    /**
     * ����һ��Excel�ļ������и����ʻ��йص��ַ�����
     * @param path
     * @param contex
     * @throws Exception
     */
    private static void processExcelFile(String path, I18NContext context) throws Exception{
        File file = new File(path);
        String fileName = file.getName();
        
        WorkbookSettings set = new WorkbookSettings();
        set.setEncoding("ISO-8859-1");
        Workbook workbook = Workbook.getWorkbook(file, set);
        WritableWorkbook writeBook = Workbook.createWorkbook(file, workbook);
        WritableSheet[] sheets = writeBook.getSheets();
        boolean changed = false;
        for (WritableSheet sheet : sheets) {
            int rows = sheet.getRows();
            int cols = sheet.getColumns();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    Cell cell = sheet.getCell(j, i);
                    String text = cell.getContents().replaceAll("\r\n", "\n");
                    String newText = context.input(text, fileName);
                    if (newText != null) {
                        Label lbl = new Label(j, i, newText);
                        sheet.addCell(lbl);
                        changed = true;
                    }
                }
            }
        }
        writeBook.write();
        writeBook.close();
    }
    
    /*
     * ����һ��PQE���ʽ��������ʽ������иı䣬����true��
     */
    private static Set<String> constVarFuncs = new HashSet<String>();
    static {
        constVarFuncs.add("Set");
        constVarFuncs.add("Inc");
        constVarFuncs.add("Dec");
        constVarFuncs.add("E_Kill");
        constVarFuncs.add("E_KillWithMate");
        constVarFuncs.add("RefreshNPC");
        constVarFuncs.add("RefreshNPCAt");
        constVarFuncs.add("FindNPCByType");
        constVarFuncs.add("E_KillWithAssociation");
        constVarFuncs.add("AI_RegisterTimer");
        constVarFuncs.add("AI_CheckTimer");
    }
    public static boolean processExpressionList(Object exprObj, I18NContext context, int questID, String cause) {
        boolean ret = false;
        if (exprObj instanceof ExpressionList) {
            ExpressionList list = (ExpressionList)exprObj;
            for (int i = 0; i < list.getExprCount(); i++) {
                if (processExpressionList(list.getExpr(i), context, questID, cause)) {
                    ret = true;
                }
            }
        } else if (exprObj instanceof Expression) {
            Expression expr = (Expression)exprObj;
            for (int i = 0; i < expr.jjtGetNumChildren(); i++) {
                if (processExpressionList(expr.jjtGetChild(i), context, questID, cause)) {
                    ret = true;
                }
            }
        } else if (exprObj instanceof Expr0) {
            Expr0 expr0 = (Expr0)exprObj;
            if (expr0.type == Expr0.TYPE_IDENTIFIER) {
                String tmp = context.input(expr0.value, "Quest Variable");
                if (tmp != null) {
                    expr0.value = tmp;
                    ret = true;
                }
            } else if (expr0.type == Expr0.TYPE_STRING) {
                // һЩ������ѱ�������Ϊ�ַ�����������ʹ�ã�Set/Inc/Dec/E_Kill/E_KillWithMate/RefreshNPC/RefreshNPCAt/FindNPCByType
                boolean isVar = false;
                if (expr0.jjtGetParent() instanceof Expression && expr0.jjtGetParent().jjtGetParent() instanceof FunctionCall) {
                    FunctionCall fc = (FunctionCall)expr0.jjtGetParent().jjtGetParent();
                    if (constVarFuncs.contains(fc.funcName)) {
                        isVar = true;
                    }
                }
                String str = PQEUtils.translateStringConstant(expr0.value);
                String tmp = context.input(str, isVar ? "Quest Variable" : (cause != null ? cause : "Quest(" + questID + ")"));
                if (tmp != null) {
                    expr0.value = "\"" + PQEUtils.reverseConv(tmp) + "\"";
                    ret = true;
                }
            } else if (expr0.type == Expr0.TYPE_FUNC) {
                FunctionCall fc = expr0.getFunctionCall();
                for (int i = 0; i < fc.getParamCount(); i++) {
                    if (processExpressionList(fc.getParam(i), context, questID, cause)) {
                        ret = true;
                    }
                }
            }
        }
        return ret;
    }
    
    /**
     * ��������Java�ļ�����Ҫ���ʻ����ַ�����
     * @param root ��Ŀ¼
     * @param mfile �����ļ�
     * @return ���δ����·��ֵ���Ҫ���ʻ����ַ���
     */
    public static String[][] findI18NRelatedJavaStrings(File root, MessageFile mfile, String encoding1, String encoding2) {
        I18NContext context = new I18NContext(true, true, mfile);
        processJava(root, context, encoding1, encoding2);
        context.report();
        return context.getMissingStrings();
    }
    
    /**
     * ��Java�ļ������й��ʻ�����ļ����б��ػ���
     * @param root ��Ŀ¼
     * @param mfile �����ļ�
     * @return ���δ�����û�б��ػ����ݵ��ַ���
     */
    public static String[][] doI18NJava(File root, MessageFile mfile, String encoding1, String encoding2) {
        I18NContext context = new I18NContext(false, true, mfile);
        processJava(root, context, encoding1, encoding2);
        context.report();
        return context.getMissingStrings();
    }
    
    /*
     * ����һ��Ŀ¼�µ�����Java�ļ������ָ����I18Nģʽ�ǹ��ʻ����򲻸ı��ļ�������ı��ļ���
     */
    private static void processJava(File root, I18NContext context, String encoding1, String encoding2) {
        List<File> javaFiles = findFiles(root, ".java");
        List<File> gtlFiles = findFiles(root, ".gtl");
        javaFiles.addAll(gtlFiles);
        gtlFiles = findFiles(root, ".h");
        javaFiles.addAll(gtlFiles);
        for (File jf : javaFiles) {
            if (jf.getName().startsWith("AutoGenerated")) {
                continue;
            }
            try {
                List<String> tokens = JavaTokenizer.parse(jf, encoding1);
                boolean changed = false;
                for (int i = 0; i < tokens.size(); i++) {
                    String tk = tokens.get(i);
                    if (tk.startsWith("\"") && tk.endsWith("\"")) {
                        String oldStr = PQEUtils.translateStringConstant(tk);
                        String newStr = context.input(oldStr, jf.getName());
                        if (newStr != null) {
                            tokens.set(i, "\"" + PQEUtils.reverseConv(newStr) + "\"");
                            changed = true;
                        }
                    }
                }
                if (!context.isI18NMode()) {
                    JavaTokenizer.save(jf, encoding2, tokens);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * �ҳ�Ŀ¼�����е�Java�ļ���
     */
    public static List<File> findFiles(File root, String suffix) {
        List<File> retList = new ArrayList<File>();
        List<File> pendingList = new ArrayList<File>();
        pendingList.add(root);
        while (pendingList.size() > 0) {
            File ff = pendingList.remove(0);
            File[] ffs = ff.listFiles();
            for (File af : ffs) {
                if (af.isDirectory()) {
                    pendingList.add(af);
                } else if (af.getName().endsWith(suffix)) {
                    retList.add(af);
                }
            }
        }
        return retList;
    }
    
    /**
     * ��������ActionScript�ļ�����Ҫ���ʻ����ַ�����
     * @param root ��Ŀ¼
     * @param mfile �����ļ�
     * @return ���δ����·��ֵ���Ҫ���ʻ����ַ���
     */
    public static String[][] findI18NRelatedActionScriptStrings(File root, MessageFile mfile, String encoding1, String encoding2) {
        I18NContext context = new I18NContext(true, true, mfile);
        processActionScript(root, context, encoding1, encoding2);
        context.report();
        return context.getMissingStrings();
    }
    
    /**
     * ��ActionScript�ļ������й��ʻ�����ļ����б��ػ���
     * @param root ��Ŀ¼
     * @param mfile �����ļ�
     * @return ���δ�����û�б��ػ����ݵ��ַ���
     */
    public static String[][] doI18NActionScript(File root, MessageFile mfile, String encoding1, String encoding2) {
        I18NContext context = new I18NContext(false, true, mfile);
        processActionScript(root, context, encoding1, encoding2);
        context.report();
        return context.getMissingStrings();
    }
    
    /*
     * ����һ��Ŀ¼�µ�����ActionScript�ļ������ָ����I18Nģʽ�ǹ��ʻ����򲻸ı��ļ�������ı��ļ���
     */
    private static void processActionScript(File root, I18NContext context, String encoding1, String encoding2) {
        List<File> actionScriptFiles = findFiles(root, ".as");
        for (File jf : actionScriptFiles) {
            if (jf.getName().startsWith("AutoGenerated")) {
                continue;
            }
            try {
                List<String> tokens = JavaTokenizer.parse(jf, encoding1);
                boolean changed = false;
                for (int i = 0; i < tokens.size(); i++) {
                    String tk = tokens.get(i);
                    if (tk.startsWith("\"") && tk.endsWith("\"")) {
                        String oldStr = PQEUtils.translateStringConstant(tk);
                        String newStr = context.input(oldStr, jf.getName());
                        if (newStr != null) {
                            tokens.set(i, "\"" + PQEUtils.reverseConv(newStr) + "\"");
                            changed = true;
                        }
                    }
                }
                if (!context.isI18NMode()) {
                    JavaTokenizer.save(jf, encoding2, tokens);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * ��������XML�ļ�����Ҫ���ʻ����ַ�����
     * @param root ��Ŀ¼
     * @param mfile �����ļ�
     * @return ���δ����·��ֵ���Ҫ���ʻ����ַ���
     */
    public static String[][] findI18NRelatedXMLStrings(File root, MessageFile mfile, String encoding1, String encoding2) {
        I18NContext context = new I18NContext(true, false, mfile);
        processXML(root, context, encoding1, encoding2);
        context.report();
        return context.getMissingStrings();
    }
    
    /**
     * ��XML�ļ������й��ʻ�����ļ����б��ػ���
     * @param root ��Ŀ¼
     * @param mfile �����ļ�
     * @return ���δ�����û�б��ػ����ݵ��ַ���
     */
    public static String[][] doI18NXML(File root, MessageFile mfile, String encoding1, String encoding2) {
        I18NContext context = new I18NContext(false, false, mfile);
        processXML(root, context, encoding1, encoding2);
        context.report();
        return context.getMissingStrings();
    }
    
    /*
     * ����һ��Ŀ¼�µ�����XML�ļ������ָ����I18Nģʽ�ǹ��ʻ����򲻸ı��ļ�������ı��ļ���
     */
    private static void processXML(File root, I18NContext context, String encoding1, String encoding2) {
        List<File> xmlFiles = findFiles(root, ".xml");
        for (File xf : xmlFiles) {
            try {
                Document doc = Utils.loadDOM(xf);
                boolean changed = processXML(xf.getName(), doc.getRootElement(), context, encoding1, encoding2);
                if (changed && !context.isI18NMode()) {
                    Utils.saveDOM(doc, xf);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /*
     * ����һ��XMLԪ���е����֡�
     * @param element
     * @param context
     * @param encoding1
     * @param encoding2
     * @return ������޸ģ�����true��
     */
    private static boolean processXML(String fileName, Element element, I18NContext context, String encoding1, String encoding2) {
        boolean changed = false;
        
        // ��������
        List list = element.getAttributes();
        for (int i = 0; i < list.size(); i++) {
            Attribute attr = (Attribute)list.get(i);
            String newStr = context.input(attr.getValue(), fileName);
            if (newStr != null) {
                attr.setValue(newStr);
                changed = true;
            }
        }
        
        // ��������
        list = element.getChildren();
        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            if (o instanceof String) {
                String newStr = context.input((String)o, fileName);
                if (newStr != null) {
                    list.set(i, newStr);
                    changed = true;
                }
            } else if (o instanceof Element) {
                if (processXML(fileName, (Element)o, context, encoding1, encoding2)) {
                    changed = true;
                }
            }
        }
        String str = element.getText();
        String newStr = context.input(str, fileName);
        if (newStr != null) {
            element.setText(newStr);
            changed = true;
        }
        return changed;
    }
    
    /**
     * ����Ŀ�Ľű����й��ʻ�����ΪFlash������ȡ�ķ�����
     * ����Flash�Ľű���ֱ�Ӵ�����ͻ��˵ģ����ӷ��������أ�����Ϊ�˼򻯿ͻ��˴�������̣������ͻ��˵Ĵ��Ч�ʣ����⽫�ű��ķ�����ȡ������
     * 
     * @param proj ��Ŀ
     * @param mfile ���еķ����ļ�
     * @return ���δ�����û�б��ػ����ݵ��ַ���
     */
    public static String[][] doI18NScript(File baseDir, MessageFile mfile) {
        I18NContext context = new I18NContext(false, true, mfile);
        processScript(baseDir, context);
        context.report();
        return context.getMissingStrings();
    }
    
    /**
     * ��һ���ض�Ŀ¼�Ľű����й��ʻ���
     * 
     * @param dir ָ����Ŀ¼
     * @param mfile ���еķ����ļ�
     */
    public static void doI18NScriptSpecifiedDir(File dir, MessageFile mfile) {
        I18NContext context = new I18NContext(false, true, mfile);
        processScriptSpecifiedDir(dir, context);
    }
    
    /**
     * ����������Ŀ�нű���Ҫ���ʻ����ַ�����
     * @param proj ��Ŀ
     * @param mfile ���еķ����ļ�
     * @return ���δ����·��ֵ���Ҫ���ʻ����ַ���
     */
    public static String[][] findI18NRelatedScriptStrings(File baseDir, MessageFile mfile) {
        I18NContext context = new I18NContext(true, true, mfile);
        processScript(baseDir, context);
        context.report();
        return context.getMissingStrings();
    }
    
    /*
     * ����һ����Ŀ�еĽű��ļ������ָ����I18Nģʽ�ǹ��ʻ����򲻸ı��ļ�������ı��ļ�����ΪFlash������ȡ�ķ�����
     */
    private static void processScript(File baseDir, I18NContext context) {
        String tmp;
        boolean changed;
        
        // �������нű��ļ�
        // scriptsĿ¼�µ�����etf.gz�ļ�
        List<File> scriptFiles = findFiles(new File(baseDir, "scripts"), ".etf.gz");
        for (File sf : scriptFiles) {
            // System.out.println("process " + sf);
            try {
                FileInputStream fis = new FileInputStream(sf);
                GZIPInputStream gis = new GZIPInputStream(fis);
                ETFFile etf = ETFFile.load(gis);
                fis.close();
                changed = false;
                for (int i = 0; i < etf.stringTable.length; i++) {
                    tmp = context.input(etf.stringTable[i], "Script");
                    if (tmp != null) {
                        etf.stringTable[i] = tmp;
                        changed = true;
                    }
                }
                if (changed) {
                    FileOutputStream fos = new FileOutputStream(sf);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    GZIPOutputStream zos = new GZIPOutputStream(bos);
                    ETFUtil.save(etf, zos);
                    zos.flush();
                    zos.close();
                    bos.flush();
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /*
     * ����һ��Ŀ¼�еĽű��ļ���
     */
    private static void processScriptSpecifiedDir(File dir, I18NContext context) {
        String tmp;
        boolean changed;
        
        // �������нű��ļ�
        // scriptsĿ¼�µ�����etf.gz�ļ�
        List<File> scriptFiles = findFiles(dir, ".etf.gz");
        for (File sf : scriptFiles) {
            System.out.println("process " + sf);
            try {
                FileInputStream fis = new FileInputStream(sf);
                GZIPInputStream gis = new GZIPInputStream(fis);
                ETFFile etf = ETFFile.load(gis);
                fis.close();
                changed = false;
                for (int i = 0; i < etf.stringTable.length; i++) {
                    tmp = context.input(etf.stringTable[i], "Script");
                    if (tmp != null) {
                        etf.stringTable[i] = tmp;
                        changed = true;
                    }
                }
                if (changed) {
                    FileOutputStream fos = new FileOutputStream(sf);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    GZIPOutputStream zos = new GZIPOutputStream(bos);
                    ETFUtil.save(etf, zos);
                    zos.flush();
                    zos.close();
                    bos.flush();
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
