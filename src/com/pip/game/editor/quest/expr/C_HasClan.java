package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;

public class C_HasClan extends AbstractFunctionCheck3 {
    
    /**
     * 构造指定全局变量的模板。
     * @param name 全局变量名称
     */
    public C_HasClan() {
        super("E_HasClan");
    }
    
    public IExpr createNew(QuestInfo qinfo) {
        // TODO Auto-generated method stub
        return new C_HasClan();
    }

    public String getName() {
        // TODO Auto-generated method stub
        return "是否有血盟";
    }

    public String toNatureString() {
        // TODO Auto-generated method stub
        return "是否有血盟";
    }

}
