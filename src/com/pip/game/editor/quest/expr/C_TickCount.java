package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;

/**
 * @author wpjiang
 *  计数器
 */
public class C_TickCount  extends AbstractFunctionCheck2{

    public C_TickCount() {
        super("E_TickCount", 1, "计数数量");
        // TODO Auto-generated constructor stub
    }

    public IExpr createNew(QuestInfo qinfo) {
        // TODO Auto-generated method stub
        return new C_TickCount();
    }

    public String getName() {
        // TODO Auto-generated method stub
        return "计数数量";
    }

    public String toNatureString() {
        // TODO Auto-generated method stub
        return "计数数量" + constant;
    }

}
