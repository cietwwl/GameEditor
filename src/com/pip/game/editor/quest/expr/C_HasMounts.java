package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;

/**
 * @author wpjiang
 *  �Ƿ�������
 */
public class C_HasMounts extends AbstractFunctionCheck3 {
    

    /**
     * ����ָ��ȫ�ֱ�����ģ�塣
     * @param name ȫ�ֱ�������
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
        return "�Ƿ�������";
    }

    public String toNatureString() {
        // TODO Auto-generated method stub
        return "�Ƿ�������";
    }

}
