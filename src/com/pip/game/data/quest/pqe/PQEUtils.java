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
	 * 所有比较操作符。
	 */
	public static int[] COMPARE_OPS = new int[] {
		ParserConstants.EQ, ParserConstants.NE, ParserConstants.LT, 
		ParserConstants.LE, ParserConstants.GT, ParserConstants.GE
	};

	/**
	 * 系统变量描述。
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
	 * 所有系统变量的查找表。
	 */
	public HashMap<String, SystemVar> SYSTEM_VARS_MAP = new HashMap<String, SystemVar>();
	
	/**
	 * 所有系统变量的数组。
	 */
	public SystemVar[] SYSTEM_VARS;
	public String[] autoGenImports;
	
	public Map<String, String> TransMap = new HashMap<String, String>();
	public Map<String, String> AITransMap = new HashMap<String, String>();
	public Map<String, String> mapAITransMap = new HashMap<String, String>();
	public Map<String, String> systemTransMap = new HashMap<String, String>();
	
	/**
	 * 系统函数描述。
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
     * 所有系统函数的查找表。
     */
    public HashMap<String, SystemFunc> SYSTEM_FUNCS_MAP = new HashMap<String, SystemFunc>();
    
    /**
     * 所有系统函数的数组。
     */
    public SystemFunc[] SYSTEM_FUNCS;

    /**
     * 从quest_conf.xml里载入系统变量和函数的定义。
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
    
    
    // 事件掩码常量定义
    
    /** 游戏循环事件，每个CYCLE这个事件都会触发。*/
    public static final int EVENT_MASK_CYCLE = 1;
    /** 用户位置改变事件。*/
    public static final int EVENT_MASK_POSITION = 1 << 1;
    /** 用户和NPC对话事件。*/
    public static final int EVENT_MASK_TOUCHNPC = 1 << 2;
    /** 用户死亡事件。*/
    public static final int EVENT_MASK_DIE = 1 << 3;
    /** 用户发送聊天消息事件。*/
    public static final int EVENT_MASK_CHAT = 1 << 4;
    /** 用户打开界面事件。*/
    public static final int EVENT_MASK_OPENUI = 1 << 5;
    /** 用户关闭任务中触发的对话（包括对话、消息或提问）事件。*/
    public static final int EVENT_MASK_CLOSECHAT = 1 << 6;
    
    /**
     * 把字符串常量解释为Java字符串。
     */
    public static String translateStringConstant(String str) {
        // 字符串常量必然开头和结尾都是"
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
     * 解释整型常量。
     */
    public static int translateNumberConstant(String str) {
        if (str.startsWith("0x") || str.startsWith("0X")) {
        	// 16进制
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
     * 把Java字符串转换为表达式中的格式。
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
     * 得到操作符的字符串表现。
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
     * 得到操作符的名称。
     */
    public static String op2nstr(int op) {
        switch (op) {
        case ParserConstants.EQ:
        	return "等于";
        case ParserConstants.NE:
        	return "不等于";
        case ParserConstants.LT:
        	return "小于";
        case ParserConstants.LE:
        	return "跌至";
        case ParserConstants.GT:
        	return "大于";
        case ParserConstants.GE:
        	return "达到";
        default:
        	return "";
        }
    }
    
    /**
     * 检查一个混合格式文本中的变量引用格式是否正确。
     * @param str 字符串
     * @param forServer 这个字符串是否是Server端解释。客户端解释的文本格式要求要严格得多。
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
                    // 变量引用部分结束，检查格式
                    String exprStr = buf.toString();
                    ExpressionList exprList = ExpressionList.fromString(exprStr);
                    if (exprList == null || exprList.getExprCount() > 1) {
                        throw new PQEException("表达式格式错误：" + exprStr);
                    }
                    exprList.checkSyntax(localVars, false);
                    
                    if (!forServer) {
                        // 如果是客户端解释的，还需要额外检查，只允许访问客户端支持的系统变量、局部变量以及GetItemCount函数。
                        Expression expr = exprList.getExpr(0);
                        if (expr.getRightExpr() != null) {
                            throw new PQEException("客户端字符串不支持变量运算：" + exprStr);
                        }
                        Expr0 expr0 = expr.getLeftExpr();
                        if (expr0.type == Expr0.TYPE_IDENTIFIER) {
                            String varName = expr0.value;
                            if (varName.startsWith("_")) {
                                SystemVar sysVar = SYSTEM_VARS_MAP.get(varName);
                                if (sysVar == null || !sysVar.clientSupport) {
                                    throw new PQEException("客户端不支持变量：" + varName);
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
                                    throw new PQEException("客户端不支持变量：" + varName);
                                }
                            }
                        } else if (expr0.type == Expr0.TYPE_FUNC) {
                            FunctionCall fc = expr0.getFunctionCall();
                            if (!fc.funcName.equals("GetItemCount")) {
                                throw new PQEException("客户端字符串只支持GetItemCount函数：" + exprStr);
                            }
                            if (fc.getParam(0).getRightExpr() != null || fc.getParam(0).getLeftExpr().type != Expr0.TYPE_NUMBER) {
                                throw new PQEException("GetItemCount函数必须使用一个数字常量作为参数：" + exprStr);   
                            }
                        } else {
                            throw new PQEException("既然是常量，何必写成变量引用呢？？？？" + exprStr);
                        }
                    }
                    
                    state = 0;
                } else {
                    buf.append(ch);
                }
            }
        }
        if (state == 1) {
            throw new PQEException("未结束的公式引用");
        }
    }

    /**
     * 把一个混合格式文本中所有用到的局部变量名替换为变量索引。
     * @param str 字符串
     */
    public static String convertRichText(String str, String[] localVars) {
        // 构建变量替换表
        HashMap<String, String> varMap = new HashMap<String, String>();
        for (int i = 0; i < localVars.length; i++) {
            varMap.put(localVars[i], "v" + i);
        }
        
        // 扫描字符串查找变量名
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
                    // 变量引用部分结束，替换
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
