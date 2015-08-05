package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * ���ʽģ�壺�ж������Ƿ��ѽ���
 * @author lighthu
 */
public class C_TaskFinish extends AbstractFunctionCheck3 {
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_TaskFinish() {
		super("E_TaskFinish");
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_TaskFinish();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "�����ѽ�";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "�����ѽ�";
	}
}
