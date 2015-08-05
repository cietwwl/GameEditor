package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * @author wpjiang
 * 计时器
 */
public class C_TimeEnd extends AbstractFunctionCheck2 {

    public C_TimeEnd() {
        super("E_TimeEnd", 1, "计时时间");
    }

    public IExpr createNew(QuestInfo qinfo) {
        return new C_TimeEnd();
    }

    public String getName() {
        // TODO Auto-generated method stub
        return "计时器计时...";
    }

    public String toNatureString() {
        // TODO Auto-generated method stub
        return "计时器时间" + constant;
    }
    

}
