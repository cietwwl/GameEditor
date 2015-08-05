package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.ParserConstants;

/**
 * ����Ϊ����жϡ�
 * @author lighthu
 */
public class C_True extends AbstractExpr {
	/**
	 * ���췽����
	 */
	public C_True() {
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_True();
	}
	
	/**
	 * �ж����ģ����һ����������һ��������
	 */
	public boolean isCondition() {
		return true;
	}

	/**
	 * ȡ�����ɵı��ʽ��
	 */
	public String getExpression() {
        return "1 == 1";
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "��";
	}

	/**
	 * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
	    if (expr.getLeftExpr().type == Expr0.TYPE_NUMBER && expr.getOp() == ParserConstants.EQ && 
	            expr.getRightExpr().type == Expr0.TYPE_NUMBER && "1".equals(expr.getLeftExpr().value) &&
	            "1".equals(expr.getRightExpr().value)) {
	        return createNew(qinfo);
	    } else {
	        return null;
	    }
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "��";
	}

	// ������IPropertySource�ӿڵ�ʵ��

	/**
	 * ȡ�����������������ģ����1�����������ʽ��
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[0];
	}

	/**
	 * ȡ�����Ե�ǰֵ��
	 */
	public Object getPropertyValue(Object id) {
		return null;
	}

	/**
	 * �������Ե�ǰֵ��
	 */
	public void setPropertyValue(Object id, Object value) {
	}
}
