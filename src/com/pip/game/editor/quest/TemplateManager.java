package com.pip.game.editor.quest;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.pip.game.data.ProjectConfig;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.QuestVariable;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.data.quest.pqe.ParserConstants;
import com.pip.game.editor.quest.expr.A_Custom;
import com.pip.game.editor.quest.expr.A_Empty;
import com.pip.game.editor.quest.expr.C_AnswerQuestion;
import com.pip.game.editor.quest.expr.C_CloseChat;
import com.pip.game.editor.quest.expr.C_CloseMessage;
import com.pip.game.editor.quest.expr.C_Custom;
import com.pip.game.editor.quest.expr.C_GlobalVar;
import com.pip.game.editor.quest.expr.C_LocalVar;
import com.pip.game.editor.quest.expr.C_Sex;
import com.pip.game.editor.quest.expr.C_True;
import com.pip.game.editor.quest.expr.IExpr;
import com.pip.util.Utils;

/**
 * 管理任务设计中用到的表达式模板。
 * @author lighthu
 */
public class TemplateManager {
    private ProjectConfig owner;
    
    // world、map、player、creature、quest、unit、ai、creatureai、mapai
    /** 定义模板需要的环境 */
    public static final int CONTEXT_WORLD = 1;
    public static final int CONTEXT_MAP = 2;
    public static final int CONTEXT_PLAYER = 4;
    public static final int CONTEXT_CREATURE = 8;
    public static final int CONTEXT_QUEST = 16;
    public static final int CONTEXT_UNIT = 32;
    public static final int CONTEXT_AI = 64;
    public static final int CONTEXT_CREATUREAI = 128;
    public static final int CONTEXT_MAPAI = 256;
    public static final int CONTEXT_SYSTEM = 512;
   
    /** 物品使用条件、出口条件、复活点条件提供的环境 */
    public static final int CONTEXT_SET_CONDITION = CONTEXT_WORLD | CONTEXT_MAP | CONTEXT_PLAYER | CONTEXT_UNIT;
    /** 任务提供的环境 */
    public static final int CONTEXT_SET_QUEST = CONTEXT_WORLD | CONTEXT_MAP | CONTEXT_PLAYER | CONTEXT_QUEST | CONTEXT_UNIT;
    /** 怪物AI提供的环境 */
    public static final int CONTEXT_SET_CREATURE_AI = CONTEXT_WORLD | CONTEXT_MAP | CONTEXT_CREATURE | CONTEXT_UNIT | CONTEXT_AI | CONTEXT_CREATUREAI;
    /** 场景AI提供的环境 */
    public static final int CONTEXT_SET_MAP_AI = CONTEXT_WORLD | CONTEXT_MAP | CONTEXT_PLAYER | CONTEXT_CREATURE | CONTEXT_UNIT | CONTEXT_AI | CONTEXT_MAPAI;
    /**系统编辑器的环境*/
    public static final int CONTEXT_SET_SYSTEM = CONTEXT_WORLD | CONTEXT_MAP | CONTEXT_PLAYER | CONTEXT_CREATURE | CONTEXT_UNIT | CONTEXT_AI | CONTEXT_MAPAI | CONTEXT_CREATUREAI | CONTEXT_SYSTEM;
    
    
	/**
	 * 模板选择树的数据。
	 */
	public String[] TEMPLATE_TYPES;
	public IExpr[][] TEMPLATES;
	public int[][] TEMPLATES_CONTEXT;    // 对应模板适用的环境，-1表示不限制
	
	/**
	 * 所有已知的模板。
	 */
	public List<IExpr> knownTemplates = new ArrayList<IExpr>();
	
	public TemplateManager(ProjectConfig owner) {
	    this.owner = owner;
        try {
            load(owner.getOwner().baseDir, owner.getProjectClassLoader());
        }  catch (Exception e) {
            e.printStackTrace();
        }          
        init();
	}
	
	public void init() {
        knownTemplates.add(new C_Sex());
		for (PQEUtils.SystemVar fn : owner.pqeUtils.SYSTEM_VARS) {
			knownTemplates.add(new C_GlobalVar(fn.name, ParserConstants.EQ, 0));
		}
		knownTemplates.add(new A_Empty());
        knownTemplates.add(new C_True());
        knownTemplates.add(new C_CloseChat());
        knownTemplates.add(new C_CloseMessage());
        knownTemplates.add(new C_AnswerQuestion());
		for (int i = 0; i < TEMPLATES.length; i++) {
			for (IExpr expr : TEMPLATES[i]) {
				if (expr instanceof C_GlobalVar) {
					continue;
				}
				if (expr instanceof C_Custom || expr instanceof A_Custom) {
					continue;
				}
				knownTemplates.add(expr);
			}
		}
	}
	
