package com.pip.game.editor.quest.expr;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.ParserConstants;

/**
 * ���ʽģ�壺�ж���������С�
 * @author lighthu
 */
public class C_HitRate extends AbstractFunctionCheck1 {
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_HitRate() {
		super("Random", ParserConstants.LT, 5000, "������");
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_HitRate();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "������";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "�������������֮ " + constant;
	}
}
