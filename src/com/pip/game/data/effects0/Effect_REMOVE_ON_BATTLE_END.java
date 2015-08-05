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
public class Effect_REMOVE_ON_BATTLE_END extends Effect_CannotMove{

    public Effect_REMOVE_ON_BATTLE_END(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "Updatable";
    }

    @Override
    public void genUpdate(PrintWriter out) {
        out.println("        Combatable targetUnit = (Combatable)ObjectAccessor.getGameObject(owner);");
        out.println("        if (targetUnit == null || targetUnit.map == null) {");
        out.println("            return true;");
        out.println("        }");
        out.println("        if (targetUnit.getThreatCount() == 0) {");
        out.println("            return true;");
        out.println("        }");
    }
    
}
