package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * ���ʽģ�壺�ж�����Ƿ񱻹���ɱ����
 * @author lighthu
 */
public class C_Killed extends AbstractFunctionCheck3 {
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_Killed() {
		super("E_Killed");
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_Killed();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "��ұ�����ɱ��";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "��ұ�����ɱ��";
	}
}
