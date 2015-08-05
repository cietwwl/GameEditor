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
public class Effect_CHANGE_WEAPON_MATK extends Effect_PercentAdd{

    public Effect_CHANGE_WEAPON_MATK(int t) {
        super(t);
    }
    
    public String getJavaInterface(){
        return "PropertyEnhancer";
    }


    public void genBuffEnhance(PrintWriter out, String p1, String p2){
        // Effect_PercentAdd：百分比float
        out.println("        if (pc.unit.equipments.getWeapon() != null) {");
        out.println("            Equipment t = pc.unit.equipments.getWeapon();");
        out.println("            pc.spellpower += t.getMagicPower() * multiple * " + p1 + " / 100.0f;");
        out.println("        }");
    }

    public void genSkillPreDamage(PrintWriter out,  int damageType, String p1, String p2){
        // Effect_MultiAdd: 数额int，百分比float
        if (damageType != SkillConfig.DAMAGE_MAGIC) {
        throw new IllegalArgumentException("只有法术伤害允许使用改变武器法术攻击力效果。");
        }
        
        out.println("        Equipments equips = ((Player)context.source).getEquipments();");
        out.println("        if ( equips != null && equips.getWeapon() != null) {");
        out.println("            Equipment weapon = equips.getWeapon();");
        out.println("            context.attackPower += weapon.getMagicPower() * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f;");
        out.println("        }");
    }
}
