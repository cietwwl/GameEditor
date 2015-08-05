/**
 * 
 */
package com.pip.game.data.autocoding;

import java.io.PrintWriter;

import com.pip.game.data.skill.EffectConfig;

/**
 * @author jhkang
 *
 */
public class BuffFinished {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public static void genBuffFinished(EffectConfig eff, PrintWriter out, String p1, String p2, String p3, String p4,
            String p5, String p6) {
        switch(eff.getType()){

            case EffectConfig.ADD_MP_ON_HIT:
                // Effect_CureOnHit: 概率float，固定值int，占上限比例float，占伤害比例float
                out.println("        if (isActive && context.hited() && context.isDamage()) {");
                out.println("            int rate = (int)(multiple * 100 * " + p1 + ");");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                int addmp = " + p2 + ";");
                out.println("                addmp += context.source.maxmp * " + p3 + " / 100.0f;");
                out.println("                addmp += context.damage * " + p4 + " / 100.0f;");
                out.println("                context.activeSkills.add(new FixedAddMPSkill(addmp));");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.ADD_DEBUFF_ON_HIT:
                // Effect_AddBuff: 概率float，概率补充变量String，BUFFID int，BUFF级别int
                out.println("        if (isActive && context.hited() && context.isAttack()) {");
                out.println("            boolean hit = false;");
                out.println("            if (" + p2 + ".length() == 0) {");
                out.println("                hit = CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000);");
                out.println("            } else {");
                out.println("                Float vo = context.skillParams.get(" + p2 + ");");
                out.println("                if (vo != null) {");
                out.println("                    hit = CommonUtil.hit(RND, (int)(multiple * 100 * (vo.floatValue() + " + p1 + ")), 10000);");
                out.println("                }");
                out.println("            }");
                out.println("            if (hit) {");
                out.println("                context.target.getBuffs().addBuff(BuffUtil.createBuff(" + p3 + ", "
                        + p4 + ", context.source, context.target, context.value));");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.ADD_BUFF_ON_HIT:
                // Effect_AddBuff: 概率float，概率补充变量String，BUFFID int，BUFF级别int
                out.println("        if (isActive && context.hited() && context.isAttack()) {");
                out.println("            boolean hit = false;");
                out.println("            if (" + p2 + ".length() == 0) {");
                out.println("                hit = CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000);");
                out.println("            } else {");
                out.println("                Float vo = context.skillParams.get(" + p2 + ");");
                out.println("                if (vo != null) {");
                out.println("                    hit = CommonUtil.hit(RND, (int)(multiple * 100 * (vo.floatValue() + " + p1 + ")), 10000);");
                out.println("                }");
                out.println("            }");
                out.println("            if (hit) {");
                out.println("                context.source.getBuffs().addBuff(BuffUtil.createBuff(" + p3 + ", "
                        + p4 + ", context.source, context.source, context.value));");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.CRIT_ACTIVE_BUFF:
                // Effect_CritActiveBuff: 概率float，BUFFID int，BUFF级别int
                out.println("        if (isActive && context.critical() && context.isAttack()) {");
                out.println("            boolean hit = CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000);");
                out.println("            if (hit) {");
                out.println("                ((CombatUnit)context.source).getBuffs().addBuff(BuffUtil.createBuff(" + p2 + ", "
                        + p3 + ", context.source, context.source, context.value));");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.CRITED_ACTIVE_BUFF:
                // Effect_CritActiveBuff: 概率float，BUFFID int，BUFF级别int
                out.println("        if (!isActive && context.critical() && context.isAttack()) {");
                out.println("            boolean hit = CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000);");
                out.println("            if (hit) {");
                out.println("                context.target.getBuffs().addBuff(BuffUtil.createBuff(" + p2 + ", "
                        + p3 + ", context.target, context.target, context.value));");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.FIRST_THREAT_ON_HIT:
                // Effect_FirstThreat: 无参数
                out.println("        if (isActive && context.hited() && context.isAttack()) {");
                out.println("            Attack.makeFirstThreat(context.source, context.target);");
                out.println("            effectTimes++;");
                out.println("        }");
                break;
            case EffectConfig.FEAR_ON_HIT:
                // Effect_FearOnHit: 概率float，概率补充变量String，持续时间int
                out.println("        if (isActive && context.hited() && context.isDamage()) {");
                out.println("            boolean hit = false;");
                out.println("            if (" + p2 + ".length() == 0) {");
                out.println("                hit = CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000);");
                out.println("            } else {");
                out.println("                Float vo = context.skillParams.get(" + p2 + ");");
                out.println("                if (vo != null) {");
                out.println("                    hit = CommonUtil.hit(RND, (int)(multiple * 100 * (vo.floatValue() + " + p1 + ")), 10000);");
                out.println("                }");
                out.println("            }");
                out.println("            if (hit) {");
                out.println("                context.activeSkills.add(new FearSkill(" + p3 + "));");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.SLOW_ON_HIT:
                // Effect_SlowOnHit:
                // 概率float，概率补充变量String，减速级别int，减速级别补充变量String，持续时间int，持续时间变量float
                out.println("        if (isActive && context.hited() && context.isDamage()) {");
                out.println("            boolean hit = false;");
                out.println("            if (" + p2 + ".length() == 0) {");
                out.println("                hit = CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000);");
                out.println("            } else {");
                out.println("                Float vo = context.skillParams.get(" + p2 + ");");
                out.println("                if (vo != null) {");
                out.println("                    hit = CommonUtil.hit(RND, (int)(multiple * 100 * (vo.floatValue() + " + p1 + ")), 10000);");
                out.println("                }");
                out.println("            }");
                out.println("            if (hit) {");
                out.println("                int sl = " + p3 + ";");
                out.println("                Float vo = context.skillParams.get(" + p4 + ");");
                out.println("                if (vo != null) {");
                out.println("                    sl += vo.intValue();");
                out.println("                }");
                out.println("                int tm = " + p5 + ";");
                out.println("                vo = context.skillParams.get(" + p6 + ");");
                out.println("                if (vo != null) {");
                out.println("                    tm *= 1.0f + vo.floatValue() / 100.0f;");
                out.println("                }");
                out.println("                context.activeSkills.add(new SlowSkill(sl, tm));");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.PARALYZE_ON_HIT:
                // Effect_FearOnHit: 概率float，概率补充变量String，持续时间int
                out.println("        if (isActive && context.hited() && context.isDamage()) {");
                out.println("            boolean hit = false;");
                out.println("            if (" + p2 + ".length() == 0) {");
                out.println("                hit = CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000);");
                out.println("            } else {");
                out.println("                Float vo = context.skillParams.get(" + p2 + ");");
                out.println("                if (vo != null) {");
                out.println("                    hit = CommonUtil.hit(RND, (int)(multiple * 100 * (vo.floatValue() + " + p1 + ")), 10000);");
                out.println("                }");
                out.println("            }");
                out.println("            if (hit) {");
                out.println("                context.activeSkills.add(new ParalyzeSkill(" + p3 + "));");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.STAY_ON_HIT:
                // Effect_FearOnHit: 概率float，概率补充变量String，持续时间int
                out.println("        if (isActive && context.hited() && context.isDamage()) {");
                out.println("            boolean hit = false;");
                out.println("            if (" + p2 + ".length() == 0) {");
                out.println("                hit = CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000);");
                out.println("            } else {");
                out.println("                Float vo = context.skillParams.get(" + p2 + ");");
                out.println("                if (vo != null) {");
                out.println("                    hit = CommonUtil.hit(RND, (int)(multiple * 100 * (vo.floatValue() + " + p1 + ")), 10000);");
                out.println("                }");
                out.println("            }");
                out.println("            if (hit) {");
                out.println("                context.activeSkills.add(new StaySkill(" + p3 + "));");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.DUMB_ON_HIT:
                // Effect_FearOnHit: 概率float，概率补充变量String，持续时间int
                out.println("        if (isActive && context.hited() && context.isDamage()) {");
                out.println("            boolean hit = false;");
                out.println("            if (" + p2 + ".length() == 0) {");
                out.println("                hit = CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000);");
                out.println("            } else {");
                out.println("                Float vo = context.skillParams.get(" + p2 + ");");
                out.println("                if (vo != null) {");
                out.println("                    hit = CommonUtil.hit(RND, (int)(multiple * 100 * (vo.floatValue() + " + p1 + ")), 10000);");
                out.println("                }");
                out.println("            }");
                out.println("            if (hit) {");
                out.println("                context.activeSkills.add(new DumbSkill(" + p3 + "));");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.REPEAT_ON_HIT:
                // Effect_PercentAdd: 百分比float
                out.println("        if (isActive && context.hited() && context.isDamage()) {");
                out.println("            if (CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000)) {");
                out.println("                context.activeSkills.add(context.skill);");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.DEC_MP_ON_HIT:
                // Effect_CureOnHit: 概率float，固定值int，占上限比例float，占伤害比例float
                out.println("        if (isActive && context.hited() && context.isDamage()) {");
                out.println("            int rate = (int)(multiple * 100 * " + p1 + ");");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                int decmp = " + p2 + ";");
                out.println("                decmp += context.target.maxmp * " + p3 + " / 100.0f;");
                out.println("                decmp += context.damage * " + p4 + " / 100.0f;");
                out.println("                context.activeSkills.add(new FixedDecMPSkill(decmp));");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.ADD_HP_ON_HIT:
                // Effect_CureOnHit: 概率float，固定值int，占上限比例float，占伤害比例float
                out.println("        if (isActive && context.hited() && context.isDamage()) {");
                out.println("            int rate = (int)(multiple * 100 * " + p1 + ");");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                int cure = " + p2 + ";");
                out.println("                cure += context.source.maxhp * " + p3 + " / 100.0f;");
                out.println("                cure += context.damage * " + p4 + " / 100.0f;");
                out.println("                context.activeSkills.add(new FixedHealSkill(cure));");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.TWO_HIT_ON_HIT:
                // Effect_Hit3Times: 无参数
                out.println("        context.activeSkills.add(context.skill);");
                out.println("        context.activeSkills.add(context.skill);");
                out.println("        effectTimes++;");
                break;
            case EffectConfig.RELIVE_TARGET:
                // Effect_PercentAdd: 百分比float
                out.println("        if (context.source == context.target) {");
                out.println("            if (isActive && !context.source.isAlive()) {");
                out.println("                int hp = (int)(context.source.maxhp * " + p1 + " / 100.0f);");
                out.println("                int mp = (int)(context.source.maxmp * " + p1 + " / 100.0f);");
                out.println("                context.source.relive(hp, mp);");
                out.println("            }");
                out.println("        } else {");
                out.println("            if (isActive) {");
                out.println("                if (!context.target.isAlive()) {");
                out.println("                    // TODO: 向死亡的人发送一个复活请求");
                out.println("                } else {");
                out.println("                    if (!context.target.isAlive()) {");
                out.println("                        int hp = (int)(context.target.maxhp * " + p1 + " / 100.0f);");
                out.println("                        int mp = (int)(context.target.maxmp * " + p1 + " / 100.0f);");
                out.println("                        context.target.relive(hp, mp);");
                out.println("                    }");
                out.println("                }");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.COUNTER_ATTACK:
                // Effect_PercentAdd: 百分比float
                out.println("        if (!isActive && context.hited() && context.isDamage()) {");
                out.println("            if (CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000)) {");
                out.println("                context.passiveSkills.add(new AutoAttackSkill(1));");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.BOUNCE:
                // Effect_Bounce: 概率float，伤害类型int，固定值int，占伤害比例float
                out.println("        if (!isActive && context.hited() && context.isDamage()) {");
                out.println("            if (CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000)) {");
                out.println("                int dmg = " + p3 + ";");
                out.println("                dmg += context.damage * " + p4 + " / 100.0f;");
                out.println("                context.passiveSkills.add(new FixedDamageSkill(" + p2 + ", dmg));");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.VAMPIRE_ON_HIT:
                // Effect_Vampire: 转换比例float，有效范围int
                out.println("        if (!isActive && context.hited() && context.isDamage() && context.source.ref().equals(source)) {");
                out.println("            Player p = (Player)ObjectAccessor.getGameObject(source);");
                out.println("            if (p != null && p.isAlive()) {");
                out.println("                int value = (int)(context.damage * multiple * " + p1 + " / 100.0f);");
                out.println("                if (p.party == null) {");
                out.println("                    p.setHp(p.hp + value, true);");
                out.println("                } else {");
                out.println("                    List<Player> ps = p.party.getPlayerInRange(context.target.map.map, "
                        + p2 + " * 8, context.target.x, context.target.y);");
                out.println("                    for (Player pp : ps) {");
                out.println("                        if (pp.isAlive()) {");
                out.println("                         pp.setHp(pp.hp + value, true);");
                out.println("                        }");
                out.println("                    }");
                out.println("                }");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.ADD_DEBUFF_ON_HITED:
                // Effect_AddBuff: 概率float，概率补充变量String，BUFFID int，BUFF级别int
                out.println("        if (!isActive && context.hited() && context.isAttack()) {");
                out.println("            boolean hit = false;");
                out.println("            if (" + p2 + ".length() == 0) {");
                out.println("                hit = CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000);");
                out.println("            } else {");
                out.println("                Float vo = context.skillParams.get(" + p2 + ");");
                out.println("                if (vo != null) {");
                out.println("                    hit = CommonUtil.hit(RND, (int)(multiple * 100 * (vo.floatValue() + " + p1 + ")), 10000);");
                out.println("                }");
                out.println("            }");
                out.println("            if (hit) {");
                out.println("                ((CombatUnit)context.source).getBuffs().addBuff(BuffUtil.createBuff(" + p3 + ", "
                        + p4 + ", context.target, context.source, context.value));");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.ADD_BUFF_ON_HITED:
                // Effect_AddBuff: 概率float，概率补充变量String，BUFFID int，BUFF级别int
                out.println("        if (!isActive && context.hited() && context.isAttack()) {");
                out.println("            boolean hit = false;");
                out.println("            if (" + p2 + ".length() == 0) {");
                out.println("                hit = CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000);");
                out.println("            } else {");
                out.println("                Float vo = context.skillParams.get(" + p2 + ");");
                out.println("                if (vo != null) {");
                out.println("                    hit = CommonUtil.hit(RND, (int)(multiple * 100 * (vo.floatValue() + " + p1 + ")), 10000);");
                out.println("                }");
                out.println("            }");
                out.println("            if (hit) {");
                out.println("                context.target.getBuffs().addBuff(BuffUtil.createBuff(" + p3 + ", "
                        + p4 + ", context.target, context.target, context.value));");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.SLOW_ON_HITED:
                // Effect_SlowOnHited: 概率float，减速级别 int，减速时间int
                out.println("        if (!isActive && context.hited() && context.isDamage()) {");
                out.println("            if (CommonUtil.hit(RND, (int)(multiple * 100 * " + p1 + "), 10000)) {");
                out.println("                context.passiveSkills.add(new SlowSkill(" + p2 + ", " + p3 + "));");
                out.println("                effectTimes++;");
                out.println("            }");
                out.println("        }");
                break;
            
        }
    }

}
