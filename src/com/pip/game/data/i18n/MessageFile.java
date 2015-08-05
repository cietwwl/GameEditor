package com.pip.game.data.i18n;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.pip.game.data.ProjectData;
import com.pip.game.data.quest.Quest;

/**
 * ���ദ��Excel��ʽ���ַ�����Ӧ��
 * @author lighthu
 */
public class MessageFile {
    /*
     * Դ���ԡ�
     */
    private String fromLang;
    /*
     * Ŀ�����ԡ�
     */
    private String toLang;
    /*
     * ҳ�б�
     */
    private String[] pageTitles;
    /*
     * ��ҳδ������ַ�����
     */
    private List[] pageTexts1;
    /*
     * ��ҳ�������ַ�����
     */
    private List[] pageTexts2;
    /*
     * ÿ���ַ�������Դ��
     */
    private List[] pageTextSources;
    /*
     * Դ�ļ���
     */
    private File sourceFile;
    /*
     * �������ַ�����
     */
    private List<String> newTexts;
    /*
     * �����ַ�������Դ��
     */
    private List<String> newTextSources;
    /*
     * �����ַ�������������ӳ�䡣
     */
    private Map<String, String> searchTable;
    /*
     * ȥ�����������Ժ���ַ������������ʽ����ģ��ƥ�䷭�롣������key��ȥ���������ݵ��ַ�����value��ԭʼ�ַ����������ж������
     */
    private Map<String, List<String>> rawTextSearchTable;
    /*
     * ����û��ͨ���Ϸ��Լ�����Ŀ����Ӧ������ԭ��
     */
    private Map<String, String> errorItems;
    private Map<String, String> errorItemSource;
    private Map<String, String> errorItemMessage;

    public MessageFile(File file, String lang1, String lang2) throws Exception {
        System.out.println("load: " + file);
        fromLang = lang1;
        toLang = lang2;
        sourceFile = file;
        load();
        generateSearchTable();
        reportErrors();
    }
    
    // ����rawTextSearchTable
    private void generateSearchTable() {
        searchTable = new HashMap<String, String>();
        rawTextSearchTable = new HashMap<String, List<String>>();
        errorItems = new HashMap<String, String>();
        errorItemSource = new HashMap<String, String>();
        errorItemMessage = new HashMap<String, String>();
        HashMap<String, String> cacheMap = new HashMap<String, String>();
        HashMap<String, String> cacheLineMap = new HashMap<String, String>();
        for (int i = 0; i < pageTitles.length; i++) {
            List<String> texts1 = (List<String>) pageTexts1[i];
            List<String> texts2 = (List<String>) pageTexts2[i];
            List<String> texts3 = (List<String>) pageTextSources[i];
            for (int j = 0; j < texts1.size(); j++) {
                String raw = texts1.get(j);
                String trans = texts2.get(j);
                String source = texts3.get(j);
                searchTable.put(raw, trans);
                
                // ���
                String error = checkError(i, j, raw, trans, source, cacheMap, cacheLineMap);
                if (error != null) {
                    errorItems.put(raw, "ҳ��" + pageTitles[i] + "\n�кţ�" + (j + 1) + "\n��Դ��" + source + "\n" + error);
                    errorItemSource.put(raw, source);
                    errorItemMessage.put(raw, error);
                }
                
                // ȥ����������
                String tmp = removeControlData(raw);
                if (tmp != null) {
                    List<String> list = rawTextSearchTable.get(tmp);
                    if (list == null) {
                        list = new ArrayList<String>();
                        rawTextSearchTable.put(tmp, list);
                    }
                    list.add(raw);
                }
            }
        }
    }
    
    /**
     * �ڱ�׼����������
     */
    public void reportErrors() {
        for (String error : errorItems.keySet()) {
            System.err.println(error + "\n" + searchTable.get(error) + "\n" + errorItems.get(error));
            System.err.println("--------------------------------------------------------------------------------------------------------------------------------------------------");
        }
    }
    
    /**
     * ���ֵĴ��������
     * @return
     */
    public int getErrorCount() {
        return errorItems.size();
    }
    
