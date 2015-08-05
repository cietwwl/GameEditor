/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_Transport;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_TRANSPORT_TO_POS extends Effect_Transport{

    public Effect_TRANSPORT_TO_POS(int t) {
        super(t);
    }
    
    public String getJavaInterface() throws Exception{
        throw new Exception("此效果不支持buff");
    }


    public void genSkillFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_Transport：目标位置
        out.println("        if (context.hited()) {");
        out.println("            context.target.goMap(" + p1 + "[0], " + p1 + "[1], " + p1 + "[2]);");
        out.println("        }");
    }
}