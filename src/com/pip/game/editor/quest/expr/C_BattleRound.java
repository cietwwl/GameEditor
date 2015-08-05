package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * 表达式模板：判断是否时间达到间隔。
 * @author lighthu
 */
public class C_BattleRound extends AbstractFunctionCheck2 {
	/**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public C_BattleRound() {
		super("E_BattleRound", 1, "倍数");
	}
	
	/**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_BattleRound();
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "战斗回合数倍数";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
		return "战斗回合数为" + constant + "的倍数";
	}
}
