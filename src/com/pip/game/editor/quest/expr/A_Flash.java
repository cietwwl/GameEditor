package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * ���ʽģ�壺���ñ�����ֵ��
 * @author lighthu
 */
public class A_Flash extends AbstractFunctionAction2 {
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public A_Flash() {
		super("Flash", 5, "֡��");
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new A_Flash();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "��Ļ��˸";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "��Ļ��˸ " + param + " ֡";
	}
}
