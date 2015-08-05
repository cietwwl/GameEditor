package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * ���ʽģ�壺�����ҹر�ĳ����Ϣ��
 * @author lighthu
 */
public class C_CloseMessage extends AbstractFunctionCheck2 {
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_CloseMessage() {
		super("E_CloseMessage", 1, "֪ͨID");
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_CloseMessage();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "��ҹر���Ϣ...";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "��ҹر���Ϣ " + constant;
	}
}
