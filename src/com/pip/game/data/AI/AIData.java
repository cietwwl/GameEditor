package com.pip.game.data.AI;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.i18n.I18NContext;
import com.pip.game.data.quest.QuestTrigger;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.ExpressionList;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.data.quest.pqe.ParserConstants;
import com.pip.game.data.quest.pqe.PQEUtils.SystemVar;


public class AIData extends DataObject {

    /**
     * 所属项目。
     */
    public ProjectData owner;

    public AIRule aiRule;
    public int type;
    public String aiImplClass;

    public final static int CREATURE_AI =1;
    public final static int MAP_AI =2;
    
    public AIData(ProjectData owner) {
        this.aiRule = new AIRule(owner);
        this.owner = owner;
        this.type = CREATURE_AI;
    }

    @Override
    public boolean changed(DataObject obj) {
        return changed(this, obj);
    }

    @Override
    public boolean depends(DataObject obj) {
        return false;
    }

    @Override
    public DataObject duplicate() {
        AIData copy = new AIData(owner);
        copy.update(this);
        return copy;
    }

    @Override
    public void load(Element elem) {
        id = Integer.parseInt(elem.getAttributeValue("id"));
        title = elem.getAttributeValue("title");
        description = elem.getAttributeValue("description");
        try{
            type = Integer.parseInt(elem.getAttributeValue("type"));
        }catch(Exception e){
            type = 1;
        }
        setCategoryName(elem.getAttributeValue("category"));
        File file = new File(owner.baseDir, "/ai/" + id + ".txt");
        if(file.exists()) {
            this.aiRule.loadAI(file);
        } else {
            //以前用的title，如果title有重复的会被覆盖，所以现在改成id.txt
            this.aiRule.loadAI(new File(owner.baseDir, "/ai/" + title + ".txt"));            
        }
    }

    @Override
    public Element save() {
        Element ret = new Element("AI");
        ret.addAttribute("id", String.valueOf(id));
        ret.addAttribute("title", title);
        ret.addAttribute("description", description);
        ret.addAttribute("type", String.valueOf(type));
        if (getWholeCategoryName() != null) {
            ret.addAttribute("category", getWholeCategoryName());
        }
        this.aiRule.save(new File(owner.baseDir, "/ai/" + id + ".txt"));
        return ret;
    }

    @Override
    public void update(DataObject obj) {
        AIData oo = (AIData) obj;
        id = oo.id;
        title = oo.title;
        description = oo.description;
        type = oo.type;
        setCategoryName(oo.getCategoryName());
        this.aiRule.update(oo.aiRule);
    }

    @Override
    public String toString() {
        return id + ":" + title;
    }
    
    public boolean equals(Object o) {
        return this == o;
    }

    
  //************added by tzhang 2010-8-2*********
    /**
     * 计算自动生成的类名
     * @param classPrefix
     * @return
     */
    public String getClassName(String classPrefix) {
        String idStr = String.valueOf(id);
        while (idStr.length() < 3) {
            idStr = "0" + idStr;
        }
        return classPrefix + idStr;
    }
    
   
    
    protected static final String[][] AITransTables = {
        { "HasItem", "p.hasItem" },
        { "_MONEY","p.getMoney()"},
        { "_CLASS","p.getClass()"},
        { "_LEVEL","owner.getLevel()"},
        { "E_TouchNPC","p.touchNPC"},
        { "AddItem", "p.getBags().getBag(0).addItem"},
        { "FindNPCAt","findNPCAt"},
        { "NpcShout","owner.shout"},
        { "_HP","owner.getHp()"},
        { "Random","battleOwner.random"},
        { "E_TimeInterval","timeInterval"},
        
    };
    
