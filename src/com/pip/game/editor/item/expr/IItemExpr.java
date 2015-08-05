package com.pip.game.editor.item.expr;

import org.eclipse.ui.views.properties.IPropertySource;

import com.pip.game.data.item.Item;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.editor.quest.expr.IExprListener;

/**
 * ������������õ��ı��ʽƬ��/ģ��ӿڡ�һ�����ʽƬ�ο���ͨ�����ʽģ�崴������������
 * �����Լ������Լ��Ϲ�������޸ġ�
 * @author lighthu
 */
public interface IItemExpr extends IPropertySource {
    /**
     * ʶ��һ�����ʽ�����Ƿ��ܹ������ģ��ʶ��
     * @param expr ���ʽ����
     * @return ����ܹ�ʶ�𣬷���ʶ������ı��ʽƬ�ζ��󣬷��򷵻�null��
     */
    public IItemExpr recognize(Item qinfo, Expression expr);
    
    /**
     * ͨ��ģ�崴��һ���µĿհױ��ʽƬ�Ρ�
     * @return �µı��ʽƬ�ζ���
     */
    public IItemExpr createNew(Item qinfo);
    
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
    public QuestInfo getQuestInfo();
}

