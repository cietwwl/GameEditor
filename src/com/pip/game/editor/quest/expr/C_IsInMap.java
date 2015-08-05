package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * 表达式模板：判断玩家是否在场景中。
 * @author tzhang
 */
public class C_IsInMap extends AbstractFunctionCheck2 {
	/**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public C_IsInMap() {
		super("IsInMap", 1, "场景ID");
	}
	
	/**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_IsInMap();
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "玩家是否在场景中";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
		return "玩家是否在场景" + constant + "中";
	}
}
