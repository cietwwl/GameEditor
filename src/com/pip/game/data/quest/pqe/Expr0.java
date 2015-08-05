package com.pip.game.data.quest.pqe;

import com.pip.game.data.ProjectData;
import com.pip.game.data.quest.Quest;

public class Expr0 extends SimpleNode {
    public static final int TYPE_NUMBER = 0;
    public static final int TYPE_STRING = 1;
    public static final int TYPE_IDENTIFIER = 2;
    public static final int TYPE_FUNC = 3;

    /**
     * ���͡�
     */
    public int type;
    /**
     * ����/����ֵ��
     */
    public String value;

    public Expr0(int id) {
        super(id);
    }

    public Expr0(Parser p, int id) {
        super(p, id);
    }

    /**
     * ת��Ϊԭʼ�ַ�����ʾ��
     */
    public String toString() {
        switch (type) {
        case TYPE_NUMBER:
        case TYPE_STRING:
        case TYPE_IDENTIFIER:
            return value;
        case TYPE_FUNC:
            return jjtGetChild(0).toString();
        default:
            return "";
        }
    }

    /**
     * ת��Ϊ��Ȼ���Ա�ʾ��
     */
    public String toNatureString() {
        switch (type) {
        case TYPE_NUMBER:
        case TYPE_STRING:
        	return value;
        case TYPE_IDENTIFIER:
        	if (ProjectData.getActiveProject().config.pqeUtils.SYSTEM_VARS_MAP.containsKey(value)) {
        		return ProjectData.getActiveProject().config.pqeUtils.SYSTEM_VARS_MAP.get(value).description;
        	}
            return value;
        case TYPE_FUNC:
            return getFunctionCall().toNatureString();
        default:
            return "";
        }
    }
    
    /**
     * ȡ�����͡�
     */
    public int getType() {
        return type;
    }
    
    /**
     * �������͡�
     */
    public void setType(int t) {
        type = t;
    }
    
    /**
     * ȡ��ֵ��
     */
    public String getValue() {
        return value;
    }
    
    /**
     * ����ֵ��
     */
    public void setValue(String s) {
        value = s;
    }
    
    /**
     * ȡ�ú������á�
     */
    public FunctionCall getFunctionCall() {
        return (FunctionCall)jjtGetChild(0);
    }
    
    /**
     * ���ú������á�
     */
    public void setFunctionCall(FunctionCall fc) {
        fc.parent = this;
        children = new Node[] { fc };
    }
 