    protected static final String[][] AITransTables2 = {
        { "_HP","battleOwner.getHp()"},
        { "HasItem", "p.hasItem" },
        { "_MONEY","p.getMoney()"},
        { "_CLASS","p.getClass()"},
        { "_LEVEL","battleOwner.getLevel()"},
        { "E_TouchNPC","p.touchNPC"},
        { "AddItem", "p.getBags().getBag(0).addItem"},
        { "FindNPCAt","findNPCAt"},
        { "NpcShout","battleOwner.shout"},
        { "NpcAttack","battleOwner.createAction"},
        { "EnterStage","enterStage"},
        { "_RoundNum","getRoundNum()"},
        { "E_UnitState","battleOwner.getUnitHpState"},
        { "E_RandomSkill","battleOwner.randomSkill"},
        { "E_SkillCount","battleOwner.skillCount"},
        { "E_AddBuff","battleOwner.addSkill"},
        { "E_RemoveBuff","battleOwner.removeSkill"},
        { "Random","battleOwner.random"},
        { "E_TimeInterval","timeInterval"},
        { "NpcShoutInBattle","battleOwner.shoutInBattle"},
        { "E_BattleRound","isMultipleRound"},
        { "E_UnitNum","((MonsterEx)battleOwner).getUnitNum"},
        { "E_SummonUnit","((MonsterEx)battleOwner).summonUnit"},
        { "E_CallFriend","((MonsterEx)battleOwner).callFriend"},
        { "E_MonstersDieByOrder","((MonsterEx)battleOwner).dieByOrder"},
        { "_HPRATE","getPlayersHPRate()"},
        { "_MPRATE","getPlayersMPRate()"},
        { "_MonsterHPRATE","getMonsterHPRate()"},
        { "Set","pool.setInt"},
        { "ChangeNpcImage","((MonsterEx)battleOwner).changeNpcImage"},
        { "_MonsterImgState","((MonsterEx)battleOwner).getChangeState()"},
    };
    
    protected static final String[][] MapAITransTables = {
        { "E_AddBuff","u.addSkill"},
        { "E_RemoveBuff","u.removeSkill"},
        { "E_TimeInterval","timeSchedule"},
        { "RefreshMapAiNPC","refreshNPC"},
        { "_NPCID","getNPCId(u)"},
        { "E_RemoveTransfer","owner.removeTransfer"},
        { "E_AddTransfer","owner.addTransfer"},
        { "Set","pool.setInt"},
    };
    
    protected static final Map<String, String> MapAITransMap = new HashMap<String, String>();
    static {
        for (int i = 0; i < MapAITransTables.length; i++) {
          
            MapAITransMap.put(MapAITransTables[i][0], MapAITransTables[i][1]);
        }
    }
    
    protected static final Map<String, String> AITransMap = new HashMap<String, String>();
    static {
        for (int i = 0; i < AITransTables.length; i++) {
          
            AITransMap.put(AITransTables[i][0], AITransTables[i][1]);
        }
    }
    
    protected static final Map<String, String> AITransMap2 = new HashMap<String, String>();
    static {
        for (int i = 0; i < AITransTables2.length; i++) {
          
            AITransMap2.put(AITransTables2[i][0], AITransTables2[i][1]);
        }
    }
    
    public String getBaseClass() {
        if(owner.config.BaseAIClass == null) {
            return "cybertron.core.ai.BaseAI";
        } else {
            return owner.config.BaseAIClass;
        }
    }
    
