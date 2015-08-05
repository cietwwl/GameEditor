package com.pip.game.data.quest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.i18n.I18NContext;
import com.pip.game.data.i18n.I18NUtils;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.ExpressionList;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.gtl.compiler.GTLCompiler;
import com.pip.util.Utils;

/**
 * ������ϸ���ݡ�
 * @author lighthu
 */
public class QuestInfo extends DataObject{
    public Quest owner;
    public String ownerName;
    /**
     * ����ı�����
     */
    public List<QuestVariable> variables = new ArrayList<QuestVariable>();
    /**
     * ����Ĵ�������
     */
    public List<QuestTrigger> triggers = new ArrayList<QuestTrigger>();
    /**
     * �ͻ���ִ�е�ETF�ļ����ݣ�GZIPѹ�����������ڷ�����ģʽ��
     */
    protected byte[] clientETF;
    /**
     * �ͻ��˿����޸ĵı����ı�־�������Ӧ�ú�variablesһ������true��ʾ�ͻ��˿����޸����������
     */
    protected boolean[] clientVarFlags;
    /**
     * �ͻ��������õ���ϵͳ�������ñ�
     */
    public static final String GTL_SYS_FUNCS_CONFIG = 
        "short Random()\n" +
        "int GetGlobalInt(String varName)\n" + 
        "String GetGlobalString(String varName)\n" +
        "boolean PQE_HasTask(int taskID)\n" +
        "boolean PQE_CanFinish()\n" +
        "void PQE_Chat(int npcID, String message, int notifyID)\n" +
        "void PQE_Message(String message, int timout, int notifyID)\n" +
        "void PQE_Question(String message, String options, int notifyID)\n" +
        "void PQE_OpenUI(String uiName)\n" +
        "void PQE_Flash(int frames)\n" +
        "void PQE_GotoMap(int mapID, int x, int y)\n" +
        "void PQE_Logout()\n" +
        "int PQE_GetItemCount(int itemID)\n" +
        "boolean PQE_HasItem(int itemID, int count)\n" +
        "boolean PQE_E_Approach(int mapID, int x, int y, int distance)\n" +
        "boolean PQE_E_EnterMap(int mapID)\n" +
        "boolean PQE_E_TouchNPC(int npcID)\n" +
        "boolean PQE_E_AnswerQuestion(int notifyID, int optionID)\n" +
        "boolean PQE_E_CloseChat(int notifyID)\n" +
        "boolean PQE_E_CloseMessage(int notifyID)\n" +
        "void PQE_Listen(int mask)\n" +
        "int PQE_GetEventMask()\n" +
        "int PQE_HasEqu(int equipId)";
    
    
    public QuestInfo(Quest owner) {
        this.owner = owner;
    }
    
    public void load() throws Exception {
        String text = Utils.loadText(owner.source);
        loadFromText(text);
        
        // ����Ƿ�����ģʽ������ʱ�Ͱ��������������õľֲ�������ת��Ϊ����
        if (owner.owner.serverMode) {
            String[] localVars = getVariables();
            
            // ��ת��֮ǰ����ͳ�����пͻ���׼���޸ĵı���
            if (clientVarFlags == null) {
                findClientVars();
            }
            
            // ��������
            owner.finishCondition = ExpressionList.convertVarNameToIndex(owner.finishCondition, localVars);
            
            // ����
            owner.description = PQEUtils.convertRichText(owner.description, localVars);
            owner.preDescription = PQEUtils.convertRichText(owner.preDescription, localVars);
            owner.postDescription = PQEUtils.convertRichText(owner.postDescription, localVars);
            owner.unfinishDescription = PQEUtils.convertRichText(owner.unfinishDescription, localVars);
            
            // ����Ŀ��
            for (QuestTarget target : owner.targets) {
                target.condition = ExpressionList.convertVarNameToIndex(target.condition, localVars);
                target.description = PQEUtils.convertRichText(target.description, localVars);
                target.hint = PQEUtils.convertRichText(target.hint, localVars);
            }
            
            // ���д�����
            for (int i = 0; i < triggers.size(); i++) {
                QuestTrigger trigger = (QuestTrigger)triggers.get(i);
                if (trigger.serverCondition == null) {
                    trigger.condition = ExpressionList.convertVarNameToIndex(trigger.condition, localVars);
                } else {
                    trigger.condition = trigger.serverCondition;
                }
                if (trigger.serverAction == null) {
                    trigger.action = ExpressionList.convertVarNameToIndex(trigger.action, localVars);   
                } else {
                    trigger.action = trigger.serverAction;
                }
            }
        }
    }
    
