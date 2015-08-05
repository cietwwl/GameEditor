package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;

/**
 * @author wpjiang
 *  是否有坐骑
 */
public class C_HasMounts extends AbstractFunctionCheck3 {
    

    /**
     * 构造指定全局变量的模板。
     * @param name 全局变量名称
     */
    public C_HasMounts() {
        super("E_HasMounts");
    }
    
    public IExpr createNew(QuestInfo qinfo) {
        // TODO Auto-generated method stub
        return new C_HasMounts();
    }

    public String getName() {
        // TODO Auto-generated method stub
        return "是否有坐骑";
    }

    public String toNatureString() {
        // TODO Auto-generated method stub
        return "是否有坐骑";
    }

}
