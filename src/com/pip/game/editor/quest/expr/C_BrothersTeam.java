package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * 表达式模板：判断玩家是否兄弟组队
 * 
 * @author wpjiang
 */
public class C_BrothersTeam extends AbstractFunctionCheck3 {
    /**
     * 构造指定全局变量的模板。
     * 
     * @param name
     *            全局变量名称
     */
    public C_BrothersTeam() {
        super("E_BrothersTeam");
    }

    /**
     * 用模板创建新的表达式片段。
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new C_BrothersTeam();
    }

    /**
     * 取得模板名称。
     */
    public String getName() {
        return "兄弟组队";
    }

    /**
     * 转换为自然语言表示。
     */
    public String toNatureString() {
        return "兄弟组队";
    }
}

