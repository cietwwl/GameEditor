package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * ���ʽģ�壺���ñ�����ֵ��
 * @author lighthu
 */
public class A_OpenUI extends AbstractFunctionAction3 {
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public A_OpenUI() {
		super("OpenUI", "ui_", "��������");
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new A_OpenUI();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "�򿪽���";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "�򿪽��� " + param1;
	}
}
