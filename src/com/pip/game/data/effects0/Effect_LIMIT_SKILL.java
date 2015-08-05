/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_LimitSkill;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_LIMIT_SKILL extends Effect_LimitSkill{

    public Effect_LIMIT_SKILL(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "CombatEffect";
    }

    @Override
    public void genCustomerFields(PrintWriter out) {
        // 如果有限制技能的效果，定义技能表
        out.println("    TIntHashSet limitSkills = new TIntHashSet();");
    }

    @Override
    public void genConstructorCodes(PrintWriter out) {
        String p1 = BuffConfig.getFieldName(this, 0, effectID, false);
        out.println("        String[] secs = " + p1 + ".split(\",\");");
        out.println("        for (String s : secs) {");
        out.println("            limitSkills.add(Integer.parseInt(s));");
        out.println("        }");
    }

    @Override
    public void genBreakCurChainCondition(PrintWriter out) {
        out.println("        if (!limitSkills.contains(context.skill.getGroupId())) {");
        out.println("            return;");
        out.println("        }");
    }
    
    
    
}
