package com.pip.game.data.AI;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.pip.game.data.ProjectData;
import com.pip.game.data.i18n.I18NContext;
import com.pip.game.data.quest.Quest;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.QuestVariable;

/**
 * AI阶段。
 * @author ybai
 */
public class AIRuleConfig {
    /** 一个ai阶段有3个步骤 */
    public QuestInfo[] steps = new QuestInfo[3];
    
    private String name;
    
    public AIRule parent;
    
    public AIRuleConfig(AIRule parent, String name) {
        this.parent = parent;
        this.name = name;
        
        for(int i=0; i<steps.length; i++) {
            steps[i] = new QuestInfo(new Quest(parent.owner));
            steps[i].ownerName = "AI";
        }
    }
    
    public String getName() {
        return name;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        for(int j=0; j<steps.length; j++) {
            QuestInfo questInfo = steps[j];
            
            List<QuestVariable> tmpVar = questInfo.variables;
            questInfo.variables = new ArrayList<QuestVariable>();
            
            sb.append("[");
            sb.append("\r\n");
            
            sb.append("    " + questInfo.saveToText().replace("\r\n", "\n").replaceAll("\n", "\r\n    "));
            
            sb.append("\r\n");
            sb.append("]");
            sb.append("\r\n");
            
            steps[j].variables = tmpVar;
        }
        
        return sb.toString();
    }

    /**
     * 对这个对象的属性进行国际化处理，如果有需要国际化的字符串，则提取出来到context中查找翻译结果。
     * @param context
     * @return 如果有某个属性被替换，返回true，否则返回false。
     */
    public boolean i18n(I18NContext context) {
        boolean changed = false;
        for (int i = 0; i < steps.length; i++) {
            if (steps[i].i18n(context)) {
                changed = true;
            }
        }
        return changed;
    }
}
