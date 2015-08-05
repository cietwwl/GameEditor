package com.pip.game.data.i18n;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pip.game.data.DataObject;
import com.pip.util.Utils;

/**
 * 国际化操作环境，记录操作模式，l10n数据，未处理字符串等。
 */
public class I18NContext {
    private boolean i18nMode;
    private boolean splitLine;   // 拆分多行文本，也支持|分隔的多行文本
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
     * 输入一个字符串，检查是否pool中是否有对应的本地化数据。
     * 可处理多行文本，每行分别作为一句话。
     * @param str 如果是查找模式，或者此词条不需要翻译，返回null，调用者不需要做进一步处理。
     * @return
     */
    public String input(String str, String source) {
        if (str == null) {
            return null;
        }
        if (splitLine && str.contains("\n")) {
            // 先查看旧版本的翻译文件里是不是已经整段翻译过了
            if (existStrings.containsKey(str)) {
                if (i18nMode) {
                    return null;
                } else {
                    String ret = existStrings.get(str);
                    foundReplaces++;
                    return ret;
                }
            }
            
            // 拆成多行分别翻译，最后在拼起来
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
            // 先查看旧版本的翻译文件里是不是已经整段翻译过了
            if (existStrings.containsKey(str)) {
                if (i18nMode) {
                    return null;
                } else {
                    String ret = existStrings.get(str);
                    foundReplaces++;
                    return ret;
                }
            }
            
            // 拆成多行分别翻译，最后在拼起来
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
     * 处理单行文本。
     * @return 如果此文本不需要翻译，返回null。
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
        
        // 对于Quest Variable的特殊处理！！！如果变量和普通字符串同名，翻译会有问题！
        if (missingStrings.contains(str)) {
            String oldSource = missingStringSources.get(str);
            boolean b1 = "Quest Variable".equals(source);
            boolean b2 = "Quest Variable".equals(oldSource);
            if ((b1 && !b2) || (!b1 && b2)) {
                I18NError.error(currentTarget, "Variable name duplicates with something: " + str, null);
            }
        }
        
        // 添加一个新字符串，并自动翻译替换
        missingStrings.add(str);
        missingStringSources.put(str, source);
        if (i18nMode) {
            return null;
        }
        foundReplaces++;
        return messageFile.autoTranslate(str, !source.equals("Quest Variable"));
    }
    
    /**
     * 取得当前记录的所有没有本地化的国际化相关地字符串。
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
     * 生成统计报告。
     */
    public void report() {
        System.out.println("替换：" + foundReplaces + "，发现新文本：" + missingStrings.size());
    }
    
    /**
     * 判断是否提取模式。
     * @return
     */
    public boolean isI18NMode() {
        return this.i18nMode;
    }
    
    /**
     * 取得底层的xls文件。
     * @return
     */
    public MessageFile getMessageFile() {
        return messageFile;
    }
    
    /**
     * 判断一个字符串是否需要国际化。
     * @param str 字符串内容
     * @return 如果此字符串中包含中文字符，返回true。
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