    public void generateAIJava(PrintWriter out, String packageName, String classPrefix) throws Exception {
        // package & import
        
        AIData aiData = owner.findAIData(id);
        if(aiData == null) {
            return;
            //throw new Exception("NPCTemplate.java:generateAIJava():npc对应的AI没有找到!!");
        }
        
        if (aiData.type == MAP_AI)
        {
            generateMapAI(aiData ,out,  packageName, classPrefix);
            return;
        }
        
        out.println("package " + packageName + ";");
        out.println();

        out.println("import java.util.LinkedHashMap;");
        out.println("import cybertron.core.ai.AI;");  
        out.println("import cybertron.core.ai.AIStage;");
        
        out.println("import " + getBaseClass() + ";");
        
        out.println("import cybertron.core.CombatUnit;");
        out.println("import optimus.gameobject.MonsterEx;");
        out.println();
        
        String className = getClassName(classPrefix);

        out.print("public class " + className + " extends " + getBaseClass());
        out.println(" {");
        out.println("    protected LinkedHashMap<Integer,AIStage> stages = new LinkedHashMap<Integer,AIStage>();");
        out.println("    AIStage stage;");
        out.println("    int currentStage;");
        out.println("    int id;");
        //构造函数 
        out.println("    public " + className + "(){");
        out.println("        super();");
        out.println("        currentStage = 1;");
        out.println("        battleState = 0;");
        aiData = owner.findAIData(id);
        if(aiData == null) {
            return;
            //throw new Exception("NPCTemplate.java:generateAIJava():npc对应的AI没有找到!!");
        }
        for(AIRuleConfig ai : aiData.aiRule.combatStatus)
        {   
            //out.print("        this.id = ");
            //out.println(this.getID() + ";");
            int j = aiData.aiRule.combatStatus.indexOf(ai);    
            
            out.print("        stage = new stage");
            out.print(j+1);
            out.println("();");
            out.print("        stages.put(");
            out.print(j+1);
            out.println(",stage);");
            
        }    
        out.println("      ");        
        out.println("   }");    
    // TODO Auto-generated constructor stub
        
        //---------------------getId()----------------------
        out.println("   public int getId() {");
        out.println("       return this.id;");
        out.println("   }");
        
        //---------------------addedToMap()------------------       
        out.println("   public void addedToMap() {");
        parserInsertPoint(out, AIRule.IP_JOIN_MAP);
        out.println("   }");
        out.println();
        
        //---------------------died()------------------       
        out.println("   public void died() {");
        parserInsertPoint(out, AIRule.IP_DEATH);
        out.println("   }");
        out.println();
        
        //---------------------enterBattle()------------------ 
        out.println("   public void enterBattle() {");
        //parserInsertPoint(out, aiRule.IP_ENTER_BATTLE);
        out.println("           AIStage stage = stages.get(currentStage);");
        out.println("           if(stage != null)");
        out.println("               stage.aiMain();");
       
        
        out.println("   }");
        out.println();
        //---------------------exitBattle()------------------ 
        out.println("   public void exitBattle() {");
        out.println("   }");
        out.println();
        //---------------------init()------------------ 
        out.println("   public void init() {");
        parserInsertPoint(out, AIRule.IP_INIT);
        out.println("   }");
        out.println();
        //---------------------relive()------------------ 
        out.println("   public void relive() {");
        parserInsertPoint(out, AIRule.IP_REVIVE);
        out.println("   }");
        out.println();
        //---------------------removeFromMap()------------------ 
        out.println("   public void removeFromMap() {");
        parserInsertPoint(out, AIRule.IP_REMOVE_FROM_MAP);
        out.println("   }");
        out.println();
        //---------------------removeFromWorld()------------------ 
        out.println("   public void removeFromWorld() {");
        parserInsertPoint(out, AIRule.IP_REMOVE_FROM_WORLD);
        out.println("   }");
        out.println();
        
        //---------------------roundStart()------------------ 
        out.println("   public void roundStart(CombatUnit unit) {");
        out.println("           battleOwner = unit;");
        out.println("           AIStage stage = stages.get(currentStage);");
        out.println("           if(stage != null)");
        out.println("               stage.aiMain();");
        out.println("   }");
        out.println();
        
        //---------------------enterStage()------------------ 
        out.println("   public void enterStage(int stageId) {");
        out.println("       currentStage = stageId;");
        out.println("   }");
        out.println();
        
        //---------------------update()------------------     
        out.println("   public boolean update() {");
        out.println("       if(battleState == 0){");
        out.println("       super.update();");
        parserInsertPoint(out, AIRule.IP_UPDATE);
        out.println("       }");
        out.println("       if(battleState == 1){");
        //out.println("           if(currentStage == 0)");
        //out.println("               return false;");
        out.println("           AIStage stage = stages.get(currentStage);");
        out.println("           if(stage != null)");
        out.println("               stage.aiMain();");
        out.println("       }");
        out.println("       return false;");
        out.println("   }");
        out.println();
        
        // ai为一个阶段
        for(AIRuleConfig ai : aiData.aiRule.combatStatus)
        {   
            int j = aiData.aiRule.combatStatus.indexOf(ai);
            out.print("    public class stage");
            out.print(j+1);
            out.println(" implements AIStage {");
           /* out.println("        public void aiMain() {");
            out.println("        }");
            
            out.println("        public void enter() {");
            out.println("        }");
            out.println("        public void exit() {");
            out.println("        }");*/
            out.println("        public int getId() {");
            out.println("            return 0;");
            out.println("        }");
            
            //steps为阶段里的步骤
            for(int i=0; i< ai.steps.length; i++) {
                List<QuestTrigger> statusTrigger = ai.steps[i].triggers;
                //for(QuestTrigger qt : statusTrigger )                   
                    if(i == 0)
                    {
                        out.println("        public void enter() {");
                        out.print("            currentStage = ");
                        out.print(j+1);
                        out.println(";");
                    }
                    if(i == 1)
                    {
                        out.println("        public void exit() {");
                        out.println("            currentStage = 0;");
                    }
                    if(i == 2)
                    {
                        out.println("        public void aiMain() {");
                    }
                    
                    for(QuestTrigger qt : statusTrigger )
                    {
                        ExpressionList triggerConditonel = ExpressionList.fromString(qt.condition);
                        if (triggerConditonel.getExprCount() > 0)
                        {   
                            out.print("       if("  );
                            parserEl(out, triggerConditonel , AITransMap2,aiData.type ,false);
                            out.println(")");
                        }
                        out.print("           ");
                        ExpressionList triggerActionel = ExpressionList.fromString(qt.action);
                        parserEl(out, triggerActionel , AITransMap2,aiData.type,true);
                        //out.println(";");
                        
                        
                    }    
                    out.println("        }");
            }
            
            
            out.println("    }");
            
        }
              
        out.println("       public " + getBaseClass() + " clone(){");
        out.println("           " + getBaseClass() + " cloneAI = new "+className+"();");
        out.println("           cloneAI.owner = this.owner;");
        out.println("           cloneAI.battleOwner = this.battleOwner;");
        out.println("            return cloneAI;");
        out.println("       }");

          
        out.println("}");
        this.aiImplClass = packageName + "." + className;
    }
    