    /**
     * ���һ�����ʽ�Ƿ�����﷨����򲻿�ʶ��ı����������������Ĳ��������Ƿ���ȷ��
     * @return ���ر��ʽ��������
     * @exception ������ʽ�޷����룬�׳��쳣������������Ϣ
     */
    public int checkSyntax(String[] localVars) throws PQEException {
        if (type == TYPE_IDENTIFIER) {
            // $��ͷ���������������
            if (value.startsWith("$")) {
                int pos = value.indexOf('.');
                if (pos == -1) {
                    throw new PQEException("������ʽ����" + value);
                }
                try {
                    Integer.parseInt(value.substring(1, pos));
                } catch (Exception e) {
                    throw new PQEException("������ʽ����" + value);
                }
                return 0;
            }
            
            return checkVarName(value, localVars);
        } else if (type == TYPE_FUNC) {
            // ��麯���Ƿ���Ч
            FunctionCall fc = getFunctionCall();
            PQEUtils.SystemFunc func = ProjectData.getActiveProject().config.pqeUtils.SYSTEM_FUNCS_MAP.get(fc.funcName);
            if (func == null) {
                throw new PQEException("ʹ����δ����ĺ���" + fc.funcName);
            }
            
            // ��麯����������
            if (fc.getParamCount() != func.paramType.length) {
                throw new PQEException("������������ȷ" + fc.toString());
            }
            
            // ����������
            for (int i = 0; i < func.paramType.length; i++) {
                int needType = func.paramType[i];
                int realType = fc.getParam(i).checkSyntax(localVars);
                if (needType != -1 && realType != needType) {
                    throw new PQEException("�������Ͳ���ȷ" + fc.toString());
                }
            }
            
            // Set/Inc/Dec�������⴦����һ�������Ǳ������������ǳ���; E_Kill, E_KillWithMate�����ڶ������������Ǳ�����
            if ("Set".equals(fc.funcName) || "Inc".equals(fc.funcName) || "Dec".equals(fc.funcName)) {
                if (fc.getParam(0).getRightExpr() != null || fc.getParam(0).getLeftExpr().type != TYPE_STRING) {
                    throw new PQEException(fc.funcName + "�ĵ�һ�����������ǳ���");
                }
                String varName = PQEUtils.translateStringConstant(fc.getParam(0).getLeftExpr().value);
                checkVarName(varName, localVars);
            }
            if ("E_Kill".equals(fc.funcName) || "E_KillWithMate".equals(fc.funcName)) {
                if (fc.getParam(1).getRightExpr() != null || fc.getParam(1).getLeftExpr().type != TYPE_STRING) {
                    throw new PQEException(fc.funcName + "�ĵڶ������������ǳ���");
                }
                String varName = PQEUtils.translateStringConstant(fc.getParam(1).getLeftExpr().value);
                checkVarName(varName, localVars);
            }
            if (/*"RefreshNPC".equals(fc.funcName) || */"FindNPCByType".equals(fc.funcName)/* || 
                    "RefreshNPCAt".equals(fc.funcName)*/) {
                if (fc.getParam(2).getRightExpr() != null || fc.getParam(2).getLeftExpr().type != TYPE_STRING) {
                    throw new PQEException(fc.funcName + "�ĵ��������������ǳ���");
                }
                String varName = PQEUtils.translateStringConstant(fc.getParam(2).getLeftExpr().value);
                checkVarName(varName, localVars);
            }
            
            // Chat/Message/Question/SendMail/SendChat�������⴦����Ӧ����Ϣ�������ַ��������������ַ���Ҫ���Ϲ淶
            if ("Chat".equals(fc.funcName)) {
                checkStringParamSyntax(fc.getParam(1), localVars);
            } else if ("Message".equals(fc.funcName)) {
                checkStringParamSyntax(fc.getParam(0), localVars);
            } else if ("Question".equals(fc.funcName)) {
                checkStringParamSyntax(fc.getParam(0), localVars);
                checkStringParamSyntax(fc.getParam(1), localVars);
            } else if ("SendMail".equals(fc.funcName)) {
                checkStringParamSyntax(fc.getParam(1), localVars);
                checkStringParamSyntax(fc.getParam(2), localVars);
            } else if ("SendChat".equals(fc.funcName)) {
                checkStringParamSyntax(fc.getParam(1), localVars);
            }
            
            return func.dataType;
        } else if (type == TYPE_NUMBER) {
            return 0;
        } else {
            return 1;
        }
    }
    
    // ���
    private int checkVarName(String varName, String[] localVars) throws PQEException {
        // ��v��ͷ����ת����ľֲ�����
        if (varName.startsWith("v")) {
            return 0;
        }
        
        // ����Ƿ�ϵͳ����
        PQEUtils.SystemVar var = ProjectData.getActiveProject().config.pqeUtils.SYSTEM_VARS_MAP.get(varName);
        if (var != null) {
            return var.dataType;
        }
        
        // ����Ƿ�ֲ�����
        for (int i = 0; i < localVars.length; i++) {
            if (varName.equals(localVars[i])) {
                return 0;
            }
        }
            
        // ���������Ƿ�ȫ�ֱ���
        if (varName.startsWith("__PLAYER_") || varName.startsWith("__TONG_") || 
                varName.startsWith("__FACTION_") || varName.startsWith("__WORLD_") ||
                varName.startsWith("__PARTY_")) {
            return 0;
        }
         
        //��ʱ��ӣ�Ϊ��"hasitem()����xx"���ʽ
        if(varName.equalsIgnoreCase("false")){
            return 0;
        }
        throw new PQEException("ʹ����δ����ı���" + varName);
    }
    
    // ���һ���������ַ��������Ƿ���Ͽͻ��˻�ϸ�ʽ�ַ�����Ҫ��
    private void checkStringParamSyntax(Expression expr, String[] localVars) throws PQEException {
        if (expr.getRightExpr() != null || expr.getLeftExpr().type != Expr0.TYPE_STRING) {
            throw new PQEException("Chat/Message/Question�����Ĳ��������ǳ�����");
        }
        String str = PQEUtils.translateStringConstant(expr.getLeftExpr().value);
        ProjectData.getActiveProject().config.pqeUtils.checkRichTextSyntax(str, localVars, true);
    }

