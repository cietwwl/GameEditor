package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;

/**
 * 表达式模板：判断任务是被放弃。
 * @author Jeffrey
 *
 */
public class C_TaskAbandon extends AbstractFunctionCheck3 {
    /**
     * 构造指定全局变量的模板。
     * @param name 全局变量名称
     */
    public C_TaskAbandon() {
        super("E_TaskAbandon");
    }
    
    /**
     * 用模板创建新的表达式片段。
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new C_TaskAbandon();
    }

    /**
     * 取得模板名称。
     */
    public String getName() {
        return "任务被放弃";
    }

    /**
     * 转换为自然语言表示。
     */
    public String toNatureString() {
        return "任务被放弃";
    }

}
