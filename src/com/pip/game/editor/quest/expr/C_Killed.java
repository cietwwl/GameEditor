package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * 表达式模板：判断玩家是否被怪物杀死。
 * @author lighthu
 */
public class C_Killed extends AbstractFunctionCheck3 {
	/**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public C_Killed() {
		super("E_Killed");
	}
	
	/**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_Killed();
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "玩家被怪物杀死";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
		return "玩家被怪物杀死";
	}
}