	public void load(File baseDir, ClassLoader classLoader) throws Exception{
        Document doc = Utils.loadDOM(new File(baseDir, "quest_conf.xml"));
        List list = doc.getRootElement().getChildren("TEMPLATE_TYPES");
        list = ((Element )list.get(0)).getChildren("TEMPLATE_TYPE");
        
        try {
            TEMPLATE_TYPES = new String[list.size()];
            TEMPLATES = new IExpr[TEMPLATE_TYPES.length][];
            TEMPLATES_CONTEXT = new int[TEMPLATE_TYPES.length][];
            for(int i=0; i<TEMPLATE_TYPES.length; i++) {
                Element elem = (Element)list.get(i); 
                TEMPLATE_TYPES[i] = elem.getAttributeValue("name");            
                List list2 = elem.getChildren("TEMPLATE");
                
                TEMPLATES[i] = new IExpr[list2.size()];
                TEMPLATES_CONTEXT[i] = new int[list2.size()];
                for(int j=0; j<TEMPLATES[i].length; j++) {
                    Element elem2 = (Element)list2.get(j); 
                    String className = elem2.getAttributeValue("classname");
                    
                    Class class1 = classLoader.loadClass(className);
    
                    String params = elem2.getAttributeValue("params");
                    if ("".equals(params)) {
                        TEMPLATES[i][j] = (IExpr)class1.newInstance();
                    } else if ("null".equals(params)){
                        Constructor[] conss = class1.getConstructors();
                        try{
                            TEMPLATES[i][j] = (IExpr)conss[0].newInstance((QuestInfo)null);
                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        String[] param = params.split(",");
                        String param1 = param[0].trim();
                        param[1] = param[1].trim();
                        int param2 = 0;
                        int param3 = Integer.parseInt(param[2].trim());
                        if("EQ".equals(param[1])) {
                            param2 = ParserConstants.EQ;
                        } else if("NE".equals(param[1])) {
                            param2 = ParserConstants.NE;
                        } else if("LT".equals(param[1])) {
                            param2 = ParserConstants.LT;
                        } else if("GE".equals(param[1])) {
                            param2 = ParserConstants.GE;
                        } else if("GT".equals(param[1])) {
                            param2 = ParserConstants.GT;
                        } else if("LE".equals(param[1])) {
                            param2 = ParserConstants.LE;
                        }
                        
                        Constructor[] conss = class1.getConstructors();
                        TEMPLATES[i][j] = (IExpr)conss[0].newInstance(param1, param2, param3);
                    }
                    
                    TEMPLATES_CONTEXT[i][j] = getContextID(elem2.getAttributeValue("context"));
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
	}
	
	protected int getContextID(String name) {
	    if (name == null) {
	        return -1;
	    }
	    if ("world".equals(name)) {
	        return CONTEXT_WORLD;
	    } else if ("map".equals(name)) {
	        return CONTEXT_MAP;
        } else if ("player".equals(name)) {
            return CONTEXT_PLAYER;
        } else if ("creature".equals(name)) {
            return CONTEXT_CREATURE;
        } else if ("quest".equals(name)) {
            return CONTEXT_QUEST;
        } else if ("unit".equals(name)) {
            return CONTEXT_UNIT;
        } else if ("ai".equals(name)) {
            return CONTEXT_AI;
        } else if ("creatureai".equals(name)) {
            return CONTEXT_CREATUREAI;
        } else if ("mapai".equals(name)) {
            return CONTEXT_MAPAI;
	    } else if ("system".equals(name)) {
	        return CONTEXT_SYSTEM;
	    }
	    return -1;
	}
	
	/**
	 * 识别一个表达式对象是否可以用某个模板来表示。
	 * @param expr
	 * @param qinfo
	 * @return
	 */
	public IExpr recognize(Expression expr, QuestInfo qinfo) {
		for (IExpr t : knownTemplates) {
			IExpr ret = t.recognize(qinfo, expr);
			if (ret != null) {
				return ret;
			}
		}
		if (qinfo instanceof QuestInfo){
    		for (QuestVariable localVar : qinfo.variables) {
    			IExpr t = new C_LocalVar(localVar.name);
    			IExpr ret = t.recognize(qinfo, expr);
    			if (ret != null) {
    				return ret;
    			}
    		}
		}
		IExpr ret = new C_Custom().recognize(qinfo, expr);
		if (ret != null) {
			return ret;
		} else {
			return new A_Custom().recognize(qinfo, expr);
		}
	}
}