    /*
     * �ҳ����п��ܱ��ͻ����޸ĵı�������������Ҫʹ�������Ϣ��
     */
    private void findClientVars() {
        String[] localVars = getVariables();
        clientVarFlags = new boolean[localVars.length];
        Set<String> clientVars = new HashSet<String>();
        for (QuestTrigger trigger : triggers) {
            ExpressionList expr1 = ExpressionList.fromString(trigger.condition);
            ExpressionList expr2 = ExpressionList.fromString(trigger.action);
            if (expr1.isClientSupport(localVars) && expr2.isClientSupport(localVars)) {
                expr1.searchAffectLocalVar(clientVars);
                expr2.searchAffectLocalVar(clientVars);
            }
        }
        for (String var : clientVars) {
            for (int i = 0; i < localVars.length; i++) {
                if (var.equals(localVars[i])) {
                    clientVarFlags[i] = true;
                    break;
                }
            }
        }
    }
    
    public Element save() {
        // ���ɷ�������Ҫ�Ļ�������
        String[] localVars = getVariables();
        findClientVars();
        for (QuestTrigger trigger : triggers) {
            trigger.serverCondition = ExpressionList.convertVarNameToIndex(trigger.condition, localVars);
            trigger.serverAction = ExpressionList.convertVarNameToIndex(trigger.action, localVars);
        }
        
        try {
            Utils.saveText(saveToText(), owner.source);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    public Element saveToXML() throws Exception {
        Element root = new Element("questinfo");
        for (QuestVariable var : variables) {
            root.addContent(var.save());
        }
        for (QuestTrigger trigger : triggers) {
            root.addContent(trigger.save());
        }
        
        if (clientVarFlags != null) {
            StringBuilder sb = new StringBuilder();
            String[] localVars = getVariables();
            for (int i = 0; i < clientVarFlags.length; i++) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                if (clientVarFlags[i]) {
                    sb.append(localVars[i]);
                }
            }
            Element elem = new Element("clientvars");
            elem.setText(sb.toString());
            root.addContent(elem);
        }
        return root;
    }
    
    public void loadFromXML(Element element) throws Exception {
        variables.clear();
        triggers.clear();
        List list = element.getChildren("variable");
        for (Object obj : list) {
            QuestVariable newVar = new QuestVariable();
            newVar.load((Element)obj);
            variables.add(newVar);
        }
        list = element.getChildren("trigger");
        for (Object obj : list) {
            QuestTrigger newTrigger = new QuestTrigger();
            newTrigger.load((Element)obj);
            triggers.add(newTrigger);
        }
        
        Element elem = element.getChild("clientvars");
        if (elem != null) {
            String[] secs = elem.getTextTrim().split(",");
            String[] localVars = getVariables();
            clientVarFlags = new boolean[localVars.length];
            for (String sec : secs) {
                for (int i = 0; i < localVars.length; i++) {
                    if (localVars[i].equals(sec)) {
                        clientVarFlags[i] = true;
                        break;
                    }
                }
            }
        }
    }
            
    public byte[] toByteArray() throws Exception {  
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
        
        String text = saveToText();
        Utils.saveText(text, bos);
        
        return bos.toByteArray(); 
    }
        
    public String saveToText() {
        
        StringBuffer sb = new StringBuffer();
        
        for (int i=0; i<variables.size(); i++) {
            QuestVariable var = variables.get(i);
            if(i == 0) {
               sb.append("var: ");
            } else {
                sb.append(",");
            }
            
            sb.append(var.name);
            
        }
        if(variables.size() > 0) {
            sb.append("\n"); 
        }
        
        for (int i=0; i<triggers.size(); i++) {            
            QuestTrigger trigger = triggers.get(i);
            
            sb.append("[");
            sb.append("\n");
            
            sb.append("condition: ");
            sb.append(trigger.condition);
            sb.append("\n");
            sb.append("action: ");
            sb.append(trigger.action);
            sb.append("\n");
            
            sb.append("]");
            
            if(i < triggers.size() - 1) {
                sb.append("\n"); 
            }            
        }
        
        return sb.toString();
        
    }
    
    public String getOneLineString() {
        return saveToText().replace("\r\n", "").replace("\n", "");
    }
        
    public void loadFromText(String text) {
        text.replaceAll("\\\\r\\\\n", "\\\\n");
        
        variables.clear();
        triggers.clear();
        
        String triggerText = text;
        
        //��ȡ����
        String[] lines = triggerText.split("\n");
        for(int i=0; i<lines.length; i++) {
            if(lines[i].startsWith("var:")) {
                String[] vars = lines[i].substring("var:".length()).trim().split(",");
                for(int k=0; k<vars.length; k++) {
                    QuestVariable newVar = new QuestVariable();
                    newVar.name = vars[k].trim();
                    variables.add(newVar);
                }                
            }
        }
        
        //��ȡtrigger
        int startTag = triggerText.indexOf('[');
        int endTag = triggerText.indexOf(']');
        
        Vector<String> triggerDescs = new Vector<String>();
        while(startTag >= 0 && endTag >= 0) {
            String triggerStr = triggerText.substring(startTag + 2, endTag - 1);
            
            triggerDescs.add(triggerStr);
            
            triggerText = triggerText.substring(endTag + 1);
            startTag = triggerText.indexOf('[');
            endTag = triggerText.indexOf(']');
        }
        
        for(int i=0; i<triggerDescs.size(); i++) {
            String desc = triggerDescs.get(i);
            lines = desc.split("\n");
            
            QuestTrigger newTrigger = new QuestTrigger();
            
            for(int j=0; j<lines.length; j++) {
                if(lines[j].trim().startsWith("condition:")) {
                    newTrigger.condition = lines[j].trim().substring("condition:".length()).trim();
                    
                } else if(lines[j].trim().startsWith("action:")) {
                    newTrigger.action = lines[j].trim().substring("action:".length()).trim();
                }
            }
            
            triggers.add(newTrigger);
        }
        
    }

    /**
     * ȡ������������������ơ�
     * @return
     */
    public String[] getVariables() {
        String[] ret = new String[variables.size()];
        for (int i = 0; i < variables.size(); i++) {
            ret[i] = variables.get(i).name;
        }
        return ret;
    }
    
    /**
     * ȡ�ô�������������Ҫ�ɷ�����ִ�еĴ�������
     * @return
     */
    public List<QuestTrigger> getServerTriggers() {
        List<QuestTrigger> ret = new ArrayList<QuestTrigger>();
        String[] localVars = getVariables();
        for (QuestTrigger trigger : triggers) {
            // ��ʽ���
            ExpressionList expr1 = ExpressionList.fromString(trigger.condition);
            if (expr1 == null) {
                continue;
            }
            try {
                expr1.checkSyntax(localVars, true);
            } catch (Exception e) {
                continue;
            }
            ExpressionList expr2 = ExpressionList.fromString(trigger.action);
            if (expr2 == null) {
                continue;
            }
            try {
                expr2.checkSyntax(localVars, false);
            } catch (Exception e) {
                continue;
            }
            
            // ��������Ͷ��������κ�һ���ͻ��˲�֧�֣����Ƿ�����������
            if (!expr1.isClientSupport(localVars) || !expr2.isClientSupport(localVars)) {
                ret.add(trigger);
            }
        }
        return ret;
    }

    /**
     * ȡ�ô����������п����ɿͻ���ִ�еĴ�������
     * @return
     */
    protected List<QuestTrigger> getClientTriggers() {
        List<QuestTrigger> ret = new ArrayList<QuestTrigger>();
        String[] localVars = getVariables();
        for (QuestTrigger trigger : triggers) {
            // ��ʽ���
            ExpressionList expr1 = ExpressionList.fromString(trigger.condition);
            if (expr1 == null) {
                continue;
            }
            try {
                expr1.checkSyntax(localVars, true);
            } catch (Exception e) {
                continue;
            }
            ExpressionList expr2 = ExpressionList.fromString(trigger.action);
            if (expr2 == null) {
                continue;
            }
            try {
                expr2.checkSyntax(localVars, false);
            } catch (Exception e) {
                continue;
            }
            
            // ��������Ͷ����������б��ʽ�ͻ��˶�֧�֣����ǿͻ��˴�����
            if (expr1.isClientSupport(localVars) && expr2.isClientSupport(localVars)) {
                ret.add(trigger);
            }
        }
        return ret;
    }
    
    /**
     * �ѿͻ����ܹ�ִ�е��߼����������ת��Ϊһ��GTL�ű���ÿ����������һ��������ÿ�������������д��һ���ص�
     * �������ȴ��ͻ��˵������ж�����Ŀ���Ƿ��ɡ�
     * @return GTL�ű��ַ���
     */
    public String generateClientGTL() {
        StringWriter buf = new StringWriter();
        PrintWriter out = new PrintWriter(buf);
        
        // �ļ�ͷ
        out.println("VERSION 4;");
        out.println("ID " + owner.id +";");
        out.println("ATTRIBUTE 32;");
        out.println("NAME \"quest\";");
        out.println("DESCRIPTION \"\";");
        out.println();
        
        // �������ݱ���
        out.println("DATA {");
        for (int i = 0; i < variables.size(); i++) {
            out.println("    int v" + i + ";");
        }
        out.println("}");
        out.println();

        // Ϊÿ������Ŀ��������������һ���ص�������������target0
        for (int i = 0; i < owner.targets.size(); i++) {
            out.println("boolean FUNCTION CALLBACK target" + i + "() {");
            out.println("    int temp;");
            out.print("    temp = ");
            ExpressionList exprList = ExpressionList.fromString(owner.targets.get(i).condition);
            String stat = exprList.getExpr(0).toGTL();
            out.print(stat);
            if (!stat.endsWith("}")) {
                out.println(";");
            }
            out.println("    return temp;");
            out.println("}");
            out.println();
        }
        
        // ����interact()�ص��������жϺ�һ��NPC�Ƿ��й���
        java.util.Set<Integer> relateNPCs = new java.util.HashSet<Integer>();
        for (int i = 0; i < triggers.size(); i++) {
            ExpressionList exprList = ExpressionList.fromString(triggers.get(i).condition);
            exprList.searchRelateNPC(relateNPCs);
            exprList = ExpressionList.fromString(triggers.get(i).action);
            exprList.searchRelateNPC(relateNPCs);
        }
        out.println("int FUNCTION CALLBACK interact(int npc) {");
        if (relateNPCs.size() == 0) {
            out.println("    return 0;");
        } else {
            out.println("    switch (npc) {");
            for (int npcID : relateNPCs) {
                out.println("    case " + npcID + ":");
            }
            out.println("        return 1;");
            out.println("    default:");
            out.println("        return 0;");
            out.println("    }");
        }
        out.println("}");
        out.println();
        
        // Ϊÿ������������һ���ص�������������trigger0
        List<QuestTrigger> clientTriggers = getClientTriggers();
        List<Integer> triggerEventMasks = new ArrayList<Integer>();
        int eventMask = 0;
        for (int i = 0; i < clientTriggers.size(); i++) {
            out.println("void FUNCTION trigger" + i + "() {");
            out.println("    int temp;");
            
            // ������������·����
            ExpressionList exprList = ExpressionList.fromString(clientTriggers.get(i).condition);
            for (int j = 0; j < exprList.getExprCount(); j++) {
                out.print("    temp = ");
                
                String stat = null;
                
                //��ӻ��ϵ��gtl����
                int count = exprList.getExpr(j).jjtGetNumChildren();
                if(count > 0 && exprList.getExpr(j).jjtGetChild(0) instanceof Expression) {
                    StringBuffer sb = new StringBuffer();
                    
                    for(int k=0; k<count; k++) {
                        if(sb.length() > 0) {
                            sb.append(" || ");
                        }
                        sb.append(((Expression)exprList.getExpr(j).jjtGetChild(k)).toGTL());                        
                    }
                    stat = sb.toString();
                } else {
                    stat = exprList.getExpr(j).toGTL();                    
                }
                
                
                out.print(stat);
                if (!stat.endsWith("}")) {
                    out.println(";");
                }
                out.println("    if (temp == 0) {");
                out.println("        return;");
                out.println("    }");
            }
            
            // �����¼�����
            int triggerEventMask = exprList.getEventMask();
            triggerEventMasks.add(triggerEventMask);
            eventMask |= triggerEventMask;
            
            // �������ж���
            exprList = ExpressionList.fromString(clientTriggers.get(i).action);
            for (int j = 0; j < exprList.getExprCount(); j++) {
                int dataType = -1;
                try {
                    dataType = exprList.getExpr(j).checkSyntax(new String[0]);
                } catch (Exception e) {
                }
                if (dataType != -1) {
                    out.print("    temp = ");
                } else {
                    out.print("    ");
                }
                String stat = exprList.getExpr(j).toGTL();
                out.print(stat);
                if (!stat.endsWith("}")) {
                    out.println(";");
                }
            }
            out.println("}");
            out.println();
        }
        
        // init������ָ���˽ű���Ҫ�������¼�����
        out.println("void FUNCTION init() {");
        out.println("    PQE_Listen(" + eventMask + ");");
        out.println("}");
        out.println();
        
        // cycle���������ε������д������ĺ���
        out.println("void FUNCTION cycle() {");
        out.println("    int mask = PQE_GetEventMask();");
        for (int i = 0; i < clientTriggers.size(); i++) {
            if ((triggerEventMasks.get(i) & PQEUtils.EVENT_MASK_CYCLE) != 0) {
                out.println("    trigger" + i + "();");
            } else {
                out.println("    if (mask & " + triggerEventMasks.get(i) + ") {");
                out.println("        trigger" + i + "();");
                out.println("    }");
            }
        }
        out.println("}");
        out.println();
        
        // cycleUI, paint, destroy, processPacket������Ϊ��
        out.println("void FUNCTION cycleUI() {");
        out.println("}");
        out.println();
        out.println("void FUNCTION paint() {");
        out.println("}");
        out.println();
        out.println("void FUNCTION destroy() {");
        out.println("}");
        out.println();
        out.println("void FUNCTION event() {");
        out.println("}");
        out.println();
        out.println("void FUNCTION processPacket() {");
        out.println("}");
        out.println();
        
        out.flush();
        return buf.toString();
    }
 
    /**
     * ȡ��Ԥ�����ɵĴ�����Ŀͻ���ETF�ļ����ݡ�ֻ��serverģʽ����ȡ�á�
     * @return
     */
    public byte[] getClientETF() {
        if (clientETF == null) {
            synchronized (QuestInfo.class) {
                if (clientETF == null) {
                    try {
                        // ���ɿͻ���ʹ�õ�����ű��ļ�
                        String gtl = generateClientGTL();
                        //System.out.println(gtl);
                        Properties systemFuncs = new Properties();
                        BufferedReader br = new BufferedReader(new StringReader(GTL_SYS_FUNCS_CONFIG));
                        String line;
                        while ((line = br.readLine()) != null) {
                            if (line.trim().length() > 0) {
                                systemFuncs.put(line, line);
                            }
                        }
                        byte[] rawETF = GTLCompiler.compileInMemory(gtl, systemFuncs);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        GZIPOutputStream zos = new GZIPOutputStream(bos);
                        zos.write(rawETF);
                        zos.flush();
                        zos.close();
                        clientETF = bos.toByteArray();
                    } catch (Throwable e) {
                        System.err.println("error in quest " + owner.id);
                        e.printStackTrace();
                    }
                }
            }
        }
        return clientETF;
    }

    /**
     * �ж�һ�������Ƿ�����ͻ����޸ġ�ֻ��serverģʽ����ȡ�á�
     * @param index ����������������е�����
     * @return ����������������ͻ����޸ģ�����false
     */
    public boolean isClientModificationAllowed(int index) {
        return clientVarFlags[index];
    }

    @Override
    public boolean changed(DataObject obj) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean depends(DataObject obj) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public DataObject duplicate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void load(Element elem) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(DataObject obj) {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * �������������Խ��й��ʻ������������Ҫ���ʻ����ַ���������ȡ������context�в��ҷ�������
     * @param context
     * @return �����ĳ�����Ա��滻������true�����򷵻�false��
     */
    public boolean i18n(I18NContext context) {
        boolean changed = false;
        for (QuestVariable var : variables) {
            String tmp = context.input(var.name, "Quest Variable");
            if (tmp != null) {
                var.name = tmp;
                changed = true;
            }
        }
        for (QuestTrigger trigger : triggers) {
            ExpressionList exprList = ExpressionList.fromString(trigger.condition);
            if (I18NUtils.processExpressionList(exprList, context, owner.id, ownerName)) {
                trigger.condition = exprList.toString();
                changed = true;
            }
            exprList = ExpressionList.fromString(trigger.action);
            if (I18NUtils.processExpressionList(exprList, context, owner.id, ownerName)) {
                trigger.action = exprList.toString();
                changed = true;
            }
        }
        return changed;
    }
}
