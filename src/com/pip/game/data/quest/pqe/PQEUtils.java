package com.pip.game.data.quest.pqe;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import com.pip.game.data.ProjectData;
import com.pip.game.editor.quest.expr.IExpr;
import com.pip.util.Utils;

public class PQEUtils {
	/**
	 * ���бȽϲ�������
	 */
	public static int[] COMPARE_OPS = new int[] {
		ParserConstants.EQ, ParserConstants.NE, ParserConstants.LT, 
		ParserConstants.LE, ParserConstants.GT, ParserConstants.GE
	};

	/**
	 * ϵͳ����������
	 * @author lighthu
	 */
	public static class SystemVar {
	    public String name;
	    public int dataType;
	    public boolean clientSupport;
	    public String description;
	    
	    public SystemVar(String name, int dataType, boolean client, String desc) {
	        this.name = name;
	        this.dataType = dataType;
	        this.clientSupport = client;
	        this.description = desc;
	    }
	}
	
	/**
	 * ����ϵͳ�����Ĳ��ұ�
	 */
	public HashMap<String, SystemVar> SYSTEM_VARS_MAP = new HashMap<String, SystemVar>();
	
	/**
	 * ����ϵͳ���������顣
	 */
	public SystemVar[] SYSTEM_VARS;
	public String[] autoGenImports;
	
	public Map<String, String> TransMap = new HashMap<String, String>();
	public Map<String, String> AITransMap = new HashMap<String, String>();
	public Map<String, String> mapAITransMap = new HashMap<String, String>();
	public Map<String, String> systemTransMap = new HashMap<String, String>();
	
	/**
	 * ϵͳ����������
	 */
	public static class SystemFunc {
        public String name;
        public int dataType;
        public boolean clientSupport;
        public int[] paramType;
        public String description;
        
        public SystemFunc(String name, int dataType, boolean client, int[] paramType, String desc) {
            this.name = name;
            this.dataType = dataType;
            this.clientSupport = client;
            this.paramType = paramType;
            this.description = desc;
        }
    }
	
	/**
     * ����ϵͳ�����Ĳ��ұ�
     */
    public HashMap<String, SystemFunc> SYSTEM_FUNCS_MAP = new HashMap<String, SystemFunc>();
    
    /**
     * ����ϵͳ���������顣
     */
    public SystemFunc[] SYSTEM_FUNCS;

