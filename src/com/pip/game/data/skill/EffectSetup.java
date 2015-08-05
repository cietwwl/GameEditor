package com.pip.game.data.skill;

/**
 * skill/effect.xml中effect的描述
 * @author ybai
 *
 */
public class EffectSetup {
    public int id;
    public int supportBuff;  //此效果支持的Buff
    public int supportedSkillType; //此效果支持的技能
    public String[] typeNames; //在编辑界面显示的名称;第0个是右侧列表上显示的;第1个是简称,在表格列名上做前缀
    public String[] typeParames; //取得参数关键字,在程序运行中使用
}
