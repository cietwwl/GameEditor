package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * ���ʽģ�壺�ж��Ƿ�ʱ��ﵽ�����
 * @author lighthu
 */
public class C_TimeInterval extends AbstractFunctionCheck2 {
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_TimeInterval() {
		super("E_TimeInterval", 1, "���(����)");
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_TimeInterval();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "ϵͳʱ��ﵽ���";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "ϵͳʱ��ﵽ��� " + constant;
	}
}
