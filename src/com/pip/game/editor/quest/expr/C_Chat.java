package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * ���ʽģ�壺�ж�����Ƿ���������Ϣ��
 * @author lighthu
 */
public class C_Chat extends AbstractFunctionCheck3 {
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_Chat() {
		super("E_Chat");
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_Chat();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "��ҷ���������Ϣ";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "��ҷ���������Ϣ";
	}
}
