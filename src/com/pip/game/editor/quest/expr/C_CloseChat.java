package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * 表达式模板：检测玩家关闭某个对话。
 * @author lighthu
 */
public class C_CloseChat extends AbstractFunctionCheck2 {
	/**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public C_CloseChat() {
		super("E_CloseChat", 1, "通知ID");
	}
	
	/**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_CloseChat();
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "玩家关闭对话...";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
		return "玩家关闭对话 " + constant;
	}
}
