package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * 表达式模板：判断是否时间达到间隔。
 * @author lighthu
 */
public class C_TimeInterval extends AbstractFunctionCheck2 {
	/**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public C_TimeInterval() {
		super("E_TimeInterval", 1, "间隔(毫秒)");
	}
	
	/**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_TimeInterval();
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "系统时间达到间隔";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
		return "系统时间达到间隔 " + constant;
	}
}