    /**
     * �Ѵ��������һ��excel�ļ��С�����ļ����Բ����ڡ�
     * @param file
     */
    public void reportErrorToExcel(String file) throws Exception {
        // ����һ���յ�excel�ļ�
        InputStream is = getClass().getResourceAsStream("/com/pip/sanguo/data/i18n/empty_sheet.xls");
        byte[] buf = new byte[256];
        FileOutputStream fos = new FileOutputStream(file);
        int len;
        while ((len = is.read(buf)) != -1) {
            if (len > 0) {
                fos.write(buf, 0, len);
            }
        }
        fos.close();
        is.close();
        
        // �����excel�ĵ�һҳд����
        WorkbookSettings set = new WorkbookSettings();
        set.setEncoding("ISO-8859-1");
        Workbook newbook = Workbook.getWorkbook(new File(file), set);
        WritableWorkbook writebook = Workbook.createWorkbook(new File(file), newbook);
        WritableSheet outsheet = writebook.getSheet(0);
        int lineNum = 0;
        for (String error : errorItems.keySet()) {
            String errorTrans = searchTable.get(error);
            String errorSource = errorItemSource.get(error);
            String errorMessage = errorItemMessage.get(error);
            outsheet.addCell(new Label(0, lineNum, error));
            outsheet.addCell(new Label(1, lineNum, errorTrans));
            outsheet.addCell(new Label(2, lineNum, errorSource));
            outsheet.addCell(new Label(3, lineNum, errorMessage));
            lineNum++;
        }
        writebook.write();
        writebook.close();
    }
    
    /*
     * ���һ��������Ŀ�Ƿ���ڿ��ܵķ�����󡣼��Ĺ��������
     * 1. ���ԭʼ�ַ�������|����\n����ô����Щ�ַ�����ּ�飬ÿ�����εķ��붼����Ϊ�գ�����ԭʼ�ַ����ǿգ���
     * 2. ÿ��������Ŀtrim�Ժ󶼲���Ϊ�ա�
     * 3. ���ԭʼ�ַ����г�����${xxx}��������һ���ֱ����ڷ������ַ�����Ҳԭ�����֣����Զ�������һ������
     * 4. ���sourceΪQuest Variable����ô���ܷ��루���Զ�������һ������
     * 5. ���ܵķ��벻һ�£���ĳ���ط������A��������һ�����еĵط������B����
     * 6. ���ԭʼ�ַ����г�����<n></n>��<l></l>��ǣ���ô���뱣֤�����������ⲿ�ֵĸ�ʽҲ����ȷ�ġ�
     * <n>id,name(mapname:x,y)</n>
     * <l>mapid,mapname:x,y</l>
     * <i>xxx</i>
     */
    private String checkError(int pageIndex, int lineNum, String raw, String trans, String source, Map<String, String> cacheMap, Map<String, String> cacheLineMap) {
        if (raw.trim().isEmpty()) {
            return "����Ϊ��";
        }
        if (source.equals("Quest Variable")) {
            String t = autoTranslate(raw, false);
            if (!trans.equals(t)) {
                return "����������ܱ�����";
            }
        }
        String[] lines = raw.split("\n|\\|");
        String[] tlines = trans.split("\n|\\|");
        if (lines.length != tlines.length) {
            return "����ǰ��������һ��";
        }
        for (int i = 0; i < lines.length; i++) {
            String r = lines[i];
            String t = tlines[i];
            if (r.trim().isEmpty() && !t.trim().isEmpty()) {
                return "����ǰΪ�գ������Ϊ��";
            }
            if (!r.trim().isEmpty() && t.trim().isEmpty()) {
                return "����ǰ��Ϊ�գ������Ϊ��";
            }
            
            // ����Ƿ����ǰ���ֵķ��벻һ��
            if (cacheMap.containsKey(r) && !t.equals(cacheMap.get(r))) {
                return r + "�������ط����벻һ�£�" + cacheLineMap.get(r);
            }
            cacheMap.put(r, t);
            cacheLineMap.put(r, "ҳ��" + pageTitles[pageIndex] + " �У�" + (lineNum + 1));
            
            // �����ܳ��ֵı���
            List<String> varList1 = findVariables(r);
            List<String> varList2 = findVariables(t);
            if (varList1.size() != varList2.size()) {
                return "�������벻һ��";
            }
            for (int j = 0; j < varList1.size(); j++) {
                String tt = autoTranslate(varList1.get(j), false);
                for (int k = 0; k < varList2.size(); k++) {
                    if (varList2.get(k).equals(tt)) {
                        varList2.remove(k);
                        break;
                    }
                }
            }
            if (varList2.size() != 0) {
                return "�������벻һ��";
            }
            
            // �����ܳ��ֵ�NPC����
            varList1 = findNPCRef(r);
            varList2 = findNPCRef(t);
            if (varList1.size() != varList2.size()) {
                return "NPC���÷��벻һ��";
            }
            for (int j = 0; j < varList2.size(); j++) {
                if (!checkNPCRef(varList2.get(j))) {
                    return "NPC���ø�ʽ����ȷ: " + varList2.get(j);
                }
            }
            
            // �����ܳ��ֵ�<i>
            varList1 = findNumber(r);
            varList2 = findNumber(t);
            if (varList1.size() != varList2.size()) {
                return "���ֱ�ʾ���벻һ��";
            }
            
            // �����ܳ��ֵ�<c>
            varList1 = findColor(r);
            varList2 = findColor(t);
            if (varList1.size() != varList2.size()) {
                return "��ɫ��ʾ���벻һ��";
            }
            
           // �����ܳ��ֵ�LOCATION
            varList1 = findLocationRef(r);
            varList2 = findLocationRef(t);
            if (varList1.size() != varList2.size()) {
                return "λ�����÷��벻һ��";
            }
            for (int j = 0; j < varList2.size(); j++) {
                if (!checkLocationRef(varList2.get(j))) {
                    return "λ�����ø�ʽ����ȷ: " + varList2.get(j);
                }
            }
        }
        return null;
    }
    
