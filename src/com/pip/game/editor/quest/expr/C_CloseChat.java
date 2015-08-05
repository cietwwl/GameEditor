package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * ���ʽģ�壺�����ҹر�ĳ���Ի���
 * @author lighthu
 */
public class C_CloseChat extends AbstractFunctionCheck2 {
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_CloseChat() {
		super("E_CloseChat", 1, "֪ͨID");
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_CloseChat();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "��ҹرնԻ�...";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "��ҹرնԻ� " + constant;
	}
}
