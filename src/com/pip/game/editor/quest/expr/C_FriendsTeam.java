package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;

public class C_FriendsTeam extends AbstractFunctionCheck3 {

    public C_FriendsTeam() {
        super("E_FriendsTeam");
    }

    public IExpr createNew(QuestInfo qinfo) {
        return new C_FriendsTeam();
    }

    public String getName() {
        return "�������";
    }

    public String toNatureString() {
        return "�������";
    }

}
