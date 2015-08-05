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
public class Effect_CANNOT_MOVE extends Effect_CannotMove{

    public Effect_CANNOT_MOVE(int t) {
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
        out.println("        if (targetUnit.getThreatCount() > 0) {");
        out.println("            return true;");
        out.println("        }");
//        out.println("        if (targetUnit.map.id != ownerMap) {");
//        out.println("            return true;");
//        out.println("        }");
//        out.println("        int offx = Math.abs(targetUnit.x - ownerX);");
//        out.println("        int offy = Math.abs(targetUnit.y - ownerY);");
//        out.println("        if (offx >= 32 || offy >= 32) {");
//        out.println("            return true;");
//        out.println("        }");
    }

    public void genCustomerFields(PrintWriter out){
        // 如果有不能移动的效果，需要记录此时玩家的位置
        out.println("    int ownerMap;");
        out.println("    int ownerX;");
        out.println("    int ownerY;");
    }

    @Override
    public void genConstructorCodes(PrintWriter out) {
        out.println("        try {");
        out.println("            ownerMap = tgt.map.id;");
        out.println("        } catch (Exception e) {}");
        out.println("        ownerX = tgt.getX();");
        out.println("        ownerY = tgt.getY();");
    }
    
    
}
