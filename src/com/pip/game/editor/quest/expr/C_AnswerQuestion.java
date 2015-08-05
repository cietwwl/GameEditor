package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * 表达式模板：检测用户是否回答了某个提问的某个选项。
 * @author lighthu
 */
public class C_AnswerQuestion extends AbstractFunctionCheck4 {
	/**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public C_AnswerQuestion() {
		super("E_AnswerQuestion", 1, "通知ID", 0, "选项ID");
	}
	
	/**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_AnswerQuestion();
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "玩家回答提问...";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
		return "提问" + param1 + "：玩家选择 " + (param2 + 1);
	}
}
