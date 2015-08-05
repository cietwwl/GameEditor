/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_PercentAdd;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_DISPEL_ALL_BUFF extends Effect_PercentAdd{

    public Effect_DISPEL_ALL_BUFF(int t) {
        super(t);
    }
    
    public String getJavaInterface() throws Exception{
        throw new Exception("此效果不支持buff");
    }


    public void genSkillFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_PercentAdd: 百分比
        out.println("        if (context.hited()) {");
        out.println("            int rate = (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + "));");
        out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
        out.println("                context.target.getBuffs().dispelGoodBuff(true);");
        out.println("            }");
        out.println("        }");
    }
}