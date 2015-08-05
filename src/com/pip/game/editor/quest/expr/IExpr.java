package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertySource;

import com.pip.game.data.DataObject;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;

/**
 * ������������õ��ı��ʽƬ��/ģ��ӿڡ�һ�����ʽƬ�ο���ͨ�����ʽģ�崴������������
 * �����Լ������Լ��Ϲ�������޸ġ�
 * @author lighthu
 */
public interface IExpr extends IPropertySource {
	/**
	 * ʶ��һ�����ʽ�����Ƿ��ܹ������ģ��ʶ��
	 * @param expr ���ʽ����
	 * @return ����ܹ�ʶ�𣬷���ʶ������ı��ʽƬ�ζ��󣬷��򷵻�null��
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr);
	
	/**
	 * ͨ��ģ�崴��һ���µĿհױ��ʽƬ�Ρ�
	 * @return �µı��ʽƬ�ζ���
	 */
	public IExpr createNew(QuestInfo qinfo);
	
	/**
	 * �ж����ģ����һ����������һ��������
	 */
	public boolean isCondition();
	
	/**
	 * ȡ��ģ�����ʾ���ơ�
	 */
	public String getName();
	
	/**
	 * ȡ�ñ��ʽƬ�ζ�Ӧ�ı��ʽ�ַ�����
	 */
	public String getExpression();
	
	/**
	 * �õ����ʽƬ�ε���Ȼ����ַ�����
	 */
	public String toNatureString();
	
	/**
	 * ��Ӽ����ߡ�
	 */
	public void addListener(IExprListener l);
	
	/**
	 * ȡ�ñ��ʽ�������������ڻ�ȡ��������б���
	 */
	public DataObject getQuestInfo();
}
