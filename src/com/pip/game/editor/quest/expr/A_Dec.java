package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * ���ʽģ�壺���ñ�����ֵ��
 * @author lighthu
 */
public class A_Dec extends AbstractFunctionAction1 {
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public A_Dec(QuestInfo qinfo) {
		super("Dec", "var", "����", 1, "����ֵ");
		questInfo = qinfo;
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new A_Dec(qinfo);
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "���ٱ�����ֵ";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "���� " + param1 + " ��ֵ���� " + param2;
	}
}
