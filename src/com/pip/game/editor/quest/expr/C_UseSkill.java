package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * 表达式模板：判断是否使用某技能。
 * @author lighthu
 */
public class C_UseSkill extends AbstractFunctionCheck2 {
	/**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public C_UseSkill() {
		super("E_UseSkill", 1, "技能ID");
	}
	
	/**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_UseSkill();
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "玩家使用技能...";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
		return "玩家使用技能 " + constant;
	}
}
