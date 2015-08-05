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
public class SkillFinished {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
    
    public static void genSkillFinishedCode(PrintWriter out, EffectConfig eff, String p1, String p2, String p3, String p4, String p5, String p6){
        switch(eff.getType()){

            case EffectConfig.ADD_MP_ON_HIT:
                // Effect_CureOnHit: ����float���̶�ֵint��ռ���ޱ���float��ռ�˺�����float
                out.println("        if (context.hited() && context.isDamage()) {");
                out.println("            int rate = (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + "));");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                int addmp = context.getSkillParam(this, \"" + p2 + "\", " + p2 + ");");
                out.println("                addmp += context.source.maxmp * context.getSkillParam(this, \"" + p3 + "\", " + p3 + ") / 100.0f;");
                out.println("                addmp += context.damage * context.getSkillParam(this, \"" + p4 + "\", " + p4 + ") / 100.0f;");
                out.println("                context.activeSkills.add(new FixedAddMPSkill(addmp));");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.ADD_DEBUFF_ON_HIT:
                // Effect_AddBuff: ����float�����ʲ������String��BUFFID int��BUFF����int
                out.println("        if (context.hited()) {");
                out.println("            boolean hit = false;");
                out.println("            if (" + p2 + ".length() == 0) {");
                out.println("                hit = CommonUtil.hit(RND, (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ")), 10000);");
                out.println("            } else {");
                out.println("                Float vo = context.skillParams.get(" + p2 + ");");
                out.println("                if (vo != null) {");
                out.println("                    hit = CommonUtil.hit(RND, (int)(100 * (vo.floatValue() + context.getSkillParam(this, \"" + p1 + "\", " + p1 + "))), 10000);");
                out.println("                }");
                out.println("            }");
                out.println("            if (hit) {");
                out.println("                context.target.getBuffs().addBuff(BuffUtil.createBuff(" + p3 + ", "
                        + p4 + ", context.source, context.target, context.value));");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.ADD_BUFF_ON_HIT:
                // Effect_AddBuff: ����float�����ʲ������String��BUFFID int��BUFF����int
                out.println("        if (context.hited()) {");
                out.println("            boolean hit = false;");
                out.println("            if (" + p2 + ".length() == 0) {");
                out.println("                hit = CommonUtil.hit(RND, (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ")), 10000);");
                out.println("            } else {");
                out.println("                Float vo = context.skillParams.get(" + p2 + ");");
                out.println("                if (vo != null) {");
                out.println("                    hit = CommonUtil.hit(RND, (int)(100 * (vo.floatValue() + context.getSkillParam(this, \"" + p1 + "\", " + p1 + "))), 10000);");
                out.println("                }");
                out.println("            }");
                out.println("            if (hit) {");
                out.println("                ((CombatUnit)context.source).getBuffs().addBuff(BuffUtil.createBuff(" + p3 + ", "
                        + p4 + ", context.source, context.source, context.value));");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.FIRST_THREAT_ON_HIT:
                // Effect_FirstThreat: �޲���
                out.println("        if (context.hited()) {");
                out.println("            Attack.makeFirstThreat(context.source, context.target, -1);");
                out.println("        }");
                break;
            case EffectConfig.FEAR_ON_HIT:
                // Effect_FearOnHit: ����float�����ʲ������String������ʱ��int
                out.println("        if (context.hited()) {");
                out.println("            boolean hit = false;");
                out.println("            if (" + p2 + ".length() == 0) {");
                out.println("                hit = CommonUtil.hit(RND, (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ")), 10000);");
                out.println("            } else {");
                out.println("                Float vo = context.skillParams.get(" + p2 + ");");
                out.println("                if (vo != null) {");
                out.println("                    hit = CommonUtil.hit(RND, (int)(100 * (vo.floatValue() + context.getSkillParam(this, \"" + p1 + "\", " + p1 + "))), 10000);");
                out.println("                }");
                out.println("            }");
                out.println("            if (hit) {");
                out.println("                context.activeSkills.add(new FearSkill(context.getSkillParam(this, \"" + p3 + "\", " + p3 + ")));");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.SLOW_ON_HIT:
                // Effect_SlowOnHit:
                // ����float�����ʲ������String�����ټ���int�����ټ��𲹳����String������ʱ��int������ʱ�����float
                out.println("        if (context.hited()) {");
                out.println("            boolean hit = false;");
                out.println("            if (" + p2 + ".length() == 0) {");
                out.println("                hit = CommonUtil.hit(RND, (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ")), 10000);");
                out.println("            } else {");
                out.println("                Float vo = context.skillParams.get(" + p2 + ");");
                out.println("                if (vo != null) {");
                out.println("                    hit = CommonUtil.hit(RND, (int)(100 * (vo.floatValue() + context.getSkillParam(this, \"" + p1 + "\", " + p1 + "))), 10000);");
                out.println("                }");
                out.println("            }");
                out.println("            if (hit) {");
                out.println("                int sl = context.getSkillParam(this, \"" + p3 + "\", " + p3 + ");");
                out.println("                Float vo = context.skillParams.get(" + p4 + ");");
                out.println("                if (vo != null) {");
                out.println("                    sl += vo.intValue();");
                out.println("                }");
                out.println("                int tm = context.getSkillParam(this, \"" + p5 + "\", " + p5 + ");");
                out.println("                vo = context.skillParams.get(" + p6 + ");");
                out.println("                if (vo != null) {");
                out.println("                    tm *= 1.0f + vo.floatValue() / 100.0f;");
                out.println("                }");
                out.println("                context.activeSkills.add(new SlowSkill(sl, tm));");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.PARALYZE_ON_HIT:
                // Effect_FearOnHit: ����float�����ʲ������String������ʱ��int
                out.println("        if (context.hited()) {");
                out.println("            boolean hit = false;");
                out.println("            if (" + p2 + ".length() == 0) {");
                out.println("                hit = CommonUtil.hit(RND, (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ")), 10000);");
                out.println("            } else {");
                out.println("                Float vo = context.skillParams.get(" + p2 + ");");
                out.println("                if (vo != null) {");
                out.println("                    hit = CommonUtil.hit(RND, (int)(100 * (vo.floatValue() + context.getSkillParam(this, \"" + p1 + "\", " + p1 + "))), 10000);");
                out.println("                }");
                out.println("            }");
                out.println("            if (hit) {");
                out.println("                context.activeSkills.add(new ParalyzeSkill(context.getSkillParam(this, \"" + p3 + "\", " + p3 + ")));");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.STAY_ON_HIT:
                // Effect_FearOnHit: ����float�����ʲ������String������ʱ��int
                out.println("        if (context.hited()) {");
                out.println("            boolean hit = false;");
                out.println("            if (" + p2 + ".length() == 0) {");
                out.println("                hit = CommonUtil.hit(RND, (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ")), 10000);");
                out.println("            } else {");
                out.println("                Float vo = context.skillParams.get(" + p2 + ");");
                out.println("                if (vo != null) {");
                out.println("                    hit = CommonUtil.hit(RND, (int)(100 * (vo.floatValue() + context.getSkillParam(this, \"" + p1 + "\", " + p1 + "))), 10000);");
                out.println("                }");
                out.println("            }");
                out.println("            if (hit) {");
                out.println("                context.activeSkills.add(new StaySkill(context.getSkillParam(this, \"" + p3 + "\", " + p3 + ")));");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.DUMB_ON_HIT:
                // Effect_FearOnHit: ����float�����ʲ������String������ʱ��int
                out.println("        if (context.hited()) {");
                out.println("            boolean hit = false;");
                out.println("            if (" + p2 + ".length() == 0) {");
                out.println("                hit = CommonUtil.hit(RND, (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ")), 10000);");
                out.println("            } else {");
                out.println("                Float vo = context.skillParams.get(" + p2 + ");");
                out.println("                if (vo != null) {");
                out.println("                    hit = CommonUtil.hit(RND, (int)(100 * (vo.floatValue() + context.getSkillParam(this, \"" + p1 + "\", " + p1 + "))), 10000);");
                out.println("                }");
                out.println("            }");
                out.println("            if (hit) {");
                out.println("                context.activeSkills.add(new DumbSkill(context.getSkillParam(this, \"" + p3 + "\", " + p3 + ")));");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.REPEAT_ON_HIT:
                // Effect_PercentAdd: �ٷֱ�float
                out.println("        if (context.hited()) {");
                out.println("            if (CommonUtil.hit(RND, (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ")), 10000)) {");
                out.println("                context.activeSkills.add(context.skill);");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.DEC_MP_ON_HIT:
                // Effect_CureOnHit: ����float���̶�ֵint��ռ���ޱ���float��ռ�˺�����float
                out.println("        if (context.hited() && context.isDamage()) {");
                out.println("            int rate = (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + "));");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                int decmp = context.getSkillParam(this, \"" + p2 + "\", " + p2 + ");");
                out.println("                decmp += context.target.maxmp * context.getSkillParam(this, \"" + p3 + "\", " + p3 + ") / 100.0f;");
                out.println("                decmp += context.damage * context.getSkillParam(this, \"" + p4 + "\", " + p4 + ") / 100.0f;");
                out.println("                context.activeSkills.add(new FixedDecMPSkill(decmp));");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.ADD_HP_ON_HIT:
                // Effect_CureOnHit: ����float���̶�ֵint��ռ���ޱ���float��ռ�˺�����float
                out.println("        if (context.hited() && context.isDamage()) {");
                out.println("            int rate = (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + "));");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                int cure = context.getSkillParam(this, \"" + p2 + "\", " + p2 + ");");
                out.println("                cure += context.source.maxhp * context.getSkillParam(this, \"" + p3 + "\", " + p3 + ") / 100.0f;");
                out.println("                cure += context.damage * context.getSkillParam(this, \"" + p4 + "\", " + p4 + ") / 100.0f;");
                out.println("                context.activeSkills.add(new FixedHealSkill(cure));");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.RELIVE_TARGET:
                // Effect_PercentAdd: �ٷֱ�float
                out.println("        if (context.source == context.target) {");
                out.println("            if (isActive && !context.source.isAlive()) {");
                out.println("                int hp = (int)(context.source.maxhp * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f);");
                out.println("                int mp = (int)(context.source.maxmp * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f);");
                out.println("                context.source.relive(hp, mp);");
                out.println("            }");
                out.println("        } else {");
                out.println("            if (isActive) {");
                out.println("                if (!context.target.isAlive()) {");
                out.println("                    // TODO: ���������˷���һ����������");
                out.println("                    if (context.target.type == GameObject.TYPE_PLAYER&&context.target.faction==context.source.faction) {");
                out.println("                        ReliveOption option = new ReliveOption(");
                out.println("                                ReliveOption.SKILL_ACTIVE, getName(), 14,");
                out.println("                               context.target.map.id, context.source.x,");
                out.println("                               context.source.y);");
                out.println("                       option.context = context;");
                out.println("                      ((Player)context.target).addReliveOption(option);");
                out.println("                   }");
                out.println("                 }");
                out.println("                } else {");
                out.println("                    if (!context.target.isAlive()) {");
                out.println("                        int hp = (int)(context.target.maxhp * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f);");
                out.println("                        int mp = (int)(context.target.maxmp * context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f);");
                out.println("                        context.target.relive(hp, mp);");
                out.println("                    }");
                out.println("                }");
                out.println("        }");
                break;
            case EffectConfig.CURE_TARGET_IGNORE_MAX:
                // Effect_CureOnHit: ����float���̶�ֵint��ռ���ޱ���float��ռ�˺�����float
                out.println("        if (context.hited()) {");
                out.println("            int rate = (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + "));");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                int addhp = context.getSkillParam(this, \"" + p2 + "\", " + p2 + ");");
                out.println("                addhp += context.source.maxhp * context.getSkillParam(this, \"" + p3 + "\", " + p3 + ") / 100.0f;");
                out.println("                addhp += context.damage * context.getSkillParam(this, \"" + p4 + "\", " + p4 + ") / 100.0f;");
                out.println("                context.target.setHp(context.target.hp + addhp, true);");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.DISPEL_BUFF:
                // Effect_PercentAdd: �ٷֱ�
                out.println("        if (context.hited()) {");
                out.println("            int rate = (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + "));");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                context.target.getBuffs().dispelGoodBuff(false);");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.DISPEL_ALL_BUFF:
                // Effect_PercentAdd: �ٷֱ�
                out.println("        if (context.hited()) {");
                out.println("            int rate = (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + "));");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                context.target.getBuffs().dispelGoodBuff(true);");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.DISPEL_DEBUFF:
                // Effect_PercentAdd: �ٷֱ�
                out.println("        if (context.hited()) {");
                out.println("            int rate = (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + "));");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                context.target.getBuffs().dispelBadBuff(false);");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.DISPEL_ALL_DEBUFF:
                // Effect_PercentAdd: �ٷֱ�
                out.println("        if (context.hited()) {");
                out.println("            int rate = (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + "));");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                context.target.getBuffs().dispelBadBuff(true);");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.INTERRUPT:
                // Effect_PercentAdd: �ٷֱ�
                out.println("        if (context.hited()) {");
                out.println("            int rate = (int)(100 * context.getSkillParam(this, \"" + p1 + "\", " + p1 + "));");
                out.println("            if (CommonUtil.hit(RND, rate, 10000)) {");
                out.println("                context.activeSkills.add(new BreakAttackSkill());");
                out.println("            }");
                out.println("        }");
                break;
            case EffectConfig.CHANGE_THREAT_TOTAL:
                // Effect_PercentAdd: �ٷֱ�
                out.println("        if (context.hited()) {");
                out.println("            float rate = 1.0f + context.getSkillParam(this, \"" + p1 + "\", " + p1 + ") / 100.0f;");
                out.println("            context.source.changeThreat(target, rate);");
                out.println("        }");
                break;
            case EffectConfig.FIRST_THREAT_TEMP:
                // Effect_FirstThreatTemp: ����ʱ��(����)
                out.println("        if (context.hited()) {");
                out.println("            int keepTime = context.getSkillParam(this, \"" + p1 + "\", " + p1 + ");");
                out.println("            Attack.makeFirstThreat(context.target, context.source, keepTime);");
                out.println("        }");
                break;
            case EffectConfig.TRANSPORT_TO_ME:
                // Effect_CannotMove���޲���
                out.println("        if (context.hited()) {");
                out.println("            try{");
                out.println("            context.target.goMap(context.source.map.id, context.source.x, context.source.y);");
                out.println("            }catch(VMapException e){");
                out.println("            }");
                out.println("        }");
//                out.println("        if (context.hited()) {");
//                out.println("            context.target.goMap(context.source.map.id, context.source.x, context.source.y);");
//                out.println("        }");
                break;
            case EffectConfig.TRANSPORT_TO_POS:
                // Effect_Transport��Ŀ��λ��
                out.println("        if (context.hited()) {");
                out.println("            context.target.goMap(" + p1 + "[0], " + p1 + "[1], " + p2 + "[2]);");
                out.println("        }");
                break;
            
        }
    }
}
