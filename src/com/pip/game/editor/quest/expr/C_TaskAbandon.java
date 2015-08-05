package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;

/**
 * ���ʽģ�壺�ж������Ǳ�������
 * @author Jeffrey
 *
 */
public class C_TaskAbandon extends AbstractFunctionCheck3 {
    /**
     * ����ָ��ȫ�ֱ�����ģ�塣
     * @param name ȫ�ֱ�������
     */
    public C_TaskAbandon() {
        super("E_TaskAbandon");
    }
    
    /**
     * ��ģ�崴���µı��ʽƬ�Ρ�
     */
    public IExpr createNew(QuestInfo qinfo) {
        return new C_TaskAbandon();
    }

    /**
     * ȡ��ģ�����ơ�
     */
    public String getName() {
        return "���񱻷���";
    }

    /**
     * ת��Ϊ��Ȼ���Ա�ʾ��
     */
    public String toNatureString() {
        return "���񱻷���";
    }

}
