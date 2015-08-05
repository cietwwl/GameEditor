package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * 表达式模板：判断是否杀死某玩家。
 * @author lighthu
 */
public class C_KillPlayer extends AbstractFunctionCheck2 {
	/**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public C_KillPlayer() {
		super("E_KillPlayer", 1, "玩家ID");
	}
	
	/**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_KillPlayer();
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "杀死其他玩家...";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
		return "已杀死玩家 " + constant;
	}
}
