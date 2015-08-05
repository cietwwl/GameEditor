package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * ���ʽģ�壺�ж�����Ƿ��������ɱ����
 * @author lighthu
 */
public class C_KilledByPlayer extends AbstractFunctionCheck3 {
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_KilledByPlayer() {
		super("E_KilledByPlayer");
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_KilledByPlayer();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "���������ɱ��";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "���������ɱ��";
	}
}