    public void generateMapAI(AIData aiData, PrintWriter out, String packageName, String classPrefix) throws Exception {
        // TODO Auto-generated method stub
        out.println("package " + packageName + ";");
        out.println();

        out.println("import java.util.LinkedHashMap;");
        out.println("import cybertron.core.ai.AI;");  
        out.println("import cybertron.core.ai.AIStage;");
        out.println("import " + getBaseClass() + ";");
        out.println("import cybertron.core.CombatUnit;");
        out.println("import cybertron.core.map.BaseMapAI;");
        out.println("import cybertron.core.Unit;");
        out.println();
        
        String className = getClassName(classPrefix);

        out.println("//AutoGenerated MapAI");
        out.print("public class " + className + " extends BaseMapAI");
        out.println(" {");
        out.println("    protected LinkedHashMap<Integer,AIStage> stages = new LinkedHashMap<Integer,AIStage>();");
        out.println("    AIStage stage;");
        out.println("    int currentStage;");
        out.println("    int id;");
        //构造函数 
        out.println("    public " + className + "(){");
        out.println("        super();");
        /*out.println("        currentStage = 1;");
        out.println("        battleState = 0;");*/
        aiData = owner.findAIData(id);
        if(aiData == null) {
            return;
            //throw new Exception("NPCTemplate.java:generateAIJava():npc对应的AI没有找到!!");
        }
        /*for(AIRuleConfig ai : aiData.aiRule.combatStatus)
        {   
            //out.print("        this.id = ");
            //out.println(this.getID() + ";");
            int j = aiData.aiRule.combatStatus.indexOf(ai);    
            
            out.print("        stage = new stage");
            out.print(j+1);
            out.println("();");
            out.print("        stages.put(");
            out.print(j+1);
            out.println(",stage);");
            
        } */   
        out.println("      ");        
        out.println("   }");    
        
        //---------------------getId()----------------------
        out.println("   public int getId() {");
        out.println("       return this.id;");
        out.println("   }");
        
        //---------------------addedToMap()------------------       
        out.println("   public void enterMap(Unit u) {");
        parserInsertPoint(out, AIRule.IP_JOIN_MAP);
        out.println("   }");
        out.println();
        
        //---------------------leaveMap()------------------ 
        out.println("   public void leaveMap(Unit u) {");
        parserInsertPoint(out, AIRule.IP_REMOVE_FROM_MAP);
        out.println("   }");
        out.println();
        
      //---------------------addCreature()------------------       
        out.println("   public void addCreature(Unit u) {");
        parserInsertPoint(out, AIRule.IP_REVIVE);
        out.println("   }");
        out.println();
        
        //---------------------died()------------------       
        out.println("   public void died(Unit u) {");
        parserInsertPoint(out, AIRule.IP_DEATH);
        out.println("   }");
        out.println();
        
        //---------------------update()------------------     
        out.println("   public void update() {");
        parserInsertPoint(out, AIRule.IP_UPDATE);
        out.println("   }");
        out.println();
        
        out.println("       public BaseMapAI clone(){");
        out.println("           BaseMapAI cloneAI = new "+className+"();");
        out.println("           cloneAI.owner = this.owner;");
        out.println("            return cloneAI;");
        out.println("       }");
        
        out.println("}");
    }

