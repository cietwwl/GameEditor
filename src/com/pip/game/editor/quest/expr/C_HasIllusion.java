package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;

/**
 * @author wpjiang
 *  是否幻化
 */
public class C_HasIllusion extends AbstractFunctionCheck3{

    /**
     * 构造指定全局变量的模板。
     * @param name 全局变量名称
     */
    public C_HasIllusion() {
        super("E_HasIllusion");
    }
    
    public IExpr createNew(QuestInfo qinfo) {
        // TODO Auto-generated method stub
        return new C_HasIllusion();
    }

    public String getName() {
        // TODO Auto-generated method stub
        return "是否幻化";
    }

    public String toNatureString() {
        // TODO Auto-generated method stub
        return "是否幻化";
    }

}
