package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.data.quest.pqe.ParserConstants;

/**
 * ���ʽģ�壺�Ա��жϡ�
 * @author lighthu
 */
public class C_Sex extends AbstractExpr {
	public int sex;
	
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public C_Sex() {
		sex = 0;
	}
	
	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new C_Sex();
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
		return "_SEX == " + sex;
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "�Ա�Ϊ...";
	}

	/**
	 * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getLeftExpr().type == Expr0.TYPE_IDENTIFIER && expr.getRightExpr() != null &&
				expr.getRightExpr().type == Expr0.TYPE_NUMBER) {
			if (!"_SEX".equals(expr.getLeftExpr().value) || expr.op != ParserConstants.EQ) {
				return null;
			}
			C_Sex ret = new C_Sex();
			ret.sex = PQEUtils.translateNumberConstant(expr.getRightExpr().value);
			return ret;
		}
		return null;
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
        switch (sex) {
        case 0:
            return "��ɫΪ����";
        case 1:
            return "��ɫΪŮ��";
        default:
            return "��ɫΪ������";
        }
    }

	// ������IPropertySource�ӿڵ�ʵ��

	/**
	 * ȡ�����������������ģ��ֻ��2���������Ƚ��Ա�
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { 
				new ComboBoxPropertyDescriptor("value", "�Ա�", new String[] { "����", "Ů��", "������" })
		};
	}

	/**
	 * ȡ�����Ե�ǰֵ��
	 */
	public Object getPropertyValue(Object id) {
		if ("value".equals(id)) {
			return sex;
		}
		return null;
	}

	/**
	 * �������Ե�ǰֵ��
	 */
	public void setPropertyValue(Object id, Object value) {
		if ("value".equals(id)) {
			int newSex = ((Integer)value).intValue();
			if (newSex != sex) {
				sex = newSex;
				fireValueChanged();
			}
		}
	}
}