    public void parserInsertPoint(PrintWriter out,int pointType) throws Exception
    {
        AIData aiData = owner.findAIData(id);
        if(aiData == null) {
            throw new Exception("NPCTemplate.java:parserInsertPoint():npc对应的AI没有找到!!");
        }
        List<QuestTrigger> initTrigger = aiData.aiRule.getTriggersFromInsertPoint(pointType);
        for(QuestTrigger qt : initTrigger )
        {
            ExpressionList triggerConditonel = ExpressionList.fromString(qt.condition);
            if (triggerConditonel.getExprCount() > 0)
            {   
                out.print("       if("  );
                if(aiData.type == 1)
                    parserEl(out, triggerConditonel , AITransMap,aiData.type,false);
                if(aiData.type == 2)
                    parserEl(out, triggerConditonel , MapAITransMap, aiData.type,false);
                out.println(")");
            }
            out.print("           ");
            ExpressionList triggerActionel = ExpressionList.fromString(qt.action);
            if(aiData.type == 1)
                parserEl(out, triggerActionel , AITransMap, aiData.type,true);
            if(aiData.type == 2)
                parserEl(out, triggerActionel , MapAITransMap, aiData.type,true);
            //out.println(";");
        }
    }
    /**
     * 解析表达式列表
     * 
     * @param out 输出字符用
     * @param el    表达式列表
     * @param conditionMap  转换表达式列表所用MAP
     * @param type  区分怪物AI以及场景AI用
     * @param isAction  区分是条件表达式列表还是动作表达式列表
     */
    public void parserEl(PrintWriter out,ExpressionList el ,Map<String, String> conditionMap ,int type ,boolean isAction){
        if(isAction){
            out.println("{");
        }
        for (int i = 0; i < el.getExprCount(); i++) {
            Expression exp = el.getExpr(i);
            Expr0 leftExpr0 = exp.getLeftExpr();
            
            if (leftExpr0.type == Expr0.TYPE_NUMBER)
            {
                out.print(leftExpr0.toString());
                out.print(PQEUtils.op2str(exp.getOp()));
                
                //need to updated
                //if(exp.getRightExpr().type == Expr0.TYPE_IDENTIFIER)
                //    out.print(conditionMap.get(exp.getRightExpr().toString()));
                //else{
                    out.print(exp.getRightExpr());
                //}
            }
            
            //变量处理 
            if (leftExpr0.type == Expr0.TYPE_IDENTIFIER)
            {
                SystemVar sysVar = owner.config.pqeUtils.SYSTEM_VARS_MAP.get(leftExpr0.toString());
                if (sysVar != null)
                {
                    out.print(conditionMap.get(leftExpr0.toString()) );
                }    
                //临时创建的变量处理
                else {
//                    if(type == 2){
                        out.print("pool.getInt(\"" +leftExpr0.toString() + "\")");
//                    }
//                    else{
//                        out.print("p.questVm.stores.get("+ this.id + ").getValue(" + i +")");
//                    }
                }
                out.print(PQEUtils.op2str(exp.getOp()));
                
                //need to updated
                //if(exp.getRightExpr().type == Expr0.TYPE_IDENTIFIER)
                  //  out.print(conditionMap.get(exp.getRightExpr().toString()));
                //else{
                    out.print(exp.getRightExpr());
                //}
                
            }
            
            //函数处理 
            if (leftExpr0.type == Expr0.TYPE_FUNC)
            {
                FunctionCall fc = leftExpr0.getFunctionCall();
                PQEUtils.SystemFunc func = owner.config.pqeUtils.SYSTEM_FUNCS_MAP.get(fc.funcName);
                out.print(conditionMap.get(fc.funcName) + "(");
              
                if(func.paramType.length == 1)
                {
                    out.print(fc.getParam(0));
                }
                else{
                    for (int j = 0; j < func.paramType.length; j++) {
                    out.print(fc.getParam(j));
                    if ( j < func.paramType.length - 1)
                        out.print(",");
                    }
                }
                
                if (fc.funcName.equalsIgnoreCase("addItem") || fc.funcName.equalsIgnoreCase("removeItem")) {
                    out.print(",");
                    out.print("\""+"AI "+String.valueOf(id)+"\"");
                }
                out.print(")");
                              
                if(exp.getRightExpr() != null) {
                    switch(exp.getOp()) {
                        case ParserConstants.EQ:
                            out.print(" == ");
                            break;
                        case ParserConstants.NE:
                            out.print(" != ");
                            break;
                        case ParserConstants.LT:
                            out.print(" < ");
                            break;
                        case ParserConstants.LE:
                            out.print(" <= ");
                            break;
                        case ParserConstants.GT:
                            out.print(" > ");
                            break;
                        case ParserConstants.GE:
                            out.print(" >= ");
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid operator.");
                    }
                    
                    out.print(exp.getRightExpr().value);
                }
            }   
            if (i < el.getExprCount()-1){
                if(!isAction){
                    out.println(" && ");
                }else if(isAction){
                    out.println(" ; ");
                }
            }
        }   
        if (isAction){
            out.println(";");
            out.println("       }");
        }  
    }

    /**
     * 对这个对象的属性进行国际化处理，如果有需要国际化的字符串，则提取出来到context中查找翻译结果。
     * @param context
     * @return 如果有某个属性被替换，返回true，否则返回false。
     */
    public boolean i18n(I18NContext context) {
        boolean changed = false;
        if (aiRule.i18n(context)) {
            changed = true;
        }
        return changed;
    }
}
