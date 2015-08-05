package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;

/**
 * ���ʽģ�壺�ж�����Ƿ�������
 */
public class C_ConsorterTeam extends AbstractFunctionCheck3 {
    /**
     * ����ָ��ȫ�ֱ�����ģ�塣
     * 
     * @param name
     *            ȫ�ֱ�������
     */
    public C_ConsorterTeam() {
        super("E_ConsorterTeam");
    }

    /**
     * ��ģ�崴���µı��ʽƬ�Ρ�
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new C_ConsorterTeam();
    }

    /**
     * ȡ��ģ�����ơ�
     */
    public String getName() {
        return "�������";
    }

    /**
     * ת��Ϊ��Ȼ���Ա�ʾ��
     */
    public String toNatureString() {
        return "�������";
    }
}

