package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * ���ʽģ�壺����û��Ƿ�ش���ĳ�����ʵ�ĳ��ѡ�
 * @author lighthu
 */
public class C_AnswerQuestion extends AbstractFunctionCheck4 {
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_AnswerQuestion() {
		super("E_AnswerQuestion", 1, "֪ͨID", 0, "ѡ��ID");
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_AnswerQuestion();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "��һش�����...";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "����" + param1 + "�����ѡ�� " + (param2 + 1);
	}
}
