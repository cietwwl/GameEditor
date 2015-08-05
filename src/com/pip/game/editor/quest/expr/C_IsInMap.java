package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * ���ʽģ�壺�ж�����Ƿ��ڳ����С�
 * @author tzhang
 */
public class C_IsInMap extends AbstractFunctionCheck2 {
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_IsInMap() {
		super("IsInMap", 1, "����ID");
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_IsInMap();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "����Ƿ��ڳ�����";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "����Ƿ��ڳ���" + constant + "��";
	}
}
