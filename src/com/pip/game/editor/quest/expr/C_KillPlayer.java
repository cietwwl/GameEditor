package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * ���ʽģ�壺�ж��Ƿ�ɱ��ĳ��ҡ�
 * @author lighthu
 */
public class C_KillPlayer extends AbstractFunctionCheck2 {
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_KillPlayer() {
		super("E_KillPlayer", 1, "���ID");
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_KillPlayer();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "ɱ���������...";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "��ɱ����� " + constant;
	}
}
