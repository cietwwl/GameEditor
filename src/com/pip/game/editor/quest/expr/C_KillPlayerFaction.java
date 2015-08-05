package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * ���ʽģ�壺�ж��Ƿ�ɱ��ĳ��ҡ�
 * @author lighthu
 */
public class C_KillPlayerFaction extends AbstractFunctionCheck2 {
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_KillPlayerFaction() {
		super("E_KillPlayerFaction", 1, "��ӪID");
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_KillPlayerFaction();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "ɱ��ĳ����Ӫ���...";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "��ɱ����Ӫ "+ constant+ " ��� " ;
	}
}
