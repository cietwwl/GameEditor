package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * ���ʽģ�壺�ж�����Ƿ��ֵ����
 * 
 * @author wpjiang
 */
public class C_BrothersTeam extends AbstractFunctionCheck3 {
    /**
     * ����ָ��ȫ�ֱ�����ģ�塣
     * 
     * @param name
     *            ȫ�ֱ�������
     */
    public C_BrothersTeam() {
        super("E_BrothersTeam");
    }

    /**
     * ��ģ�崴���µı��ʽƬ�Ρ�
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new C_BrothersTeam();
    }

    /**
     * ȡ��ģ�����ơ�
     */
    public String getName() {
        return "�ֵ����";
    }

    /**
     * ת��Ϊ��Ȼ���Ա�ʾ��
     */
    public String toNatureString() {
        return "�ֵ����";
    }
}

