package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.ParserConstants;

/**
 * 表达式模板：判断随机数命中。
 * @author lighthu
 */
public class C_HitRate extends AbstractFunctionCheck1 {
	/**
	 * 构造指定全局变量的模板。
	 * @param name 全局变量名称
	 */
	public C_HitRate() {
		super("Random", ParserConstants.LT, 5000, "命中率");
	}
	
	/**
	 * 用模板创建新的表达式片段。
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_HitRate();
	}

	/**
	 * 取得模板名称。
	 */
	public String getName() {
		return "掷骰子";
	}

	/**
	 * 转换为自然语言表示。
	 */
	public String toNatureString() {
		return "掷骰子命中万分之 " + constant;
	}
}
