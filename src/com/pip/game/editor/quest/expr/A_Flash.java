package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * 表达式模板：设置变量的值。
 * @author lighthu
 */
public class A_Flash extends AbstractFunctionAction2 {
	/**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public A_Flash() {
		super("Flash", 5, "帧数");
	}
	
	/**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new A_Flash();
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "屏幕闪烁";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
		return "屏幕闪烁 " + param + " 帧";
	}
}
