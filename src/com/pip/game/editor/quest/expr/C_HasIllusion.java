package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;

/**
 * @author wpjiang
 *  �Ƿ�û�
 */
public class C_HasIllusion extends AbstractFunctionCheck3{

    /**
     * ����ָ��ȫ�ֱ�����ģ�塣
     * @param name ȫ�ֱ�������
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
        return "�Ƿ�û�";
    }

    public String toNatureString() {
        // TODO Auto-generated method stub
        return "�Ƿ�û�";
    }

}
