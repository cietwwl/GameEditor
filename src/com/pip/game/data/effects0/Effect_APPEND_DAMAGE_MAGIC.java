package com.pip.game.data.effects0;

import java.io.PrintWriter;

import com.pip.game.data.skill.BuffConfig;
import com.pip.game.data.skill.Effect_AppendDamage;
import com.pip.game.data.skill.SkillConfig;

public class Effect_APPEND_DAMAGE_MAGIC extends Effect_AppendDamage{

    public Effect_APPEND_DAMAGE_MAGIC(int t) {
        super(t);
    }
    
    public void genBuffPreDamage(PrintWriter out, String p1, String p2, String p3){
        out.println("        context.appendDamage(MagicDamage.class, " + p1 + ", 0, 0f, 0, 0f, " + p2 + "==1, " + p3 + "==1);");
    }

    public void genSkillPreDamage(PrintWriter out,  int damageType, String p1, String p2){
        out.println("        context.appendDamage(MagicDamage.class, " + p1 + ", 0, 0f, 0, 0f, " + p2 + "==1, magic_use_props==1);");
    }
}
