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
 * AI�׶Ρ�
 * @author ybai
 */
public class AIRuleConfig {
    /** һ��ai�׶���3������ */
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
     * �������������Խ��й��ʻ������������Ҫ���ʻ����ַ���������ȡ������context�в��ҷ�������
     * @param context
     * @return �����ĳ�����Ա��滻������true�����򷵻�false��
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
