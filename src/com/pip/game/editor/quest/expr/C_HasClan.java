package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;

public class C_HasClan extends AbstractFunctionCheck3 {
    
    /**
     * ����ָ��ȫ�ֱ�����ģ�塣
     * @param name ȫ�ֱ�������
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
        return "�Ƿ���Ѫ��";
    }

    public String toNatureString() {
        // TODO Auto-generated method stub
        return "�Ƿ���Ѫ��";
    }

}
