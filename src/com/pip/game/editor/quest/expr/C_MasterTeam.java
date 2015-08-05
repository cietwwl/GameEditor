package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;

/**
 * ���ʽģ�壺�ж�����Ƿ�ʦͽ���
 * 
 */
public class C_MasterTeam extends AbstractFunctionCheck3 {
    /**
     * ����ָ��ȫ�ֱ�����ģ�塣
     * 
     * @param name
     *            ȫ�ֱ�������
     */
    public C_MasterTeam() {
        super("E_MasterTeam");
    }

    /**
     * ��ģ�崴���µı��ʽƬ�Ρ�
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new C_MasterTeam();
    }

    /**
     * ȡ��ģ�����ơ�
     */
    public String getName() {
        return "ʦͽ���";
    }

    /**
     * ת��Ϊ��Ȼ���Ա�ʾ��
     */
    public String toNatureString() {
        return "ʦͽ���";
    }
}

