/**
 * 
 */
package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.Effect_CannotMove;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.BuffConfig;

/**
 * @author jhkang
 */
public class Effect_TRANSPORT_TO_ME extends Effect_CannotMove{

    public Effect_TRANSPORT_TO_ME(int t) {
        super(t);
    }
    
    public String getJavaInterface() throws Exception{
        throw new Exception("此效果不支持buff");
    }


    public void genSkillFinished(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6){
        // Effect_CannotMove：无参数
        out.println("        if (context.hited()) {");
        out.println("            try{");
        out.println("            context.target.goMap(context.source.map.id, context.source.getX(), context.source.getY());");
        out.println("            }catch(Exception e){");
        out.println("            }");
        out.println("        }");
        //                out.println("        if (context.hited()) {");
        //                out.println("            context.target.goMap(context.source.map.id, context.source.x, context.source.y);");
        //                out.println("        }");
    }
}