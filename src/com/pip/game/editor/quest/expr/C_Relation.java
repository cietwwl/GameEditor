package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;

/**
 * ���ʽģ�壺���������
 * 
 * ĿǰӦ��ֻ֧�ֻ���ΪĬ�ϵ��������
 * 
 * @author ybai
 */
public class C_Relation extends AbstractExpr {
    public final static int C_AND = 0;
    public final static int C_OR = 1;
    
    public final static String CR_AND = "AND";
    public final static String CR_OR = "OR";
    
    private final static String[] r_expression = {CR_AND, CR_OR};
	public int relation;
	
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_Relation() {
	    relation = C_OR;
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_Relation();
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
		return r_expression[relation];
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "������ϵ";
	}

	/**
	 * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {    
	    if(expr.getLeftExpr().type == Expr0.TYPE_IDENTIFIER && expr.getRightExpr() == null) {
	        C_Relation ret = new C_Relation();
	        if(CR_AND.equals(expr.getLeftExpr().value)) {
	            ret.relation = 0;
	        } else if ("OR".equals(expr.getLeftExpr().value)) {
	            ret.relation = 1;
	        }
	        return ret;
	    }
		return null;
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		switch (relation) {
		case 0:
			return "��";
		case 1:
			return "��";
		default:
			return "����δ֪";
		}
	}
	
	public static boolean checkRelationExpr(String str) {
	    for(int i=0; i<r_expression.length; i++) {
	        if(str.equals(r_expression[i])) {
	            return true;
	        }
	    }
	    return false;
	}

	// ������IPropertySource�ӿڵ�ʵ��

	/**
	 * ȡ�����������������ģ��ֻ��2���������Ƚ��Ա�
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
//		return new IPropertyDescriptor[] { 
//				new ComboBoxPropertyDescriptor("value", "������ϵ", new String[] { "��", "��"})
//		};
	    return new IPropertyDescriptor[0];
	}

	/**
	 * ȡ�����Ե�ǰֵ��
	 */
	public Object getPropertyValue(Object id) {
		if ("value".equals(id)) {
			return relation;
		}
		return null;
	}

	/**
	 * �������Ե�ǰֵ��
	 */
	public void setPropertyValue(Object id, Object value) {
		if ("value".equals(id)) {
			int newRelation = ((Integer)value).intValue();
			if (newRelation != relation) {
			    relation = newRelation;
				fireValueChanged();
			}
		}
	}
}