    /**
     * ���һ�����ʽ�Ƿ��ܹ��ɿͻ���ִ�С��������������ʽ�Ƿ���Ч�����ȱ���ͨ��
     * checkSyntax�ļ����ܵ��ô˷�����
     */
    public boolean isClientSupport(String[] localVars) {
        if (type == TYPE_IDENTIFIER) {
            // �����ȫ�ֱ�������Ҫ�ж��Ƿ�ͻ���֧�֣��ֲ������ͻ���ȫ��֧�֣����������������������ǲ�֧��
            if (value.startsWith("$")) {
                return false;
            }
            PQEUtils.SystemVar var = ProjectData.getActiveProject().config.pqeUtils.SYSTEM_VARS_MAP.get(value);
            if (var != null) {
                return var.clientSupport;
            }
            
            // ���������Ƿ�ȫ�ֱ���
            if (value.startsWith("__PLAYER_") || value.startsWith("__TONG_") || 
                    value.startsWith("__FACTION_") || value.startsWith("__WORLD_") ||
                    value.startsWith("__PARTY_")) {
                return false;
            }
            return true;
        } else if (type == TYPE_FUNC) {
            // ��麯���ͻ����Ƿ�֧��
            FunctionCall fc = getFunctionCall();
            PQEUtils.SystemFunc func = ProjectData.getActiveProject().config.pqeUtils.SYSTEM_FUNCS_MAP.get(fc.funcName);
            if (!func.clientSupport) {
                return false;
            }
            
            // ���ÿ�������ͻ����Ƿ�֧��
            for (int i = 0; i < fc.getParamCount(); i++) {
                if (!fc.getParam(i).isClientSupport(localVars)) {
                    return false;
                }
            }
            
            // �����Set/Inc/Dec��������Ҫ���⴦���ͻ���ֻ���޸ľֲ����������ұ��������������ǳ���
            if ("Set".equals(fc.funcName) || "Inc".equals(fc.funcName) || "Dec".equals(fc.funcName)) {
                if (fc.getParam(0).getRightExpr() != null || fc.getParam(0).getLeftExpr().type != TYPE_STRING) {
                    return false;
                }
                String varName = PQEUtils.translateStringConstant(fc.getParam(0).getLeftExpr().value);
                if (varName.startsWith("$") || ProjectData.getActiveProject().config.pqeUtils.SYSTEM_VARS_MAP.containsKey(varName)) {
                    return false;
                }
            }
            
            return true;
        } else if (type == TYPE_STRING) {
            // ����ַ������õ��˷ǿͻ���֧�ֵı�����Ҳ���ڷ���������Χ
            try {
                ProjectData.getActiveProject().config.pqeUtils.checkRichTextSyntax(value, localVars, false);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * �������ʽ�п��ܻ��޸ĵı������֡�
     * @param retList
     */
    public void searchAffectLocalVar(java.util.Set<String> retList) {
        if (type == TYPE_FUNC) {
            // �������в���
            FunctionCall fc = getFunctionCall();
            for (int i = 0; i < fc.getParamCount(); i++) {
                fc.getParam(i).searchAffectLocalVar(retList);
            }
            
            // �����Set/Inc/Dec��������ѵ�һ���������������뷵���б���
            if ("Set".equals(fc.funcName) || "Inc".equals(fc.funcName) || "Dec".equals(fc.funcName)) {
                Expression param0 = fc.getParam(0);
                if (param0.getRightExpr() == null && param0.getLeftExpr().type == TYPE_STRING) {
                    retList.add(PQEUtils.translateStringConstant(param0.getLeftExpr().value));
                }
            }
            
            // �����E_Kill����E_KillWithMate��������ѵڶ����������������뷵���б���
            if ("E_Kill".equals(fc.funcName) || "E_KillWithMate".equals(fc.funcName)) {
                Expression param1 = fc.getParam(1);
                if (param1.getRightExpr() == null && param1.getLeftExpr().type == TYPE_STRING) {
                    retList.add(PQEUtils.translateStringConstant(param1.getLeftExpr().value));
                }
            }
            
            // �����RefreshNPC/RefreshNPCAt/FindNPCByType��������ѵ������������������뷵���б���
            if (/*"RefreshNPC".equals(fc.funcName) || "RefreshNPCAt".equals(fc.funcName) ||*/
                    "FindNPCByType".equals(fc.funcName)) {
                Expression param2 = fc.getParam(2);
                if (param2.getRightExpr() == null && param2.getLeftExpr().type == TYPE_STRING) {
                    retList.add(PQEUtils.translateStringConstant(param2.getLeftExpr().value));
                }
            }
        }
    }

    /**
     * �������ʽ�п�����֮�н�����NPC��
     * @param retList
     */
    public void searchRelateNPC(java.util.Set<Integer> retList) {
        if (type == TYPE_FUNC) {
            // �������в���
            FunctionCall fc = getFunctionCall();
            for (int i = 0; i < fc.getParamCount(); i++) {
                fc.getParam(i).searchRelateNPC(retList);
            }
            
            // �����E_TouchNPC���������ҵ�һ�������ǳ����������ǹ�����NPC
            if ("E_TouchNPC".equals(fc.funcName)) {
                Expression param1 = fc.getParam(0);
                if (param1.getRightExpr() == null && param1.getLeftExpr().type == TYPE_NUMBER) {
                    retList.add(PQEUtils.translateNumberConstant(param1.getLeftExpr().value));
                }
            }
        }
    }
        
    /**
     * �ѱ��ʽ���õ��ľֲ�������ת��Ϊ������
     */
    public void convertVarNameToIndex(String[] localVars) {
        if (type == TYPE_IDENTIFIER) {
            // ����Ǳ������ã��滻������
            value = convertVarNameToIndex(value, localVars);
        } else if (type == TYPE_FUNC) {
            FunctionCall fc = getFunctionCall();
            
            // ���в���ת��
            for (int i = 0; i < fc.getParamCount(); i++) {
                fc.getParam(i).convertVarNameToIndex(localVars);
            }
            
            // Set/Inc/Dec�����ĵ�һ�������Ǳ������ַ�������Ҫ�����滻
            if ("Set".equals(fc.funcName) || "Inc".equals(fc.funcName) || "Dec".equals(fc.funcName) || "E_EscortSuccess".equals(fc.funcName)) {
                Expression param = fc.getParam(0);
                if (param.getRightExpr() == null && param.getLeftExpr().type == Expr0.TYPE_STRING) {
                    String varName = PQEUtils.translateStringConstant(param.getLeftExpr().value);
                    varName = convertVarNameToIndex(varName, localVars);
                    param.getLeftExpr().value = "\"" + PQEUtils.reverseConv(varName) + "\"";
                }
            }
            
            // E_Kill��E_KillWithMate�����ĵڶ��������Ǳ������ַ�������Ҫ�����滻
            if ("E_Kill".equals(fc.funcName) || "E_KillWithMate".equals(fc.funcName)) {
                Expression param = fc.getParam(1);
                if (param.getRightExpr() == null && param.getLeftExpr().type == Expr0.TYPE_STRING) {
                    String varName = PQEUtils.translateStringConstant(param.getLeftExpr().value);
                    varName = convertVarNameToIndex(varName, localVars);
                    param.getLeftExpr().value = "\"" + PQEUtils.reverseConv(varName) + "\"";
                }
            }
            
            // RefreshNPC/RefreshNPCAt/FindNPCByType�����ĵ����������Ǳ������ַ�������Ҫ�����滻
            if (/*"RefreshNPC".equals(fc.funcName) || "RefreshNPCAt".equals(fc.funcName) ||*/
                    "FindNPCByType".equals(fc.funcName)) {
                Expression param = fc.getParam(2);
                if (param.getRightExpr() == null && param.getLeftExpr().type == Expr0.TYPE_STRING) {
                    String varName = PQEUtils.translateStringConstant(param.getLeftExpr().value);
                    varName = convertVarNameToIndex(varName, localVars);
                    param.getLeftExpr().value = "\"" + PQEUtils.reverseConv(varName) + "\"";
                }
            }
            
            // Chat/Message/Question/SendMail/SendChat�������ַ���������Ҫ�滻
            if ("Chat".equals(fc.funcName)) {
                convertVarNameInStringToIndex(fc.getParam(1), localVars);
            } else if ("Message".equals(fc.funcName)) {
                convertVarNameInStringToIndex(fc.getParam(0), localVars);
            } else if ("Question".equals(fc.funcName)) {
                convertVarNameInStringToIndex(fc.getParam(0), localVars);
                convertVarNameInStringToIndex(fc.getParam(1), localVars);
            } else if ("SendMail".equals(fc.funcName)) {
                convertVarNameInStringToIndex(fc.getParam(1), localVars);
                convertVarNameInStringToIndex(fc.getParam(2), localVars);
            } else if ("SendChat".equals(fc.funcName)) {
                convertVarNameInStringToIndex(fc.getParam(1), localVars);
            }
        }
    }
    
    public Expr0 clone(Node parentNode){
        Expr0 ret = (Expr0)super.clone(parentNode);
        ret.type = type;
        ret.value = value;
        
        return ret;
    }
    
    // �ѱ�����ת��Ϊ����������
    private static String convertVarNameToIndex(String varName, String[] localVars) {
        for (int i = 0; i < localVars.length; i++) {
            if (varName.equals(localVars[i])) {
                return "v" + i;
            }
        }
        return varName;
    }

    // ���ַ��������еı�����ת��Ϊ����������
    private void convertVarNameInStringToIndex(Expression expr, String[] localVars) {
        if (expr.getRightExpr() == null && expr.getLeftExpr().type == Expr0.TYPE_STRING) {
            String str = PQEUtils.translateStringConstant(expr.getLeftExpr().value);
            str = PQEUtils.convertRichText(str, localVars);
            expr.getLeftExpr().value = "\"" + PQEUtils.reverseConv(str) + "\"";
        }
    }
    
    /**
     * ������ʽ�����������¼����롣��������������ĳ���¼����п��ܳ�������ô���ʽ���¼�����������
     * �¼���Ӧ������ֵ�������������ĳ������ܲ��������κ�һ���¼�����ô����������EVENT_MASK_CYCLE��
     * ��ʾ��ÿһ��CYCLE�����ܱ�������
     */
    public int getEventMask() {
        if (type != TYPE_FUNC) {
            return PQEUtils.EVENT_MASK_CYCLE;
        }
        String funcName = getFunctionCall().funcName;
        if ("E_Approach".equals(funcName) || "E_EnterMap".equals(funcName)) {
            return PQEUtils.EVENT_MASK_POSITION;
        }
        if ("E_TouchNPC".equals(funcName)) {
            return PQEUtils.EVENT_MASK_TOUCHNPC;
        }
        if ("E_Killed".equals(funcName) || "E_KilledByPlayer".equals(funcName)) {
            return PQEUtils.EVENT_MASK_DIE;
        }
        if ("E_Chat".equals(funcName)) {
            return PQEUtils.EVENT_MASK_CHAT;
        }
        if ("E_OpenUI".equals(funcName)) {
            return PQEUtils.EVENT_MASK_OPENUI;
        }
        if ("E_CloseChat".equals(funcName) || "E_AnswerQuestion".equals(funcName) || "E_CloseMessage".equals(funcName)) {
            return PQEUtils.EVENT_MASK_CLOSECHAT;
        }
        return PQEUtils.EVENT_MASK_CYCLE;
    }

    /**
     * ����ʵ�ֱ��ʽ���ܶ�Ӧ��GTL���롣
     */
    public String toGTL() {
        if (type == TYPE_NUMBER || type == TYPE_STRING) {
            return value;
        } else if (type == TYPE_IDENTIFIER) {
            // ����Ǿֲ�������ֱ����ԭ����д�����ɣ������ȫ�ֱ�������Ҫ��������ת��ΪGetGlobalInt��GetGlobalString�ĵ��á�
            PQEUtils.SystemVar var = ProjectData.getActiveProject().config.pqeUtils.SYSTEM_VARS_MAP.get(value);
            if (var != null) {
                if (var.dataType == 0) {
                    return "GetGlobalInt(\"" + value + "\")";
                } else {
                    return "GetGlobalString(\"" + value + "\")";
                }
            } else {
                return value;
            }
        } else {
            return getFunctionCall().toGTL();
        }
    }

    /**
     * �ѻ�ϸ�ʽ�ַ������õ���NPC���õĳ����ص����ø���һ�¡�
     * @throws Exception
     */
    public void validateMixedText(ProjectData proj) throws Exception {
        if (type == TYPE_FUNC) {
            FunctionCall fc = getFunctionCall();
            for (int i = 0; i < fc.getParamCount(); i++) {
                fc.getParam(i).validateMixedText(proj);
            }
        } else if (type == TYPE_STRING) {
            String msg = PQEUtils.translateStringConstant(value);
            msg = Quest.validateMixedText(proj, msg);
            value = "\"" + PQEUtils.reverseConv(msg) + "\"";
        }
    }
}
