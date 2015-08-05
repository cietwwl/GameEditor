package com.pip.game.data.i18n;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pip.game.data.DataObject;
import com.pip.util.Utils;

/**
 * ���ʻ�������������¼����ģʽ��l10n���ݣ�δ�����ַ����ȡ�
 */
public class I18NContext {
    private boolean i18nMode;
    private boolean splitLine;   // ��ֶ����ı���Ҳ֧��|�ָ��Ķ����ı�
    private MessageFile messageFile;
    private Map<String, String> existStrings;
    private Set<String> missingStrings;
    private Map<String, String> missingStringSources;
    private int foundReplaces;
    private DataObject currentTarget;
    
    public I18NContext(boolean i18nMode, boolean splitLine, MessageFile mfile) {
        this.i18nMode = i18nMode;
        this.splitLine = splitLine;
        this.messageFile = mfile;
        existStrings = mfile.getMap();
        missingStrings = new HashSet<String>();
        missingStringSources = new HashMap<String, String>();
    }
    
    public void setCurrentTarget(DataObject obj) {
        currentTarget = obj;
    }
    
    /**
     * ����һ���ַ���������Ƿ�pool���Ƿ��ж�Ӧ�ı��ػ����ݡ�
     * �ɴ�������ı���ÿ�зֱ���Ϊһ�仰��
     * @param str ����ǲ���ģʽ�����ߴ˴�������Ҫ���룬����null�������߲���Ҫ����һ������
     * @return
     */
    public String input(String str, String source) {
        if (str == null) {
            return null;
        }
        if (splitLine && str.contains("\n")) {
            // �Ȳ鿴�ɰ汾�ķ����ļ����ǲ����Ѿ����η������
            if (existStrings.containsKey(str)) {
                if (i18nMode) {
                    return null;
                } else {
                    String ret = existStrings.get(str);
                    foundReplaces++;
                    return ret;
                }
            }
            
            // ��ɶ��зֱ��룬�����ƴ����
            String[] secs = Utils.splitString(str, '\n');
            boolean changed = false;
            for (int i = 0; i < secs.length; i++) {
                String tmp = inputSingleLine(secs[i], source);
                if (tmp != null) {
                    secs[i] = tmp;
                    changed = true;
                }
            }
            if (!changed) {
                return null;
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < secs.length; i++) {
                    if (i > 0) {
                        sb.append("\n");
                    }
                    sb.append(secs[i]);
                }
                return sb.toString();
            }
        } else if (splitLine && str.contains("|")) {
            // �Ȳ鿴�ɰ汾�ķ����ļ����ǲ����Ѿ����η������
            if (existStrings.containsKey(str)) {
                if (i18nMode) {
                    return null;
                } else {
                    String ret = existStrings.get(str);
                    foundReplaces++;
                    return ret;
                }
            }
            
            // ��ɶ��зֱ��룬�����ƴ����
            String[] secs = Utils.splitString(str, '|');
            boolean changed = false;
            for (int i = 0; i < secs.length; i++) {
                String tmp = inputSingleLine(secs[i], source);
                if (tmp != null) {
                    secs[i] = tmp;
                    changed = true;
                }
            }
            if (!changed) {
                return null;
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < secs.length; i++) {
                    if (i > 0) {
                        sb.append("|");
                    }
                    sb.append(secs[i]);
                }
                return sb.toString();
            }
        } else {
            return inputSingleLine(str, source);
        }
    }
    
    /*
     * �������ı���
     * @return ������ı�����Ҫ���룬����null��
     */
    private String inputSingleLine(String str, String source) {
        if (existStrings.containsKey(str)) {
            if (i18nMode) {
                return null;
            } else {
                String ret = existStrings.get(str);
                foundReplaces++;
                if (ret.contains(" ") && "Quest Variable".equals(source)) {
                    I18NError.error(currentTarget, "Whitespace in variable name, before: " + str + ", after: " + ret, null);
                }
                return ret;
            }
        }
        if (!isI18NRelated(str)) {
            return null;
        }
        
        // ����Quest Variable�����⴦�����������������ͨ�ַ���ͬ��������������⣡
        if (missingStrings.contains(str)) {
            String oldSource = missingStringSources.get(str);
            boolean b1 = "Quest Variable".equals(source);
            boolean b2 = "Quest Variable".equals(oldSource);
            if ((b1 && !b2) || (!b1 && b2)) {
                I18NError.error(currentTarget, "Variable name duplicates with something: " + str, null);
            }
        }
        
        // ���һ�����ַ��������Զ������滻
        missingStrings.add(str);
        missingStringSources.put(str, source);
        if (i18nMode) {
            return null;
        }
        foundReplaces++;
        return messageFile.autoTranslate(str, !source.equals("Quest Variable"));
    }
    
    /**
     * ȡ�õ�ǰ��¼������û�б��ػ��Ĺ��ʻ���ص��ַ�����
     */
    public String[][] getMissingStrings() {
        String[] ret = new String[missingStrings.size()];
        missingStrings.toArray(ret);
        String[][] ret2 = new String[ret.length][2];
        for (int i = 0; i < ret.length; i++) {
            ret2[i][0] = ret[i];
            ret2[i][1] = missingStringSources.get(ret[i]);
        }
        return ret2;
    }
    
    /**
     * ����ͳ�Ʊ��档
     */
    public void report() {
        System.out.println("�滻��" + foundReplaces + "���������ı���" + missingStrings.size());
    }
    
    /**
     * �ж��Ƿ���ȡģʽ��
     * @return
     */
    public boolean isI18NMode() {
        return this.i18nMode;
    }
    
    /**
     * ȡ�õײ��xls�ļ���
     * @return
     */
    public MessageFile getMessageFile() {
        return messageFile;
    }
    
    /**
     * �ж�һ���ַ����Ƿ���Ҫ���ʻ���
     * @param str �ַ�������
     * @return ������ַ����а��������ַ�������true��
     */
    public static boolean isI18NRelated(String str) {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch >= 0x4E00 && ch <= 0x9FA5) {
                return true;
            }
        }
        return false;
    }
}