    // ���NPC���ø�ʽ�Ƿ���ȷ��������<n>id,name(mapname:x,y)</n>
    private boolean checkNPCRef(String refText) {
        try {
            int pos1 = refText.indexOf(',');
            if (pos1 == -1) {
                return false;
            }
            int pos2 = refText.indexOf('(', pos1 + 1);
            if (pos2 == -1) {
                return false;
            }
            int pos3 = refText.indexOf(':', pos2 + 1);
            if (pos3 == -1) {
                return false;
            }
            int pos4 = refText.indexOf(',', pos3 + 1);
            if (pos4 == -1) {
                return false;
            }
            int pos5 = refText.indexOf(')', pos4 + 1);
            if (pos5 == -1) {
                return false;
            }
            String sec1 = refText.substring(0, pos1);
            String sec2 = refText.substring(pos1 + 1, pos2);
            String sec3 = refText.substring(pos2 + 1, pos3);
            String sec4 = refText.substring(pos3 + 1, pos4);
            String sec5 = refText.substring(pos4 + 1, pos5);
            String sec6 = refText.substring(pos5 + 1);
            if (!isNumber(sec1) || !isNumber(sec4) || !isNumber(sec5) || sec6.length() > 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isNumber(String str) {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if ((ch >= '0' && ch <= '9') || ch == '-') {
            } else {
                return false;
            }
        }
        return true;
    }
    
    // ���λ�����ø�ʽ�Ƿ���ȷ��������<l>mapid,mapname:x,y</l>
    private boolean checkLocationRef(String refText) {
        try {
            int pos1 = refText.indexOf(',');
            if (pos1 == -1) {
                return false;
            }
            int pos2 = refText.indexOf(':', pos1 + 1);
            if (pos2 == -1) {
                return false;
            }
            int pos3 = refText.indexOf(',', pos2 + 1);
            if (pos3 == -1) {
                return false;
            }
            String sec1 = refText.substring(0, pos1);
            String sec2 = refText.substring(pos1 + 1, pos2);
            String sec3 = refText.substring(pos2 + 1, pos3);
            String sec4 = refText.substring(pos3 + 1);
            if (!isNumber(sec1) || !isNumber(sec3) || !isNumber(sec4)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private List<String> findColor(String text) {
        List<String> list = new ArrayList<String>();
        int pos = text.indexOf("<c");
        while (pos != -1) {
            int pos2 = text.indexOf("</c>", pos);
            if (pos2 == -1) {
                break;
            }
            list.add(text.substring(pos, pos2 + 4));
            pos = text.indexOf("<c", pos2);
        }
        return list;
    }
    
    private List<String> findNumber(String text) {
        List<String> list = new ArrayList<String>();
        int pos = text.indexOf("<i>");
        while (pos != -1) {
            int pos2 = text.indexOf("</i>", pos);
            if (pos2 == -1) {
                break;
            }
            list.add(text.substring(pos + 3, pos2));
            pos = text.indexOf("<i>", pos2);
        }
        return list;
    }
    
    private List<String> findLocationRef(String text) {
        List<String> list = new ArrayList<String>();
        int pos = text.indexOf("<l>");
        while (pos != -1) {
            int pos2 = text.indexOf("</l>", pos);
            if (pos2 == -1) {
                break;
            }
            list.add(text.substring(pos + 3, pos2));
            pos = text.indexOf("<l>", pos2);
        }
        return list;
    }
    
    private List<String> findNPCRef(String text) {
        List<String> list = new ArrayList<String>();
        int pos = text.indexOf("<n>");
        while (pos != -1) {
            int pos2 = text.indexOf("</n>", pos);
            if (pos2 == -1) {
                break;
            }
            list.add(text.substring(pos + 3, pos2));
            pos = text.indexOf("<n>", pos2);
        }
        return list;
    }
    
    private List<String> findVariables(String text) {
        List<String> list = new ArrayList<String>();
        int pos = text.indexOf("${");
        while (pos != -1) {
            int pos2 = text.indexOf("}", pos);
            if (pos2 == -1) {
                break;
            }
            list.add(text.substring(pos, pos2 + 1));
            pos = text.indexOf("${", pos2);
        }
        return list;
    }
    
    /*
     * ȥ��һ���ַ�����Ŀ������ݡ��������ݰ�����
     * <n>��</n>֮��Ĳ���
     * <l>��</l>֮��Ĳ���
     * ${��}֮��Ĳ���
     * ���з������ַ�0x4E00��0x9FA5֮��
     */
    private String removeControlData(String str) {
        str = removePart(str, "<n>", "</n>");
        str = removePart(str, "<l>", "</l>");
        str = removePart(str, "${", "}");
        return removeNonCNChars(str);
    }
    
    /*
     * �Ƴ�һ���ַ����еķǺ����ַ���
     */
    private String removeNonCNChars(String str) {
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch >= 0x4E00 && ch <= 0x9FA5) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    
    /*
     * �Ƴ�һ���ַ�����������ĳ����ģ���м�Ĳ��֡�
     */
    private String removePart(String str, String prefix, String suffix) {
        StringBuilder sb = new StringBuilder(str.length());
        int start = 0;
        int pos = str.indexOf(prefix, start);
        while (pos != -1) {
            int pos2 = str.indexOf(suffix, pos + prefix.length());
            if (pos2 == -1) {
                break;
            }
            sb.append(str.substring(start, pos));
            start = pos2 + suffix.length();
            pos = str.indexOf(prefix, start);
        }
        sb.append(str.substring(start));
        return sb.toString();
    }

    private void load() throws Exception {
        WorkbookSettings set = new WorkbookSettings();
        set.setEncoding("ISO-8859-1");
        Workbook workbook = Workbook.getWorkbook(sourceFile, set);
        Sheet[] sheets = workbook.getSheets();
        pageTitles = new String[sheets.length];
        pageTexts1 = new List[sheets.length];
        pageTexts2 = new List[sheets.length];
        pageTextSources = new List[sheets.length];
        newTexts = new ArrayList<String>();
        newTextSources = new ArrayList<String>();
        for (int i = 0; i < sheets.length; i++) {
            Sheet sheet = sheets[i];
            pageTitles[i] = sheet.getName();
            int rows = sheet.getRows();
            List<String> texts1 = new ArrayList<String>();
            List<String> texts2 = new ArrayList<String>();
            List<String> texts3 = new ArrayList<String>();
            for (int j = 0; j < rows; j++) {
                String text1 = sheet.getCell(0, j).getContents();
                text1 = text1.replaceAll("\r\n", "\n");
                String text2 = sheet.getCell(1, j).getContents();
                if ("vi_VN".equals(toLang)) {
                    text2 = convertVNString(text2);
                }
                text2 = text2.replaceAll("\r\n", "\n");
                texts1.add(text1);
                texts2.add(text2);
                if (sheet.getColumns() > 2) {
                    texts3.add(sheet.getCell(2, j).getContents());
                }
                else {
                    texts3.add("");
                }
            }
            pageTexts1[i] = texts1;
            pageTexts2[i] = texts2;
            pageTextSources[i] = texts3;
        }
        workbook.close();
    }

    public void save() throws Exception {
        if (newTexts.size() > 0) {
            WorkbookSettings set = new WorkbookSettings();
            set.setEncoding("ISO-8859-1");
            Workbook srcbook = Workbook.getWorkbook(sourceFile, set);
            WritableWorkbook workbook = Workbook.createWorkbook(sourceFile, srcbook);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
            String name = sdf.format(new Date());
            for (int start = 0; start < newTexts.size(); start += 10000) {
                int id = 1;
                while (true) {
                    String nname = name + "_" + id;
                    if (workbook.getSheet(nname) == null) {
                        break;
                    }
                    id++;
                }
                WritableSheet sheet = workbook.createSheet(name + "_" + id, 0);
                for (int i = start; i < start + 10000 && i < newTexts.size(); i++) {
                    Label lbl = new Label(0, i - start, newTexts.get(i));
                    sheet.addCell(lbl);
                    lbl = new Label(1, i - start, autoTranslate(newTexts.get(i), !newTextSources.get(i).equals("Quest Variable")));
                    sheet.addCell(lbl);
                    lbl = new Label(2, i - start, newTextSources.get(i));
                    sheet.addCell(lbl);
                }
            }
            workbook.write();
            workbook.close();
        }
    }

    public void addString(String str, String cause) {
        newTexts.add(str);
        newTextSources.add(cause);
    }

    public Map<String, String> getMap() {
        return searchTable;
    }

    /**
     * �Զ�����һ���ַ�����
     * @param src
     * @return
     */
    public String autoTranslate(String src, boolean supportSmartMatch) {
        // ֧�ּ����Զ�ת��
        if ("zh_CN".equals(fromLang) && "zh_TW".equals(toLang)) {
            return BIG5toGBK.convertGB2BIG5(src);
        }
        supportSmartMatch = false;   // ��ʱ�ر��Զ����빦��
        if (supportSmartMatch) {
            // ȥ�������ַ����Ƿ���ƥ��
            String tmp = removeControlData(src);
            if (rawTextSearchTable.containsKey(tmp)) {
                String raw = rawTextSearchTable.get(tmp).get(0);    // ԭʼ�ַ���
                String trans = searchTable.get(raw);                // ԭʼ�ַ����ķ���
                return autoTranslate(src, raw, trans);
            }
        }
        
        return src;
    }
    
    /*
     * ������һ���ο��������Զ�����һ�����ַ�����
     * @param newText ���ַ���
     * @param refText �ο��ַ���������ַ���ȥ�������ַ�������ַ�����ͬ
     * @param refTrans �ο��ַ����ķ���
     * @return
     */
    private String autoTranslate(String newText, String refText, String refTrans) {
        return refText + "\n" + refTrans;
    }
    
    public static void merge(String newFile, String oldFile) throws Exception {
        MessageFile f1 = new MessageFile(new File(oldFile), "zh_CN", "en_US");
        Map<String, String> transMap = f1.getMap();

        WorkbookSettings set = new WorkbookSettings();
        set.setEncoding("ISO-8859-1");
        Workbook newbook = Workbook.getWorkbook(new File(newFile), set);
        WritableWorkbook writebook = Workbook.createWorkbook(new File(newFile), newbook);
        for (WritableSheet outsheet : writebook.getSheets()) {
            int rows = outsheet.getRows();
            for (int j = 0; j < rows; j++) {
                String text1 = outsheet.getCell(0, j).getContents();
                if (transMap.containsKey(text1)) {
                    Label lbl = new Label(1, j, transMap.get(text1));
                    outsheet.addCell(lbl);
                }
            }
        }
        writebook.write();
        writebook.close();
    }
    
    /**
     * У���ļ��г��ֵ�����NPC���ú�λ�����á�
     * @param file
     * @throws Exception
     */
    public static void adjustRef(ProjectData proj, String file) throws Exception {
        WorkbookSettings set = new WorkbookSettings();
        set.setEncoding("ISO-8859-1");
        Workbook newbook = Workbook.getWorkbook(new File(file), set);
        WritableWorkbook writebook = Workbook.createWorkbook(new File(file), newbook);
        for (WritableSheet outsheet : writebook.getSheets()) {
            int rows = outsheet.getRows();
            for (int j = 0; j < rows; j++) {
                String text2 = outsheet.getCell(1, j).getContents();
                try {
                    String newText = Quest.validateMixedText(proj, text2);
                    if (!text2.equals(newText)) {
                        Label lbl = new Label(1, j, newText);
                        outsheet.addCell(lbl);
                    }
                } catch (Exception e) {
                    System.err.println("����" + text2);
                }
            }
        }
        writebook.write();
        writebook.close();
    }

    public static void main(String[] args) throws Exception {
        MessageFile f1 = new MessageFile(new File("C:\\Users\\lighthu\\Desktop\\messages_trans.xls"), "zh_CN", "en_US");
        File sourceFile = new File("C:\\Users\\lighthu\\Desktop\\messages.xls");
        File destFile = new File("C:\\Users\\lighthu\\Desktop\\messages2.xls");

        Workbook srcbook = Workbook.getWorkbook(sourceFile);
        Workbook destbook = Workbook.getWorkbook(destFile);
        WritableWorkbook writebook = Workbook.createWorkbook(destFile, destbook);
        Sheet[] sheets = srcbook.getSheets();
        Map<String, String> existMap = f1.getMap();
        int found = 0;
        for (int i = 0; i < sheets.length; i++) {
            Sheet sheet = sheets[i];
            WritableSheet outsheet = writebook.createSheet(sheet.getName(), i);
            int rows = sheet.getRows();
            for (int j = 0; j < rows; j++) {
                String text1 = sheet.getCell(0, j).getContents();
                text1 = text1.replaceAll("\r\n", "\n");
                String text2 = sheet.getCell(1, j).getContents();
                text2 = text2.replaceAll("\r\n", "\n");
                String text3 = sheet.getCell(2, j).getContents();

                if (!text3.equals("Quest Variable")) {
                    if (existMap.containsKey(text1) && !existMap.get(text1).equals(text1)) {
                        text2 = existMap.get(text1);
                        System.out.println("found: " + text1);
                        found++;
                    } else {
                        text3 = "δ�ҵ�ƥ��" + text3;
                    }
                }
                
                Label lbl = new Label(0, j, text1);
                outsheet.addCell(lbl);
                lbl = new Label(1, j, text2);
                outsheet.addCell(lbl);
                lbl = new Label(2, j, text3);
                outsheet.addCell(lbl);
            }
        }
        System.out.println("total: " + found);
        srcbook.close();
        writebook.write();
        writebook.close();
    }

    private static int convertVNChar(int ch1, int ch2) {
        int key = (ch1 << 16) | ch2;
        switch (key) {
            case 0x01020300: return 0x1EB0;
            case 0x01030300: return 0x1EB1;
            case 0x00C20300: return 0x1EA6;
            case 0x00E20300: return 0x1EA7;
            case 0x0CA0300: return 0x1EC0;
            case 0x0EA0300: return 0x1EC1;
            case 0x0490300: return 0x00CC;
            case 0x0690300: return 0x00EC;
            case 0x04F0300: return 0x00D2;
            case 0x06F0300: return 0x00F2;
            case 0x0D40300: return 0x1ED2;
            case 0x0F40300: return 0x1ED3;
            case 0x01A00300: return 0x1EDC;
            case 0x01A10300: return 0x1EDD;
            case 0x01AF0300: return 0x1EEA;
            case 0x01B00300: return 0x1EEB;
            case 0x0590300: return 0x1EF2;
            case 0x0790300: return 0x1EF3;
            case 0x410309: return 0x1EA2;
            case 0x610309: return 0x1EA3;
            case 0x01020309: return 0x1EB2;
            case 0x01030309: return 0x1EB3;
            case 0x0C20309: return 0x1EA8;
            case 0x0E20309: return 0x1EA9;
            case 0x0450309: return 0x1EBA;
            case 0x0650309: return 0x1EBB;
            case 0x0490309: return 0x1EC8;
            case 0x0690309: return 0x1EC9;
            case 0x04F0309: return 0x1ECE;
            case 0x06F0309: return 0x1ECF;
            case 0x0550309: return 0x1EE6;
            case 0x0750309: return 0x1EE7;
            case 0x0590309: return 0x1EF6;
            case 0x0790309: return 0x1EF7;
            case 0x0CA0309: return 0x1EC2;
            case 0x0EA0309: return 0x1EC3;
            case 0x0D40309: return 0x1ED4;
            case 0x0F40309: return 0x1ED5;
            case 0x01A00309: return 0x1EDE;
            case 0x01A10309: return 0x1EDF;
            case 0x01AF0309: return 0x1EEC;
            case 0x01B00309: return 0x1EED;
            case 0x410303: return 0x0C3;
            case 0x610303: return 0x0E3;
            case 0x450303: return 0x1EBC;
            case 0x650303: return 0x1EBD;
            case 0x0490303: return 0x128;
            case 0x0690303: return 0x129;
            case 0x04F0303: return 0x0D5;
            case 0x06F0303: return 0x0F5;
            case 0x0550303: return 0x168;
            case 0x0750303: return 0x169;
            case 0x0590303: return 0x1EF8;
            case 0x0790303: return 0x1EF9;
            case 0x1020303: return 0x1EB4;
            case 0x1030303: return 0x1EB5;
            case 0x0C20303: return 0x1EAA;
            case 0x0E20303: return 0x1EAB;
            case 0x0CA0303: return 0x1EC4;
            case 0x0EA0303: return 0x1EC5;
            case 0x0D40303: return 0x1ED6;
            case 0x0F40303: return 0x1ED7;
            case 0x01A00303: return 0x1EE0;
            case 0x01A10303: return 0x1EE1;
            case 0x01AF0303: return 0x1EEE;
            case 0x01B00303: return 0x1EEF;
            case 0x01020301: return 0x1EAE;
            case 0x01030301: return 0x1EAF;
            case 0x0C20301: return 0x1EA4;
            case 0x0E20301: return 0x1EA5;
            case 0x0CA0301: return 0x1EBE;
            case 0x0EA0301: return 0x1EBF;
            case 0x0D40301: return 0x1ED0;
            case 0x0F40301: return 0x1ED1;
            case 0x01A00301: return 0x1EDA;
            case 0x01A10301: return 0x1EDB;
            case 0x01AF0301: return 0x1EE8;
            case 0x01B00301: return 0x1EE9;
            case 0x0590301: return 0x0DD;
            case 0x0790301: return 0x0FD;
            case 0x0410323: return 0x1EA0;
            case 0x0610323: return 0x1EA1;
            case 0x450323: return 0x1EB8;
            case 0x650323: return 0x1EB9;
            case 0x0490323: return 0x1ECA;
            case 0x0690323: return 0x1ECB;
            case 0x04F0323: return 0x1ECC;
            case 0x06F0323: return 0x1ECD;
            case 0x0550323: return 0x1EE4;
            case 0x0750323: return 0x1EE5;
            case 0x0590323: return 0x1EF4;
            case 0x0790323: return 0x1EF5;
            case 0x1020323: return 0x1EB6;
            case 0x1030323: return 0x1EB7;
            case 0x0C20323: return 0x1EAC;
            case 0x0E20323: return 0x1EAD;
            case 0x0CA0323: return 0x1EC6;
            case 0x0EA0323: return 0x1EC7;
            case 0x0D40323: return 0x1ED8;
            case 0x0F40323: return 0x1ED9;
            case 0x1A00323: return 0x1EE2;
            case 0x1A10323: return 0x1EE3;
            case 0x1AF0323: return 0x1EF0;
            case 0x1B00323: return 0x1EF1;
        }
        return 0;
    }
    
    public static String convertVNString(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            int ch = str.charAt(i);
            if (i < str.length() - 1) {
                int ch2 = str.charAt(i + 1);
                int chnew = convertVNChar(ch & 0xFFFF, ch2 & 0xFFFF);
                if (chnew == 0) {
                    sb.append((char)ch);
                } else {
                    sb.append((char)chnew);
                    i++;
                }
            } else {
                sb.append((char)ch);
            }
        }
        return sb.toString();
    }
}
