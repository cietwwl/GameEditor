package com.pip.game.editor.quest.expr;

/**
 * ���Դ���֪ͨ�ı��ʽģ�塣
 * @author lighthu
 */
public abstract class AbstractNotifyAction extends AbstractExpr {
	public int notifyID = -1;
	
	/**
	 * �ж����ģ����һ����������һ��������
	 */
	public boolean isCondition() {
		return false;
	}
}
