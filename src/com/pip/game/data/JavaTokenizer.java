package com.pip.game.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Java���Դʷ���������������һ��Java���������һ������token��
 * @author lighthu
 */
public class JavaTokenizer {
    // ������
    protected Reader in;
    // ��������һ���ַ���-1��ʾû��
    protected int cachedChar = -1;
    
    public JavaTokenizer(Reader r) {
        in = r;
    }
    
    /**
     * ���������ж�ȡһ��token�����������β�����ؿմ���
     * @return
     */
    public String read() throws IOException {
        StringBuilder sb = new StringBuilder();
        int state = 0;   // -1 - ����ѭ����0 - �ڿո��С�1 - ����ͨtoken�С�2 - ����ע��/��
                         // 3 - ������ע�͡�4 - ������ͨע�͡�5 - ע��������*��6 - ���ַ����С�
                         // 7 - ���ַ���������\���š�8 - ���ַ����ʽ�С�9 - ���ַ����ʽ������\����
        
        // ����ǰ�ַ�
        if (cachedChar == -1) {
            cachedChar = in.read();
            if (cachedChar == -1) {
                return sb.toString();
            }
        }
        char ch = (char)cachedChar;
        cachedChar = -1;
        sb.append(ch);
        if (Character.isWhitespace(ch)) {
            // �ո񣬲��ҿո�
            state = 0;
        } else if (ch == '(' || ch == '{' || ch == '}' || ch == ')' || ch == ';' || ch == '*' || ch == ',' || ch == '=' || ch == '+') {
            return sb.toString();
        } else if (ch == '/') {
            state = 2;
        } else if (ch == '"') {
            state = 6;
        } else if (ch == '\'') {
            state = 8;
        } else {
            state = 1;
        }
        
        while (state != -1) {
            int nch = in.read();
            if (nch == -1) {
                break;
            }
            switch (state) {
            case 0:   // �ڿո���
                if (!Character.isWhitespace((char)nch)) {
                    cachedChar = nch;
                    state = -1;
                } else {
                    sb.append((char)nch);
                }
                break;
            case 1:  // ����ͨtoken��
                if (Character.isWhitespace((char)nch) || nch == '(' || nch == '{' ||
                        nch == '}' || nch == ')' || nch == ';' || nch == '/' || nch == '*' ||
                        nch == '"' || nch == '\'' || nch == ',' || nch == '=' || nch == '+') {
                    cachedChar = nch;
                    state = -1;
                } else {
                    sb.append((char)nch);
                }
                break;
            case 2:  // ����ע��/
                if (nch == '/') {
                    sb.append((char)nch);
                    state = 3;
                } else if (nch == '*') {
                    sb.append((char)nch);
                    state = 4;
                } else {
                    cachedChar = nch;
                    state = -1;
                }
                break;
            case 3:  // ����ע����
                if (nch == '\r' || nch == '\n') {
                    cachedChar = nch;
                    state = -1;
                } else {
                    sb.append((char)nch);
                }
                break;
            case 4:  // ����ͨע����
                if (nch == '*') {
                    sb.append((char)nch);
                    state = 5;
                } else {
                    sb.append((char)nch);
                }
                break;
            case 5:  // ����ͨע��������*
                if (nch == '/') {
                    sb.append((char)nch);
                    state = -1;
                } else if (nch != '*') {
                    sb.append((char)nch);
                    state = 4;
                }
                break;
            case 6:  // ���ַ�����
                if (nch == '"') {
                    sb.append((char)nch);
                    state = -1;
                } else if (nch == '\\') {
                    sb.append((char)nch);
                    state = 7;
                } else {
                    sb.append((char)nch);
                }
                break;
            case 7:  // �ַ���������ת���ַ�\
                sb.append((char)nch);
                state = 6;
                break;
            case 8:  // ���ַ����ʽ��
                if (nch == '\'') {
                    sb.append((char)nch);
                    state = -1;
                } else if (nch == '\\') {
                    sb.append((char)nch);
                    state = 9;
                } else {
                    sb.append((char)nch);
                }
                break;
            case 9:  // �ַ����ʽ������ת���ַ�\
                sb.append((char)nch);
                state = 8;
                break;
            }
        }
        return sb.toString();
    }
    
    /**
     * ���������н���������token��
     * @param r
     * @return
     * @throws IOException
     */
    public static List<String> parse(Reader r) throws IOException {
        JavaTokenizer tokens = new JavaTokenizer(r);
        String token;
        List<String> ret = new ArrayList<String>();
        while ((token = tokens.read()).length() != 0) {
            ret.add(token);
        }
        return ret;
    }
    
    /**
     * ���ļ��н��������е�token��
     * @param f �ļ�
     * @param encoding ���뷽ʽ
     * @return 
     * @throws IOException
     */
    public static List<String> parse(File f, String encoding) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis, encoding);
            BufferedReader br = new BufferedReader(isr);
            return parse(br);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }
    
    /**
     * ��token��д�뵽Դ�ļ��С�
     * @param f �ļ�
     * @param encoding ���뷽ʽ
     * @param tokens
     * @throws IOException
     */
    public static void save(File f, String encoding, List<String> tokens) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
            BufferedWriter bw = new BufferedWriter(osw);
            for (String s : tokens) {
                bw.write(s);
            }
            bw.flush();
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }
}
