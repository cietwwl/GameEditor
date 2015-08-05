package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * ���ʽģ�壺�ж��Ƿ�ʱ��ﵽ�����
 * @author lighthu
 */
public class C_BattleRound extends AbstractFunctionCheck2 {
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_BattleRound() {
		super("E_BattleRound", 1, "����");
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_BattleRound();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "ս���غ�������";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "ս���غ���Ϊ" + constant + "�ı���";
	}
}