    /**
     * ��quest_conf.xml������ϵͳ�����ͺ����Ķ��塣
     * @param baseDir
     * @param classLoader
     * @throws Exception
     */
    public PQEUtils(File baseDir, ClassLoader classLoader) throws Exception {
        Document doc = Utils.loadDOM(new File(baseDir, "quest_conf.xml"));
        List list = doc.getRootElement().getChildren("SYSTEM_VARS");
        list = ((Element )list.get(0)).getChildren("SYSTEM_VAR");
                
        SYSTEM_VARS = new SystemVar[list.size()];
        for(int i=0; i<SYSTEM_VARS.length; i++) {
            Element elem = (Element)list.get(i); 
            String name = elem.getAttributeValue("name");
            int dataType = Integer.parseInt(elem.getAttributeValue("dataType"));
            boolean client = "1".equals(elem.getAttributeValue("client")) ? true : false;
            String desc = elem.getAttributeValue("desc");
            
            SYSTEM_VARS[i] = new SystemVar(name, dataType, client, desc);
            SYSTEM_VARS_MAP.put(name, SYSTEM_VARS[i]);
            String transName = elem.getAttributeValue("transName");
            if (transName != null && transName.length() > 0) {
                TransMap.put(name, transName);
            }
            String aiTransName = elem.getChildText("creature_ai_impl");
            if (aiTransName != null && aiTransName.length() > 0) {
                AITransMap.put(name, aiTransName);
            }
            String mapAiTransName = elem.getChildText("map_ai_impl");
            if (mapAiTransName != null && mapAiTransName.length() > 0) {
                mapAITransMap.put(name, mapAiTransName);
            }
            String systemTransName = elem.getChildText("system_impl");
            if (systemTransName != null && systemTransName.length() > 0) {
                systemTransMap.put(name, systemTransName);
            }
        }        
        list = doc.getRootElement().getChildren("SystemFuncs");
        list = ((Element )list.get(0)).getChildren("SystemFunc");
        
        SYSTEM_FUNCS = new SystemFunc[list.size()];
        for(int i=0; i<SYSTEM_FUNCS.length; i++) {
            try {
                Element elem = (Element)list.get(i); 
                String name = elem.getAttributeValue("name");
                int dataType = Integer.parseInt(elem.getAttributeValue("dataType"));
                boolean client = "1".equals(elem.getAttributeValue("client")) ? true : false;
                
                String params = elem.getAttributeValue("paramsType");
                int[] paramsType = null;
                if("".equals(params)) {
                    paramsType = new int[]{};
                } else {
                    String[] paramsTypes = elem.getAttributeValue("paramsType").split(",");
                    paramsType = new int[paramsTypes.length];
                    for(int j=0; j<paramsTypes.length; j++) {
                        paramsType[j] = Integer.parseInt(paramsTypes[j].trim());
                    }
                }
                
                String desc = elem.getAttributeValue("desc");
                
                SYSTEM_FUNCS[i] = new SystemFunc(name, dataType, client, paramsType, desc);
                SYSTEM_FUNCS_MAP.put(name, SYSTEM_FUNCS[i]);
                
                String transName = elem.getAttributeValue("transName");
                if (transName != null && transName.length() > 0) {
                    TransMap.put(name, transName);
                }
                String aiTransName = elem.getChildText("creature_ai_impl");
                if (aiTransName != null && aiTransName.length() > 0) {
                    AITransMap.put(name, aiTransName);
                }
                String mapAiTransName = elem.getChildText("map_ai_impl");
                if (mapAiTransName != null && mapAiTransName.length() > 0) {
                    mapAITransMap.put(name, mapAiTransName);
                }
                String systemTransName = elem.getChildText("system_impl");
                if (systemTransName != null && systemTransName.length() > 0) {
                    systemTransMap.put(name, systemTransName);
                }
            } catch(Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        
        list = doc.getRootElement().getChildren("AutoGenImports");
        list = ((Element )list.get(0)).getChildren("AutoGenImport");
        autoGenImports = new String[list.size()];
        for(int i=0; i<autoGenImports.length; i++) {
            Element elem2 = (Element)list.get(i); 
            String name = elem2.getAttributeValue("name");
            autoGenImports[i] = name;
        }
    }
    
    
    // �¼����볣������
    
    /** ��Ϸѭ���¼���ÿ��CYCLE����¼����ᴥ����*/
    public static final int EVENT_MASK_CYCLE = 1;
    /** �û�λ�øı��¼���*/
    public static final int EVENT_MASK_POSITION = 1 << 1;
    /** �û���NPC�Ի��¼���*/
    public static final int EVENT_MASK_TOUCHNPC = 1 << 2;
    /** �û������¼���*/
    public static final int EVENT_MASK_DIE = 1 << 3;
    /** �û�����������Ϣ�¼���*/
    public static final int EVENT_MASK_CHAT = 1 << 4;
    /** �û��򿪽����¼���*/
    public static final int EVENT_MASK_OPENUI = 1 << 5;
    /** �û��ر������д����ĶԻ��������Ի�����Ϣ�����ʣ��¼���*/
    public static final int EVENT_MASK_CLOSECHAT = 1 << 6;
    
    /**
     * ���ַ�����������ΪJava�ַ�����
     */
    public static String translateStringConstant(String str) {
        // �ַ���������Ȼ��ͷ�ͽ�β����"
        StringBuffer buf = new StringBuffer();
        char[] data = str.toCharArray();
        for (int i = 1; i < data.length - 1; i++) {
            char ch = data[i];
            if (ch == '\\') {
                switch (data[i + 1]) {
                case 'n':
                    buf.append("\n");
                    break;
                case 'r':
                    buf.append("\r");
                    break;
                case 't':
                    buf.append("\t");
                    break;
                default:
                    buf.append(data[i + 1]);
                    break;
                }
                i++;
            } else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    /**
     * �������ͳ�����
     */
    public static int translateNumberConstant(String str) {
        if (str.startsWith("0x") || str.startsWith("0X")) {
        	// 16����
        	str = str.substring(2);
        	if (str.length() < 8) {
        		return Integer.parseInt(str, 16);
        	} else {
        		int low = Integer.parseInt(str.substring(1), 16);
        		int high = Integer.parseInt(str.substring(0, 1), 16);
        		return (high << 28) | low;
        	}
        } else {
            return Integer.parseInt(str);
        }
    }

    /**
     * ��Java�ַ���ת��Ϊ���ʽ�еĸ�ʽ��
     */
    public static String reverseConv(String msg) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < msg.length(); i++) {
            switch (msg.charAt(i)) {
            case '\n':
                buf.append("\\n");
                break;
            case '\r':
                buf.append("\\r");
                break;
            case '\t':
                buf.append("\\t");
                break;
            case '"':
                buf.append("\\\"");
                break;
            case '\\':
                buf.append("\\\\");
                break;
            default:
                buf.append(msg.charAt(i));
                break;
            }
        }
        return buf.toString();
    }

    /**
     * �õ����������ַ������֡�
     * @param op
     * @return
     */
    public static String op2str(int op) {
        switch (op) {
        case ParserConstants.EQ:
        	return "==";
        case ParserConstants.NE:
        	return "!=";
        case ParserConstants.LT:
        	return "<";
        case ParserConstants.LE:
        	return "<=";
        case ParserConstants.GT:
        	return ">";
        case ParserConstants.GE:
        	return ">=";
        default:
        	return "";
        }
    }
    
    /**
     * �õ������������ơ�
     */
    public static String op2nstr(int op) {
        switch (op) {
        case ParserConstants.EQ:
        	return "����";
        case ParserConstants.NE:
        	return "������";
        case ParserConstants.LT:
        	return "С��";
        case ParserConstants.LE:
        	return "����";
        case ParserConstants.GT:
        	return "����";
        case ParserConstants.GE:
        	return "�ﵽ";
        default:
        	return "";
        }
    }
    
    /**
     * ���һ����ϸ�ʽ�ı��еı������ø�ʽ�Ƿ���ȷ��
     * @param str �ַ���
     * @param forServer ����ַ����Ƿ���Server�˽��͡��ͻ��˽��͵��ı���ʽҪ��Ҫ�ϸ�öࡣ
     * @throws PQEException
     */
    public void checkRichTextSyntax(String str, String[] localVars, boolean forServer) throws PQEException {
        char[] arr = str.toCharArray();
        int count = arr.length;
        int state = 0;
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < count; i++) {
            char ch = arr[i];
            if (state == 0) {
                if (ch == '$' && i < count - 1 && arr[i + 1] == '{') {
                    i++;
                    state = 1;
                    buf.setLength(0);
                }
            } else if (state == 1) {
                if (ch == '}') {
                    // �������ò��ֽ���������ʽ
                    String exprStr = buf.toString();
                    ExpressionList exprList = ExpressionList.fromString(exprStr);
                    if (exprList == null || exprList.getExprCount() > 1) {
                        throw new PQEException("���ʽ��ʽ����" + exprStr);
                    }
                    exprList.checkSyntax(localVars, false);
                    
                    if (!forServer) {
                        // ����ǿͻ��˽��͵ģ�����Ҫ�����飬ֻ������ʿͻ���֧�ֵ�ϵͳ�������ֲ������Լ�GetItemCount������
                        Expression expr = exprList.getExpr(0);
                        if (expr.getRightExpr() != null) {
                            throw new PQEException("�ͻ����ַ�����֧�ֱ������㣺" + exprStr);
                        }
                        Expr0 expr0 = expr.getLeftExpr();
                        if (expr0.type == Expr0.TYPE_IDENTIFIER) {
                            String varName = expr0.value;
                            if (varName.startsWith("_")) {
                                SystemVar sysVar = SYSTEM_VARS_MAP.get(varName);
                                if (sysVar == null || !sysVar.clientSupport) {
                                    throw new PQEException("�ͻ��˲�֧�ֱ�����" + varName);
                                }
                            } else {
                                boolean found = false;
                                for (String lv : localVars) {
                                    if (lv.equals(varName)) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    throw new PQEException("�ͻ��˲�֧�ֱ�����" + varName);
                                }
                            }
                        } else if (expr0.type == Expr0.TYPE_FUNC) {
                            FunctionCall fc = expr0.getFunctionCall();
                            if (!fc.funcName.equals("GetItemCount")) {
                                throw new PQEException("�ͻ����ַ���ֻ֧��GetItemCount������" + exprStr);
                            }
                            if (fc.getParam(0).getRightExpr() != null || fc.getParam(0).getLeftExpr().type != Expr0.TYPE_NUMBER) {
                                throw new PQEException("GetItemCount��������ʹ��һ�����ֳ�����Ϊ������" + exprStr);   
                            }
                        } else {
                            throw new PQEException("��Ȼ�ǳ������α�д�ɱ��������أ�������" + exprStr);
                        }
                    }
                    
                    state = 0;
                } else {
                    buf.append(ch);
                }
            }
        }
        if (state == 1) {
            throw new PQEException("δ�����Ĺ�ʽ����");
        }
    }

    /**
     * ��һ����ϸ�ʽ�ı��������õ��ľֲ��������滻Ϊ����������
     * @param str �ַ���
     */
    public static String convertRichText(String str, String[] localVars) {
        // ���������滻��
        HashMap<String, String> varMap = new HashMap<String, String>();
        for (int i = 0; i < localVars.length; i++) {
            varMap.put(localVars[i], "v" + i);
        }
        
        // ɨ���ַ������ұ�����
        char[] arr = str.toCharArray();
        int count = arr.length;
        int state = 0;
        StringBuffer buf = new StringBuffer();
        StringBuffer retBuf = new StringBuffer();
        for (int i = 0; i < count; i++) {
            char ch = arr[i];
            if (state == 0) {
                if (ch == '$' && i < count - 1 && arr[i + 1] == '{') {
                    i++;
                    state = 1;
                    buf.setLength(0);
                } else {
                    retBuf.append(ch);
                }
            } else if (state == 1) {
                if (ch == '}') {
                    // �������ò��ֽ������滻
                    String exprStr = buf.toString().trim();
                    if (varMap.containsKey(exprStr)) {
                        retBuf.append("${" + varMap.get(exprStr) + "}");
                    } else {
                        retBuf.append("${" + exprStr + "}");
                    }
                    state = 0;
                } else {
                    buf.append(ch);
                }
            }
        }
        if (state == 1) {
            retBuf.append("${");
            retBuf.append(buf.toString());
        }
        return retBuf.toString();
    }
}
