package com.pip.game.editor.quest.expr;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.Expr0;
import com.pip.game.data.quest.pqe.Expression;
import com.pip.game.data.quest.pqe.FunctionCall;
import com.pip.game.data.quest.pqe.PQEUtils;

/**
 * ���ʽģ�壺������λ��
 * @author tzhang
 */
public class A_AddMirrorSet extends AbstractExpr {
    
    public int mapId;
	public int mask;
	
	/**
	 * ����ָ��ȫ�ֱ�����ģ�塣
	 * @param name ȫ�ֱ�������
	 */
	public A_AddMirrorSet() {
	    mapId = 0;
	    mask = 1;
	}
	
	/**
	 * �ж����ģ����һ����������һ��������
	 */
	public boolean isCondition() {
		return false;
	}

	/**
	 * ��ģ�崴���µı��ʽƬ�Ρ�
	 */
	public IExpr createNew(QuestInfo qinfo) {
		return new A_AddMirrorSet();
	}

	/**
	 * ȡ��ģ�����ơ�
	 */
	public String getName() {
		return "������λ";
	}

	/**
	 * ת��Ϊ��Ȼ���Ա�ʾ��
	 */
	public String toNatureString() {
		return "���ӵ�ͼ" + mapId + "��λ " +  mask;
	}
	
	/**
	 * ȡ�����ɵı��ʽ��
	 */
	public String getExpression() {
		return "AddMirrorSet(" + mapId + ","+ mask + ")";
	}

	/**
	 * ʶ��һ�����ʽ�Ƿ�ƥ�䱾ģ�塣���ƥ�䣬����һ���µı��ʽƬ�ζ��󣬷��򷵻�null��
	 */
	public IExpr recognize(QuestInfo qinfo, Expression expr) {
		if (expr.getLeftExpr().type == Expr0.TYPE_FUNC && expr.getLeftExpr().getFunctionCall().funcName.equals("AddMirrorSet") && expr.getRightExpr() == null) {
			FunctionCall fc = expr.getLeftExpr().getFunctionCall();
			//������ԭ�ȵĻ�����������һ����ͼID
			if(fc.getParamCount() == 1){
			    Expression param1 = fc.getParam(0);
			    if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER ){
    			    A_AddMirrorSet ret = (A_AddMirrorSet)createNew(qinfo);
    			    ret.mask = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
    			    return ret;
			    }
			}else{
			    if (fc.getParamCount() != 2) {
	                return null;
	            }
    			Expression param1 = fc.getParam(0);
    			Expression param2 = fc.getParam(1);
    			if (param1.getRightExpr() == null && param1.getLeftExpr().type == Expr0.TYPE_NUMBER 
    			        && param2.getRightExpr() == null && param2.getLeftExpr().type == Expr0.TYPE_NUMBER ) {
    				A_AddMirrorSet ret = (A_AddMirrorSet)createNew(qinfo);
    				ret.mapId = PQEUtils.translateNumberConstant(param1.getLeftExpr().value);
    				ret.mask = PQEUtils.translateNumberConstant(param2.getLeftExpr().value);
    				return ret;
    			}
			}
		}
		return null;
	}

	// ������IPropertySource�ӿڵ�ʵ��

	/**
	 * ȡ�����������������ģ����2���������ַ�������������������
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { 
		        new TextPropertyDescriptor("mapId", "��ͼID"),
				new TextPropertyDescriptor("mask", "��λ")
		};
	}

	/**
	 * ȡ�����Ե�ǰֵ��
	 */
	public Object getPropertyValue(Object id) {
		if ("mask".equals(id)) {
			return String.valueOf(mask);
		}else if ("mapId".equals(id)) {
            return String.valueOf(mapId);
        }
		return null;
	}

	/**
	 * �������Ե�ǰֵ��
	 */
	public void setPropertyValue(Object id, Object value) {
	    if ("mask".equals(id)) {
			int newValue = Integer.parseInt((String)value);
			if (newValue != mask) {
				mask = newValue;
				fireValueChanged();
			}
		} else if ("mapId".equals(id)) {
            int newValue = Integer.parseInt((String)value);
            if (newValue != mapId) {
                mapId = newValue;
                fireValueChanged();
            }
        }
	}

 
}
