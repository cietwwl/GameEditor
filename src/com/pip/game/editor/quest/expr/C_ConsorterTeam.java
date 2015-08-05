package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;

/**
 * 表达式模板：判断玩家是否夫妻组队
 */
public class C_ConsorterTeam extends AbstractFunctionCheck3 {
    /**
     * 构造指定全局变量的模板。
     * 
     * @param name
     *            全局变量名称
     */
    public C_ConsorterTeam() {
        super("E_ConsorterTeam");
    }

    /**
     * 用模板创建新的表达式片段。
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new C_ConsorterTeam();
    }

    /**
     * 取得模板名称。
     */
    public String getName() {
        return "夫妻组队";
    }

    /**
     * 转换为自然语言表示。
     */
    public String toNatureString() {
        return "夫妻组队";
    }
}

