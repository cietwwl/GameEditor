package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * ���ʽģ�壺�ж��Ƿ�ʹ��ĳ���ܡ�
 * @author lighthu
 */
public class C_UseSkill extends AbstractFunctionCheck2 {
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_UseSkill() {
		super("E_UseSkill", 1, "����ID");
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_UseSkill();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "���ʹ�ü���...";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "���ʹ�ü��� " + constant;
	}
}
